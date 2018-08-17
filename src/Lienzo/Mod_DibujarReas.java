//;;;; Daniel-san
//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; CREADO 17_08_15
import processing.core.PVector;

class Mod_DibujarReas extends Modificador { 
  float factorTamanio = 1.7f;

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanios = s.requerir(Atr_Tamano.manager, Atributo.OPCIONAL);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OPCIONAL);

    float tamSinTam = (Atr_Tamano.inicialMinimo2Dados*.5f+Atr_Tamano.inicialMaximo2Dados*.5f)*2;
    
    final int MAXIMAS_LINEAS = 8;//NUEVO
    
    s.p5.pushStyle();
    if (colores == null) s.p5.stroke(255);
    for (int i=0; i<s.tamano; i++) {
      PVector p1 = posiciones.p[i];
      float t1 = (tamanios==null? tamSinTam : tamanios.d[i]);
      int contarLineas = 0;//NUEVO
      for (int j=i+1; j<s.tamano; j++) {
        if (i!=j) {
          PVector p2 = posiciones.p[j];
          float t2 = (tamanios==null?tamSinTam:tamanios.d[j]);
          float distancia = s.p5.dist(p1.x, p1.y, p2.x, p2.y);
          if (distancia < (t1+t2)*factorTamanio) {
            if (colores != null) {
              s.p5.stroke( s.p5.lerpColor(colores.c[i], colores.c[j], .5f) );
            }
            s.p5.line(p1.x, p1.y, p2.x, p2.y);
            contarLineas++;
            if( contarLineas >= MAXIMAS_LINEAS ) break;//NUEVO
          }
        }
      }
    }
    s.p5.popStyle();
  }

  static Registrador<Mod_DibujarReas> registrador = new Registrador() {
    public String key() {
      return "Dibujar Reas";
    }
     public String categoria() {return "Vizualizar Particulas";}
    public Mod_DibujarReas generarInstancia() {
      return new Mod_DibujarReas();
    }
  };
}
