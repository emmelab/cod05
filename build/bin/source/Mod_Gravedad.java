import processing.core.PVector;

class Mod_Gravedad extends Modificador {
  float fuerza = .05f;
  PVector direccion = PVector.fromAngle(processing.core.PApplet.HALF_PI);

  void ejecutar(Sistema sistema) {
    Atr_Posicion posiciones = sistema.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = sistema.requerir(Atr_Velocidad.manager, Atributo.OPCIONAL);
    Atr_Aceleracion aceleracion = sistema.requerir(Atr_Aceleracion.manager, Atributo.OPCIONAL);

    PVector gravedad = direccion.get();
    gravedad.mult(fuerza);
    PVector[] lista = null;
    
    if (aceleracion != null) lista = aceleracion.a;
    else if (velocidades != null) lista = velocidades.v;
    else lista = posiciones.p;
    for (int i=0; i<sistema.tamano; i++) {
      lista[i].add(gravedad);
    }
  }
                
  static Registrador<Mod_Gravedad> registrador = new Registrador() {
    public String key() {
      return "Gravedad";
    }
     public String categoria() {return "Aplicar Fuerza";}
    public Mod_Gravedad generarInstancia() {
      return new Mod_Gravedad();
    }
  };
}
