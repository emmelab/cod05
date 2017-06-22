import oscP5.*;
import netP5.*;
import java.io.InputStreamReader;

OscP5 oscP5;
int puerto = 14000;
int sel = -1;
float conectaTimer = 2000;

int[] ultimoPing = {0,0,0};

int conectandoStringRnd = 0;
String[] conectandoString = {".",":","¨"," .","..",":.",".:"," :","::","¨.",".¨",":¨"};

String dirUtileria = "..\\modulosDeUtileria\\lib\\*";
String javaPath = "";
String[] opciones = {"Lienzo", "Observador", "Carrete"};
String[] ejecucion = {"dummyLienzo", "dummyObservador", "dummyCarrete"};

void setup() {
  size(600, 160);
  dirUtileria = sketchPath(dirUtileria);
  javaPath = System.getProperty("java.home");
  println(dirUtileria+"\n"+javaPath);
}

void mousePressed() {
  if (mouseY < 80) {
    if(oscP5 != null){
    oscP5.stop();
    oscP5 = null;
    }
    puerto ++;
    if (puerto > 14010) puerto = 14000;
    conectaTimer = millis()+2000;
  }
  else if (sel != -1) {
    thread("ejecutarSel");
  }
}

void ejecutarSel() {  
  File f = new File(sketchPath(ejecucion[sel]));
  Process p = launch("\""+javaPath+"\\bin\\java\" -cp "+
    dirUtileria+"  "+ejecucion[sel]);
  try {
    p.waitFor();
    println("exitValue = "+p.exitValue());
    println(getStringFromInputStream(p.getErrorStream()));
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
  if (conectaTimer < millis() && oscP5 == null){
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
  }
}

void draw() {
  abrirPuerto();
  sel = -1;
  background(0);
  noStroke();
  for(int i=0; i<ultimoPing.length;i++){
    float diff = millis()-ultimoPing[i];
  if(diff < 1000){
    fill(255,0,255,map(diff,0,1000,255,0));
    rect(i*25,0,25,25);
  }
  }
  
  textSize(32);
  if (mouseY < 80) fill(255,0,255);
  else fill(255);
  text("Puerto = "+puerto+" "+
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