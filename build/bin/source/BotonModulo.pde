class BotonModulo extends Auto implements AutoDraw, AutoMousePressed {
  ConfiguracionCOD05.ConfigModulo config;
  EstadoModulo estado = EstadoModulo.LOCAL;
  boolean mostrar = false, remotoEncontrado = false, panelIPsAbierto = false;
  float todoGris = 0;
  Tweener animPos, animAro, animAroConectado, animAlfa, animColor;
  //TwOutBack animPos, animAro, animAroConectado;
  //TwOutQuad animAlfa, animColor;
  PVector pos;
  PImage icono, aroCerrado, aroAbierto;
  color colEncendido, colApagado;
  float escala = 0.6f;
  BotonModulo(PVector pos, String icono, color col) {
    this.pos = pos;
    setIcono( icono, 0.6 );
    this.aroCerrado = iconos.get(dicIcos.aroCerrado);
    this.aroAbierto = iconos.get(dicIcos.aroAbierto);
    this.colEncendido = col;
    this.colApagado = color(red(col)*.299+green(col)*.587+blue(col)*.144);
    iniciarTweeners();
    autoDraw.add(this);
    autoMousePressed.add(this);
  }
  
  void iniciarTweeners(){
    animAlfa = (new TwOutQuad()).inicializar(.5, 0, 255);
    animAro = (new TwOutBack()).inicializar(.5, this.icono.width*.8, this.icono.width*1.2, .5);
    animAroConectado = (new TwOutBack()).inicializar(animAro);
    animPos = (new TwOutBack()).inicializar(.3, pos.y-100, pos.y);
    animColor = (new TwOutQuad()).inicializar(.3);
  }
  
  void setIcono( String icono, float escala ){
    this.escala = escala;
    this.icono = iconos.get(icono);
    if (this.icono == null) this.icono = iconos.iconoVacio();
    iniciarTweeners();
  }

  void set(ConfiguracionCOD05.ConfigModulo config) {
    this.config = config;
    estado = config.estado;//-*-*-*-*-*-*-*-*
  }

  void mousePressed() {
    if( !autoActivo ) return;
    if (dist(pos.x, pos.y, mouseX, mouseY) < icono.width/2) {
      if (estado == EstadoModulo.APAGADO) estado = EstadoModulo.LOCAL;
      else if (estado == EstadoModulo.LOCAL) estado = panelIPsAbierto ? EstadoModulo.REMOTO : EstadoModulo.APAGADO;
      else if (estado == EstadoModulo.REMOTO) estado = panelIPsAbierto? EstadoModulo.APAGADO : EstadoModulo.LOCAL;
      if (config!=null)config.estado = estado;
    }
  }

  void draw() {
    if (mostrar) {
      animAlfa.actualizar(dt);
      animPos.actualizar(dt);
      if (animPos.estado >= animPos.duracion) {
        animColor.actualizar(estado==EstadoModulo.LOCAL?-dt:dt);
        animAro.actualizar(estado!=EstadoModulo.APAGADO?dt:-dt);
        animAroConectado.actualizar(estado!=EstadoModulo.APAGADO&&(remotoEncontrado||estado==EstadoModulo.LOCAL)?dt:-dt);
      }
    }


    pushStyle();
    imageMode(CENTER);
    tint( lerpColor( colEncendido, colApagado, todoGris), animAlfa.valor());
    pushMatrix();
    translate(pos.x, animPos.valor());
    rotate(pos.z);
    scale(escala);
    pos.z += dt*.75;
    if (animAroConectado.estado>0) {
      image(aroAbierto, 0, 0, animAroConectado.valor(), animAroConectado.valor());
    }
    if (animAro.estado > 0) {
      rotate(HALF_PI);
      image(aroAbierto, 0, 0, animAro.valor(), animAro.valor());
    }
    popMatrix();
    pushMatrix();
    tint( lerpColor( lerpColor(colEncendido, colApagado, animColor.valor()), colApagado, todoGris), animAlfa.valor());
    translate(pos.x, animPos.valor());
    scale(escala);
    image(icono, 0, 0);  
    popMatrix();
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
