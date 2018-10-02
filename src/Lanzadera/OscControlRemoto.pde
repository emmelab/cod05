ControlOsc controlOsc = new ControlOsc();

class ControlOsc implements AutoSetup {
  OscP5 osc;
  ConfiguracionCOD05 configRemota;
  Ejecutador ejecutador;//6 seg de vida

  int ultimoPingLienzo, ultimoPingObservador, ultimoPingCarrete;
  //String 

  String resultado = "/resultado";

  String lanzar = "/Lanzadera/lanzar";
  String responderLanzar = "responderLanzar";

  String establecerIPs = "/Lanzadera/establecerIPs";
  String responderEstablecerIPs = "responderEstablecerIPs";
  String callbackEstablecerIPs = "callbackEstablecerIPs";
  String pedirIPs = "/Lanzadera/pedirIPs";
  String callbackPedirIPs = "callbackPedirIPs";
  String establecerEstados = "/Lanzadera/establecerEstados";
  String responderEstablecerEstados = "responderEstablecerEstados";
  String callbackEstablecerEstados = "callbackEstablecerEstados";
  String pedirEstados = "/Lanzadera/pedirEstados";
  String callbackPedirEstados = "callbackPedirEstados";
  String ping = "/Lanzadera/ping";
  String responderPing = "responderPing";
  String callbackPing = "callbackPing";

  OscMessage msjPingLienzo, msjPingObservador, msjPingCarrete;

  ControlOsc() {
    autoSetup.add(this);
  }

  void setup() {
    osc = oscP5;
    osc.plug(this, responderLanzar, lanzar);

    osc.plug(this, responderEstablecerIPs, establecerIPs);
    osc.plug(this, callbackEstablecerIPs, establecerIPs+resultado);
    osc.plug(this, callbackPedirIPs, pedirIPs+resultado);

    osc.plug(this, responderEstablecerEstados, establecerEstados);
    osc.plug(this, callbackEstablecerEstados, establecerEstados+resultado);
    osc.plug(this, callbackPedirEstados, pedirEstados+resultado);

    osc.plug(this, callbackPing, ping+resultado);
    osc.plug(this, responderPing, ping);

    msjPingLienzo = new OscMessage("/Lanzadera/ping").add(osc.ip()).add(oscP5Port).add(0);
    msjPingObservador = new OscMessage("/Lanzadera/ping").add(osc.ip()).add(oscP5Port).add(1);
    msjPingCarrete = new OscMessage("/Lanzadera/ping").add(osc.ip()).add(oscP5Port).add(2);
  }

  void responderEstablecerIPs(String lienzoIp, int lienzoPort, 
    String observadorIp, int observadorPort, 
    String carreteIp, int carretePort) {
    if (configRemota == null) configRemota = new ConfiguracionCOD05();
    configRemota.lienzo.ip = lienzoIp;
    configRemota.lienzo.puerto = lienzoPort;
    configRemota.observador.ip = observadorIp;
    configRemota.observador.puerto = observadorPort;
    configRemota.carrete.ip = carreteIp;
    configRemota.carrete.puerto = carretePort;

    if (configRemota.lienzo.estado != EstadoModulo.APAGADO) {
      if (osc.ip().equals(lienzoIp)) configRemota.lienzo.estado = EstadoModulo.LOCAL;
      else configRemota.lienzo.estado = EstadoModulo.REMOTO;
    }
    if (configRemota.observador.estado != EstadoModulo.APAGADO) {
      if (osc.ip().equals(observadorIp)) configRemota.observador.estado = EstadoModulo.LOCAL;
      else configRemota.observador.estado = EstadoModulo.REMOTO;
    }
    if (configRemota.carrete.estado != EstadoModulo.APAGADO) {
      if (osc.ip().equals(carreteIp)) configRemota.carrete.estado = EstadoModulo.LOCAL;
      else configRemota.carrete.estado = EstadoModulo.REMOTO;
    }
  }
  void responderEstablecerEstados(int lienzo, int observador, int carrete, int modo_observador ) {
    if (configRemota == null) configRemota = new ConfiguracionCOD05();
    configRemota.lienzo.estado = EstadoModuloList[lienzo];
    configRemota.observador.estado = EstadoModuloList[observador];
    configRemota.carrete.estado = EstadoModuloList[carrete];
    configRemota.modoObservador = modo_observador==0?ModoObservador.WEBCAM:ModoObservador.KINECT;
  }

  void ejecutarRemotos(ConfiguracionCOD05 config) {
    ejecutarRemotos(config, config.lienzo, 0);
    ejecutarRemotos(config, config.observador, 1);
    ejecutarRemotos(config, config.carrete, 2);
  }
  void ejecutarRemotos(ConfiguracionCOD05 config, ConfiguracionCOD05.ConfigModulo modulo, int moduloID) {
    if (modulo.estado == EstadoModulo.REMOTO) {
      if (modulo.ip.equals(osc.ip()) && !modoDummies) modulo.estado = EstadoModulo.LOCAL;
      else {
        NetAddress destino = new NetAddress(modulo.ip, (modoDummies?modulo.puerto:oscP5Port));
        OscMessage msj = new OscMessage(establecerEstados)
          .add(EstadoModuloToInt(config.lienzo.estado))
          .add(EstadoModuloToInt(config.observador.estado))
          .add(EstadoModuloToInt(config.carrete.estado))
          .add(config.modoObservador==ModoObservador.WEBCAM?0:1);
        osc.send( msj, destino);
        println(establecerEstados, msj, destino);
        msj = new OscMessage(establecerIPs)
          .add(config.lienzo.estado == EstadoModulo.LOCAL?oscP5.ip():config.lienzo.ip).add(config.lienzo.puerto)
          .add(config.observador.estado == EstadoModulo.LOCAL?oscP5.ip():config.observador.ip).add(config.observador.puerto)
          .add(config.carrete.estado == EstadoModulo.LOCAL?oscP5.ip():config.carrete.ip).add(config.carrete.puerto);
        osc.send( msj, destino);
        println(establecerIPs, msj, destino);
        osc.send(new OscMessage(lanzar).add(moduloID), destino);
      }
    }
  }

  void responderLanzar(int moduloID) {
    println("responderLanzar("+moduloID+")");
    if (configRemota == null) configRemota = new ConfiguracionCOD05();
    if (ejecutador == null) ejecutador = new Ejecutador(configRemota, modoPDE);
    else if (millis()-ejecutador.nacimiento>6000) ejecutador = new Ejecutador(configRemota, modoPDE);
    if (moduloID == 0) ejecutador.ejecutarLienzo();
    if (moduloID == 1) ejecutador.ejecutarObservador();
    if (moduloID == 2) ejecutador.ejecutarCarrete();
  }

  void responderPing(String remitenteIp, int remitentePort, int id) {
    if (osc != null) {
      OscMessage msj = new OscMessage(controlOsc.ping+controlOsc.resultado);
      msj.add(id);
      osc.send(msj, new NetAddress( remitenteIp, remitentePort));
    }
  }
  void callbackPing(int id) {
    if (id==0) controlOsc.ultimoPingLienzo = millis();
    else if (id==1) controlOsc.ultimoPingObservador = millis();
    else if (id==2) controlOsc.ultimoPingCarrete = millis();
  }
  void callbackEstablecerIPs() {
  }
  void callbackPedirIPs() {
  }
  void callbackEstablecerEstados() {
  }
  void callbackPedirEstados() {
  }

  void pingLienzo(String ip, int puerto) {
    if (osc != null) osc.send(msjPingLienzo, new NetAddress(ip, modoDummies?puerto:oscP5Port));
  }
  void pingObservador(String ip, int puerto) {
    if (osc != null) {
      osc.send(msjPingObservador, new NetAddress(ip, modoDummies?puerto:oscP5Port));
    }
  }
  void pingCarrete(String ip, int puerto) {
    if (osc != null) osc.send(msjPingCarrete, new NetAddress(ip, modoDummies?puerto:oscP5Port));
  }
}
