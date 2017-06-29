class Ejecutador {
  ConfiguracionCOD05 config;
  boolean modoUtileria;
  int nacimiento;
  
  Process lienzo,observador,carrete;

  String javaPath = System.getProperty("java.home");

  String dirReal = sketchPath(""), //sketchPath("..\\lib\\*"),
    lienzoReal = "./Lienzo", 
    observadorReal = "./Observador", 
    carreteReal = "./Carrete";

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
  
  boolean enEjecucion() {
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
  
  void ejecutarLocales() {
    if (modoUtileria) {
      if (config.lienzo.estado == EstadoModulo.LOCAL) {
        lienzo=exec( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      /*  PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);*/
      }
      if (config.observador.estado == EstadoModulo.LOCAL) {
        observador=exec( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
       /* PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador);*/
      }
      if (config.carrete.estado == EstadoModulo.LOCAL) {
        carrete=exec( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
       /* PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);*/
      }
    } else {
      if (config.lienzo.estado == EstadoModulo.LOCAL) {
        lienzo=exec( templateLanzador.replace(ejecutarKeyword, lienzoReal));
    /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);  */
    }
      if (config.observador.estado == EstadoModulo.LOCAL) {
        observador=exec( templateLanzador.replace(ejecutarKeyword, observadorReal));
    /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador); */ 
    }
      if (config.carrete.estado == EstadoModulo.LOCAL) {
        carrete=exec( templateLanzador.replace(ejecutarKeyword, carreteReal));
    /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);  */
    }
    }
  }

  void ejecutarLienzo() {
    if (modoUtileria) {
      lienzo=exec( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);*/
    } else {
      lienzo=exec( templateLanzador.replace(ejecutarKeyword, lienzoReal));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(lienzo);*/
    }
  }
  void ejecutarObservador() {
    if (modoUtileria) {
      observador=exec( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador);*/
    } else {
      observador=exec( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(observador);*/
    }
  }
  void ejecutarCarrete() {
    if (modoUtileria) {
      carrete=exec( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);*/
    } else {
      carrete=exec( templateLanzador.replace(ejecutarKeyword, carreteReal));
      /*PAppConsola cons = new PAppConsola();
      PApplet.runSketch(new String[]{"PAppConsola"},cons);
      cons.pasarStream(carrete);*/
    }
  }
}