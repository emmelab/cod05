class BarraSuperior implements AutoDraw {
  float margen, alto;
  PImage marca, ayuda;
  BotonAtras botonAtras;

  BarraSuperior() {
    marca = iconos.get(dicIcos.marca);
    ayuda = iconos.get(dicIcos.ayuda);
    alto = marca.height*1.5;
    margen = alto/2;
    botonAtras = new BotonAtras( "< Atrás", margen + marca.width, alto - margen*0.74, 20 );
    autoDraw.add(this);
  }

  void draw() {  
      pushStyle();
      noStroke();
      imageMode(CENTER);
      fill(paleta.panelSuperior);
      tint(paleta.inactivo);
      rect(0, 0, width, alto);
      image(marca, marca.width/2+ margen/2, margen);
      image(ayuda, width - ayuda.width/2 - margen/2, margen);
      
      botonAtras.dibujar( paleta.inactivo );
      
      textSize(20);
      fill(paleta.inactivo);
      String textIP = "IP: " + oscP5.ip();
      text( textIP, width - margen * 1.1 - ayuda.width - textWidth( textIP ), alto - margen * 0.74 );
      
      popStyle();   
  }
  
  class BotonAtras extends Auto implements AutoMousePressed{
    
    String text;
    int tamanoTexto;
    int x, y;
    int anchoTexto;
    boolean activo;
    
    BotonAtras( String text, float x, float y, int tamanoTexto ){
      this.text = text;
      this.x = round( x );
      this.y = round( y );
      this.tamanoTexto = tamanoTexto;
      pushStyle();textSize( tamanoTexto );
      anchoTexto = ceil(textWidth( text ));popStyle();
      setNombre( "botonAtras" );
      autoMousePressed.add(this);
    }
    
    void dibujar( color col ){
      if( !autoActivo ) return;
      fill( col );
      textSize( tamanoTexto );
      text( text, this.x, this.y );
    }
    
    void mousePressed(){
      if( !autoActivo ) return;
      if( mouseX > x && mouseX < x + anchoTexto ){
        if( mouseY>y-tamanoTexto && mouseY<y ){
          interfaz.resetIntro();
          setAutoActivo( false );
        }
      }
    }
    
  }//end BotonAtras
  
}
