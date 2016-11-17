class Monitor {
  color[]  colorsitos;
  color[][]  paleta;
  PVector centro;
  float tam;
  Monitor() {
    colorsitos = new color[5];
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }

  Monitor(color[][] paleta_, PVector centro_, float tam_) {
    colorsitos = new color[5];
    paleta = paleta_;
    centro = centro_;
    tam = tam_;
  }

  void cerrado(float a, float b, float t, int cerrado) {
    pushStyle();
    colorMode(RGB);
    float pI =  HALF_PI;
    float fI = PI+HALF_PI;
    float pD = PI+HALF_PI;
    float fD = TWO_PI+HALF_PI;
    if (cerrado!=1) {
      pI =  HALF_PI+0.2;
      fI = PI+HALF_PI-0.2;
      pD = PI+HALF_PI+0.2;
      fD = TWO_PI+HALF_PI-0.2;
    }
    strokeWeight(5);
    stroke(paleta[2][3]);
    noFill();
    arc(a, b, t*2, t*2, pI, fI);  
    arc(a, b, t*2, t*2, pD, fD);
    popStyle();
  }

  void niveles(float a, float b, float t, int nivel) {

    pushStyle();
    colorMode(RGB);
    for (int i= 0; i<3; i++) {
      colorsitos[i]= paleta[2][1];
    }
    colorsitos[nivel] = paleta[2][2];
    noStroke();
    fill(colorsitos[0], 150);
    //----------Nivel superior
    for (int i= 0; i<3; i++) {
      float x = a+t/2*cos(map(i, 0, 3, -QUARTER_PI, -PI));
      float y = b+t/2*sin(map(i, 0, 3, -QUARTER_PI, -PI));
      ellipse(x, y, t/4, t/4);
    }

    fill(colorsitos[1], 150);
    //----------Nivel medio
    for (int i= 0; i<2; i++) {
      float x=a+t/2*cos(map(i, 0, 1, 0, PI));
      float y=b+t/2*sin(map(i, 0, 1, 0, PI));
      ellipse(x, y, t/4, t/4);
    }
    ellipse(a, b, t/4, t/4);

    fill(colorsitos[2], 150);
    for (int i= 0; i<3; i++) {
      float x=a+t/2*cos(map(i, 0, 3, QUARTER_PI, PI));
      float y=b+t/2*sin(map(i, 0, 3, QUARTER_PI, PI));
      ellipse(x, y, t/4, t/4);
    }

    popStyle();
  }

  void eje(float a, float b, float t, int inclinacion) {
    pushStyle();
    float tam = t/4;
    colorMode(RGB);
    for (int i= 0; i<5; i++) {
      colorsitos[i] = paleta[2][1];
    }
    noStroke();
    if (inclinacion != 0 && inclinacion != 4) {
      colorsitos[inclinacion] = paleta[2][2];

      if (colorsitos[2]!=paleta[2][1]) {
        fill(colorsitos[2], 150);
        // -----------eje central
        for (int i= 0; i<2; i++) {
          float x=a+t/2*cos(map(i, 0, 1, HALF_PI, PI+HALF_PI));
          float y=b+t/2*sin(map(i, 0, 1, HALF_PI, PI+HALF_PI));
          ellipse(x, y, tam, tam);
        }
        ellipse(a, b, tam, tam);
      }
      if (colorsitos[3]!=paleta[2][1]) {
        fill(colorsitos[3], 150);
        // eje derecho
        for (int i= 0; i<3; i++) {
          float x=a+t/2*cos(map(i, 0, 2, QUARTER_PI, -QUARTER_PI));
          float y=b+t/2*sin(map(i, 0, 2, QUARTER_PI, -QUARTER_PI));
          ellipse(x, y, tam, tam);
        }
      }
      if (colorsitos[1]!=paleta[2][1]) {
        fill(colorsitos[1], 150);
        // -----------eje izquierdo
        for (int i= 0; i<3; i++) {
          float x=a+t/2*cos(map(i, 0, 2, PI-QUARTER_PI, PI+QUARTER_PI));
          float y=b+t/2*sin(map(i, 0, 2, PI-QUARTER_PI, PI+QUARTER_PI));
          ellipse(x, y, tam, tam);
        }
      }
      popStyle();
    } else {
      colorsitos[inclinacion] = paleta[2][3];

      if (colorsitos[2]!=paleta[2][1]) {
        fill(colorsitos[2], 150);
        // -----------eje central
        for (int i= 0; i<2; i++) {
          float x=a+t/2*cos(map(i, 0, 1, HALF_PI, PI+HALF_PI));
          float y=b+t/2*sin(map(i, 0, 1, HALF_PI, PI+HALF_PI));
          ellipse(x, y, tam, tam);
        }
        ellipse(a, b, tam, tam);
      }
      if (colorsitos[4]!=paleta[2][1]) {
        fill(colorsitos[4], 150);
        // eje derecho
        for (int i= 0; i<3; i++) {
          float x=a+t/2*cos(map(i, 0, 2, QUARTER_PI, -QUARTER_PI));
          float y=b+t/2*sin(map(i, 0, 2, QUARTER_PI, -QUARTER_PI));
          ellipse(x, y, tam, tam);
        }
      }
      if (colorsitos[0]!=paleta[2][1]) {
        fill(colorsitos[0], 150);
        // -----------eje izquierdo
        for (int i= 0; i<3; i++) {
          float x=a+t/2*cos(map(i, 0, 2, PI-QUARTER_PI, PI+QUARTER_PI));
          float y=b+t/2*sin(map(i, 0, 2, PI-QUARTER_PI, PI+QUARTER_PI));
          ellipse(x, y, tam, tam);
        }
      }
      popStyle();
    }
  }

  void seleccionEstimulo(float a, float b, float t, int seleccionEstimulo) {
    pushStyle();
    colorMode(RGB);
    float p =  PI+HALF_PI;
    float f = map(seleccionEstimulo, 0, 400, PI+HALF_PI, PI+HALF_PI+TWO_PI);

    /* if (cerrado!=1) {
     pI =  HALF_PI+0.2;
     fI = PI+HALF_PI-0.2;
     pD = PI+HALF_PI+0.2;
     fD = TWO_PI+HALF_PI-0.2;
     }*/
    strokeWeight(5);
    stroke(paleta[2][3]);
    noFill();
    arc(a, b, t*2.5, t*2.5, p, f);  
    //arc(a, b, t*2, t*2, pD, fD);
    popStyle();
  }

  void dibujarBase() {
    pushStyle();
    rectMode(CORNER);
    noStroke();
    fill(paleta[1][2]);
    rect(0, 0, width, height/5.3);

  

    popStyle();
  }
  void dibujar(int cerrado_, int nivel_, int eje_) {  
    dibujarBase();
    float t = width<height? width/17 : height/17;
    cerrado(width/2, height/10, t, cerrado_);
    niveles(width/2, height/10, t, nivel_);
    eje(width/2, height/10, t, eje_);
  }

  void dibujar(int cerrado_, int nivel_, int eje_, int seleccionEstimulo_) {  
    dibujarBase();
    float t = width<height? width/17 : height/17;
    cerrado(width/2, height/10, t, cerrado_);
    niveles(width/2, height/10, t, nivel_);
    eje(width/2, height/10, t, eje_);
    seleccionEstimulo(width/2, height/10, t, seleccionEstimulo_);
  }
}

