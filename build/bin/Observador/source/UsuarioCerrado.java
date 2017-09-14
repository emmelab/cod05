import SimpleOpenNI.*;
import java.util.HashMap;
import java.util.ArrayList;
import processing.core.PVector;

public class UsuarioCerrado {
  SimpleOpenNI context;
  
  int idUsuario;
  int estabilidad;
  int minimoEstabilidad;
  boolean cerradoBruto, cerroBruto, abrioBruto;
  boolean cerrado, cerro, abrio;
  
  public static final int[] jointsExtremidades =
  { SimpleOpenNI.SKEL_LEFT_HAND, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_RIGHT_ELBOW };
  
  private static final int[] jointsCentro =
  { SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_SHOULDER };
  
  
  HashMap<Integer, Float> confianza;
  HashMap<Integer, PVector> joints;
  PVector centroPromedio;

  float umbralCerrado;
  private static float factorUmbral = 1.9f;

  //---------------------------------------------- CONSTRUCTOR

  public UsuarioCerrado( SimpleOpenNI context, int idUsuario, int minimoEstabilidad ) {
    this.context = context;
    this.idUsuario = idUsuario;

    generarJointsUsados();

    this.minimoEstabilidad = minimoEstabilidad;

    centroPromedio = new PVector();
  }

  private void generarJointsUsados() {
    ArrayList<Integer> usados = new ArrayList();
    for (Integer i : jointsExtremidades) {
      usados.add(i);
    }
    for (Integer i : jointsCentro) {
      if (!usados.contains(i)) usados.add(i);
    }

    joints = new HashMap();
    confianza = new HashMap();
    for (Integer i : usados) {
      joints.put(i, new PVector());
      confianza.put(i, 0f);
    }
  }
  
  //----------------------------------------------- METODOS PUBLICOS
  
  //--sets y gets
  public static void setFactorUmbral( float _factorUmbral ){
    factorUmbral = _factorUmbral;
  }
  
  public static float getFactorUmbral(){
    return factorUmbral;
  }
  //--
  
  //-- actualizar
  void actualizar( ) {

    if (context.isTrackingSkeleton(idUsuario)) {
      //actualizo los Joints del usuario
      leerJoints( );

      //actualizo el centro promedio entre su torso y sus hombros
      calcularCentroPromedio();

      //calculo el umbral
      calcularUmbral();

      //actualizo el valor de cerrado y cerrado_valorBruto
      actualizarEstado();
    }
  }

  //----------------------------------------------- METODOS PRIVADOS
  
  private void leerJoints( ) {
    for (Integer i : joints.keySet ()) {
      confianza.put(i, context.getJointPositionSkeleton(idUsuario, i, joints.get(i) ) );
    }    
  }

  private void actualizarEstado() {
    boolean cerradoAnterior = cerrado;
    boolean cerradoBrutoAnterior = cerradoBruto;

    cerradoBruto = true;
    for (Integer i : jointsExtremidades) {
      if (confianza.get(i) > .5) {
        float d = PVector.dist(centroPromedio, joints.get(i) );
        if (d > umbralCerrado) {
          cerradoBruto = false;
          break;
        }
      }
    }
    if (cerradoBruto == cerradoBrutoAnterior) {
      cerroBruto = abrioBruto = false;
      if (estabilidad < minimoEstabilidad) {
        estabilidad++;
      } else {
        cerrado = cerradoBruto;
      }
    } else {
      estabilidad = 0;
      cerroBruto = cerradoBruto;
      abrioBruto = !cerradoBruto;
    }

    if (cerrado == cerradoAnterior) {
      cerro = abrio = false;
    } else {
      cerro = cerrado;
      abrio = !cerrado;
    }

  }

  private void calcularCentroPromedio() {    
    centroPromedio.set(0, 0, 0);
    for (Integer i : jointsCentro) {
      centroPromedio.add( joints.get(i) );
    }
    centroPromedio.div(jointsCentro.length);
  }

  private void calcularUmbral() {
    umbralCerrado = 0;
    for (Integer i : jointsCentro) {
      umbralCerrado += PVector.dist( joints.get(i), centroPromedio);
    }
    umbralCerrado = factorUmbral * umbralCerrado / jointsCentro.length;
  }

}
