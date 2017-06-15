class BotonModulo implements AutoDraw, AutoMousePressed {
  EstadoModulo estado = EstadoModulo.LOCAL;
  boolean mostrar = false, remotoEncontrado = false;
  float todoGris = 0;
  TwOutBack animPos, animAro;
  TwOutQuad animAlfa, animColor;
  PVector pos;
  PImage icono, aroCerrado, aroAbierto;
  color colEncendido, colApagado;

  BotonModulo(PVector pos, String icono, color col) {
    this.pos = pos;
    this.icono = iconos.get(icono);
    this.aroCerrado = iconos.get(dicIcos.aroCerrado);
    this.aroAbierto = iconos.get(dicIcos.aroAbierto);
    if (this.icono == null) this.icono = iconos.iconoVacio();
    this.colEncendido = col;
    this.colApagado = color(red(col)*.299+green(col)*.587+blue(col)*.144);
    animAlfa = (TwOutQuad)(new TwOutQuad()).inicializar(.5, 0, 255);
    animAro = (TwOutBack)(new TwOutBack()).inicializar(.5, this.icono.width*.8, this.icono.width*1.2, .5);
    animPos = (TwOutBack)(new TwOutBack()).inicializar(.3, pos.y-100, pos.y);
    animColor = (TwOutQuad)(new TwOutQuad()).inicializar(.3);

    autoDraw.add(this);
    autoMousePressed.add(this);
  }

  void mousePressed() {
    if (dist(pos.x, pos.y, mouseX, mouseY) < icono.width/2) {
      if (estado == EstadoModulo.APAGADO) estado = EstadoModulo.LOCAL;
      else if (estado == EstadoModulo.LOCAL) estado = EstadoModulo.REMOTO;
      else if (estado == EstadoModulo.REMOTO) estado = EstadoModulo.APAGADO;
    }
  }

  void draw() {
    if (mostrar) {
      animAlfa.actualizar(dt);
      animPos.actualizar(dt);
      if (animPos.estado >= animPos.duracion) {
        animColor.actualizar(estado==EstadoModulo.LOCAL?-dt:dt);
        animAro.actualizar(estado!=EstadoModulo.APAGADO?dt:-dt);
      }
    }
    pushStyle();
    imageMode(CENTER);
    tint( lerpColor( colEncendido, colApagado, todoGris), animAlfa.valor());
    pushMatrix();
    translate(pos.x, animPos.valor());
    rotate(pos.z);
    pos.z += dt*.75;
    if (animAro.estado>0) {
      image(aroAbierto, 0, 0, animAro.valor(), animAro.valor());
    }
    rotate(HALF_PI);
    image(aroAbierto, 0, 0, animAro.valorMayor, animAro.valorMayor);
    popMatrix();
    tint( lerpColor( lerpColor(colEncendido, colApagado, animColor.valor()), colApagado, todoGris), animAlfa.valor());
    image(icono, pos.x, animPos.valor());    
    popStyle();
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