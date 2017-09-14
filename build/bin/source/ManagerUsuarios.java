import java.util.HashMap;
import processing.core.PVector;
import processing.core.PApplet;

public class ManagerUsuarios{
  
  public static final String
  CABEZA = "CABEZA",
  CUELLO = "CUELLO",
  HOMBRO_IZQUIERDO = "HOMBRO_IZQUIERDO",
  HOMBRO_DERECHO = "HOMBRO_DERECHO",
  CODO_IZQUIERDO = "CODO_IZQUIERDO",
  CODO_DERECHO = "CODO_DERECHO",
  MANO_IZQUIERDA = "MANO_IZQUIERDA",
  MANO_DERECHA = "MANO_DERECHA",
  TORSO = "TORSO",
  CADERA_IZQUIERDA = "CADERA_IZQUIERDA",
  CADERA_DERECHA = "CADERA_DERECHA",
  RODILLA_IZQUIERDA = "RODILLA_IZQUIERDA",
  RODILLA_DERECHA = "RODILLA_DERECHA",
  PIE_IZQUIERDO = "PIE_IZQUIERDO",
  PIE_DERECHO = "PIE_DERECHO";
  
  private HashMap<Integer, Usuario> usuarios = new HashMap<Integer, Usuario>();
  
  public ManagerUsuarios(){
    
  }
  
  //---------------------------------------- METODOS PUBLICOS
  
  //---- seters y geters
  
  public HashMap<Integer, Usuario> getUsuarios(){
    return usuarios;
  }
  
  public PVector[] getJoints( String nombre_joint ){
    PVector[] joints = new PVector[ usuarios.size() ];
    int i = 0;
    for( Usuario u : usuarios.values() ){
      joints[ i ] = u.getPosicionJoint( nombre_joint );
      i++;
    }
    return joints;
  }
  
  public float[] getConfianzasJoints( String nombre_joint ){
    float[] confianzas = new float[ usuarios.size() ];
    int i = 0;
    for( Usuario u : usuarios.values() ){
      confianzas[ i ] = u.getConfianzaJoint( nombre_joint );
      i++;
    }
    return confianzas;
  }
  
  public void setUsuarioJoint( int keyUsuario, String nombre_joint, float x, float y, float confianza ){
    if( usuarios.containsKey( keyUsuario ) ){
      
      Usuario usuario = usuarios.get( keyUsuario );
      
      PVector nuevaPosicion = new PVector( x, y );
      //PVector velocidad = PVector.sub( usuario.getPosicionJoint( nombre_joint ), nuevaPosicion );
      PVector velocidad = new PVector( 0, 0 );
      
      usuario.setPosicionJoint( nombre_joint, nuevaPosicion );
      usuario.setVelocidadJoint( nombre_joint, velocidad );
      usuario.setConfianzaJoint( nombre_joint, confianza );
      
    }else{
      
      Usuario nuevo = new Usuario();
      nuevo.setPosicionJoint( nombre_joint, new PVector( x, y ) );
      nuevo.setVelocidadJoint( nombre_joint, new PVector( 0, 0 ) );
      nuevo.setConfianzaJoint( nombre_joint, confianza );
      usuarios.put( keyUsuario, nuevo );
      
      
    }
    //PApplet.println( "nombre_joint: " + nombre_joint );
  }
  
  public void removerUsuario( int keyUsuario ){
    usuarios.get( keyUsuario ).setEliminarUsuario( true );
  }
  
  public void actualizar(){
    for( Integer key : usuarios.keySet() ){
      if( usuarios.get( key ).getEliminarUsuario() ){
        usuarios.remove( key );
      }
    }
  }
  
  public void debug(PApplet p5){
    p5.pushStyle();
      //p5.fill( 255 );
      for( Usuario u : usuarios.values() )
        if( !u.getEliminarUsuario() ) u.debug( p5 );
    p5.popStyle();
  }
  
  //************************************************** CLASE INTERNA

  public class Usuario{
  
    private boolean eliminarUsuario;
    private HashMap<String, PVector> posicionesJoints = new HashMap<String, PVector>();
    private HashMap<String, PVector> velocidadesJoints = new HashMap<String, PVector>();
    private HashMap<String, Float> confianzasJoints = new HashMap<String, Float>();
    
    public Usuario(){
      
    }
    
    //---------------------------------------- METODOS PUBLICOS
  
    //---- seters y geters
    
    public void setEliminarUsuario( boolean eliminarUsuario ){
      this.eliminarUsuario = eliminarUsuario;
    }
    
    public boolean getEliminarUsuario(){
      return eliminarUsuario;
    }
    
    public void setPosicionJoint( String nombre_joint, PVector valor ){
      posicionesJoints.put( nombre_joint, valor );
    }
    
    public PVector getPosicionJoint( String nombre_joint ){
      return posicionesJoints.get( nombre_joint );
    }
    
    public void setVelocidadJoint( String nombre_joint, PVector valor ){
      velocidadesJoints.put( nombre_joint, valor );
    }
    
    public PVector getVelocidadJoint( String nombre_joint ){
      return velocidadesJoints.get( nombre_joint );
    }
    
    public void setConfianzaJoint( String nombre_joint, float valor ){
      confianzasJoints.put( nombre_joint, valor );
    }
    
    public float getConfianzaJoint( String nombre_joint ){
      return confianzasJoints.get( nombre_joint );
    }
    //----
    
    public void debug(PApplet p5){
      
      /*
      for( PVector v : posicionesJoints.values() )
        p5.ellipse( v.x * p5.width, v.y * p5.height, 10, 10 );
      */
      
      for( String key : posicionesJoints.keySet() ){

        float confianza = confianzasJoints.get( key );
        PVector posicion = posicionesJoints.get( key );
        PVector velocidad = velocidadesJoints.get( key );
        //PVector posicionAnterior = PVector.sub( posicion, velocidad );
        
        p5.stroke( 255, 0, 0 );
        //p5.line( posicionAnterior.x * p5.width, posicionAnterior.y * p5.height, posicion.x * p5.width, posicion.y * p5.height );
        //p5.line( posicion.x * p5.width - velocidad.x * p5.width, posicion.y * p5.height - velocidad.y * p5.height, posicion.x * p5.width, posicion.y * p5.height );
        //p5.line( posicionAnterior.x * p5.width + posicion.x * p5.width, posicionAnterior.y * p5.height + posicion.y * p5.height, posicion.x * p5.width, posicion.y * p5.height );
        /*
        p5.fill( 255 );
        p5.textSize( 30 );
        p5.text( "PA: " + posicionAnterior.x * p5.width + " : " + posicionAnterior.y * p5.height, posicion.x * p5.width + 80, posicion.y * p5.height );
        */
        p5.noStroke();
        
        if( confianza < .2f ) p5.fill( 123, 0, 169 );
        else if( confianza < .6f ) p5.fill( 0, 89, 255 );
        else if( confianza < 1.0f ) p5.fill( 0, 163, 162 );
        else p5.fill( 1, 214, 62 );
        
        /*
        if( confianza < .2f ) p5.fill( #58BDE0 );
        else if( confianza < .6f ) p5.fill( #58E0CD );
        else if( confianza < 1.0f ) p5.fill( #58E07D );
        else p5.fill( #61E058 );
        */
        
        p5.ellipse( posicion.x * p5.width, posicion.y * p5.height, 30, 30 );
        
      }
      
    }
    
  }
  
}