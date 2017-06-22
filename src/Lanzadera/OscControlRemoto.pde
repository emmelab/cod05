ControlOsc controlOsc = new ControlOsc();

void callbackEstablecerIPs(){}
void callbackPedirIPs(String ipLienzo, String ipObservador, String ipCarrete){}
void callbackEstablecerEstados(){}
void callbackPedirEstados(String estadoLienzo, String estadoObservador, String estadoCarrete){}

void devolverPing(String remitenteIp, int remitentePort, int id){
  if (oscP5 != null){
    OscMessage msj = new OscMessage("/Lanzadera/pong");
    msj.add(id);
    oscP5.send(msj,new NetAddress( remitenteIp,remitentePort));
  }
}
void callbackPing(int id) {
  if(id==0) controlOsc.ultimoPingLienzo = millis();
  else if(id==1) controlOsc.ultimoPingObservador = millis();
  else if(id==2) controlOsc.ultimoPingCarrete = millis();
}

class ControlOsc implements AutoSetup {
  OscP5 osc;
  
  int ultimoPingLienzo,ultimoPingObservador,ultimoPingCarrete;
  //String 
  
  String resultado = "/resultado";
  
  String establecerIPs = "/establecerIPs";
  String callbackEstablecerIPs = "callbackEstablecerIPs";
  String pedirIPs = "/pedirIPs";
  String callbackPedirIPs = "callbackPedirIPs";
  String establecerEstados = "/establecerEstados";
  String callbackEstablecerEstados = "callbackEstablecerEstados";
  String pedirEstados = "/pedirEstados";
  String callbackPedirEstados = "callbackPedirEstados";
  
  OscMessage msjPingLienzo = new OscMessage("/Lanzadera/ping").add(0),
             msjPingObservador = new OscMessage("/Lanzadera/ping").add(1),
             msjPingCarrete = new OscMessage("/Lanzadera/ping").add(2);
  void setup() {
    osc = oscP5;
    osc.plug(this,callbackEstablecerIPs,establecerIPs+resultado);
    osc.plug(this,callbackPedirIPs,pedirIPs+resultado);
    osc.plug(this,callbackEstablecerEstados,establecerEstados+resultado);
    osc.plug(this,callbackPedirEstados,pedirEstados+resultado);
  }
  
  void pingLienzo(String ip, int puerto) {
    if (osc != null) osc.send(msjPingLienzo,new NetAddress(ip,puerto));
  }
  void pingObservador(String ip, int puerto) {
    if (osc != null) osc.send(msjPingObservador,new NetAddress(ip,puerto));
  }
  void pingCarrete(String ip, int puerto) {
    if (osc != null) osc.send(msjPingCarrete,new NetAddress(ip,puerto));
  }
}