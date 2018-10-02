PApplet p5;
Reloj reloj;
Consola consola;
Paleta paleta;
Motor motor;
GuiP5 gui;

boolean test = false;

void setup(){
  size( 800, 600 );
  p5 = this;
  reloj = new Reloj();
  consola = new Consola();
  consola.setVerFps( false );
  consola.setDebug( true );
  paleta = new Paleta();
  motor = new Motor();
  gui = new GuiP5( p5 );
}

void draw(){
  background( paleta.grisFondo );
  reloj.actualizar();
  motor.ejecutar();
  gui.ejecutar();
  consola.ejecutar();
}
