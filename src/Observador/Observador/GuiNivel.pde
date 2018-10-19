class GuiNivel{
  
  GuiNivel( GuiP5 guiP5, String pestana ){
      guiP5.addRange( "umbralesNivel" )
            //primero que nada desactivo el "desencadenamiento de enventos"
            .setBroadcast(false)
            .setLabel( "Factor nivel bajo y alto" )
            .setPosition( width*0.5 - 256, height - 70 )
            .setSize( 300, 20 )
            .setRange( 0, 1 )
            .setRangeValues( UsuarioNivel.getFactorUmbralBajo(), UsuarioNivel.getFactorUmbralAlto() )
            //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
            .setBroadcast(true)
            .moveTo( pestana );
            
      guiP5.getController("umbralesNivel").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
      
      guiP5.addButton( "resetNivel" )
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

public void resetNivel(){
  XML xml = loadXML( "DefaultUmbrales.xml" );
  XML hijo = xml.getChild( "UsuarioNivel" );
  if( hijo != null ){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");
    
    UsuarioNivel.setUmbrales( factorUmbralBajo, factorUmbralAlto );

    motor.getGuiP5().get( Range.class, "umbralesNivel" ).setRangeValues( UsuarioNivel.getFactorUmbralBajo(), UsuarioNivel.getFactorUmbralAlto() );
    
    saveDatosXML();
  }  
}