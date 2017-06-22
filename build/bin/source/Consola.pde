class Consola {

  Modos modos;
  ColeccionCategorias cC;
  ColeccionEstimulos cE;
  ColeccionOpciones cO;
  Monitor monitor;

  String modoActual = "cualquiercosa";
  float centroX = width/2;
  float centroY = height/1.7;
  float tam = width>height?height/4.5:width/4.5;

  String[] datosDeSistema_nombresCategorias;
  String[] datosDeSistema_nombresModificadores;
  String[] datosDeSistema_nombresModificadoresExistentes;
  String[] datosDeSistema_nombresOpciones;
  String[] datosDeSistema_nombresEstimulos;

  int cerrado=0;
  int nivel=0;
  int eje=0;

  int limiteSelector;
  int selector;

  boolean dosNivelesDeSeleccion;

  PVector cursor = new PVector();



  Consola() {
    modos = new Modos();
    modos.inicializar(new PVector(centroX, centroY), tam);
    cC = new ColeccionCategorias();
    cO = new ColeccionOpciones();
    cE = new ColeccionEstimulos();
    cC.inicializar(datosDeSistema_nombresCategorias, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);
    cO.inicializar(datosDeSistema_nombresOpciones);    
    cE.inicializar(datosDeSistema_nombresEstimulos);    
    monitor = new Monitor();
  }

  Consola(color[][] paleta_) { 

    modos = new Modos(paleta_, new PVector(centroX, centroY), tam);
    cC = new ColeccionCategorias(paleta_, new PVector(centroX, centroY), tam);
    cO = new ColeccionOpciones(paleta_, new PVector(centroX, centroY), tam);
    cE = new ColeccionEstimulos(paleta_, new PVector(centroX, centroY), tam);
    cC.inicializar(datosDeSistema_nombresCategorias, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);
    cO.inicializar(datosDeSistema_nombresOpciones);    
    cE.inicializar(datosDeSistema_nombresEstimulos);    
    monitor = new Monitor(paleta_, new PVector(centroX, centroY), tam);
  }


  void ejecutar() {

 base();
    if (modos.getModo().equals(ESPERA)) {
      //mandarMensaje("/holi...ten un buen dia");
      monitor.dibujar(cerrado, nivel, eje);
    } else if (modos.getModo().equals(AGREGAR)) {        
     
      monitor.dibujar(cerrado, nivel, eje);
     cC.dibujarCategoria();
    } else if (modos.getModo().equals(ELIMINAR)) {       
      
      monitor.dibujar(cerrado, nivel, eje);
      cC.dibujarCategoria();
    } else if (modos.getModo().equals(ESTIMULOS)) {      

      int contadorSeleccionEstimulo = cC.getContadorSeleccionEstimulo(cursor);//limites 0,400
      monitor.dibujar(cerrado, nivel, eje, contadorSeleccionEstimulo);       
      fill(255);
      ellipse(cursor.x, cursor.y, 10, 10);      
      if (cE.getSeleccionarEstimulo()) {
        botonesAccionesN2();
        mandarMensaje("/pedir/estimulos/totales");
      }
      cE.dibujar();
    } else if (modos.getModo().equals(OPCIONES)) {
      monitor.dibujar(cerrado, nivel, eje);
      cO.dibujar();
    }
    modos.ejecutar(modoActual);
  }

  void setCursor(float x, float y) {
    cursor.set(x, y);
  }

  void actualizarIconos(int cerrado_, int nivel_, int eje_) {
    cerrado = cerrado_;
    nivel = nivel_;
    eje = eje_;
  }

  void activarAnadir() {
    modos.setModo(AGREGAR);
    limitarSelector(); 
    //botonesAccionesN1();
  }
  void activarQuitar() {
    modos.setModo(ELIMINAR); 
    limitarSelector();
    // botonesAccionesN1();
  }

  void activarOpciones() {
    modos.setModo(OPCIONES); 
    limitarSelector();
    // botonesAccionesN1();
  }
  void activarEstimulos() {
    modos.setModo(ESTIMULOS); 
    limitarSelector();
    //  botonesAccionesN1();
  }
  void activarEspera() {
    modos.setModo(ESPERA); 
    limitarSelector();
    // botonesAccionesN1();
  }

  void aumentoSelector() {
    if (!modos.getModo().equals(ESPERA)) {     
      selector++;
      revisarSensible();
    }
  }

  void decrementoSelector() {
    if (!modos.getModo().equals(ESPERA)) {     
      selector--;
      revisarSensible();
    }
  }

  void revisarSensible() {    
    selector = (selector+limiteSelector)%limiteSelector;
    selector = int(constrain(selector, 0, limiteSelector));
    if (modos.getModo().equals(AGREGAR)) {     
      cC.setSensible(selector);
    } else if (modos.getModo().equals(ELIMINAR)) {       
      cC.setSensible(selector);
    } else if (modos.getModo().equals(ESTIMULOS)) {
    } else if (modos.getModo().equals(OPCIONES)) {
      cC.setSensible(selector);
    }
  }

  void botonesAccionesN2() {
    if (modos.getModo().equals(AGREGAR)) {
      String nombreModSeleccionado = cC.getSensible(selector);
      OscMessage mensajeModificadores;
      mensajeModificadores = new OscMessage("/agregar/modificadores");
      mensajeModificadores.add(nombreModSeleccionado); 
      oscP5.send(mensajeModificadores, sistema);
    } else if (modos.getModo().equals(ELIMINAR)) {
      String nombreModSeleccionado = cC.getSensible(selector);
      int cantModSeleccionado = cC.getModSensible(selector).getCant();
      OscMessage mensajeModificadores;
      mensajeModificadores = new OscMessage("/quitar/modificadores");
      mensajeModificadores.add(nombreModSeleccionado+"_"+(cantModSeleccionado-1));           
      oscP5.send(mensajeModificadores, sistema);
    } else if (modos.getModo().equals(ESTIMULOS)) {
      OscMessage mensajeOpciones;
      mensajeOpciones = new OscMessage("/seleccionar/estimulo");
      // mensajeOpciones.add(nombreModSeleccionado);           
      oscP5.send(mensajeOpciones, sistema);
    } else if (modos.getModo().equals(OPCIONES)) {
      String nombreModSeleccionado = cO.getSensible(selector);
      OscMessage mensajeOpciones;
      mensajeOpciones = new OscMessage("/accion/opciones");
      mensajeOpciones.add(nombreModSeleccionado);           
      oscP5.send(mensajeOpciones, sistema);
    }

    //activarEspera();
  }


  void mandarMensaje(String mensaje) {   
    OscMessage mensajeModificadores;
    mensajeModificadores = new OscMessage(mensaje);
    oscP5.send(mensajeModificadores, sistema);
  }

  void limitarSelector() {
    if (modos.getModo().equals(ESPERA)) {
      limiteSelector = 0; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(AGREGAR)) {
      limiteSelector = datosDeSistema_nombresModificadores.length; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(ELIMINAR)) {
      limiteSelector = datosDeSistema_nombresModificadores.length; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(ESTIMULOS)) {
      limiteSelector = datosDeSistema_nombresEstimulos.length; //cambiar con no se nada supongo
    } else if (modos.getModo().equals(OPCIONES)) {
      limiteSelector = datosDeSistema_nombresOpciones.length; //cambiar con no se nada supongo
    }
  }


  void renovarDatosCategorias( ArrayList<String>nombres_, ArrayList<String>nombresCategorias_, ArrayList<String>nombresExistentes_) {

    dosNivelesDeSeleccion = true;
    int c = nombres_.size();
    int ce = nombresExistentes_.size();

    datosDeSistema_nombresCategorias = new String[c];
    datosDeSistema_nombresModificadores  = new String[c];
    datosDeSistema_nombresModificadoresExistentes  = new String[ce];

    for (int i=0; i<c; i++) {
      datosDeSistema_nombresModificadores[i] = (String)nombres_.get(i);
      datosDeSistema_nombresCategorias[i] = (String)nombresCategorias_.get(i);
    }
    for (int i=0; i<ce; i++) {
      String[] n = split(nombresExistentes_.get(i), "_");
      datosDeSistema_nombresModificadoresExistentes[i] = n[0];
    }

    cC.inicializar(datosDeSistema_nombresCategorias, datosDeSistema_nombresModificadores, 
    datosDeSistema_nombresModificadoresExistentes);

    limitarSelector();
  }

  void renovarDatosEstimulos( ArrayList<String>nombres_) {

    int c = nombres_.size();
    datosDeSistema_nombresEstimulos  = new String[c];
    for (int i=0; i<c; i++) {
      datosDeSistema_nombresEstimulos[i] = (String)nombres_.get(i);
    }

    cE.inicializar(datosDeSistema_nombresEstimulos);

    limitarSelector();
  }

  void renovarDatosOpciones( ArrayList<String>nombres_) {

    int c = nombres_.size();
    datosDeSistema_nombresOpciones  = new String[c];
    for (int i=0; i<c; i++) {
      datosDeSistema_nombresOpciones[i] = (String)nombres_.get(i);
    }

    cO.inicializar(datosDeSistema_nombresOpciones);

    limitarSelector();
  }

  void agregarMod(String cual) {
    cC.agregar(cual);
  }
  void quitarMod(String cual) {
     
    cC.quitar(cual);
  }
  
  void base() {
    noStroke();
    fill(paleta[1][0]);
    rect(0, 0, width, height);
    float tam_ = tam*350/100;
    fill(paleta[1][1]);
    ellipse(centroX, centroY, tam_, tam_);
   
  }
}