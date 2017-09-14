class GuiNivel{
  
  GuiNivel( GuiP5 guiP5, String pestana ){
      guiP5.addRange( "umbralesNivel" )
            //primero que nada desactivo el "desencadenamiento de enventos"
            .setBroadcast(false)
            .setLabel( "Factor umbral bajo y alto" )
            .setPosition( width*0.1, height - 50 )
            .setSize( 300, 20 )
            .setRange( 0, 1 )
            .setRangeValues( UsuarioNivel.getFactorUmbralBajo(), UsuarioNivel.getFactorUmbralAlto() )
            //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
            .setBroadcast(true)
            .moveTo( pestana );
  }
  
}
