class GuiDesequilibrio{
  
  private Range umbralesDesequilibrio;
  
  GuiDesequilibrio( GuiP5 guiP5, String pestana ){
    
    umbralesDesequilibrio = guiP5.addRange( "umbralesDesequilibrio" )
                            //primero que nada desactivo el "desencadenamiento de enventos"
                            .setBroadcast(false)
                            .setLabel( "Umbrales de desequilibrio" )
                            .setPosition( width * 0.5 - 150, height - 70 )
                            .setSize( 300, 20 )
                            .setRange( 0, UsuarioDesequilibrio.MAXIMO_VALOR_UMBRAL )
                            .setRangeValues( UsuarioDesequilibrio.getUmbralMenor(), UsuarioDesequilibrio.getUmbralMaximo() )
                            //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
                            .setBroadcast(true)
                            .moveTo( pestana );
                            
    umbralesDesequilibrio.getCaptionLabel().align(ControlP5.CENTER, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
  }
  
}