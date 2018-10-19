class UIcontrol {

  Paleta selectorPaleta;
  PVector pos;
  float ancho;
  float alto;
  boolean escondido;

  UIcontrol(PVector pos_, float ancho_, float alto_, color[][] paleta_) {
    ancho = ancho_;
    alto = alto_;
     pos = pos_;
     
    selectorPaleta = new Paleta();
     selectorPaleta.setPos(new PVector(pos.x+ancho/2, pos.y+alto/2));
    selectorPaleta.setSize(ancho>alto?alto-ancho/10:ancho-ancho/10);
    selectorPaleta.setMatrizPaleta(paleta_);
   

   
    esconder();
  }

  void mouseDrag(float x, float y) {
    if (!escondido) {
      selectorPaleta.mouseDrag(x, y);
    }
  }

  void mousePressed(float x, float y) {
    if (!escondido) {
      selectorPaleta.mousePressed(x, y);
    }
  }

  void dibujar() {
    if (!escondido) {
      pushStyle();
      rectMode(CORNER);
      fill(0, 100);
      noStroke();
      rect(0, 0, ancho, alto);
      popStyle();
      selectorPaleta.dibujar();
    }
  }

  void esconder() {
    escondido = !escondido;
  }
}
