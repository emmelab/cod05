import SimpleOpenNI.*;
import processing.core.PMatrix3D;

public class UsuarioEspalda{
  
  private final SimpleOpenNI kinect;
  private boolean espalda;
  private PMatrix3D[] orientacionesJoints = new PMatrix3D[ 3 ];

  public static final int[] joints = {
    SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_TORSO
  };
  
  public UsuarioEspalda( SimpleOpenNI kinect ){
    this.kinect = kinect;
    for( int i = 0; i < orientacionesJoints.length; i++ ){
      orientacionesJoints[ i ] = new PMatrix3D();
    }
  }
  
  //------------------------------------------- METODOS PUBLICOS
  
  //---- gets
  public PMatrix3D[] getOrientacionesJoints(){
    return orientacionesJoints;
  }
  
  public boolean getEspalda(){
    return espalda;
  }
  //----
  
  public void actualizar( int idUsuario ){
    
    actualizarOrientaciones( idUsuario );
    procesarOrientaciones();
    
  }
  
  //------------------------------------------- METODOS PRIVADOS
  
  private void actualizarOrientaciones( int idUsuario ){
    
    if( kinect.isTrackingSkeleton( idUsuario ) ){
      
      for( int i = 0; i < orientacionesJoints.length; i++ ){
        kinect.getJointOrientationSkeleton( idUsuario, joints[ i ], orientacionesJoints[ i ] );
      }
      
    }
    
  }
  
  private void procesarOrientaciones(){
    
    float promedio = 0.0f;
    for( PMatrix3D orientacion : orientacionesJoints ){
      promedio += orientacion.m00;
    }
    promedio /= orientacionesJoints.length;
    
    espalda = ( promedio > 0 )? false : true ;
    
  }
  
}
