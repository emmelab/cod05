import oscP5.*;
import netP5.*;

int lt = 0;
float dt = 1f/60;

PFont openSans_Semibold;
PFont openSans_Light;

OscP5 oscTester;
InterfazIPs g;
Conexiones IPs;

void setup() {
  size( 800, 600 );
  
  
  inicializarTipografias(29);
  oscTester = new OscP5(this,5000);
  IPs = new Conexiones("../lanzadera.xml");
  //g = new InterfazIPs(IPs);
  
  
  for(AutoSetup auto : autoSetup) auto.setup();
}

void inicializarTipografias(float textSize){
    openSans_Semibold = createFont( "OpenSans-Semibold.ttf", textSize);
    openSans_Light = createFont( "OpenSans-Light.ttf", textSize);
    textFont(openSans_Semibold);
}

void draw() {
  dt = (millis()-lt)/1000f;
  lt = millis();
  
    background(#25292b);
  for(AutoDraw auto : autoDraw) auto.draw();
}
void keyPressed() {
  for(AutoKeyPressed auto : autoKeyPressed) auto.keyPressed();
}
void keyReleased() {
  for(AutoKeyReleased auto : autoKeyReleased) auto.keyReleased();
}
void mousePressed() {
  for(AutoMousePressed auto : autoMousePressed) auto.mousePressed();
}
void mouseReleased() {
  for(AutoMouseReleased auto : autoMouseReleased) auto.mouseReleased();
}