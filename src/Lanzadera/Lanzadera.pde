boolean modoPDE = true;// poner en false cuando se haga una exportacion posta
boolean modoDummies = true && modoPDE;// lo mismo

import oscP5.*;
import netP5.*;

int lt = 0;
float dt = 1f/60;

PFont openSans_Semibold;
PFont openSans_Light;

int oscP5Port = 12000;
OscP5 oscP5;

Reloj reloj = new Reloj();
Consola consola = new Consola( false /*show fps*/ );

void settings() {
  size( 800, 600 );
}

void setup(){
  inicializarTipografias(29);
  oscP5 = new OscP5(this,oscP5Port);
  if( paleta != null )for(AutoSetup auto : autoSetup) auto.setup();
}

void inicializarTipografias(float textSize){
    openSans_Semibold = createFont( "OpenSans-Semibold.ttf", textSize);
    openSans_Light = createFont( "OpenSans-Light.ttf", textSize);
    textFont(openSans_Semibold);
}

void draw() {
  reloj.actualizar();
  dt = (millis()-lt)/1000f;
  lt = millis();
    background(paleta.fondo);
  for(AutoDraw auto : autoDraw) auto.draw();
  consola.ejecutar();
}
void keyPressed() {
  if( key == 'c' || key == 'C' ) consola.setDebug( !consola.getDebug() );
  //if(!interfaz.introActiva)
    for(AutoKeyPressed auto : autoKeyPressed) auto.keyPressed();
}
void keyReleased() {
  if(!interfaz.introActiva)for(AutoKeyReleased auto : autoKeyReleased) auto.keyReleased();
}
void mousePressed() {
  if(!interfaz.introActiva)for(AutoMousePressed auto : autoMousePressed) auto.mousePressed();
}
void mouseReleased() {
  if(!interfaz.introActiva)for(AutoMouseReleased auto : autoMouseReleased) auto.mouseReleased();
}
