import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class dummyLienzo extends PApplet {

String archivoConfigXML = "../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

int puertoLie = 12000, puertoObs = 12010, puertoCarr = 12020;
String ipLie = "127.0.0.1", ipObs = "127.0.0.1", ipCarr = "127.0.0.1";

ConfiguracionCOD05 config;

public void setup(){
  
  
  XML xml = null;
  try {xml = loadXML(archivoConfigXML);}
  catch (Exception e){}
  if (xml != null) {
    xml = xml.getChild(xmlTagEjecucion);
    if (xml != null){
      config = new ConfiguracionCOD05();
      config.cargar(xml);
      
      puertoLie = config.lienzo.puerto;
      puertoObs = config.observador.puerto;
      puertoCarr = config.carrete.puerto;
      ipLie = config.lienzo.ip;
      ipObs = config.observador.ip;
      ipCarr = config.carrete.ip;
    }
  }
}

public void draw(){
  background(0);
  textSize(48);
  translate(40,60);
  text("Lienzo",0,0);
  translate(textWidth(" "), 0);
  pushMatrix();
  translate(textWidth("Lienzo"), 0);
  textSize(24);
  text("puerto = "+puertoLie,0,0);
  popMatrix();
  translate(0,28);
  text("observador = "+ipObs+":"+puertoObs,0,0);
  translate(0,28);
  text("carrete = "+ipCarr+":"+puertoCarr,0,0);
}
//v 14/06/2017
enum EstadoModulo {
  APAGADO, LOCAL, REMOTO
}

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
  public void settings() {  size(600,160); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "dummyLienzo" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
