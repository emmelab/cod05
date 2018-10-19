import SimpleOpenNI.*;
import processing.core.PVector;
import processing.core.PApplet;

class UsuarioNivel {
  SimpleOpenNI context;
  int idUsuario;

  PVector piso, centroMasa, centroMasaCalibrado;

  float umbralBajo, umbralAlto;
  private static float factorUmbralBajo = .47f, factorUmbralAlto = .62f;
  private float factorUmbralBajo_deInstancia, factorUmbralAlto_deInstancia;

  boolean bajoBruto, medioBruto, altoBruto;
  boolean entroBajoBruto, entroMedioBruto, entroAltoBruto;
  boolean salioBajoBruto, salioMedioBruto, salioAltoBruto;

  boolean bajo, medio, alto;
  boolean entroBajo, entroMedio, entroAlto;
  boolean salioBajo, salioMedio, salioAlto;

  int estabilidad, minimoEstabilidad;
  int actual, anterior, estable;
  
  private boolean pisoCalculado;

  public UsuarioNivel(SimpleOpenNI context, int idUsuario, int minimoEstabilidad ) {
    this.context = context;
    this.idUsuario = idUsuario;
    this.minimoEstabilidad = minimoEstabilidad;

    piso = new PVector();
    centroMasa = new PVector();
    centroMasaCalibrado = new PVector();
    
    factorUmbralBajo_deInstancia = factorUmbralBajo;
    factorUmbralAlto_deInstancia = factorUmbralAlto;
  }
  
  //----------------------------------------------- METODOS PUBLICOS
  
  //-- gets y sets
  public static void setFactorUmbralBajo( float _factorUmbralBajo ){
    factorUmbralBajo = PApplet.constrain( _factorUmbralBajo, 0.0f, factorUmbralAlto );
  }
  
  public static void setFactorUmbalAlto( float _factorUmbralAlto ){
    factorUmbralAlto = PApplet.constrain( _factorUmbralAlto, factorUmbralBajo, 1.0f );
  }
  
  public static void setUmbrales( float _factorUmbralBajo, float _factorUmbralAlto ){
    factorUmbralBajo = _factorUmbralBajo;
    factorUmbralAlto = _factorUmbralAlto;
  }
  
  public static float getFactorUmbralBajo(){
    return factorUmbralBajo;
  }
  
  public static float getFactorUmbralAlto(){
    return factorUmbralAlto;
  }
  
  public void setPisoCalculado( boolean pisoCalculado ){
    this.pisoCalculado = pisoCalculado;
  }
  
  public boolean getPisoCalculado(){
    return pisoCalculado;
  }
  //--
  
  public void actualizar() {
    
    comprobarPiso();
    
    if( factorUmbralBajo_deInstancia != factorUmbralBajo )
      recaulcularUmbralBajo();
      
    if( factorUmbralAlto_deInstancia != factorUmbralAlto )
      recalcularUmbralAlto();
    
    context.getCoM(idUsuario, centroMasa);
    float alturaCentro = centroMasa.y - piso.y;

    boolean bBrutAnt = bajoBruto, mBrutAnt = medioBruto, aBrutAnt = altoBruto;
    boolean bAnt = bajo, mAnt = medio, aAnt = alto;

    entroBajo = entroMedio = entroAlto =
      salioBajo = salioMedio = salioAlto =
      entroBajoBruto = entroMedioBruto = entroAltoBruto =
      salioBajoBruto = salioMedioBruto = salioAltoBruto = false;

    if ( centroMasa.y < umbralBajo ) {
      bajoBruto = ! (medioBruto = altoBruto = false);
    } else if (centroMasa.y < umbralAlto) {
      medioBruto = ! (bajoBruto = altoBruto = false);
    } else {
      altoBruto = ! (bajoBruto = medioBruto = false);
    }

    entroBajoBruto = bajoBruto && !bBrutAnt;
    salioBajoBruto = !bajoBruto && bBrutAnt;
    entroMedioBruto = medioBruto && !mBrutAnt;
    salioMedioBruto = !medioBruto && mBrutAnt;
    entroAltoBruto = altoBruto && !aBrutAnt;
    salioAltoBruto = !altoBruto && aBrutAnt;

    if (entroBajoBruto || salioBajoBruto || entroMedioBruto || salioMedioBruto || entroAltoBruto || salioAltoBruto) {
      estabilidad = 0;
    } else {
      if (estabilidad < minimoEstabilidad) {
        estabilidad++;
      } else {
        bajo = bajoBruto;
        medio = medioBruto;
        alto = altoBruto;
      }
    }

    entroBajo = bajo && !bAnt;
    salioBajo = !bajo && bAnt;
    entroMedio = medio && !mAnt;
    salioMedio = !medio && mAnt;
    entroAlto = alto && !aAnt;
    salioAlto = !alto && aAnt;
  }
  
  //----------------------------------------------- METODOS PRIVADOS
  
  private void comprobarPiso(){
    
    if( !pisoCalculado ){
      
      float promedioDeCofianza = 0;
      
      int cantidadTotalDeJoint = 15;//15 son la cantidad de joint (al menos para la Kinect 1)
      
      for( int i = 0; i < cantidadTotalDeJoint; i++ ){
        promedioDeCofianza += context.getJointPositionSkeleton( idUsuario, i, new PVector() );
      }
      
      promedioDeCofianza /= cantidadTotalDeJoint;
      
      if( promedioDeCofianza >= 0.6 ){
        pisoCalculado = calcularPiso( cantidadTotalDeJoint );
      }
      
    }
    
  }
  
  private boolean calcularPiso( int cantidadTotalDeJoint ) {
    if (context.isTrackingSkeleton(idUsuario)) {
      context.getCoM(idUsuario, centroMasaCalibrado);
      piso.set(centroMasaCalibrado);

      // piso.y = el joint mas bajo
      PVector pos = new PVector();
      
      for (int i = 0; i < cantidadTotalDeJoint; i++) {
        context.getJointPositionSkeleton(idUsuario, i, pos);
        if (pos.y < piso.y) piso.y = pos.y;
      }

      umbralAlto = PApplet.lerp(piso.y, centroMasaCalibrado.y, factorUmbralAlto);
      umbralBajo = PApplet.lerp(piso.y, centroMasaCalibrado.y, factorUmbralBajo);
      return true;
    }
    else {
      PApplet.println("No se puede calcular piso hasta tener bien al esqueleto");
      return false;
    }
  }
  
  private void recaulcularUmbralBajo(){
    factorUmbralBajo_deInstancia = factorUmbralBajo;
    umbralBajo = PApplet.lerp(piso.y, centroMasaCalibrado.y, factorUmbralBajo);
  }
  
  private void recalcularUmbralAlto(){
    factorUmbralAlto_deInstancia = factorUmbralAlto;
    umbralAlto = PApplet.lerp(piso.y, centroMasaCalibrado.y, factorUmbralAlto);
  }

}
