ArrayList<AutoSetup> autoSetup = new ArrayList();
ArrayList<AutoDraw> autoDraw = new ArrayList();
ArrayList<AutoKeyPressed> autoKeyPressed = new ArrayList();
ArrayList<AutoKeyReleased> autoKeyReleased = new ArrayList();
ArrayList<AutoMousePressed> autoMousePressed = new ArrayList();
ArrayList<AutoMouseReleased> autoMouseReleased = new ArrayList();

interface AutoSetup { void setup(); }
interface AutoDraw { void draw(); }
interface AutoKeyPressed { void keyPressed(); }
interface AutoKeyReleased { void keyReleased(); }
interface AutoMousePressed { void mousePressed(); }
interface AutoMouseReleased { void mouseReleased(); }

Paleta paleta = new Paleta();
DiccionarioIconos dicIcos = new DiccionarioIconos();
Iconos iconos = new Iconos();
Interfaz interfaz = new Interfaz();
