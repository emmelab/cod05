class Boton {
  int posX;
  int posY;
  int tamH;
  int tamV;
  float textSize;
  String nombre;
  color c;
  color colorTexto;

  boolean activo = false;

  Boton( int posX, int posY, String nombre, float textSize, color c ) {
    this.posX = posX;
    this.posY = posY;
    this.nombre = nombre;
    this.textSize = textSize;
    textSize( textSize );
    this.tamV = int( textAscent() + textDescent() ) + 5;
    this.tamH = (int)textWidth(this.nombre) + 10;
    this.c = c;
    colorTexto = 255;
  }

  Boton( int posX, int posY, int tamH, int tamV ) {
    this.posX = posX;
    this.posY = posY;
    this.nombre = "";
    this.textSize = 12;
    textSize( textSize );
    this.tamV = tamV;
    this.tamH = tamH;
    this.c = 100;
    colorTexto = 255;
  }

  void dibujar() {
    pushStyle();
    textAlign(LEFT);
    textSize( textSize );    
    fill( c );
    rect( posX, posY - textSize, tamH, tamV );
    fill(colorTexto);
    text( nombre, posX + 5, posY );
    popStyle();
  }

  boolean mousePressed() {
    if ( mouseX > posX && mouseX < posX + tamH && mouseY > posY - textSize && mouseY < posY + tamV - textSize ) {
      activo = !activo;
    }
    return activo;
  }
  
  void mouseReleased(){
    reiniciar();
  }

  void reiniciar() {
    activo = false;
  }
  
  void setColor( color c ){
    this.c = c;
  }
  
  boolean getActivo(){
    return activo;
  }
  
}