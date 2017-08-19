class GuiCerrado{
  
  private Slider factorCerrado;
  
  GuiCerrado( GuiP5 guiP5, String pestana ){
    
    factorCerrado = guiP5.addSlider( "sliderFactorCerrado" )
                          .setBroadcast(false)
                          .setLabel( "Factor\nUmbral" )
                          .setSize( 300, 20 )
                          .setPosition( width * 0.5, height - 50 )
                          .setRange( 0, 5 )
                          .setValue( UsuarioCerrado.getFactorUmbral() )
                          .setSliderMode(Slider.FLEXIBLE)
                          .setBroadcast(true)
                          .moveTo( pestana )
                          ;    
  }
}

void sliderFactorCerrado( float factor ){
  UsuarioCerrado.setFactorUmbral( factor );
  saveDatosXML();
}
