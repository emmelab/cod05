///////////////////////CONTROLES TECLADO
//--- flechas izquierda y derecha: para navegacion
//--- enter:  ingresar en un submenu o activar un modificador
//--- 'a' o 'A' : agregarModificador
//--- 's' o 'S' : quitarModificador

//--- 'r' o 'R' : reiniciarCarrete
//--- 'm' o 'M' : prederApagarControlesDeMouse
//--- 'q' o 'Q' : opciones
//--- 'i' o 'I' : imprimir los modificadores como una linea para el xml de las maquinarias
//--- ALT : cancelar

///////////////////////CONTROLES MOUSE
//--- navegar por los modificadores 
//--- dar click para agregar o quitar


//import controlP5.*; 
import oscP5.*;
import netP5.*;

int contadorCapturas = 0;

OscP5 oscP5;
NetAddress sistema;

//ControlP5 cp5;
color[][] paleta; 

//IpManager ipManager;

String ip = "127.0.0.1";
String puertoEnvio = "12100";
String puertoRecivo = "12000";


Consola consola;
Controlador controlador;
ArrayList<String> nombresModificadores;
ArrayList<String> nombresModificadoresExistentes;
ArrayList<String> nombresCategorias;
ArrayList<String> nombresMaquinarias;
ArrayList<String> nombresOpciones;
int cantidad;
int cantidadExistentes;

color colorBoton;
color lineaBoton;
color letras;
color letrasActivas;
color fondo;


PFont fuente;

boolean abajo = false;// variables de control para los niveles
boolean medio = false;// variables de control para los niveles
boolean alto = false;// variables de control para los niveles

boolean abierto = false;// variables de control para los abierto/cerrado
boolean cerrado = false;// variables de control para los abirto/cerrado

int cerradoValor = 1;
int desequilibrioValor = 2;
int nivelValor = 1;

boolean totales;
boolean existentes;
boolean maquinarias;

boolean conectadoConSistema = false;
boolean existeSistema = false;
UIcontrol uiControl;

BDD bdd;
void setup() {
  size(500, 600);
  bdd = new BDD();
  //frameRate(25);
  smooth();
  setPaleta();
  strokeCap(PROJECT);


  initOSC();

  uiControl = new UIcontrol(new PVector(0, 0), width, height, paleta);
  /*cp5 = new ControlP5(this);
   ipManager = new IpManager(cp5);
   ipManager.set();*/

  //fuente = loadFont("28DaysLater-48.vlw");
  //fuente = loadFont("Castellar-30.vlw");
  fuente = loadFont("Consolas-48.vlw");

  textFont(fuente);

  consola = new Consola(paleta);
  controlador = new Controlador(consola);
  iniciarTitle();
}

void iniciarTitle(){
  try{String version = loadStrings( "../version.txt" )[0];
  surface.setTitle( getClass().getName() + " "+(version!=null?version:""));}
  catch( Exception e ){System.err.println( "Exception title: " + e.getMessage() );}
}

void draw() {

  background( fondo);
  if (consola != null && conectadoConSistema) {
    consola.ejecutar();
  } else if (!conectadoConSistema && existeSistema) {
    pushStyle();
    fill(255);
    textSize(30);
    textAlign(CENTER, CENTER);
    text("conectando", width/2, height/2);
    popStyle();
    consolaDebug();
  } else {
    pushStyle();
    fill(255);
    textSize(30);
    textAlign(CENTER, CENTER);
    text("desconectando", width/2, height/2);
    popStyle();
    consolaDebug();
  }

  // ipManager.fondo();//-----esto ya no sirve pero lo dejo aqui para recordarme 
  //-----que tengoq ue borrar la pesataña y todo lo demas qe 
  //-----tenga uqe ver con esto

  revisarConectadoConSistema();
  if (!existeSistema) {
    estadoDesconectado();
  }
  UI_paleta();
}

void revisarConectadoConSistema() {   
  if (!conectadoConSistema && totales && existentes) {
    botonesListo();
    println("..........listo.........");
    consolaDebug.printlnAlerta("..........listo.........");
    conectadoConSistema = true;
    controlador.anadir();
  }
}

void estadoDesconectado() {
  if (frameCount%60==0) {
    println("pido");
    consolaDebug.printlnAlerta("pido");
    //consola.mandarMensaje("/pedir/modificadores/total");
    consola.mandarMensaje("/pedir/opciones");
  }
}

void reiniciarCarrete() {
  existeSistema=false;
  conectadoConSistema=false;
}

void UI_paleta() {
  if (mousePressed) {
    uiControl.mouseDrag(mouseX, mouseY);
  }
  uiControl.dibujar();
}

void keyPressed() {

  /* if (key == ' ') {
   if (uiControl.escondido)
   ipManager.esconder();
   }*/
  if (conectadoConSistema) {
    if (keyCode==RIGHT) {
      controlador.derecha();
    }

    if (keyCode==ALT) {
      //  if (ipManager.escondido)
      uiControl.esconder();
    }

    if (keyCode==LEFT) {
      controlador.izquierda();
    }

    if (keyCode==ENTER) {
      controlador.aceptar();
    }

    if (key == 'A' || key == 'a') {
      controlador.anadir();
    }

    if (key == 's' || key == 'S') {
      controlador.quitar();
    }

    if (key == 'q' || key == 'Q') {
      controlador.opciones();
    }

    if (key == 'i' || key == 'I') {
      //consola.imprimirLista();
      consola.cC.imprimirMaquinaria();
    }

    if (key == 'g' || key == 'G') {
      saveFrame("capturas/captura_####.tff");
      contadorCapturas++;
    }



    /*if (key == 'm' || key == 'M') {
     consola.cC.imprimirMaquinaria();
     }*/

    if (key == 'm' || key == 'M') {
      bdd.interaccionConMouse = !bdd.interaccionConMouse;
    }

    if (key == 'r' || key == 'R') {
      reiniciarCarrete() ;
    }
  }
}

void mousePressed() {
  uiControl.mousePressed(mouseX, mouseY);
  if (bdd.interaccionConMouse) {
    consola.mousePressed();
  }
}

void actualizarMovimiento() {
  controlador.actualizarIconos(cerradoValor, nivelValor, desequilibrioValor);
}

void cantidadBotones(int cantidad_) {
  existeSistema = true;
  cantidad=cantidad_;
  nombresModificadores = new ArrayList<String>();
  nombresCategorias = new ArrayList<String>();
  nombresMaquinarias = new ArrayList<String>();
  nombresOpciones = new ArrayList<String>();
  //estado = new ArrayList<Boolean>();
  consolaDebug.printlnAlerta("cantidad botones: "+cantidad_);
}

void cantidadBotonesExistentes(int cantidad_) {
  cantidadExistentes=cantidad_;
  nombresModificadoresExistentes = new ArrayList<String>();
  // estadoExistentes = new ArrayList<Boolean>();
  consolaDebug.printlnAlerta("cantidad existentes: "+cantidad_);
}

void modificadores(String nombre_, String categoria_, int estado_) {
  nombresModificadores.add(nombre_); 
  nombresCategorias.add(categoria_); 
  consolaDebug.printlnAlerta("llega mod: "+nombre_ +" de "+categoria_);
  /* if (estado_ == 0) {
   estado.add(false);
   } else {
   estado.add(true);
   }*/
}

void modificadoresExistentes(String nombre, int estado_) {
  nombresModificadoresExistentes.add(nombre); 

  consolaDebug.printlnAlerta("exite mod: "+nombre);
  /*if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}

void opciones(String nombre_, int estado_) {
  nombresOpciones.add(nombre_); 
  consolaDebug.printlnAlerta("llega opcion: "+nombre_);
  /* if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}

void maquinarias(String nombre_) {
  nombresMaquinarias.add(nombre_); 
  consolaDebug.printlnAlerta("llega maquinaria: "+nombre_);
  /* if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}

/*void estimulos(String nombre_, int estado_) {
 
 nombresModificadores.add(nombre_); 
 println("me estan llegando los estimulos");*/
/* if (estado_ == 0) {
 estado.add(true);
 } else {
 estado.add(false);
 }*/
/*}*/


void totalesListo() {
  totales =true;
  println("totales listo");
  consolaDebug.printlnAlerta("totales listo", paleta[2][3]);
  if (!existentes) {
    consola.mandarMensaje("/pedir/modificadores/existentes");
  }
}

void  existentesListo() {
  existentes =true;
  println("existentes listo");
  consolaDebug.printlnAlerta("existentes listo", paleta[2][3]);
  /*if (!maquinarias) {
   consola.mandarMensaje("/pedir/maquinarias");
   }*/
}

void  maquinariasListo() {
  maquinarias = true;
  consola.renovarDatosMaquinarias(nombresMaquinarias);
  println("maquinarias listo");
  consolaDebug.printlnAlerta("maquinarias Listo", paleta[2][3]);
  if (!conectadoConSistema) {
    consola.mandarMensaje("/pedir/modificadores/total");
  }
  /* if (!estimulos) {
   consola.mandarMensaje("/pedir/estimulos");
   }*/
}

/*void  estimulosListo() {
 consola.renovarDatosEstimulos(nombresModificadores);
 println("estimulos listos");
 consola.mandarMensaje("/pedir/opciones");
 }*/

void botonesListo() {
  consola.renovarDatosCategorias(nombresModificadores, nombresCategorias, nombresModificadoresExistentes);

  // consola.renovarDatosCategorias(nombresModificadores, nombresCategorias, nombresModificadoresExistentes);
  //  renovarConsola = false;
  totales = false;
  existentes = false;
  //consola.mandarMensaje("/pedir/estimulos");
}
void opcionesListo() {
  consola.renovarDatosOpciones(nombresOpciones);
  println("opciones listos");
  consolaDebug.printlnAlerta("opciones Listo", paleta[2][3]);
  consola.mandarMensaje("/pedir/maquinarias");
  // consola.renovarDatos(cantidad, estado, nombresModificadores);
}

void agregarMod(String cual) {
  consola.agregarMod(cual);
}
void quitarMod(String cual) {
  consola.quitarMod(cual);  

  // consola.renovarDatos(cantidad, estado, nombresModificadores);
}

void agregarListaMod(String cual) {
  String[] lista = cual.split("_");
  for (int i=0; i<lista.length; i++) {
    consola.agregarMod(lista[i]);
  }
}

void quitarListaMod(String cual, char separador) {
  println("llega mensaje");
  println(cual);

  String[] lista = split(cual, separador); 
  for (int i=0; i<lista.length; i++) {    
    consola.quitarMod(lista[i]);
  }
}
//-----------------------------------------------------------------------------------CAPTURA--------------------------

void menuQuitarModificador() {
  // if (!consola.nivelAgregar && !consola.nivelQuitar)
  controlador.quitar();
}
void menuAgregarModificador() {
  //  if (!consola.nivelAgregar && !consola.nivelQuitar)
  controlador.anadir();
}
void menuNavegarIzquierda() {
  controlador.izquierda();
}
void menuNavegarDerecha() {
  controlador.derecha();
}
void cancelar() {
  controlador.cancelar();
}
void aceptar() {
  controlador.aceptar();
}

void cerrado(int valor) {
  cerradoValor = valor;

  println( "cerradoValor: " +  cerradoValor);
}
void desequilibrio(int valor) {
  desequilibrioValor = valor;
  println( "desequilibrioValor: " +  desequilibrioValor);
}
void nivel(int valor) {
  nivelValor = valor;
  println( "nivelValor: " +  nivelValor);
}
/*oid cursorMoCap(float x, float y) {
 consola.setCursor(x*width, y*height);
 }*/
