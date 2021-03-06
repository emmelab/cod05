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

public class dummyLanzadera extends PApplet {

boolean export = false;





ConfiguracionCOD05 config;
OscP5 oscP5;
int puerto = 14000;
int sel = -1;
float conectaTimer = 2000;
boolean pausarOsc = false;

int[] ultimoPing = {0, 0, 0};

int conectandoStringRnd = 0;
String[] conectandoString = {".", ":", "¨", " .", "..", ":.", ".:", " :", "::", "¨.", ".¨", ":¨"};

String dirUtileria = export?"*":"../../lib/*";
String javaPath = "";
String[] opciones = {"Lienzo", "Observador", "Carrete"};
String[] ejecucion = {"dummyLienzo", "dummyObservador", "dummyCarrete"};

public void setup() {
  
  dirUtileria = sketchPath(dirUtileria);
  javaPath = System.getProperty("java.home");
  println("dirUtileria = "+dirUtileria+"\njavaPath = "+javaPath);

  if (config == null) config = new ConfiguracionCOD05();
  XML xmlConfig = null;
  if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
  if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagPanel);

  config.cargar(xmlConfig);
  if (xmlConfig==null) {
    config.lienzo.puerto = floor(random(12015, 13000));
    config.observador.puerto = floor(random(12015, 13000));
    config.carrete.puerto = floor(random(12015, 13000));
  }
}

public void mousePressed() {
  if (mouseX > width-50 && mouseY < 50) {
    java.awt.Point loc = ((java.awt.Canvas)surface.getNative()).getLocationOnScreen();
    String[] args = {ARGS_SKETCH_FOLDER+"="+sketchPath("."), ARGS_LOCATION+"="+loc.x+","+(loc.y+height+30), "dummyLanzadera"};
    PApplet.runSketch(args, new dummyLanzadera());
  } else if (mouseY < 80 && !pausarOsc) {
    if (oscP5 != null) {
      oscP5.stop();
      oscP5 = null;
    }
    puerto ++;
    if (puerto > 14010) puerto = 14000;
    conectaTimer = millis()+2000;
  } else if (sel != -1 && !pausarOsc) {
    thread("ejecutarSel");
  }
}

public void ejecutarSel() {
  int sel = this.sel;
  if (sel == -1) return;
  XML xmlConfig = new XML("xml");
  xmlConfig.addChild(config.guardar("ejecucion"));
  String launchString = "\""+javaPath+"/bin/java\" -cp \""+
    dirUtileria+"\"  "+ejecucion[sel]+" xmlConfig=\""+xmlConfig.toString().replace('"', '\'')+"\"";
  println("launchString = "+launchString);
  Process p = launch(launchString);
  try {
    pausarOsc = true;
    oscP5.stop();
    oscP5 = null;
    p.waitFor();
    println("exitValue = "+p.exitValue());
    println(getStringFromInputStream(p.getErrorStream()));
    pausarOsc = false;
  }
  catch(Exception e) {
    println("Exception = " + e);
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

public void abrirPuerto() {
  if (conectaTimer < millis() && oscP5 == null && !pausarOsc) {
    oscP5 = new OscP5(this, puerto);
    oscP5.plug(this, "devolverPing", "/Lanzadera/ping");
    oscP5.plug(this, "responderEstablecerIPs", "/Lanzadera/establecerIPs");
    oscP5.plug(this, "responderEstablecerEstados", "/Lanzadera/establecerEstados");
    oscP5.plug(this, "responderLanzar", "/Lanzadera/lanzar");
  }
}

public void devolverPing(String remitenteIp, int remitentePort, int id) {
  if (oscP5 != null) {
    if (id<ultimoPing.length)ultimoPing[id] = millis();
    OscMessage msj = new OscMessage("/Lanzadera/ping/resultado");
    msj.add(id);
    oscP5.send(msj, new NetAddress( remitenteIp, remitentePort));

    surface.setAlwaysOnTop(true);
    surface.setAlwaysOnTop(false);
  }
}

public void responderEstablecerIPs(String lienzoIp, int lienzoPort, 
  String observadorIp, int observadorPort, 
  String carreteIp, int carretePort) {
  if (config == null) config = new ConfiguracionCOD05();
  config.lienzo.ip = lienzoIp;
  config.lienzo.puerto = lienzoPort;
  config.observador.ip = observadorIp;
  config.observador.puerto = observadorPort;
  config.carrete.ip = carreteIp;
  config.carrete.puerto = carretePort;

  if (config.lienzo.estado != EstadoModulo.APAGADO) {
    if (oscP5.ip().equals(lienzoIp)) config.lienzo.estado = EstadoModulo.LOCAL;
    else config.lienzo.estado = EstadoModulo.REMOTO;
  }
  if (config.observador.estado != EstadoModulo.APAGADO) {
    if (oscP5.ip().equals(observadorIp)) config.observador.estado = EstadoModulo.LOCAL;
    else config.observador.estado = EstadoModulo.REMOTO;
  }
  if (config.carrete.estado != EstadoModulo.APAGADO) {
    if (oscP5.ip().equals(carreteIp)) config.carrete.estado = EstadoModulo.LOCAL;
    else config.carrete.estado = EstadoModulo.REMOTO;
  }

  println("config.lienzo.estado = "+config.lienzo.estado);
  println("config.observador.estado = "+config.observador.estado);
  println("config.carrete.estado = "+config.carrete.estado);
}
public void responderEstablecerEstados(int lienzo, int observador, int carrete) {
  if (config == null) config = new ConfiguracionCOD05();
  config.lienzo.estado = EstadoModuloList[lienzo];
  config.observador.estado = EstadoModuloList[observador];
  config.carrete.estado = EstadoModuloList[carrete];
  println("config.lienzo.estado = "+config.lienzo.estado);
  println("config.observador.estado = "+config.observador.estado);
  println("config.carrete.estado = "+config.carrete.estado);
}
public void responderLanzar(int modulo) {
  if (config == null) config = new ConfiguracionCOD05();
  if (config.lienzo.estado == EstadoModulo.LOCAL && modulo==0) {
    sel = 0;
    thread("ejecutarSel");
  }
  if (config.observador.estado == EstadoModulo.LOCAL && modulo==1) {
    sel = 1;
    thread("ejecutarSel");
  }
  if (config.carrete.estado == EstadoModulo.LOCAL && modulo==2) {
    sel = 2;
    thread("ejecutarSel");
  }
}

public void draw() {
  abrirPuerto();
  sel = -1;
  background(0);

  pushMatrix();
  boolean overCloner = mouseX > width-50 && mouseY < 50;
  if (overCloner) {
    fill(255, 0, 255);
    stroke(0);
  } else {
    stroke(255, 0, 255);
    fill(0);
  }
  translate(width-50, 0);
  rect(0, 0, 50, 50);
  ellipse(25, 25, 22, 22);
  popMatrix();

  noStroke();
  for (int i=0; i<ultimoPing.length; i++) {
    float diff = millis()-ultimoPing[i];
    if (diff < 1000) {
      fill(255, 0, 255, map(diff, 0, 1000, 255, 0));
      rect(i*25, 0, 25, 25);
    }
  }

  textSize(32);
  if (mouseX < width-50 && mouseY < 80) fill(255, 0, 255);
  else fill(255);
  text("Puerto = "+(pausarOsc?"Ocupado":puerto)+" "+
    (oscP5==null?
    conectandoString[conectandoStringRnd=(frameCount%20!=0?conectandoStringRnd:floor(random(conectandoString.length)))]:""), 100, 40);
  translate(40, 120);
  float x = mouseX-40;
  float tWid = 0;
  for (int i=0; i<opciones.length; i++) {
    tWid = textWidth(opciones[i]);
    if (mouseY > 80 && x > 0 && x < tWid) {
      sel = i;
      fill(255, 0, 255);
    } else {
      fill(255);
    }
    text(opciones[i], 0, 0);
    tWid += textWidth("   ");
    translate(tWid, 0);
    x -= tWid;
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
      if (estadoInt != -1) estado = new EstadoModulo[]{EstadoModulo.APAGADO, EstadoModulo.LOCAL, EstadoModulo.REMOTO}[estadoInt];
    }    
    public XML generar() {
      XML xml = new XML("ConfigModulo");
      xml.setString("id", id);
      xml.setString("ip", ip);
      xml.setInt("puerto", puerto);
      xml.setInt("estado", estado==EstadoModulo.APAGADO?0:estado==EstadoModulo.LOCAL?1:2);
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
  public void settings() {  size(600, 160); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "dummyLanzadera" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
