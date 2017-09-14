import processing.video.*;
import controlP5.*;


//declara el objeto para acceder a la camara
Capture camara;

PMoCap movimiento;

int ancho = 320;
int alto = 240;

float umbral = 180;
float umbralCerrado = 0.2;
float umbralNivel = 0.6f;
float umbralEje = 10f;
PImage[] imagenesPrueba = new PImage[9];
boolean test = false;
UI ui;

UsuarioNivel uNivel = new UsuarioNivel();
UsuarioCerrado uCerrado = new UsuarioCerrado();
UsuarioDesequilibrio uDeseq = new UsuarioDesequilibrio();

ComunicacionOSC osc;
void setup() {
  size( 840, 600 );
  osc = new ComunicacionOSC(this);
  if (test) {
    for (int i=0; i<imagenesPrueba.length; i++) {
      imagenesPrueba[i] = loadImage("prueba"+(i+1)+".png");
      int h = 240;
      int w = int(imagenesPrueba[i].width*240/imagenesPrueba[i].height);
      imagenesPrueba[i].resize(w, h);
      imagenesPrueba[i].loadPixels();

      for (int j = 0; j < imagenesPrueba[i].pixels.length; j++) {
        imagenesPrueba[i].pixels[j] = alpha(imagenesPrueba[i].pixels[j])>50 && brightness(imagenesPrueba[i].pixels[j])<200?color(0):color(255);
      }

      PGraphics pG = createGraphics(320, 240);
      pG.beginDraw();
      pG.background(255);
      pG.imageMode(CENTER);
      pG.image(imagenesPrueba[i], pG.width/2, pG.height/2);
      pG.endDraw();
      imagenesPrueba[i] = pG.copy();
      //se indica que el fondo fijo ha sido tomado
    }
  }
  //listarCamaras();
  //inicializa la camara
  camara = new Capture(this, ancho, alto );

  //enciende la camara
  camara.start();

  float umbral = 50;
  int cantidadDeCuadrosDeRetardo = 2;
  // boolean metodoConFondoDeReferencia = true;

  movimiento = new PMoCap( this, ancho, alto, umbral, 
    cantidadDeCuadrosDeRetardo, 7 );

  ui = new UI(this, width-150, height/2, 20);

  ui.crearSlider("umbralCerrado", 0, 0.3, umbralCerrado);
  ui.crearSlider("umbralNivel", 0, 1, umbralNivel);
  ui.crearSlider("umbralEje", 0, 100, umbralEje);
}
int cual;
void draw() {
  //----LOS UMBRALES DEBERIAN SER DINAMICOS EN REFERENCIA AL TAMA:O DEL BOUNDING BOX 
  //----ASI SE SOSTENDRIAN LOS UMBRALES ISN IMPORTAR SI LA PERSONA ESTA MUY ATRAS O ADELANTE.
  umbralCerrado = ui.cp5.getController("umbralCerrado").getValue();
  umbralNivel = ui.cp5.getController("umbralNivel").getValue();
  umbralEje = ui.cp5.getController("umbralEje").getValue();
  //consulta si existe un nuevo fotograma
  if ( camara.available() ) {
    background(0);
    //lee el nuevo fotograma
    camara.read();
    //ejecuta la substraccion de video
    //
    if (test) {
      movimiento.capturar( imagenesPrueba[cual] );
    } else {
      movimiento.capturar( camara );
    }

    PImage resultado = movimiento.getImagenAnalisis();
    image( camara, 0, 0 );
    image( movimiento.imagenesAcomparar[1], ancho, 0 );
    image( movimiento.substraccion, 0, alto );
    //image( movimiento.bitonal, ancho, 0 );
    image(resultado, ancho, alto );
    osc.enviarMensajesAPI(movimiento);
    pushStyle();
    fill(0);
    rect(0, alto*2, alto, ancho);
    fill(255);
    textSize(50);
    text("nivel: "+movimiento.getNivel(umbralNivel), 10, alto*2+10 );
    //text("eje: "+movimiento.getDesequilibrio(umbralEje), 10, alto*2+10+15 );
    text("abierto: "+movimiento.getCerrado(umbralCerrado), 200, alto*2+10);
    popStyle();

    movimiento.conteo();
  }
}
void mousePressed() {
  //movimiento.recapturarFondo();
}

void keyPressed() {
  if (test) {
    cual++;
    if (cual>imagenesPrueba.length-1)
      cual = 0;
  }
}

public void nuevofondo(int theValue) {
  println("a button event from colorC: "+theValue);
  movimiento.recapturarFondo();
}