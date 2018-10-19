class Monitor {
  color[]  colorsitos;
  color[][]  paleta;
  PVector centro;
  float tam;
  Iconos iconos;
  Iconos iconoMarca;
  Monitor() {
    colorsitos = new color[5];
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    iconos = new Iconos(int(bdd.monitorDiametro));
    iconoMarca = new Iconos(int(bdd.monitorDiametro*20/100));
  }

  Monitor(color[][] paleta_, PVector centro_, float tam_) {
    colorsitos = new color[5];
    paleta = paleta_;
    centro = centro_;
    tam = tam_;
    iconos = new Iconos(int(tam*70/100));
    iconoMarca = new Iconos((int)tam);
  }

  void cerrado(float a, float b, float t, int cerrado) {
    pushStyle();
    /*colorMode(RGB);
     float pI =  HALF_PI;
     float fI = PI+HALF_PI;
     float pD = PI+HALF_PI;
     float fD = TWO_PI+HALF_PI;*/
    tint(paleta[2][3]);
    if (cerrado!=1) {
      iconos.dibujar(CERRADO, a, b);
      /* pI =  HALF_PI+0.2;
       fI = PI+HALF_PI-0.2;
       pD = PI+HALF_PI+0.2;
       fD = TWO_PI+HALF_PI-0.2;*/
    } else {
      iconos.dibujar(ABIERTO, a, b);
    }
    /* strokeWeight(5);
     stroke(paleta[2][3]);
     noFill();
     arc(a, b, t*2, t*2, pI, fI);  
     arc(a, b, t*2, t*2, pD, fD);
     popStyle();*/
  }

  void niveles(float a, float b, float t, int nivel) {

    /* pushStyle();
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
     
     popStyle();*/

    tint(paleta[2][2], 170);
    if (nivel==0) {
      iconos.dibujar(N_ALTO, a, b);
    } else if (nivel==1) {
      iconos.dibujar(N_MEDIO, a, b);
    } else if (nivel==2) {
      iconos.dibujar(N_BAJO, a, b);
    }
  }

  void eje(float a, float b, float t, int inclinacion) {
    /* pushStyle();
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
     }*/

    tint(paleta[2][2], 170);
    if (inclinacion==0) {
      tint(paleta[2][3], 170);
      iconos.dibujar(EJE_IZQUIERDA, a, b);
    } else if (inclinacion==1) {
      iconos.dibujar(EJE_IZQUIERDA, a, b);
    } else if (inclinacion==2) {
      iconos.dibujar(EJE_CENTRO, a, b);
    } else if (inclinacion==3) {
      iconos.dibujar(EJE_DERECHA, a, b);
    } else if (inclinacion==4) {
      tint(paleta[2][3], 170);
      iconos.dibujar(EJE_DERECHA, a, b);
    }
  }

  /* void seleccionEstimulo(float a, float b, float t, int seleccionEstimulo) {
   pushStyle();
   colorMode(RGB);
   float p =  PI+HALF_PI;
   float f = map(seleccionEstimulo, 0, 400, PI+HALF_PI, PI+HALF_PI+TWO_PI);
   
   
   strokeWeight(5);
   stroke(paleta[2][3]);
   noFill();
   arc(a, b, t*2.5, t*2.5, p, f);  
   //arc(a, b, t*2, t*2, pD, fD);
   popStyle();
   }*/

  void dibujarBase(float a, float b, float ancho, float alto) {
    pushStyle();
    rectMode(CORNER);
    noStroke();
    fill(paleta[1][2]);
    rect(a, b, ancho, alto);
    tint(paleta[1][3]);
    iconoMarca.dibujar(COD05_1, iconoMarca.w/1.5, alto/2); 
    iconos.dibujar(MONITOR_BASE, bdd.monitorX, bdd.monitorY);      
    popStyle();
  }

  void dibujar(int cerrado_, int nivel_, int eje_) {  
    dibujarBase(bdd.baseMonitorX, bdd.baseMonitorY, bdd.baseMonitorAncho, bdd.baseMonitorAlto);    
    cerrado(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, cerrado_);
    niveles(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, nivel_);
    eje(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, eje_);
  }
  //----Hacer Esto
  void dibujar_noKinect(int cerrado_, int nivel_, int eje_) {  
    dibujarBase(bdd.baseMonitorX, bdd.baseMonitorY, bdd.baseMonitorAncho, bdd.baseMonitorAlto);    
    cerrado(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, cerrado_);
    niveles(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, nivel_);
    eje(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, eje_);
  }

  /*void dibujar(int cerrado_, int nivel_, int eje_, int seleccionEstimulo_) {  
   dibujarBase(bdd.baseMonitorX, bdd.baseMonitorY,  bdd.baseMonitorAncho, bdd.baseMonitorAlto);
   float t = width<height? width/17 : height/17;
   cerrado(width/2, height/10, t, cerrado_);
   niveles(width/2, height/10, t, nivel_);
   eje(width/2, height/10, t, eje_);
   seleccionEstimulo(width/2, height/10, t, seleccionEstimulo_);
   }*/
}
