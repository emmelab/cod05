class GuiCerrado{
  
  private Slider factorCerrado;
  
  GuiCerrado( GuiP5 guiP5, String pestana ){
    
    factorCerrado = guiP5.addSlider( "sliderFactorCerrado" )
                          .setBroadcast(false)
                          .setLabel( "Umbral cerrado" )
                          .setSize( 300, 20 )
                          .setPosition( width * 0.5 - 150, height - 70 )
                          .setRange( 0, 5 )
                          .setValue( UsuarioCerrado.getFactorUmbral() )
                          .setSliderMode(Slider.FLEXIBLE)
                          .setBroadcast(true)
                          .moveTo( pestana )
                          ;    
     
     factorCerrado.getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
     
  }
}

void sliderFactorCerrado( float factor ){
  UsuarioCerrado.setFactorUmbral( factor );
  saveDatosXML();
}