import processing.core.PVector;

class Atr_Velocidad extends Atributo {
  static float inicialMinima = .05f;
  static float inicialMaxima = 2;
  
  PVector[] v;

  Atr_Velocidad(Sistema s) {
    super(s);
    v = new PVector[s.tamano];
    for (int i=0; i<v.length; i++) {
      v[i] = PVector.random2D();
      v[i].mult( s.p5.random(inicialMinima, inicialMaxima) );
    }
  }

  static Manager<Atr_Velocidad> manager = new Manager() {
    public String key() { 
      return "Velocidad";
    }
    public Atr_Velocidad generarInstancia(Sistema s) { 
      return new Atr_Velocidad(s);
    }
  };
}
