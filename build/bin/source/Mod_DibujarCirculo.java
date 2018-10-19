import processing.core.PVector;

class Mod_DibujarCirculo extends Modificador {
  void ejecutar(Sistema s) {
    Atr_Tamano tamanos = s.requerir(Atr_Tamano.manager, Atributo.OBLIGATORIO);
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OPCIONAL);

    s.p5.pushStyle();
    for (int i=0; i<s.tamano; i++) {
      PVector p = posiciones.p[i];

      if (colores != null) {
        s.p5.fill(colores.c[i]);
      }
      s.p5.ellipse(p.x, p.y, tamanos.d[i], tamanos.d[i]);
    }
    s.p5.popStyle();
  }

  static Registrador<Mod_DibujarCirculo> registrador = new Registrador() {
    public String key() {
      return "Dibujar Circulo";
    }
     public String categoria() {return "Vizualizar Particulas";}
    public Mod_DibujarCirculo generarInstancia() {
      return new Mod_DibujarCirculo();
    }
  };
}
