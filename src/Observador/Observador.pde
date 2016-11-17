PApplet p5;
Motor motor;

int estabilidadGeneral = 6;

void setup(){
    
  size( 800, 600, P2D );
  p5 = this;
  motor = new Motor();

}

void draw(){
  frame.setTitle( "fps: " + frameRate );  
  
  try{
    motor.ejecutar();
  }catch( Exception e ){
    println( "ERROR DRAW: " + e.getMessage() );
    exit();
    return;
  }
  
}

void keyPressed(){
  motor.keyPressed();
}

//---------------------------------------------- EVENTOS KINECT
void onNewUser(SimpleOpenNI curContext, int userId)
{
  println("onNewUser - userId: " + userId);
  println("\tstart tracking skeleton");

  curContext.startTrackingSkeleton(userId);
  //--------------------------------------------------------------------desequilibrio
  UsuarioDesequilibrio uD = new UsuarioDesequilibrio( curContext, userId, estabilidadGeneral, 25, 45 );
  motor.desequilibrios.put( userId, uD );
  //--------------------------------------------------------------------niveles
  UsuarioNivel uN = new UsuarioNivel( curContext, userId, estabilidadGeneral, SUELO_MIN, SUELO_MAX);//.5, .8 );
  motor.niveles.put( userId, uN );
  //---------------------------------------------------------------------cerrado
  int [] extremidades = new int[] {
    SimpleOpenNI.SKEL_LEFT_HAND, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_RIGHT_ELBOW
  };
  int [] centro = new int[] {
    SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_SHOULDER
  };
  UsuarioCerrado uC = new UsuarioCerrado( curContext, userId, estabilidadGeneral, 1.9, extremidades, centro );
  motor.cerrados.put( userId, uC );
}

void onLostUser(SimpleOpenNI curContext, int userId)
{
  println("onLostUser - userId: " + userId);
}

void onVisibleUser(SimpleOpenNI curContext, int userId)
{
}
