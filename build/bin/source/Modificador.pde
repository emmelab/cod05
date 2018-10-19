class Modificador {

  String nombre;
  boolean sensible;
  boolean hover;
  boolean hoverExtendido; //---------------como el icono es muy pequeño el espaio de hover es muy reducido
  //---------------- entonces esto es para extender el espacio de hover sin embargo mantener 
  //----------------el click solo en el espacio de incono
  Categoria categoria;
  color[][] paleta;
  int mods;
  Iconos iconos;
  float x_;
  float y_;
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
    x_ = x;
    y_ = y;
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
    if (bdd.interaccionConMouse) {
      mouseHover( x, y, t*12/100);
      mouseHover_extendido( x, y, t*18/100);
      sensible = hover;
    }

    if (categoria.sensible) {    
      if (sensible) {
        enSensible( x, y, t);
      } 


      tint(col);
      iconos.dibujar(nombre, x, y);

      if (mods>0) {
        pushStyle();
        noFill();
        stroke(paleta[1][3]);  
        strokeWeight(2);      
        ellipse(x, y, t*20/100, t*20/100);
        popStyle();
      }
    } else {
      fill(col);
      ellipse(x, y, t*6/100, t*6/100);

      if (mods>0) {
        pushStyle();
        noFill();
        stroke(paleta[1][3]);  
        strokeWeight(2);      
        ellipse(x, y, t*8/100, t*8/100);
        popStyle();
      }
    }
  }



  void enSensible(float x, float y, float t) {
    pushStyle();
    // noFill();
    // strokeWeight(1);
    // stroke(paleta[2][3]);
    noStroke();
    fill(paleta[2][4]);
    ellipse(x, y, (t*13/100)*1.5, (t*13/100)*1.5);
    popStyle();
  }

  int getCant() {
    return mods;
  }

  void mouseHover(float x, float y, float t) {
    if (dist(mouseX, mouseY, x, y)<t)
      hover=true;
    else
      hover=false;
  }

  void mouseHover_extendido(float x, float y, float t) {
    if (dist(mouseX, mouseY, x, y)<t)
      hoverExtendido=true;
    else
      hoverExtendido=false;
  }
}
