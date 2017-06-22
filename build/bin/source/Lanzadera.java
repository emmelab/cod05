import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import oscP5.*; 
import netP5.*; 
import java.io.InputStreamReader; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Lanzadera extends PApplet {

boolean modoPDE = false;// poner en false cuando se haga una exportacion posta
boolean modoDummies = true && modoPDE;// lo mismo




int lt = 0;
float dt = 1f/60;

PFont openSans_Semibold;
PFont openSans_Light;

int oscP5Port = 12000;
OscP5 oscP5;

public void settings() {
  size( 800, 600 );
}

public void setup(){
  inicializarTipografias(29);
  oscP5 = new OscP5(this,oscP5Port);
  for(AutoSetup auto : autoSetup) auto.setup();
}

public void inicializarTipografias(float textSize){
    openSans_Semibold = createFont( "OpenSans-Semibold.ttf", textSize);
    openSans_Light = createFont( "OpenSans-Light.ttf", textSize);
    textFont(openSans_Semibold);
}

public void draw() {
  dt = (millis()-lt)/1000f;
  lt = millis();
    background(paleta.fondo);
  for(AutoDraw auto : autoDraw) auto.draw();
}
public void keyPressed() {
  if(!interfaz.introActiva)for(AutoKeyPressed auto : autoKeyPressed) auto.keyPressed();
}
public void keyReleased() {
  if(!interfaz.introActiva)for(AutoKeyReleased auto : autoKeyReleased) auto.keyReleased();
}
public void mousePressed() {
  if(!interfaz.introActiva)for(AutoMousePressed auto : autoMousePressed) auto.mousePressed();
}
public void mouseReleased() {
  if(!interfaz.introActiva)for(AutoMouseReleased auto : autoMouseReleased) auto.mouseReleased();
}
ArrayList<AutoSetup> autoSetup = new ArrayList();
ArrayList<AutoDraw> autoDraw = new ArrayList();
ArrayList<AutoKeyPressed> autoKeyPressed = new ArrayList();
ArrayList<AutoKeyReleased> autoKeyReleased = new ArrayList();
ArrayList<AutoMousePressed> autoMousePressed = new ArrayList();
ArrayList<AutoMouseReleased> autoMouseReleased = new ArrayList();

interface AutoSetup { public void setup(); }
interface AutoDraw { public void draw(); }
interface AutoKeyPressed { public void keyPressed(); }
interface AutoKeyReleased { public void keyReleased(); }
interface AutoMousePressed { public void mousePressed(); }
interface AutoMouseReleased { public void mouseReleased(); }

Paleta paleta = new Paleta();
DiccionarioIconos dicIcos = new DiccionarioIconos();
Iconos iconos = new Iconos();
Interfaz interfaz = new Interfaz();
class BarraSuperior implements AutoDraw {
  float margen,alto;
  PImage marca, ayuda;

  BarraSuperior() {
    marca = iconos.get(dicIcos.marca);
    ayuda = iconos.get(dicIcos.ayuda);
    alto = marca.height*1.5f;
    margen = alto/2;
    autoDraw.add(this);
  }

  public void draw() {
    pushStyle();
    noStroke();
    imageMode(CENTER);
    fill(paleta.panelSuperior);
    tint(paleta.inactivo);
      rect(0, 0, width, alto);
      image(marca, marca.width/2+ margen/2, margen);
      image(ayuda, width - ayuda.width/2 - margen/2, margen);
    fill(paleta.ips[0]);
    textSize(32);
    text(oscP5.ip(),margen/2,alto+48);
    popStyle();
  }
}
class BotonBasico implements AutoDraw, AutoMousePressed {
  PVector pos;
  PImage icono;
  float escala = 1;
  
  Tweener hoverEscala = new Tweener().inicializar(1,1,1,1);
  Tweener toggleAlfa = new Tweener().inicializar(.5f,200,255,0);
  
  int col;
  
  boolean toggle,presionado;
  
  BotonBasico(float x, float y, float angulo, String icono, int col) {
    pos = new PVector(x,y,angulo);
    this.icono = iconos.get(icono);
    this.col = col;
    autoDraw.add(this);
    autoMousePressed.add(this);
  }
  
  public void draw(){
    boolean over = over(mouseX,mouseY);
    hoverEscala.actualizar(over?dt:-dt);
    toggleAlfa.actualizar(toggle?dt:-dt);
    
    presionado = false;
    pushStyle();
    imageMode(CENTER);
    pushMatrix();
    translate(pos.x,pos.y);
    rotate(pos.z);
    scale(escala*hoverEscala.valor());
    tint(col,toggleAlfa.valor());
    image(icono,0,0);
    popMatrix();
    popStyle();
  }
  public void mousePressed(){
    if (over(mouseX,mouseY)){
      presionado = true;
      toggle = !toggle;
    }
  }
  
  public boolean over(float x, float y) {
    return dist(x,y,pos.x,pos.y) < icono.width*escala/2;
  }
}
class BotonModulo implements AutoDraw, AutoMousePressed {
  ConfiguracionCOD05.ConfigModulo config;
  EstadoModulo estado = EstadoModulo.LOCAL;
  boolean mostrar = false, remotoEncontrado = false, panelIPsAbierto = false;
  float todoGris = 0;
  Tweener animPos, animAro, animAroConectado,animAlfa,animColor;
  //TwOutBack animPos, animAro, animAroConectado;
  //TwOutQuad animAlfa, animColor;
  PVector pos;
  PImage icono, aroCerrado, aroAbierto;
  int colEncendido, colApagado;

  BotonModulo(PVector pos, String icono, int col) {
    this.pos = pos;
    this.icono = iconos.get(icono);
    this.aroCerrado = iconos.get(dicIcos.aroCerrado);
    this.aroAbierto = iconos.get(dicIcos.aroAbierto);
    if (this.icono == null) this.icono = iconos.iconoVacio();
    this.colEncendido = col;
    this.colApagado = color(red(col)*.299f+green(col)*.587f+blue(col)*.144f);
    animAlfa = (new TwOutQuad()).inicializar(.5f, 0, 255);
    animAro = (new TwOutBack()).inicializar(.5f, this.icono.width*.8f, this.icono.width*1.2f, .5f);
    animAroConectado = (new TwOutBack()).inicializar(animAro);
    animPos = (new TwOutBack()).inicializar(.3f, pos.y-100, pos.y);
    animColor = (new TwOutQuad()).inicializar(.3f);

    autoDraw.add(this);
    autoMousePressed.add(this);
  }

  public void set(ConfiguracionCOD05.ConfigModulo config){
  this.config = config;
  estado = config.estado;
  }

  public void mousePressed() {
    if (dist(pos.x, pos.y, mouseX, mouseY) < icono.width/2) {
      if (estado == EstadoModulo.APAGADO) estado = EstadoModulo.LOCAL;
      else if (estado == EstadoModulo.LOCAL) estado = panelIPsAbierto ? EstadoModulo.REMOTO : EstadoModulo.APAGADO;
      else if (estado == EstadoModulo.REMOTO) estado = EstadoModulo.APAGADO;
      if(config!=null)config.estado = estado;
    }
  }

  public void draw() {
    if (mostrar) {
      animAlfa.actualizar(dt);
      animPos.actualizar(dt);
      if (animPos.estado >= animPos.duracion) {
        animColor.actualizar(estado==EstadoModulo.LOCAL?-dt:dt);
        animAro.actualizar(estado!=EstadoModulo.APAGADO?dt:-dt);
        animAroConectado.actualizar(estado!=EstadoModulo.APAGADO&&(remotoEncontrado||estado==EstadoModulo.LOCAL)?dt:-dt);
      }
    }
    pushStyle();
    imageMode(CENTER);
    tint( lerpColor( colEncendido, colApagado, todoGris), animAlfa.valor());
    pushMatrix();
    translate(pos.x, animPos.valor());
    rotate(pos.z);
    pos.z += dt*.75f;
    if (animAroConectado.estado>0) {
      image(aroAbierto, 0, 0, animAroConectado.valor(), animAroConectado.valor());
    }
    if(animAro.estado > 0){
    rotate(HALF_PI);
    image(aroAbierto, 0, 0, animAro.valor(), animAro.valor());
    }
    popMatrix();
    tint( lerpColor( lerpColor(colEncendido, colApagado, animColor.valor()), colApagado, todoGris), animAlfa.valor());
    image(icono, pos.x, animPos.valor());    
    popStyle();
  }

  public void aro(float x, float y, float w, float h, float divs, float offset) {
    if (divs == 0) {
      ellipse(x, y, w, h);
    } else {
      boolean negativo = divs < 0;
      divs = abs(divs);
      float salto = PI/divs;
      for (int i=0; i<divs; i++) {
        arc(x, y, w, h, salto*(i*2+offset-(negativo?1:0)), salto*(i*2+offset+(negativo?0:1)));
      }
    }
  }
}
class CampoIP implements AutoDraw, AutoKeyPressed, AutoMousePressed {
  ConfiguracionCOD05.ConfigModulo config;
  boolean focus = false, focusPort = false, borrarTodo = true;
  PVector pos, tam, posInputPuerto,tamInputPuerto;
  String port = "12000", portInput = port;
  String ip = "127.0.0.1", ipInput = ip;
  int col;

  CampoIP(float x, float y, float w, float h, int col, float porcentajeTamInputPuerto) {
    pos = new PVector(x, y);
    tam = new PVector(w*(1-porcentajeTamInputPuerto)-3, h);
    posInputPuerto = new PVector(tam.x+3,0);
    tamInputPuerto = new PVector(w*porcentajeTamInputPuerto-6,h);

    this.col = col;

    autoDraw.add(this);
    autoMousePressed.add(this);
    autoKeyPressed.add(this);
  }

  public void set(ConfiguracionCOD05.ConfigModulo config){
    this.config = config;
    set(config.ip,config.puerto);
  }

  public boolean overIp(float x, float y) {
    return (x > pos.x && y > pos.y && x < pos.x+tam.x && y < pos.y+tam.y);
  }
  public boolean overPuerto(float x, float y) {
    return (x > pos.x+posInputPuerto.x && y > pos.y+posInputPuerto.y &&
    x < pos.x+posInputPuerto.x+tamInputPuerto.x && y < pos.y+posInputPuerto.y+tamInputPuerto.y);
  }

  public void mousePressed() {
    focusPort = focus = false;
    if (overIp(mouseX, mouseY)) {
      focus = true;
    }
    else if (overPuerto(mouseX,mouseY)){
      focusPort = true;
    }
    if(!focus && !focusPort){
      borrarTodo=true;
    }
    if(!focus){
        ipInput = ip;
    }
    if (!focusPort){
    portInput = port;
    }
  }

public void set(String newIp, int newPort){
  ipInput = newIp;
  portInput = str(newPort);
  digerirPort();
  digerirIp();
}

public void digerirPort(){
    focusPort = false;
    borrarTodo = true;
  int val = PApplet.parseInt(portInput);
  if (val < 1024) val = 1024;
  else if (val > 65534) val = 65534;
  port = portInput = str(val);
  if(config != null)config.puerto = val;
}
  public void digerirIp(){
    focus = false;
    borrarTodo = true;
    String[] partesIp = ipInput.split("\\.");
    ipInput = "";
    for (int i=0; i<4; i++){
      if (i!=0)ipInput+=".";
      if (i<partesIp.length){
        if (partesIp[i].length() == 0) ipInput += "0";
        else if (partesIp[i].length() > 3) ipInput += partesIp[i].substring(0,3);
        else ipInput += partesIp[i];
      }
      else ipInput += "0";
    }
    ip = ipInput;
  if(config != null)config.ip = ip;
  }
  
  public void keyPressed(){
    if(focus)keyPressedIp();
    else if (focusPort)keyPressedPort();
  }
  
  public void keyPressedPort(){
    if (keyCode == ESC) {
        keyCode = RETURN;
        key = ' ';
        focusPort = false;
        borrarTodo = true;
        portInput = port;
      }
      else if (keyCode == BACKSPACE) {
        if (borrarTodo) portInput = "";
        else if (portInput.length()>0) portInput = portInput.substring(0,portInput.length()-1);
        borrarTodo = false;
      }
      else{
        borrarTodo = false;
        if (keyCode == ENTER || keyCode == RETURN){
        digerirPort();
      }
      else if (key >= '0' && key <= '9') {
         portInput += key;
      }
      }
  }
  public void keyPressedIp(){
      if (keyCode == ESC) {
        keyCode = RETURN;
        key = ' ';
        focus = false;
        borrarTodo = true;
        ipInput = ip;
      }
      else if (keyCode == BACKSPACE) {
        if (borrarTodo) ipInput = "";
        else if (ipInput.length()>0) ipInput = ipInput.substring(0,ipInput.length()-1);
        borrarTodo = false;
      }
      else{
        borrarTodo = false;
        if (keyCode == ENTER || keyCode == RETURN){
        digerirIp();
      }
      else if (key == '.') {
        if (ipInput.matches("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*")){
          digerirIp();
        }
        else ipInput += key;
      }
      else if (key >= '0' && key <= '9') {
         ipInput += key;
      }}
    }
  

  public void draw() {
    drawIp();
    drawPuerto();
  }
  
  public void drawIp(){
    drawGeneric(focus,pos,tam,"999.999.999.999",ipInput);
  }
  public void drawPuerto(){
    drawGeneric(focusPort,PVector.add(pos,posInputPuerto),tamInputPuerto,"65535",portInput);
  }
  public void drawGeneric(boolean focus, PVector pos, PVector tam, String tamRefText, String text){
    pushStyle();
    pushMatrix();
    if (focus) {
      stroke(paleta.play);
      noFill();
    } else {
      noStroke();
      fill(col);
    }
    translate(pos.x, pos.y);
    rect(0, 0, tam.x, tam.y);
    textSize(tam.y-5);
    textAlign(LEFT, CENTER);
    translate((tam.x-textWidth(tamRefText))/2, tam.y/2);
    fill(focus?paleta.play:paleta.fondo);
    text(text + (focus && frameCount%60<30?"|":""), 0, -textAscent()/6);
    popMatrix();
    popStyle();
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

ConfiguracionCOD05(){
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
    if(lienzo==null)lienzo = new ConfigModulo().Iniciar("lienzo", 12010);
    if(observador==null)observador = new ConfigModulo().Iniciar("observador", 12020);
    if(carrete==null)carrete = new ConfigModulo().Iniciar("carrete", 12030);
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
class Ejecutador {
  ConfiguracionCOD05 config;
  boolean modoUtileria;
  int nacimiento;
  
  Process lienzo,observador,carrete;

  String javaPath = System.getProperty("java.home");

  String dirReal = sketchPath(""), //sketchPath("..\\lib\\*"),
    lienzoReal = "Lienzo", 
    observadorReal = "Observador", 
    carreteReal = "Carrete";

  String dirUtileria = sketchPath("..\\modulosDeUtileria\\lib\\*"), 
    lienzoUtileria = "dummyLienzo", 
    observadorUtileria = "dummyObservador", 
    carreteUtileria = "dummyCarrete";

  String ejecutarKeyword = "%ejec";
  String templateLanzador = "";

  Ejecutador(ConfiguracionCOD05 config, boolean modoUtileria) {
    nacimiento = millis();
    if (config != null) {
      XML xmlArchivo = null;
      if (new File(sketchPath(archivoConfigXML)).exists()) xmlArchivo = loadXML( archivoConfigXML );
      if (xmlArchivo == null) xmlArchivo = new XML("COD05");
      XML xmlEjecucion = xmlArchivo.getChild(xmlTagEjecucion);
      if (xmlEjecucion != null) xmlArchivo.removeChild(xmlEjecucion);
      xmlArchivo.addChild(config.guardar(xmlTagEjecucion));
      saveXML(xmlArchivo, archivoConfigXML);
      this.config = config;
    }
    this.modoUtileria = modoUtileria;
    if(modoUtileria)templateLanzador = "\""+javaPath+"\\bin\\java\" -cp "+dirUtileria+" "+ejecutarKeyword ;
    else templateLanzador = dirReal+ejecutarKeyword;
  }
  
  public boolean enEjecucion() {
    if (lienzo == null && observador == null && carrete == null) return false;
    else {
      if (lienzo != null) {
        if(!lienzo.isAlive()) lienzo = null;
      }
      if (observador != null) {
        if(!observador.isAlive()) observador = null;
      }
      if (carrete != null) {
        if(!carrete.isAlive()) carrete = null;
      }
    return true;
    }
  }
  
  public void ejecutarLocales() {
    if (modoUtileria) {
      if (config.lienzo.estado == EstadoModulo.LOCAL) {
        lienzo=launch( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      /*  PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);*/
      }
      if (config.observador.estado == EstadoModulo.LOCAL) {
        observador=launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
       /* PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador);*/
      }
      if (config.carrete.estado == EstadoModulo.LOCAL) {
        carrete=launch( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
       /* PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);*/
      }
    } else {
      if (config.lienzo.estado == EstadoModulo.LOCAL) {
        lienzo=launch( templateLanzador.replace(ejecutarKeyword, lienzoReal));
    /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);  */
    }
      if (config.observador.estado == EstadoModulo.LOCAL) {
        observador=launch( templateLanzador.replace(ejecutarKeyword, observadorReal));
    /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador); */ 
    }
      if (config.carrete.estado == EstadoModulo.LOCAL) {
        carrete=launch( templateLanzador.replace(ejecutarKeyword, carreteReal));
    /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);  */
    }
    }
  }

  public void ejecutarLienzo() {
    if (modoUtileria) {
      lienzo=launch( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);*/
    } else {
      lienzo=launch( templateLanzador.replace(ejecutarKeyword, lienzoReal));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);*/
    }
  }
  public void ejecutarObservador() {
    if (modoUtileria) {
      observador=launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador);*/
    } else {
      observador=launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador);*/
    }
  }
  public void ejecutarCarrete() {
    if (modoUtileria) {
      carrete=launch( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);*/
    } else {
      carrete=launch( templateLanzador.replace(ejecutarKeyword, carreteReal));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);*/
    }
  }
}
class DiccionarioIconos {
  String
    aroCerrado = "aro-cerrado", 
    aroAbierto = "aro-abierto", 
    play = "play", 
    marca = "marca", 
    mas = "mas", 
    menos = "menos", 
    ayuda = "ayuda", 
    observador = "observador", 
    carrete = "carrete", 
    lienzo = "lienzo", 
    conexion = "conexion";
}

class Iconos implements AutoSetup {
  PImage iconoVacio_ref;
  HashMap<String, PImage> iconos;

  String[] preload = {
    dicIcos.lienzo, 
    dicIcos.carrete, 
    dicIcos.observador, 
    dicIcos.play, 
    dicIcos.aroCerrado, 
    dicIcos.aroAbierto,
    dicIcos.mas,
    dicIcos.menos,
  };

  Iconos() {
    autoSetup.add(this);
  }
  public void setup() {
    iconos = new HashMap<String, PImage>();
    for (int i=0; i<preload.length; i++) {
      PImage icono = loadImage("../iconos/"+ preload[i]+".png");         
      iconos.put(preload[i], icono);
    }
  }

  public PImage iconoVacio() {
    if (iconoVacio_ref ==null) {
      PGraphics graf = createGraphics(32, 32);
      graf.beginDraw();
      graf.background(0);
      graf.noStroke();
      graf.fill(255);
      graf.ellipse(graf.width/2, graf.height/2, graf.width, graf.height);
      graf.fill(0);
      graf.textAlign(CENTER);
      graf.textSize(8);
      graf.text("Icono\nperdido", graf.width/2, graf.height/2);
      graf.endDraw();
      iconoVacio_ref = graf.get();
      iconoVacio_ref.mask(iconoVacio_ref);
    }
    return iconoVacio_ref;
  }

  public PImage get(String nombre) {
    if (iconos == null) {
      println("Error: pidiendo icono antes de ser cargado -> "+nombre);    
      return null;
    } else {
      PImage icono = iconos.get(nombre);
      if (icono==null) {
        icono = loadImage("../iconos/"+ nombre+".png");
        if (icono == null) {
          println(nombre+ " no encontrado");
          icono = iconoVacio();
        }
        iconos.put(nombre, icono);
      }
      return icono;
    }
  }

  public void dibujar(String nombre, float x, float y) {
    imageMode(CENTER);
    PImage icono = iconos.get(nombre);
    if (icono!=null)
      image(icono, x, y);
    else {
      icono = loadImage("../iconos/"+ nombre+".png");
      iconos.put(nombre, icono);
      if (icono != null) image(icono, x, y);
      else {
        println(nombre+ " no encontrado");
        iconos.put(nombre, iconoVacio());
        image(icono, x, y);
      }
    }
  }
}
class Interfaz implements AutoSetup, AutoDraw {
  ConfiguracionCOD05 config;
  BotonBasico botonPlay;
  BotonModulo lienzo, observador, carrete;
  InterfazYSensorConexion interfazYSensorConexion;
  BarraSuperior barraSuperior;
  TwOutQuad animTodoGris;
  Ejecutador ejecutadorLocal;//vive hasta que se mueran sus procesos
  boolean todoLocal = true;
  int pingOff = 4000;
  float pingTimer = 4, pingFrec = 1;

  Interfaz() {
    autoSetup.add(this);
    autoDraw.add(this);
    animTodoGris = (TwOutQuad)(new TwOutQuad()).inicializar(.9f, 0, 3);
  }
  public void setup() {
    {
      float verti = height/2;
      float sepHoriz = 160;
      lienzo = new BotonModulo(new PVector(width/2-sepHoriz, verti), dicIcos.lienzo, paleta.ips[0]);
      observador = new BotonModulo(new PVector(width/2, verti), dicIcos.observador, paleta.ips[1]);
      carrete = new BotonModulo(new PVector(width/2+sepHoriz, verti), dicIcos.carrete, paleta.ips[2]);
    }
    interfazYSensorConexion = new InterfazYSensorConexion();
    barraSuperior = new BarraSuperior();
    cargarDatos();

    botonPlay = new BotonBasico(90, height-90, 0, "play", paleta.play);
    botonPlay.escala = .6f;
    botonPlay.hoverEscala = new TwOutBack().inicializar(.25f, 1, 1.1f, 0);
    botonPlay.toggleAlfa = new TwOutQuad().inicializar(.25f, 255, 25, 0);
  }
  public void draw() {
    if (introActiva)intro();
    else {
      //interfazYSensorConexion.visible = !lienzo.local || !observador.local || !carrete.local;
      todoLocal = lienzo.estado == EstadoModulo.LOCAL && observador.estado == EstadoModulo.LOCAL && carrete.estado == EstadoModulo.LOCAL;
      grisPorTodoLocal();
      pingTesting();
    }

    lienzo.colEncendido = interfazYSensorConexion.lienzo.col;
    observador.colEncendido = interfazYSensorConexion.observador.col;
    carrete.colEncendido = interfazYSensorConexion.carrete.col;

    if (ejecutadorLocal != null) {
      if (!ejecutadorLocal.enEjecucion()) {
        ejecutadorLocal = null;
        botonPlay.toggle = false;
      } else {
        botonPlay.toggle = true;
      }
    } else if (botonPlay.presionado && botonPlay.toggle) ejecutar();
  }

  public void ejecutar() {
    guardarDatos();
    ejecutadorLocal = new Ejecutador(config, modoPDE);
    ejecutadorLocal.ejecutarLocales();
    controlOsc.ejecutarRemotos(config);
  }

  public void cargarDatos() {
    if (config == null) config = new ConfiguracionCOD05();
    XML xmlConfig = null;
    if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
    if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagPanel);

    config.cargar(xmlConfig);
    lienzo.set(config.lienzo);
    observador.set(config.observador);
    carrete.set(config.carrete);
    interfazYSensorConexion.setConfig(config);
  }
  public void guardarDatos() {
    if (config != null) {
      XML xmlArchivo = null;
      if (new File(sketchPath(archivoConfigXML)).exists()) xmlArchivo = loadXML( archivoConfigXML );
      if (xmlArchivo == null) xmlArchivo = new XML("COD05");
      XML xmlPanel = xmlArchivo.getChild(xmlTagPanel);
      if (xmlPanel != null) xmlArchivo.removeChild(xmlPanel);
      xmlArchivo.addChild(config.guardar(xmlTagPanel));
      saveXML(xmlArchivo, archivoConfigXML);
    }
  }

  float introTime = 0;
  boolean introActiva = true;
  public void intro() {
    float introBotonModuloBase = 2, introBotonModuloSep = .05f;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*0))lienzo.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*1))observador.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*2))carrete.mostrar = true;

    introTime += dt;
    if (introTime > introBotonModuloBase+introBotonModuloSep*2) introActiva = false;
  }
  public boolean introCheck(float t) {
    return introTime < t && introTime+dt >= t;
  }

  public void grisPorTodoLocal() {
    carrete.panelIPsAbierto = observador.panelIPsAbierto = lienzo.panelIPsAbierto = interfazYSensorConexion.visible;
    todoLocal = todoLocal && !interfazYSensorConexion.visible;
    animTodoGris.actualizar(todoLocal?dt:-dt);
    carrete.todoGris = constrain(animTodoGris.valor()-2, 0, 1);
    observador.todoGris = constrain(animTodoGris.valor()-1, 0, 1);
    lienzo.todoGris = constrain(animTodoGris.valor(), 0, 1);
  }

  public void pingTesting() {
    pingTimer -= dt;
    if (pingTimer <= 0) {
      pingTimer = pingFrec;
      if (config.lienzo.estado==EstadoModulo.REMOTO) {
        controlOsc.pingLienzo(config.lienzo.ip, config.lienzo.puerto);
        if (!lienzo.remotoEncontrado) {
          lienzo.animAro.actualizar(-.3f);
        }
      }
      if (config.observador.estado==EstadoModulo.REMOTO) {
        controlOsc.pingObservador(config.observador.ip, config.observador.puerto);
        if (!observador.remotoEncontrado) {
          observador.animAro.actualizar(-.3f);
        }
      }
      if (config.carrete.estado==EstadoModulo.REMOTO) {
        controlOsc.pingCarrete(config.carrete.ip, config.carrete.puerto);
        if (!carrete.remotoEncontrado) {
          carrete.animAro.actualizar(-.3f);
        }
      }
    }

    lienzo.remotoEncontrado = millis()-controlOsc.ultimoPingLienzo <= pingOff;
    observador.remotoEncontrado = millis()-controlOsc.ultimoPingObservador <= pingOff;
    carrete.remotoEncontrado = millis()-controlOsc.ultimoPingCarrete <= pingOff;
  }
}
//void reintentarConexion

public void oscStatus(OscStatus estado) {
  println("OscStatus = "+estado);
  oscStatus(estado.id());
}
public void oscStatus(int estado) {
  if (estado != OscStatus.DEFAULT && estado != OscStatus.SEND_FAILED && estado != OscStatus.OSCP5_CLOSED) {
    println("algun problema de conexion: "+estado);
  }
}
/*void oscEvent(OscMessage msj) {
  println(msj);
}*/

class InterfazYSensorConexion implements AutoDraw {
  ConfiguracionCOD05 config;
  TwOutQuad animacion;
  TwInOutBack tweenPanel;
  PImage iconoConexion;
  //String iconoConexion;
  boolean visible = false;

  boolean reintantando;
  final String ipLocalHost = "127.0.0.1";

  String ipCarrete = ipLocalHost, ipObservador = ipLocalHost, ipLienzo = ipLocalHost;
  String portCarrete = "12000", portObservador = "11000", portLienzo = "10000";
  float tamPanelInferior = 180;
  CampoIP lienzo, observador, carrete;
  float[] posYBase;
  float anchoCampoIP = 400, altoCampoIP = 30;

  BotonBasico mas, menos;
  PVector ejeMasMenos;

  InterfazYSensorConexion() {
    autoDraw.add(this);
    iconoConexion = iconos.get(dicIcos.conexion);
    //if (this.iconoConexion == null) this.iconoConexion = iconos.iconoVacio();
    animacion = (TwOutQuad)(new TwOutQuad()).inicializar(0.5f);
    tweenPanel = (TwInOutBack)(new TwInOutBack()).inicializar(.7f);

    ejeMasMenos = new PVector(width + tamPanelInferior/2, height-tamPanelInferior/2, tamPanelInferior);
    mas = new BotonBasico( ejeMasMenos.x - ejeMasMenos.z, ejeMasMenos.y, 0, dicIcos.mas, paleta.panelSuperior );
    menos = new BotonBasico( ejeMasMenos.x, ejeMasMenos.y + ejeMasMenos.z, HALF_PI, dicIcos.menos, paleta.fondo );
    mas.escala = menos.escala = (tamPanelInferior-90)/mas.icono.height;

    float xBase = width/2-anchoCampoIP/2;
    posYBase = new float[]{ tamPanelInferior/2-altoCampoIP*2, tamPanelInferior/2-altoCampoIP*.5f, tamPanelInferior/2+altoCampoIP*1 };
    lienzo = new CampoIP(xBase, posYBase[0], anchoCampoIP, altoCampoIP, paleta.ips[0], .4f);
    observador = new CampoIP(xBase, posYBase[1], anchoCampoIP, altoCampoIP, paleta.ips[1], .4f);
    carrete = new CampoIP(xBase, posYBase[2], anchoCampoIP, altoCampoIP, paleta.ips[2], .4f);
  }
  public void draw() {
    boolean sinConexion = oscP5.ip().equals(ipLocalHost);
    pushStyle();
    //if (sinConexion)
    {

      fill(255, 0, 0);
      textAlign(LEFT, TOP);
      textSize(16);
      text("Sin Conexion (posiblemente)", 12, 8);
    }
    popStyle();
    panelInferior();

    observador.col = (lienzo.ip .equals( observador.ip) ) ? paleta.ips[0] : paleta.ips[1];
    carrete.col = (observador.ip .equals( carrete.ip) ) ? observador.col : (lienzo.ip .equals( carrete.ip) ) ? lienzo.col : paleta.ips[2];
  }

  public void setConfig(ConfiguracionCOD05 config) {
    this.config = config;
    lienzo.set(config.lienzo);
    observador.set(config.observador);
    carrete.set(config.carrete);
    visible = config.panelConexiones;
  }

  public void panelInferior() {
    tweenPanel.actualizar(visible?dt:-dt);

    menos.pos.z = tweenPanel.valor()*HALF_PI+HALF_PI;
    mas.pos.z = menos.pos.z+HALF_PI;
    menos.pos.set(ejeMasMenos.x+ejeMasMenos.z*cos(menos.pos.z), ejeMasMenos.y+ejeMasMenos.z*sin(menos.pos.z));
    mas.pos.set(ejeMasMenos.x+ejeMasMenos.z*cos(mas.pos.z), ejeMasMenos.y+ejeMasMenos.z*sin(mas.pos.z));
    if (menos.presionado || mas.presionado) {
      visible = !visible;
      config.panelConexiones = visible;
    }

    float offsetPanel = height-tweenPanel.valor()*tamPanelInferior;

    pushStyle();
    pushMatrix();
    noStroke();
    fill(paleta.panelSuperior);
    translate(0, offsetPanel);
    rect(0, 0, width, tamPanelInferior*2);
    popMatrix();
    popStyle();

    lienzo.pos.y = posYBase[0]+offsetPanel;
    observador.pos.y = posYBase[1]+offsetPanel;
    carrete.pos.y = posYBase[2]+offsetPanel;
  }
}
ControlOsc controlOsc = new ControlOsc();

class ControlOsc implements AutoSetup {
  OscP5 osc;
  ConfiguracionCOD05 configRemota;
  Ejecutador ejecutador;//6 seg de vida
  
  int ultimoPingLienzo,ultimoPingObservador,ultimoPingCarrete;
  //String 
  
  String resultado = "/resultado";
  
  String lanzar = "/Lanzadera/lanzar";
  String responderLanzar = "responderLanzar";
  
  String establecerIPs = "/Lanzadera/establecerIPs";
  String responderEstablecerIPs = "responderEstablecerIPs";
  String callbackEstablecerIPs = "callbackEstablecerIPs";
  String pedirIPs = "/Lanzadera/pedirIPs";
  String callbackPedirIPs = "callbackPedirIPs";
  String establecerEstados = "/Lanzadera/establecerEstados";
  String responderEstablecerEstados = "responderEstablecerEstados";
  String callbackEstablecerEstados = "callbackEstablecerEstados";
  String pedirEstados = "/Lanzadera/pedirEstados";
  String callbackPedirEstados = "callbackPedirEstados";
  String ping = "/Lanzadera/ping";
  String responderPing = "responderPing";
  String callbackPing = "callbackPing";
  
  OscMessage msjPingLienzo,msjPingObservador,msjPingCarrete;
  
  ControlOsc(){
    autoSetup.add(this);
  }
             
  public void setup() {
    osc = oscP5;
    osc.plug(this,responderLanzar,lanzar);
    
    osc.plug(this,responderEstablecerIPs,establecerIPs);
    osc.plug(this,callbackEstablecerIPs,establecerIPs+resultado);
    osc.plug(this,callbackPedirIPs,pedirIPs+resultado);
    
    osc.plug(this,responderEstablecerEstados,establecerEstados);
    osc.plug(this,callbackEstablecerEstados,establecerEstados+resultado);
    osc.plug(this,callbackPedirEstados,pedirEstados+resultado);
    
    osc.plug(this,callbackPing,ping+resultado);
    osc.plug(this,responderPing,ping);
    
    msjPingLienzo = new OscMessage("/Lanzadera/ping").add(osc.ip()).add(oscP5Port).add(0);
    msjPingObservador = new OscMessage("/Lanzadera/ping").add(osc.ip()).add(oscP5Port).add(1);
    msjPingCarrete = new OscMessage("/Lanzadera/ping").add(osc.ip()).add(oscP5Port).add(2);
  }
  
  public void responderEstablecerIPs(String lienzoIp, int lienzoPort,
                              String observadorIp, int observadorPort,
                              String carreteIp, int carretePort) {
     if (configRemota == null) configRemota = new ConfiguracionCOD05();
     configRemota.lienzo.ip = lienzoIp;
     configRemota.lienzo.puerto = lienzoPort;
     configRemota.observador.ip = observadorIp;
     configRemota.observador.puerto = observadorPort;
     configRemota.carrete.ip = carreteIp;
     configRemota.carrete.puerto = carretePort;
     
     if (configRemota.lienzo.estado != EstadoModulo.APAGADO) {
       if (osc.ip().equals(lienzoIp)) configRemota.lienzo.estado = EstadoModulo.LOCAL;
       else configRemota.lienzo.estado = EstadoModulo.REMOTO;
     }
     if (configRemota.observador.estado != EstadoModulo.APAGADO) {
       if (osc.ip().equals(observadorIp)) configRemota.observador.estado = EstadoModulo.LOCAL;
       else configRemota.observador.estado = EstadoModulo.REMOTO;
     }
     if (configRemota.carrete.estado != EstadoModulo.APAGADO) {
       if (osc.ip().equals(carreteIp)) configRemota.carrete.estado = EstadoModulo.LOCAL;
       else configRemota.carrete.estado = EstadoModulo.REMOTO;
     }
  }
  public void responderEstablecerEstados(int lienzo, int observador, int carrete) {
     if (configRemota == null) configRemota = new ConfiguracionCOD05();
     configRemota.lienzo.estado = EstadoModuloList[lienzo];
     configRemota.observador.estado = EstadoModuloList[observador];
     configRemota.carrete.estado = EstadoModuloList[carrete];
  }
  
  public void ejecutarRemotos(ConfiguracionCOD05 config){
    ejecutarRemotos(config,config.lienzo,0);
    ejecutarRemotos(config,config.observador,1);
    ejecutarRemotos(config,config.carrete,2);
  }
  public void ejecutarRemotos(ConfiguracionCOD05 config, ConfiguracionCOD05.ConfigModulo modulo, int moduloID){
    if (modulo.estado == EstadoModulo.REMOTO) {
      if (modulo.ip.equals(osc.ip()) && !modoDummies) modulo.estado = EstadoModulo.LOCAL;
      else {
        NetAddress destino = new NetAddress(modulo.ip,(modoDummies?modulo.puerto:oscP5Port));
        OscMessage msj = new OscMessage(establecerEstados)
                          .add(EstadoModuloToInt(config.lienzo.estado))
                          .add(EstadoModuloToInt(config.observador.estado))
                          .add(EstadoModuloToInt(config.carrete.estado));
        osc.send(msj,destino);
        msj = new OscMessage(establecerIPs)
                          .add(config.lienzo.ip).add(config.lienzo.puerto)
                          .add(config.observador.ip).add(config.observador.puerto)
                          .add(config.carrete.ip).add(config.carrete.puerto);
        osc.send(msj,destino);
        osc.send(new OscMessage(lanzar).add(moduloID),destino);
      }
    }
  }
  
  public void responderLanzar(int moduloID){
    println("responderLanzar("+moduloID+")");
  if (configRemota == null) configRemota = new ConfiguracionCOD05();
  if(ejecutador == null) ejecutador = new Ejecutador(configRemota,modoPDE);
  else if(millis()-ejecutador.nacimiento>6000) ejecutador = new Ejecutador(configRemota,modoPDE);
  if (moduloID == 0) ejecutador.ejecutarLienzo();
  if (moduloID == 1) ejecutador.ejecutarObservador();
  if (moduloID == 2) ejecutador.ejecutarCarrete();
  }
  
public void responderPing(String remitenteIp, int remitentePort, int id){
  if (osc != null){
    OscMessage msj = new OscMessage(controlOsc.ping+controlOsc.resultado);
    msj.add(id);
    osc.send(msj,new NetAddress( remitenteIp,remitentePort));
  }
}
public void callbackPing(int id) {
  if(id==0) controlOsc.ultimoPingLienzo = millis();
  else if(id==1) controlOsc.ultimoPingObservador = millis();
  else if(id==2) controlOsc.ultimoPingCarrete = millis();
}
public void callbackEstablecerIPs(){}
public void callbackPedirIPs(){}
public void callbackEstablecerEstados(){}
public void callbackPedirEstados(){}
  
  public void pingLienzo(String ip, int puerto) {
    if (osc != null) osc.send(msjPingLienzo,new NetAddress(ip,modoDummies?puerto:oscP5Port));
  }
  public void pingObservador(String ip, int puerto) {
    if (osc != null) {
      osc.send(msjPingObservador,new NetAddress(ip,modoDummies?puerto:oscP5Port));
    }
  }
  public void pingCarrete(String ip, int puerto) {
    if (osc != null) osc.send(msjPingCarrete,new NetAddress(ip,modoDummies?puerto:oscP5Port));
  }
}


class PAppConsola extends PApplet {

    String que = "nada";
    
  public void settings() {
    size(400, 600);
  }

  public void draw() {
    background(0);
    textSize(24);
    translate(24, 48);
    text( addLinebreakForWidth(que, width-48, 0, null), 0, 0);
  }

  public void pasarStream(Process process) {
    try{
    process.waitFor();
    println(process.exitValue());
    que = getStringFromInputStream(process.getErrorStream());
    }
    catch(Exception e){
      que = e.getMessage();
    }
  }

  public String getStringFromInputStream(InputStream is) {

    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();

    String line;
    try {

      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 
    finally {
      if (br != null) {
        try {
          br.close();
        } 
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return sb.toString();
  }

  public String addLinebreakForWidth(String originalText, float maxWidth, float textSize, PFont font) {
    pushStyle();  
    if (font != null) textFont(font);
    if (textSize > 0)textSize(textSize);
    String construct = "";
    String[] lines = originalText.split("\n");
    for (int i=0; i<lines.length; i++) {
      String[] words = lines[i].split(" ");
      if (i!=0)construct += "\n";
      construct += words[0];
      float currentWidth = textWidth(words[0]);
      for (int j=1; j<words.length; j++) {
        float addedWidth = textWidth(" "+words[j]);
        if (currentWidth+addedWidth > maxWidth) {
          construct += "\n"+words[j];
          currentWidth = textWidth(words[j]);
        } else {
          currentWidth += addedWidth;
          construct += " "+words[j];
        }
      }
    }
    popStyle();
    return construct;
  }
}
class Paleta implements AutoSetup {
  int play,inactivo,ayuda,mas,fondo,panelSuperior,marca;
  int[] ips;
  
  Paleta(){
    autoSetup.add(this);
  }
  
  public void setup() {
    play = color(0xffBEBE40);
    inactivo = color(0xff3E4545);
    ips = new int[]{color(0xffB44343),color(0xffB44382),color(0xff7543B4)};
    ayuda = color(0xff43B478);
    mas = color(0xff3A4040);
    fondo = color(0xff1A1D1E);
    panelSuperior = color(0xff141516);
    marca = color(0xff42494A);
  }
}
//      10/5/2017
class Tweener {
  boolean limitado = true;
  float valorMenor = 0, valorMayor = 1;
  float duracion = 1;
  float estado = 0;
  
  private boolean errorDuracion = true;
  
  public float actualizar(float dt){
    estado += dt;
    if (limitado) {
      if (duracion <= 0 && errorDuracion){
        println("ERROR duracion menor que zero");
        errorDuracion = false;
      }
      if (estado < 0) estado = 0;
      else if (estado >= duracion) estado = duracion;
    }
    return valor();
  }
  public float valor(){
    return lerp(valorMenor,valorMayor,estado/duracion);
  }
  
  Tweener(){
  }
  public Tweener inicializar(Tweener otro){
    return inicializar(otro.duracion,otro.valorMenor,otro.valorMayor,otro.estado,otro.limitado);
  }
  public Tweener inicializar(float duracion){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  public Tweener inicializar(float duracion, float valorMenor, float valorMayor){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  public Tweener inicializar(float duracion, float estado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  public Tweener inicializar(float duracion, float valorMenor, float valorMayor, float estado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }
  public Tweener inicializar(boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  public Tweener inicializar(float duracion,boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  public Tweener inicializar(float duracion, float valorMenor, float valorMayor,boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  public Tweener inicializar(float duracion, float estado,boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  public Tweener inicializar(float duracion, float valorMenor, float valorMayor, float estado, boolean limitado){
    this.duracion = duracion;
    this.valorMenor = valorMenor;
    this.valorMayor = valorMayor;
    this.estado = estado;
    this.limitado = limitado;
    return this;
  }
}
class TwOutQuad extends Tweener {
  public float valor(){
    return lerp( valorMenor, valorMayor, pow(estado/duracion,2));
  }
}
class TwOutBack extends Tweener {
    float s = 1.70158f;
  public float valor(){
    float t = estado;
    return lerp(valorMenor, valorMayor, ((t=t/duracion-1)*t*((s+1)*t + s) + 1));
  }
}
class TwInOutBack extends Tweener {
    float s = 1.70158f;
  public float valor(){
    float tempS = s;
    float t = estado;
    if ((t/=duracion/2) < 1) return lerp(valorMenor,valorMayor,1.f/2*(t*t*(((tempS*=(1.525f))+1)*t - tempS)));
    return lerp(valorMenor,valorMayor, 1.f/2*((t-=2)*t*(((tempS*=(1.525f))+1)*t + tempS) + 2));
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Lanzadera" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
