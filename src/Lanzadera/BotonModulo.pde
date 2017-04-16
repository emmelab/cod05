class BotonModulo implements AutoDraw, AutoMousePressed {
  boolean local = true;
  boolean mostrar = false;
  TwOutBack animPos, animAro;
  TwOutQuad animAlfa, animColor;
  PVector pos;
  PImage icono;
  color colEncendido, colApagado;

  BotonModulo(PVector pos, String icono, color col) {
    this.pos = pos;
    this.icono = iconos.get(icono);
    if (this.icono == null) this.icono = iconos.iconoVacio();
    this.colEncendido = col;
    this.colApagado = color(red(col)*.299+green(col)*.587+blue(col)*.144);
    animAlfa = (TwOutQuad)(new TwOutQuad()).inicializar(.5, 0, 255);
    animAro = (TwOutBack)(new TwOutBack()).inicializar(.5, this.icono.width*.8, this.icono.width*1.2);
    animPos = (TwOutBack)(new TwOutBack()).inicializar(.3, pos.y-100, pos.y);
    animColor = (TwOutQuad)(new TwOutQuad()).inicializar(.3);

    autoDraw.add(this);
    autoMousePressed.add(this);
  }

  void mousePressed() {
    if (dist(pos.x, pos.y, mouseX, mouseY) < icono.width/2) {
      local = !local;
    }
  }

  void draw() {
    if (mostrar) {
      animAlfa.actualizar(dt);
      animPos.actualizar(dt);
      animColor.actualizar(local?-dt:dt);
      animAro.actualizar(local?-dt:dt);
    }
    pushMatrix();
    pushStyle();
    imageMode(CENTER);
    tint(lerpColor(colEncendido, colApagado, animColor.valor()), animAlfa.valor());
    noFill();
    stroke(colEncendido, animAlfa.valor());
    if (animAro.estado>0) {
      float rot = millis()*.001;
      ellipseMode(CENTER);
      strokeWeight(4);
      strokeCap(SQUARE);
      aro(pos.x, animPos.valor(), animAro.valor(), animAro.valor(), 11, rot);
    }
    image(icono, pos.x, animPos.valor());    
    popStyle();
    popMatrix();
  }

  void aro(float x, float y, float w, float h, float divs, float offset) {
    if (divs == 0) {
      ellipse(x, y, w, h);
    } else {
      boolean negativo = divs < 0;
      divs = abs(divs);
      float salto = PI/divs;
      for (int i=0; i<divs; i++) {
        arc(x, y, w, h, salto*(i*2+offset-(negativo?1:0)), salto*(i*2+offset+(negativo?0:1)));
      }
    }
  }
}