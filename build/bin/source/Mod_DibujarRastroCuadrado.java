import processing.core.PVector;

class Mod_DibujarRastroCuadrado extends Modificador {
  float factor = .08f;

  void ejecutar(Sistema sistema) {
    Sistema s = sistema;
    Atr_Rastro rastro = s.requerir(Atr_Rastro.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanos = s.requerir(Atr_Tamano.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OPCIONAL);

    s.p5.pushStyle(); 


    for (int i = 0; i < sistema.tamano; i++) {
       if (colores != null) {
      s.p5.fill(colores.c[i]);
    }
      for (int j = 0; j < rastro.r[i].length; j++) {
        float d = s.p5.map(j, 0, rastro.r[i].length-1, tamanos.d[i], tamanos.d[i]/5);
        PVector p = rastro.r[i][j];
          s.p5.rect(p.x, p.y, d, d);
      }
    }
      s.p5.popStyle(); 
  }


  static Registrador<Mod_DibujarRastroCuadrado> registrador = new Registrador() {
    public String key() {
      return "Dibujar Rastro Cuadrado";
    }
    public String categoria() {
      return "Forma De Rastro";
    }
    public Mod_DibujarRastroCuadrado generarInstancia() {
      return new Mod_DibujarRastroCuadrado();
    }
  };
}
