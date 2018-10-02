import controlP5.*;

class GuiP5 extends ControlP5{
  
  final String titulo = "CONFIGURADOR DEL OBSERVADOR WEBCAM";
  final PImage logoCOD05, logoObservador;
  final PFont fuentePequena, fuenteGrande;
  
  String pestanaActiva;
  static final String
  PESTANA_CAMARA = "Cámara",
  PESTANA_PROCESOS = "Proceso",
  PESTANA_DESEQUILIBRIO = "Desequilibrio",
  PESTANA_NIVEL = "Nivel",
  PESTANA_CERRADO = "Cerrado";
  
  //Pestana principal - CAMARA
  ScrollableList listaDeCamaras;
  
  //Pestanas
  GuiPestanaProceso pestanaProceso;
  GuiPestanaDesequilibrio pestanaDesequilibrio;
  
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
    String[] pestanas = { PESTANA_CAMARA, PESTANA_PROCESOS, PESTANA_DESEQUILIBRIO, PESTANA_NIVEL, PESTANA_CERRADO };
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
    gui.addButton( "nuevoFondo" )
    //primero que nada desactivo el "desencadenamiento de enventos"
    .setBroadcast(false)
    .setLabel( "Nuevo fondo" )
    .setWidth( 100 )
    .setHeight( 20 )
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
     
    //PESTANA 1 - Procesos
    pestanaProceso = new GuiPestanaProceso();
    
    //PESTANA 2 - Desequilibrio
    pestanaDesequilibrio = new GuiPestanaDesequilibrio();
   
  }
  
  void crearSlider( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto, float minimo, float maximo, float valor ){
    Slider s = gui.addSlider( nombre )
    .setBroadcast(false)
    .setLabel( etiqueta )
    .setSize( 130, 20 )
    .setPosition( x, y )
    .setRange( minimo, maximo )
    .setValue( valor/*motor.movimiento.!!variable correspondiente!!*/ )
    .setSliderMode(Slider.FLEXIBLE)
    .setBroadcast(true)
    .moveTo( pestana )
    ;
    //gui.getController(nombre).getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
    s.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
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
      case 4:
        pestanaActiva = PESTANA_CERRADO;
        break;
      case 3:
        pestanaActiva = PESTANA_NIVEL;
        break;
      case 2:
        pestanaActiva = PESTANA_DESEQUILIBRIO;
        break;
      case 1:
        pestanaActiva = PESTANA_PROCESOS;
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
    if( motor.movimiento != null ) motor.movimiento.conteo();
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
      case PESTANA_PROCESOS:
        pestanaProceso.ejecutar();
        break;
      case PESTANA_DESEQUILIBRIO:
        pestanaDesequilibrio.ejecutar();
        break;
      case PESTANA_NIVEL:
        break;
      case PESTANA_CERRADO:
        break;
      default:
       break;
    }
    
  }
  
  void pestanaCamara(){
    image( motor.camara, width*0.45, height * 0.35 );
  }
  
}

void controlEvent(ControlEvent evento) {
  
  if( evento.isTab() ) {
    gui.setPestanaActiva( evento.getTab().getId() );
    gui.actualizarColorPestanas();
  }
  
}

void SeleccionarCamara( int numeroCamara ){
  motor.camaraSeleccionada = motor.camarasAptas[ numeroCamara ];
  motor.estado = Motor.ESTADO_INICIAR_CAMARA;
}
