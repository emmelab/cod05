import processing.core.PApplet;
import SimpleOpenNI.*;
import processing.core.PVector;

public class UsuarioDesequilibrio {
  SimpleOpenNI  context;
  int idUsuario;

  int estabilidad, minimoEstabilidad;

  PVector centroMasa2D, centroMasa, centroCaja; //centro del boundingbox  
  private static float umbralMenor = 20, umbralMaximo = 35;
  public static final int MAXIMO_VALOR_UMBRAL = 100;
  int[] userMap;

  float[] memoriaDesequilibrio;
  int punteroMemoriaDesequilibrio;
  float desequilibrioBruto,desequilibrio;
  boolean izquierda, derecha;
  boolean izquierdaBruto, derechaBruto;
  boolean entroIzquierda, salioIzquierda, entroDerecha, salioDerecha;
  boolean entroIzquierdaBruto, salioIzquierdaBruto, entroDerechaBruto, salioDerechaBruto;

  public UsuarioDesequilibrio(SimpleOpenNI context, int idUsuario, int minimoEstabilidad ) {
    this.context = context;
    this.idUsuario = idUsuario;
    this.minimoEstabilidad = minimoEstabilidad;
    memoriaDesequilibrio = new float[minimoEstabilidad];

    centroMasa2D = new PVector();
    centroMasa = new PVector();
    centroCaja = new PVector();
  }
  
  //-------------------------------------------------- METODOS PUBLICOS

  //--sets y gets
  public static void setUmbralMenor( float _umbralMenor ){
    umbralMenor = PApplet.constrain( _umbralMenor, 0.0f, umbralMaximo );
  }
  
  public static void setUmbralMaximo( float _umbralMaximo ){
    umbralMaximo = PApplet.constrain( _umbralMaximo, umbralMenor, MAXIMO_VALOR_UMBRAL );
  }
  
  public static float getUmbralMenor(){
    return umbralMenor;
  }
  
  public static float getUmbralMaximo(){
    return umbralMaximo;
  }
  //--

  public void actualizar() {
    if (context.getCoM(idUsuario, centroMasa)) {
      context.convertRealWorldToProjective(centroMasa, centroMasa2D);
      actualizarCentroCaja();
      
      actualizarValor();
      actualizarBooleanas();
    }
  }
  
  //-------------------------------------------------- METODOS PRIVADOS

  private void actualizarValor(){
    desequilibrioBruto = (centroCaja.x - centroMasa2D.x) / umbralMaximo;
    
    memoriaDesequilibrio[punteroMemoriaDesequilibrio] = desequilibrioBruto;
    punteroMemoriaDesequilibrio++;
    if (punteroMemoriaDesequilibrio >= memoriaDesequilibrio.length) punteroMemoriaDesequilibrio %= memoriaDesequilibrio.length;
    
    desequilibrio = 0;
    for(Float f : memoriaDesequilibrio){
      desequilibrio += f;
    }
    desequilibrio /= memoriaDesequilibrio.length;
  }
  private void actualizarBooleanas() {
    
    boolean izqAnt = izquierda;
    boolean derAnt = derecha;
    boolean izqBrutAnt = izquierdaBruto;
    boolean derBrutAnt = derechaBruto;

    if (centroCaja.x < centroMasa2D.x - umbralMenor) {
      izquierdaBruto = true;
      derechaBruto = false;
    } else if (centroCaja.x > centroMasa2D.x + umbralMenor) {
      izquierdaBruto = false;
      derechaBruto = true;
    } else {
      izquierdaBruto = derechaBruto = false;
    }

    entroIzquierdaBruto = izquierdaBruto && !izqBrutAnt;
    salioIzquierdaBruto = !izquierdaBruto && izqBrutAnt;
    entroDerechaBruto = derechaBruto && !derBrutAnt;
    salioDerechaBruto = !derechaBruto && derBrutAnt;

    if (entroIzquierdaBruto || salioIzquierdaBruto || entroDerechaBruto || salioDerechaBruto) {
      estabilidad = 0;
    } else {
      if (estabilidad < minimoEstabilidad) {
        estabilidad++;
      } else {
        izquierda = izquierdaBruto;
        derecha = derechaBruto;
      }
    }

    entroIzquierda = izquierda && !izqAnt;
    salioIzquierda = !izquierda && izqAnt;
    entroDerecha = derecha && !derAnt;
    salioDerecha = !derecha && derAnt;
  }

  private void actualizarCentroCaja() {
    if (userMap == null) userMap = context.userMap();
    else context.userMap(userMap);
    
    int ancho = context.depthWidth();
    int alto = context.depthHeight();
    int minX = ancho;
    int minY = alto;
    int maxX = 0;
    int maxY = 0;
    for (int i=0; i<userMap.length; i++) {
      if (userMap[i] == idUsuario) {
        int x = i%ancho;
        int y = i/ancho;
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;
        if (y < minY) minY = y;
        if (y > maxY) maxY = y;
      }
    }
    centroCaja.set( (maxX+minX)/2, (maxY+minY)/2, 0);
  }

}
