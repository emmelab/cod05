class Interfaz implements AutoSetup, AutoDraw {
  BotonModulo lienzo, observador, carrete;
  InterfazYSensorConexion interfazYSensorConexion;
  BarraSuperior barraSuperior;
  TwOutQuad animTodoGris;
  boolean todoLocal = true;

  Interfaz() {
    autoSetup.add(this);
    autoDraw.add(this);
    animTodoGris = (TwOutQuad)(new TwOutQuad()).inicializar(.9,0,3);
  }
  void setup() {
    interfazYSensorConexion = new InterfazYSensorConexion();
    {
      float verti = height/2;
      float sepHoriz = 160;
      lienzo = new BotonModulo(new PVector(width/2-sepHoriz, verti), dicIcos.lienzo, paleta.ips[0]);
      observador = new BotonModulo(new PVector(width/2, verti), dicIcos.observador, paleta.ips[1]);
      carrete = new BotonModulo(new PVector(width/2+sepHoriz, verti), dicIcos.carrete, paleta.ips[2]);
    }
    barraSuperior = new BarraSuperior();
  }
  void draw() {
    if(introActiva)intro();
    else {
      //interfazYSensorConexion.visible = !lienzo.local || !observador.local || !carrete.local;
      todoLocal = lienzo.local && observador.local && carrete.local;
      grisPorTodoLocal();
    }
    
    lienzo.colEncendido = interfazYSensorConexion.lienzo.col;
    observador.colEncendido = interfazYSensorConexion.observador.col;
    carrete.colEncendido = interfazYSensorConexion.carrete.col;
  }
  
  float introTime = 0;
  boolean introActiva = true;
  void intro(){
    float introBotonModuloBase = 2, introBotonModuloSep = .05f;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*0))lienzo.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*1))observador.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*2))carrete.mostrar = true;
    
    introTime += dt;
    if (introTime > introBotonModuloBase+introBotonModuloSep*2) introActiva = false;
  }
  boolean introCheck(float t){
    return introTime < t && introTime+dt >= t;
  }
  
  void grisPorTodoLocal(){
    animTodoGris.actualizar(todoLocal?dt:-dt);
    carrete.todoGris = constrain(animTodoGris.valor()-2,0,1);
    observador.todoGris = constrain(animTodoGris.valor()-1,0,1);
    lienzo.todoGris = constrain(animTodoGris.valor(),0,1);
  }
}