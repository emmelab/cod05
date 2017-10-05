class GuiDesequilibrio{
  
  private Range umbralesDesequilibrio;
  
  GuiDesequilibrio( GuiP5 guiP5, String pestana ){
    
    umbralesDesequilibrio = guiP5.addRange( "umbralesDesequilibrio" )
                            //primero que nada desactivo el "desencadenamiento de enventos"
                            .setBroadcast(false)
                            .setLabel( "Umbrales de desequilibrio" )
                            .setPosition( width*0.5 - 256, height - 70 )
                            .setSize( 300, 20 )
                            .setRange( 0, UsuarioDesequilibrio.MAXIMO_VALOR_UMBRAL )
                            .setRangeValues( UsuarioDesequilibrio.getUmbralMenor(), UsuarioDesequilibrio.getUmbralMaximo() )
                            //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
                            .setBroadcast(true)
                            .moveTo( pestana );
                            
    umbralesDesequilibrio.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
    
    guiP5.addButton( "resetDesequilibrio" )
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

public void resetDesequilibrio(){
  XML xml = loadXML( "DefaultUmbrales.xml" );
  XML hijo = xml.getChild( "UsuarioDesequilibrio" );
  if( hijo != null ){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");    
    
    UsuarioDesequilibrio.setUmbrales( factorUmbralBajo, factorUmbralAlto );
    
    motor.getGuiP5().get( Range.class, "umbralesDesequilibrio" ).setRangeValues( UsuarioDesequilibrio.getUmbralMenor(), UsuarioDesequilibrio.getUmbralMaximo() );
    
    saveDatosXML();
  }  
}
