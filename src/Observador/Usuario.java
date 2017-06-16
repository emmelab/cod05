import SimpleOpenNI.*;
import processing.core.PVector;

public class Usuario{
  
  private final int id;
  private final SimpleOpenNI kinect;
  private UsuarioDesequilibrio desequilibrio;
  private UsuarioNivel nivel;
  private UsuarioCerrado cerrado;
  private UsuarioEspalda espalda;
  
  private PVector[] posicionesJoints = new PVector[ 15 ];
  private PVector[] velocidadesJoints = new PVector[ 15 ];
  private float[] confianzasJoints = new float[ 15 ];
  
  public static final String[] nombresDeJoints = { 
  "CABEZA", "CUELLO", "HOMBRO_IZQUIERDO", "HOMBRO_DERECHO", "CODO_IZQUIERDO", "CODO_DERECHO", "MANO_IZQUIERDA", "MANO_DERECHA", "TORSO",
  "CADERA_IZQUIERDA", "CADERA_DERECHA", "RODILLA_IZQUIERDA", "RODILLA_DERECHA", "PIE_IZQUIERDO", "PIE_DERECHO"
  };
  
  public Usuario( SimpleOpenNI kinect, int idUsuario, int estabilidadGeneral ){
    this.id = idUsuario;
    this.kinect = kinect;
    desequilibrio = new UsuarioDesequilibrio( kinect, idUsuario, estabilidadGeneral );
    nivel = new UsuarioNivel( kinect, idUsuario, estabilidadGeneral );
    cerrado = new UsuarioCerrado( kinect, idUsuario, estabilidadGeneral );
    espalda = new UsuarioEspalda( kinect );
    
    for( int i = 0; i < posicionesJoints.length; i++ ){
      posicionesJoints[ i ] = new PVector();
      velocidadesJoints[ i ] = new PVector();
    }
    
  }
  
  //---------------------------------------- METODOS PUBLICOS
  
  //---- geters
  public int getId(){
    return id;  
  }
  
  public SimpleOpenNI getKinect(){
    return kinect;
  }
  
  public UsuarioDesequilibrio getDesequilibrio(){
    return desequilibrio;
  }
  
  public UsuarioNivel getNivel(){
    return nivel;
  }
  
  public UsuarioCerrado getCerrado(){
    return cerrado;
  }
  
  public UsuarioEspalda getEspalda(){
    return espalda;
  }
  
  public PVector[] getPosicionesJoints(){
    return posicionesJoints;
  }
  
  public PVector[] getVelocidadesJoints(){
    return velocidadesJoints;
  }
  
  public float[] getConfianzasJoints(){
    return confianzasJoints;
  }
  
  //----
  
  public void actualizar(){
    
    desequilibrio.actualizar();
    nivel.actualizar();
    cerrado.actualizar();
    espalda.actualizar( id );
    actualizarJoints();
    
  }
  
  //---------------------------------------- METODOS PRIVADOS
  
  private void actualizarJoints(){ //<>//
    
    if( kinect.isTrackingSkeleton( id ) ){
      for( int i = 0; i < posicionesJoints.length; i++ ){
        
        PVector posicionActual = new PVector();
        confianzasJoints[ i ] = kinect.getJointPositionSkeleton( id, i, posicionActual );
        
        velocidadesJoints[ i ] = PVector.sub( posicionActual, posicionesJoints[ i ] );
        
        if( i == 6 ) System.out.println( velocidadesJoints[ i ].x + " : " + velocidadesJoints[ i ].y );
        
        posicionesJoints[ i ] = posicionActual;
        
      }
    }
    
  }
  
  
}
