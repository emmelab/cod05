import oscP5.*;
import netP5.*;
import java.io.InputStreamReader;

ConfiguracionCOD05 config;
OscP5 oscP5;
int puerto = 14000;
int sel = -1;
float conectaTimer = 2000;
boolean pausarOsc = false;

String archivoConfigXML = "../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

int[] ultimoPing = {0,0,0};

int conectandoStringRnd = 0;
String[] conectandoString = {".",":","¨"," .","..",":.",".:"," :","::","¨.",".¨",":¨"};

String dirUtileria = "..\\..\\lib\\*";
String javaPath = "";
String[] opciones = {"Lienzo", "Observador", "Carrete"};
String[] ejecucion = {"dummyLienzo", "dummyObservador", "dummyCarrete"};

void setup() {
  size(600, 160);
  dirUtileria = sketchPath(dirUtileria);
  javaPath = System.getProperty("java.home");
  println(dirUtileria+"\n"+javaPath);
  
  if (config == null) config = new ConfiguracionCOD05();
    XML xmlConfig = null;
    if(new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
    if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagPanel);
    
    config.cargar(xmlConfig);
    if(xmlConfig==null){
      config.lienzo.puerto = floor(random(12015,13000));
      config.observador.puerto = floor(random(12015,13000));
      config.carrete.puerto = floor(random(12015,13000));
    }
}

void mousePressed() {
  if (mouseX > width-50 && mouseY < 50) {
  java.awt.Point loc = ((java.awt.Canvas)surface.getNative()).getLocationOnScreen();
    String[] args = {ARGS_LOCATION+"="+loc.x+","+(loc.y+height+30),"dummyLanzadera"};
  PApplet.runSketch(args, new dummyLanzadera());
  }
  else if (mouseY < 80 && !pausarOsc) {
    if(oscP5 != null){
    oscP5.stop();
    oscP5 = null;
    }
    puerto ++;
    if (puerto > 14010) puerto = 14000;
    conectaTimer = millis()+2000;
  }
  else if (sel != -1 && !pausarOsc) {
    thread("ejecutarSel");
  }
}

void ejecutarSel() {
  XML xmlConfig = new XML("xml");
  xmlConfig.addChild(config.guardar("ejecucion"));
  String launchString = "\""+javaPath+"\\bin\\java\" -cp "+
    dirUtileria+"  "+ejecucion[sel]+" xmlConfig=\""+xmlConfig.toString().replace('"','\'')+"\"";
    println(launchString);
  Process p = exec(launchString);
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
    println(e);
  }
}

String getStringFromInputStream(InputStream is) {

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

void abrirPuerto(){
  if (conectaTimer < millis() && oscP5 == null && !pausarOsc){
    oscP5 = new OscP5(this,puerto);
    oscP5.plug(this,"devolverPing","/Lanzadera/ping");
  }
}

void devolverPing(String remitenteIp, int remitentePort, int id){
  if (oscP5 != null){
    if(id<ultimoPing.length)ultimoPing[id] = millis();
    OscMessage msj = new OscMessage("/Lanzadera/ping/resultado");
    msj.add(id);
    oscP5.send(msj,new NetAddress( remitenteIp,remitentePort));
    
    surface.setAlwaysOnTop(true);
    surface.setAlwaysOnTop(false);
  }
}

void draw() {
  abrirPuerto();
  sel = -1;
  background(0);
  
  pushMatrix();
  boolean overCloner = mouseX > width-50 && mouseY < 50;
  if(overCloner){
  fill(255,0,255);
  stroke(0);
  }
  else{
  stroke(255,0,255);
  fill(0);
  }
  translate(width-50,0);
  rect(0,0,50,50);
  ellipse(25,25,22,22);
  popMatrix();
  
  noStroke();
  for(int i=0; i<ultimoPing.length;i++){
    float diff = millis()-ultimoPing[i];
  if(diff < 1000){
    fill(255,0,255,map(diff,0,1000,255,0));
    rect(i*25,0,25,25);
  }
  }
  
  textSize(32);
  if (mouseX < width-50 && mouseY < 80) fill(255,0,255);
  else fill(255);
  text("Puerto = "+(pausarOsc?"Ocupado":puerto)+" "+
  (oscP5==null?
  conectandoString[conectandoStringRnd=(frameCount%20!=0?conectandoStringRnd:floor(random(conectandoString.length)))]:""),100,40);
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