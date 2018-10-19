class SelectorDeColor extends ElementoUI {

  PImage imagenSelectorByS; 
  PImage imagenSelectorT; 
  boolean activo;
  float hSeleccion = 255;
  float sSeleccion = 255;
  float bSeleccion = 255;
  float redSeleccion;
  float greenSeleccion;
  float blueSeleccion;
  PVector posByS;
  PVector posT;
  Input[] inputs;
  float anchoContenedor; 
  float altoContenedor; 
  float borde; 
  color colorSeleccionado;


  SelectorDeColor() {
    nombre = "";
    imagenSelectorByS = createImage(100, 100, HSB);
    imagenSelectorT = createImage(20, 100, HSB);
    setTam(100);
  }

  void setColor(float h, float s, float b) {
    hSeleccion = h;
    sSeleccion = s;
    bSeleccion = b;
  }

  void setTam(float tam) {
    ancho = tam;
    alto = tam;
    borde = ancho/10;
    float anchoSatyBri = ancho;
    float anchoTon = ancho/5;
    float anchoInputs = ancho/3;
    anchoContenedor = (ancho+anchoTon+anchoInputs+borde*4);//ancho del selector de brillo y saturacion(ancho)--- ancoho del selector de tonalidad (ancho/5)--- anchoe de los inputs (ancho/3)
    altoContenedor = alto+borde*2;
    posByS = new PVector(pos.x-anchoContenedor/2+anchoSatyBri/2+borde, pos.y); //posicion selector brillo y saturacion
    posT = new PVector(pos.x-anchoContenedor/2+anchoSatyBri+borde*2+anchoTon/2, pos.y);  //posicion selector tonalidad
    inputs = new Input[7];
    float altoInputs = (altoContenedor-borde*7)/7;
    for (int i=0; i<inputs.length; i++) {
      inputs[i] = new Input();
      inputs[i].setPos(new PVector(pos.x+anchoContenedor/2-borde-anchoInputs/2, (pos.y-altoContenedor/2+borde+altoInputs/2+i*(altoInputs+borde/1.5))));
      inputs[i].setSize(anchoInputs, altoInputs);
      inputs[i].setLimite(3);
    }
    inputs[0].setNombre("Rojo");
    inputs[1].setNombre("Verde");
    inputs[2].setNombre("Azul");
    inputs[3].setNombre("Tono");
    inputs[4].setNombre("Saturacion");
    inputs[5].setNombre("Brillo");
    inputs[6].setNombre("Hexagecimal");
    inputs[0].setEtiqueta("r");
    inputs[1].setEtiqueta("g");
    inputs[2].setEtiqueta("b");
    inputs[3].setEtiqueta("h");
    inputs[4].setEtiqueta("s");
    inputs[5].setEtiqueta("b");
    inputs[6].setEtiqueta("Hex");
    imagenSelectorByS.resize(int(ancho), int(alto));
    imagenSelectorT.resize(int(ancho/5), int(alto));
    //setSelector();
  }

  void setSelector() {
    colorMode(HSB);
    imagenSelectorT.loadPixels();
    for (int i = 0; i < imagenSelectorT.pixels.length; i++) {
      int y = int(i/imagenSelectorT.width);
      int x = int(i%imagenSelectorT.width);
      float h = map(y, 0, imagenSelectorT.height, 0, 255);
      imagenSelectorT.pixels[i] = color(h, sSeleccion, bSeleccion);
    }
    imagenSelectorT.updatePixels();


    imagenSelectorByS.loadPixels();
    for (int i = 0; i < imagenSelectorByS.pixels.length; i++) {
      int x = i%imagenSelectorByS.width;
      int y = i/imagenSelectorByS.width;
      float s = map(y, 0, imagenSelectorByS.height, 255, 0);
      float b = map(x, 0, imagenSelectorByS.width, 0, 255);
      imagenSelectorByS.pixels[i] = color(hSeleccion, s, b);
    }
    imagenSelectorByS.updatePixels();
  }

  void dibujar() {
    dibujarBase();
    dibujarSelector();
    dibujarInputs();
  }

  void dibujarBase() {
    pushStyle();
    rectMode(CENTER);

    fill(0);
    noStroke();
    rect(pos.x, pos.y, anchoContenedor, altoContenedor);
    popStyle();
  }

  void dibujarSelector() {
    setSelector();
    colorMode(RGB);    
    pushStyle();
    imageMode(CENTER);
    noTint();
    image(imagenSelectorT, posT.x, posT.y);
    image(imagenSelectorByS, posByS.x, posByS.y);
    stroke(255, 255, 255);
    strokeWeight(2);
    float y = 0;
    float x = 0;
    y = map(sSeleccion, 255, 0, 0, imagenSelectorByS.height)+pos.y-alto/2;
    x = map(bSeleccion, 0, 255, 0, imagenSelectorByS.width)+pos.x-anchoContenedor/2+borde;

    line(x-borde/2, y, x+borde/2, y);
    line(x, y-borde/2, x, y+borde/2);

    y = map(hSeleccion, 0, 255, 0, imagenSelectorT.height)+pos.y-alto/2;
    x = posT.x;
    line(x-borde/2*3, y, x+borde/2*3, y);
    popStyle();
  }

  void dibujarInputs() {
    for (int i = 0; i < inputs.length; i++) {
      inputs[i].dibujar();
      println(i+"; "+inputs[i].texto);
    }
  }

  void setContenidoDeInputs(color c) {
    pushStyle();
    colorMode(HSB);
    hSeleccion = hue(c);
    sSeleccion = saturation(c);
    bSeleccion = brightness(c);
    colorSeleccionado = color(hSeleccion, sSeleccion, bSeleccion);
    String valor = "";
    popStyle();
    valor = str(int(red(c)));
    valor = "255";
    inputs[0].setTexto(valor);
    valor = str(int(green(c)));
    inputs[1].setTexto(valor);
    valor = str(int(blue(c)));
    inputs[2].setTexto(valor);
    valor = str(int(hue(c)));
    inputs[3].setTexto(valor);
    valor = str(int(saturation(c)));
    inputs[4].setTexto(valor);
    valor = str(int(brightness(c)));
    inputs[5].setTexto(valor);
    valor = hex(c);
    inputs[6].setTexto(valor);
  }

  void mousePressed(float x, float y) {
    for (int i = 0; i < inputs.length; i++) {
      inputs[i].mousePressed(x, y);
    }
    if (x>posByS.x-ancho/2 && x<posByS.x+ancho/2 &&
      y>posByS.y-alto/2 && y<posByS.y+alto/2) {
      sSeleccion = map(y-posByS.y+alto/2, 0, imagenSelectorByS.height, 260, -5);
      bSeleccion = map(x-pos.x+anchoContenedor/2-borde, 0, imagenSelectorByS.width, -5, 260);
    } 

    if (x>posT.x-ancho/10 && x<posT.x+ancho/10 &&
      y>posT.y-alto/2 && y<posT.y+alto/2) {
      hSeleccion = map(y-posT.y+alto/2, 0, imagenSelectorT.height, -5, 260);
    }

    pushStyle();
    colorMode(HSB);
    colorSeleccionado = color(hSeleccion, sSeleccion, bSeleccion);
    popStyle();
  }

  void keyPressed(int keyCode, char key) {
    color c = color(0, 0, 0);
    for (int i = 0; i < inputs.length; i++) {
      if (inputs[i].estaSeleccionado()) {
        inputs[i].keyPressed(keyCode, key);
        if (inputs[i].getNombre().equals("Rojo") || inputs[i].getNombre().equals("Verde") || inputs[i].getNombre().equals("Azul")) {
          pushStyle();
          colorMode(RGB);
          c = color(int(inputs[0].getTexto()), int(inputs[1].getTexto()), int(inputs[2].getTexto()));
          popStyle();
          hSeleccion = hue(c);
          sSeleccion = saturation(c);
          bSeleccion = brightness(c);
        } else if (inputs[i].getNombre().equals("Hexagecimal")) {
          c = color(int(inputs[6].getTexto()));
          hSeleccion = hue(c);
          sSeleccion = saturation(c);
          bSeleccion = brightness(c);
        }
        pushStyle();
        colorMode(HSB);
        colorSeleccionado = color(hSeleccion, sSeleccion, bSeleccion);
        popStyle();
      }
    }
  }

  boolean seleccionLista(float x, float y) {
    boolean listo = false;
    if ((x>pos.x-anchoContenedor/2 && x<pos.x+anchoContenedor/2 && y>pos.y-altoContenedor/2 && y<pos.y+altoContenedor/2)) {
      listo = false;
    } else {
      listo = true;
    }
    return listo;
  }

  color getColor() {
    return colorSeleccionado;
  }
}



class Paleta extends ElementoUI {

  color[][] matrizPaleta;
  boolean sensible;
  int indiceX; 
  int indiceY; 
  SelectorDeColor selectorDeColor;
  PVector[][] posicionesDeColores;
  float[] anchoDeColores;
  float[] altoDeColores;
  Paleta() {
    nombre = "Paleta";
    selectorDeColor = new SelectorDeColor();
    pos = new PVector(0, 0);
  }

  void setMatrizPaleta(color[][] matrizPaleta_) {
    matrizPaleta = matrizPaleta_;
    posicionesDeColores = new PVector[matrizPaleta.length][];
    anchoDeColores = new float[matrizPaleta.length];
    altoDeColores = new float[matrizPaleta.length];
    for (int i = 0; i<matrizPaleta.length; i++) {
      altoDeColores[i] = alto/matrizPaleta.length;
      anchoDeColores[i] = ancho/matrizPaleta[i].length;
      posicionesDeColores[i] = new PVector[matrizPaleta[i].length];
      for (int j=0; j<matrizPaleta[i].length; j++) {
        posicionesDeColores[i][j] = new PVector(pos.x+(j)*anchoDeColores[i]-ancho/2, pos.y+(i)*altoDeColores[i]-alto/2);
      }
    }
  }


  void dibujar() {   
    dibujarBase();
    dibujarSelector();
  }

  void dibujarBase() {
    pushStyle();
    rectMode(CORNER);
    for (int i=0; i<matrizPaleta.length; i++) {     
      for (int j=0; j<matrizPaleta[i].length; j++) {
        fill(matrizPaleta[i][j]);
        stroke(matrizPaleta[i][j]);
        rect(posicionesDeColores[i][j].x, posicionesDeColores[i][j].y, anchoDeColores[i], altoDeColores[i]);
      }
    }
    popStyle();
  }

  void setPosiciones() {
    float altoCuadro = alto/matrizPaleta.length;
    for (int i=0; i<matrizPaleta.length; i++) {
      float anchoCuadro = ancho/matrizPaleta[i].length;
      for (int j=0; j<matrizPaleta[i].length; j++) {
        fill(matrizPaleta[i][j]);
        stroke(matrizPaleta[i][j]);
        rect(pos.x+(j)*anchoCuadro-ancho/2, pos.y+(i)*altoCuadro-alto/2, anchoCuadro, altoCuadro);
      }
    }
  }

  void dibujarSelector() {
    if (sensible) {
      selectorDeColor.dibujar();
    }
  }

  void mouseDrag(float x, float y) {
    selectorDeColor.mousePressed(x, y);

    matrizPaleta[indiceX][indiceY] = selectorDeColor.getColor();
    selectorDeColor.setContenidoDeInputs(matrizPaleta[indiceX][indiceY]);
  }

  void mousePressed(float x, float y) {
    if (!sensible) {
      if (x>pos.x-ancho/2 && x<pos.x+ancho/2 &&
        y>pos.y-alto/2 && y<pos.y+alto/2) {
        sensible = true;

        for (int i=0; i<matrizPaleta.length; i++) {     
          for (int j=0; j<matrizPaleta[i].length; j++) {
            if (x>posicionesDeColores[i][j].x && x<posicionesDeColores[i][j].x+anchoDeColores[i] &&
              y>posicionesDeColores[i][j].y && y<posicionesDeColores[i][j].y+altoDeColores[i]) {
              indiceX = i;
              indiceY = j;     
              float xP = 0;
              float yP = 0;
                         
              if (x>pos.x) {
                xP =x-ancho/3;
              } else {
                xP =x+ancho/3;
              } 

              if (y>pos.y) {        
                yP = y-ancho/6;
              } else {
                yP = y+ancho/6;
              }
              
               selectorDeColor.setPos(new PVector(xP, yP));
               
              selectorDeColor.setTam(ancho/3);
              selectorDeColor.setContenidoDeInputs(matrizPaleta[indiceX][indiceY]);
            }
          }
        }
      }
    } else {
      if (selectorDeColor.seleccionLista(x, y)) {
        sensible = false;
      } else {
        selectorDeColor.mousePressed(x, y);
        matrizPaleta[indiceX][indiceY] = selectorDeColor.getColor();
        selectorDeColor.setContenidoDeInputs(matrizPaleta[indiceX][indiceY]);
      }
    }
  }

  void keyPressed(int keyCode, char key) {
    if (sensible) {
      selectorDeColor.keyPressed(keyCode, key);
      matrizPaleta[indiceX][indiceY] = selectorDeColor.getColor();
    }
  }
  color[][] getPaleta() {
    return matrizPaleta;
  }
}
class Input extends ElementoUI {

  boolean sensible;
  boolean pleca;
  String texto = "";
  int selector;
  boolean limitado;
  int limite = 0;
  boolean cerrado = false;
  String etiqueta;


  Input() {
  }

  void setEtiqueta(String etiqueta_) {
    etiqueta = etiqueta_;
  }
  void setLimite(int limite_) {
    limitado = true;
    limite = limite_;
  }

  void setSinLimite() {
    limitado = false;
  }

  void dibujar() {
    dibujarBase();
    dibujarTexto();
     
  }

  void dibujarBase() {
    pushStyle();
    strokeWeight(1);
    fill(127, 50);
    noStroke();
    rectMode(CENTER);
    float radio = alto/2;
     textSize(alto/100*80);
    if (cerrado) {
      stroke(255);
      ellipse(pos.x, pos.y, alto, alto);
      noStroke();
      fill(255);
      ellipse(pos.x, pos.y, radio, radio);
      textAlign(CENTER, CENTER);
     
      stroke(110);
      text(nombre, pos.x, pos.y);
    } else {
      float anchoTexto = alto/100*60*(texto.length()+1);
      textAlign(RIGHT, CENTER);
      if (ancho<anchoTexto) {
        rect(pos.x, pos.y, anchoTexto-radio*2, alto);
        ellipse(pos.x-anchoTexto/2+radio, pos.y, radio, radio);
        ellipse(pos.x+anchoTexto/2-radio, pos.y, radio, radio);
        fill(127, 50);
        arc(pos.x-anchoTexto/2+radio, pos.y, radio*2, radio*2, HALF_PI, HALF_PI+PI, OPEN);
        arc(pos.x+anchoTexto/2-radio, pos.y, radio*2, radio*2, HALF_PI+PI, HALF_PI+TWO_PI, OPEN);
        line(pos.x-(anchoTexto-radio*2)/2, pos.y-radio, pos.x+(anchoTexto-radio*2)/2, pos.y-radio);
        line(pos.x-(anchoTexto-radio*2)/2, pos.y+radio, pos.x+(anchoTexto-radio*2)/2, pos.y+radio);
        fill(255);
        text(etiqueta+": ", pos.x-anchoTexto/2, pos.y);
      } else {
        rect(pos.x, pos.y, ancho-radio*2, alto);
        fill(255);
        ellipse(pos.x-ancho/2+radio, pos.y, radio, radio);
        ellipse(pos.x+ancho/2-radio, pos.y, radio, radio);
        fill(127, 50);
        stroke(255);
        arc(pos.x-ancho/2+radio, pos.y, radio*2, radio*2, HALF_PI, HALF_PI+PI, OPEN);
        arc(pos.x+ancho/2-radio, pos.y, radio*2, radio*2, HALF_PI+PI, HALF_PI+TWO_PI, OPEN);
        line(pos.x-(ancho-radio*2)/2, pos.y-radio, pos.x+(ancho-radio*2)/2, pos.y-radio);
        line(pos.x-(ancho-radio*2)/2, pos.y+radio, pos.x+(ancho-radio*2)/2, pos.y+radio);
        fill(255);
        text(etiqueta+": ", pos.x-ancho/2, pos.y);
      }
    }
    popStyle();
  }

  void dibujarTexto() {
    pushStyle();
    if (sensible) {
      if (frameCount % 20 == 0) { 
        pleca = !pleca;
      }
    } else {
      pleca = false;
    }

    selector = int(constrain(selector, 0, texto.length()));
    String texto1=texto.substring(0, selector);
    String texto2="";

    if (texto.length()>1) {
      if (selector<texto.length()) {
        texto2=texto.substring(selector+1, texto.length());
      }
    } 

    fill(255);
    textAlign(CENTER, CENTER);
   
    textSize(alto/100*80);
    float anchoTexto = alto/100*60*(texto.length()+1);
    if (pleca) {
     
      text(texto1 + "|" + texto2, pos.x, pos.y);
    } else {
 
      text(texto1 + "" + texto2, pos.x, pos.y);
    }
    popStyle();
  }

  void keyPressed(int keyCode, char key) {
    if (key==CODED) {
      if (keyCode==LEFT) {
        selector--;
      } else if (keyCode == RIGHT) {
        selector++;
      } else {
        // message
        println ("tecla especial sin funcion");
      }
    } else {
      if (key==BACKSPACE) {
        if (texto.length()>0) {
          texto=texto.substring(0, texto.length()-1);
        }
      } else {
        if (sensible) {
          if (limitado) {
            if (texto.length()<limite) {
              selector++;
              texto+=key;
            }
          } else {
            selector++;
            texto+=key;
          }
        }
      }
    }
  }

  String getTexto() {
    return texto;
  }

  void setTexto(String texto_) {
    texto = texto_;
    if (texto.length()>limite)
      texto = texto.substring(0, limite);
    
  }

  boolean estaSeleccionado() {
    return sensible;
  }

  void mousePressed(float x, float y) {
    if (x>pos.x-ancho/2 && x<pos.x+ancho/2 &&
      y>pos.y-alto/2 && y<pos.y+alto/2) {
      sensible = true;
      selector = texto.length();
    } else {
      sensible=false;
      selector = texto.length();
    }
  }
}


class Boton extends ElementoUI {

  boolean sensible;

  Boton() {
  }

  void dibujar() {
    dibujarBase();
    dibujarTexto();
  }

  void dibujarBase() {
    pushStyle();
    if (sensible) {
      fill(0);
    } else {
      fill(30);
    }
    rect(pos.x, pos.y, ancho, alto);
    popStyle();
  }

  void dibujarTexto() {
    pushStyle();
    fill(0);
    textAlign(CENTER, CENTER);
    textSize(alto/100*80>ancho/nombre.length()?alto/100*80:ancho/nombre.length());
    text(nombre, pos.x+ancho/2, pos.y+alto/2);
    popStyle();
  }

  void mouseRevisar(float x, float y) {
    if (x>pos.x && x<pos.x+ancho &&
      y>pos.y && y<pos.y+alto) {
      sensible = true;
    } else {
      sensible=false;
    }
  }

  boolean mousePressed() {
    boolean activar = false; 
    if (sensible)
      activar = true;
    return activar;
  }
}


class ElementoUI {

  PVector pos;
  float ancho;
  float alto;
  String nombre;


  ElementoUI() {
    pos = new PVector(0,0);
  }

  void setPos(PVector pos_) {
    pos.set(pos_.x,pos_.y);
  }
  void setSize(float ancho_, float alto_) {
    ancho = ancho_;
    alto = alto_;
  }
  
   void setSize(float t) {
    ancho = t;
    alto = t;
  }
  void setNombre(String nombre_) {
    nombre = nombre_;
  }
  
  String getNombre() {
    return nombre;
  }
}
