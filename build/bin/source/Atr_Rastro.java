import processing.core.PVector;
import processing.core.PApplet;

class Atr_Rastro extends Atributo {
  static int inicialTamArreglo = 10;
  
  PVector[][] r;

  Atr_Rastro(Sistema s) {
    super(s);
    Atr_Posicion posiciones = (Atr_Posicion)s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    r = new PVector[s.tamano][inicialTamArreglo];
   
    for (int i=0; i<r.length; i++) {
       for (int j=0; j<r[i].length; j++) {
        r[i][j] = posiciones.p[i].get();;// Esto clona una instancia de PVector generando una nueva instancia :D
      }
    }
  }

  static Manager<Atr_Rastro> manager = new Manager() {
    public String key() { 
      return "Rastro";
    }
    public Atr_Rastro generarInstancia(Sistema s) { 
      return new Atr_Rastro(s);
    }
  };
}
