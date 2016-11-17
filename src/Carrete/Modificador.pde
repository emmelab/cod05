class Modificador {

  String nombre;
  boolean sensible;
  Categoria categoria;
  color[][] paleta;
  int mods;
  Iconos iconos;

  Modificador(String nombre_, Categoria categoria_) {
    nombre = nombre_;
    categoria = categoria_;
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
      paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }

  Modificador(String nombre_, Categoria categoria_, color[][] paleta_) {
    nombre = nombre_;
    categoria = categoria_;
    paleta = paleta_;
  }

  void addMod() {
    mods++;
  }

  void removerMod() {
    println("llega");
    if (mods>0)
      mods--;
  }

  void resetMods() {
    mods=0;
  }

  void setSensible(boolean sensible_/*, boolean estado_*/) {
    sensible = sensible_;
  }

  String getNombre() {
    return nombre;
  }

  void dibujar(float x, float y, float t, color c) {
    noStroke();

    if (categoria.sensible) {    
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
    } else {
      fill(c);

      ellipse(x, y, t/2, t/2);
    }
  }

  void dibujarIconos(float x, float y, float t, color col) {
    if (iconos == null)
      iconos = new Iconos(int(t*13/100));

    noStroke();

    if (categoria.sensible) {    
      if (sensible) {
        enSensible( x, y, t);
      } 
    
      tint(col);
      iconos.dibujar(nombre, x, y);
     
      if (mods>0) {
        pushStyle();
        noFill();
        stroke(150, 150, 220);
        ellipse(x, y, t*18/100, t*18/100);
        popStyle();
      }
    } else {
      fill(col);
      ellipse(x, y, t*6/100, t*6/100);
    }
  }

  void enSensible(float x, float y, float t) {
    pushStyle();
    noFill();
    strokeWeight(1);
    stroke(paleta[2][3]);
    ellipse(x, y, (t*13/100)*1.25, (t*13/100)*1.25);
    popStyle();
  }

  int getCant() {
    return mods;
  }
}

