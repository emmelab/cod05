class Atr_Color extends Atributo {
  static int inicialSaturacion = 255;
  static int inicialBrillo = 255;

  int[] c;

  Atr_Color(Sistema s) {
    super(s);
    c = new int[s.tamano];
    s.p5.pushStyle();
    s.p5.colorMode(s.p5.HSB);
    for (int i=0; i<c.length; i++) {
      c[i] = s.p5.color(s.p5.random(255), inicialSaturacion, inicialBrillo); //Si, hacer random en el primer momento de alguna cosa, no queda mal
      //c[i] = s.p5.color( (255f*i)/coleccion.length , 255, 255); //Esto reparte los colores equitativamente
    }
    s.p5.popStyle();
  }

  static Manager<Atr_Color> manager = new Manager() {
    public String key() { 
      return "Color";
    }
    public Atr_Color generarInstancia(Sistema s) { 
      return new Atr_Color(s);
    }
  };
}

