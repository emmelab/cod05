ControlOsc controlOsc = new ControlOsc();

void callbackEstablecerIPs(){}
void callbackPedirIPs(String ipLienzo, String ipObservador, String ipCarrete){}
void callbackEstablecerEstados(){}
void callbackPedirEstados(String estadoLienzo, String estadoObservador, String estadoCarrete){}

class ControlOsc implements AutoSetup {
  OscP5 osc;
  
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
  
  void setup() {
    osc = oscP5;
    osc.plug(this,callbackEstablecerIPs,establecerIPs+resultado);
    osc.plug(this,callbackPedirIPs,pedirIPs+resultado);
    osc.plug(this,callbackEstablecerEstados,establecerEstados+resultado);
    osc.plug(this,callbackPedirEstados,pedirEstados+resultado);
  }
}