class Ejecutador {
  ConfiguracionCOD05 config;
  boolean modoUtileria;
  int nacimiento;

  String javaPath = System.getProperty("java.home");

  String dirReal = sketchPath("..\\"), //sketchPath("..\\lib\\*"),
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
    templateLanzador = "\""+javaPath+"\\bin\\java\" -cp "+dirUtileria+" "+ejecutarKeyword ;
  }
  
  void ejecutarLocales() {
    if (modoUtileria) {
      if (config.lienzo.estado == EstadoModulo.LOCAL) launch( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      if (config.observador.estado == EstadoModulo.LOCAL) launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      if (config.carrete.estado == EstadoModulo.LOCAL) launch( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
    } else {
      if (config.lienzo.estado == EstadoModulo.LOCAL) launch( templateLanzador.replace(ejecutarKeyword, lienzoReal));
      if (config.observador.estado == EstadoModulo.LOCAL) launch( templateLanzador.replace(ejecutarKeyword, observadorReal));
      if (config.carrete.estado == EstadoModulo.LOCAL) launch( templateLanzador.replace(ejecutarKeyword, carreteReal));
    }
  }

  void ejecutarLienzo() {
    if (modoUtileria) {
      launch( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
    } else {
      launch( templateLanzador.replace(ejecutarKeyword, lienzoReal));
    }
  }
  void ejecutarObservador() {
    if (modoUtileria) {
      launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
    } else {
      launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
    }
  }
  void ejecutarCarrete() {
    if (modoUtileria) {
      launch( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
    } else {
      launch( templateLanzador.replace(ejecutarKeyword, carreteReal));
    }
  }
}