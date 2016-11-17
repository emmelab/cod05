class InterfazDebugNivel{
  
  ControlP5 cp5;
  
  boolean bang_calcularPiso;
  
  UsuarioNivel usuarioNivel;
  
  Slider umbralBajo, umbralAlto;
  
  InterfazDebugNivel( ControlP5 cp5 ){
    this.cp5 = cp5;
    
    cp5.addBang( "calcularPiso" )
        .setVisible( false )
        .setPosition( width - 150, height - 70 )
        .setSize( 100, 20 )
        .setLabel("Calcular Piso")
        .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER);
    
    umbralBajo = cp5.addSlider( "umbralBajo" )
                    .setLabel( "Umbral Bajo" )
                    .setSize( 20, 150 )
                    .setPosition( width - 150, height*0.5 - 75 )
                    .setRange( 0, 1 )
                    .setSliderMode(Slider.FLEXIBLE)
                    .setVisible( false );
    umbralBajo.getCaptionLabel().alignX( ControlP5.CENTER );
                    
    umbralAlto = cp5.addSlider( "umbralAlto" )
                    .setLabel( "Umbral Alto" )
                    .setSize( 20, 150 )
                    .setPosition( width - 75, height*0.5 - 75 )
                    .setRange( 0, 1 )
                    .setSliderMode(Slider.FLEXIBLE)
                    .setVisible( false );
    umbralAlto.getCaptionLabel().alignX( ControlP5.CENTER );
        
  }
  
  void setUsuarioNivel( UsuarioNivel un ){
    usuarioNivel = un;
    
    
    if( usuarioNivel != null ){
      
      umbralBajo.setValue( usuarioNivel.factorUmbralBajo );
      umbralBajo.setVisible( true );
      
      umbralAlto.setValue( usuarioNivel.factorUmbralAlto );
      umbralAlto.setVisible( true );
      
    }
    
  }
  
  void hide( int estado ){
    
    if( estado == motor.DEBUG_NIVEL ){
      cp5.get(Bang.class, "calcularPiso").setVisible( true );
      
      if( usuarioNivel != null ){
        umbralBajo.setVisible( true );
        umbralAlto.setVisible( true );
      }
      
    }else{
      cp5.get(Bang.class, "calcularPiso").setVisible( false );
      umbralBajo.setVisible( false );
      umbralAlto.setVisible( false );
    }
    
  }

}

void calcularPiso(){
  background( 255, 0, 0 );
  motor.interfaz.interfazDebugNivel.bang_calcularPiso = true;
}

void umbralBajo( float entrada ){
  if( motor.interfaz.interfazDebugNivel.usuarioNivel != null )
    motor.interfaz.interfazDebugNivel.usuarioNivel.setFactorUmbralBajo( entrada );
}

void umbralAlto( float entrada ){
  if( motor.interfaz.interfazDebugNivel.usuarioNivel != null )
    motor.interfaz.interfazDebugNivel.usuarioNivel.setFactorUmbalAlto( entrada );
}
