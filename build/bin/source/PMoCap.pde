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
 
 PImage imagenAnalisis() - devuelve una PImage con la imagen bitonal y el analisis del movimiento
 
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
  boolean comparaConFondo;

  // objetos PImage para almacenar el fondo actual de comparación
  PImage imagenAcomparar;
  // objetos PImage para almacenar el fondo fijo de para todo el proceso
  PImage fondoFijo;
  // objetos PImage para almacenar el resultado de la substracción
  PImage substraccion;
  // objetos PImage para almacenar el resultado del filtro bitonal
  PImage bitonal;

  // variable del valor de umbral del filtro bitonal
  float umbral;

  // variables que indican las posiciones límites del área de movimiento
  protected int arriba, abajo, derecha, izquierda;
  // area del movimiento
  protected int area;
  // X e Y del centro de gravedad del área del movimiento
  protected float x, y;
  // X e Y del centro del Bounding Box
  protected float xBoundingBox, yBoundingBox;

  // buffer de almacenamiento de imágenes
  BufferVideo retardo;  

  //-------------------------------
  //constructor de la clase
  PMoCap( int ancho_, int alto_, float umbral_, int fotogramasRetardo, boolean comparaConFondo_ ) {

    //se inician las propiedades con los parametros
    comparaConFondo = comparaConFondo_;
    ancho = ancho_;
    alto = alto_;
    largo = ancho*alto;
    umbral = umbral_;

    //indica que aun no hay ningún fondo captado
    fondoTomado = false;

    //inicializa el buffer
    retardo = new BufferVideo( ancho, alto, fotogramasRetardo );

    //inicializa los objetos PImage
    fondoFijo = createImage( ancho, alto, RGB );
    substraccion = createImage( ancho, alto, RGB );
    bitonal = createImage( ancho, alto, RGB );
  }
  //---------------------------------------------------
  // método de captura
  void capturar( PImage entrada ) {

    // se decide en función del método de captura "compara con fondo" 
    // o "compara con fotograma anterior"
    if ( !comparaConFondo ) {
      // siendo el método "comparar con un fotograma anterior"
      // carga la nueva imagen en el buffer
      retardo.cargar( entrada );
      // toma una imagen del buffer como fondo actual para comparar
      imagenAcomparar = retardo.punteroPrimero();
    }
    else {
      //cuando el método es "comparar con fondo" 
      if ( !fondoTomado ) {
        //si aun no se a tomado un fondo, se toma la imagen actual como nuevo fondo fijo
        fondoFijo.copy( entrada, 0, 0, ancho, alto, 0, 0, ancho, alto);
        //se indica que el fondo fijo ha sido tomado
        fondoTomado = true;
      }
      //tomar la imagen del fondo fijo como imagen para comparar
      imagenAcomparar = fondoFijo;
    }

    //propara las dos imagenes para leer sus pixeles
    entrada.loadPixels();
    imagenAcomparar.loadPixels();

    //inicializa las variables
    hayMovimiento = false;
    area = 0;
    x = 0;
    y = 0;

    int posx, posy;
    long totx = 0;
    long toty = 0;

    //usa el ciclo for para recorrer los pixeles de las imágenes
    for ( int i=0 ; i<largo ; i++ ) {

      //obtiene la posicion en X e Y en función del orden del pixel
      posx = i % ancho;
      posy = i / ancho;

      //toma el color del pixel i-ésimo de cada una de las imágenes
      color actual = entrada.pixels[i];
      color delFondo = imagenAcomparar.pixels[i];

      // oftiene la diferencia (absoluta, es decir en valor positivo) de cada uno de los componentes
      // color: rojo, verde y azul
      float difRed = abs( red(actual) - red(delFondo) );
      float difGreen = abs( green(actual) - green(delFondo) );
      float difBlue = abs( blue(actual) - blue(delFondo) );
      // obtiene la diferencia promedio
      float gris = ( difRed + difGreen + difBlue ) / 3.0;

      //carga el resultado de la substracción en la imagen para tal fin
      substraccion.pixels[i] = color( gris, gris, gris );

      //si la diferencia supera el valor de umbral, se lo considera movimiento
      boolean conMovimiento = gris>umbral;

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
      }
      else {
        // si no hubo movimiento el pixel de la imagen bitonal será negro
        bitono = color(0, 0, 0);
      }
      //pinta el pixel de la imagen bitonal
      bitonal.pixels[i] = bitono;
    }
    if ( area>0 ) {
      x = totx / area;
      y = toty / area;
      xBoundingBox = izquierda + (derecha-izquierda) * 0.5;
      yBoundingBox = arriba + (abajo-arriba) * 0.5;
    }

    //actualiza los pixeles de las imagenes resultantes de la substracción y el filtro bitonal
    substraccion.updatePixels();
    bitonal.updatePixels();
    //actualizamos entrada nomas para que podamos dibujar el PImage en un metodo image() - hgm
    entrada.updatePixels();
  }
  //---------------------------------------------------
  // sobrecarga del método con parámetro del tipo Capture
  void capturar( Capture entrada ) {
    capturar( (PImage) entrada );
  }

  //-------------------------------
  // método para volver a tomar una imagen de fondo
  void recapturarFondo() {
    fondoTomado = false;
  }
  //-------------------------------
  // método para construir la imagen resultante del análisis del movimiento
  PImage getImagenAnalisis() {

    //se inician el grafico y la imagen donde se devolverá el resultado
    PGraphics grafico = createGraphics( ancho, alto, P2D );
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
      grafico.stroke( paleta.rojo );
      grafico.noFill();
      grafico.ellipse(x, y, 5, 5);
      grafico.rect( xBoundingBox, yBoundingBox, 5, 5 );
      grafico.rectMode( CORNERS );
      grafico.rect( izquierda, arriba, derecha, abajo );
    }
    // se cierra el dibujo
    grafico.endDraw();
    //se copia el grafico a la imagen
    resultado.copy( grafico, 0, 0, ancho, alto, 0, 0, ancho, alto );

    return resultado;
  }

  //-------------------------------
  // método que responde si hay movimiento en un pixel determinado
  // y dibuja el movimiento con el pixel en cuestion
  boolean movEnPixel( int x, int y, PImage imagen ) {

    boolean valor = blue( bitonal.get( x, y ) ) > 127;
    int margen = 5;

    if ( imagen != null ) {
      color c = ( valor ? color(0, 255, 0) : color(255, 0, 0) );

      for ( int i = max(0,x-margen) ; i<min(x+margen,ancho) ; i++ ) {
        imagen.set( i, y, c );
      }
      for ( int i = max(0,y-margen) ; i<min(y+margen,alto) ; i++ ) {
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

    for ( int i=0 ; i<ancho_ ; i++ ) {
      for ( int j=0 ; j<alto_ ; j++ ) {
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

      for ( int i=x ; i<x+ancho_ ; i++ ) {
        imagen.set( i, y, c );
        imagen.set( i, y+alto_-1, c );
      }

      for ( int i=y ; i<y+alto_ ; i++ ) {
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
    for ( int i=0 ; i<cantidad ; i++ ) {
      buffer[i] = createImage( ancho, alto, RGB );
    }
  }
  //-------------------------------

  void cargar( Capture imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
    cabeza = ( cabeza+1 ) % cantidad;
  }
  //-------------------------------

  void cargar( PImage imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
    cabeza = ( cabeza+1 ) % cantidad;
  }
  //-------------------------------

  PImage punteroPrimero() {
    return buffer[ cabeza ];
  }
}
