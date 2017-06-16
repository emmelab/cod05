import SimpleOpenNI.*;

ComunicacionOSC comunicacionOSC;
Motor motor;

int estabilidadGeneral = 6;

void setup(){
  size( 800, 600, P2D );
  
  comunicacionOSC = new ComunicacionOSC( this );
  motor = new Motor(this);

}

void draw(){
  frame.setTitle( "fps: " + frameRate ); 
  motor.ejecutar();
}

void keyPressed(){
  motor.keyPressed();
}

void mouseWheel(MouseEvent e){
  motor.mouseWheel( e );
}

//---------------------------------------------- EVENTOS KINECT
void onNewUser(SimpleOpenNI curContext, int userId)
{
  println("onNewUser - userId: " + userId);
  println("\tstart tracking skeleton");

  curContext.startTrackingSkeleton(userId);
  
  motor.addUsuario( userId );

}

void onLostUser(SimpleOpenNI curContext, int userId)
{
  println("onLostUser - userId: " + userId);
  //motor.removerUsuario( userId );
}

void onVisibleUser(SimpleOpenNI curContext, int userId)
{
}
