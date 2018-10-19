class Modos {

  String modo;
  float tam;
  float diametroIconoCentral;
  color[][] paleta;
  float centroX ;
  float centroY ;
  boolean usarTexto = false;

  Iconos iconos;
  Iconos iconosGrandes;
  IconoCentral iconoCentral;


  Modos() {
    iconoCentral = new IconoCentral();
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    modo = ESPERA;
    iconos = new Iconos(int(tam*70/100));
    iconosGrandes = new Iconos((int)tam);
    setIconoCentral(COD05_2, paleta[2][3]);
  }

  Modos(color[][] paleta_, PVector posCentral_, float tam_) {    
    iconoCentral = new IconoCentral();
    paleta = paleta_;
    modo = ESPERA;
    inicializar(posCentral_, tam_);
    iconos = new Iconos(int(tam*70/100));
    iconosGrandes = new Iconos((int)tam);
    if (iconoCentral.nombre==null)
      setIconoCentral(COD05_2, paleta[2][3]);
  }

  void inicializar(PVector posCentral, float tam_) {
    centroX = posCentral.x;
    centroY = posCentral.y;
    tam = tam_;
  }

  void baseGris(float cX, float cY, float t) {
    fill(paleta[1][1]);     
    ellipse(cX, cY, t, t);
  }

  void espera(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(ESPERA, cX, cY);
  }
  void agregar(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(AGREGAR, cX, cY);
  }
  void eliminar(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(ELIMINAR, cX, cY);
  }
  void maquinarias(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(MAQUINARIAS, cX, cY);
  }
  void opciones( float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(OPCIONES, cX, cY);
  }

  void ejecutar() {
    fill(paleta[2][1]);
    textAlign(CENTER, CENTER);
    textSize(30);
    if (modo.equals(ESPERA)) {
      baseGris(centroX, centroY, tam);
      espera(centroX, centroY) ;
    } else if (modo.equals(AGREGAR)) {
      baseGris(centroX, centroY, tam);
      agregar(centroX, centroY) ;
    } else if (modo.equals(ELIMINAR)) {
      baseGris(centroX, centroY, tam);
      eliminar(centroX, centroY) ;
    } else if (modo.equals(OPCIONES)) {
      baseGris(centroX, centroY, tam);
      opciones(centroX, centroY) ;
    } else if (modo.equals(MAQUINARIAS)) {
      baseGris(centroX, centroY, tam);
      maquinarias(centroX, centroY) ;
    } else {
      text(modo, centroX, centroY);
    }

    if (!(getModo().equals(OPCIONES)) && !(getModo().equals(ESPERA)) && !(getModo().equals(MAQUINARIAS))) {
      if (iconos == null)
        iconos = new Iconos(int(tam*70/100));
      tint(iconoCentral.col);    
      iconos.dibujar(iconoCentral.nombre, centroX, centroY);
      //dibujoDeEstadoDelIconoCentral(centroX, centroY, int(tam*70/100), iconoCentral.mods);
    }
  }

  void dibujoDeEstadoDelIconoCentral(float x, float y, float t, boolean mods) {
    if (mods) {
      pushStyle();
      stroke(paleta[1][1]);
      strokeWeight(6);
      fill(paleta[2][3]);  
      float tamDivisor = 4;  
      float px = x+(t/2-t/(tamDivisor*2))*cos(radians(-45));
      float py = y+(t/2-t/(tamDivisor*2))*sin(radians(-45));
      ellipse(px, py, t/tamDivisor, t/tamDivisor);
      popStyle();
    }
  }

  void setIconoCentral(Modificador mod, color c) {
    if (mod!=null) {
      iconoCentral.setNombre(mod.nombre);
      iconoCentral.setColor(c);

      if (mod.mods>0) {
        iconoCentral.setMods(true);
      } else {
        iconoCentral.setMods(false);
      }
    }
  }

  void setIconoCentral(String nombre, color c) {
    iconoCentral.setNombre(nombre);
    iconoCentral.setColor(c);
    iconoCentral.setMods(false);
  }

  void setModo(String modo_) {
    modo = modo_;
  }

  String getModo() {
    return modo;
  }
}

class IconoCentral {
  String nombre;
  color col;
  boolean mods = false;
  IconoCentral() {
  }
  void setNombre(String n) {
    nombre = n;
  }
  void setColor(color c) {
    col = c;
  }  
  void setMods(boolean m) {
    mods = m;
  }
}
