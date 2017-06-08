String[] getNombreDeJoint(){
  String[] nombreDeJoint = {
    "CABEZA", "CUELLO", "HOMBRO_IZQUIERDO", "HOMBRO_DERECHO", "CODO_IZQUIERDO", "CODO_DERECHO", "MANO_IZQUIERDA", "MANO_DERECHA", "TORSO",
    "CADERA_IZQUIERDA", "CADERA_DERECHA", "RODILLA_IZQUIERDA", "RODILLA_DERECHA", "PIE_IZQUIERDO", "PIE_DERECHO"
  };
  return nombreDeJoint;
}

/*// VIEJO ORDEN
String[] getNombreDeJoint(){
  String[] nombreDeJoint = {
    "CABEZA", "CUELLO", "TORSO", "HOMBRO_IZQUIERDO", "CODO_IZQUIERDO", "MANO_IZQUIERDA", "HOMBRO_DERECHO", "CODO_DERECHO", "MANO_DERECHA",
    "CADERA_IZQUIERDA", "RODILLA_IZQUIERDA", "PIE_IZQUIERDO", "CADERA_DERECHA", "RODILLA_DERECHA", "PIE_DERECHO"
  };
  return nombreDeJoint;
}
*/

int[] getTiposDeJoint(){
  int[] tiposDeJoint = {
    SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_RIGHT_ELBOW, 
    SimpleOpenNI.SKEL_LEFT_HAND, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_RIGHT_HIP, 
    SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT, SimpleOpenNI.SKEL_RIGHT_FOOT
  };
  return tiposDeJoint;
}

/*// VIEJO ORDEN
int[] getTiposDeJoint(){
  int[] tiposDeJoint = {
    SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND, 
    SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE, 
    SimpleOpenNI.SKEL_LEFT_FOOT, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT
  };
  return tiposDeJoint;
}
*/

int[][] getParesDeJoints(){
  int[][] paresDeJoints = {
    {SimpleOpenNI.SKEL_HEAD,SimpleOpenNI.SKEL_NECK},
    {SimpleOpenNI.SKEL_NECK,SimpleOpenNI.SKEL_TORSO},
    {SimpleOpenNI.SKEL_LEFT_HAND,SimpleOpenNI.SKEL_LEFT_ELBOW},
    {SimpleOpenNI.SKEL_LEFT_ELBOW,SimpleOpenNI.SKEL_LEFT_SHOULDER},
    {SimpleOpenNI.SKEL_LEFT_SHOULDER,SimpleOpenNI.SKEL_NECK},
    {SimpleOpenNI.SKEL_TORSO,SimpleOpenNI.SKEL_LEFT_HIP},
    {SimpleOpenNI.SKEL_LEFT_HIP,SimpleOpenNI.SKEL_LEFT_KNEE},
    {SimpleOpenNI.SKEL_LEFT_KNEE,SimpleOpenNI.SKEL_LEFT_FOOT},
    {SimpleOpenNI.SKEL_RIGHT_HAND,SimpleOpenNI.SKEL_RIGHT_ELBOW},
    {SimpleOpenNI.SKEL_RIGHT_ELBOW,SimpleOpenNI.SKEL_RIGHT_SHOULDER},
    {SimpleOpenNI.SKEL_RIGHT_SHOULDER,SimpleOpenNI.SKEL_NECK},
    {SimpleOpenNI.SKEL_TORSO,SimpleOpenNI.SKEL_RIGHT_HIP},
    {SimpleOpenNI.SKEL_RIGHT_HIP,SimpleOpenNI.SKEL_RIGHT_KNEE},
    {SimpleOpenNI.SKEL_RIGHT_KNEE,SimpleOpenNI.SKEL_RIGHT_FOOT},
  };
  return paresDeJoints;
}
