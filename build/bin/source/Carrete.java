import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import controlP5.*; 
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

///////////////////////CONTROLES TECLADO
//--- flechas izquierda y derecha: para navegacion
//--- enter:  ingresar en un submenu o activar un modificador
//--- barra espaciadora: desplagar ipManager
//--- 'a' o 'A' : agregarModificador
//--- 's' o 'S' : quitarModificador
//--- 'd' o 'D' : cancelar

/*---- mi favorita hasta ahora---- ir a opciones para quitar 
 ------ el fondo agregar movimiento, y cohesion(2), esperar 
 ------ unos segundos, agregar fuerzaPorSemejanza y alfa 
 ------ segun velocidad, agregar dibujar lineas, quitar los 
 ------ dos cohesion y agregar un sepracion inmediatamente xD*/

 



int contadorCapturas = 0;

OscP5 oscP5;
NetAddress sistema;

ControlP5 cp5;
int[][] paleta; 

IpManager ipManager;

String ip = "127.0.0.1";
String puertoEnvio = "12100";
String puertoRecivo = "12000";


Consola consola;
Controlador controlador;
ArrayList<String> nombresBotones;
ArrayList<String> nombresBotonesExistentes;
ArrayList<String> nombresCategorias;
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

int cerradoValor = 0;
int desequilibrioValor = 0;
int nivelValor = 0;

boolean totales;
boolean existentes;

boolean conectadoConSistema = false;

UIcontrol uiControl;

Iconos iconos;
public void setup() {
  
  surface.setAlwaysOnTop(true);
  //frameRate(25);
  
  
  setPaleta();

  cosasLocasSetUp();

  initOSC();

  uiControl = new UIcontrol(new PVector(0, 0), width, height, paleta);
  cp5 = new ControlP5(this);
  ipManager = new IpManager(cp5);
  ipManager.set();

  //fuente = loadFont("28DaysLater-48.vlw");
  //fuente = loadFont("Castellar-30.vlw");
  fuente = loadFont("Consolas-48.vlw");



  textFont(fuente);



  consola = new Consola(paleta);
  controlador = new Controlador(consola);
}


public void draw() {

  background( fondo);
  
  
  if (consola != null) {
    consola.ejecutar();
  }

  ipManager.fondo();

  if (totales && existentes) {
    botonesListo();
    println("..........listo botones.........");
    controlador.anadir();
    controlador.cancelar();
  }

  if (!conectadoConSistema) {
  modoSoloKinect.actualizar(width/2,height/2,1);
  
    if (frameCount%60==0) {
      println("pido");
      consola.mandarMensaje("/pedir/modificadores/total");
    }

    pushStyle();
    fill(255);
    textSize(30);
    text("desconectado", width/2, height/2);
    popStyle();
  }

  if (mousePressed) {
    uiControl.mouseDrag(mouseX, mouseY);
  }
  uiControl.dibujar();

  cosasLocasDraw();  
}

public void keyPressed() {

  if (key == ' ') {
    if (uiControl.escondido)
      ipManager.esconder();
  }

  if (keyCode==RIGHT) {
    controlador.derecha();
  }

  /*if (keyCode==ALT) {
    if (ipManager.escondido)
      uiControl.esconder();
  }*/

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

  if (key == 'w' || key == 'W') {
    controlador.estimulos();
  }

  if (key == 'd' || key == 'D') {
    controlador.cancelar();
  }

  if (key == 'i' || key == 'I') {
    //consola.imprimirLista();
  }

  if (key == 'g' || key == 'G') {
    saveFrame("capturas/captura_####.tff");
    contadorCapturas++;
  }
}

public void mousePressed() {
  uiControl.mousePressed(mouseX, mouseY);
}

public void actualizarMovimiento() {
  controlador.actualizarIconos(cerradoValor, nivelValor, desequilibrioValor);
}

public void cantidadBotones(int cantidad_) {
  conectadoConSistema = true;
  cantidad=cantidad_;
  nombresBotones = new ArrayList<String>();
  nombresCategorias = new ArrayList<String>();
  // estado = new ArrayList<Boolean>();
}

public void cantidadBotonesExistentes(int cantidad_) {
  cantidadExistentes=cantidad_;
  nombresBotonesExistentes = new ArrayList<String>();
  // estadoExistentes = new ArrayList<Boolean>();
}

public void modificadores(String nombre_, String categoria_, int estado_) {
  nombresBotones.add(nombre_); 
  nombresCategorias.add(categoria_); 

  /* if (estado_ == 0) {
   estado.add(false);
   } else {
   estado.add(true);
   }*/
}

public void modificadoresExistentes(String nombre, int estado_) {
  nombresBotonesExistentes.add(nombre); 

  /*if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}

public void opciones(String nombre_, int estado_) {

  nombresBotones.add(nombre_); 

  /* if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}

public void estimulos(String nombre_, int estado_) {

  nombresBotones.add(nombre_); 
  println("me estan llegando los estimulos");
  /* if (estado_ == 0) {
   estado.add(true);
   } else {
   estado.add(false);
   }*/
}


public void totalesListo() {

  totales =true;
  if (!existentes) {
    consola.mandarMensaje("/pedir/modificadores/existentes");
  }
}

public void  existentesListo() {

  existentes =true;
}

public void  estimulosListo() {
  consola.renovarDatosEstimulos(nombresBotones);
  println("estimulos listos");
  consola.mandarMensaje("/pedir/opciones");
}

public void botonesListo() {

  consola.renovarDatosCategorias(nombresBotones, nombresCategorias, nombresBotonesExistentes);
  println("mods listos");
  //  renovarConsola = false;
  totales = false;
  existentes = false;
  consola.mandarMensaje("/pedir/estimulos");
}
public void opcionesListo() {
  consola.renovarDatosOpciones(nombresBotones);
  println("opciones listos");
  // consola.renovarDatos(cantidad, estado, nombresBotones);
}

public void agregarMod(String cual) {
  consola.agregarMod(cual);
}
public void quitarMod(String cual) {

  consola.quitarMod(cual);

  // consola.renovarDatos(cantidad, estado, nombresBotones);
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
public void cursorMoCap(float x, float y) {
  consola.setCursor(x*width, y*height);
}

//-------------------------- HARD CORDE -----------------

public void cosasLocasSetUp() {
  iconos = new Iconos(110);
}

public void cosasLocasDraw() {
  if (conectadoConSistema) {
    if (!consola.modos.getModo().equals(ESPERA)) {
      String nombreDelIcono;
      PVector cenHardCode;
      ColeccionCategorias cChardCode = consola.cC;
      nombreDelIcono = cChardCode.getSensible(consola.selector);
      cenHardCode = cChardCode.posCentro;
      iconos.dibujar(nombreDelIcono, cenHardCode.x, cenHardCode.y);
    }
  }
}
ConfiguracionCOD05 config;

public void initOSC() {
  if (config == null) config = new ConfiguracionCOD05();
  XML xmlConfig = null;
  if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
  if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagEjecucion);

  config.cargar(xmlConfig);
  
  oscP5 = new OscP5(this, config.carrete.puerto);
  sistema = new NetAddress(config.lienzo.ip, config.lienzo.puerto);

  //-----------------------------------------OSC sistema de particulas

  oscP5.plug(this, "cantidadBotones", "/modificadores/totales");
  oscP5.plug(this, "cantidadBotonesExistentes", "/modificadores/existentes");
  oscP5.plug(this, "cantidadBotones", "/opciones");
  oscP5.plug(this, "cantidadBotones", "/estimulos/totales");
  oscP5.plug(this, "modificadores", "/modificadores/totales");
  oscP5.plug(this, "modificadoresExistentes", "/modificadores/existentes");
  oscP5.plug(this, "estimulos", "/estimulos/totales");
  oscP5.plug(this, "opciones", "/opciones");
  oscP5.plug(this, "opcionesListo", "/opciones/listo");
  oscP5.plug(this, "existentesListo", "/modificadores/existentes/listo");
  oscP5.plug(this, "totalesListo", "/modificadores/totales/listo");
  oscP5.plug(this, "estimulosListo", "/estimulos/listo");
 
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

  oscP5.plug(this, "cursorMoCap", "/cursor");
}

//---------------------------------------------IP MANAGER----EVENTOS-----------------------

public void Conectar() { 

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
}

public void conectarOSC() {
  int pe = PApplet.parseInt(puertoEnvio);
  int pr = PApplet.parseInt(puertoRecivo);
  oscP5 = new OscP5(this, pr); 
  sistema = new NetAddress(ip, pe);

  //-----------------------------------------OSC plugs

  //-----------------------------------------OSC sistema de particulas

  oscP5.plug(this, "cantidadBotones", "/modificadores/totales");
  oscP5.plug(this, "cantidadBotonesExistentes", "/modificadores/existentes");
  oscP5.plug(this, "cantidadBotones", "/opciones");
  oscP5.plug(this, "modificadores", "/modificadores/totales");
  oscP5.plug(this, "modificadoresExistentes", "/modificadores/existentes");
  oscP5.plug(this, "estimulos", "/estimulos/totales");
  oscP5.plug(this, "opciones", "/opciones");
  oscP5.plug(this, "opcionesListo", "/opciones/listo");
  oscP5.plug(this, "existentesListo", "/modificadores/existentes/listo");
  oscP5.plug(this, "totalesListo", "/modificadores/totales/listo");
  oscP5.plug(this, "estimulosListo", "/estimulos/listo");
  
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
class Categoria extends Opcion {

  ArrayList modificadores;
  int mods = 0;


  Categoria(String nombre_) {
    nombre = nombre_;
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
  /*void inicializar( float t_, color col_, int cant_, PVector pos_, PVector posCentro_) {
   col = col_;
   cant = cant_;    
   pos = pos_;
   posCentro = posCentro_;
   t = t_;
   }
   
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
    dibujar();
    dibujarMods();
    displayModificadoresExistentes();
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

  public void displayModificadoresExistentes() {
    if (mods>0) {
      /*  if (coneccion == null) {
       coneccion = loadImage("coneccion.png");
       coneccion.resize(t*135/100, t*135/100);
       }*/

      float ang = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
      float px = pos.x-(t*13/100/2)*cos(ang);
      float py = pos.y-(t*13/100/2)*sin(ang);
      float x = posCentro.x+(t/2-t*13/100/2)*cos(ang);
      float y = posCentro.y+(t/2-t*13/100/2)*sin(ang);

      float diam = dist(x, y, px, py);

      pushStyle();
      strokeWeight(2);
      stroke(150, 150, 220);
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
      /*  popMatrix();
       rotate(ang);
       translate(cx,cy);
       image(coneccion, 0, 0);
       pushMatrix();*/
      if (mods<4) {        
        for (int i=0; i<mods; i++) {
          x = px-diam/(mods+1)*(i+1)*cos(ang);
          y = py-diam/(mods+1)*(i+1)*sin(ang);
          pushStyle();
          fill(paleta[2][3]);
          ellipse(x, y, t*13/100, t*13/100);
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
          ellipse(x, y, t*13/100, t*13/100);
          popStyle();
        }
      }
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

  public void inicializar(String[] nombresCategorias, String[] nombresModificadores, String[] nombresModificadoresExistentes) {

    if (nombresCategorias != null && nombresModificadores != null) {
      setCategorias(nombresCategorias, nombresModificadores);
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
  public void setCategorias(String[] nombresCategorias, String[] nombresModificadores) {

    categorias = new ArrayList();
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
      float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
      float diametroSelector = t*118/100;
      float x = width/2+(diametroSelector*cos(angulo));
      float y = height/1.7f+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);

      println(c.nombre+": "+c.modificadores.size());
      c.inicializar(colorsito, cant, pos, posCentro, t);
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

  public Modificador getModSensible(int sensible) {
    Modificador mod = listaMods.get(sensible);    
    return mod;
  }
  ///////////////////////------------------------------------MALSISISMO
  public int getContadorSeleccionEstimulo(PVector cursor) {
    boolean seleccionando = false;
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);  
      if (dist(c.pos.x, c.pos.y, cursor.x, cursor.y)<30 ) {
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

  public boolean getSeleccionarEstimulo() {
    boolean sE = contador > 400?true:false;
    return sE;
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
class ColeccionEstimulos {
  int contador;
  int cant;
  PVector posCentro;
  ArrayList estimulos;  
  int[][] paleta;
  float t;

  ColeccionEstimulos() {
     paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
      paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionEstimulos(int[][] paleta_,PVector posCentro_,float t_) {
    paleta = paleta_;
    t = t_;
    posCentro = posCentro_;
  }

  public void inicializar(String[] nombresEstimulos) {
    
    if (nombresEstimulos != null) {
      setEstimulos(nombresEstimulos);
    }
  }

  public void setEstimulos(String[] nombresEstimulos) {

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
      int colorsito = color(hue, 100, 200);
      float norm = map(i, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
        float diametroSelector = t*118/100;
        float x = width/2+(diametroSelector*cos(angulo));
      float y = height/1.7f+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);

      //println(e.nombre+": "+e.modificadores.size());
      e.inicializar(colorsito, cant, pos, posCentro,t);
      popStyle();
    }
    println(estimulos.size());
  }

  public void dibujar() {   
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

  public void setSensible(int sensible) {
    for (int i=0; i<estimulos.size (); i++) {
      Estimulo e = (Estimulo)estimulos.get(i);  
      e.setSensible(false);
    }
    Estimulo e = (Estimulo)estimulos.get(sensible);
    e.setSensible(true);
  }

  public String getSensible(int sensible) {
    String nombreSensible;
    Estimulo e = (Estimulo)estimulos.get(sensible);
    nombreSensible = e.getNombre();

    return nombreSensible;
  }

  public int getContadorSeleccionEstimulo(PVector cursor) {
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

  public boolean getSeleccionarEstimulo() {
    boolean sE = contador > 400?true:false;
    return sE;
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
      float x = width/2+(diametroSelector*cos(angulo));
      float y = height/1.7f+(diametroSelector*sin(angulo));
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
}
//v 22/06/2017
String archivoConfigXML = "../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

enum EstadoModulo {
  APAGADO, LOCAL, REMOTO
}
final EstadoModulo[] EstadoModuloList = new EstadoModulo[]{EstadoModulo.APAGADO, EstadoModulo.LOCAL, EstadoModulo.REMOTO};
public int EstadoModuloToInt(EstadoModulo estado) {return estado==EstadoModulo.APAGADO?0:estado==EstadoModulo.LOCAL?1:2;};

class ConfiguracionCOD05 {
  ConfigModulo lienzo, observador, carrete;
  boolean panelConexiones = false;

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
    lienzo = new ConfigModulo().Iniciar("lienzo", 12010);
    observador = new ConfigModulo().Iniciar("observador", 12020);
    carrete = new ConfigModulo().Iniciar("carrete", 12030);
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
  ColeccionEstimulos cE;
  ColeccionOpciones cO;
  Monitor monitor;

  String modoActual = "cualquiercosa";
  float centroX = width/2;
  float centroY = height/1.7f;
  float tam = width>height?height/4.5f:width/4.5f;

  String[] datosDeSistema_nombresCategorias;
  String[] datosDeSistema_nombresModificadores;
  String[] datosDeSistema_nombresModificadoresExistentes;
  String[] datosDeSistema_nombresOpciones;
  String[] datosDeSistema_nombresEstimulos;

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
    cE = new ColeccionEstimulos();
    cC.inicializar(datosDeSistema_nombresCategorias, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);
    cO.inicializar(datosDeSistema_nombresOpciones);    
    cE.inicializar(datosDeSistema_nombresEstimulos);    
    monitor = new Monitor();
  }

  Consola(int[][] paleta_) { 

    modos = new Modos(paleta_, new PVector(centroX, centroY), tam);
    cC = new ColeccionCategorias(paleta_, new PVector(centroX, centroY), tam);
    cO = new ColeccionOpciones(paleta_, new PVector(centroX, centroY), tam);
    cE = new ColeccionEstimulos(paleta_, new PVector(centroX, centroY), tam);
    cC.inicializar(datosDeSistema_nombresCategorias, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);
    cO.inicializar(datosDeSistema_nombresOpciones);    
    cE.inicializar(datosDeSistema_nombresEstimulos);    
    monitor = new Monitor(paleta_, new PVector(centroX, centroY), tam);
  }


  public void ejecutar() {

 base();
    if (modos.getModo().equals(ESPERA)) {
      //mandarMensaje("/holi...ten un buen dia");
      monitor.dibujar(cerrado, nivel, eje);
    } else if (modos.getModo().equals(AGREGAR)) {        
     
      monitor.dibujar(cerrado, nivel, eje);
     cC.dibujarCategoria();
    } else if (modos.getModo().equals(ELIMINAR)) {       
      
      monitor.dibujar(cerrado, nivel, eje);
      cC.dibujarCategoria();
    } else if (modos.getModo().equals(ESTIMULOS)) {      

      int contadorSeleccionEstimulo = cC.getContadorSeleccionEstimulo(cursor);//limites 0,400
      monitor.dibujar(cerrado, nivel, eje, contadorSeleccionEstimulo);       
      fill(255);
      ellipse(cursor.x, cursor.y, 10, 10);      
      if (cE.getSeleccionarEstimulo()) {
        botonesAccionesN2();
        mandarMensaje("/pedir/estimulos/totales");
      }
      cE.dibujar();
    } else if (modos.getModo().equals(OPCIONES)) {
      monitor.dibujar(cerrado, nivel, eje);
      cO.dibujar();
    }
    modos.ejecutar(modoActual);
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
  public void activarEstimulos() {
    modos.setModo(ESTIMULOS); 
    limitarSelector();
    //  botonesAccionesN1();
  }
  public void activarEspera() {
    modos.setModo(ESPERA); 
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
    } else if (modos.getModo().equals(ELIMINAR)) {       
      cC.setSensible(selector);
    } else if (modos.getModo().equals(ESTIMULOS)) {
    } else if (modos.getModo().equals(OPCIONES)) {
      cC.setSensible(selector);
    }
  }

  public void botonesAccionesN2() {
    if (modos.getModo().equals(AGREGAR)) {
      String nombreModSeleccionado = cC.getSensible(selector);
      OscMessage mensajeModificadores;
      mensajeModificadores = new OscMessage("/agregar/modificadores");
      mensajeModificadores.add(nombreModSeleccionado); 
      oscP5.send(mensajeModificadores, sistema);
    } else if (modos.getModo().equals(ELIMINAR)) {
      String nombreModSeleccionado = cC.getSensible(selector);
      int cantModSeleccionado = cC.getModSensible(selector).getCant();
      OscMessage mensajeModificadores;
      mensajeModificadores = new OscMessage("/quitar/modificadores");
      mensajeModificadores.add(nombreModSeleccionado+"_"+(cantModSeleccionado-1));           
      oscP5.send(mensajeModificadores, sistema);
    } else if (modos.getModo().equals(ESTIMULOS)) {
      OscMessage mensajeOpciones;
      mensajeOpciones = new OscMessage("/seleccionar/estimulo");
      // mensajeOpciones.add(nombreModSeleccionado);           
      oscP5.send(mensajeOpciones, sistema);
    } else if (modos.getModo().equals(OPCIONES)) {
      String nombreModSeleccionado = cO.getSensible(selector);
      OscMessage mensajeOpciones;
      mensajeOpciones = new OscMessage("/accion/opciones");
      mensajeOpciones.add(nombreModSeleccionado);           
      oscP5.send(mensajeOpciones, sistema);
    }

    //activarEspera();
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
    } else if (modos.getModo().equals(ESTIMULOS)) {
      limiteSelector = datosDeSistema_nombresEstimulos.length; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(OPCIONES)) {
      limiteSelector = datosDeSistema_nombresOpciones.length; //cambiar con no se nada supongo
    }
  }


  public void renovarDatosCategorias( ArrayList<String>nombres_, ArrayList<String>nombresCategorias_, ArrayList<String>nombresExistentes_) {

    dosNivelesDeSeleccion = true;
    int c = nombres_.size();
    int ce = nombresExistentes_.size();

    datosDeSistema_nombresCategorias = new String[c];
    datosDeSistema_nombresModificadores  = new String[c];
    datosDeSistema_nombresModificadoresExistentes  = new String[ce];

    for (int i=0; i<c; i++) {
      datosDeSistema_nombresModificadores[i] = (String)nombres_.get(i);
      datosDeSistema_nombresCategorias[i] = (String)nombresCategorias_.get(i);
    }
    for (int i=0; i<ce; i++) {
      String[] n = split(nombresExistentes_.get(i), "_");
      datosDeSistema_nombresModificadoresExistentes[i] = n[0];
    }

    cC.inicializar(datosDeSistema_nombresCategorias, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);

    limitarSelector();
  }

  public void renovarDatosEstimulos( ArrayList<String>nombres_) {

    int c = nombres_.size();
    datosDeSistema_nombresEstimulos  = new String[c];
    for (int i=0; i<c; i++) {
      datosDeSistema_nombresEstimulos[i] = (String)nombres_.get(i);
    }

    cE.inicializar(datosDeSistema_nombresEstimulos);

    limitarSelector();
  }

  public void renovarDatosOpciones( ArrayList<String>nombres_) {

    int c = nombres_.size();
    datosDeSistema_nombresOpciones  = new String[c];
    for (int i=0; i<c; i++) {
      datosDeSistema_nombresOpciones[i] = (String)nombres_.get(i);
    }

    cO.inicializar(datosDeSistema_nombresOpciones);

    limitarSelector();
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
    consola.activarAnadir();
  }
  public void quitar() {
    consola.activarQuitar();
  }
  public void opciones() {
    consola.activarOpciones();
  }
  public void estimulos() {
    consola.activarEstimulos();
  }

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
    color(0xff25282D), color(0xff1F2227), color(0xff21262A)
  };

  paleta[1] = x2;

  int[] x3 = {
    color(0xff1b1922), color(0xff25282D), color(0xffAFC22B), color(0xffBE4041), color(0xff43B4D0)
  };

  paleta[2] = x3;

  int[] x4 = {
    color(0xffB44343), color(0xffB47A43), color(0xffB4B243), color(0xff7DB443), color(0xff45B443), color(0xff43B478), 
    color(0xff43B4B0), color(0xff4380B4), color(0xff4348B4), color(0xff7543B4), color(0xffAD43B4), color(0xffB44382)
  };

  paleta[3] = x4;
}
class Iconos {
  HashMap<String, PImage> iconos;

  String[] nombres = {
    "Colision Con Joint", "Aplicar Colisiones", "Colision Simple", "Dibujar Reas", "Varios", 
    "Vizualizar Particulas", "Dibujar Circulo", "Aplicar Fuerza", "Atraccion Al Centro", "Friccion Global", 
    "Escena", "Dibujar Flecha", "Espacio Cerrado", "Espacio Toroidal", "Dibujar Rastro Circular", 
    "Rastro Normal", "Rastro Elastico", "Forma De Rastro", "Dibujar Rastro Triangular", "Dibujar Rastro", 
    "Flock Separacion", "Flock Cohesion", "Flocking", "Transparencia", "Alfa Segun Velocidad", 
    "Aplicar Movimiento", "Dibujar Rastro Lineal", "Reset Lluvia", "Fuerzas Por Semejanza", "Flock Alineamiento", 
    "Reset Lluvia", "Fuerzas Por Semejanza", "Mover", "Gravedad"
  };

  Iconos() {
    iconos = new HashMap<String, PImage>();
    for (int i=0; i<34; i++) {
      PImage icono = loadImage("icono ("+(i+1)+").png");         
      iconos.put(nombres[i], icono);
    }
    /* PImage icono;
     icono = loadImage("icono (29).png"); 
     iconos.put( nombres[31], icono); 
     icono = loadImage("icono (30).png"); 
     iconos.put( nombres[32], icono);
     icono = loadImage("icono (27).png");
     iconos.put( nombres[33], icono);*/
  }

  Iconos(int t) {
    iconos = new HashMap<String, PImage>();
    for (int i=0; i<34; i++) {
      PImage icono = loadImage("iconos/blancos/icono ("+(i+1)+").png");  
      icono.resize(t, t);      
      iconos.put(nombres[i], icono);
     
    }
    /* PImage icono;
     icono = loadImage("iconos/icono (29).png"); 
     icono.resize(t, t);  
     iconos.put( nombres[31], icono); 
     icono = loadImage("iconos/icono (30).png"); 
     icono.resize(t, t);  
     iconos.put( nombres[32], icono);
     icono = loadImage("iconos/icono (27).png");
     icono.resize(t, t);  
     iconos.put( nombres[33], icono);*/
  }

  public void dibujar(String nombre, float x, float y) {
    imageMode(CENTER);
    PImage icono = iconos.get(nombre);
    if (icono!=null)
      image(icono, x, y);
    else
      println(nombre+ " no sirve por alguna razon");
  }
}
////-------------------- el nombre de netAddres y de oscP5 deberian ser especificados en el constructor para un futuro 

class IpManager {
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

  public void set() {

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
    ipEnvio = new Input();
    ipEnvio.setPos(new PVector(width-franjaW*3, margenSuperior));
    ipEnvio.setSize(PApplet.parseInt(franjaW*3.5f)/2, PApplet.parseInt(alto)/2);
    ipEnvio.setNombre("IP");

    puertoEnvio = new Input();
    puertoEnvio.setPos(new PVector(width-franjaW*3, margenSuperior+alto*2));
    puertoEnvio.setSize(PApplet.parseInt(franjaW*3.5f)/2, PApplet.parseInt(alto)/2);
    puertoEnvio.setNombre("Puerto Envio");

    puertoRecivo = new Input();
    puertoRecivo.setPos(new PVector(width-franjaW*3, margenSuperior+alto*4));
    puertoRecivo.setSize(PApplet.parseInt(franjaW*3.5f)/2, PApplet.parseInt(alto)/2);
    puertoRecivo.setNombre("Puerto Recivo");    

    botonConectar = new Boton();
    botonConectar.setPos(new PVector(width-franjaW*3, margenSuperior+alto*8));
    botonConectar.setSize(80, 20);
    botonConectar.setNombre("Conectar");

    esconder();
  }

  public void esconder() {
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
  }

  public void fondo() {
    if (!escondido) {
      pushStyle();
      rectMode(CORNER);
      fill(0, 100);
      noStroke();
      rect(width-franjaW*3-franjaW*0.1f, 0, franjaW*4, height);
      popStyle();
      ipEnvio.dibujar();  
      puertoEnvio.dibujar();
      puertoRecivo.dibujar();
      botonConectar.dibujar();
    }
  }
}
class Modificador {

  String nombre;
  boolean sensible;
  Categoria categoria;
  int[][] paleta;
  int mods;
  Iconos iconos;

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

    if (categoria.sensible) {    
      if (sensible) {
        enSensible( x, y, t);
      } 
    
      tint(col);
      iconos.dibujar(nombre, x, y);
     
      if (mods>0) {
        pushStyle();
        noFill();
        stroke(150, 150, 220);
        ellipse(x, y, t*18/100, t*18/100);
        popStyle();
      }
    } else {
      fill(col);
      ellipse(x, y, t*6/100, t*6/100);
    }
  }

  public void enSensible(float x, float y, float t) {
    pushStyle();
    noFill();
    strokeWeight(1);
    stroke(paleta[2][3]);
    ellipse(x, y, (t*13/100)*1.25f, (t*13/100)*1.25f);
    popStyle();
  }

  public int getCant() {
    return mods;
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
  float tElipse;
  float tLineas;
  float tCuerpo;
  float tam;
  int[][] paleta;
  float centroX ;
  float centroY ;
  float strokeW = 5;
  boolean usarTexto = false;
  Modos() {
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    modo = ESPERA;
  }

  Modos(int[][] paleta_, PVector posCentral_, float tam_) {
    paleta = paleta_;
    modo = ESPERA;
    inicializar(posCentral_, tam_);
  }

  public void inicializar(PVector posCentral, float tam_) {
    centroX = posCentral.x;
    centroY = posCentral.y;
    tam = tam_;
  }

  public void base(float cX, float cY, float t) {
    tElipse = t/11*2;
    tLineas = t/5*2;
    tCuerpo = t/2*2;
    noStroke();
    fill(paleta[2][3]);      
    ellipse(cX, cY, t, t);
  }
   
  public void espera(float cX, float cY, float t) {
    pushMatrix();
    fill(paleta[2][1]);
    stroke(paleta[2][3]); 
    strokeWeight(strokeW);
    translate(cX, cY);      
    ellipse( 0, 0, t, t);
    ellipse( 0-t*1.3f, 0, t, t);
    ellipse( 0+t*1.3f, 0, t, t);
    popMatrix();
  }
  public void agregar(String nombre, float cX, float cY, float t) {
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
  public void eliminar(String nombre, float cX, float cY, float t) {
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
  public void estimulos(String nombre, float cX, float cY, float t) {
    pushStyle();
    stroke(paleta[2][1]);
    strokeWeight(strokeW);
    pushMatrix();
    translate(cX, cY);

    line( 0, (t/3)/3.5f, 0, -(t/3)/2);
    line( 0, (t/3)/3.5f, (t/3)/1.2f, t/3);
    line( 0, (t/3)/3.5f, -(t/3)/1.2f, t/3);
    line( -(t/3), -(t/4)/20, (t/3), -(t/4)/20);
    ellipse( 0, -(t/3)/1.5f, t/5, t/5 );
    popMatrix();
    popStyle();

    if (usarTexto) {       
      fill(paleta[2][2]);
      text(nombre, cX, cY);
    }
  }
  public void opciones(String nombre, float cX, float cY, float t) {
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

  public void ejecutar(String nombre) {
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
      opciones( nombre, centroX, centroY, tElipse*0.9f) ;
    } else {
      text(nombre, centroX, centroY);
    }
  }

  public void setModo(String modo_) {
    modo = modo_;
  }

  public String getModo() {
    return modo;
  }
}
class Monitor {
  int[]  colorsitos;
  int[][]  paleta;
  PVector centro;
  float tam;
  Monitor() {
    colorsitos = new int[5];
    paleta = new int[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }

  Monitor(int[][] paleta_, PVector centro_, float tam_) {
    colorsitos = new int[5];
    paleta = paleta_;
    centro = centro_;
    tam = tam_;
  }

  public void cerrado(float a, float b, float t, int cerrado) {
    pushStyle();
    colorMode(RGB);
    float pI =  HALF_PI;
    float fI = PI+HALF_PI;
    float pD = PI+HALF_PI;
    float fD = TWO_PI+HALF_PI;
    if (cerrado!=1) {
      pI =  HALF_PI+0.2f;
      fI = PI+HALF_PI-0.2f;
      pD = PI+HALF_PI+0.2f;
      fD = TWO_PI+HALF_PI-0.2f;
    }
    strokeWeight(5);
    stroke(paleta[2][3]);
    noFill();
    arc(a, b, t*2, t*2, pI, fI);  
    arc(a, b, t*2, t*2, pD, fD);
    popStyle();
  }

  public void niveles(float a, float b, float t, int nivel) {

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

  public void eje(float a, float b, float t, int inclinacion) {
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

  public void seleccionEstimulo(float a, float b, float t, int seleccionEstimulo) {
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
    arc(a, b, t*2.5f, t*2.5f, p, f);  
    //arc(a, b, t*2, t*2, pD, fD);
    popStyle();
  }

  public void dibujarBase() {
    pushStyle();
    rectMode(CORNER);
    noStroke();
    fill(paleta[1][2]);
    rect(0, 0, width, height/5.3f);

  

    popStyle();
  }
  public void dibujar(int cerrado_, int nivel_, int eje_) {  
    dibujarBase();
    float t = width<height? width/17 : height/17;
    cerrado(width/2, height/10, t, cerrado_);
    niveles(width/2, height/10, t, nivel_);
    eje(width/2, height/10, t, eje_);
  }

  public void dibujar(int cerrado_, int nivel_, int eje_, int seleccionEstimulo_) {  
    dibujarBase();
    float t = width<height? width/17 : height/17;
    cerrado(width/2, height/10, t, cerrado_);
    niveles(width/2, height/10, t, nivel_);
    eje(width/2, height/10, t, eje_);
    seleccionEstimulo(width/2, height/10, t, seleccionEstimulo_);
  }
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

  Opcion(String nombre_,int[][] paleta_) {
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

    for (int j =0; j< 5; j++) {
      float cX = posCentro.x+t/3*cos(angulo);
      float cY = posCentro.y+t/3*sin(angulo);
      float x1 = (cX)+((d/4)*j)*cos(angulo);
      float y1 = (cY)+((d/4)*j)*sin(angulo);
      float x2 = x1+(d/10)*cos(angulo);
      float y2 = y1+(d/10)*sin(angulo);
      float x3 = x1+(d/5.75f)*cos(angulo);
      float y3 = y1+(d/5.75f)*sin(angulo);
      strokeWeight(3);
      line(x1, y1, x2, y2);          

      strokeWeight(5);
      point(x3, y3);
    }
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
boolean mouseActivo = true;

public void setupMouse(){
}

public void drawMouse(){
  if (mouseActivo)
  {
    
  }
}

class UsaMouse{
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
String ESPERA = "espera";
String AGREGAR = "agregar";
String ELIMINAR = "eliminar";
String ESTIMULOS = "estimulos";
String OPCIONES = "opciones";

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
  public void settings() {  size(600, 600);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Carrete" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
