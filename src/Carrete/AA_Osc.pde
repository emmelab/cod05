
void initOSC() {
  oscP5 = new OscP5(this, 13000);
  sistema = new NetAddress("127.0.0.1", 12000);

  //-----------------------------------------OSC sistema de particulas

  oscP5.plug(this, "cantidadBotones", "/modificadores/totales");
  oscP5.plug(this, "cantidadBotonesExistentes", "/modificadores/existentes");
  oscP5.plug(this, "cantidadBotones", "/opciones");
  oscP5.plug(this, "cantidadBotones", "/estimulos/totales");
  oscP5.plug(this, "modificadores", "/modificadores/totales");
  oscP5.plug(this, "modificadoresExistentes", "/modificadores/existentes");
  oscP5.plug(this, "estimulos", "/estimulos/totales");
  oscP5.plug(this, "opciones", "/opciones");
  oscP5.plug(this, "opcionesListo", "/opciones/listo");
  oscP5.plug(this, "existentesListo", "/modificadores/existentes/listo");
  oscP5.plug(this, "totalesListo", "/modificadores/totales/listo");
  oscP5.plug(this, "estimulosListo", "/estimulos/listo");
 
   oscP5.plug(this, "agregarMod", "/agregarMod");
  oscP5.plug(this, "quitarMod", "/quitarMod");
  
  //----------------------------------------OSC captura de movimiento



  oscP5.plug(this, "menuQuitarModificador", "/MenuQuitarModificador");
  oscP5.plug(this, "menuAgregarModificador", "/MenuAgregarModificador");
  oscP5.plug(this, "menuNavegarIzquierda", "/MenuNavegarIzquierda");
  oscP5.plug(this, "menuNavegarDerecha", "/MenuNavegarDerecha");

  oscP5.plug(this, "cancelar", "/Cancelar");
  oscP5.plug(this, "aceptar", "/Aceptar");

  oscP5.plug(this, "actualizarMovimiento", "/actualizarMovimiento");
  oscP5.plug(this, "cerrado", "/cerrado");
  oscP5.plug(this, "desequilibrio", "/desequilibrio");
  oscP5.plug(this, "nivel", "/nivel");

  oscP5.plug(this, "cursorMoCap", "/cursor");
}

//---------------------------------------------IP MANAGER----EVENTOS-----------------------

public void Conectar() { 

  String unaIp = cp5.get(Textfield.class, "IP").getText();
  String unPuertoEnvio = cp5.get(Textfield.class, "Puerto Envio").getText();
  String unPuertoRecivo = cp5.get(Textfield.class, "Puerto Recivo").getText();

  if (!unaIp.equals(""))ip = cp5.get(Textfield.class, "IP").getText();
  if (!unPuertoEnvio.equals(""))puertoEnvio = cp5.get(Textfield.class, "Puerto Envio").getText();
  if (!unPuertoRecivo.equals(""))puertoRecivo = cp5.get(Textfield.class, "Puerto Recivo").getText();

  conectarOSC();

  cp5.get(Textfield.class, "IP").clear();
  cp5.get(Textfield.class, "Puerto Envio").clear();
  cp5.get(Textfield.class, "Puerto Recivo").clear();
}

void conectarOSC() {
  int pe = int(puertoEnvio);
  int pr = int(puertoRecivo);
  oscP5 = new OscP5(this, pr); 
  sistema = new NetAddress(ip, pe);

  //-----------------------------------------OSC plugs

  //-----------------------------------------OSC sistema de particulas

  oscP5.plug(this, "cantidadBotones", "/modificadores/totales");
  oscP5.plug(this, "cantidadBotonesExistentes", "/modificadores/existentes");
  oscP5.plug(this, "cantidadBotones", "/opciones");
  oscP5.plug(this, "modificadores", "/modificadores/totales");
  oscP5.plug(this, "modificadoresExistentes", "/modificadores/existentes");
  oscP5.plug(this, "estimulos", "/estimulos/totales");
  oscP5.plug(this, "opciones", "/opciones");
  oscP5.plug(this, "opcionesListo", "/opciones/listo");
  oscP5.plug(this, "existentesListo", "/modificadores/existentes/listo");
  oscP5.plug(this, "totalesListo", "/modificadores/totales/listo");
  oscP5.plug(this, "estimulosListo", "/estimulos/listo");
  
   oscP5.plug(this, "agregarMod", "/agregarMod");
  oscP5.plug(this, "quitarMod", "/quitarMod");


  //----------------------------------------OSC captura de movimiento



  oscP5.plug(this, "menuQuitarModificador", "/MenuQuitarModificador");
  oscP5.plug(this, "menuAgregarModificador", "/MenuAgregarModificador");
  oscP5.plug(this, "menuNavegarIzquierda", "/MenuNavegarIzquierda");
  oscP5.plug(this, "menuNavegarDerecha", "/MenuNavegarDerecha");

  oscP5.plug(this, "cancelar", "/Cancelar");
  oscP5.plug(this, "aceptar", "/Aceptar");

  oscP5.plug(this, "actualizarMovimiento", "/actualizarMovimiento");
  oscP5.plug(this, "cerrado", "/cerrado");
  oscP5.plug(this, "desequilibrio", "/desequilibrio");
  oscP5.plug(this, "nivel", "/nivel");


  println("IP: " + ip  +" || "+ "Puerto Envio: " + pe +" || "+ "Puerto Recivo: " + pr );
}




/*

 // incoming osc message are forwarded to the oscEvent method. 
 void oscEvent(OscMessage theOscMessage) {
 // print the address pattern and the typetag of the received OscMessage 
 print("### received an osc message.");
 print(" addrpattern: "+theOscMessage.addrPattern());
 println(" typetag: "+theOscMessage.typetag());
 }
 */
