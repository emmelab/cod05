import processing.core.PVector;

class Atr_Posicion extends Atributo {
  PVector[] p;

  Atr_Posicion(Sistema s) {
    super(s);
    p = new PVector[s.tamano];
    int grilla = s.p5.ceil(s.p5.sqrt(p.length));
    int grillaX = grilla;
    int grillaY = grilla;
    for (int i=0; i<p.length; i++) {
      p[i] = new PVector( (i%grillaX + .5f) * (s.p5.width / grillaX), (i/grillaY + .5f) * (s.p5.height / grillaY) );
      //p[i] = new PVector( s.p5.width/2,s.p5.height/2); //desde el centro
      //p[i] = new PVector(s.p5.random(s.p5.width),s.p5.random(s.p5.height)); //random
    }
  }

  static Manager<Atr_Posicion> manager = new Manager() {
    public String key() { 
      return "Posicion";
    }
    public Atr_Posicion generarInstancia(Sistema s) { 
      return new Atr_Posicion(s);
    }
  };
}
