PApplet p5;
Reloj reloj;
Consola consola;
Paleta paleta;
Motor motor;
GuiP5 gui;

void setup(){
  size( 800, 600, P2D );
  p5 = this;
  reloj = new Reloj();
  consola = new Consola();
  consola.setVerFps( false );
  consola.setDebug( true );
  paleta = new Paleta();
  motor = new Motor();
  gui = new GuiP5( p5 );
  iniciarTitle();
}

void iniciarTitle(){
  try{String version = loadStrings( "../version.txt" )[0];
  surface.setTitle( getClass().getName() + " "+(version!=null?version:""));}
  catch( Exception e ){System.err.println( "Exception title: " + e.getMessage() );}
}

void draw(){
  background( paleta.grisFondo );
  reloj.actualizar();
  motor.ejecutar();
  gui.ejecutar();
  consola.ejecutar();
}
