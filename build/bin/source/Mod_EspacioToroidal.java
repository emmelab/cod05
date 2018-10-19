import processing.core.PVector;

class Mod_EspacioToroidal extends Modificador {
  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanios = s.requerir(Atr_Tamano.manager, Atributo.OPCIONAL);

    for (int i=0; i<s.tamano; i++) {
      PVector p = posiciones.p[i];
      float radio = (tamanios != null)? tamanios.d[i]/2 : 0 ;

      p.x = ( p.x < 0-radio )? s.p5.width+radio : ( p.x > s.p5.width+radio )? 0-radio : p.x;
      p.y = ( p.y < 0-radio )? s.p5.height+radio : ( p.y > s.p5.height+radio )? 0-radio : p.y;
    }
  }
      
  static Registrador<Mod_EspacioToroidal> registrador = new Registrador() {
    public String key() {
      return "Espacio Toroidal";
    }
     public String categoria() {return "Escena";}
    public Mod_EspacioToroidal generarInstancia() {
      return new Mod_EspacioToroidal();
    }
  };
}
