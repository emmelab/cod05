import SimpleOpenNI.*;

ComunicacionOSC comunicacionOSC;
Paleta paleta;
Motor motor;

int estabilidadGeneral = 6;

void setup(){
  size( 800, 600, P2D );
  comunicacionOSC = new ComunicacionOSC( this );
  paleta = new Paleta();
  motor = new Motor(this);
  iniciarTitle();
}

void iniciarTitle(){
  try{String version = loadStrings( "../../version.txt" )[0];
  frame.setTitle( getClass().getName() + " "+(version!=null?version:""));}
  catch( Exception e ){System.err.println( "Exception title: " + e.getMessage() );}
}

void draw(){
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
