class Atr_Tamano extends Atributo {
  static float inicialMinimo2Dados = 5;
  static float inicialMaximo2Dados = 10;
  
  float[] w, h, d;

  Atr_Tamano(Sistema s) {
    super(s);
    w = new float[s.tamano];
    h = new float[s.tamano];
    d = new float[s.tamano];
    for (int i=0; i<s.tamano; i++) w[i] = h[i] = d[i] = s.p5.random(inicialMinimo2Dados, inicialMaximo2Dados)+s.p5.random(inicialMinimo2Dados, inicialMaximo2Dados);
  }

  static Manager<Atr_Tamano> manager = new Manager() {
    public String key() { 
      return "Tamaño";
    }
    public Atr_Tamano generarInstancia(Sistema s) { 
      return new Atr_Tamano(s);
    }
  };
}
