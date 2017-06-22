class Ejecutador {
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

  Ejecutador(boolean modoUtileria, boolean lienzo, boolean observador, boolean carrete) {
    if (modoUtileria) {
      String templateLanzador = "\""+javaPath+"\\bin\\java\" -cp "+dirUtileria+" "+ejecutarKeyword ;
      if (lienzo) launch( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      if (observador) launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      if (carrete) launch( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
    } else {
      String templateLanzador = dirReal+ejecutarKeyword ;
      if (lienzo) launch( templateLanzador.replace(ejecutarKeyword, lienzoUtileria));
      if (observador) launch( templateLanzador.replace(ejecutarKeyword, observadorUtileria));
      if (carrete) launch( templateLanzador.replace(ejecutarKeyword, carreteUtileria));
    }
  }
}