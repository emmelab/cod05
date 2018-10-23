class Movimiento extends PMoCap{
  
  boolean amortiguacionActivada;
  float factorAmortiguacion = 0.2;
  float amor_x, amor_y, amor_yBoundingBox, amor_xBoundingBox;
  int amor_arriba, amor_abajo, amor_izquierda, amor_derecha;
  
  boolean autocaptura;
  int umbralAutocaptura, tiempoAutocaptura, tiempoTolenranciaAutocaptura = 2000;
  final int UMBRAL_MAXIMO_AUTOCAPTURA;
  
  Movimiento( int ancho, int alto, float umbral, int fotogramasRetardo, boolean comparaConFondo ){
    super( ancho, alto, umbral, fotogramasRetardo, comparaConFondo );
    UMBRAL_MAXIMO_AUTOCAPTURA = round((ancho*alto)*0.25);
    umbralAutocaptura = round(UMBRAL_MAXIMO_AUTOCAPTURA*0.1);
  }
  
  void setSensibilidadAutocaptura( float sensibilidad ){
    sensibilidad = constrain( sensibilidad, 0.0, 1.0 );
    umbralAutocaptura = round(UMBRAL_MAXIMO_AUTOCAPTURA*sensibilidad);
  }
  
  void capturar( PImage entrada ){
    super.capturar( entrada );
    if( amortiguacionActivada ) actualizarAmortiguaciones();
    if( autocaptura ) autocaptura();
  }
  
  void actualizarAmortiguaciones(){
    amor_x = amortiguar( x, amor_x );
    amor_y = amortiguar( y, amor_y );
    
    amor_xBoundingBox = amortiguar( xBoundingBox, amor_xBoundingBox );
    amor_yBoundingBox = amortiguar( yBoundingBox, amor_yBoundingBox );
    
    amor_arriba = amortiguar( arriba, amor_arriba );
    amor_abajo = amortiguar( abajo, amor_abajo );
    amor_izquierda = amortiguar( izquierda, amor_izquierda );
    amor_derecha = amortiguar( derecha, amor_derecha );
  }
  
  float amortiguar( float valorCrudo, float valorFiltrado ){
    return valorCrudo * factorAmortiguacion + valorFiltrado * ( 1.0 - factorAmortiguacion );
  }
  
  int amortiguar( int valorCrudo, int valorFiltrado ){
    return round( valorCrudo * factorAmortiguacion + valorFiltrado * ( 1.0 - factorAmortiguacion ) );
  }
  
  void autocaptura(){
    if( hayMovimiento && area < umbralAutocaptura ){
      tiempoAutocaptura += reloj.getDeltaMillis();
      if( tiempoAutocaptura > tiempoTolenranciaAutocaptura ){
        recapturarFondo();
        consola.printlnAlerta("Autocaptura!!");
        tiempoAutocaptura = 0;
      }
    }else
      tiempoAutocaptura = 0;
  }
  
  //Metodos gets
  
  PImage getImagenAnalisis() {
    
    if( amortiguacionActivada && hayMovimiento ){
      PGraphics grafico = createGraphics( ancho, alto, P2D );
      PImage resultado = super.getImagenAnalisis();
      //se inicia el dibujo del grafico
      grafico.beginDraw();
      grafico.image( resultado, 0, 0 );
      //se dibujan el centro y el borde del area de movimiento
      grafico.stroke( paleta.amarillo );
      grafico.noFill();
      grafico.ellipse( amor_x, amor_y, 5, 5);
      grafico.rect( amor_xBoundingBox, amor_yBoundingBox, 5, 5 );
      grafico.rectMode( CORNERS );
      grafico.rect( amor_izquierda, amor_arriba, amor_derecha, amor_abajo );
      grafico.endDraw();
      //se copia el grafico a la imagen
      resultado.copy( grafico, 0, 0, grafico.width, grafico.height, 0, 0, resultado.width, resultado.height );
      return resultado;
    }else
      return super.getImagenAnalisis();
  }
  
  float getX(){
    return amortiguacionActivada? amor_x : x;
  }
  
  float getY(){
    return amortiguacionActivada? amor_y : y;
  }
  
  float getXBoundingBox(){
    return amortiguacionActivada? amor_xBoundingBox : xBoundingBox;
  }
  
  float getYBoundingBox(){
    return amortiguacionActivada? amor_yBoundingBox : yBoundingBox;
  }
  
  int getArriba(){
    return amortiguacionActivada? amor_arriba : arriba;
  }
  
  int getAbajo(){
    return amortiguacionActivada? amor_abajo : abajo;
  }
  
  int getIzquierda(){
    return amortiguacionActivada? amor_izquierda : izquierda;
  }
  
  int getDerecha(){
    return amortiguacionActivada? amor_derecha : derecha;
  }
  //end metodos gets
  
}
