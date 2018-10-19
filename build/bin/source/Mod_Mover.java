import processing.core.PVector;

class Mod_Mover extends Modificador {
  Mod_Mover() {
  }

  void ejecutar(Sistema sistema) {
    Atr_Posicion posiciones = sistema.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = sistema.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion aceleraciones = sistema.requerir(Atr_Aceleracion.manager, Atributo.OPCIONAL);
    Atr_Rastro rastros = sistema.requerir(Atr_Rastro.manager, Atributo.OPCIONAL);

    for (int i=0; i<sistema.tamano; i++) { 
      if (aceleraciones != null) {
        velocidades.v[i].add(aceleraciones.a[i]);      
        aceleraciones.a[i].mult(0);
      }
      posiciones.p[i].add( velocidades.v[i] );
      if (rastros != null) rastros.r[i][0].set( posiciones.p[i] );
    }
  }
                  
  static Registrador<Mod_Mover> registrador = new Registrador() {
    public String key() {
      return "Mover";
    }
     public String categoria() {return "Aplicar Movimiento";}
    public Mod_Mover generarInstancia() {
      return new Mod_Mover();
    }
  };
}
