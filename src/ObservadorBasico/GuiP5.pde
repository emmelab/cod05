import controlP5.*;

class GuiP5 extends ControlP5{
  
  final String titulo = "CONFIGURADOR DEL OBSERVADOR WEBCAM";
  final PImage logoCOD05, logoObservador;
  final PFont fuentePequena, fuenteGrande;
  
  String pestanaActiva;
  static final String
  PESTANA_CAMARA = "Cámara",
  PESTANA_CAPTURA = "Captura",
  PESTANA_POSTURAS = "Posturas";
  
  ScrollableList listaDeCamaras;
  
  GuiP5( PApplet p5 ){
    super( p5 );
    addFrameRate().setInterval(5).setPosition(0,height - 15).moveTo( "global" );
    
    logoCOD05 = loadImage( "logoCOD05.png" );
    logoObservador = loadImage( "observador.png" );
    
    fuentePequena = loadFont( "MyriadPro-Regular-14.vlw" );
    fuenteGrande = loadFont( "MyriadPro-Regular-48.vlw" );
    setFont( fuentePequena );
    textFont( fuentePequena );
    setColorCaptionLabel( paleta.blanco );
    
    controlWindow.setPositionOfTabs( (int) controlWindow.getPositionOfTabs()[ 0 ], 42 );

  }
  
  void iniciarPestanas(){
    String[] pestanas = { PESTANA_CAMARA, PESTANA_CAPTURA, PESTANA_POSTURAS };
    pestanaActiva = PESTANA_CAMARA;
    //------------ PESTANAS
    for( int i = 1; i < pestanas.length; i++ ){
      addTab( pestanas[ i ] )
      .activateEvent( true )
      .setId( i )
      .setWidth( (width / pestanas.length) - 5 )
      .setHeight( 42 )
      .getCaptionLabel().alignX( ControlP5.CENTER )
      ;
      
      ControllerGroup<Tab> grupo = ( ControllerGroup<Tab> ) getTab( pestanas[ i ] );
      grupo.setColorBackground( paleta.grisClaro );
      grupo.setColorActive( paleta.amarillo );
      grupo.setColorForeground( paleta.negro ); 
      
    }
    
    Tab t = getTab("default");
    t.setLabel( pestanas[ 0 ] );
    t.activateEvent( true );
    t.setId( 0 );
    t.setWidth( width / pestanas.length );
    t.setHeight( 42 );
    t.getCaptionLabel().alignX( ControlP5.CENTER );
    
    ControllerGroup<Tab> grupo = ( ControllerGroup<Tab> ) getTab("default");
    grupo.setColorBackground( paleta.grisClaro );
    grupo.setColorActive( paleta.amarillo );
    grupo.setColorForeground( paleta.negro ); 
    grupo.getCaptionLabel().setColor( paleta.negro );
    //------------
    iniciarContenidoPestanas();
  }
  
  void iniciarContenidoPestanas(){
    
    //Global
    addButton( "nuevoFondo" )
    //primero que nada desactivo el "desencadenamiento de enventos"
    .setBroadcast(false)
    .setLabel( "Capturar fondo" )
    .setWidth( 125 )
    .setHeight( 30 )
    .setPosition( width * 0.825, 540 )
    //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
    .setBroadcast(true)
    .moveTo( "global" );
    
    //PESTANA 0 - DEFAULT CAMARA
    listaDeCamaras = addScrollableList("SeleccionarCamara")
      .setBroadcast(false)
     .setPosition(width*0.05, height*0.35)
     .setSize(150, 200)
     .setBarHeight(40)
     .setItemHeight(30)
     .addItems( motor.getNombresCamarasAptas() )
     .setLabel( "Seleccionar camara" )
     .setValue( 0 )
     //.setName( "listaSeleccionarPuerto" )
     // .setType(ScrollableList.LIST) // currently supported DROPDOWN and LIST
     .setBroadcast(true)
     ;
     
     //PESTANA 1 - CAPTURA
     float posX = width * 0.825;
     int posY = 140, saltoY = 80, saltoCorto = 50;
     crearSlider(  "sliderUmbral", "Umbral", PESTANA_CAPTURA, posX, posY, 130, 20, 0, 255, 100 );
     crearToggle( "toggleActivarAmortiguacion", "Amortiguar", PESTANA_CAPTURA, posX, posY += saltoY, 30, 20 );
     Slider sAmor = crearSlider(  "sliderAmortiguacion", "Factor amortiguador", PESTANA_CAPTURA, posX, posY += saltoCorto, 130, 20, 0.0, 1.0, 0.2 );
     sAmor.setVisible( false );
     crearToggle( "toggleAutocaptura", "Autocaptura", PESTANA_CAPTURA, posX, posY += saltoY, 30, 20 );
     Slider sAuto = crearSlider(  "sliderSensibilidadAutocaptura", "Sensibilidad autocap.", PESTANA_CAPTURA, posX, posY += saltoCorto, 130, 20, 0.0, 1.0, 0.1 );
     sAuto.setVisible( false );
     Slider sTAuto = crearSlider(  "sliderTiempoAutocaptura", "Tiempo autocaptura", PESTANA_CAPTURA, posX, posY += saltoCorto, 130, 20, 0, 3000, 2000 );
     sTAuto.setVisible( false );
     
     //PESTANA 2 - POSTURAS
     posX = width * 0.6;
     /*int*/ posY = 140;/*,*/ saltoY = 100;
     crearSlider(  "sliderUmbralDesequilibrio", "Umbral desequilibrio", PESTANA_POSTURAS, posX, posY, 200, 20, 0, 80, 10 );
     crearRango( "rangoNivel", "Umbrales de nivel", PESTANA_POSTURAS, posX, posY += saltoY, 200, 20, 0.0, 1.0, 0.45, 0.55 );
     crearRango( "rangoApertura", "Umbrales de apertura", PESTANA_POSTURAS, posX, posY += saltoY, 200, 20, 0.0, 1.0, 0.45, 0.55 );
   
  }
  
  Slider crearSlider( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto, float minimo, float maximo, float valor ){
    Slider s = addSlider( nombre )
    .setBroadcast(false)
    .setLabel( etiqueta )
    .setSize( round(ancho), round(alto) )
    .setPosition( x, y )
    .setRange( minimo, maximo )
    .setValue( valor/*motor.movimiento.!!variable correspondiente!!*/ )
    .setSliderMode(Slider.FLEXIBLE)
    .setBroadcast(true)
    .moveTo( pestana )
    ;
    //gui.getController(nombre).getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
    s.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
    return s;
  }
  
  void crearRango( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto, float minimo, float maximo, float valorMinimo, float valorMaximo ){
    Range r = addRange( nombre )
   // disable broadcasting since setRange and setRangeValues will trigger an event
   .setBroadcast(false) 
   .setLabel( etiqueta )
   .setPosition(x,y)
   .setSize(round(ancho),round(alto))
   .setHandleSize(20)
   .setRange(minimo,maximo)
   .setRangeValues(valorMinimo,valorMaximo)
   // after the initialization we turn broadcast back on again
   .setBroadcast(true)
   //.setColorForeground(color(255,40))
   //.setColorBackground(color(255,40))  
   .moveTo( pestana )
   ;
   
   r.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
   
  }
  
  void crearToggle( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto ){
    Toggle t = addToggle( nombre )
    .setBroadcast(false) 
    .setLabel( etiqueta )
    .setPosition( x, y )
    .setSize( round(ancho), round(alto) )
    .setBroadcast(true)
    .moveTo( pestana );
    t.getCaptionLabel().toUpperCase( false );
  }
  
  //seters y geters
  public String getPestanaActiva(){
    ControllerList listaPestanas = controlWindow.getTabs();
    for( int i = 0; i < listaPestanas.size(); i++ ){
      Tab t = (Tab) listaPestanas.get( i );
      if( t.isActive() ) return t.getCaptionLabel().getText();//opcion 1, pestana 0 devuelve como Camara
      //if( t.isActive() ) return t.getName();//opcion 2, pestana 0 devuelve como default
    }
    return null;
  }
  
  public void setPestanaActiva( int numero ){
    switch( numero ){
      case 2:
        pestanaActiva = PESTANA_POSTURAS;
        break;
      case 1:
        pestanaActiva = PESTANA_CAPTURA;
        break;
      default:
        pestanaActiva = PESTANA_CAMARA;
        break;
    }
  }
  
  public void actualizarColorPestanas(){
    
    ControllerList listaPestanas = controlWindow.getTabs();
    
    for( int i = 0; i < listaPestanas.size(); i++ ){
      Tab t = (Tab) listaPestanas.get( i );
      if( t.isActive() ) t.getCaptionLabel().setColor( paleta.negro );
      else t.getCaptionLabel().setColor( paleta.blanco );
    }

  }
  
  void ejecutar(){
    //background( paleta.grisFondo );
    
    pushStyle();
    
    fill( paleta.negro );
    noStroke();
    rect( 0, 0, width, 42 );
    
    image( logoCOD05, 21, 0 );
    
    fill( paleta.grisClaro );//grisFondo
    textAlign( CENTER, CENTER );
    text( titulo, width * 0.5, 21 );
    
    switch( motor.estado ){
      case Motor.ESTADO_COMPROBANDO_CAMARAS:
        comprobandoCamaras();
        break;
      case Motor.ESTADO_CAMARAS_NO_COMPATIBLES:
        camarasNoCompatibles();
        break;
      case Motor.ESTADO_CORRIENDO:
        pestanas();
        break;
    }

    popStyle();
    
  }
  
  void comprobandoCamaras(){
    fill( paleta.grisClaro );
    textFont(fuenteGrande);
    textSize( 30 );
    text( "Comprobando cámaras", width*0.5, height*0.5 );
  }
  
  void camarasNoCompatibles(){
    fill( paleta.grisClaro );
    textFont(fuenteGrande);
    textSize( 30 );
    text( "La webcam no es compatible con (320x240,fps30)", width*0.5, height*0.5 );
  }
  
  void pestanas(){
    //barra pestanas
    fill( paleta.grisClaro );
    rect( 0, 42, width, 42 );
    //
    
    switch( pestanaActiva ){
      case PESTANA_CAMARA:
        pestanaCamara();
        break;
      case PESTANA_CAPTURA:
        pestanaProcesos();
        break;
      case PESTANA_POSTURAS:
        pestanaPosturas();
        break;
      default:
       break;
    }
    
  }
  
  void pestanaCamara(){
    image( motor.camara, width*0.45, height * 0.35 );
  }
  
  void pestanaProcesos(){
    
    image( motor.camara, 10, 100 );
    image( motor.movimiento.imagenAcomparar, 10+Motor.ANCHO_CAMARA, 100 );
    image( motor.movimiento.substraccion, 10, 100+Motor.ALTO_CAMARA );
    //image( movimiento.bitonal, ancho, 0 );
    image( motor.movimiento.getImagenAnalisis(), 10+Motor.ANCHO_CAMARA, 100+Motor.ALTO_CAMARA );
    
  }
  
  void pestanaPosturas(){
    image( motor.movimiento.getImagenAnalisis(), 10, 100 );
    textAlign( LEFT );
    fill( paleta.amarillo );
    text( "Desequilibrio: " + motor.posturas.desequilibrio.estado.toString(), 10, 440 );
    text( "Nivel: " + motor.posturas.nivel.estado.toString(), 10, 470 );
    text( "Apertura: " + motor.posturas.apertura.estado.toString(), 10, 500 );
  }
  
}

void controlEvent(ControlEvent evento) {
  
  if( evento.isTab() ) {
    gui.setPestanaActiva( evento.getTab().getId() );
    gui.actualizarColorPestanas();
  }else if( evento.isFrom( "rangoNivel" ) ){
    motor.posturas.nivel.rangoInferior = evento.getController().getArrayValue( 0 );
    motor.posturas.nivel.rangoSuperior = evento.getController().getArrayValue( 1 );
  }else if( evento.isFrom( "rangoApertura" ) ){
    motor.posturas.apertura.rangoInferior = evento.getController().getArrayValue( 0 );
    motor.posturas.apertura.rangoSuperior = evento.getController().getArrayValue( 1 );
  }
  
}

void SeleccionarCamara( int numeroCamara ){
  motor.camaraSeleccionada = motor.camarasAptas[ numeroCamara ];
  motor.estado = Motor.ESTADO_INICIAR_CAMARA;
}

void nuevoFondo(){
  motor.movimiento.recapturarFondo();
}

void sliderUmbral( float valor ){
  motor.movimiento.setUmbral( valor );
}

void toggleActivarAmortiguacion( boolean toggle ){
  motor.movimiento.amortiguacionActivada = toggle;
  gui.getController( "sliderAmortiguacion" ).setVisible( toggle );
}

void sliderAmortiguacion( float valor ){
  motor.movimiento.factorAmortiguacion = valor;
}

void toggleAutocaptura( boolean toggle ){
  motor.movimiento.autocaptura = toggle;
  gui.getController( "sliderSensibilidadAutocaptura" ).setVisible( toggle );
  gui.getController( "sliderTiempoAutocaptura" ).setVisible( toggle );
}

void sliderSensibilidadAutocaptura( float valor ){
  motor.movimiento.setSensibilidadAutocaptura( valor );
}

void sliderTiempoAutocaptura( int valor ){
  motor.movimiento.tiempoTolenranciaAutocaptura = valor;
}

void sliderUmbralDesequilibrio( float valor ){
  motor.posturas.desequilibrio.umbralDesequilibrio = valor;
}
