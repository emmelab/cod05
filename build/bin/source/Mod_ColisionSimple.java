  import processing.core.PVector;
import java.util.ArrayList;

class Mod_ColisionSimple extends Modificador {
  float elasticidad = .7f;

  void ejecutar(Sistema s) {
    Atr_Tamano tamanos = s.requerir(Atr_Tamano.manager, Atributo.OBLIGATORIO);
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OPCIONAL);

    for (int i=0; i<s.tamano; i++) {

      PVector vel = null;
      if (velocidades != null) vel = velocidades.v[i];
      PVector pos = posiciones.p[i];
      float radio = tamanos.d[i]/2;
      ArrayList<PVector> expulsion = new ArrayList();
      ArrayList<Float> importancia = new ArrayList();
      float importanciaTotal = 0;
      for (int j=0; j<s.tamano; j++) {
        if (i!=j) {

          PVector posj = posiciones.p[j];
          float radioj = tamanos.d[j]/2;
          float distChoque2 = (radio+radioj)*(radio+radioj);
          float dist2 = (pos.x-posj.x)*(pos.x-posj.x)+(pos.y-posj.y)*(pos.y-posj.y);
          if (dist2 < distChoque2) {
            float magnitud = (radio+radioj)-s.p5.sqrt(dist2);
            if (magnitud == 0) continue;
            PVector direccion = PVector.fromAngle( s.p5.atan2( pos.y-posj.y, pos.x-posj.x ) );
            direccion.mult(magnitud);
            expulsion.add(direccion);
            importancia.add(magnitud);
            importanciaTotal += magnitud;

            if (s.debug) {
              dibujarDebug(s,pos, radio);
            }
          }
        }
      }

      if (importanciaTotal > 0) {
        PVector expulsionFinal = new PVector();
        for (int e=0; e<expulsion.size (); e++) {
          PVector expulsionLocal = expulsion.get(e);
          expulsionLocal.mult( importancia.get(e) / importanciaTotal );
          expulsionFinal.add(expulsionLocal);
        }
        if (vel == null)
          pos.add(expulsionFinal);
        else {
          PVector fuerza = expulsionFinal.get();
          fuerza.mult(elasticidad);
          expulsionFinal.mult(1-elasticidad);
          pos.add(expulsionFinal);
          vel.add(fuerza);
        }
      }
    }
  }

  void dibujarDebug(Sistema s, PVector pos, float radio) {
    s.p5.pushStyle();
    s.p5.noFill();
    s.p5.stroke(0, 255, 0);
    s.p5.ellipse(pos.x, pos.y, radio*2.3f, radio*2.3f);
    s.p5.popStyle();
  }

  static Registrador<Mod_ColisionSimple> registrador = new Registrador() {
    public String key() {
      return "Colision Simple";
    }
     public String categoria() {return "Aplicar Colisiones";}
    public Mod_ColisionSimple generarInstancia() {
      return new Mod_ColisionSimple();
    }
  };
}
