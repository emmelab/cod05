
class TextBox {

  String id;

  int posX;
  int posY;
  int tamV;
  int tamH;
  float textSize;
  color textColor;
  color boxColor;
  String textoIntroducido;

  String referencia;
  boolean cajaExterna = false;
  color cajaExternaColor = 0;
  color cajaExternaColorText = 255;
  int cajaExternaPaddingDerecha = 2;
  int cajaExternaPaddingIzquierda = 2; 
  int cajaExternaPaddingArriba = 2;
  int cajaExternaPaddingAbajo = 2;

  boolean activo;
  boolean empezo = false;

  boolean modificable = true;


  //seteas el tamaño y te acomoda el texto
  TextBox( String id, int posX, int posY, String textoIntroducido, int tamH, int tamV ) {
    this.posX = posX;
    this.posY = posY;
    this.textoIntroducido = textoIntroducido;
    this.tamV = tamV + 5;
    this.tamH = tamH + 10;

    float tamMinH = 12/textWidth(textoIntroducido) *tamH;
    float tamMinV = 12/(textDescent()+textAscent()) *tamV;

    this.textSize = int(min(tamMinH, tamMinV));
    boxColor = color( 200 );
    textColor = color(50);

    activo = false;

    this.id = id;
  }

  // seteas la caja y el texto
  TextBox( String id, int posX, int posY, int tamH, int tamV, String textoIntroducido, int textSize ) {
    this.posX = posX;
    this.posY = posY;
    this.textoIntroducido = textoIntroducido;
    this.tamV = tamV + 5;
    this.tamH = tamH + 10;

    this.textSize = textSize;
    boxColor = color( 200 );
    textColor = color(50);

    activo = false;

    this.id = id;
  }

  // te acomoda la caja al tamaño del texto
  TextBox( String id, int posX, int posY, String textoIntroducido, float textSize ) {
    this.posX = posX;
    this.posY = posY;
    this.textoIntroducido = textoIntroducido;
    this.textSize = textSize;
    textSize( textSize );
    this.tamV = int( textAscent() + textDescent() ) + 5;
    this.tamH = (int)textWidth(this.textoIntroducido) + 10;
    boxColor = color( 200 );
    textColor = color(50);

    activo = false;

    this.id = id;
  }

  void dibujar() {
    pushStyle();
    noStroke();
    rectMode(CORNER);
    if ( cajaExterna ) {
      fill( cajaExternaColor );
      rect( posX - cajaExternaPaddingIzquierda, posY - textSize - cajaExternaPaddingArriba, tamH + cajaExternaPaddingIzquierda + cajaExternaPaddingDerecha, tamV + cajaExternaPaddingArriba + cajaExternaPaddingAbajo );
      fill( cajaExternaColorText );
      textAlign(CENTER);
      text( referencia, posX - cajaExternaPaddingIzquierda/2, posY + tamV/5 );
    }
    textAlign(LEFT);
    fill(boxColor);
    rect( posX, posY - textSize, tamH, tamV );
    if ( modificable ) {
      fill(textColor);
    } else {
      fill( textColor, 100 );
    }
    textSize( textSize );

    text( textoIntroducido, posX + 5, posY + 5 );


    if ( activo ) {
      if ( millis() % 1000 > 0 &&  millis() % 1000 < 500 ) {
        fill(0);
        rect( posX + textWidth(this.textoIntroducido) + 5, posY - textSize, 1, tamV );
      }
    }

    popStyle();
  }

  String getTextoIntroducido() {
    return textoIntroducido;
  }

  String getId() {
    return id;
  }

  String getReferencia() {
    return referencia;
  }

  void setReferencia( String r ) {
    referencia = r;
  }

  void setCajaExterna( boolean b ) {
    cajaExterna = b;
  }

  void setColorCajaExterna( color c ) {
    cajaExternaColor = c;
  }

  void setPaddings( int arriba, int derecha, int abajo, int izquierda ) {
    cajaExternaPaddingDerecha = derecha;
    cajaExternaPaddingIzquierda = izquierda; 
    cajaExternaPaddingArriba = arriba;
    cajaExternaPaddingAbajo = abajo;
  }

  void setCajaExterior( String referencia, color colorCaja, color colorTexto, int paddingAr, int paddingD, int paddingAb, int paddingI) {
    this.referencia = referencia;
    this.cajaExternaColor = colorCaja;
    this.cajaExternaColorText = colorTexto;
    setPaddings( paddingAr, paddingD, paddingAb, paddingI );
  }

  void setColorText( color c ) {
    this.textColor = c;
  }

  void setColorBox( color c ) {
    this.boxColor = c;
  }

  void setTextoIntroducido( String t ) {
    textoIntroducido = t;
  }

  void setModificable( boolean n ) {
    modificable = n;
  }

  void mousePressed(float mapeoY ) {
    if ( modificable ) {
      if ( mouseX > posX && mouseX < posX + tamH && mouseY > posY + mapeoY - textSize && mouseY < posY + mapeoY - textSize + tamV ) {
        if ( !empezo ) {
          textoIntroducido = new String();
        }
        empezo = true;
        activo = true;
      } else {
        activo = false;
      }
    }
  }

  void keyPressed() {
    if ( activo && modificable ) {
      char caracter = key;
      if ( int(caracter) >= 48 && int(caracter) <= 57 || caracter == '.' || caracter == ':') {
        textoIntroducido = textoIntroducido + caracter;
      }
      if ( keyCode == BACKSPACE) {
        if ( textoIntroducido.length() > 0 ) {
          textoIntroducido = textoIntroducido.substring( 0, textoIntroducido.length() - 1 );
        }
      }
    }
  }
}