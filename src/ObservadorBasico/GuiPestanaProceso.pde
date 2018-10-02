class GuiPestanaProceso{
  
  GuiPestanaProceso(){
    float posX = width * 0.825;
    int posY = 140, saltoY = 100;
    crearSlider( "sliderConvolucion", "Convolucion", posX, posY, 3, 10, 7 );
    crearSlider( "sliderUmbral", "Umbral", posX, posY += saltoY, 0, 255, 50 );
    crearSlider( "sliderFotogramasRetardo", "Fotogramas retardo", posX, posY += saltoY, 1, 120, 2 );
    crearSlider( "sliderBlend", "Blend", posX, posY += saltoY, 0, 0.2, 0.2 );
  }
  
  void crearSlider( String nombre, String etiqueta, float x, float y, float minimo, float maximo, float valor ){
    gui.crearSlider( nombre, etiqueta, GuiP5.PESTANA_PROCESOS, x, y, 130, 20, minimo, maximo, valor );
  }
  
  void ejecutar(){
    image( motor.camara, 10, 100 );
    image( motor.movimiento.imagenesAcomparar[1], 10+Motor.ANCHO_CAMARA, 100 );
    image( motor.movimiento.substraccion, 10, 100+Motor.ALTO_CAMARA );
    //image( movimiento.bitonal, ancho, 0 );
    
    PImage resultado = motor.movimiento.getImagenAnalisis();
    image(resultado, 10+Motor.ANCHO_CAMARA, 100+Motor.ALTO_CAMARA );
  }
}

void sliderConvolucion( int valor ){
  motor.movimiento.convolucion = valor;
}

void sliderUmbral( float valor ){
  motor.movimiento.umbral = valor;
}

void sliderFotogramasRetardo( int valor ){
  motor.movimiento.fotogramasRetardo = valor;
}

void sliderBlend( float valor ){
  motor.movimiento.blend = valor;
}

void nuevoFondo(){
  motor.movimiento.recapturarFondo();
}
