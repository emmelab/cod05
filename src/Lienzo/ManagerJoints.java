import java.util.HashMap;
import java.util.Map;
import processing.core.PVector;
import processing.core.PApplet;

class ManagerJoints{
  
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
  
  HashMap<String, PVector> posicionesJoints = new HashMap<String, PVector>();
  HashMap<String, PVector> velocidadesJoints = new HashMap<String, PVector>();
  HashMap<String, Float> confianzasJoints = new HashMap<String, Float>();
  
  ManagerJoints(){
    
  }
  
  void setJoint( String nombre_joint, float x, float y, float confianza ){
    
    PVector nuevaPosicion = new PVector( x, y );
    PVector velocidad = posicionesJoints.get( nombre_joint ) != null ? PVector.sub( posicionesJoints.get( nombre_joint ), nuevaPosicion ) : new PVector( 0, 0 );
    
    posicionesJoints.put( nombre_joint, nuevaPosicion );
    velocidadesJoints.put( nombre_joint, velocidad );
    confianzasJoints.put( nombre_joint, confianza );
    
  }
  
  PVector getPosicionJoint( String nombre_joint ){
    return posicionesJoints.get( nombre_joint );
  }
  
  PVector getVelocidadJoint( String nombre_joint ){
    return velocidadesJoints.get( nombre_joint );
  }
  
  float getConfianzaJoint( String nombre_joint ){
    return confianzasJoints.get( nombre_joint );
  }
  
  void debug(PApplet p5){
      
      p5.pushStyle();
        p5.noStroke();
        
        for( Map.Entry p : posicionesJoints.entrySet() ){
          
          PVector posicion = (PVector) p.getValue();
          float confianza = confianzasJoints.get( p.getKey() );
          
          if( confianza < .2f ) p5.fill( 123, 0, 169 );
          else if( confianza < .6f ) p5.fill( 0, 89, 255 );
          else if( confianza < 1.0f ) p5.fill( 0, 163, 162 );
          else p5.fill( 1, 214, 62 );
          
          p5.ellipse( posicion.x * p5.width, posicion.y * p5.height, 30, 30 );
          
        }
        
      p5.popStyle();
      
    }
  
}
