String archivoConfigXML = "../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

int puertoLie = 12000, puertoObs = 12010, puertoCarr = 12020;
String ipLie = "127.0.0.1", ipObs = "127.0.0.1", ipCarr = "127.0.0.1";

ConfiguracionCOD05 config;

void setup(){
  size(600,160);
  
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

void draw(){
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