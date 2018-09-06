ArrayList<AutoSetup> autoSetup = new ArrayList();
ArrayList<AutoDraw> autoDraw = new ArrayList();
ArrayList<AutoKeyPressed> autoKeyPressed = new ArrayList();
ArrayList<AutoKeyReleased> autoKeyReleased = new ArrayList();
ArrayList<AutoMousePressed> autoMousePressed = new ArrayList();
ArrayList<AutoMouseReleased> autoMouseReleased = new ArrayList();

interface Debug { void debug(); String getImplementaciones(); }
interface AutoSetup extends Debug { void setup(); }
interface AutoDraw extends Debug { void draw(); }
interface AutoKeyPressed extends Debug { void keyPressed(); }
interface AutoKeyReleased extends Debug { void keyReleased(); }
interface AutoMousePressed extends Debug { void mousePressed(); }
interface AutoMouseReleased extends Debug { void mouseReleased(); }

Paleta paleta = new Paleta();
DiccionarioIconos dicIcos = new DiccionarioIconos();
Iconos iconos = new Iconos();
Interfaz interfaz = new Interfaz();
