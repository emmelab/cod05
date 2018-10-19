import processing.core.PVector;

class Atr_Aceleracion extends Atributo {
  PVector[] a;

  Atr_Aceleracion(Sistema s) {
    super(s);
    a = new PVector[s.tamano];
    for (int i=0; i<a.length; i++) a[i] = new PVector();
  }

  static Manager<Atr_Aceleracion> manager = new Manager() {
    public String key() { 
      return "Aceleracion";
    }
    public Atr_Aceleracion generarInstancia(Sistema s) { 
      return new Atr_Aceleracion(s);
    }
  };
}
