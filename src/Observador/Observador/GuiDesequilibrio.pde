class GuiDesequilibrio{
  
  private Range umbralesDesequilibrio;
  
  GuiDesequilibrio( GuiP5 guiP5, String pestana ){
    
    umbralesDesequilibrio = guiP5.addRange( "umbralesDesequilibrio" )
                            //primero que nada desactivo el "desencadenamiento de enventos"
                            .setBroadcast(false)
                            .setLabel( "Umbral menor y mayor" )
                            .setPosition( width*0.05, height - 50 )
                            .setSize( 300, 20 )
                            .setRange( 0, UsuarioDesequilibrio.MAXIMO_VALOR_UMBRAL )
                            .setRangeValues( UsuarioDesequilibrio.getUmbralMenor(), UsuarioDesequilibrio.getUmbralMaximo() )
                            //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
                            .setBroadcast(true)
                            .moveTo( pestana );
    
  }
  
}
