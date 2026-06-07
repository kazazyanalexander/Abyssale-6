import json
import uvicorn
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from langchain_openai import ChatOpenAI
from langgraph.graph import StateGraph, MessagesState, START, END
from langgraph.checkpoint.memory import MemorySaver
from langchain_core.messages import HumanMessage, SystemMessage, AIMessage
from typing import Dict, Any, Optional
import os
from datetime import datetime

LLM_BASE_URL = os.getenv("LLM_BASE_URL", "http://localhost:1234/v1")

# ----------------------------
# 1. Charger les configurations des PNJ depuis un fichier JSON externe
# ----------------------------
with open("npc_config.json", "r", encoding="utf-8") as f:
    config_data = json.load(f)
    
# Extraire le contexte du monde et les configurations des PNJ
WORLD_CONTEXT = config_data.get("world", {})
NPC_CONFIGS = config_data.get("npcs", [])

# Language-specific system prompts (you can customize these)
LANGUAGE_SYSTEM_PROMPTS = {
    "en": "You must respond ONLY in English language.",
    "fr": "You must respond ONLY in French language. Use 'tu' for informal address.",
    "de": "You must respond ONLY in German language. Use 'du' for informal address.",
    "zh": "You must respond ONLY in Chinese language (Simplified Chinese)."
}

# ----------------------------
# 2. Construire le prompt de contexte du monde
# ----------------------------
def build_world_context_prompt(language: str = "en") -> str:
    """
    Construit le prompt de contexte du monde dans la langue spécifiée.
    """
    world_name = WORLD_CONTEXT.get("name", "Abyssal-6")
    world_goal = WORLD_CONTEXT.get("goal", "Repair the reactor")
    world_description = WORLD_CONTEXT.get("description", "")
    world_rules = WORLD_CONTEXT.get("rules", [])
    world_urgency = WORLD_CONTEXT.get("urgency", "")
    world_time_limit = WORLD_CONTEXT.get("time_limit", "10 MINUTES")
    world_location = WORLD_CONTEXT.get("location", "")
    world_atmosphere = WORLD_CONTEXT.get("atmosphere", "")
    
    # Traductions des titres de section selon la langue
    section_titles = {
        "en": {
            "world_context": "=== WORLD CONTEXT (STATION ABYSSAL-6) ===",
            "description": "Description:",
            "goal": "Your mission:",
            "rules": "Important rules of this world:",
            "urgency": "Current situation:",
            "time_limit": "Time remaining:",
            "location": "Your current location:",
            "atmosphere": "Atmosphere:",
            "note": "NOTE:"
        },
        "fr": {
            "world_context": "=== CONTEXTE DU MONDE (STATION ABYSSAL-6) ===",
            "description": "Description:",
            "goal": "Votre mission:",
            "rules": "Règles importantes de ce monde:",
            "urgency": "Situation actuelle:",
            "time_limit": "Temps restant:",
            "location": "Votre position actuelle:",
            "atmosphere": "Atmosphère:",
            "note": "NOTE:"
        },
        "de": {
            "world_context": "=== WELTKONTEXT (STATION ABYSSAL-6) ===",
            "description": "Beschreibung:",
            "goal": "Ihre Mission:",
            "rules": "Wichtige Regeln dieser Welt:",
            "urgency": "Aktuelle Situation:",
            "time_limit": "Verbleibende Zeit:",
            "location": "Ihr aktueller Standort:",
            "atmosphere": "Atmosphäre:",
            "note": "HINWEIS:"
        },
        "zh": {
            "world_context": "=== 世界背景 (深渊站-6) ===",
            "description": "描述:",
            "goal": "您的任务:",
            "rules": "这个世界的重要规则:",
            "urgency": "当前情况:",
            "time_limit": "剩余时间:",
            "location": "您当前的位置:",
            "atmosphere": "氛围:",
            "note": "注意:"
        }
    }
    
    titles = section_titles.get(language, section_titles["en"])
    
    # Construire le prompt
    prompt_parts = [
        f"\n{titles['world_context']}\n",
        f"{titles['description']} {world_description}",
        f"{titles['goal']} {world_goal}"
    ]
    
    if world_rules:
        rules_text = "\n".join([f"  - {rule}" for rule in world_rules])
        prompt_parts.append(f"{titles['rules']}\n{rules_text}")
    
    if world_urgency:
        prompt_parts.append(f"{titles['urgency']} {world_urgency}")
    
    if world_time_limit:
        prompt_parts.append(f"{titles['time_limit']} {world_time_limit}")
    
    if world_location:
        prompt_parts.append(f"{titles['location']} {world_location}")
    
    if world_atmosphere:
        prompt_parts.append(f"{titles['atmosphere']} {world_atmosphere}")
    
    prompt_parts.append(f"{titles['note']} The player is a survivor trying to save the station. Help them accomplish their mission while staying in character.\n")
    
    return "\n".join(prompt_parts)

# ----------------------------
# 3. Assistant : créer un agent LangGraph avec état pour un PNJ
# ----------------------------
def create_npc_agent(npc_config: Dict[str, Any], language: str = "en"):
    """
    Returns a compiled LangGraph agent with memory, language support and world context.
    """
    # Get language-specific instruction
    language_instruction = LANGUAGE_SYSTEM_PROMPTS.get(language, LANGUAGE_SYSTEM_PROMPTS["en"])
    
    # Get world context in the appropriate language
    world_context = build_world_context_prompt(language)
    
    # Combine NPC's personality with world context and language instruction
    enhanced_system_prompt = f"""{npc_config["system_prompt"]}

{world_context}

{language_instruction}

IMPORTANT: The player is currently speaking in {language.upper()}. You must respond in the SAME language as the player. Match the player's language exactly.
IMPORTANT: You are a character in the Abyssal-6 station. Use the world context above to inform your responses and stay consistent with the game's setting and urgency."""
    
    # Utiliser le modèle spécifié dans la configuration du PNJ
    model_name = npc_config.get("model", "granite-3.2-2b-instruct")
    
    llm = ChatOpenAI(
        base_url=LLM_BASE_URL,
        api_key="not-needed",
        model=model_name,
        temperature=npc_config.get("temperature", 0.7),
    )
    
    system_message = SystemMessage(content=enhanced_system_prompt)

    # Définir le nœud unique qui traite le chat
    def chat_node(state: MessagesState):
        messages = [system_message] + state["messages"]
        response = llm.invoke(messages)
        return {"messages": [response]}

    # Construire le graphe
    builder = StateGraph(MessagesState)
    builder.add_node("npc", chat_node)
    builder.add_edge(START, "npc")
    builder.add_edge("npc", END)

    # MemorySaver conserve l'historique des conversations par thread_id
    memory = MemorySaver()
    return builder.compile(checkpointer=memory)

# ----------------------------
# 4. Instancier les agents pour tous les PNJ (par langue)
# ----------------------------
# Store agents in a nested dictionary: agents[npc_name][language]
agents: Dict[str, Dict[str, Any]] = {}

if not NPC_CONFIGS:
    print("⚠️  Warning: No NPC configurations found in npc_config.json")
    print("   Make sure the file has an 'npcs' array with character configurations.")
else:
    for cfg in NPC_CONFIGS:
        name = cfg.get("name", "unknown")
        agents[name] = {}
        # Create an agent for each supported language
        for lang in LANGUAGE_SYSTEM_PROMPTS.keys():
            agents[name][lang] = create_npc_agent(cfg, lang)
            print(f"✅ Loaded NPC: {name} for language: {lang}")

# Afficher le contexte du monde chargé
print("\n📖 World Context Loaded:")
print(f"   Name: {WORLD_CONTEXT.get('name', 'N/A')}")
print(f"   Goal: {WORLD_CONTEXT.get('goal', 'N/A')}")
print(f"   Rules: {len(WORLD_CONTEXT.get('rules', []))} rules loaded")
print(f"   Urgency: {WORLD_CONTEXT.get('urgency', 'N/A')}")
print(f"   Time Limit: {WORLD_CONTEXT.get('time_limit', 'N/A')}")
print()

# ----------------------------
# 5. Serveur web FastAPI
# ----------------------------
app = FastAPI(title="Serveur Multi-PNJ LangGraph Multilingue avec Contexte du Monde")

class ChatRequest(BaseModel):
    npc_name: str      # ex : "character_guard"
    player_id: str     # ex : "player123" – chaque joueur a une mémoire séparée
    message: str
    language: Optional[str] = "en"  # New field for language, defaults to English

@app.post("/chat")
async def chat(request: ChatRequest):
    # Validate language
    supported_languages = set(LANGUAGE_SYSTEM_PROMPTS.keys())
    if request.language not in supported_languages:
        # Default to English if language not supported
        request.language = "en"
    
    # Vérifier si le PNJ existe
    if request.npc_name not in agents:
        raise HTTPException(status_code=404, detail=f"NPC '{request.npc_name}' not found")
    
    # Get the language-specific agent
    agent = agents[request.npc_name].get(request.language)
    if not agent:
        # Fallback to English if language-specific agent doesn't exist
        agent = agents[request.npc_name]["en"]
        request.language = "en"

    # Utiliser un thread_id unique = npc_name + player_id + language
    # This ensures separate memory for each language
    config = {"configurable": {"thread_id": f"{request.npc_name}_{request.player_id}_{request.language}"}}

    try:
        # Add language and world context awareness to the user message
        enhanced_message = f"[Player message in {request.language.upper()}] {request.message}"
        
        final_state = agent.invoke(
            {"messages": [HumanMessage(content=enhanced_message)]},
            config=config
        )
        # Extraire le dernier message IA
        reply = final_state["messages"][-1].content
        return {"reply": reply, "language_used": request.language}
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

@app.get("/npcs")
async def list_npcs():
    """Return the list of available NPC names and their supported languages."""
    return {
        "npcs": list(agents.keys()),
        "supported_languages": list(LANGUAGE_SYSTEM_PROMPTS.keys())
    }

@app.get("/npc/{npc_name}/languages")
async def get_npc_languages(npc_name: str):
    """Get supported languages for a specific NPC."""
    if npc_name not in agents:
        raise HTTPException(status_code=404, detail=f"NPC '{npc_name}' not found")
    return {
        "npc_name": npc_name,
        "supported_languages": list(agents[npc_name].keys())
    }

@app.get("/world/context")
async def get_world_context():
    """Return the current world context for debugging purposes."""
    return {
        "world": WORLD_CONTEXT,
        "supported_languages": list(LANGUAGE_SYSTEM_PROMPTS.keys())
    }

@app.get("/history/{npc_name}/{player_id}/{language}")
async def get_conversation_history(npc_name: str, player_id: str, language: str = "en"):
    """
    Get the complete conversation history for a specific NPC, player, and language.
    """
    # Validate inputs
    if npc_name not in agents:
        raise HTTPException(status_code=404, detail=f"NPC '{npc_name}' not found")
    
    if language not in LANGUAGE_SYSTEM_PROMPTS:
        language = "en"
    
    # Get the agent
    agent = agents[npc_name].get(language, agents[npc_name]["en"])
    
    # Create thread_id (same as used in /chat endpoint)
    thread_id = f"{npc_name}_{player_id}_{language}"
    config = {"configurable": {"thread_id": thread_id}}
    
    try:
        # Get the current state with history
        state = agent.get_state(config)
        
        # Extract messages from state
        messages = []
        for msg in state.values.get("messages", []):
            message_data = {
                "type": type(msg).__name__,
                "content": msg.content,
                "timestamp": getattr(msg, "timestamp", None)
            }
            
            # Add additional metadata if available
            if hasattr(msg, "additional_kwargs"):
                message_data["metadata"] = msg.additional_kwargs
                
            messages.append(message_data)
        
        return {
            "npc_name": npc_name,
            "player_id": player_id,
            "language": language,
            "thread_id": thread_id,
            "message_count": len(messages),
            "messages": messages,
            "next_nodes": state.next if hasattr(state, 'next') else None
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error retrieving history: {str(e)}")


@app.get("/history/all/{npc_name}/{player_id}")
async def get_all_language_histories(npc_name: str, player_id: str):
    """
    Get conversation histories for all languages for a specific NPC and player.
    """
    if npc_name not in agents:
        raise HTTPException(status_code=404, detail=f"NPC '{npc_name}' not found")
    
    histories = {}
    for lang in LANGUAGE_SYSTEM_PROMPTS.keys():
        try:
            agent = agents[npc_name].get(lang)
            if agent:
                thread_id = f"{npc_name}_{player_id}_{lang}"
                config = {"configurable": {"thread_id": thread_id}}
                state = agent.get_state(config)
                
                messages = []
                for msg in state.values.get("messages", []):
                    messages.append({
                        "type": type(msg).__name__,
                        "content": msg.content[:200] + "..." if len(msg.content) > 200 else msg.content
                    })
                
                histories[lang] = {
                    "message_count": len(messages),
                    "preview": messages[-3:] if messages else []  # Last 3 messages
                }
        except Exception as e:
            histories[lang] = {"error": str(e)}
    
    return {
        "npc_name": npc_name,
        "player_id": player_id,
        "histories": histories
    }


@app.get("/history/checkpoints/{npc_name}/{player_id}/{language}")
async def get_checkpoint_history(npc_name: str, player_id: str, language: str = "en"):
    """
    Get all checkpoints (not just current state) for time-travel debugging.
    This shows the state after each node execution.
    """
    if npc_name not in agents:
        raise HTTPException(status_code=404, detail=f"NPC '{npc_name}' not found")
    
    if language not in LANGUAGE_SYSTEM_PROMPTS:
        language = "en"
    
    agent = agents[npc_name].get(language, agents[npc_name]["en"])
    thread_id = f"{npc_name}_{player_id}_{language}"
    config = {"configurable": {"thread_id": thread_id}}
    
    try:
        # Get all checkpoints in history (reverse chronological order)
        checkpoints = list(agent.get_state_history(config))
        
        checkpoint_data = []
        for i, checkpoint in enumerate(checkpoints):
            # Extract message from this checkpoint
            messages = checkpoint.values.get("messages", [])
            last_message = messages[-1] if messages else None
            
            checkpoint_data.append({
                "checkpoint_index": i,
                "checkpoint_id": checkpoint.config.get("configurable", {}).get("checkpoint_id"),
                "next_nodes": checkpoint.next,
                "message_count": len(messages),
                "last_message": {
                    "type": type(last_message).__name__ if last_message else None,
                    "content": last_message.content[:150] + "..." if last_message and len(last_message.content) > 150 else (last_message.content if last_message else None)
                },
                "timestamp": checkpoint.metadata.get("step", 0)
            })
        
        return {
            "npc_name": npc_name,
            "player_id": player_id,
            "language": language,
            "thread_id": thread_id,
            "total_checkpoints": len(checkpoint_data),
            "checkpoints": checkpoint_data
        }
        
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error retrieving checkpoints: {str(e)}")
# ----------------------------
# 6. Exécuter le serveur
# ----------------------------
if __name__ == "__main__":
    print("\n" + "="*50)
    print("🚀 Starting Abyssal-6 NPC Server")
    print("="*50)
    print(f"📍 LLM Server: {LLM_BASE_URL}")
    print(f"🌍 World: {WORLD_CONTEXT.get('name', 'Unknown')}")
    print(f"🎯 Goal: {WORLD_CONTEXT.get('goal', 'Unknown')}")
    print(f"🗣️  Languages: {', '.join(LANGUAGE_SYSTEM_PROMPTS.keys())}")
    print("="*50 + "\n")
    
    uvicorn.run(app, host="0.0.0.0", port=8000)