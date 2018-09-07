ArrayList<AutoSetup> autoSetup = new ArrayList();
ArrayList<AutoDraw> autoDraw = new ArrayList();
ArrayList<AutoKeyPressed> autoKeyPressed = new ArrayList();
ArrayList<AutoKeyReleased> autoKeyReleased = new ArrayList();
ArrayList<AutoMousePressed> autoMousePressed = new ArrayList();
ArrayList<AutoMouseReleased> autoMouseReleased = new ArrayList();

//interface Nombre { void setNombre( String nombre ); String getNombre(); }
interface Debug{ void debug( boolean setup ); /*String setNombre(); String getNombre();*/ String getImplementaciones(); }
interface AutoSetup extends Debug { void setup(); }
interface AutoDraw extends Debug { void draw(); }
interface AutoKeyPressed extends Debug { void keyPressed(); }
interface AutoKeyReleased extends Debug { void keyReleased(); }
interface AutoMousePressed extends Debug { void mousePressed(); }
interface AutoMouseReleased extends Debug { void mouseReleased(); }

Paleta paleta = new Paleta( "paleta_Glb" );
DiccionarioIconos dicIcos = new DiccionarioIconos( "dicIcos_Glb" );
Iconos iconos = new Iconos( "iconos_Glb" );
Interfaz interfaz = new Interfaz( "interfaz_GlB" );
