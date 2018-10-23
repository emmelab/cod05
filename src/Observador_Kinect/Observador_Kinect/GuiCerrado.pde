class GuiCerrado{
  
  private Slider factorCerrado;
  
  GuiCerrado( GuiP5 guiP5, String pestana ){
    
    factorCerrado = guiP5.addSlider( "sliderFactorCerrado" )
                          .setBroadcast(false)
                          .setLabel( "Umbral cerrado" )
                          .setSize( 300, 20 )
                          .setPosition( width*0.5 - 256, height - 70 )
                          .setRange( 0, 5 )
                          .setValue( UsuarioCerrado.getFactorUmbral() )
                          .setSliderMode(Slider.FLEXIBLE)
                          .setBroadcast(true)
                          .moveTo( pestana )
                          ;    
     
     factorCerrado.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
     
     guiP5.addButton( "resetCerrado" )
     //primero que nada desactivo el "desencadenamiento de enventos"
     .setBroadcast(false)
     .setLabel( "Reestablecer" )
     .setWidth( 100 )
     .setHeight( 20 )
     .setPosition( width * 0.5 + 156, height - 70 )
     //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
     .setBroadcast(true)
     .moveTo( pestana );
     
  }
}

void sliderFactorCerrado( float factor ){
  UsuarioCerrado.setFactorUmbral( factor );
  saveDatosXML();
}

public void resetCerrado(){
  XML xml = loadXML( "DefaultUmbrales.xml" );
  XML hijo = xml.getChild( "UsuarioCerrado" );
  if( hijo != null ){
    
    float factorUmbralCerrado = hijo.getFloat("factorUmbral");
    UsuarioCerrado.setFactorUmbral( factorUmbralCerrado );

    motor.getGuiP5().get( Slider.class, "sliderFactorCerrado" ).setValue( UsuarioCerrado.getFactorUmbral() );
    
    saveDatosXML();
  }  
}