import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import oscP5.*; 
import netP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Carrete extends PApplet {

// Soy un triangulooo....
// No soy un cuadradoooo...
//No soy un circulo....
// Tengo Arriba....
//Soy 2D

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



int contadorCapturas = 0;

OscP5 oscP5;
NetAddress sistema;

//ControlP5 cp5;
int[][] paleta; 

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

int colorBoton;
int lineaBoton;
int letras;
int letrasActivas;
int fondo;


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
public void setup() {
  
  bdd = new BDD();
  //frameRate(25);
  
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
}


public void draw() {

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

public void revisarConectadoConSistema() {   
  if (!conectadoConSistema && totales && existentes) {
    botonesListo();
    println("..........listo.........");
    consolaDebug.printlnAlerta("..........listo.........");
    conectadoConSistema = true;
    controlador.anadir();
  }
}

public void estadoDesconectado() {
  if (frameCount%60==0) {
    println("pido");
    consolaDebug.printlnAlerta("pido");
    //consola.mandarMensaje("/pedir/modificadores/total");
    consola.mandarMensaje("/pedir/opciones");
  }
}

public void reiniciarCarrete() {
  existeSistema=false;
  conectadoConSistema=false;
}

public void UI_paleta() {
  if (mousePressed) {
    uiControl.mouseDrag(mouseX, mouseY);
  }
  uiControl.dibujar();
}

public void keyPressed() {

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

public void mousePressed() {
  uiControl.mousePressed(mouseX, mouseY);
  if (bdd.interaccionConMouse) {
    consola.mousePressed();
  }
}

public void actualizarMovimiento() {
  controlador.actualizarIconos(cerradoValor, nivelValor, desequilibrioValor);
}

public void cantidadBotones(int cantidad_) {
  existeSistema = true;
  cantidad=cantidad_;
  nombresModificadores = new ArrayList<String>();
  nombresCategorias = new ArrayList<String>();
  nombresMaquinarias = new ArrayList<String>();
  nombresOpciones = new ArrayList<String>();
  //estado = new ArrayList<Boolean>();
  consolaDebug.printlnAlerta("cantidad botones: "+cantidad_);
}

public void cantidadBotonesExistentes(int cantidad_) {
  cantidadExistentes=cantidad_;
  nombresModificadoresExistentes = new ArrayList<String>();
  // estadoExistentes = new ArrayList<Boolean>();
  consolaDebug.printlnAlerta("cantidad existentes: "+cantidad_);
}

public void modificadores(String nombre_, String categoria_, int estado_) {
  nombresModificadores.add(nombre_); 
  nombresCategorias.add(categoria_); 
  consolaDebug.printlnAlerta("llega mod: "+nombre_ +" de "+categoria_);
  /* if (estado_ == 0) {
   estado.add(false);
   } else {
   estado.add(true);
   }*/
}

public void modificadoresExistentes(String nombre, int estado_) {
  nombresModificadoresExistentes.add(nombre); 

  consolaDebug.printlnAlerta("exite mod: "+nombre);
  /*if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}

public void opciones(String nombre_, int estado_) {
  nombresOpciones.add(nombre_); 
  consolaDebug.printlnAlerta("llega opcion: "+nombre_);
  /* if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}

public void maquinarias(String nombre_) {
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


public void totalesListo() {
  totales =true;
  println("totales listo");
  consolaDebug.printlnAlerta("totales listo", paleta[2][3]);
  if (!existentes) {
    consola.mandarMensaje("/pedir/modificadores/existentes");
  }
}

public void  existentesListo() {
  existentes =true;
  println("existentes listo");
  consolaDebug.printlnAlerta("existentes listo", paleta[2][3]);
  /*if (!maquinarias) {
   consola.mandarMensaje("/pedir/maquinarias");
   }*/
}

public void  maquinariasListo() {
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

public void botonesListo() {
  consola.renovarDatosCategorias(nombresModificadores, nombresCategorias, nombresModificadoresExistentes);

  // consola.renovarDatosCategorias(nombresModificadores, nombresCategorias, nombresModificadoresExistentes);
  //  renovarConsola = false;
  totales = false;
  existentes = false;
  //consola.mandarMensaje("/pedir/estimulos");
}
public void opcionesListo() {
  consola.renovarDatosOpciones(nombresOpciones);
  println("opciones listos");
  consolaDebug.printlnAlerta("opciones Listo", paleta[2][3]);
  consola.mandarMensaje("/pedir/maquinarias");
  // consola.renovarDatos(cantidad, estado, nombresModificadores);
}

public void agregarMod(String cual) {
  consola.agregarMod(cual);
}
public void quitarMod(String cual) {
  consola.quitarMod(cual);  

  // consola.renovarDatos(cantidad, estado, nombresModificadores);
}

public void agregarListaMod(String cual) {
  String[] lista = cual.split("_");
  for (int i=0; i<lista.length; i++) {
    consola.agregarMod(lista[i]);
  }
}

public void quitarListaMod(String cual, char separador) {
  println("llega mensaje");
  println(cual);

  String[] lista = split(cual, separador); 
  for (int i=0; i<lista.length; i++) {    
    consola.quitarMod(lista[i]);
  }
}
//-----------------------------------------------------------------------------------CAPTURA--------------------------

public void menuQuitarModificador() {
  // if (!consola.nivelAgregar && !consola.nivelQuitar)
  controlador.quitar();
}
public void menuAgregarModificador() {
  //  if (!consola.nivelAgregar && !consola.nivelQuitar)
  controlador.anadir();
}
public void menuNavegarIzquierda() {
  controlador.izquierda();
}
public void menuNavegarDerecha() {
  controlador.derecha();
}
public void cancelar() {
  controlador.cancelar();
}
public void aceptar() {
  controlador.aceptar();
}

public void cerrado(int valor) {
  cerradoValor = valor;

  println( "cerradoValor: " +  cerradoValor);
}
public void desequilibrio(int valor) {
  desequilibrioValor = valor;
  println( "desequilibrioValor: " +  desequilibrioValor);
}
public void nivel(int valor) {
  nivelValor = valor;
  println( "nivelValor: " +  nivelValor);
}
/*oid cursorMoCap(float x, float y) {
 consola.setCursor(x*width, y*height);
 }*/
ConfiguracionCOD05 config;
public void initOSC() {
  if (config == null) config = new ConfiguracionCOD05();
  XML xmlConfig = null;
  if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
  if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagEjecucion);

  config.cargar(xmlConfig);

  /* oscP5 = new OscP5(this, 12030);//config.carrete.puerto);
   sistema = new NetAddress("127.0.0.1", 12010);//"config.lienzo.ip, config.lienzo.puerto);
   */
  oscP5 = new OscP5(this, config.carrete.puerto);
  sistema = new NetAddress(config.lienzo.ip, config.lienzo.puerto);

  //-----------------------------------------OSC sistema de particulas

  oscP5.plug(this, "cantidadBotones", "/modificadores/totales");
  oscP5.plug(this, "modificadores", "/modificadores/totales");
  oscP5.plug(this, "totalesListo", "/modificadores/totales/listo");

  oscP5.plug(this, "cantidadBotonesExistentes", "/modificadores/existentes");
  oscP5.plug(this, "modificadoresExistentes", "/modificadores/existentes");
  oscP5.plug(this, "existentesListo", "/modificadores/existentes/listo");

  oscP5.plug(this, "cantidadBotones", "/maquinarias");
  oscP5.plug(this, "maquinarias", "/maquinarias");
  oscP5.plug(this, "maquinariasListo", "/maquinarias/listo");

  oscP5.plug(this, "cantidadBotones", "/estimulos/totales"); 
  oscP5.plug(this, "estimulos", "/estimulos/totales");
  oscP5.plug(this, "estimulosListo", "/estimulos/listo");

  oscP5.plug(this, "cantidadBotones", "/opciones");
  oscP5.plug(this, "opciones", "/opciones");
  oscP5.plug(this, "opcionesListo", "/opciones/listo");  

  oscP5.plug(this, "agregarMod", "/agregarMod");
  oscP5.plug(this, "quitarMod", "/quitarMod");

  //----------------------------------------OSC captura de movimiento



  oscP5.plug(this, "menuQuitarModificador", "/MenuQuitarModificador");
  oscP5.plug(this, "menuAgregarModificador", "/MenuAgregarModificador");
  oscP5.plug(this, "menuNavegarIzquierda", "/MenuNavegarIzquierda");
  oscP5.plug(this, "menuNavegarDerecha", "/MenuNavegarDerecha");

  oscP5.plug(this, "cancelar", "/Cancelar");
  oscP5.plug(this, "aceptar", "/Aceptar");
  oscP5.plug(this, "quitarListaMod", "/quitarListaMod");
  oscP5.plug(this, "agregarListaMod", "/agregarListaMod");

  oscP5.plug(this, "actualizarMovimiento", "/actualizarMovimiento");
  oscP5.plug(this, "cerrado", "/cerrado");
  oscP5.plug(this, "desequilibrio", "/desequilibrio");
  oscP5.plug(this, "nivel", "/nivel");

  oscP5.plug(this, "cursorMoCap", "/cursor");
}

//---------------------------------------------IP MANAGER----EVENTOS-----------------------

/*public void Conectar() { 
 
 String unaIp = cp5.get(Textfield.class, "IP").getText();
 String unPuertoEnvio = cp5.get(Textfield.class, "Puerto Envio").getText();
 String unPuertoRecivo = cp5.get(Textfield.class, "Puerto Recivo").getText();
 
 if (!unaIp.equals(""))ip = cp5.get(Textfield.class, "IP").getText();
 if (!unPuertoEnvio.equals(""))puertoEnvio = cp5.get(Textfield.class, "Puerto Envio").getText();
 if (!unPuertoRecivo.equals(""))puertoRecivo = cp5.get(Textfield.class, "Puerto Recivo").getText();
 
 conectarOSC();
 
 cp5.get(Textfield.class, "IP").clear();
 cp5.get(Textfield.class, "Puerto Envio").clear();
 cp5.get(Textfield.class, "Puerto Recivo").clear();
 }*/

public void conectarOSC() {
  int pe = PApplet.parseInt(puertoEnvio);
  int pr = PApplet.parseInt(puertoRecivo);
  oscP5 = new OscP5(this, pr); 
  sistema = new NetAddress(ip, pe);

  //-----------------------------------------OSC plugs

  //-----------------------------------------OSC sistema de particulas

  oscP5.plug(this, "cantidadBotones", "/modificadores/totales");
  oscP5.plug(this, "modificadores", "/modificadores/totales");
  oscP5.plug(this, "totalesListo", "/modificadores/totales/listo");
  oscP5.plug(this, "cantidadBotonesExistentes", "/modificadores/existentes");
  oscP5.plug(this, "modificadoresExistentes", "/modificadores/existentes");
  oscP5.plug(this, "existentesListo", "/modificadores/existentes/listo");

  oscP5.plug(this, "cantidadBotones", "/estimulos/totales"); 
  oscP5.plug(this, "estimulos", "/estimulos/totales");
  oscP5.plug(this, "estimulosListo", "/estimulos/listo");

  oscP5.plug(this, "cantidadBotones", "/opciones");
  oscP5.plug(this, "opciones", "/opciones");
  oscP5.plug(this, "opcionesListo", "/opciones/listo");

  oscP5.plug(this, "cantidadBotones", "/maquinarias");
  oscP5.plug(this, "maquinarias", "/maquinarias");
  oscP5.plug(this, "maquinarias", "/maquinarias/listo");

  oscP5.plug(this, "agregarMod", "/agregarMod");
  oscP5.plug(this, "quitarMod", "/quitarMod");




  //----------------------------------------OSC captura de movimiento



  oscP5.plug(this, "menuQuitarModificador", "/MenuQuitarModificador");
  oscP5.plug(this, "menuAgregarModificador", "/MenuAgregarModificador");
  oscP5.plug(this, "menuNavegarIzquierda", "/MenuNavegarIzquierda");
  oscP5.plug(this, "menuNavegarDerecha", "/MenuNavegarDerecha");

  oscP5.plug(this, "cancelar", "/Cancelar");
  oscP5.plug(this, "aceptar", "/Aceptar");

  oscP5.plug(this, "actualizarMovimiento", "/actualizarMovimiento");
  oscP5.plug(this, "cerrado", "/cerrado");
  oscP5.plug(this, "desequilibrio", "/desequilibrio");
  oscP5.plug(this, "nivel", "/nivel");


  println("IP: " + ip  +" || "+ "Puerto Envio: " + pe +" || "+ "Puerto Recivo: " + pr );
}




/*

 // incoming osc message are forwarded to the oscEvent method. 
 void oscEvent(OscMessage theOscMessage) {
 // print the address pattern and the typetag of the received OscMessage 
 print("### received an osc message.");
 print(" addrpattern: "+theOscMessage.addrPattern());
 println(" typetag: "+theOscMessage.typetag());
 }
 */
class BDD {
  //--------------------- ESTRUCTURA RUEDA ---------------------
  float ruedaX = width/2;
  float ruedaY = height/5+(height-height/5-height/10)/2;
  float ruedaDiametro =width>height?(height*7/10)/3:(width*7/10)/3;

  //--------------------- ESTRUCTURA MONITOR ---------------------
  float monitorX = width/2;
  float monitorY = height/10;
  float monitorDiametro = width<height? width/17 : height/17;
  float baseMonitorX =0;
  float baseMonitorY =0;
  float baseMonitorAncho =width; 
  float baseMonitorAlto =height/5;

  //--------------------- ESTRUCTURA MAQUINARIAS ---------------------
  float baseMaquinariasX =0;
  float baseMaquinariasY =height-height/10;
  float baseMaquinariasAncho =width;
  float baseMaquinariasAlto =height/10;  

  //-----------------------globales de toda la vida
  boolean interaccionConMouse = false;

 
}
class Categoria extends Opcion {
  ArrayList modificadores;
  int mods = 0;
  boolean esUnaOpcionDeNavegacion;
  boolean hover;
  String hoverMod;
  Modificador hoverMod_modificador;
  Categoria(String nombre_) {
    nombre = nombre_;
    esUnaOpcionDeNavegacion = false;
    modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = new int[4][12];
    for (int i=0; i<4; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    iconos = new Iconos(PApplet.parseInt(t));
    pos = new PVector();
    posCentro = new PVector();
  }

  Categoria(String nombre_, int[][] paleta_) {
    nombre = nombre_;
    modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = paleta_;
    iconos = new Iconos(PApplet.parseInt(t));
    pos = new PVector();
    posCentro = new PVector();
    conIcono = true;
  }

  public void inicializar( int col_, int cant_, PVector pos_, PVector posCentro_, float t_, boolean esUnaOpcionDeNavegacion_) {
    col = col_;
    cant = cant_;    
    pos = pos_;
    posCentro = posCentro_;
    t = t_;//width>height?height/4:width/4;
    tamFigura = t*26/100;
    iconos = new Iconos(PApplet.parseInt(tamFigura));
    esUnaOpcionDeNavegacion = esUnaOpcionDeNavegacion_;
  }

  /* 
   String getNombre() {
   String n = "no tengo";
   
   if (nombre!=null)
   n = nombre;
   return n;
   }*/

  public void addMod() {
    mods++;
  }
  public void removerMod() {
    if (mods>0)
      mods--;
  }

  public void resetMods() {
    mods=0;
  }

  public void aniadir(String nombre, int[][] paleta) {
    Modificador m = new Modificador(nombre, this, paleta);
    modificadores.add(m);
  }


  public void aniadir(String nombre) {
    Modificador m = new Modificador(nombre, this);
    modificadores.add(m);
  }

  public void setHover() {
    setSensible(false);
    hover = false;
    hoverMod = null;
    hoverMod_modificador = null;
    for (int i=0; i<modificadores.size (); i++) {
      Modificador m = (Modificador) modificadores.get(i);
      if (m.hoverExtendido) {       
        setSensible(true);
        if (m.hover) {
          hover = true;
          hoverMod = m.nombre;
          hoverMod_modificador = m;
        }
      }
    }
  }
  public String getHover() {
    return hoverMod;
  }

  public Modificador getHover_modificador() {
    return hoverMod_modificador;
  }


  public void dibujarMods() {
    float angulo = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
    float contraAngulo = angulo-PI;
    if (modificadores.size()>1) {
      float espacioAngular = radians(map(modificadores.size(), 0, 20, 0, 360));
      for (int i=0; i<modificadores.size (); i++) {
        Modificador m = (Modificador) modificadores.get(i);
        float espacioAngularDistribuido = map(i, 0, modificadores.size()-1, 0, espacioAngular);
        float x;
        float y;
        if (sensible) {
          x = pos.x+/*(t*42/100*cos(contraAngulo))+*/(t*42/100*cos(angulo-espacioAngular/2+espacioAngularDistribuido));
          y = pos.y+/*(t*42/100*sin(contraAngulo))+*/(t*42/100*sin(angulo-espacioAngular/2+espacioAngularDistribuido));
        } else {
          x = pos.x+(t*31/100*cos(angulo-espacioAngular/2+espacioAngularDistribuido));
          y = pos.y+(t*31/100*sin(angulo-espacioAngular/2+espacioAngularDistribuido));
        }
        //m.dibujar(x, y, t*0.6, col);// dibujar texto y ellipses
        m.dibujarIconos(x, y, t, col);// dibujar iconos de imagenes
      }
    } else {
      Modificador m = (Modificador) modificadores.get(0);
      float x;
      float y;
      if (sensible) {
        x = pos.x+(t*42/100*cos(angulo));
        y = pos.y+(t*42/100*sin(angulo));
      } else {
        x = pos.x+(t*31/100*cos(angulo));
        y = pos.y+(t*31/100*sin(angulo));
      }
      //m.dibujar(x, y, t*0.6, col);// dibujar texto y ellipses
      m.dibujarIconos(x, y, t, col);// dibujar iconos de imagenes
    }
  }

  public void dibujarCategoria() {
    if (!esUnaOpcionDeNavegacion) {      
      dibujar();
      dibujarMods();
      displayModificadoresExistentes();
    } else {
      dibujar();
    }
  }


  //  void setSensible(boolean sensible_/*, boolean estado_*/) {
  /*   sensible = sensible_;
   // estado = estado_;
   }
   
   void enSensible() {
   pushStyle();
   fill(paleta[0]);
   ellipse(pos.x, pos.y, t*1.5, t*1.5);
   float angulo = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
   float d = t*2;
   
   for (int j =0; j< 5; j++) {
   float cX = posCentro.x+t/3*cos(angulo);
   float cY = posCentro.y+t/3*sin(angulo);
   float x1 = (cX)+((d/4)*j)*cos(angulo);
   float y1 = (cY)+((d/4)*j)*sin(angulo);
   float x2 = x1+(d/10)*cos(angulo);
   float y2 = y1+(d/10)*sin(angulo);
   float x3 = x1+(d/5.75)*cos(angulo);
   float y3 = y1+(d/5.75)*sin(angulo);
   stroke(paleta[0]);
   strokeWeight(3);
   line(x1, y1, x2, y2);          
   
   strokeWeight(5);
   point(x3, y3);
   }
   popStyle();
   }*/

  public void coneccionCanal(float px, float py, float x, float y, float ang) {   

    pushStyle();
    strokeWeight(2);
    stroke(paleta[1][3]);
    noFill();
    float xar1 = pos.x+t*16/100*cos(ang+radians(90));
    float yar1 = pos.y+t*16/100*sin(ang+radians(90));
    float xab1 = posCentro.x+t*16/100*cos(ang+radians(90));
    float yab1 = posCentro.y+t*16/100*sin(ang+radians(90));
    float xar2 = pos.x+t*16/100*cos(ang-radians(90));
    float yar2 = pos.y+t*16/100*sin(ang-radians(90));
    float xab2 = posCentro.x+t*16/100*cos(ang-radians(90));
    float yab2 = posCentro.y+t*16/100*sin(ang-radians(90));
    line(xar1, yar1, xab1, yab1);
    line(xar2, yar2, xab2, yab2);
    arc(pos.x, pos.y, t*32/100, t*32/100, ang+radians(270), ang+radians(270)+radians(180));
    popStyle();
  }

  public void coneccionLinea(float px, float py, float cx, float cy) {
    pushStyle();
    strokeWeight(1);
    stroke(paleta[1][3]);
    noFill();     
    line(px, py, cx, cy);    
    ellipse(pos.x, pos.y, t*32/100, t*32/100);
    popStyle();
  }

  public void feedbackModificadores(float px, float py, float diam, float ang) {
    float x = 0;
    float y = 0;
    if (mods<4) {        
      for (int i=0; i<mods; i++) {
        x = px-diam/(mods+1)*(i+1)*cos(ang);
        y = py-diam/(mods+1)*(i+1)*sin(ang);
        pushStyle();
        fill(paleta[2][3]);
        ellipse(x, y, t*9/100, t*9/100);
        /*textAlign(CENTER, CENTER);
         fill(255);
         textSize(15);
         text(mods, x, y);*/
        popStyle();
      }
    } else {
      for (int i=0; i<3; i++) {
        x = px-diam/(4)*(i+1)*cos(ang);
        y = py-diam/(4)*(i+1)*sin(ang);
        pushStyle();
        fill(paleta[2][3]);
        ellipse(x, y, t*9/100, t*9/100);
        popStyle();
      }
    }
  }
  public void displayModificadoresExistentes() {
    if (mods>0) {
      float ang = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
      float px = pos.x-(t*32/100/2)*cos(ang);
      float py = pos.y-(t*32/100/2)*sin(ang);
      float cx = posCentro.x+(t/2)*cos(ang);
      float cy = posCentro.y+(t/2)*sin(ang);     
      float diam = dist(px, py, cx, cy);
      coneccionCanal(px, py, cx, cy, ang);
      //coneccionLinea( px, py, cx, cy);
      feedbackModificadores(px, py, diam, ang);
    }
  }
}
class ColeccionCategorias {
  int contador;
  int cant;
  PVector posCentro;
  ArrayList categorias;  
  HashMap<Integer, Modificador> listaMods;
  HashMap<String, Modificador> listaModsPorNombre;
  int[][] paleta;
  float t;
  // boolean dosNivelesDeJerarquia;

  ColeccionCategorias() {
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionCategorias(int[][] paleta_, PVector posCentro_, float t_) {
    paleta = paleta_;
    t = t_;
    posCentro = posCentro_;
  }

  public void inicializar(String[] nombresCategorias, boolean[]opcionesDeNavegacion, String[] nombresModificadores, String[] nombresModificadoresExistentes) {

    if (nombresCategorias != null && nombresModificadores != null) {
      setCategorias(nombresCategorias, opcionesDeNavegacion, nombresModificadores);
    }

    if (nombresModificadoresExistentes != null) {
      setModificadoresExistentesEnCategorias(nombresModificadoresExistentes);
    }
  }

  public void setModificadoresExistentesEnCategorias(String[] nombresModificadoresExistentes) {

    for (int i=0; i<cant; i++) {
      Categoria c = (Categoria)categorias.get(i);
      c.resetMods();
      for (int k=0; k<c.modificadores.size (); k++) {
        Modificador m = (Modificador)c.modificadores.get(k);
        m.resetMods();
        for (int l=0; l<nombresModificadoresExistentes.length; l++) {
          if (m.nombre.equals(nombresModificadoresExistentes[l])) {
            c.addMod();
            m.addMod();
          }
        }
        //---------------k
      }
      //---------------j
    }
    //--------------i
  }
  public void setCategorias(String[] nombresCategorias, boolean[]opcionesDeNavegacion, String[] nombresModificadores) {

    categorias = new ArrayList();
    ArrayList<Boolean> opcionesDeNav = new ArrayList<Boolean>();
    for (int i=0; i<nombresCategorias.length; i++) {
      boolean existe = false;
      int indiceCategoria = 0;
      for (int j=0; j<categorias.size (); j++) {
        Categoria c = (Categoria)categorias.get(j);
        if (c.getNombre().equals(nombresCategorias[i])) {
          existe = true;
          indiceCategoria = j;
        }
      }

      if (!existe) {
        pushStyle();
        colorMode(HSB);
        Categoria c = new Categoria(nombresCategorias[i], paleta);       
        c.aniadir(nombresModificadores[i], paleta);
        categorias.add(c);
        opcionesDeNav.add(opcionesDeNavegacion[i]);
        popStyle();
      } else {
        Categoria c = (Categoria)categorias.get(indiceCategoria);
        c.aniadir(nombresModificadores[i], paleta);
      }
    }
    cant = categorias.size();
    int contadorDeListaDeModificadores = 0;
    listaMods =  new HashMap<Integer, Modificador>();
    listaModsPorNombre =  new HashMap<String, Modificador>();
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      for (int j=0; j<c.modificadores.size (); j++) {
        Modificador m = (Modificador)c.modificadores.get(j);
        listaMods.put(contadorDeListaDeModificadores, m);
        listaModsPorNombre.put(m.getNombre(), m);
        contadorDeListaDeModificadores++;
      }
      pushStyle();
      colorMode(HSB);
      float hue = map(i, 0, cant, 0, 255);
      int colorsito = color(hue, 150, 220);
      float norm = map(i, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      float angulo = radians(360*norm+90);
      //float anguloUnidad = radians(360*normUnidad);
      float diametroSelector = t*118/100;
      float x = bdd.ruedaX+(diametroSelector*cos(angulo));
      float y = bdd.ruedaY+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);

      println(c.nombre+": "+c.modificadores.size());
      //c.inicializar(colorsito, cant, pos, posCentro, t);
      c.inicializar(colorsito, cant, pos, posCentro, t, opcionesDeNav.get(i));
      popStyle();
    }
    println(categorias.size());
  }

  public void dibujar() {   
    if (categorias!=null) {
      for (int i=0; i<categorias.size (); i++) {
        pushStyle();
        colorMode(HSB); 
        Categoria c = (Categoria)categorias.get(i);     
        c.dibujar();
        popStyle();
      }
    }
  }

  public void dibujarCategoria() {   
    if (categorias!=null) {
      for (int i=0; i<categorias.size (); i++) {
        pushStyle();
        colorMode(HSB); 
        Categoria c = (Categoria)categorias.get(i);     
        c.dibujarCategoria();
        popStyle();
      }
    }
  }

  public void setSensible(int sensible) {
    String nombreSensible = listaMods.get(sensible).getNombre();
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);  
      c.setSensible(false);

      for (int j=0; j<c.modificadores.size (); j++) {
        Modificador m = (Modificador)c.modificadores.get(j); 
        m.setSensible(false);   
        if (m.nombre.equals(nombreSensible)) {
          c.setSensible(true);
          m.setSensible(true);
        }
      }
    }
  }

  public String getSensible(int sensible) {
    String nombreSensible;
    nombreSensible = listaMods.get(sensible).getNombre();
    return nombreSensible;
  }

  public int getColorSensible(int sensible) {
    int colorSensible;
    Modificador mod = listaMods.get(sensible);
    Categoria c = mod.categoria;   
    colorSensible = c.col;
    return colorSensible;
  }

  public Modificador getModSensible(int sensible) {
    Modificador mod = listaMods.get(sensible);    
    return mod;
  }

  //---------------------MOUSE------------
  public void mouse() {
    if (categorias != null) {
      for (int i=0; i<categorias.size (); i++) {
        Categoria c = (Categoria)categorias.get(i);
        c.setHover();
      }
    }
  }
  public String getSensibleMouse() {
    String nombreSensibleMouse = null;
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      if (c.hover) {
        nombreSensibleMouse = c.getHover();
      }
    }
    return nombreSensibleMouse;
  }

  public Modificador getSensibleMouse_modificador() {
    Modificador modificadorSensibleMouse = null;
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      if (c.hover) {
        modificadorSensibleMouse = c.getHover_modificador();
      }
    }
    return modificadorSensibleMouse;
  }

  public int getColorSensibleMouse() {
    int colorSensibleMouse = color(0);
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      if (c.hover) {
        colorSensibleMouse = c.col;
      }
    }    
    return colorSensibleMouse;
  }

  //------------------------------------------------
  public void imprimirMaquinaria() {
    String maquinaria = "";
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);  
      c.setSensible(false);

      for (int j=0; j<c.modificadores.size (); j++) {
        Modificador m = (Modificador)c.modificadores.get(j); 
        if (m.mods>0) {
          maquinaria = maquinaria.equals("")?maquinaria+m.nombre:maquinaria+"|"+m.nombre;
        }
      }
    }
    println(maquinaria);
  }
 
 
  public void agregar(String cual) {
    Modificador m = listaModsPorNombre.get(cual);
    m.categoria.addMod();
    m.addMod();
  }
  public void quitar(String cual) {
    String[] n = split(cual, "_");
    Modificador m = listaModsPorNombre.get(n[0]);
    m.categoria.removerMod();
    m.removerMod();
  }
}
/*class ColeccionEstimulos {
  int contador;
  int cant;
  PVector posCentro;
  ArrayList estimulos;  
  color[][] paleta;
  float t;

  ColeccionEstimulos() {
     paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
      paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionEstimulos(color[][] paleta_,PVector posCentro_,float t_) {
    paleta = paleta_;
    t = t_;
    posCentro = posCentro_;
  }

  void inicializar(String[] nombresEstimulos) {
    
    if (nombresEstimulos != null) {
      setEstimulos(nombresEstimulos);
    }
  }

  void setEstimulos(String[] nombresEstimulos) {

    estimulos = new ArrayList();
    for (int i=0; i<nombresEstimulos.length; i++) {
      Estimulo e = new Estimulo(nombresEstimulos[i], paleta);       
      estimulos.add(e);
    }
    cant = estimulos.size();
    int contadorDeListaDeModificadores = 0;

    for (int i=0; i<estimulos.size (); i++) {
      Estimulo e = (Estimulo)estimulos.get(i);
      pushStyle();
      colorMode(HSB);
      float hue = map(i, 0, cant, 0, 255);
      color colorsito = color(hue, 100, 200);
      float norm = map(i, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
        float diametroSelector = t*118/100;
        float x = width/2+(diametroSelector*cos(angulo));
      float y = height/1.7+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);

      //println(e.nombre+": "+e.modificadores.size());
      e.inicializar(colorsito, cant, pos, posCentro,t);
      popStyle();
    }
    println(estimulos.size());
  }

  void dibujar() {   
    if (estimulos!=null) {
      for (int i=0; i<estimulos.size (); i++) {
        pushStyle();
        colorMode(HSB);  

        Estimulo c = (Estimulo)estimulos.get(i);     
        c.dibujar();

        popStyle();
      }
    }
  }

  void setSensible(int sensible) {
    for (int i=0; i<estimulos.size (); i++) {
      Estimulo e = (Estimulo)estimulos.get(i);  
      e.setSensible(false);
    }
    Estimulo e = (Estimulo)estimulos.get(sensible);
    e.setSensible(true);
  }

  String getSensible(int sensible) {
    String nombreSensible;
    Estimulo e = (Estimulo)estimulos.get(sensible);
    nombreSensible = e.getNombre();

    return nombreSensible;
  }

  int getContadorSeleccionEstimulo(PVector cursor) {
    boolean seleccionando = false;
    for (int i=0; i<estimulos.size (); i++) {
      Estimulo e = (Estimulo)estimulos.get(i);  
      if (dist(e.pos.x, e.pos.y, cursor.x, cursor.y)<30 ) {
        if ( contador < 420)
          contador+=5;
        seleccionando = true;
      }
    }
    if (!seleccionando && contador > 0) {
      contador--;
    }

    return contador;
  }

  boolean getSeleccionarEstimulo() {
    boolean sE = contador > 400?true:false;
    return sE;
  }
}*/
class ColeccionMaquinarias {
  int contador;
  int cant;
  PVector posInicial;
  ArrayList maquinarias;  
  int[][] paleta;
  float t;
  int selector; //--- el selector

    ColeccionMaquinarias() {
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionMaquinarias(int[][] paleta_, float t_) {
    paleta = paleta_;
    t = t_;
  }

  public void inicializar(String[] nombresMaquinarias) {
    if (nombresMaquinarias != null) {
      setMaquinarias(nombresMaquinarias);
    }
  }

  public void setMaquinarias(String[] nombresMaquinarias) {
    maquinarias = new ArrayList();
    for (int i=0; i<nombresMaquinarias.length; i++) {
      Maquinaria m = new Maquinaria(nombresMaquinarias[i], paleta);       
      maquinarias.add(m);
    }
    cant = maquinarias.size();
    //int contadorDeListaDeModificadores = 0;

    for (int i=0; i<maquinarias.size (); i++) {
      Maquinaria m = (Maquinaria)maquinarias.get(i);
      pushStyle();
      colorMode(HSB);
      float hue = map(0, 0, cant, 0, 255);
      int colorsito = color(hue, 100, 200);
      float norm = map(0, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      //float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
      //float diametroSelector = t*50/100;

      //PVector pos = new PVector(x, y);      
      //println(e.nombre+": "+e.modificadores.size());
      m.inicializar(colorsito, cant, new PVector(), new PVector(bdd.ruedaX, bdd.ruedaY), t*25/100);
      popStyle();
    }
    println(maquinarias.size());
  }
  public void dibujarBase(float a, float b, float ancho, float alto) {
    pushStyle();
    rectMode(CORNER);
    noStroke();
    fill(paleta[1][2]);
    rect(a, b, ancho, alto);
    popStyle();
  }

  public void dibujar() {    
    dibujarBase(bdd.baseMaquinariasX, bdd.baseMaquinariasY, bdd.baseMaquinariasAncho, bdd.baseMaquinariasAlto);
    int cantidadMaqsVisibles=8;//cuantos se van a ver en el pie
    float espacioMaqsX = width/cantidadMaqsVisibles;//cuanto espacio ocupa cada uno incluido bordes
    float alCentro = espacioMaqsX/2;//cuanto hay desde el borde hasta el centro
    float x = 0;
    float y = height-bdd.baseMaquinariasAlto/2;
    if (maquinarias!=null) {
      pushStyle();
      colorMode(HSB); 
      int contadorColores = 0;
      int contadorOrden = 0;
      Maquinaria m = null;
      for (int i=selector-1; i>=0; i--) { 
        contadorOrden++;
        x = width/2-contadorOrden*espacioMaqsX;//+espacioMaqsX;        
        m = (Maquinaria)maquinarias.get(i);
        m.dibujarIconos(x, y, t, paleta[3][contadorColores]);
        contadorColores++;
        contadorColores = (contadorColores+paleta[3].length)%paleta[3].length;
      }
      x = width/2;
      m = (Maquinaria)maquinarias.get(selector);     
        m.dibujarIconos(x, y, t, paleta[3][contadorColores]);
      contadorColores++;
      contadorColores = (contadorColores+paleta[3].length)%paleta[3].length;
      contadorOrden=0;
      for (int i=selector+1; i<maquinarias.size (); i++) {   
        contadorOrden++;     
        x = width/2+contadorOrden*espacioMaqsX;//+espacioMaqsX;
        m = (Maquinaria)maquinarias.get(i);
        m.dibujarIconos(x, y, t, paleta[3][contadorColores]);
        contadorColores++;
        contadorColores = (contadorColores+paleta[3].length)%paleta[3].length;
      }
      popStyle();
    }
  }

  public void setSensible(int sensible) {
    selector = sensible;
    if (sensible>-1) {
      for (int i=0; i<maquinarias.size (); i++) {
        Maquinaria e = (Maquinaria)maquinarias.get(i);  
        e.setSensible(false);
      }
      Maquinaria e = (Maquinaria)maquinarias.get(sensible);
      e.setSensible(true);
    }
  }

  public String getSensible(int sensible) {
    String nombreSensible;
    Maquinaria e = (Maquinaria)maquinarias.get(sensible);
    nombreSensible = e.getNombre();

    return nombreSensible;
  }

  public int getColorSensible(int sensible) {
    int colorSensible;
    Maquinaria maq = (Maquinaria)maquinarias.get(sensible);
    colorSensible = maq.col;
    return colorSensible;
  }

  public Maquinaria getMaqSensible(int sensible) {
    Maquinaria maq = (Maquinaria)maquinarias.get(sensible);
    return maq;
  }
}
class ColeccionOpciones {
  int contador;
  int cant;
  PVector posCentro;
  ArrayList opciones;  
  int[][] paleta;
  float t;

  ColeccionOpciones() {
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionOpciones(int[][] paleta_, PVector posCentro_, float t_) {
    paleta = paleta_;
    t = t_;
    posCentro = posCentro_;
  }

  public void inicializar(String[] nombresOpciones) {

    if (nombresOpciones != null) {
      setOpciones(nombresOpciones);
    }
  }


  public void setOpciones(String[] nombresOpciones) {

    opciones = new ArrayList();
    for (int i=0; i<nombresOpciones.length; i++) {
      Opcion c = new Opcion(nombresOpciones[i], paleta);       
      opciones.add(c);
    }
    cant = opciones.size();
    int contadorDeListaDeModificadores = 0;

    for (int i=0; i<opciones.size (); i++) {
      Opcion c = (Opcion)opciones.get(i);
      pushStyle();
      colorMode(HSB);
      float hue = map(i, 0, cant, 0, 255);
      int colorsito = color(hue, 100, 200);
      float norm = map(i, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
      //float diametroSelector = width<height? width/4 : height/4;
      float diametroSelector = t*118/100;
      float x = posCentro.x+(diametroSelector*cos(angulo));
      float y = posCentro.y+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);


      c.inicializar(colorsito, cant, pos, posCentro, t);
      popStyle();
    }
    println(opciones.size());
  }

  public void dibujar() {   
    if (opciones!=null) {
      for (int i=0; i<opciones.size (); i++) {
        pushStyle();
        colorMode(HSB);  

        Opcion c = (Opcion)opciones.get(i);     
        c.dibujar();

        popStyle();
      }
    }
  }

  public void setSensible(int sensible) {
    for (int i=0; i<opciones.size (); i++) {
      Opcion c = (Opcion)opciones.get(i);  
      c.setSensible(false);
    }
    Opcion c = (Opcion)opciones.get(sensible);
    c.setSensible(true);
  }

  public String getSensible(int sensible) {
    String nombreSensible;
 Opcion c = (Opcion)opciones.get(sensible);
    nombreSensible = c.getNombre();

    return nombreSensible;
  }
  
  public int getColorSensible(int sensible) {
    int colorSensible;
    Opcion o = (Opcion)opciones.get(sensible);
    colorSensible = o.col;

    return colorSensible;
  }
}
/*final class Conexiones {
 
 private String nombreArchivo;
 private XML configuracion;
 
 Conexiones() {
 nombreArchivo = "configuracion.xml";
 configuracion = cargarConfiguracion(nombreArchivo);
 }
 
 Conexiones(String rutaArchivo) {
 nombreArchivo = rutaArchivo;
 configuracion = cargarConfiguracion(nombreArchivo);
 }  
 
 public String[][] getConexiones() {
 XML[] conexiones = configuracion.getChildren("conexion");
 String[][] lista = new String[conexiones.length][3];
 for (int i = 0; i < conexiones.length; i++) {
 lista[i][0] = (conexiones[i].hasAttribute("nombre") ? conexiones[i].getString("nombre") : "No especificado");
 lista[i][1] = (conexiones[i].hasAttribute("ip") ? conexiones[i].getString("ip") : "No especificado");
 lista[i][2] = (conexiones[i].hasAttribute("puerto") ? conexiones[i].getString("puerto") : "No especificado");
 }
 return lista;
 }
 
 private XML cargarConfiguracion(String nombre) {
 XML archivo;
 try {
 archivo = loadXML(nombre);
 }
 catch(Exception e) {
 archivo = crearArchivoXML();
 guardarXML(archivo);
 }
 return archivo;
 }
 
 private void guardarXML(XML archivo) {
 saveXML(archivo, "configuracion.xml");
 }
 
 public void agregarConexion(String nombre, String ip, int puerto) {
 XML nueva = configuracion.addChild("conexion");
 nueva.setString("id", str(configuracion.getChildren("conexion").length-1));
 nueva.setString("nombre", nombre);
 nueva.setString("ip", ip);
 nueva.setInt("puerto", puerto);
 guardarXML(configuracion);
 }
 
 //Crea un archivo default de XML en caso de no encontrarse
 private XML crearArchivoXML() {
 String estructura = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gestor><conexion id=\"0\" nombre=\"Kinect\" ip=\"127.0.0.1\" puerto=\"11000\"></conexion><conexion id=\"1\" nombre=\"Interfaz\" ip=\"127.0.0.1\" puerto=\"11000\"></conexion><conexion id=\"2\" nombre=\"Particulas\" ip=\"127.0.0.1\" puerto=\"11000\"></conexion></gestor>";
 return parseXML(estructura);
 }
 
 //Devuelve la IP buscando por nombre, en caso de no encontrar devuelve null
 public String getIP(String nombre) {
 return getAtributo("nombre", nombre, "ip");
 }
 
 //Devuelve la IP buscando por id, en caso de no encontrar devuelve null
 public String getIP(int id) {
 return getAtributo("id", str(id), "ip");
 }
 
 public int getPuerto(String nombre) {
 String puerto = getAtributo("nombre", nombre, "puerto");
 if (puerto == null) {
 return 0;
 } else {
 return int(puerto);
 }
 }
 
 public int getPuerto(int id) {
 String puerto = getAtributo("id", str(id), "puerto");
 if (puerto == null) {
 return 0;
 } else {
 return int(puerto);
 }
 }
 
 public boolean setPuerto(String nombre, String valor) {
 return setAtributo("nombre", nombre, "puerto", valor);
 }
 
 public boolean setPuerto(int id, String valor) {
 return setAtributo("id", str(id), "puerto", valor);
 }
 
 public boolean setIP(String nombre, String valor) {
 return setAtributo("nombre", nombre, "ip", valor);
 }
 
 public boolean setIP(int id, String valor) {
 return setAtributo("id", str(id), "ip", valor);
 }
 
 
 private String getAtributo(String atributo, String busqueda, String devuelve) {
 XML[] IPs = configuracion.getChildren("conexion");
 for (int i = 0; i < IPs.length; i++) {
 if (IPs[i].hasAttribute(atributo) && IPs[i].getString(atributo).equals(busqueda)) {
 return IPs[i].getString(devuelve);
 }
 }
 return null;
 }
 
 private boolean setAtributo(String atributo, String busqueda, String cambiaAtributo, String guarda) {
 XML[] IPs = configuracion.getChildren("conexion");
 for (int i = 0; i < IPs.length; i++) {
 if (IPs[i].hasAttribute(atributo) && IPs[i].getString(atributo).equals(busqueda)) {
 IPs[i].setString(cambiaAtributo, guarda);
 guardarXML(configuracion);
 return true;
 }
 }
 return false;
 }
 }*/
//v 07/09/2017
String archivoConfigXML = "../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

enum EstadoModulo {
  APAGADO, LOCAL, REMOTO
}
final EstadoModulo[] EstadoModuloList = new EstadoModulo[]{EstadoModulo.APAGADO, EstadoModulo.LOCAL, EstadoModulo.REMOTO};
public int EstadoModuloToInt(EstadoModulo estado) {
  return estado==EstadoModulo.APAGADO?0:estado==EstadoModulo.LOCAL?1:2;
};

class ConfiguracionCOD05 {
  ConfigModulo lienzo, observador, carrete;
  boolean panelConexiones = false;

  ConfiguracionCOD05() {
    lienzo = new ConfigModulo().Iniciar("lienzo", 12010);
    observador = new ConfigModulo().Iniciar("observador", 12020);
    carrete = new ConfigModulo().Iniciar("carrete", 12030);
  }
  class ConfigModulo {
    String id = "indefinido";
    String ip = "127.0.0.1";
    int puerto = 12000;
    EstadoModulo estado = EstadoModulo.LOCAL;

    public ConfigModulo Iniciar(String id, int puerto) {
      this.id = id;
      this.puerto = puerto;
      return this;
    }

    public void cargar(XML xml) {
      id = xml.getString("id", id);
      ip = xml.getString("ip", ip);
      puerto = xml.getInt("puerto", puerto);
      int estadoInt = xml.getInt("estado", -1);
      if (estadoInt != -1) estado = EstadoModuloList[estadoInt];
    }    
    public XML generar() {
      XML xml = new XML("ConfigModulo");
      xml.setString("id", id);
      xml.setString("ip", ip);
      xml.setInt("puerto", puerto);
      xml.setInt("estado", EstadoModuloToInt(estado));
      return xml;
    }
  }
  public void cargar(XML xml) {
    if (lienzo==null)lienzo = new ConfigModulo().Iniciar("lienzo", 12010);
    if (observador==null)observador = new ConfigModulo().Iniciar("observador", 12020);
    if (carrete==null)carrete = new ConfigModulo().Iniciar("carrete", 12030);
    if (xml != null) {
      panelConexiones = xml.getInt("panelConexiones", panelConexiones?1:0)==1;
      XML[] configs = xml.getChildren("ConfigModulo");
      for (ConfigModulo cm : new ConfigModulo[]{lienzo, observador, carrete}) {
        for (XML cxml : configs) {
          if (cm.id.equals(cxml.getString("id", ""))) cm.cargar(cxml);
        }
      }
    }
  }
  public XML guardar(String nombre) {
    XML xml = new XML(nombre);
    xml.setInt("panelConexiones", panelConexiones?1:0);
    for (ConfigModulo cm : new ConfigModulo[]{lienzo, observador, carrete}) {
      xml.addChild(cm.generar());
    }
    return xml;
  }
}
class Consola {
  Modos modos;
  ColeccionCategorias cC;
  //ColeccionEstimulos cE;
  ColeccionOpciones cO;
  ColeccionMaquinarias cM;
  Monitor monitor;

  float centroX = bdd.ruedaX;
  float centroY = bdd.ruedaY;
  float tam = bdd.ruedaDiametro;

  String[] datosDeSistema_nombresCategorias;
  String[] datosDeSistema_nombresModificadores;
  String[] datosDeSistema_nombresModificadoresExistentes;
  String[] datosDeSistema_nombresOpciones;
 // String[] datosDeSistema_nombresEstimulos;
  String[] datosDeSistema_nombresMaquinarias;

  boolean[]datosDeAPI_opcionesDeNavegacion;

  int cerrado=0;
  int nivel=0;
  int eje=0;

  int limiteSelector;
  int selector;

  boolean dosNivelesDeSeleccion;

  PVector cursor = new PVector();



  Consola() {
    modos = new Modos();
    modos.inicializar(new PVector(centroX, centroY), tam);
    cC = new ColeccionCategorias();
    cO = new ColeccionOpciones();
  // cE = new ColeccionEstimulos();
    cM = new ColeccionMaquinarias();    
    cC.inicializar(datosDeSistema_nombresCategorias, datosDeAPI_opcionesDeNavegacion, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);
    cO.inicializar(datosDeSistema_nombresOpciones);    
   // cE.inicializar(datosDeSistema_nombresEstimulos);    
    cM.inicializar(datosDeSistema_nombresMaquinarias);    
    monitor = new Monitor();
  }

  Consola(int[][] paleta_) { 

    modos = new Modos(paleta_, new PVector(centroX, centroY), tam);
    cC = new ColeccionCategorias(paleta_, new PVector(centroX, centroY), tam);
    cO = new ColeccionOpciones(paleta_, new PVector(centroX, centroY), tam);
  //  cE = new ColeccionEstimulos(paleta_, new PVector(centroX, centroY), tam);
    cM = new ColeccionMaquinarias(paleta_, tam);
    cC.inicializar(datosDeSistema_nombresCategorias, datosDeAPI_opcionesDeNavegacion, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);
    cO.inicializar(datosDeSistema_nombresOpciones);    
   // cE.inicializar(datosDeSistema_nombresEstimulos);    
    cM.inicializar(datosDeSistema_nombresMaquinarias);    
    monitor = new Monitor(paleta_, new PVector(centroX, centroY), tam);
  }


  public void ejecutar() {
    base();
    if (modos.getModo().equals(ESPERA)) {
      //mandarMensaje("/holi...ten un buen dia");
      monitor.dibujar(cerrado, nivel, eje/*,width/2,height/2*/);
    } else if (modos.getModo().equals(AGREGAR)) {       
      monitor.dibujar(cerrado, nivel, eje);
      cC.dibujarCategoria();
    } else if (modos.getModo().equals(ELIMINAR)) {      
      monitor.dibujar(cerrado, nivel, eje);
      cC.dibujarCategoria();
    //} else if (modos.getModo().equals(ESTIMULOS)) {     
      /* int contadorSeleccionEstimulo = cC.getContadorSeleccionEstimulo(cursor);//limites 0,400
       monitor.dibujar(cerrado, nivel, eje, contadorSeleccionEstimulo);       
       fill(255);
       ellipse(cursor.x, cursor.y, 10, 10);      
       if (cE.getSeleccionarEstimulo()) {
       botonesAccionesN2();
       mandarMensaje("/pedir/estimulos/totales");
       }
       cE.dibujar();*/
    } else if (modos.getModo().equals(OPCIONES)) {
      monitor.dibujar(cerrado, nivel, eje);
      cO.dibujar();
    } else if (modos.getModo().equals(MAQUINARIAS)) {
      monitor.dibujar(cerrado, nivel, eje);
      cC.dibujarCategoria();
    }
    modos.ejecutar();

    if (conectadoConSistema) { //conectado con sistema es una variable global que posdria paserle como variable
      cM.dibujar();
    }

    if (bdd.interaccionConMouse) {
      mouse();
    }
  }

  public void setCursor(float x, float y) {
    cursor.set(x, y);
  }

  public void actualizarIconos(int cerrado_, int nivel_, int eje_) {
    cerrado = cerrado_;
    nivel = nivel_;
    eje = eje_;
  }

  public void activarAnadir() {
    modos.setModo(AGREGAR);
    limitarSelector(); 
    //botonesAccionesN1();
  }
  public void activarQuitar() {
    modos.setModo(ELIMINAR); 
    limitarSelector();
    // botonesAccionesN1();
  }

  public void activarOpciones() {
    modos.setModo(OPCIONES); 
    limitarSelector();
    // botonesAccionesN1();
  }
 /* void activarEstimulos() {
    modos.setModo(ESTIMULOS); 
    limitarSelector();
    //  botonesAccionesN1();
  }*/
  public void activarEspera() {
    modos.setModo(ESPERA); 
    limitarSelector();
    // botonesAccionesN1();
  }
  public void activarMaquinarias() {
    modos.setModo(MAQUINARIAS); 
    limitarSelector();
    // botonesAccionesN1();
  }

  public void aumentoSelector() {
    if (!modos.getModo().equals(ESPERA)) {     
      selector++;
      revisarSensible();
    }
  }

  public void decrementoSelector() {
    if (!modos.getModo().equals(ESPERA)) {     
      selector--;
      revisarSensible();
    }
  }

  public void revisarSensible() {    
    selector = (selector+limiteSelector)%limiteSelector;
    selector = PApplet.parseInt(constrain(selector, 0, limiteSelector));
    if (modos.getModo().equals(AGREGAR)) {     
      cC.setSensible(selector);
      cM.setSensible(0);
      modos.setIconoCentral(cC.getSensible(selector), cC.getColorSensible(selector));
    } else if (modos.getModo().equals(ELIMINAR)) {       
      cC.setSensible(selector);
      cM.setSensible(0);
      modos.setIconoCentral(cC.getSensible(selector), cC.getColorSensible(selector));
   // } else if (modos.getModo().equals(ESTIMULOS)) {
      //cM.setSensible(0);
    } else if (modos.getModo().equals(OPCIONES)) {
      cO.setSensible(selector);
      cM.setSensible(0);
      modos.setIconoCentral(cO.getSensible(selector), cC.getColorSensible(selector));
    } /*else if (modos.getModo().equals(OPCIONES)) {
     cC.setSensible(selector);
     cM.setSensible(0);
     modos.setIconoCentral(cC.getSensible(selector),cC.getColorSensible(selector));
     }*/
    else if (modos.getModo().equals(MAQUINARIAS)) {
      cM.setSensible(selector);
      modos.setIconoCentral(cM.getSensible(selector), cM.getColorSensible(selector));
    }
  }



  boolean seDetuboElMouse=false;
  boolean seMueveElMouse=false;
  public void mouse() {
    cC.mouse();  

    if (mouseX != pmouseX) {
      seDetuboElMouse = false;
      seMueveElMouse = true;
    }

    if (seMueveElMouse && !seDetuboElMouse && mouseX == pmouseX) {
      seDetuboElMouse = true;
      seMueveElMouse = false;
    }
    if (seDetuboElMouse) {
      if (!(modos.getModo().equals(ELIMINAR)) && !(modos.getModo().equals(ESPERA)) ) {
        Modificador modSeleccionado = cC.getSensibleMouse_modificador();    
        modos.setIconoCentral(modSeleccionado, cC.getColorSensibleMouse());
        if ( modSeleccionado!=null) {
          if (modSeleccionado.mods>0) {
            activarQuitar();
          }
        }
      }
      if (!(modos.getModo().equals(AGREGAR)) && !(modos.getModo().equals(ESPERA)) ) {
        Modificador modSeleccionado = cC.getSensibleMouse_modificador();   
        modos.setIconoCentral(modSeleccionado, cC.getColorSensibleMouse()); 
        if ( modSeleccionado!=null) {
          if (modSeleccionado.mods<1) {
            activarAnadir();
          }
        }
      }
    }
    seDetuboElMouse = false;
  }

  public void mousePressed() {   
    String nombreModSeleccionado = cC.getSensibleMouse();    
    if (nombreModSeleccionado != null) {
      if (modos.getModo().equals(AGREGAR)) {
        if (esOpcionDeNavegacion(nombreModSeleccionado)) {
          activarNavegacion(nombreModSeleccionado);
        } else {
          OscMessage mensajeModificadores;
          mensajeModificadores = new OscMessage("/agregar/modificadores");
          mensajeModificadores.add(nombreModSeleccionado); 
          oscP5.send(mensajeModificadores, sistema);
        }
      } else if (modos.getModo().equals(ELIMINAR)) {
        if (esOpcionDeNavegacion(nombreModSeleccionado)) {
          activarNavegacion(nombreModSeleccionado);
        } else {        
          int cantModSeleccionado = cC.getSensibleMouse_modificador().getCant();
          OscMessage mensajeModificadores;
          mensajeModificadores = new OscMessage("/quitar/modificadores");
          mensajeModificadores.add(nombreModSeleccionado+"_"+(cantModSeleccionado-1));           
          oscP5.send(mensajeModificadores, sistema);
        }
      }
    }
    //-------Para que cada vez que se preciona el mouse se revise el estado de 
    //-------los modificadores
    seDetuboElMouse = true;
  }

  public void botonesAccionesN2() {
    if (modos.getModo().equals(AGREGAR)) {
      String nombreModSeleccionado = cC.getSensible(selector);
      if (esOpcionDeNavegacion(nombreModSeleccionado)) {
        activarNavegacion(nombreModSeleccionado);
      } else {
        OscMessage mensajeModificadores;
        mensajeModificadores = new OscMessage("/agregar/modificadores");
        mensajeModificadores.add(nombreModSeleccionado); 
        oscP5.send(mensajeModificadores, sistema);
      }
    } else if (modos.getModo().equals(ELIMINAR)) {
      String nombreModSeleccionado = cC.getSensible(selector);
      if (esOpcionDeNavegacion(nombreModSeleccionado)) {
        activarNavegacion(nombreModSeleccionado);
      } else {        
        int cantModSeleccionado = cC.getModSensible(selector).getCant();
        OscMessage mensajeModificadores;
        mensajeModificadores = new OscMessage("/quitar/modificadores");
        mensajeModificadores.add(nombreModSeleccionado+"_"+(cantModSeleccionado-1));           
        oscP5.send(mensajeModificadores, sistema);
      }
   // } else if (modos.getModo().equals(ESTIMULOS)) {
      /*OscMessage mensajeOpciones;
       mensajeOpciones = new OscMessage("/seleccionar/estimulo");
       // mensajeOpciones.add(nombreModSeleccionado);           
       oscP5.send(mensajeOpciones, sistema);*/
    } else if (modos.getModo().equals(OPCIONES)) {
      String nombreModSeleccionado = cO.getSensible(selector);
      OscMessage mensajeOpciones;
      mensajeOpciones = new OscMessage("/accion/opciones");
      mensajeOpciones.add(nombreModSeleccionado);           
      oscP5.send(mensajeOpciones, sistema);
    } else if (modos.getModo().equals(MAQUINARIAS)) {
      String nombreMaqSeleccionado = cM.getSensible(selector);
      OscMessage mensajeOpciones;
      mensajeOpciones = new OscMessage("/set/maquinaria");
      mensajeOpciones.add(nombreMaqSeleccionado);           
      oscP5.send(mensajeOpciones, sistema);
      activarAnadir();
    }

    // activarEspera();
  }

  public boolean esOpcionDeNavegacion(String nombre) {
    boolean navegable = false;
    for (int i=0; i<opcionesDeNavegacion.length; i++) {
      if (nombre.equals(opcionesDeNavegacion[i]))
        navegable = true;
    }
    return navegable;
  }


  public void activarNavegacion(String aDonde) {
    if (aDonde.equals(MAQUINARIAS)) {
      activarMaquinarias();
    } else if (aDonde.equals(OPCIONES)) {
      activarOpciones();
    }
  }


  public void mandarMensaje(String mensaje) {   
    OscMessage mensajeModificadores;
    mensajeModificadores = new OscMessage(mensaje);
    oscP5.send(mensajeModificadores, sistema);
  }

  public void limitarSelector() {
    if (modos.getModo().equals(ESPERA)) {
      limiteSelector = 0; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(AGREGAR)) {
      limiteSelector = datosDeSistema_nombresModificadores.length; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(ELIMINAR)) {
      limiteSelector = datosDeSistema_nombresModificadores.length; //cambiar con no se nada supongo
   // } else if (modos.getModo().equals(ESTIMULOS)) {
     // limiteSelector = datosDeSistema_nombresEstimulos.length; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(OPCIONES)) {
      limiteSelector = datosDeSistema_nombresOpciones.length; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(MAQUINARIAS)) {
      limiteSelector = datosDeSistema_nombresMaquinarias.length; //cambiar con no se nada supongo
    }
  }

  String[] opcionesDeNavegacion = {
    MAQUINARIAS
  } 
  ;
  public void renovarDatosCategorias( ArrayList<String>nombres_, ArrayList<String>nombresCategorias_, ArrayList<String>nombresExistentes_) {

    dosNivelesDeSeleccion = true;
    int c = (nombres_.size()+opcionesDeNavegacion.length);
    int ce = nombresExistentes_.size();

    datosDeSistema_nombresCategorias = new String[c];
    datosDeSistema_nombresModificadores  = new String[c];
    datosDeAPI_opcionesDeNavegacion = new boolean[c];
    datosDeSistema_nombresModificadoresExistentes  = new String[ce];

    for (int i=0; i<opcionesDeNavegacion.length; i++) {
      datosDeSistema_nombresModificadores[i] = opcionesDeNavegacion[i];
      datosDeSistema_nombresCategorias[i] = opcionesDeNavegacion[i];
      datosDeAPI_opcionesDeNavegacion[i] = true;
    }

    for (int i=opcionesDeNavegacion.length; i<c; i++) {
      println(i-opcionesDeNavegacion.length);
      datosDeSistema_nombresModificadores[i] = (String)nombres_.get(i-opcionesDeNavegacion.length);
      datosDeSistema_nombresCategorias[i] = (String)nombresCategorias_.get(i-opcionesDeNavegacion.length);
      datosDeAPI_opcionesDeNavegacion[i] = false;
    }

    for (int i=0; i<ce; i++) {
      String[] n = split(nombresExistentes_.get(i), "_");
      datosDeSistema_nombresModificadoresExistentes[i] = n[0];
    }

    cC.inicializar(datosDeSistema_nombresCategorias, datosDeAPI_opcionesDeNavegacion, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);

    limitarSelector();
  }

  /*void renovarDatosEstimulos( ArrayList<String>nombres_) {

    int c = nombres_.size();
  //  datosDeSistema_nombresEstimulos  = new String[c];
    for (int i=0; i<c; i++) {
      datosDeSistema_nombresEstimulos[i] = (String)nombres_.get(i);
    }

    cE.inicializar(datosDeSistema_nombresEstimulos);

    limitarSelector();
  }*/

  public void renovarDatosOpciones( ArrayList<String>nombres_) {

    int c = nombres_.size();
    datosDeSistema_nombresOpciones  = new String[c];
    for (int i=0; i<c; i++) {
      datosDeSistema_nombresOpciones[i] = (String)nombres_.get(i);
    }

    cO.inicializar(datosDeSistema_nombresOpciones);

    limitarSelector();
  }

  public void renovarDatosMaquinarias( ArrayList<String>nombres_) {

    int c = nombres_.size();
    datosDeSistema_nombresMaquinarias  = new String[c];
    for (int i=0; i<c; i++) {
      datosDeSistema_nombresMaquinarias[i] = (String)nombres_.get(i);
      println( datosDeSistema_nombresMaquinarias[i]);
    }

    cM.inicializar(datosDeSistema_nombresMaquinarias);

    limitarSelector();
    println();
  }

  public void agregarMod(String cual) {
    cC.agregar(cual);
  }
  public void quitarMod(String cual) {

    cC.quitar(cual);
  }

  public void base() {
    noStroke();
    fill(paleta[1][0]);
    rect(0, 0, width, height);
    float tam_ = tam*350/100;
    fill(paleta[1][1]);
    ellipse(centroX, centroY, tam_, tam_);
  }
}
Reloj reloj = new Reloj();
ConsolaDebug consolaDebug = new ConsolaDebug();

public void consolaDebug() {
  reloj.actualizar();
  consolaDebug.ejecutar();
}

public final class ConsolaDebug {

  private String texto;
  private ArrayList<Alerta> alertas = new ArrayList<Alerta>();
  private int colorTexto, colorAlerta, colorSuperAlerta;
  private int tamanoTexto, tamanoAlerta;
  private boolean debug;

  private boolean verFps, verDatos, verAlertas;

  private static final float LEADIN = 1.5f; //--- NUEVO!

  public ConsolaDebug() {
    texto = "";
    colorTexto = color( 0xff000000 );//color( 255 );
    colorAlerta = color(175, 194, 43);//#FF0000
    tamanoTexto = PApplet.parseInt( height * 0.12f ); //int( height * 0.023 ); //tamanoTexto = 20;
    tamanoAlerta = PApplet.parseInt( height * 0.12f ); //int( height * 0.023 ); //tamanoAlerta = 20;

    debug = verFps = verDatos = verAlertas = true;
  }

  //--------------------------------------- METODOS PUBLICOS

  //GETERS AND SETERS
  public void setDebug( boolean debug ) {
    this.debug = debug;
  }

  public void setVerFps( boolean verFps ) {
    this.verFps = verFps;
  }

  public void setVerDatos( boolean verDatos ) {
    this.verDatos = verDatos;
  }

  public void setVerAlertas( boolean verAlertas ) {
    this.verAlertas = verAlertas;
  }

  public boolean getDebug() {
    return debug;
  }

  public boolean getVerFps() {
    return verFps;
  }

  public boolean getVerDatos() {
    return verDatos;
  }

  public boolean getVerAlertas() {
    return verAlertas;
  }
  //--------

  public void println( String texto ) {
    this.texto += texto + "\n";
  }

  public void printlnAlerta( String alerta ) {
    alertas.add( new Alerta( alerta ) );
    System.out.println( alerta );
  }

  public void printlnAlerta( String alerta, int c ) {
    alertas.add( new Alerta( alerta, c ) );
    System.out.println( alerta );
  }

  public void ejecutar() {

    if ( !verDatos ) texto = "";
    if ( verFps ) texto = "fps: " + nf( frameRate, 0, 2 ) + "\n" + texto;

    if ( debug ) ejecutarDebug();
    else ejecutarNoDebug();
    texto = "";
  }

  //--------------------------------------- METODOS PRIVADOS

  private void ejecutarDebug() {
    pushStyle();

    textAlign( LEFT, TOP );
    textSize( tamanoTexto );
    textLeading( tamanoTexto * LEADIN ); 

    noStroke();

    //NUEVO rectangulo negro de fondo

    fill( 255 );
    int desde = 0, hasta = 0, iteracion = 0;
    while ( texto.indexOf( "\n", desde ) > 0 ) {

      hasta = texto.indexOf( "\n", desde );
      String aux = texto.substring( desde, hasta );

      rect( 0, iteracion * (tamanoTexto * LEADIN), textWidth( aux ) + 3, tamanoTexto * ( LEADIN * 1.1666666f ) );

      desde = hasta + 1;
      iteracion++;
    }

    //

    fill( colorTexto );
    text( texto, 0, 3 );
    if ( !texto.equals("") ) System.out.println( texto );

    textAlign( RIGHT, BOTTOM );
    textSize( tamanoAlerta );
    imprimirAlertas( verAlertas );

    popStyle();
  }

  private void ejecutarNoDebug() {
    if ( !texto.equals("") ) System.out.println( texto );
    imprimirAlertas( false );
  }

  private void imprimirAlertas( boolean debug ) {

    float posY = tamanoAlerta + tamanoAlerta * (LEADIN * 0.16666666f) ;//0.25

    for ( int i = alertas.size() - 1; i >= 0; i-- ) {

      Alerta a = alertas.get( i );
      a.ejecutar();

      if ( a.getEstado() == Alerta.ESTADO_ELIMINAR ) {
        alertas.remove( i );
      } else if ( debug ) {

        //------ NUEVO rectangulo negro de fondo

        if ( a.getEstado() == Alerta.ESTADO_MOSTRAR )
          fill( 0 );
        else
          fill( 0, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );

        rect( width - textWidth( a.getAlerta() ) - 5, posY- tamanoAlerta * ( LEADIN * 0.875f ), textWidth( a.getAlerta() ) + 5, tamanoAlerta * LEADIN );

        //------
        int colorTexto = a.tengoColor?a.m_color:colorAlerta;
        if ( a.getEstado() == Alerta.ESTADO_MOSTRAR ) {
          fill( colorTexto );
        } else {
          fill( colorTexto, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );
        }
        text( a.getAlerta(), width, posY );
        posY += tamanoAlerta * LEADIN;

        if ( posY > height && i - 1 >= 0 ) {
          removerAlertasFueraDePantalla( i - 1 );
          return;
        }
      }
    }//end for
  }

  private void removerAlertasFueraDePantalla( int desde ) {
    for ( int i = desde; i >= 0; i-- )
      alertas.remove( i );
  }

  //clase interna y miembro
  public class Alerta {

    private String alerta;
    int m_color;
    boolean tengoColor;
    private int estado;
    public static final int
      ESTADO_MOSTRAR = 0, 
      ESTADO_DESAPARECER = 1, 
      ESTADO_ELIMINAR = 2;

    private int tiempo;
    public static final int
      TIEMPO_MOSTRAR = 5000, //3000
      TIEMPO_DESAPARECER = 2000;

    public Alerta( String alerta ) {
      this.alerta = alerta;
      estado = ESTADO_MOSTRAR;
      tengoColor = false;
    }

    public Alerta( String alerta, int c ) {
      this.alerta = alerta;
      m_color = c;
      estado = ESTADO_MOSTRAR;
      tengoColor = true;
    }

    //------------------------------ METODOS PUBLICOS

    public String getAlerta() {
      return alerta;
    }

    public int getEstado() {
      return estado;
    }

    public int getTiempo() {
      return tiempo;
    }

    public void ejecutar() {
      tiempo += reloj.getDeltaMillis();
      if ( estado == ESTADO_MOSTRAR && tiempo > TIEMPO_MOSTRAR ) {
        estado = ESTADO_DESAPARECER;
        tiempo = 0;
      } else if ( estado == ESTADO_DESAPARECER && tiempo > TIEMPO_DESAPARECER ) {
        estado = ESTADO_ELIMINAR;
      }
    }
  }
}

public class Reloj {

  private int millisActual, millisAnterior, deltaMillis;

  public Reloj() {
  }

  public int getDeltaMillis() {
    return deltaMillis;
  }

  public void actualizar() {
    millisAnterior = millisActual;
    millisActual = millis();
    deltaMillis = millisActual - millisAnterior;
  }
}
class Controlador {

  Consola consola;

  Controlador(Consola consola_) {

    consola = consola_;
  }


  public void aceptar() {
    //consola.activar();
    consola.botonesAccionesN2();
    /* if (consola.modos.getModo().equals(ESPERA)) {
     consola.mandarMensaje("/holi...ten un buen dia");
     } else if (consola.modos.getModo().equals(AGREGAR)) {
     consola.mandarMensaje("/pedir/modificadores/existentes");
     } else if (consola.modos.getModo().equals(ELIMINAR)) {
     consola.mandarMensaje("/pedir/modificadores/existentes");
     } else if (consola.modos.getModo().equals(ESTIMULOS)) {
     consola.mandarMensaje("/pedir/estimulos/totales");
     } else if (consola.modos.getModo().equals(OPCIONES)) {
     consola.mandarMensaje("/pedir/opciones");
     }*/
  }

  public void izquierda() {
    consola.decrementoSelector();
  }

  public void derecha() {
    consola.aumentoSelector();
  }

  public void anadir() {
    if (!(consola.modos.getModo().equals(MAQUINARIAS))) {
      consola.activarAnadir();
    }
  }
  public void quitar() {
    consola.activarQuitar();
  }
  public void opciones() {
    consola.activarOpciones();
  }
  /*void estimulos() {
    consola.activarEstimulos();
  }*/

  public void cancelar() {    

    //consola.cancelar();
  }

  public void actualizarIconos(int cerrado, int nivel, int eje) {   
    consola.actualizarIconos(cerrado, nivel, eje);
  }
}
class Estimulo extends Opcion{

   Estimulo(String nombre_) {
    nombre = nombre_;
    //  modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
      paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    iconos = new Iconos(PApplet.parseInt(t));
    pos = new PVector();
    posCentro = new PVector();
  }

  Estimulo(String nombre_, int[][] paleta_) {
    nombre = nombre_;
    //modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = paleta_;
    iconos = new Iconos(PApplet.parseInt(t));
    pos = new PVector();
    posCentro = new PVector();
  }
 
}
public void setPaleta() {
  paleta = new int[4][];

  int[] x1 = {
    color(0xff000000), color(0xffFFFFFF)
  };
  paleta[0] = x1;

  int[] x2 = {
    color(0xff25282D)/*baseDeLaBaseDeLaRueda*/, color(0xff2D3235)/*baseCirculardelarueda*/, color(0xff202529)/*baseDeMonitoryMaquinarias*/, color(0xff818181)
  };

  paleta[1] = x2;

  int[] x3 = {
    color(0xff1b1922), color(0xff25282D), color(0xffAFC22B), color(0xffBE4041), color(0xff1F2227)//conector
  };

  paleta[2] = x3;

  int[] x4 = {
    color(0xffB44343), color(0xffB47A43), color(0xffB4B243), color(0xff7DB443), color(0xff45B443), color(0xff43B478), 
    color(0xff43B4B0), color(0xff4380B4), color(0xff4348B4), color(0xff7543B4), color(0xffAD43B4), color(0xffB44382)
  };

  paleta[3] = x4;
}
//---- PARA LOS ICONOS:
//---- primero que las iamgenes tengan los nombres y no tenerlos todos aqui
//---- entonces el nombre sale de las iamgenes y no al reves
//---- segundo que haya un contructor donde le pasas un arreglo de nombres y te 
//---- crea un hashmap con lso iconos solo de esos string que encesesita asi no usa 
//---- espacio de memoria de gana
//---- tercero poner en lsonombre de lso iconos si son modificadores categorias u otros
//---- asi hay otor contructor al que le pasas solo un string diciendole modificadores o categorias
//---- y te pasa el hashmap de es gran conglomerado de cosas... por la misma razon que el anterior.
//---- como lso nombres estos tiene que coincidir con lso de los modificadores de el lienzo hay que
//---- crea algo para dividir las iamgenes algo como mod/nombre_del_mod.png asi se peude ahce run 
//---- split primeor por el '.' pra extraer el modo y segundo por el '/' pARA SACAr solo el nombre y 
//---- si es un modificador.

class Iconos {
  HashMap<String, PImage> iconos;
  float tamanioTexto;
  int w, h;
  String[] nombres = {
    "Sin Imagen", "Colision Con Joint", "Aplicar Colisiones", "Colision Simple", "Dibujar Reas", "Varios", 
    "Vizualizar Particulas", "Dibujar Circulo", "Aplicar Fuerza", "Atraccion Al Centro", "Friccion Global", 
    "Escena", "Dibujar Flecha", "Espacio Cerrado", "Espacio Toroidal", "Dibujar Rastro Circular", 
    "Rastro Normal", "Rastro Elastico", "Forma De Rastro", "Dibujar Rastro Triangular", "Dibujar Rastro", 
    "Flock Separacion", "Flock Cohesion", "Flocking", "Transparencia", "Alfa Segun Velocidad", 
    "Aplicar Movimiento", "Dibujar Rastro Lineal", "Reset Lluvia", "Fuerzas Por Semejanza", "Flock Alineamiento", 
    "Reset Lluvia", "Fuerzas Por Semejanza", "Mover", "Gravedad", "maquinarias", "espera", "opciones", "eliminar", 
    "agregar", "Lumiere", "Cohl", "Melies", "Guy Blache", "Estimulos", "Alfa Segun Cercania", 
    "Atraccion Al Torso", "Mod Fuerzas Por Semejanza", "Egoespacio", "Paleta Color", "Paleta Personalizada", "Paleta Default", 
    "Dibujar Rastro Cuadrado", "Dibujar Rastro Shape", "Dibujar Cuadrado", "cod05 1", "cod05 2", ABIERTO, CERRADO, N_MEDIO, 
    N_ALTO, N_BAJO, EJE_DERECHA, EJE_IZQUIERDA, EJE_CENTRO, MONITOR_BASE
  };

  Iconos(int t) {
    iconos = new HashMap<String, PImage>();
    PImage imagenVacia = imagenVacia();
    imagenVacia.resize(t, t);      
    iconos.put(nombres[0], imagenVacia);
    for (int i=1; i<66; i++) {
      PImage icono = loadImage("iconos/blancos/icono ("+(i)+").png");  
      int rw = icono.width>=icono.height?t:(icono.width*t/icono.height);
      int rh = icono.height>=icono.width?t:(icono.height*t/icono.width);
      w = rw;
      h = rh;
      icono.resize(rw, rh);      
      iconos.put(nombres[i], icono);
      tamanioTexto = t/3;
    }
  }

  public PImage imagenVacia() {
    PImage img = createImage(100, 100, ARGB);
    img.loadPixels();
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 100; j++) {
        int pixelIndex = i+j*img.width;
        if (((i-j)<4 && (i-j)>-4 && dist(i, j, 50, 50) <= 50) || (dist(i, j, 50, 50) <= 50 && dist(i, j, 50, 50)>50-4) ) {
          img.pixels[pixelIndex] = color(255);
        } else  /*if (dist(i, j, 50, 50)<50) {
         img.pixels[pixelIndex] = color(255);
         } else */ {
          img.pixels[pixelIndex] = color(255, 0);
        }
      }
    }
    img.updatePixels();
    return img;
  }

  public void dibujar(String nombre, float x, float y) {
    imageMode(CENTER);
    PImage icono = iconos.get(nombre);
    if (icono!=null) {
      image(icono, x, y);
    } else {
      icono = iconos.get("Sin Imagen");
      image(icono, x, y);
      if (nombre!=null) {
        pushStyle();
        fill(255);
        textAlign(CENTER, CENTER);
        textSize(tamanioTexto);
        text(nombre, x, y);
        popStyle();
      }
    }
  }
}
////-------------------- el nombre de netAddres y de oscP5 deberian ser especificados en el constructor para un futuro 

/*class IpManager {
  ControlP5 cp5;
  float alto = height/20;
  float margenSuperior = height/2-alto*5;

  float franjaW = width/10;
  float franjaH = height/10;

  boolean escondido;
  Input ipEnvio;
  Input puertoEnvio;
  Input puertoRecivo;
  Boton botonConectar;

  IpManager(ControlP5 cp5_) {
    cp5 = cp5_;
  }

  void set() {

    /* cp5.addTextfield("IP")
     .setPosition(width-franjaW*3, margenSuperior)
     .setSize(int(franjaW*3.5), int(alto))
     //.setFont()
     //.setFocus(true)
     // .setColor(color(255, 0, 0))
     .setColor(color(255)) 
     .setColorActive(color(paleta[2][4]))
     .setColorBackground(color(paleta[2][4], 100 ))           
     .setColorForeground(color(paleta[2][1], 100)) 
     // .setColorValueLabel(paletaDeColor[0]) 
     ;
     
     cp5.addTextfield("Puerto Envio")
     .setPosition(width-franjaW*3, margenSuperior+alto*2)
     .setSize(int(franjaW*3.5), int(alto))
     //   .setFont(createFont("arial", 20))
     .setAutoClear(false)
     .setColor(color(255)) 
     .setColorActive(color(paleta[2][4]))
     .setColorBackground(color(paleta[2][4], 100 ))           
     .setColorForeground(color(paleta[2][1], 100)) 
     ;
     
     cp5.addBang("Conectar")
     .setPosition(width-franjaW*3, margenSuperior+alto*8)
     .setSize(int(franjaW*2.5), int(alto))
     // .setColorActive(color(paleta[2][4][3], 100))
     .setColorBackground(color(paleta[2][2], 100 ))           
     .setColorForeground(color(paleta[2][2], 100)) 
     .getCaptionLabel().align(ControlP5.CENTER, ControlP5.CENTER)
     
     ;    
     
     cp5.addTextfield("Puerto Recivo")
     .setPosition(width-franjaW*3, margenSuperior+alto*4)
     .setSize(int(franjaW*3.5), int(alto))
     .setColor(color(255)) 
     .setColorActive(color(paleta[2][4]))
     .setColorBackground(color(paleta[2][4], 100 ))           
     .setColorForeground(color(paleta[2][1], 100)) 
     // .setAutoClear(false)
     ;*/
  /*  ipEnvio = new Input();
    ipEnvio.setPos(new PVector(width-franjaW*3, margenSuperior));
    ipEnvio.setSize(int(franjaW*3.5)/2, int(alto)/2);
    ipEnvio.setNombre("IP");

    puertoEnvio = new Input();
    puertoEnvio.setPos(new PVector(width-franjaW*3, margenSuperior+alto*2));
    puertoEnvio.setSize(int(franjaW*3.5)/2, int(alto)/2);
    puertoEnvio.setNombre("Puerto Envio");

    puertoRecivo = new Input();
    puertoRecivo.setPos(new PVector(width-franjaW*3, margenSuperior+alto*4));
    puertoRecivo.setSize(int(franjaW*3.5)/2, int(alto)/2);
    puertoRecivo.setNombre("Puerto Recivo");    

    botonConectar = new Boton();
    botonConectar.setPos(new PVector(width-franjaW*3, margenSuperior+alto*8));
    botonConectar.setSize(80, 20);
    botonConectar.setNombre("Conectar");

    esconder();
  }

  void esconder() {
    if (!escondido) {
      //franjaW = -width;
      escondido=true;
    } else {
      //franjaW = width/10;
      escondido=false;
    }

    /*  cp5.get(Textfield.class, "IP") .setPosition(width-franjaW*3, margenSuperior);
     cp5.get(Textfield.class, "Puerto Envio")  .setPosition(width-franjaW*3, margenSuperior+alto*2);
     cp5.get(Textfield.class, "Puerto Recivo") .setPosition(width-franjaW*3, margenSuperior+alto*4);
     cp5.get(Bang.class, "Conectar")  .setPosition(width-franjaW*2.8, margenSuperior+alto*8);
     */
 /* }

  void fondo() {
    if (!escondido) {
      pushStyle();
      rectMode(CORNER);
      fill(0, 100);
      noStroke();
      rect(width-franjaW*3-franjaW*0.1, 0, franjaW*4, height);
      popStyle();
      ipEnvio.dibujar();  
      puertoEnvio.dibujar();
      puertoRecivo.dibujar();
      botonConectar.dibujar();
    }
  }
}*/
class Maquinaria extends Opcion {

  String nombre;
  boolean sensible;
  int[][] paleta;
  Iconos iconos;

  Maquinaria(String nombre_) {
    nombre = nombre_;
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }

  Maquinaria(String nombre_, int[][] paleta_) {
    nombre = nombre_;
    paleta = paleta_;
  }

  public void setSensible(boolean sensible_/*, boolean estado_*/) {
    sensible = sensible_;
  }

  public String getNombre() {
    return nombre;
  }

  /*void dibujar(float x, float y, color c) {
   noStroke();
   pushMatrix();
   if (sensible) {
   enSensible( x, y, t);
   } 
   fill(c);
   
   ellipse(x, y, t, t);
   textSize(14);
   fill(255);
   float angulo = atan2(y-categoria.posCentro.y, x-categoria.posCentro.x);
   pushMatrix();
   translate(x, y);
   if (angulo>radians(90) || angulo<radians(-90)) {
   textAlign(RIGHT, CENTER);
   rotate(angulo-PI);
   } else {
   textAlign(LEFT, CENTER);
   rotate(angulo);
   }
   text(nombre, 0, 0);
   popMatrix();
   }*/

  public void dibujarIconos(float x, float y, float t, int col) {
    if (iconos == null)
      iconos = new Iconos(PApplet.parseInt(t*27/100));
    noStroke();
    if (sensible) {
      enSensible( x, y, t);
    } 
    tint(col);
    iconos.dibujar(nombre, x, y);

 
  }

  public void enSensible(float x, float y, float t) {
    pushStyle();
    noFill();
    strokeWeight(1.5f);
    stroke(paleta[2][3]);
    ellipse(x, y, (t*35/100)*1.1f, (t*35/100)*1.1f);
    popStyle();
  }
}
class Modificador {

  String nombre;
  boolean sensible;
  boolean hover;
  boolean hoverExtendido; //---------------como el icono es muy pequeño el espaio de hover es muy reducido
  //---------------- entonces esto es para extender el espacio de hover sin embargo mantener 
  //----------------el click solo en el espacio de incono
  Categoria categoria;
  int[][] paleta;
  int mods;
  Iconos iconos;
  float x_;
  float y_;
  Modificador(String nombre_, Categoria categoria_) {
    nombre = nombre_;
    categoria = categoria_;
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }

  Modificador(String nombre_, Categoria categoria_, int[][] paleta_) {
    nombre = nombre_;
    categoria = categoria_;
    paleta = paleta_;
  }

  public void addMod() {
    mods++;
  }

  public void removerMod() {
    println("llega");
    if (mods>0)
      mods--;
  }

  public void resetMods() {
    mods=0;
  }

  public void setSensible(boolean sensible_/*, boolean estado_*/) {
    sensible = sensible_;
  }

  public String getNombre() {
    return nombre;
  }

  public void dibujar(float x, float y, float t, int c) {
    noStroke();
    x_ = x;
    y_ = y;
    if (categoria.sensible) {    
      if (sensible) {
        enSensible( x, y, t);
      } 
      fill(c);

      ellipse(x, y, t, t);
      textSize(14);
      fill(255);
      float angulo = atan2(y-categoria.posCentro.y, x-categoria.posCentro.x);
      pushMatrix();
      translate(x, y);
      if (angulo>radians(90) || angulo<radians(-90)) {
        textAlign(RIGHT, CENTER);
        rotate(angulo-PI);
      } else {
        textAlign(LEFT, CENTER);
        rotate(angulo);
      }
      text(nombre, 0, 0);
      popMatrix();
    } else {
      fill(c);

      ellipse(x, y, t/2, t/2);
    }
  }

  public void dibujarIconos(float x, float y, float t, int col) {
    if (iconos == null)
      iconos = new Iconos(PApplet.parseInt(t*13/100));

    noStroke();
    if (bdd.interaccionConMouse) {
      mouseHover( x, y, t*12/100);
      mouseHover_extendido( x, y, t*18/100);
      sensible = hover;
    }

    if (categoria.sensible) {    
      if (sensible) {
        enSensible( x, y, t);
      } 


      tint(col);
      iconos.dibujar(nombre, x, y);

      if (mods>0) {
        pushStyle();
        noFill();
        stroke(paleta[1][3]);  
        strokeWeight(2);      
        ellipse(x, y, t*20/100, t*20/100);
        popStyle();
      }
    } else {
      fill(col);
      ellipse(x, y, t*6/100, t*6/100);

      if (mods>0) {
        pushStyle();
        noFill();
        stroke(paleta[1][3]);  
        strokeWeight(2);      
        ellipse(x, y, t*8/100, t*8/100);
        popStyle();
      }
    }
  }



  public void enSensible(float x, float y, float t) {
    pushStyle();
    // noFill();
    // strokeWeight(1);
    // stroke(paleta[2][3]);
    noStroke();
    fill(paleta[2][4]);
    ellipse(x, y, (t*13/100)*1.5f, (t*13/100)*1.5f);
    popStyle();
  }

  public int getCant() {
    return mods;
  }

  public void mouseHover(float x, float y, float t) {
    if (dist(mouseX, mouseY, x, y)<t)
      hover=true;
    else
      hover=false;
  }

  public void mouseHover_extendido(float x, float y, float t) {
    if (dist(mouseX, mouseY, x, y)<t)
      hoverExtendido=true;
    else
      hoverExtendido=false;
  }
}
ModoSoloKinect modoSoloKinect = new ModoSoloKinect();

class ModoSoloKinect {
  String carpetaSiluetas = "siluetas/";
  
  String[] abCerr = new String[]{"abierto_","cerrado_"};
  String[] niveles = new String[]{"alto_","medio_","bajo_"};
  String[] desequilibrio = new String[]{"izq","centro","der"};
  
  PImage[][][] imgSiluetaLiteral;
  boolean activo = true;
  
  ModoSoloKinect(){
  }
  
  public void cargarImagenes(){
    imgSiluetaLiteral = new PImage[2][3][3];
    for (int ac = 0; ac < abCerr.length; ac++){
      for (int nv = 0; nv < niveles.length; nv++){
       for (int des = 0; des < desequilibrio.length; des++){
        imgSiluetaLiteral[ac][nv][des] =
        loadImage( dataPath( carpetaSiluetas + abCerr[ac] + niveles[nv] + desequilibrio[des] + ".png") );
      } 
      }
    }
  }
  
  public void desactivar()
  {
    
  }
  
  public void activar(){
    
  }
  
  public void actualizar(float centroX, float centroY, float escala){
    if (imgSiluetaLiteral == null) cargarImagenes();
    
    pushStyle();
    pushMatrix();
    translate(centroX,centroY);
    scale(escala);
    imageMode(CENTER);
    //println(consola.cerrado+" "+consola.nivel+" "+consola.eje);
    int eje = consola.eje-1;
    if (eje < 0) eje = 0;
    else if (eje > 2) eje = 2;
    image( imgSiluetaLiteral[consola.cerrado][consola.nivel][eje] , 0,0 );
    popMatrix();
    popStyle();
  } 
}
class Modos {

  String modo;
  float tam;
  float diametroIconoCentral;
  int[][] paleta;
  float centroX ;
  float centroY ;
  boolean usarTexto = false;

  Iconos iconos;
  Iconos iconosGrandes;
  IconoCentral iconoCentral;


  Modos() {
    iconoCentral = new IconoCentral();
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    modo = ESPERA;
    iconos = new Iconos(PApplet.parseInt(tam*70/100));
    iconosGrandes = new Iconos((int)tam);
    setIconoCentral(COD05_2, paleta[2][3]);
  }

  Modos(int[][] paleta_, PVector posCentral_, float tam_) {    
    iconoCentral = new IconoCentral();
    paleta = paleta_;
    modo = ESPERA;
    inicializar(posCentral_, tam_);
    iconos = new Iconos(PApplet.parseInt(tam*70/100));
    iconosGrandes = new Iconos((int)tam);
    if (iconoCentral.nombre==null)
      setIconoCentral(COD05_2, paleta[2][3]);
  }

  public void inicializar(PVector posCentral, float tam_) {
    centroX = posCentral.x;
    centroY = posCentral.y;
    tam = tam_;
  }

  public void baseGris(float cX, float cY, float t) {
    fill(paleta[1][1]);     
    ellipse(cX, cY, t, t);
  }

  public void espera(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(ESPERA, cX, cY);
  }
  public void agregar(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(AGREGAR, cX, cY);
  }
  public void eliminar(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(ELIMINAR, cX, cY);
  }
  public void maquinarias(float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(MAQUINARIAS, cX, cY);
  }
  public void opciones( float cX, float cY) {
    tint(paleta[2][3]);
    iconosGrandes.dibujar(OPCIONES, cX, cY);
  }

  public void ejecutar() {
    fill(paleta[2][1]);
    textAlign(CENTER, CENTER);
    textSize(30);
    if (modo.equals(ESPERA)) {
      baseGris(centroX, centroY, tam);
      espera(centroX, centroY) ;
    } else if (modo.equals(AGREGAR)) {
      baseGris(centroX, centroY, tam);
      agregar(centroX, centroY) ;
    } else if (modo.equals(ELIMINAR)) {
      baseGris(centroX, centroY, tam);
      eliminar(centroX, centroY) ;
    } else if (modo.equals(OPCIONES)) {
      baseGris(centroX, centroY, tam);
      opciones(centroX, centroY) ;
    } else if (modo.equals(MAQUINARIAS)) {
      baseGris(centroX, centroY, tam);
      maquinarias(centroX, centroY) ;
    } else {
      text(modo, centroX, centroY);
    }

    if (!(getModo().equals(OPCIONES)) && !(getModo().equals(ESPERA)) && !(getModo().equals(MAQUINARIAS))) {
      if (iconos == null)
        iconos = new Iconos(PApplet.parseInt(tam*70/100));
      tint(iconoCentral.col);    
      iconos.dibujar(iconoCentral.nombre, centroX, centroY);
      //dibujoDeEstadoDelIconoCentral(centroX, centroY, int(tam*70/100), iconoCentral.mods);
    }
  }

  public void dibujoDeEstadoDelIconoCentral(float x, float y, float t, boolean mods) {
    if (mods) {
      pushStyle();
      stroke(paleta[1][1]);
      strokeWeight(6);
      fill(paleta[2][3]);  
      float tamDivisor = 4;  
      float px = x+(t/2-t/(tamDivisor*2))*cos(radians(-45));
      float py = y+(t/2-t/(tamDivisor*2))*sin(radians(-45));
      ellipse(px, py, t/tamDivisor, t/tamDivisor);
      popStyle();
    }
  }

  public void setIconoCentral(Modificador mod, int c) {
    if (mod!=null) {
      iconoCentral.setNombre(mod.nombre);
      iconoCentral.setColor(c);

      if (mod.mods>0) {
        iconoCentral.setMods(true);
      } else {
        iconoCentral.setMods(false);
      }
    }
  }

  public void setIconoCentral(String nombre, int c) {
    iconoCentral.setNombre(nombre);
    iconoCentral.setColor(c);
    iconoCentral.setMods(false);
  }

  public void setModo(String modo_) {
    modo = modo_;
  }

  public String getModo() {
    return modo;
  }
}

class IconoCentral {
  String nombre;
  int col;
  boolean mods = false;
  IconoCentral() {
  }
  public void setNombre(String n) {
    nombre = n;
  }
  public void setColor(int c) {
    col = c;
  }  
  public void setMods(boolean m) {
    mods = m;
  }
}
class Monitor {
  int[]  colorsitos;
  int[][]  paleta;
  PVector centro;
  float tam;
  Iconos iconos;
  Iconos iconoMarca;
  Monitor() {
    colorsitos = new int[5];
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    iconos = new Iconos(PApplet.parseInt(bdd.monitorDiametro));
    iconoMarca = new Iconos(PApplet.parseInt(bdd.monitorDiametro*20/100));
  }

  Monitor(int[][] paleta_, PVector centro_, float tam_) {
    colorsitos = new int[5];
    paleta = paleta_;
    centro = centro_;
    tam = tam_;
    iconos = new Iconos(PApplet.parseInt(tam*70/100));
    iconoMarca = new Iconos((int)tam);
  }

  public void cerrado(float a, float b, float t, int cerrado) {
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

  public void niveles(float a, float b, float t, int nivel) {

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

  public void eje(float a, float b, float t, int inclinacion) {
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

  public void dibujarBase(float a, float b, float ancho, float alto) {
    pushStyle();
    rectMode(CORNER);
    noStroke();
    fill(paleta[1][2]);
    rect(a, b, ancho, alto);
    tint(paleta[1][3]);
    iconoMarca.dibujar(COD05_1, iconoMarca.w/1.5f, alto/2); 
    iconos.dibujar(MONITOR_BASE, bdd.monitorX, bdd.monitorY);      
    popStyle();
  }

  public void dibujar(int cerrado_, int nivel_, int eje_) {  
    dibujarBase(bdd.baseMonitorX, bdd.baseMonitorY, bdd.baseMonitorAncho, bdd.baseMonitorAlto);    
    cerrado(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, cerrado_);
    niveles(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, nivel_);
    eje(bdd.monitorX, bdd.monitorY, bdd.monitorDiametro, eje_);
  }
  //----Hacer Esto
  public void dibujar_noKinect(int cerrado_, int nivel_, int eje_) {  
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
class Opcion {
  int col;
  int cant;
  boolean sensible;
  // boolean estado;
  String nombre;
  PVector pos;
  PVector posCentro;
  float t ;
  float d ;
  int[][] paleta;
  boolean conIcono = false;
  float tamFigura;
  Iconos iconos;

  Opcion() {
  }

  Opcion(String nombre_) {
    nombre = nombre_;
    //  modificadores = new ArrayList();
    col = color(255);
    t = 50;
    tamFigura = t*26/100;
    d = t*5;
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    iconos = new Iconos(PApplet.parseInt(t));
    pos = new PVector();
    posCentro = new PVector();
  }

  Opcion(String nombre_, int[][] paleta_) {
    nombre = nombre_;
    //modificadores = new ArrayList();
    col = color(255);
    t = 50;
    tamFigura = t*26/100;
    d = t*5;
    paleta = paleta_;
    iconos = new Iconos(PApplet.parseInt(t));
    pos = new PVector();
    posCentro = new PVector();
  }
  public void inicializar( int col_, int cant_, PVector pos_, PVector posCentro_, float t_) {
    col = col_;
    cant = cant_;    
    pos = pos_;
    posCentro = posCentro_;
    t = t_;//width>height?height/4:width/4;
    tamFigura = t*26/100;
    iconos = new Iconos(PApplet.parseInt(tamFigura));
  }

  public String getNombre() {
    String n = "no tengo";

    if (nombre!=null)
      n = nombre;
    return n;
  }

  public void dibujar() {
    if (sensible)
      enSensible();

    if (conIcono) {
      dibujarIcono();
    } else {
      dibujarElipse();
    }
  }

  public void setSensible(boolean sensible_/*, boolean estado_*/) {
    sensible = sensible_;
    // estado = estado_;
  }

  public void dibujarIcono() {
    tint(col);
    iconos.dibujar(nombre, pos.x, pos.y);

    //-- dibujar FLecha Maquinarias.....
    if (nombre.equals(MAQUINARIAS)) {
      fill(paleta[2][0]);
      triangle(pos.x, pos.y+tamFigura*1.3f, pos.x-7, pos.y+tamFigura*1.3f-10, pos.x+7, pos.y+tamFigura*1.3f-10);
    }
  }

  public void dibujarElipse() {
    fill(col);
    noStroke();
    ellipse(pos.x, pos.y, tamFigura, tamFigura);
    textSize(15);
    fill(255);
    text(nombre, pos.x, pos.y);
  }

  public void enSensible() {
    pushStyle();
    stroke(paleta[2][4]);
    fill(paleta[2][4]);
    ellipse(pos.x, pos.y, tamFigura*1.5f, tamFigura*1.5f);

    float angulo = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
    float d = t*59/100;

    /*for (int j =0; j< 2; j++) {
     float cX = posCentro.x+t/3*cos(angulo);
     float cY = posCentro.y+t/3*sin(angulo);
     float x1 = (cX)+((d/4)*j)*cos(angulo);
     float y1 = (cY)+((d/4)*j)*sin(angulo);
     float x2 = x1+(d/10)*cos(angulo);
     float y2 = y1+(d/10)*sin(angulo);
     float x3 = x1+(d/5.75)*cos(angulo);
     float y3 = y1+(d/5.75)*sin(angulo);
     strokeWeight(10);
     line(x1, y1, x2, y2);          
     
     strokeWeight(10);
     point(x3, y3);
     }*/
    float cX = posCentro.x+t/2*cos(angulo);
    float cY = posCentro.y+t/2*sin(angulo);
    float x1 = cX+(dist(pos.x, pos.y, cX, cY)-tamFigura/2)*cos(angulo);
    float y1 = cY+(dist(pos.x, pos.y, cX, cY)-tamFigura/2)*sin(angulo);
    strokeWeight(10);
    line(cX, cY, x1, y1);         
    popStyle();
  }
}
class UIcontrol {

  Paleta selectorPaleta;
  PVector pos;
  float ancho;
  float alto;
  boolean escondido;

  UIcontrol(PVector pos_, float ancho_, float alto_, int[][] paleta_) {
    ancho = ancho_;
    alto = alto_;
     pos = pos_;
     
    selectorPaleta = new Paleta();
     selectorPaleta.setPos(new PVector(pos.x+ancho/2, pos.y+alto/2));
    selectorPaleta.setSize(ancho>alto?alto-ancho/10:ancho-ancho/10);
    selectorPaleta.setMatrizPaleta(paleta_);
   

   
    esconder();
  }

  public void mouseDrag(float x, float y) {
    if (!escondido) {
      selectorPaleta.mouseDrag(x, y);
    }
  }

  public void mousePressed(float x, float y) {
    if (!escondido) {
      selectorPaleta.mousePressed(x, y);
    }
  }

  public void dibujar() {
    if (!escondido) {
      pushStyle();
      rectMode(CORNER);
      fill(0, 100);
      noStroke();
      rect(0, 0, ancho, alto);
      popStyle();
      selectorPaleta.dibujar();
    }
  }

  public void esconder() {
    escondido = !escondido;
  }
}
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
  int colorSeleccionado;


  SelectorDeColor() {
    nombre = "";
    imagenSelectorByS = createImage(100, 100, HSB);
    imagenSelectorT = createImage(20, 100, HSB);
    setTam(100);
  }

  public void setColor(float h, float s, float b) {
    hSeleccion = h;
    sSeleccion = s;
    bSeleccion = b;
  }

  public void setTam(float tam) {
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
      inputs[i].setPos(new PVector(pos.x+anchoContenedor/2-borde-anchoInputs/2, (pos.y-altoContenedor/2+borde+altoInputs/2+i*(altoInputs+borde/1.5f))));
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
    imagenSelectorByS.resize(PApplet.parseInt(ancho), PApplet.parseInt(alto));
    imagenSelectorT.resize(PApplet.parseInt(ancho/5), PApplet.parseInt(alto));
    //setSelector();
  }

  public void setSelector() {
    colorMode(HSB);
    imagenSelectorT.loadPixels();
    for (int i = 0; i < imagenSelectorT.pixels.length; i++) {
      int y = PApplet.parseInt(i/imagenSelectorT.width);
      int x = PApplet.parseInt(i%imagenSelectorT.width);
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

  public void dibujar() {
    dibujarBase();
    dibujarSelector();
    dibujarInputs();
  }

  public void dibujarBase() {
    pushStyle();
    rectMode(CENTER);

    fill(0);
    noStroke();
    rect(pos.x, pos.y, anchoContenedor, altoContenedor);
    popStyle();
  }

  public void dibujarSelector() {
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

  public void dibujarInputs() {
    for (int i = 0; i < inputs.length; i++) {
      inputs[i].dibujar();
      println(i+"; "+inputs[i].texto);
    }
  }

  public void setContenidoDeInputs(int c) {
    pushStyle();
    colorMode(HSB);
    hSeleccion = hue(c);
    sSeleccion = saturation(c);
    bSeleccion = brightness(c);
    colorSeleccionado = color(hSeleccion, sSeleccion, bSeleccion);
    String valor = "";
    popStyle();
    valor = str(PApplet.parseInt(red(c)));
    valor = "255";
    inputs[0].setTexto(valor);
    valor = str(PApplet.parseInt(green(c)));
    inputs[1].setTexto(valor);
    valor = str(PApplet.parseInt(blue(c)));
    inputs[2].setTexto(valor);
    valor = str(PApplet.parseInt(hue(c)));
    inputs[3].setTexto(valor);
    valor = str(PApplet.parseInt(saturation(c)));
    inputs[4].setTexto(valor);
    valor = str(PApplet.parseInt(brightness(c)));
    inputs[5].setTexto(valor);
    valor = hex(c);
    inputs[6].setTexto(valor);
  }

  public void mousePressed(float x, float y) {
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

  public void keyPressed(int keyCode, char key) {
    int c = color(0, 0, 0);
    for (int i = 0; i < inputs.length; i++) {
      if (inputs[i].estaSeleccionado()) {
        inputs[i].keyPressed(keyCode, key);
        if (inputs[i].getNombre().equals("Rojo") || inputs[i].getNombre().equals("Verde") || inputs[i].getNombre().equals("Azul")) {
          pushStyle();
          colorMode(RGB);
          c = color(PApplet.parseInt(inputs[0].getTexto()), PApplet.parseInt(inputs[1].getTexto()), PApplet.parseInt(inputs[2].getTexto()));
          popStyle();
          hSeleccion = hue(c);
          sSeleccion = saturation(c);
          bSeleccion = brightness(c);
        } else if (inputs[i].getNombre().equals("Hexagecimal")) {
          c = color(PApplet.parseInt(inputs[6].getTexto()));
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

  public boolean seleccionLista(float x, float y) {
    boolean listo = false;
    if ((x>pos.x-anchoContenedor/2 && x<pos.x+anchoContenedor/2 && y>pos.y-altoContenedor/2 && y<pos.y+altoContenedor/2)) {
      listo = false;
    } else {
      listo = true;
    }
    return listo;
  }

  public int getColor() {
    return colorSeleccionado;
  }
}



class Paleta extends ElementoUI {

  int[][] matrizPaleta;
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

  public void setMatrizPaleta(int[][] matrizPaleta_) {
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


  public void dibujar() {   
    dibujarBase();
    dibujarSelector();
  }

  public void dibujarBase() {
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

  public void setPosiciones() {
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

  public void dibujarSelector() {
    if (sensible) {
      selectorDeColor.dibujar();
    }
  }

  public void mouseDrag(float x, float y) {
    selectorDeColor.mousePressed(x, y);

    matrizPaleta[indiceX][indiceY] = selectorDeColor.getColor();
    selectorDeColor.setContenidoDeInputs(matrizPaleta[indiceX][indiceY]);
  }

  public void mousePressed(float x, float y) {
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

  public void keyPressed(int keyCode, char key) {
    if (sensible) {
      selectorDeColor.keyPressed(keyCode, key);
      matrizPaleta[indiceX][indiceY] = selectorDeColor.getColor();
    }
  }
  public int[][] getPaleta() {
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

  public void setEtiqueta(String etiqueta_) {
    etiqueta = etiqueta_;
  }
  public void setLimite(int limite_) {
    limitado = true;
    limite = limite_;
  }

  public void setSinLimite() {
    limitado = false;
  }

  public void dibujar() {
    dibujarBase();
    dibujarTexto();
     
  }

  public void dibujarBase() {
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

  public void dibujarTexto() {
    pushStyle();
    if (sensible) {
      if (frameCount % 20 == 0) { 
        pleca = !pleca;
      }
    } else {
      pleca = false;
    }

    selector = PApplet.parseInt(constrain(selector, 0, texto.length()));
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

  public void keyPressed(int keyCode, char key) {
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

  public String getTexto() {
    return texto;
  }

  public void setTexto(String texto_) {
    texto = texto_;
    if (texto.length()>limite)
      texto = texto.substring(0, limite);
    
  }

  public boolean estaSeleccionado() {
    return sensible;
  }

  public void mousePressed(float x, float y) {
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

  public void dibujar() {
    dibujarBase();
    dibujarTexto();
  }

  public void dibujarBase() {
    pushStyle();
    if (sensible) {
      fill(0);
    } else {
      fill(30);
    }
    rect(pos.x, pos.y, ancho, alto);
    popStyle();
  }

  public void dibujarTexto() {
    pushStyle();
    fill(0);
    textAlign(CENTER, CENTER);
    textSize(alto/100*80>ancho/nombre.length()?alto/100*80:ancho/nombre.length());
    text(nombre, pos.x+ancho/2, pos.y+alto/2);
    popStyle();
  }

  public void mouseRevisar(float x, float y) {
    if (x>pos.x && x<pos.x+ancho &&
      y>pos.y && y<pos.y+alto) {
      sensible = true;
    } else {
      sensible=false;
    }
  }

  public boolean mousePressed() {
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

  public void setPos(PVector pos_) {
    pos.set(pos_.x,pos_.y);
  }
  public void setSize(float ancho_, float alto_) {
    ancho = ancho_;
    alto = alto_;
  }
  
   public void setSize(float t) {
    ancho = t;
    alto = t;
  }
  public void setNombre(String nombre_) {
    nombre = nombre_;
  }
  
  public String getNombre() {
    return nombre;
  }
}

//------------  LA MARCA
String COD05_2 = "cod05 2";
String COD05_1 = "cod05 1";
//---------- EL MONITOR
String ABIERTO =  "abierto";
String CERRADO = "cerrado";
String N_MEDIO = "nivel medio";
String N_ALTO = "nivel alto";
String N_BAJO = "nivel bajo";
String EJE_DERECHA = "inclinacion derecha";
String EJE_IZQUIERDA = "inclinacion izquierda";
String EJE_CENTRO = "sin inclinacion";
String MONITOR_BASE = "base monitor";


//------------MODOS
String ESPERA = "espera";
String AGREGAR = "agregar";
String ELIMINAR = "eliminar";
//String ESTIMULOS = "estimulos";
String OPCIONES = "opciones";
String MAQUINARIAS = "maquinarias";


//-------------- DE AQUI  PARA ABAJO LOS NOMBRES YA NO SON NECESARIOS porque vamos asacarlos de los nombres de las iamgenes.
//-modificadores
String M_ALFA_SEGUN_VELOCIDAD = "Alfa Segun Velocidad"   ;
String M_ATRACCION_AL_CENTRO = "Atraccion Al Centro"  ;  
String M_COLISION_SIMPLE = "Colision Simple"    ;
String M_DIBUJAR_CIRCULO = "Dibujar Circulo" ;  
String M_DIBUJAR_FLECHA = "Dibujar Flecha"     ;
String M_DIBUJAR_RASTRO_CIRCULAR = "Dibujar Rastro Circular";
String M_DIUJAR_REAS = "Dibujar Reas"   ;  
String M_ESPACIO_CERRADO = "Espacio Cerrado"    ;
String M_ESPACIO_TOROIDAL = "Espacio Toroidal"  ;
String M_FLOCK_ALINEAMIENTO = "Flock Alineamiento"  ;
String M_FLOCK_COHESION = "Flock Cohesion" ;
String M_FLOCK_SEPARACION = "Flock Separacion"  ;   
String M_FRICCION_GLOBAL = "Friccion Global";    
String M_FUERZAS_POR_SEMEJANZAS = "Fuerzas Por Semejanza" ;
String M_GRAVEDAD = "Gravedad"    ;
String M_RASTRO_ELASTICO = "Rastro Elastico" ;
String M_RESET_LLUVIA = "Reset Lluvia" ; 
String M_MOVER = "Mover" ;

//--modificadores ineccistentes
String M_COLISION_CON_JOINT = "Colision Con Joint"  ; 
String M_RASTRO_NORMAL = "Rastro Normal"; 



//-categoriasString C_FUERZAS_POR_SEMEJANZAS = "Fuerzas Por Semejanza"
String C_TRANSPARENCIA = "Transparencia";
String C_DIBUJAR_RASTRO = "Dibujar Rastro";
String C_FLOCKING = "Flocking";
String C_FORMA_DE_RASTRO = "Forma De Rastro";
String C_ESCENA = "Escena";
String C_APLICAR_FUERZAS = "Aplicar Fuerza";
String C_VISUALIZAR_PARTICULAS = "Vizualizar Particulas";
String C_VARIOS = "Varios";
String C_APLICAR_COLISIONES = "Aplicar Colisiones";
String C_APLICAR_MOVIMIENTO = "Aplicar Movimiento";
String C_RESET_LLUVIA = "Reset Lluvia"  ;
  public void settings() {  size(500, 600);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Carrete" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
