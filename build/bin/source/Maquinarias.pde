class Maquinaria extends Opcion {

  String nombre;
  boolean sensible;
  color[][] paleta;
  Iconos iconos;

  Maquinaria(String nombre_) {
    nombre = nombre_;
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }

  Maquinaria(String nombre_, color[][] paleta_) {
    nombre = nombre_;
    paleta = paleta_;
  }

  void setSensible(boolean sensible_/*, boolean estado_*/) {
    sensible = sensible_;
  }

  String getNombre() {
    return nombre;
  }

  /*void dibujar(float x, float y, color c) {
   noStroke();
   pushMatrix();
   if (sensible) {
   enSensible( x, y, t);
   } 
   fill(c);
   
   ellipse(x, y, t, t);
   textSize(14);
   fill(255);
   float angulo = atan2(y-categoria.posCentro.y, x-categoria.posCentro.x);
   pushMatrix();
   translate(x, y);
   if (angulo>radians(90) || angulo<radians(-90)) {
   textAlign(RIGHT, CENTER);
   rotate(angulo-PI);
   } else {
   textAlign(LEFT, CENTER);
   rotate(angulo);
   }
   text(nombre, 0, 0);
   popMatrix();
   }*/

  void dibujarIconos(float x, float y, float t, color col) {
    if (iconos == null)
      iconos = new Iconos(int(t*27/100));
    noStroke();
    if (sensible) {
      enSensible( x, y, t);
    } 
    tint(col);
    iconos.dibujar(nombre, x, y);

 
  }

  void enSensible(float x, float y, float t) {
    pushStyle();
    noFill();
    strokeWeight(1.5);
    stroke(paleta[2][3]);
    ellipse(x, y, (t*35/100)*1.1, (t*35/100)*1.1);
    popStyle();
  }
}
