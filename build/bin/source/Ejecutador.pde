class Ejecutador {
  ConfiguracionCOD05 config;
  boolean modoUtileria;
  int nacimiento;

  Process lienzo, observador, carrete;

  String javaPath = System.getProperty("java.home");

  String dirReal = sketchPath(""), //sketchPath("..\\lib\\*"),
    lienzoReal = "Lienzo", 
    observadorReal = "cd Observador ^& Observador", 
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
    if (modoUtileria)templateLanzador = "\""+javaPath+"\\bin\\java\" -cp "+dirUtileria+" "+ejecutarKeyword ;
    else templateLanzador = "start cmd /k echo off ^& "+dirReal.substring(0, dirReal.indexOf(':')+1)+" ^& cd \"" + dirReal+"\" ^& "+ejecutarKeyword + " ^& echo \"iniciando "+ejecutarKeyword+"\"";
  }

  boolean enEjecucion() {
    if (lienzo == null && observador == null && carrete == null) return false;
    else {
      if (lienzo != null) {
        if (!lienzo.isAlive()) lienzo = null;
      }
      if (observador != null) {
        if (!observador.isAlive()) observador = null;
      }
      if (carrete != null) {
        if (!carrete.isAlive()) carrete = null;
      }
      return true;
    }
  }

void ejecutarLocales() {
  if (config.lienzo.estado == EstadoModulo.LOCAL) ejecutarLienzo();
  if (config.observador.estado == EstadoModulo.LOCAL) ejecutarObservador();
  if (config.carrete.estado == EstadoModulo.LOCAL) ejecutarCarrete();
}
  /*void ejecutarLocales() {
    //consolaDebug.printlnAlerta( "" + templateLanzador.replace(ejecutarKeyword, observadorReal) );
    if (modoUtileria) {
      if (config.lienzo.estado == EstadoModulo.LOCAL) {
        lienzo=launch( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      }
      if (config.observador.estado == EstadoModulo.LOCAL) {
        observador=launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      }
      if (config.carrete.estado == EstadoModulo.LOCAL) {
        carrete=launch( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
      }
    } else {
      if (config.lienzo.estado == EstadoModulo.LOCAL) {
        lienzo=launch( templateLanzador.replace(ejecutarKeyword, lienzoReal));
      }
      if (config.observador.estado == EstadoModulo.LOCAL) {
        observador = launch( templateLanzador.replace(ejecutarKeyword, observadorReal));
      }
      if (config.carrete.estado == EstadoModulo.LOCAL) {
        carrete=launch( templateLanzador.replace(ejecutarKeyword, carreteReal));
      }
    }
  }*/

  void ejecutarLienzo() {
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
  void ejecutarObservador() {
    consolaDebug.printlnAlerta( "Vamos que intento correr el observadorr!!" );
    if (modoUtileria) {
      observador=launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      /*PAppConsola cons = new PAppConsola();
       PApplet.runSketch(new String[]{"PAppConsola"},cons);
       cons.pasarStream(observador);*/
    } else {
      observador=launch( templateLanzador.replace(ejecutarKeyword, observadorReal));
      /*PAppConsola cons = new PAppConsola();
       PApplet.runSketch(new String[]{"PAppConsola"},cons);
       cons.pasarStream(observador);*/
    }
  }
  void ejecutarCarrete() {
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