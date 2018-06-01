//---------------------------------------------
// version 1.10 actualizada al 24/Nov/2013
// clase Processing Motion Capture
// Emiliano Causa @2011
// emiliano.causa@gmail.com
//---------------------------------------------
/*
============ PMoCap =========================================================================
 
 constructor: PMoCap( int ancho_, int alto_, float umbral_, int fotogramasRetardo, boolean comparaConFondo_ )
 
 ancho y alto: dimensiones del cuadro
 umbral: es el umbral de diferencia entre (0,255).
 fotogramasRetardo: cuadros de retardo para hacer la captura.
 comparaConFondo: true -> la substraccion se hace con un fondo fijo
 false -> la substraccion se hace contra un cuadro retardado
 
 void capturar( Capture entrada ) - hace el proceso de captura de movimiento
 
 void recapturarFondo() - toma el siguiente cuadro como fondo fijo para la substraccion
 
 PImage getalisis() - devuelve una PImage con la imagen bitonal y el analisis del movimiento
 
 boolean movEnPixel( int x, int y ) - verifica si hay movimiento en el pixel (x,y)
 boolean movEnPixel( int x, int y, PImage imagen ) - idem. pero sobreimprime el resultado en el PImage pasado como parametro
 
 float movEnArea( int x, int y, int ancho_, int alto_ ) - devuelve el porcentaje normalizado de movimiento dentro de un area rectangular
 float movEnArea( int x, int y, int ancho_, int alto_, PImage imagen ) - idem. pero sobreimprime el resultado en el PImage pasado como parametro
 
 variables:
 .area = superficie del movimiento
 .x e .y = posicion del centro de gravedad del movimiento
 .arriba .abajo .derecha .izquierda = limites del area de movimiento
 .imagenAcomparar = PImage del imagen para la substraccion
 .substraccion = PImage de la substraccion para la substraccion
 .bitonal = PImage resultado de la substraccion pasada por el umbral
 
 ======== BufferVideo ========================================================================
 
 constructor:
 BufferVideo( int ancho_, int alto_, int cantidad_ )
 ancho y alto: dimensiones
 cantidad: cantidad de fotogramas de la secuencia (tiempo del retardo)
 
 void cargar( Capture imagen ) - carga una nueva imagen.
 
 PImage punteroPrimero() - muestra el primer fotograma que debe salir de la espera.
 
 */
class PMoCap {

  // ancho y alto de la imagen para procesar
  int ancho, alto;
  // cantidad de pixeles de la imagen
  int largo;
  // variable que indica si existe actualmente un fondo captado
  boolean fondoTomado;
  // variable que indica si existe movimiento en el fotograma actual
  boolean hayMovimiento;
  // variable que indica el tipo de captura, "compara con fondo" o "compara con fotograma anterior"
  //boolean comparaConFondo;

  // objetos PImage para almacenar el fondo actual de comparación
  PImage[] imagenesAcomparar = new PImage[2];
  // objetos PImage para almacenar el fondo fijo de para todo el proceso
  PImage fondoFijo;
  // objetos PImage para almacenar el resultado de la substracción
  PImage substraccion;
  // objetos PImage para almacenar el resultado del filtro bitonal
  PImage bitonal;

  // variable del valor de umbral del filtro bitonal
  float umbral;

  // variables que indican las posiciones límites del área de movimiento
  int arriba, abajo, derecha, izquierda;
  // area del movimiento
  int area;
  // X e Y del centro de gravedad del área del movimiento
  float x, y;
  //X e Y del centro del bounding box
  float xBoundingBox, yBoundingBox;
  // variables de ancho y alto del bounding box
  int anchoBoundingBox, altoBoundingBox;
  int convolucion;
  // X e Y del centro de gravedad del área del movimiento
  VerticeDeControl[] verticesDeControl = new VerticeDeControl[16];

  // buffer de almacenamiento de imágenes
  BufferVideo retardo;  
  int fotogramasRetardo;
  float blend;
  //UI
  UI ui;
  //-------------------------------
  //constructor de la clase
  PMoCap(PApplet p5, int ancho_, int alto_, float umbral_, int fotogramasRetardo_, /*boolean comparaConFondo_,*/ int convolucion_) {
    convolucion = convolucion_;


    ui = new UI(p5, width-150, 0, 20);
    ui.crearSlider("convolucion", 3, 10, convolucion_);
    //se inician las propiedades con los parametros
    //comparaConFondo = comparaConFondo_;
    ancho = ancho_;
    alto = alto_;
    largo = ancho*alto;

    umbral = umbral_;
    ui.crearSlider("umbral", 0, 255, umbral_);
    fotogramasRetardo = fotogramasRetardo_;
    ui.crearSlider("fotogramasRetardo", 1, 120, fotogramasRetardo_);
    //indica que aun no hay ningún fondo captado
    fondoTomado = false;

    //inicializa el buffer
    retardo = new BufferVideo( ancho, alto, fotogramasRetardo_ );

    //inicializa los objetos PImage
    fondoFijo = createImage( ancho, alto, RGB );
    substraccion = createImage( ancho, alto, RGB );
    bitonal = createImage( ancho, alto, RGB );

    ui.crearSlider("blend", 0, 0.2, fotogramasRetardo_);

    ui.cp5.addButton("nuevofondo")
      //.setValue(0)
      .setPosition(width-150, height-100)
      .setSize(100, 19)
      ;
  }


  //---------------------------------------------------
  // método de captura

  float sqDif(float y, float y_, float limite) {
    return map(sq(y-y_), 0, sq(limite), 0, limite);
  }

  void actualizar() {
    convolucion = int(ui.cp5.getController("convolucion").getValue());
    if (convolucion%2==0)
      convolucion++;
    fotogramasRetardo = int(ui.cp5.getController("fotogramasRetardo").getValue());
    umbral = int(ui.cp5.getController("umbral").getValue());
    blend = ui.cp5.getController("blend").getValue();
  }

  float cont = 3;
  float decremento = 0.1;

  void capturar( PImage entrada ) {
    actualizar();
    //println(convolucion);
    // se decide en función del método de captura "compara con fondo" 
    // o "compara con fotograma anterior"
    //if ( !comparaConFondo ) {
    // siendo el método "comparar con un fotograma anterior"
    // carga la nueva imagen en el buffer
    retardo.cargar( entrada );
    // toma una imagen del buffer como fondo actual para comparar
    imagenesAcomparar[0] = retardo.punteroPrimero();
    //} else {
    //cuando el método es "comparar con fondo" 
    if ( !fondoTomado ) {
      if (cont <= 0) {
        if (test) {
          //---- esto es para crear un fondo blanco para testear
          fondoFijo.loadPixels();
          for (int i = 0; i < fondoFijo.pixels.length; i++) {
            fondoFijo.pixels[i] = color(255);
          }
          //se indica que el fondo fijo ha sido tomado
          fondoTomado = true;
        } else {
          //si aun no se a tomado un fondo, se toma la imagen actual como nuevo fondo fijo
          fondoFijo.copy( entrada, 0, 0, ancho, alto, 0, 0, ancho, alto);

          //se indica que el fondo fijo ha sido tomado
          fondoTomado = true;
        }
      } else {
        cont-=decremento;
        //println(cont);
      }
    }
    //tomar la imagen del fondo fijo como imagen para comparar
    blendFondo(entrada, blend);
    imagenesAcomparar[1] = fondoFijo;
    //}

    //propara las dos imagenes para leer sus pixeles
    entrada.loadPixels();
    imagenesAcomparar[0].loadPixels();
    imagenesAcomparar[1].loadPixels();

    //inicializa las variables
    hayMovimiento = false;
    area = 0;
    x = 0;
    y = 0;

    int posx, posy;
    long totx = 0;
    long toty = 0;

    //usa el ciclo for para recorrer los pixeles de las imágenes
    int inicio = (convolucion-1)/2;
    int aumento = 3;
    for ( int x=inicio; x<ancho-inicio; x+=aumento) {
      for ( int y=inicio; y<alto-inicio; y+=aumento) {

        //obtiene la posicion en X e Y en función del orden del pixel
        int i = y*ancho+x;
        posx = i % ancho;
        posy = i / ancho;
        float dif = getConv_dif(entrada, imagenesAcomparar[1], i, convolucion) ;
        setConv(substraccion, i, aumento, color(dif));
        //substraccion.pixels[i] = color(dif);
        //si la diferencia supera el valor de umbral, se lo considera movimiento

        boolean conMovimiento = (dif>umbral);

        //variable de color para la image bitonal
        color bitono;

        //si hay movimiento en este pixel
        if ( conMovimiento ) {

          //el pixel de la image bitonal sera blanco
          bitono = color(255, 255, 255);

          //si no había movimiento en los pixeles anteriores
          if ( !hayMovimiento ) {
            //marca la existencia de movimiento
            hayMovimiento = true;
            //inicia los valores de los bordes del área de movimiento
            abajo = arriba = posy;
            derecha = izquierda = posx;
          }
          //actualiza los valores de los bordes del área de movimiento si
          //la posición es de un borde
          abajo = max( abajo, posy );
          arriba = min( arriba, posy );
          derecha = max( derecha, posx );
          izquierda = min( izquierda, posx );
          //suma la posición para obtener la posición promedio
          totx += posx;
          toty += posy;
          //contabiliza el nuevo pixel en el área de movimiento
          area ++;
        } else {
          // si no hubo movimiento el pixel de la imagen bitonal será negro
          bitono = color(0, 0, 0);
        }
        //pinta el pixel de la imagen bitonal
        setConv(bitonal, i, aumento, bitono);
        //bitonal.pixels[i] = bitono;
      }
    }
    //el alncho y alto dle bounding box
    anchoBoundingBox = abs(izquierda-derecha);
    altoBoundingBox = abs(abajo-arriba);

    xBoundingBox = derecha-anchoBoundingBox/2; 
    yBoundingBox = abajo-altoBoundingBox/2;
    if ( area>0 ) {
      x = totx / area;
      y = toty / area;
    }

    //actualiza los pixeles de las imagenes resultantes de la substracción y el filtro bitonal
    substraccion.updatePixels();
    bitonal.updatePixels();


    verticesDeControl[0] = new VerticeDeControl(izquierda, arriba, 45);
    for (int i=1; i<4; i++) {
      verticesDeControl[i] = new VerticeDeControl(izquierda+anchoBoundingBox/4*i, arriba, 90, 0.63f);
    }    
    verticesDeControl[4] = new VerticeDeControl(derecha, arriba, 45+90);
    for (int i=1; i<4; i++) {
      verticesDeControl[i+4] = new VerticeDeControl(derecha, arriba+altoBoundingBox/4*i, 90+90);
    }
    verticesDeControl[8] = new VerticeDeControl(derecha, abajo, 45+180);
    for (int i=1; i<4; i++) {
      verticesDeControl[i+8] = new VerticeDeControl(derecha-anchoBoundingBox/4*i, abajo, 90+180, 0.2f);
    }
    verticesDeControl[12] = new VerticeDeControl(izquierda, abajo, 45+270);
    for (int i=1; i<4; i++) {
      verticesDeControl[i+12] = new VerticeDeControl(izquierda, abajo-altoBoundingBox/4*i, 90+270);
    }

    for (int i=0; i<verticesDeControl.length; i++) {
      verticesDeControl[i].setControl(this, null);
    }
    setDistanciaPromedio();
    setDistanciaLateralPromedio();
  }

  void conteo() {
    if ( !fondoTomado ) {
      pushStyle();
      textAlign(CENTER, CENTER);
      textSize(50);
      fill(0, 255, 0);
      text(int(cont), width/2, height/2);
      popStyle();
    }
  }
  //---------------------------------------------------
  // sobrecarga del método con parámetro del tipo Capture
  void capturar( Capture entrada ) {
    capturar( (PImage) entrada );
  }

  void setConv(PImage deQue, int pixel, int t, int c) {
    if (t%2==0) {
      t++;
    }
    int mov = (t-1)/2;
    for (int i=(mov*-1); i<=mov; i++) {
      for (int j=(mov*-1); j<=mov; j++) {
        int indice = pixel+i*deQue.width+j;
        deQue.pixels[indice] = c;
      }
    }
  }

  float getConv_dif(PImage entrada, PImage imagenAcomparar, int cual, color t) {
    float difRed = 0;
    float difGreen = 0;
    float difBlue = 0;
    /*float difHue = 0;
     float difBri = 0;
     float difSat = 0;*/
    if (imagenAcomparar==null) {
      return 0;
    }
    if (t%2==0) {
      t++;
    }
    int mov = (t-1)/2;
    float cuantasSumas = 0;
    for (int i=(mov*-1); i<=mov; i++) {
      for (int j=(mov*-1); j<=mov; j++) {
        int indice = cual+i*entrada.width+j;
        color actual = entrada.pixels[indice];
        color delFondo = imagenAcomparar.pixels[indice];
        float importancia = convolucion2D(i, j, mov);
        difRed += abs( red(actual)-red(delFondo))*importancia;
        difGreen += abs( green(actual)-green(delFondo))*importancia;
        difBlue += abs( blue(actual)-blue(delFondo))*importancia;
        /* difBri += sqDif( brightness(actual), brightness(delFondo), 255)*importancia;
         difSat += sqDif( saturation(actual), saturation(delFondo), 255)*importancia;*/
        cuantasSumas+=importancia;
      }
    }
    difRed /= cuantasSumas;
    difGreen /= cuantasSumas;
    difBlue /= cuantasSumas;

    /*difBri /= cuantasSumas;
     difSat /= cuantasSumas;  */

    float difMaxima = max(max(difRed, difGreen), difBlue);
    float dif = difMaxima;
    return dif;
  }

  float convolucion2D(float x, float y, int rango) {
    x = map(x, 0, rango, 0, 3);
    y = map(y, 0, rango, 0, 3);
    float media = 0;
    float sigma = 1.0;
    float zx = (exp(-1*(pow(x-media, 2.0)/(2.0*pow(sigma, 2.0)))) * (1.0/(sigma*sqrt(2.0*PI))));
    float zy = (exp(-1*(pow(y-media, 2.0)/(2.0*pow(sigma, 2.0)))) * (1.0/(sigma*sqrt(2.0*PI))));
    float z = (zx+zy)/2;
    return map(z, 0, 0.3989423, 0, 1);
  }

  //-------------------------------
  // método para volver a tomar una imagen de fondo
  void recapturarFondo() {
    fondoTomado = false;
    cont = 3;
  }
  //-------------------------------
  // método para construir la imagen resultante del análisis del movimiento
  PImage getImagenAnalisis() {

    //se inician el grafico y la imagen donde se devolverá el resultado
    PGraphics grafico = createGraphics( ancho, alto );
    PImage resultado = createImage( ancho, alto, RGB );
    // en Processing 2.0
    // PGraphics grafico = createGraphics( ancho, alto );
    // PImage resultado = createImage( ancho, alto , RGB );

    //se inicia el dibujo del grafico
    grafico.beginDraw();
    //se dibuja la imagen bitonal
    grafico.image( bitonal, 0, 0 );
    //si hay movimiento
    if ( hayMovimiento ) {
      //se dibujan el centro y el borde del area de movimiento
      grafico.stroke(0, 255, 0);
      grafico.noFill();
      grafico.rectMode( CORNERS );
      grafico.ellipse(x, y, 5, 5);
      grafico.ellipse(xBoundingBox, yBoundingBox, 5, 5);
      grafico.line(x, y, xBoundingBox, yBoundingBox);
      grafico.rect( izquierda, arriba, derecha, abajo );
      grafico.text("Area: "+area, izquierda, arriba-7);
      grafico.text("Proporcion: "+anchoBoundingBox/constrain(altoBoundingBox, width, 0.01), izquierda, arriba-16);
      grafico.stroke(255, 255, 0);
      for (int i = 0; i<verticesDeControl.length; i++) {
        VerticeDeControl v = verticesDeControl[i];
        if (i==5 || i==15) {
          grafico.fill(255, 0, 0);
        } else if (i==6 || i==14) {
          grafico.fill(0, 255, 0);
        } else if (i==7 || i==13) {
          grafico.fill(0, 0, 255);
        } else {
          grafico.fill(255);
        }
        grafico.ellipse(v.posInicial.x, v.posInicial.y, 10, 10);
        grafico.line(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y);
        grafico.ellipse(v.posControl.x, v.posControl.y, 10, 10);
      }
    }
    // se cierra el dibujo
    grafico.endDraw();
    //se copia el grafico a la imagen
    resultado.copy( grafico, 0, 0, ancho, alto, 0, 0, ancho, alto );

    return resultado;
  }
  float distanciaPromedio = 0;
  void setDistanciaPromedio() {

    float divisorPromedio = 0;
    for (VerticeDeControl v : verticesDeControl) {
      distanciaPromedio += (dist(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y)*v.importancia);
      divisorPromedio += v.importancia;
    }
    distanciaPromedio = distanciaPromedio/divisorPromedio;
  }

  float distanciaLateralPromedio = 0;
  void setDistanciaLateralPromedio() {

    float divisorPromedio = 0;
    for (int i=4; i<8; i++) {
      VerticeDeControl v = verticesDeControl[i];
      distanciaLateralPromedio += (dist(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y)*v.importancia);
      divisorPromedio += v.importancia;
    }
    for (int i=12; i<16; i++) {
      VerticeDeControl v = verticesDeControl[i];
      distanciaLateralPromedio += (dist(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y)*v.importancia);
      divisorPromedio += v.importancia;
    }
    distanciaLateralPromedio = distanciaLateralPromedio/divisorPromedio;
  }
  float anchoEntreVerticesDeControl = 0;
  void setAnchoEntreVerticesDeControl() {
    float record = 0;
    float[] ancho = new float[3];
    VerticeDeControl v1_alto = verticesDeControl[5];
    VerticeDeControl v2_alto = verticesDeControl[15];
    ancho[0] = (abs(v1_alto.posControl.x - v2_alto.posControl.x));
    VerticeDeControl v1_medio = verticesDeControl[6];
    VerticeDeControl v2_medio = verticesDeControl[14];
    ancho[1] = (abs(v1_medio.posControl.x - v2_medio.posControl.x));
    VerticeDeControl v1_bajo = verticesDeControl[7];
    VerticeDeControl v2_bajo = verticesDeControl[13];
    ancho[2] = (abs(v1_bajo.posControl.x - v2_bajo.posControl.x));
    for (int i=0; i<3; i++) {
      if (record<ancho[i]) {      
        record = ancho[i];
      }
    }

    anchoEntreVerticesDeControl = record;
  }

  int getCerrado(float umbral) {
    int cerrado = 0;
    fill(255, 0, 0);
    text("este es el cerrado: "+ distanciaPromedio/altoBoundingBox, 50, 50);
    if (distanciaPromedio/altoBoundingBox>umbral) {
      cerrado = 1;
    }
    return cerrado;
  }
  int getSuperCerrado() {
    return getCerrado(0.08);
  }
  int getNivel(float umbral) {
    int nivel = 0;
    if (getSuperCerrado()==0) {
      setAnchoEntreVerticesDeControl();
    }
    //float altura = getCerrado(umbralCerrado)<1?anchoBoundingBox/max(0.001, altoBoundingBox):0.4*(anchoBoundingBox/max(0.001, altoBoundingBox))/(0.01*distanciaPromedio);
    float altura = anchoEntreVerticesDeControl/max(0.001, altoBoundingBox);
    String print = anchoEntreVerticesDeControl+" / "+max(0.001, altoBoundingBox)+" = "+altura;
    text(print, 0, height-50);
    //if (getCerrado(umbralCerrado)==0) {
    if (altura>umbral/1.5) {
      nivel = 1;
    } 
    if (altura>umbral) {
      nivel = 2;
    }
    /*} else {
     if (altura>umbral/1.5*1.2) {
     nivel = 1;
     } 
     if (altura>umbral*1.2) {
     nivel = 2;
     }
     }*/

    return nivel;
  }
  int getDesequilibrio(float umbral) {
    int eje = 0;
    if (distanciaPromedio>5) {
      //println(distanciaPromedio);
      //println(x-xBoundingBox);
      if (x-xBoundingBox>umbral) {
        eje = -1;
      } else if (x-xBoundingBox<umbral*-1) {
        eje = 1;
      }
    }
    return eje;
  }

  //-------------------------------
  // método que responde si hay movimiento en un pixel determinado
  // y dibuja el movimiento con el pixel en cuestion
  boolean movEnPixel( int x, int y, PImage imagen ) {

    boolean valor = blue( bitonal.get( x, y ) ) > 127;
    int margen = 5;

    if ( imagen != null ) {
      color c = ( valor ? color(0, 255, 0) : color(255, 0, 0) );

      for ( int i = max(0, x-margen); i<min(x+margen, ancho); i++ ) {
        imagen.set( i, y, c );
      }
      for ( int i = max(0, y-margen); i<min(y+margen, alto); i++ ) {
        imagen.set( x, i, c );
      }
    }

    return valor;
  }
  //-------------------------------
  //sobrecarga del método anterior
  boolean movEnPixel( int x, int y ) {
    return movEnPixel( x, y, null );
  }
  //-------------------------------
  // método que evalua la cantidad porcentual de movimiento en una región
  float movEnArea( int x, int y, int ancho_, int alto_, PImage imagen ) {

    float cuantos = 0;

    for ( int i=0; i<ancho_; i++ ) {
      for ( int j=0; j<alto_; j++ ) {
        int posx = x+i;
        int posy = y+j;
        boolean valor = blue( bitonal.get( posx, posy ) ) > 127;

        if ( valor ) {
          cuantos++;
          if ( imagen != null ) {
            imagen.set( posx, posy, color(0, 255, 0) );
          }
        }
      }
    }

    if ( imagen != null ) {

      color c = color(255, 0, 0);
      if ( cuantos>0 ) {
        c = color(0, 255, 0);
      }

      for ( int i=x; i<x+ancho_; i++ ) {
        imagen.set( i, y, c );
        imagen.set( i, y+alto_-1, c );
      }

      for ( int i=y; i<y+alto_; i++ ) {
        imagen.set( x, i, c );
        imagen.set( x+ancho_-1, i, c );
      }
    }
    return cuantos / ( ancho_ * alto_ );
  }
  //-------------------------------
  //sobrecarga del método anterior
  float movEnArea( int x, int y, int ancho_, int alto_ ) {
    return movEnArea( x, y, ancho_, alto_, null );
  }
  //-------------------------------

  int getArriba() {
    return arriba;
  };

  //-------------------------------

  int getAbajo() {
    return abajo;
  }
  //-------------------------------

  int getDerecha() {
    return derecha;
  }
  //-------------------------------

  int getIzquierda() {
    return izquierda;
  }
  //-------------------------------

  int getArea() {
    return area;
  }
  //-------------------------------

  float getX() {
    return x;
  }
  //-------------------------------

  float getY() {
    return y;
  }
  //-------------------------------

  float getUmbral() {
    return umbral;
  }
  //-------------------------------

  void setUmbral( float nuevoUmbral ) {
    umbral = nuevoUmbral;
  }
  //-------------------------------
  void blendFondo(PImage entrada, float factor2) {
    float escala = 1000;
    factor2 = factor2*escala;
    float factor1 = escala-factor2;

    if (frameCount%fotogramasRetardo == 0) {
      fondoFijo.loadPixels();
      entrada.loadPixels();
      /*PImage imagen = createImage(entrada.width, entrada.height, RGB);
       imagen.loadPixels();*/
      for (int i = 0; i < fondoFijo.pixels.length; i++) {
        float dif = brightness(entrada.pixels[i])- brightness(imagenesAcomparar[0].pixels[i]);
        /*int x = i%bitonal.width;
         int y = i/bitonal.width;*/
        if (dif<10) {
          float r = (red(fondoFijo.pixels[i])*escala*factor1+red(entrada.pixels[i])*escala*factor2)/(escala*escala);
          float g = (green(fondoFijo.pixels[i])*escala*factor1+green(entrada.pixels[i])*escala*factor2)/(escala*escala);
          float b = (blue(fondoFijo.pixels[i])*escala*factor1+blue(entrada.pixels[i])*escala*factor2)/(escala*escala);
          fondoFijo.pixels[i] = color(r, g, b);
        }
      }
      fondoFijo.updatePixels();
    }
    //se indica que el fondo
  }
}
//---------------------------------------------

class BufferVideo {

  PImage buffer[];
  int cantidad;
  int cabeza;
  int ancho, alto;

  //-------------------------------

  BufferVideo( int ancho_, int alto_, int cantidad_ ) {

    cantidad = cantidad_;
    cabeza = 0;
    ancho = ancho_;
    alto = alto_;

    buffer = new PImage[ cantidad ];
    for ( int i=0; i<cantidad; i++ ) {
      buffer[i] = createImage( ancho, alto, RGB );
    }
  }
  //-------------------------------

  void cargar( Capture imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
    //buffer[ cabeza ] = blendImage(buffer[ cabeza-1 ], imagen, 0.999999999, 0.0001);
    cabeza = ( cabeza+1 ) % cantidad;
  }
  //-------------------------------

  void cargar( PImage imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
    /*int indicePrevia = cabeza-1>0?cabeza-1:buffer.length-1;
     buffer[ cabeza ] = blendImage(buffer[ indicePrevia ], imagen, 0.7, 0.3);*/
    cabeza = ( cabeza+1 ) % cantidad;
  }
  //-------------------------------

  PImage punteroPrimero() {
    return buffer[ cabeza ];
  }
}