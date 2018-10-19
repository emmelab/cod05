class BotonBasico extends Auto implements AutoDraw, AutoMousePressed {
  PVector pos;
  PImage icono;
  float escala = 0.7;

  Tweener hoverEscala = new Tweener().inicializar(1, 1, 1, 1);
  Tweener toggleAlfa = new Tweener().inicializar(.5, 200, 255, 0);

  color col;

  boolean toggle, presionado;

  BotonBasico(float x, float y, float angulo, String icono, color col) {
    pos = new PVector(x, y, angulo);
    this.icono = iconos.get(icono);
    this.col = col;
    autoDraw.add(this);
    autoMousePressed.add(this);
  }

  void draw() {
    if ( autoActivo ) {
      boolean over = over(mouseX, mouseY);
      hoverEscala.actualizar(over?dt:-dt);
      toggleAlfa.actualizar(toggle?dt:-dt);

      presionado = false;
      pushStyle();
      imageMode(CENTER);
      pushMatrix();
      translate(pos.x, pos.y);
      rotate(pos.z);
      scale(escala*hoverEscala.valor());
      tint(col, toggleAlfa.valor());
      image(icono, 0, 0);
      popMatrix();
      popStyle();
    }
  }
  void mousePressed() {
    if( !autoActivo ) return;
    if (over(mouseX, mouseY)) {
      presionado = true;
      toggle = !toggle;
    }
  }

  boolean over(float x, float y) {
    return dist(x, y, pos.x, pos.y) < icono.width*escala/2;
  }
}
