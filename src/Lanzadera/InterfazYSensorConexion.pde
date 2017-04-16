//void reintentarConexion

void oscStatus(OscStatus estado){
  println(estado);
  oscStatus(estado.id());
}
void oscStatus(int estado){
  if (estado != OscStatus.DEFAULT && estado != OscStatus.SEND_FAILED && estado != OscStatus.OSCP5_CLOSED){
    println("algun problema de conexion: "+estado);    
  }
}

class InterfazYSensorConexion implements AutoDraw {
  TwOutQuad animacion;
  PImage iconoConexion;
  boolean visible = true;// Esto debe ser true si al menos hay uno de los modulos desactivados
  
  boolean reintantando;
  final String ipLocalHost = "127.0.0.1";  
  
  InterfazYSensorConexion(){
    autoDraw.add(this);
    icono = iconos.get(conexion);
    if (this.icono == null) this.icono = iconos.iconoVacio();
    animacion = (TwOutQuad)(new TwOutQuad()).inicializar(0.5f);
  }
  void draw(){
    boolean sinConexion = oscTester.ip().equals(ipLocalHost);
    pushStyle();
    //if (sinConexion)
    {
      fill(255,0,0);
      textAlign(LEFT,TOP);
      textSize(16);
      text("Sin Conexion (posiblemente)",12,8);
    }
    popStyle();
  }
}