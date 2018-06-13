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
  HashMap<String, float> confianzasJoints = new HashMap<String, PVector>();
  
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
  
}
