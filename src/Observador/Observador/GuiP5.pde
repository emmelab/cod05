import controlP5.*;

class GuiP5 extends ControlP5{
  
  final String titulo = "CONFIGURADOR DEL OBSERVADOR";
  final PImage logoCOD05, logoObservador;
  
  //HashMap<String, Tab> pestanas = new HashMap<String, Tab>();
  
  GuiDesequilibrio guiDesequilibrio;
  GuiCerrado guiCerrado;
  GuiNivel guiNivel;
  
  public GuiP5( PApplet p5, String[] pestanas ){
    super( p5 );
    addFrameRate().setInterval(5).setPosition(0,height - 15).moveTo( "global" );
    
    logoCOD05 = loadImage( "logoCOD05.png" );
    logoObservador = loadImage( "observador.png" );
    logoObservador.resize( round( logoObservador.width * 0.45 ), round( logoObservador.height * 0.45 ) );
    
    PFont fuente = loadFont( "MyriadPro-Regular-14.vlw" );
    setFont( fuente );
    textFont( fuente );
    setColorCaptionLabel( paleta.blanco );
    
    controlWindow.setPositionOfTabs( (int) controlWindow.getPositionOfTabs()[ 0 ], 42 );

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
    
    //PESTANA 0 - DEFAULT CAMARA COMUN
    addToggle("espejoEjeX")
     .setPosition( 75, 84 + (height - 84) * 0.65 - 25 )
     .setSize(20,20)
     .setValue( comunicacionOSC.getInvertidoEjeX() )
     .setLabel( "Espejo eje X" )
     .setColorForeground( paleta.grisClaro ) 
     .getCaptionLabel().alignX( ControlP5.LEFT ).alignY( ControlP5.CENTER ).toUpperCase( false ).setPaddingX( 30 )
     ;
     
     addToggle("espejoEjeY")
     .setPosition( 75, 84 + (height - 84) * 0.65 + 25 )
     .setSize(20,20)
     .setValue( comunicacionOSC.getInvertidoEjeY() )
     .setLabel( "Espejo eje Y" )
     .setColorForeground( paleta.grisClaro ) 
     .getCaptionLabel().alignX( ControlP5.LEFT ).alignY( ControlP5.CENTER ).toUpperCase( false ).setPaddingX( 30 )
     ;
    
    //PESTANA 1 - DEBUG DESEQUILIBRIO
    guiDesequilibrio = new GuiDesequilibrio( this, pestanas[ 1 ] );
    
    //PESTANA 2 - DEBUG NIVEL
    guiNivel = new GuiNivel( this, pestanas[ 2 ] );
    
    //PESTANA 3 - DEBUG CERRADO
    guiCerrado = new GuiCerrado( this, pestanas[ 3 ] );
    
    //PESTANA 4 - DEBUG ESPALDA
    
  }
  
  //---------------------------------------- METODOS PUBLICOS
  
  //seters y geters
  public void setPestanaActiva( String pestana ){
    if( !pestana.equals( motor.NOMBRE_ESTADO[ 0 ] ) ){
      getTab( pestana ).bringToFront();
    }else{
      getTab( "default" ).bringToFront();
    }
    actualizarColorPestanas();
  }
  
  public void actualizarColorPestanas(){
    
    ControllerList listaPestanas = controlWindow.getTabs();
    
    for( int i = 0; i < listaPestanas.size(); i++ ){
      Tab t = (Tab) listaPestanas.get( i );
      if( t.isActive() ) t.getCaptionLabel().setColor( paleta.negro );
      else t.getCaptionLabel().setColor( paleta.blanco );
    }

  }
  
  public void ejecutar( int estado ){
    pushStyle();
    
    fill( paleta.negro );
    noStroke();
    rect( 0, 0, width, 42 );
    
    image( logoCOD05, 21, 0 );
    
    fill( paleta.grisClaro );//grisFondo
    textAlign( CENTER, CENTER );
    text( titulo, width * 0.5, 21 );
    
    fill( paleta.grisClaro );
    rect( 0, 42, width, 42 );
    
    if( estado == Motor.CAMARA_COMUN ){
      fill( paleta.negro );
      rect( width * 0.05, 84 + ( (height - 84) * 0.2 ), width * 0.215, (height - 84) * 0.6 );
      
      fill( paleta.blanco );
      textAlign( CENTER, CENTER );
      text( "Espejar hacia\nCOD05 Lienzo", width * 0.1575, 84 + ( (height - 84) * 0.525 ) );
      
      imageMode( CENTER );
      image( logoObservador, width * 0.1575, 84 + ( (height - 84) * 0.25 ) + logoObservador.height * 0.5 );
      
    }else if( estado == Motor.DEBUG_DESEQUILIBRIO ){
      fill( paleta.grisClaro );
      rect( 144, 120, 512, 384 );
    }
    
    popStyle();
  }
 
}

void controlEvent(ControlEvent evento) {
  
  if( evento.isTab() ) {
    motor.setEstado( evento.getTab().getId() );
  }else if( evento.isFrom( "umbralesDesequilibrio" ) ){
    
    UsuarioDesequilibrio.setUmbralMenor( evento.getController().getArrayValue( 0 ) );
    UsuarioDesequilibrio.setUmbralMaximo( evento.getController().getArrayValue( 1 ) );
    saveDatosXML();
  
  }else if( evento.isFrom( "umbralesNivel" ) ){
    
    UsuarioNivel.setFactorUmbralBajo( evento.getController().getArrayValue( 0 ) );
    UsuarioNivel.setFactorUmbalAlto( evento.getController().getArrayValue( 1 ) );
    saveDatosXML();
    
  }
}

void espejoEjeX(boolean valor) {
  println("Eje X invertido:", valor);
  comunicacionOSC.setInvertidoEjeX( valor );
  saveDatosXML();
}

void espejoEjeY(boolean valor) {
  println("Eje Y invertido:", valor);
  comunicacionOSC.setInvertidoEjeY( valor );
  saveDatosXML();
} 
