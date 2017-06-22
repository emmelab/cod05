class Modos {

  String modo;
  float tElipse;
  float tLineas;
  float tCuerpo;
  float tam;
  color[][] paleta;
  float centroX ;
  float centroY ;
  float strokeW = 5;
  boolean usarTexto = false;
  Modos() {
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    modo = ESPERA;
  }

  Modos(color[][] paleta_, PVector posCentral_, float tam_) {
    paleta = paleta_;
    modo = ESPERA;
    inicializar(posCentral_, tam_);
  }

  void inicializar(PVector posCentral, float tam_) {
    centroX = posCentral.x;
    centroY = posCentral.y;
    tam = tam_;
  }

  void base(float cX, float cY, float t) {
    tElipse = t/11*2;
    tLineas = t/5*2;
    tCuerpo = t/2*2;
    noStroke();
    fill(paleta[2][3]);      
    ellipse(cX, cY, t, t);
  }
   
  void espera(float cX, float cY, float t) {
    pushMatrix();
    fill(paleta[2][1]);
    stroke(paleta[2][3]); 
    strokeWeight(strokeW);
    translate(cX, cY);      
    ellipse( 0, 0, t, t);
    ellipse( 0-t*1.3, 0, t, t);
    ellipse( 0+t*1.3, 0, t, t);
    popMatrix();
  }
  void agregar(String nombre, float cX, float cY, float t) {
    //fill(paleta[2][4][3]);      
    //ellipse(centroX, centroY, ancho/2, ancho/2);
    pushStyle();
    stroke(paleta[2][1]);
    strokeWeight(strokeW);
    pushMatrix();
    translate(cX, cY);      
    line( t, 0, -t, 0);
    line( 0, t, 0, -t);
    popMatrix();
    popStyle();

    if (usarTexto) {
      fill(paleta[2][2]);
      text(nombre, cX, cY);
    }
  }
  void eliminar(String nombre, float cX, float cY, float t) {
    //fill(paleta[2][4][3]);
    //ellipse(centroX, centroY, ancho/2, ancho/2);      
    pushStyle();
    stroke(paleta[2][1]);
    strokeWeight(strokeW);
    pushMatrix();
    translate(cX, cY);
    rotate(QUARTER_PI);      
    line( -t, 0, t, 0);
    line( 0, t, 0, -t);
    popMatrix();
    popStyle();

    if (usarTexto) {       
      fill(paleta[2][2]);
      text(nombre, cX, cY);
    }
  }
  void estimulos(String nombre, float cX, float cY, float t) {
    pushStyle();
    stroke(paleta[2][1]);
    strokeWeight(strokeW);
    pushMatrix();
    translate(cX, cY);

    line( 0, (t/3)/3.5, 0, -(t/3)/2);
    line( 0, (t/3)/3.5, (t/3)/1.2, t/3);
    line( 0, (t/3)/3.5, -(t/3)/1.2, t/3);
    line( -(t/3), -(t/4)/20, (t/3), -(t/4)/20);
    ellipse( 0, -(t/3)/1.5, t/5, t/5 );
    popMatrix();
    popStyle();

    if (usarTexto) {       
      fill(paleta[2][2]);
      text(nombre, cX, cY);
    }
  }
  void opciones(String nombre, float cX, float cY, float t) {
    //fill(paleta[2][4][3]);
    //ellipse(centroX, centroY, ancho/2, ancho/2);
    pushMatrix();    
    pushStyle();
    noStroke();
    rectMode(CENTER);

    fill(paleta[2][1]);
    translate(cX, cY);   
    ellipse(0, 0, t*4, t*4);

    for (int i=0; i<8; i++) {
      rect(0, t*2, t, t) ;
      rotate(radians(360/8));
    }
    fill(paleta[2][3]);
    ellipse(0, 0, t*2, t*2);

    popStyle();
    popMatrix();

    if (usarTexto) {       
      fill(paleta[2][2]);
      text(nombre, cX, cY);
    }
  }

  void ejecutar(String nombre) {
    base(centroX, centroY, tam);
    fill(paleta[2][1]);
    textAlign(CENTER, CENTER);
    textSize(30);
    if (modo.equals(ESPERA)) {
      espera(centroX, centroY, tElipse) ;
    } else if (modo.equals(AGREGAR)) {
      agregar( nombre, centroX, centroY, tLineas) ;
    } else if (modo.equals(ELIMINAR)) {
      eliminar( nombre, centroX, centroY, tLineas) ;
    } else if (modo.equals(ESTIMULOS)) {
      estimulos( nombre, centroX, centroY, tCuerpo) ;
    } else if (modo.equals(OPCIONES)) {
      opciones( nombre, centroX, centroY, tElipse*0.9) ;
    } else {
      text(nombre, centroX, centroY);
    }
  }

  void setModo(String modo_) {
    modo = modo_;
  }

  String getModo() {
    return modo;
  }
}