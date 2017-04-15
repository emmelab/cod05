class CheckBox {
  int posX;
  int posY;
  int tam;
  float textSize;
  float tamTotal;
  String nombre;
  color relleno;
  color cTexto;

  boolean check;

  CheckBox( int posX, int posY, int tam, int textSize, String nombre ) {
    this.posX = posX;
    this.posY = posY;
    this.tam = tam;
    this.nombre = nombre;

    this.textSize = textSize;

    textSize( textSize );
    tamTotal = tam + tam/4 + textWidth( nombre );
  }

  CheckBox( int posX, int posY, int tam, int textSize, String nombre, color texto, color relleno ) {
    this.posX = posX;
    this.posY = posY;
    this.tam = tam;
    this.nombre = nombre;

    this.relleno = relleno;
    this.cTexto = texto;

    this.textSize = textSize;
    textSize( textSize );
    tamTotal = tam + tam/4 + textWidth( nombre );
  }

  void dibujar() {
    pushStyle();
    textSize( textSize );
    textAlign(CORNER);
    fill( relleno );
    rect( posX, posY, tam, tam );
    fill( cTexto );
    text( nombre, posX + tam + tam/4, posY + tam - tam/4 );
    if ( check ) {
      fill( 255 - red(relleno), 255 - green(relleno), 255 - blue(relleno) );
      text( "X", posX + tam/4, posY + tam - tam/4 );
    }
    popStyle();
  }

  void setRelleno( color c ) {
    relleno = c;
  }

  void setTextColor( color c ) {
    cTexto = c;
  }

  boolean getEstado() {
    return check;
  }

  String getNombre() {
    return nombre;
  }

  void mousePressed() {
    if ( mouseX > posX && mouseX < posX + tamTotal && mouseY > posY && mouseY < posY + tam ) {
      check = !check;
    }
  }
}