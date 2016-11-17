//-------------------------------------
//resolver un ropblema con el flock y quiza con otro modificadores
//cuando algun modificador no esta funcional puede que algunos aributos se modifiquen constantemente y cuadno se activa el modificador pasan cosas malas....
//ejemplo cuando mover se desactiva el flock sigue aumentando la aceleracion que no se resetea como deberia....
//opciones ubicar el reseteo de la aceleracion como parte constante de la existencia del mismo... o algo parecido....
import oscP5.*;
import netP5.*;

OscP5 oscP5;
NetAddress consola;

Sistema sistema;

boolean pausa; 
boolean fondoAlfa;

int posModificadorMover;
int raizDeCantidad = 12;//12

void setup() {
  size(displayWidth, displayHeight);
  //size( 800, 600 );
  noCursor();
  sistema = new Sistema(this, raizDeCantidad*raizDeCantidad);
  //if (sistema.registroModificadores==null)sistema.registroModificadores = new HashMap();
  //println(sistema.registroModificadores);
  println(Mod_AlfaSegunVelocidad.registrador);
   //Mod_AlfaSegunVelocidad.velocidadMayor = 5;
   println(Mod_AtraccionAlCentro.registrador);
   //Mod_AtraccionAlCentro.factor = .001f;
   println(Mod_ColisionSimple.registrador);
   println(Mod_DibujarCirculo.registrador);
   println(Mod_DibujarFlecha.registrador);
   println(Mod_DibujarReas.registrador);
   println(Mod_EspacioCerrado.registrador);
   println(Mod_EspacioToroidal.registrador);
   println(Mod_FlockAlineamiento.registrador);
   println(Mod_FlockCohesion.registrador);
   println(Mod_FlockSeparacion.registrador);
   println(Mod_FuerzasPorSemejanza.registrador);
   println(Mod_Gravedad.registrador);
   println(Mod_Mover.registrador);
   println(Mod_ResetLluvia.registrador); 
   println(Mod_RastroElastico.registrador);
    println(Mod_DibujarRastroCircular.registrador);
    println(Mod_DibujarRastroShape.registrador);
  //Atr_Tamano.inicialMinimo2Dados = 1;
  // Atr_Tamano.inicialMaximo2Dados = 20;
   
  // sistema.agregar(Mod_AlfaSegunVelocidad.registrador) .velocidadMayor = 100;
  // sistema.agregar(Mod_FlockSeparacion.registrador);
   //sistema.agregar(Mod_FlockCohesion.registrador);
   //sistema.agregar(Mod_FlockAlineamiento.registrador);
   sistema.agregar(Mod_EspacioToroidal.registrador.key());
   //sistema.agregar(Mod_FuerzasPorSemejanza.registrador);
  // sistema.agregar(Mod_AtraccionAlCentro.registrador);
 //  sistema.agregar(Mod_ColisionSimple.registrador.key());
   sistema.agregar(Mod_Mover.registrador.key());
  // sistema.agregar(Mod_DibujarReas.registrador).factorTamanio = 1f;
   sistema.agregar(Mod_DibujarCirculo.registrador.key());
 //   sistema.agregar(Mod_RastroElastico.registrador.key());
  //   sistema.agregar(Mod_DibujarRastroCircular.registrador.key());
 //    sistema.agregar(Mod_RastroElastico.registrador.key());
  //   sistema.agregar(Mod_DibujarRastroCircular.registrador.key());
  //sistema.agregar(Mod_DibujarFlecha.registrador.key());
  //---------------------------------------------------------------------------MODIFICADORES TOTAL----------------------------------------------------------------------------

  initOSC();
  modificadoresExistentes();
  
  //modificadoresTotal();
  for (String n : sistema.registroModificadores.keySet()) {
    String categoria = sistema.registroModificadores.get(n).categoria();
   // println(categoria);
  }
}

void draw() {
  if (!pausa) {
    ciclo();
  }
}

void ciclo() { 
  if (fondoAlfa) {
    pushStyle();
    fill(0, 5);
    rect(0, 0, width, height);
    popStyle();
  } else {
    background(0);
  }

  sistema.actualizar();

  fill(255);
  text(frameRate, 5, 10);
}

boolean sketchFullScreen(){
  return true;
}


void keyPressed() {
   
  if (key == ' ') pausa = !pausa;
  else if (keyCode == TAB) ciclo();
  else if (keyCode == BACKSPACE || keyCode == DELETE) sistema.reset();
  else if (key == 'f') fondoAlfa = !fondoAlfa;
  else if (key == 'd') sistema.debug = !sistema.debug;
  else if (key == 's') saveFrame("capturas/capturas_"+frameCount+".png");
}
