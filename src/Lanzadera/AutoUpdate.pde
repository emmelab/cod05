ArrayList<AutoSetup> autoSetup = new ArrayList();
ArrayList<AutoDraw> autoDraw = new ArrayList();
ArrayList<AutoKeyPressed> autoKeyPressed = new ArrayList();
ArrayList<AutoKeyReleased> autoKeyReleased = new ArrayList();
ArrayList<AutoMousePressed> autoMousePressed = new ArrayList();
ArrayList<AutoMouseReleased> autoMouseReleased = new ArrayList();

interface Nombre { void setNombre( String nombre ); }
interface Debug extends Nombre{ void debug( boolean setup ); /*String setNombre(); String getNombre();*/ String getImplementaciones(); }
interface AutoSetup extends Debug { void setup(); }
interface AutoDraw extends Debug { void draw(); }
interface AutoKeyPressed extends Debug { void keyPressed(); }
interface AutoKeyReleased extends Debug { void keyReleased(); }
interface AutoMousePressed extends Debug { void mousePressed(); }
interface AutoMouseReleased extends Debug { void mouseReleased(); }

Paleta paleta;
DiccionarioIconos dicIcos;
Iconos iconos;
Interfaz interfaz;

void iniciarPrincipal(){
  paleta = new Paleta( "asdasd" );
  dicIcos = new DiccionarioIconos();
  iconos = new Iconos();
  interfaz = new Interfaz();
}
