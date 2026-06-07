@echo off
title Serveur PNJ - Station Abyssale-6

REM Force local ANSI standard for CMD parsing stability
chcp 1252 >nul
set PYTHONIOENCODING=utf-8

echo ========================================
echo   Serveur PNJ - Station Abyssale-6
echo ========================================
echo.

REM Checking Python
python -c "import sys; print(f'[OK] Python trouve : {sys.version.split()[0]}')" >nul 2>&1
if errorlevel 1 (
    echo [ERREUR] Python n est pas installe ou pas dans le PATH.
    pause
    exit /b 1
)

echo [OK] Python est detecte.
echo.

REM Checking dependencies
echo Verification des dependances...
python -c "import langgraph" >nul 2>&1
if errorlevel 1 (
    echo [ATTENTION] Dependances manquantes. Installation...
    pip install langgraph langchain_openai fastapi uvicorn
    if errorlevel 1 (
        echo [ERREUR] Echec de l installation des dependances.
        pause
        exit /b 1
    )
) else (
    echo [OK] Dependances trouvees.
)
echo.

REM Checking JSON configuration file
if not exist "npc_config.json" (
    echo [ERREUR] npc_config.json introuvable !
    pause
    exit /b 1
)

REM Checking LM Studio Connection
echo Test de connexion a LM Studio...
curl -s -o nul -w "%%{http_code}" http://localhost:1234/v1/models > "%temp%\temp_check.txt" 2>nul
set /p response=<"%temp%\temp_check.txt"
del "%temp%\temp_check.txt" 2>nul

if "%response%"=="200" (
    echo [OK] LM Studio est en cours d execution.
) else (
    echo [ATTENTION] Impossible de se connecter a LM Studio.
    echo Assurez-vous que le serveur LM Studio est lance.
    echo.
    echo Appuyez sur une touche pour continuer, ou Ctrl+C pour quitter...
    pause >nul
)
echo.

set "LLM_BASE_URL=http://localhost:1234/v1"

REM Démarrer le serveur
echo ========================================
echo   Demarrage du serveur PNJ...
echo ========================================
echo.
echo Le serveur sera disponible sur :
echo   http://localhost:8000
echo   http://localhost:8000/npcs
echo.
echo Appuyez sur Ctrl+C pour arrêter le serveur.
echo.

python npc_server.py

pause