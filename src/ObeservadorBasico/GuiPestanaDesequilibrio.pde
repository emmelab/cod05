class GuiPestanaDesequilibrio{
  
  GuiPestanaDesequilibrio(){
    
    float posX = width*0.5 + 50;
    int posY = 175, saltoY = 100;
    
    crearSlider( "sliderUmbralEje", "Umbral eje", posX, posY, 0f, 100f, 10f );
    crearSlider( "sliderUmbralNivel", "Umbral nivel", posX, posY += saltoY, 0f, 1f, 0.6 );
    crearSlider( "sliderUmbralCerrado", "Umbral cerrado", posX, posY += saltoY, 0f, 0.3, 0.2 );
    
  }
  
  void crearSlider( String nombre, String etiqueta, float x, float y, float minimo, float maximo, float valor ){
    gui.crearSlider( nombre, etiqueta, GuiP5.PESTANA_DESEQUILIBRIO, x, y, 200, 20, minimo, maximo, valor );
  }
  
  void ejecutar(){
    PImage resultado = motor.movimiento.getImagenAnalisis();
    image(resultado, width*0.5-Motor.ANCHO_CAMARA, height*0.5-Motor.ALTO_CAMARA*0.5 );
    
    UsuarioDesequilibrio d = motor.uDeseq;
    UsuarioNivel n = motor.uNivel;
    UsuarioCerrado c = motor.uCerrado;
    
    String texto = "Deseq: " + ( d.salioIzquierda? "salio IZQ" : d.salioDerecha? "salio DER" : "..." );
    texto += "\nNivel: " + (n.entroBajo? "entro bajo" : n.entroMedio? "entro medio" : n.entroAlto? "entro alto" : "...");
    texto += "\nCerrado: " + ( c.cerro? "cerro" : c.abrio? "abrio" : "..." );
    texto += "\n\t" + c.cerrado;
    
    textoDebug( texto );
    
  }
  
  void textoDebug( String texto ){
    float posX = width*0.5-Motor.ANCHO_CAMARA;
    float posY = height*0.5+Motor.ALTO_CAMARA*0.5+50;
    fill( paleta.blanco );
    textAlign( LEFT );
    text( texto, posX, posY );
  }
}

void sliderUmbralEje( float valor ){
  umbralEje = valor;
}

void sliderUmbralNivel( float valor ){
  umbralNivel = valor;
}

void sliderUmbralCerrado( float valor ){
  umbralCerrado = valor;
}
