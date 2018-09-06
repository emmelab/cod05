class BotonBasico implements AutoDraw, AutoMousePressed {
  PVector pos;
  PImage icono;
  float escala = 0.7;

  //--- este cambio seguramente no te agrade pero era el hardcode del momento.... xD
  //--- lo comento asi e enteras... lo que hago es crear esta variable para que la interfaz y sensor de conexion
  //--- solo dibuje el mas si algunas de las cosas no esta conectada como local...
  //--- ara que que sean faciles de encontrar todo lo que hice que tenga uqe ver con esto que espero no sea mucho
  //--- todo lo voy a comentar con un :D .... 
  boolean dibujar = true;

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
    debug( true );
  }

  void draw() {
    if (dibujar) {
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
    debug( false );
  }
  
  void mousePressed() {
    if (over(mouseX, mouseY)) {
      presionado = true;
      toggle = !toggle;
    }
  }

  boolean over(float x, float y) {
    return dist(x, y, pos.x, pos.y) < icono.width*escala/2;
  }
  
    //Implementaciones Nombre
  String nombre = "<vacio>";
  void setNombre( String nombre ){
    this.nombre = nombre;
  }
  
  //Implementaciones Debug
  void debug( boolean setup ){
    if( setup ) consola.printlnAlerta( "Construccion -> BotonBasico <- Interfaces: " + getImplementaciones() );
    else consola.println( "BotonBasico->Interfaces: " + getImplementaciones() );
  }
  /*
  void debug(){
    consola.println( "BotonBasico->Interfaces: " + getImplementaciones() );
  }*/
  
  String getImplementaciones(){
    return "AutoDraw, AutoMousePressed";
  }
  
}
