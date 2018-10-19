import processing.video.*;

class Motor{
  
  String estado;
  static final String
  ESTADO_COMPROBANDO_CAMARAS = "Comprobando camaras",
  ESTADO_CAMARAS_NO_COMPATIBLES = "Camaras no compatibles",
  ESTADO_INICIAR_GUI_PESTANAS = "Iniciar GUI Pestanas",
  ESTADO_INICIAR_CAMARA = "Iniciar Camara",
  ESTADO_CORRIENDO = "Corriendo";
  
  DetallesCamara[] camarasAptas;
  DetallesCamara camaraSeleccionada;
  Capture camara;
  static final int ANCHO_CAMARA = 320, ALTO_CAMARA = 240, FPS_CAMARA = 30;
  
  Movimiento movimiento;
  Posturas posturas;
  ComunicacionOSC osc;
  
  Motor(){
    osc = new ComunicacionOSC( p5 );
    estado = ESTADO_COMPROBANDO_CAMARAS;
    //println( Capture.list() );
    thread( "reconocerCamaras" );
  }
  
  String[] getNombresCamarasAptas(){
    String[] nombres = new String[ camarasAptas.length ];
    for( int i = 0; i < camarasAptas.length; i++ )
      nombres[ i ] = camarasAptas[ i ].nombre;
    return nombres;
  }
  
  void ejecutar(){
    
    switch( estado ){
      case ESTADO_COMPROBANDO_CAMARAS:
        break;
      case ESTADO_INICIAR_GUI_PESTANAS:
        iniciarGuiPestanas();
        break;
      case ESTADO_INICIAR_CAMARA:
        iniciarCamara();
        break;
      case ESTADO_CORRIENDO:
        corriendo();
        break;
      default:
        break;
    }
    
  }
  
  void iniciarGuiPestanas(){
    try{
      gui.iniciarPestanas();
      estado = ESTADO_INICIAR_CAMARA;
    }catch( Exception e ){
      consola.printlnError( "Exception iniciar gui pestanas: " + e.getMessage() );
    }
  }
  
  void iniciarCamara(){
    if( camara != null ){
      camara.stop();
      camara = null;
    }
    if( camaraSeleccionada != null ){
      camara = new Capture( p5, camaraSeleccionada.ancho, camaraSeleccionada.alto, camaraSeleccionada.nombre, camaraSeleccionada.fps );
      camara.start();

      movimiento = new Movimiento( camaraSeleccionada.ancho, camaraSeleccionada.alto, 100, 5, true );
      posturas = new Posturas( movimiento );
     
    }
    estado = ESTADO_CORRIENDO;
  }
  
  void corriendo(){
    if( camara == null ){
      consola.printlnError( "camara null en corriendo" );
      return;
    }
    if( camara.available() ){
      camara.read();
      movimiento.capturar( camara );
      posturas.ejecutar();
      osc.enviarMensajesAPI( posturas );
    }
  }
  
}

int iReco, iRecoMax=1;
void reconocerCamaras(){
  String[] listaCamaras = Capture.list();
  iRecoMax = listaCamaras.length;
  DetallesCamara[] todasLasCamaras = new DetallesCamara[ iRecoMax ];
  IntList idCamarasAptas = new IntList();
  for( iReco = 0; iReco < iRecoMax; iReco++ ){
    try{
      todasLasCamaras[ iReco ] = new DetallesCamara( listaCamaras[ iReco ] );
      if( todasLasCamaras[iReco].ancho == Motor.ANCHO_CAMARA &&
          todasLasCamaras[iReco].alto == Motor.ALTO_CAMARA &&
          todasLasCamaras[iReco].fps == Motor.FPS_CAMARA
      ){
        idCamarasAptas.append( iReco );
      }
    }catch( Exception e ){
      println( "Exception 'reconocerCamaras()': " + e.getMessage() );
    }
  }
  
  if( idCamarasAptas.size() > 0 ){
    motor.camarasAptas = new DetallesCamara[ idCamarasAptas.size() ];
    for( int i = 0; i < motor.camarasAptas.length; i++ ){
      motor.camarasAptas[ i ] = todasLasCamaras[ idCamarasAptas.get( i ) ];
    }
    motor.camaraSeleccionada = todasLasCamaras[ idCamarasAptas.get( 0 ) ];
    motor.estado = Motor.ESTADO_INICIAR_GUI_PESTANAS;
  }else{
    motor.estado = Motor.ESTADO_CAMARAS_NO_COMPATIBLES;
  }
  
}

class DetallesCamara{
  
  String nombre;
  int ancho, alto;
  int fps;
  
  DetallesCamara( String info ){
    nombre = tomarValor( "name=", info );
    String tamano = tomarValor( "size=", info );
    int separador = tamano.indexOf( "x" );
    if( separador != -1 ){
      ancho = Integer.parseInt( tamano.substring( 0, separador ) );
      alto = Integer.parseInt( tamano.substring( separador + 1 ) );
    }
    fps = Integer.parseInt( tomarValor( "fps=", info ) ); 
  }
  
  String tomarValor( String variable, String datos ){
    String valor = "";
    if( datos.indexOf( variable ) != -1 ){
      int desde = datos.indexOf( variable ) + variable.length();
      int hasta = datos.indexOf( ",", desde );
      valor = hasta != -1 ? datos.substring( desde, hasta ) : datos.substring( desde );
    }
    return valor;
  }
  
}
