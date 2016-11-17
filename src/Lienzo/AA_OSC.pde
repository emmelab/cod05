int cantidadOpciones = 5;

String[] opciones = new String[cantidadOpciones];

void initOSC() {

  cargarOpciones();

  noSmooth();
  noStroke();

  oscP5 = new OscP5(this, 12000);
  consola = new NetAddress("192.168.0.102", 13000);

  oscP5.plug(this, "opciones", "/pedir/opciones");
   oscP5.plug(this, "accionOpciones", "/accion/opciones");
  
  oscP5.plug(this, "modificadoresTotal", "/pedir/modificadores/total");
  oscP5.plug(this, "modificadoresExistentes", "/pedir/modificadores/existentes");
  oscP5.plug(this, "modificadoresAgregar", "/agregar/modificadores");
  oscP5.plug(this, "modificadoresQuitar", "/quitar/modificadores");
  
  oscP5.plug(this, "recibirUnEstimulo", "/enviar/estimulos");
  oscP5.plug(this, "enviarEstimulos", "/pedir/estimulos");
  
 
}

void accionOpciones(String cual) {
  if (cual.equals("pausa")) pausa = !pausa;
  if (cual.equals("ciclo")) ciclo();
  if (cual.equals("reset")) sistema.reset();
  if (cual.equals("fondoAlfa")) fondoAlfa = !fondoAlfa;
  if (cual.equals("pausa")) sistema.debug = !sistema.debug;
}

void cargarOpciones() {
  opciones[0] = "pausa";
  opciones[1] = "ciclo";
  opciones[2] = "reset";
  opciones[3] = "fondoAlfa";
  opciones[4] = "sistema.debug";
}

void opciones() {
  mensaje_CANTIDAD("/opciones", cantidadOpciones);
  for (int i = 0; i< cantidadOpciones; i++) {
    mensaje_NOMBRE_ESTADO("/opciones", opciones[i], 0);
  }
  mensaje("/opciones/listo") ;
}

void modificadoresTotal() {
   // println("paso por aqui");
  mensaje_CANTIDAD("/modificadores/totales", sistema.registroModificadores.size());
 // println(sistema.registroModificadores.size());
  //for (int i = 0; i< sistema.registroModificadores.size(); i++) {
  for (String n : sistema.registroModificadores.keySet()) {
    String categoria = sistema.registroModificadores.get(n).categoria();
    //println(categoria);
    mensaje_NOMBRE_CATEGORIA_ESTADO("/modificadores/totales",n, categoria, 0);
    
  }
  mensaje("/modificadores/totales/listo");
}

void modificadoresExistentes() {
  String[] lista = sistema.getDirectorioModificadores();
  mensaje_CANTIDAD("/modificadores/existentes", lista.length);
  //int contador = 0;
  for (int i = 0; i< sistema.getCantidadModificadores (); i++) {
    //String categoria = sistema.registroModificadores.get(lista[i]).categoria();
   //contador++;
   //println()
    mensaje_NOMBRE_ESTADO("/modificadores/existentes", lista[i], sistema.getEstado(lista[i])? 1 : 0);
  }
  mensaje("/modificadores/existentes/listo");
}

void modificadoresAgregar(String cual) {
  if (sistema.agregar(cual)!=null){
    OscMessage msj = new OscMessage("/agregarMod");
    msj.add(cual);
    oscP5.send(msj, consola);
  }
}
void modificadoresQuitar(String cual) {
  sistema.eliminar(cual);
    OscMessage msj = new OscMessage("/quitarMod|");
    msj.add(cual);
    oscP5.send(msj, consola);
  //modificadoresExistentes();
}


void mensaje(String mensaje) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  oscP5.send(mensajeModificadores, consola);
}

void mensaje_CANTIDAD(String mensaje, int cantidad) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(cantidad);
  oscP5.send(mensajeModificadores, consola);
}

void mensaje_NOMBRE_ESTADO(String mensaje, String nombre, int estado) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(nombre);
  mensajeModificadores.add(estado); // 0 para false   --- 1 para true // para evitar usar otra funcion agrego un dato de estado de las opciones luego peude serviar para revisar el estado dle alfa y al pausa por ejemplo
  oscP5.send(mensajeModificadores, consola);
}

void mensaje_NOMBRE_CATEGORIA_ESTADO(String mensaje, String nombre, String categoria, int estado) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(nombre);
  mensajeModificadores.add(categoria);
  mensajeModificadores.add(estado); // 0 para false   --- 1 para true // para evitar usar otra funcion agrego un dato de estado de las opciones luego peude serviar para revisar el estado dle alfa y al pausa por ejemplo
  oscP5.send(mensajeModificadores, consola);
}


void mensaje_POSICION_ESTADO(String mensaje, int posicion, int estado) {
  OscMessage mensajeModificadores ;
  mensajeModificadores = new OscMessage(mensaje);
  mensajeModificadores.add(posicion);
  mensajeModificadores.add(estado); // 0 para false   --- 1 para true // para evitar usar otra funcion agrego un dato de estado de las opciones luego peude serviar para revisar el estado dle alfa y al pausa por ejemplo
  oscP5.send(mensajeModificadores, consola);
}

