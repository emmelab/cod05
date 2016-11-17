import processing.core.PVector;

class Mod_ResetLluvia extends Modificador {
  float umbralVelocidadMinima = 0.05f;
  float yResetMinima = 0, yResetMaxima = 200;
  float divisorLimiteInferior = 8;

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);

    float limiteInferior = (divisorLimiteInferior-1f)*s.p5.height/divisorLimiteInferior;

    for (int i=0; i<s.tamano; i++) {
      PVector v = velocidades.v[i];
      PVector p = posiciones.p[i];

      //pregunto por si la particula esta cerca del piso
      //y por si la magnitud cuadrada de la velocidad es muy baja ( no hace falta sacar la raiz cuadrada o usar mag() )
      if ( p.y > limiteInferior && v.magSq() < umbralVelocidadMinima) {

        //en caso de ser cierto solo reseteo la posicion en el eje Y
        p.y = s.p5.random(yResetMinima,yResetMaxima);

        // mmmmm opcional para mas caos
        //p.x= random(width);
      }
    }
  }
                       
  static Registrador<Mod_ResetLluvia> registrador = new Registrador() {
    public String key() {
      return "Mod Reset Lluvia";
    }
     public String categoria() {return "Reset Lluvia";}
    public Mod_ResetLluvia generarInstancia() {
      return new Mod_ResetLluvia();
    }
  };
}

