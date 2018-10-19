enum EstadoDesequilibrio{ NULO, IZQUIERDA, CENTRO, DERECHA };
enum EstadoNivel{ NULO, ARRIBA, MEDIO, ABAJO };
enum EstadoApertura{ NULO, ABIERTO, INTERMEDIO, CERRADO };

class Posturas{
  
  Movimiento movimiento;
  Desequilibrio desequilibrio;
  Nivel nivel;
  Apertura apertura;
  
  Posturas( Movimiento movimiento ){
    this.movimiento = movimiento;
    desequilibrio = new Desequilibrio();
    nivel = new Nivel();
    apertura = new Apertura();
  }
  
  void ejecutar(){
    desequilibrio.analizar();
    nivel.analizar();
    apertura.analizar();
  }
  
  //Clase interna Desequilibrio
  class Desequilibrio{
  
    EstadoDesequilibrio estado = EstadoDesequilibrio.NULO, estadoPrevio = estado;
    float umbralDesequilibrio = 10;
    
    //banderas
    boolean salioDerecha;
    boolean salioIzquierda;
    
    void analizar(){
      
      if( !movimiento.hayMovimiento ){
        estado = EstadoDesequilibrio.NULO;
        return;
      }
      
      float diferencia = movimiento.getXBoundingBox() - movimiento.getX();
      if( abs( diferencia ) > umbralDesequilibrio ){
        estado = diferencia > 0? EstadoDesequilibrio.IZQUIERDA : EstadoDesequilibrio.DERECHA;
      }else estado = EstadoDesequilibrio.CENTRO;
      
      actualizarBanderas();
    }
    
    void actualizarBanderas() {
      salioDerecha = salioIzquierda = false;
  
      if ( estadoPrevio != estado && estado == EstadoDesequilibrio.CENTRO ) {    
        if ( estadoPrevio == EstadoDesequilibrio.DERECHA ) {        
          salioDerecha = true;
        }
        if ( estadoPrevio == EstadoDesequilibrio.IZQUIERDA ) {       
          salioIzquierda = true;
        }
      }
      estadoPrevio = estado;
    }
    
  }
  
  //Clase interna Nivel
  class Nivel{
    
    EstadoNivel estado = EstadoNivel.NULO, estadoPrevio = estado;
    float rangoInferior = 0.45;
    float rangoSuperior = 0.55;
    
    //banderas
    boolean entroAlto;
    boolean entroMedio;
    boolean entroBajo;
    
    void analizar(){
      
      if( !movimiento.hayMovimiento ){
        estado = EstadoNivel.NULO;
        return;
      }
      
      float alto = movimiento.getAbajo() - movimiento.getArriba();
      if( alto > movimiento.alto * rangoSuperior )
        estado = EstadoNivel.ARRIBA;
      else if( alto > movimiento.alto * rangoInferior )
        estado = EstadoNivel.MEDIO;
      else
        estado = EstadoNivel.ABAJO;
      
      actualizarBanderas();
      
    }
    
    void actualizarBanderas(){
      entroAlto = entroMedio = entroBajo = false;
      
      if (estadoPrevio != estado) {
        if ( estadoPrevio == EstadoNivel.ARRIBA ) {
          if ( estado == EstadoNivel.MEDIO ) {
            entroMedio = true;
          } else if ( estado == EstadoNivel.ABAJO ) {
            entroBajo = true;
          }
        }
        if ( estadoPrevio == EstadoNivel.MEDIO ) {
          if ( estado == EstadoNivel.ARRIBA ) {
            entroAlto = true;
          } else if ( estado == EstadoNivel.ABAJO ) {
            entroBajo = true;
          }
        }
        if ( estadoPrevio == EstadoNivel.ABAJO ) {
          if ( estado == EstadoNivel.ARRIBA ) {
            entroAlto = true;
          } else if ( estado == EstadoNivel.MEDIO ) {
            entroMedio = true;
          }
        }
        estadoPrevio = estado;
      }
    }
    
  }
  
  //Clase interna Apertura
  class Apertura{
    
    EstadoApertura estado = EstadoApertura.NULO, estadoPrevio = estado;
    float rangoInferior = 0.45;
    float rangoSuperior = 0.55;
    
    //banderas
    boolean cerrado, cerro, abrio;
    
    void analizar(){
      
      if( !movimiento.hayMovimiento ){
        estado = EstadoApertura.NULO;
        return;
      }
      
      float apertura = movimiento.getDerecha() - movimiento.getIzquierda();
      if( apertura > movimiento.ancho * rangoSuperior )
        estado = EstadoApertura.ABIERTO;
      else if( apertura > movimiento.ancho * rangoInferior )
        estado = EstadoApertura.INTERMEDIO;
      else
        estado = EstadoApertura.CERRADO;
      
      actualizarBanderas();
      
    }
    
    void actualizarBanderas(){
      cerrado = estado == EstadoApertura.CERRADO;
      cerro = abrio = false;
      if ( estadoPrevio != estado ) {
        if ( estadoPrevio == EstadoApertura.ABIERTO ) {        
          cerro = true;
        } else if ( estadoPrevio == EstadoApertura.CERRADO ) {
          abrio = true;
        }
      }
      estadoPrevio = estado;
    }
    
  }
  
}
