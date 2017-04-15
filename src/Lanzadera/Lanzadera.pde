import oscP5.*;
import netP5.*;

InterfazIPs g;
Conexiones IPs;

void setup() {
  size( 1024, 768 );
  IPs = new Conexiones("data/configuracion.xml");
  g = new InterfazIPs(IPs);

}

void draw() {
  g.draw();
}

void keyPressed() {
  g.keyPressed();
}

void mousePressed() {
  g.mousePressed();
}

void mouseReleased() {
  g.mouseReleased();
}