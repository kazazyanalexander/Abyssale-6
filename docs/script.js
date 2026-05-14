/* --- CHARACTER CAROUSEL SCRIPT (existing) --- */
const characters = [
  {
    name: "Docteur Williams",
    description: "Médecin du bord, peut soigner les blessures",
    file: "character_doctor.glb",
  },
  {
    name: "Garde Thompson",
    description: "Garde de sécurité, protège les zones sensibles",
    file: "character_guard.glb",
  },
  {
    name: "Dr. Chen",
    description: "Scientifique en chef, spécialiste des réacteurs",
    file: "character_scientist.glb",
  },
  {
    name: "Ingénieur Martinez",
    description: "Ingénieur système, connaît la station comme sa poche",
    file: "character_engineer.glb",
  },
  {
    name: "Infirmière",
    description: "Une infirmière faisant sa tournée",
    file: "character_nurse.glb",
  },
  {
    name: "Ombre",
    description: "Une silhouette qui semble vous suivre",
    file: "character_stalker.glb",
  },
  {
    name: "Dr. Aris Thorne",
    description: "Généticien en chef, spécialiste des mutations abyssales",
    file: "character_geneticist.glb",
  },
  {
    name: "Chercheur",
    description: "Un scientifique faisant sa tournée d'inspection",
    file: "character_researcher.glb",
  },
  {
    name: "Technicien errant",
    description: "Un technicien qui erre dans la station sans but apparent",
    file: "character_wandering_tech.glb",
  },
];
const carouselTrack = document.getElementById("carouselTrack");
const prevBtn = document.getElementById("prevBtn");
const nextBtn = document.getElementById("nextBtn");
const indicatorsContainer = document.getElementById("indicators");
let currentIndexChar = 0;
let itemWidth = 310; // 280px width + 30px gap
const engines = [];
const scenes = [];
const models = [];

// Create carousel items for characters
characters.forEach((char, index) => {
  const item = document.createElement("div");
  item.classList.add("carousel-item");
  item.innerHTML = `<div class="loading-overlay" id="loading-${index}"><div class="loading-spinner"></div></div><canvas id="canvas-${index}"></canvas><div class="character-info"><div class="character-name">${char.name}</div><div class="character-description">${char.description}</div></div>`;
  carouselTrack.appendChild(item);
  // Create indicator
  const indicator = document.createElement("div");
  indicator.classList.add("indicator");
  if (index === 0) indicator.classList.add("active");
  indicator.addEventListener("click", () => goToSlideChar(index));
  indicatorsContainer.appendChild(indicator);
  // Initialize Babylon.js scene for each character
  initScene(index, char.file);
});

const indicators = document.querySelectorAll("#indicators .indicator");
function initScene(index, fileName) {
  const canvas = document.getElementById(`canvas-${index}`);
  if (!canvas) return;
  const engine = new BABYLON.Engine(canvas, true, {
    preserveDrawingBuffer: true,
    stencil: true,
  });
  engines.push(engine);
  const createScene = async () => {
    const scene = new BABYLON.Scene(engine);
    scene.clearColor = new BABYLON.Color4(0.04, 0.09, 0.16, 0);
    const camera = new BABYLON.ArcRotateCamera(
      `camera-${index}`,
      Math.PI / 2,
      Math.PI / 3,
      5,
      BABYLON.Vector3.Zero(),
      scene,
    );
    camera.attachControl(canvas, false);
    camera.wheelPrecision = 50;
    camera.lowerRadiusLimit = 2;
    camera.upperRadiusLimit = 10;
    const hemiLight = new BABYLON.HemisphericLight(
      `hemiLight-${index}`,
      new BABYLON.Vector3(0, 1, 0),
      scene,
    );
    hemiLight.intensity = 0.6;
    const dirLight = new BABYLON.DirectionalLight(
      `dirLight-${index}`,
      new BABYLON.Vector3(-1, -2, -1),
      scene,
    );
    dirLight.position = new BABYLON.Vector3(20, 40, 20);
    dirLight.intensity = 0.8;
    try {
      const result = await BABYLON.SceneLoader.ImportMeshAsync(
        "",
        "./characters/",
        fileName,
        scene,
      );
      const root = new BABYLON.TransformNode(`root-${index}`, scene);
      result.meshes.forEach((m) => {
        if (m !== scene.meshes[0]) m.parent = root;
      });
      models[index] = root;
      const boundingInfo = root.getHierarchyBoundingVectors();
      const center = boundingInfo.min.add(boundingInfo.max).scale(0.5);
      root.position = center.scale(-1);
      root.scaling = new BABYLON.Vector3(1.2, 1.2, 1.2);
      const loadingOverlay = document.getElementById(`loading-${index}`);
      if (loadingOverlay) loadingOverlay.style.display = "none";
    } catch (error) {
      console.error(`Error loading ${fileName}:`, error);
      const loadingOverlay = document.getElementById(`loading-${index}`);
      if (loadingOverlay)
        loadingOverlay.innerHTML =
          '<span style="color: #ff6b6b; font-size: 0.9rem;">Erreur chargement</span>';
    }
    scene.onBeforeRenderObservable.add(() => {
      if (models[index])
        models[index].rotate(BABYLON.Axis.Y, 0.01, BABYLON.Space.LOCAL);
    });
    scenes.push(scene);
    engine.runRenderLoop(() => scene.render());
  };
  createScene();
  window.addEventListener("resize", () => engine.resize());
}
function updateCarouselChar() {
  const offset = -currentIndexChar * itemWidth;
  carouselTrack.style.transform = `translateX(${offset}px)`;
  const inds = document.querySelectorAll("#indicators .indicator");
  inds.forEach((ind, idx) =>
    ind.classList.toggle("active", idx === currentIndexChar),
  );
  const containerWidth = document.querySelector(
    ".carousel-container",
  ).offsetWidth;
  const maxIndex = characters.length - Math.floor(containerWidth / itemWidth);
  prevBtn.style.opacity = currentIndexChar === 0 ? "0.3" : "1";
  prevBtn.style.pointerEvents = currentIndexChar === 0 ? "none" : "auto";
  nextBtn.style.opacity = currentIndexChar >= maxIndex ? "0.3" : "1";
  nextBtn.style.pointerEvents = currentIndexChar >= maxIndex ? "none" : "auto";
}
function goToSlideChar(index) {
  const containerWidth = document.querySelector(
    ".carousel-container",
  ).offsetWidth;
  const maxIndex = characters.length - Math.floor(containerWidth / itemWidth);
  currentIndexChar = Math.max(0, Math.min(index, maxIndex));
  updateCarouselChar();
}
function nextSlideChar() {
  const containerWidth = document.querySelector(
    ".carousel-container",
  ).offsetWidth;
  const maxIndex = characters.length - Math.floor(containerWidth / itemWidth);
  if (currentIndexChar < maxIndex) {
    currentIndexChar++;
    updateCarouselChar();
  }
}
function prevSlideChar() {
  if (currentIndexChar > 0) {
    currentIndexChar--;
    updateCarouselChar();
  }
}
nextBtn.addEventListener("click", nextSlideChar);
prevBtn.addEventListener("click", prevSlideChar);
document.addEventListener("keydown", (e) => {
  if (e.key === "ArrowRight") nextSlideChar();
  if (e.key === "ArrowLeft") prevSlideChar();
});
let touchStartXChar = 0,
  touchEndXChar = 0;
carouselTrack.addEventListener("touchstart", (e) => {
  touchStartXChar = e.changedTouches[0].screenX;
});
carouselTrack.addEventListener("touchend", (e) => {
  touchEndXChar = e.changedTouches[0].screenX;
  const diff = touchStartXChar - touchEndXChar;
  if (Math.abs(diff) > 50) {
    if (diff > 0) nextSlideChar();
    else prevSlideChar();
  }
});
let autoScrollChar = setInterval(nextSlideChar, 5000);
document
  .querySelector(".carousel-container")
  .addEventListener("mouseenter", () => clearInterval(autoScrollChar));
document
  .querySelector(".carousel-container")
  .addEventListener("mouseleave", () => {
    autoScrollChar = setInterval(nextSlideChar, 5000);
  });
updateCarouselChar();
window.addEventListener("resize", () => {
  itemWidth = document.querySelector(".carousel-item").offsetWidth + 30;
  updateCarouselChar();
});

/* --- ITEM CAROUSEL SCRIPT (new) --- */
const items = [
  {
    name: "Beamer",
    description:
      "Téléporteur. Permet de mémoriser une position et de s'y téléporter.",
    img: "items/item_beamer.png",
  },
  {
    name: "Torche",
    description: "Objet d'échange.",
    img: "items/item_torch.png",
  },
  {
    name: "Carte bleue",
    description: "Objet d'échange.",
    img: "items/item_blue_card.png",
  },
  {
    name: "Bouteille d'oxygène",
    description: "Objet d'échange.",
    img: "items/item_oxygen.png",
  },
  {
    name: "Potion de force (Magic Cookie)",
    description: "Double la capacité de transport du joueur.",
    img: "items/item_magic_cookie.png",
  },
  {
    name: "Échantillon génétique",
    description: "Objet d'échange.",
    img: "items/item_genetic.png",
  },
  {
    name: "Scaphandre autonome",
    description: "Permet d'explorer les zones inondées.",
    img: "items/item_diving_suit.png",
  },
  {
    name: "Trousse de premiers soins",
    description: "Objet d'échange.",
    img: "items/item_firstaid.png",
  },
  {
    name: "Carte rouge",
    description: "Déverrouille la porte de la machinerie auxiliaire.",
    img: "items/item_red_card.png",
  },
  {
    name: "Clé anglaise",
    description: "Déverrouille la porte du réacteur.",
    img: "items/item_wrench.png",
  },
];
const itemCarouselTrack = document.getElementById("itemCarouselTrack");
const itemPrevBtn = document.getElementById("itemPrevBtn");
const itemNextBtn = document.getElementById("itemNextBtn");
const itemIndicatorsContainer = document.getElementById("itemIndicators");
let currentIndexItem = 0;
let itemWidthItem = 310;

// Create item carousel items
items.forEach((item, index) => {
  const div = document.createElement("div");
  div.classList.add("carousel-item");
  div.innerHTML = `<img src="${item.img}" alt="${item.name}" loading="lazy"><div class="item-info"><div class="item-name">${item.name}</div><div class="item-description">${item.description}</div></div>`;
  itemCarouselTrack.appendChild(div);
  const indicator = document.createElement("div");
  indicator.classList.add("indicator");
  if (index === 0) indicator.classList.add("active");
  indicator.addEventListener("click", () => goToSlideItem(index));
  itemIndicatorsContainer.appendChild(indicator);
});
const itemIndicators = document.querySelectorAll("#itemIndicators .indicator");

function updateCarouselItem() {
  const offset = -currentIndexItem * itemWidthItem;
  itemCarouselTrack.style.transform = `translateX(${offset}px)`;
  itemIndicators.forEach((ind, idx) =>
    ind.classList.toggle("active", idx === currentIndexItem),
  );
  const containerWidth = document.getElementById(
    "itemCarouselContainer",
  ).offsetWidth;
  const maxIndex = items.length - Math.floor(containerWidth / itemWidthItem);
  itemPrevBtn.style.opacity = currentIndexItem === 0 ? "0.3" : "1";
  itemPrevBtn.style.pointerEvents = currentIndexItem === 0 ? "none" : "auto";
  itemNextBtn.style.opacity = currentIndexItem >= maxIndex ? "0.3" : "1";
  itemNextBtn.style.pointerEvents =
    currentIndexItem >= maxIndex ? "none" : "auto";
}
function goToSlideItem(index) {
  const containerWidth = document.getElementById(
    "itemCarouselContainer",
  ).offsetWidth;
  const maxIndex = items.length - Math.floor(containerWidth / itemWidthItem);
  currentIndexItem = Math.max(0, Math.min(index, maxIndex));
  updateCarouselItem();
}
function nextSlideItem() {
  const containerWidth = document.getElementById(
    "itemCarouselContainer",
  ).offsetWidth;
  const maxIndex = items.length - Math.floor(containerWidth / itemWidthItem);
  if (currentIndexItem < maxIndex) {
    currentIndexItem++;
    updateCarouselItem();
  }
}
function prevSlideItem() {
  if (currentIndexItem > 0) {
    currentIndexItem--;
    updateCarouselItem();
  }
}
itemNextBtn.addEventListener("click", nextSlideItem);
itemPrevBtn.addEventListener("click", prevSlideItem);
document.addEventListener("keydown", (e) => {
  if (e.key === "ArrowRight") nextSlideItem();
  if (e.key === "ArrowLeft") prevSlideItem();
});
let touchStartXItem = 0,
  touchEndXItem = 0;
itemCarouselTrack.addEventListener("touchstart", (e) => {
  touchStartXItem = e.changedTouches[0].screenX;
});
itemCarouselTrack.addEventListener("touchend", (e) => {
  touchEndXItem = e.changedTouches[0].screenX;
  const diff = touchStartXItem - touchEndXItem;
  if (Math.abs(diff) > 50) {
    if (diff > 0) nextSlideItem();
    else prevSlideItem();
  }
});
let autoScrollItem = setInterval(nextSlideItem, 5000);
document
  .getElementById("itemCarouselContainer")
  .addEventListener("mouseenter", () => clearInterval(autoScrollItem));
document
  .getElementById("itemCarouselContainer")
  .addEventListener("mouseleave", () => {
    autoScrollItem = setInterval(nextSlideItem, 5000);
  });
updateCarouselItem();
window.addEventListener("resize", () => {
  itemWidthItem =
    document.querySelector("#itemCarouselTrack .carousel-item").offsetWidth +
    30;
  updateCarouselItem();
});

/* --- 3D STATION MAP SCRIPT (existing) --- */
(function init3D() {
  if (typeof BABYLON === "undefined" || typeof BABYLON.GUI === "undefined") {
    console.error("Babylon.js not ready, retrying...");
    setTimeout(init3D, 200);
    return;
  }
  const canvas = document.getElementById("renderCanvas");
  if (!canvas) return;
  const engine = new BABYLON.Engine(canvas, true, {
    preserveDrawingBuffer: true,
    stencil: true,
  });
  const createScene = () => {
    const scene = new BABYLON.Scene(engine);
    scene.clearColor = new BABYLON.Color4(0.002, 0.003, 0.1, 0.8);
    const camera = new BABYLON.ArcRotateCamera(
      "camera",
      BABYLON.Tools.ToRadians(180),
      BABYLON.Tools.ToRadians(20),
      1400,
      new BABYLON.Vector3(0, 200, 0),
      scene,
    );
    camera.attachControl(canvas, true);
    camera.minZ = 0.1;
    camera.wheelPrecision = 80;
    camera.wheelDeltaPercentage = 0.01;
    camera.lowerRadiusLimit = 150;
    camera.upperRadiusLimit = 3200;
    camera.lowerBetaLimit = 0.1;
    camera.upperBetaLimit = Math.PI - 0.1;
    camera.useAutoRotationBehavior = true;
    camera.autoRotationBehavior.idleRotationSpeed = -0.05;
    camera.autoRotationBehavior.idleRotationWaitTime = 3000;
    const hemiLight = new BABYLON.HemisphericLight(
      "hemiLight",
      new BABYLON.Vector3(0, 1, 0),
      scene,
    );
    hemiLight.intensity = 0.7;
    hemiLight.diffuse = new BABYLON.Color3(0.9, 0.95, 1.0);
    hemiLight.groundColor = new BABYLON.Color3(0.2, 0.2, 0.3);
    const pointLight = new BABYLON.PointLight(
      "pointLight",
      new BABYLON.Vector3(800, 1200, -800),
      scene,
    );
    pointLight.intensity = 1.2;
    const pipeTexture = new BABYLON.Texture("textures/cont_left.jpg", scene);
    pipeTexture.uScale = pipeTexture.vScale = 3;
    const obsTexture = new BABYLON.Texture("textures/observation.jpg", scene);
    obsTexture.wAng = Math.PI;
    const frontTex = new BABYLON.Texture("textures/cont_front.jpg", scene);
    const backTex = frontTex;
    const topTex = new BABYLON.Texture("textures/box_side.jpg", scene);
    const bottomTex = topTex;
    const leftTex = new BABYLON.Texture("textures/cont_top.jpg", scene);
    leftTex.wAng = Math.PI / 2;
    const rightTex = leftTex;
    const pipeMat = new BABYLON.StandardMaterial("pipeMat", scene);
    pipeMat.diffuseTexture = pipeTexture;
    pipeMat.specularColor = new BABYLON.Color3(0.3, 0.3, 0.3);
    pipeMat.specularPower = 35;
    const scale = 1.2;
    const connections = [
      [0, 0, 0, 0, 0, 200],
      [0, 0, 0, 0, 200, 0],
      [0, 200, 0, 0, 400, 0],
      [0, 400, 0, 0, 600, 0],
      [0, 400, 0, 300, 400, 0],
      [0, 600, 0, -300, 600, 0],
      [300, 400, 0, 300, 400, -200],
      [600, 400, -200, 600, 600, -200],
      [0, 200, 0, 0, 200, 200],
      [0, 200, 200, 0, 400, 200],
      [300, 400, -200, 600, 400, -200],
      [0, 200, 200, 300, 200, 200],
      [300, 400, 200, 300, 200, 200],
      [0, 400, 200, 300, 400, 200],
    ];
    connections.forEach(([x1, y1, z1, x2, y2, z2]) => {
      const p1 = new BABYLON.Vector3(x1 * scale, z1 * scale, y1 * scale);
      const p2 = new BABYLON.Vector3(x2 * scale, z2 * scale, y2 * scale);
      const dist = BABYLON.Vector3.Distance(p1, p2);
      const tube = BABYLON.MeshBuilder.CreateCylinder(
        "tube",
        { height: dist, diameter: 44, tessellation: 16 },
        scene,
      );
      tube.position = p1.add(p2).scale(0.5);
      tube.lookAt(p2, 0, Math.PI / 2);
      tube.material = pipeMat;
    });
    const rooms = [
      {
        name: "Observation",
        x: 0,
        y: 400,
        z: 200,
        col: "#00ffff",
        isBox: false,
      },
      {
        name: "Hydroponic",
        x: 300,
        y: 400,
        z: 200,
        col: "#ffffff",
        isBox: true,
      },
      {
        name: "Dormitories",
        x: 0,
        y: 200,
        z: 200,
        col: "#ffd700",
        isBox: true,
      },
      {
        name: "Infirmary",
        x: 300,
        y: 200,
        z: 200,
        col: "#ff0000",
        isBox: true,
      },
      { name: "Sas", x: 0, y: 0, z: 0, col: "#ff0000", isBox: true },
      { name: "Transporter", x: 0, y: 0, z: 200, col: "#ffffff", isBox: false },
      { name: "Airlock", x: 0, y: 200, z: 0, col: "#ff0000", isBox: true },
      { name: "Guard", x: 0, y: 400, z: 0, col: "#32cd32", isBox: true },
      { name: "MedBay", x: 0, y: 600, z: 0, col: "#32cd32", isBox: true },
      {
        name: "Greenhouse",
        x: -300,
        y: 600,
        z: 0,
        col: "#ffd700",
        isBox: true,
      },
      { name: "Laboratory", x: 300, y: 400, z: 0, col: "#ffffff", isBox: true },
      { name: "Reactor", x: 600, y: 600, z: -200, col: "#00ffff", isBox: true },
      { name: "Engine", x: 600, y: 400, z: -200, col: "#32cd32", isBox: true },
      {
        name: "Ext. Engine",
        x: 300,
        y: 400,
        z: -200,
        col: "#32cd32",
        isBox: true,
      },
    ];
    const advancedTexture =
      BABYLON.GUI.AdvancedDynamicTexture.CreateFullscreenUI("UI", true, scene);
    rooms.forEach((room) => {
      const { name, x, y, z, col, isBox } = room;
      const posX = x * scale,
        posY = z * scale,
        posZ = y * scale;
      let mesh;
      if (isBox) {
        const width = 240,
          height = 120,
          depth = 120;
        mesh = BABYLON.MeshBuilder.CreateBox(
          name,
          { width, height, depth },
          scene,
        );
        const faceMaterials = [];
        const texArray = [
          frontTex,
          backTex,
          topTex,
          bottomTex,
          leftTex,
          rightTex,
        ];
        for (let i = 0; i < 6; i++) {
          const mat = new BABYLON.StandardMaterial(name + "_face" + i, scene);
          mat.diffuseTexture = texArray[i];
          mat.specularColor = new BABYLON.Color3(0.2, 0.2, 0.2);
          faceMaterials.push(mat);
        }
        const multiMat = new BABYLON.MultiMaterial(name + "_multi", scene);
        multiMat.subMaterials = faceMaterials;
        mesh.material = multiMat;
        mesh.subMeshes = [];
        const vCount = mesh.getTotalVertices();
        const iCount = mesh.getIndices().length;
        for (let i = 0; i < 6; i++)
          new BABYLON.SubMesh(i, 0, vCount, (iCount / 6) * i, iCount / 6, mesh);
      } else {
        mesh = BABYLON.MeshBuilder.CreateSphere(name, { diameter: 200 }, scene);
        const sphereMat = new BABYLON.StandardMaterial(
          name + "_sphereMat",
          scene,
        );
        sphereMat.diffuseTexture = obsTexture;
        sphereMat.specularColor = new BABYLON.Color3(0.2, 0.2, 0.2);
        sphereMat.diffuseColor = BABYLON.Color3.FromHexString(col);
        mesh.material = sphereMat;
      }
      mesh.position = new BABYLON.Vector3(posX, posY, posZ);
      const label = new BABYLON.GUI.TextBlock();
      label.text = name;
      label.color = col;
      label.fontSize = 16;
      label.fontStyle = "bold";
      label.shadowBlur = 4;
      label.shadowColor = "black";
      label.shadowOffsetX = 2;
      label.shadowOffsetY = 2;
      label.width = "220px";
      label.height = "60px";
      label.textWrapping = true;
      label.textHorizontalAlignment =
        BABYLON.GUI.Control.HORIZONTAL_ALIGNMENT_CENTER;
      advancedTexture.addControl(label);
      label.linkWithMesh(mesh);
      label.linkOffsetY = -70;
      label.linkOffsetX = 0;
    });
    return scene;
  };
  const scene = createScene();
  engine.runRenderLoop(() => scene.render());
  window.addEventListener("resize", () => engine.resize());
  // Prevent page scroll when interacting with the 3D canvas
  canvas.addEventListener("wheel", (e) => e.preventDefault(), {
    passive: false,
  });
  canvas.addEventListener("touchmove", (e) => e.preventDefault(), {
    passive: false,
  });
  canvas.addEventListener("touchstart", (e) => e.stopPropagation(), {
    passive: false,
  });
})();
