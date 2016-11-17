import processing.core.PVector;

class Mod_FuerzasPorSemejanza extends Modificador {
  float factor = .001f;

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion fuerzas = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);

    for (int i=0; i<s.tamano; i++) {
      PVector p=posiciones.p[i];
      int c=colores.c[i];
      float tinte = s.p5.hue(c);
      PVector f=fuerzas.a[i];
      PVector atraccionFinal = new PVector();

      for (int j=0; j<s.tamano; j++) {
        if (i!=j) {

          PVector pj = posiciones.p[j];
          int cj = colores.c[j];
          float diferenciaTinte = s.p5.abs( s.p5.hue(cj)-tinte );

          //calculo la direccion entre la particula y la otra
          float angulo = s.p5.atan2( p.y-pj.y, p.x-pj.x );

          if ( diferenciaTinte <= 63 || diferenciaTinte > 191 ) {
            atraccionFinal.add ( PVector.fromAngle(angulo + s.p5.PI) );
          } else {
            atraccionFinal.add ( PVector.fromAngle(angulo) );
          }

          if (s.debug) {
            if (s.p5.dist(s.p5.mouseX, s.p5.mouseY, p.x, p.y) < 15) dibujarDebug(s,p, pj, diferenciaTinte <= 63 || diferenciaTinte > 191);
          }
        }
      }

      f.add(atraccionFinal);
      f.mult( factor );
      f.add( PVector.mult(PVector.fromAngle(s.p5.atan2(s.p5.height/2-p.y,s.p5.width/2-p.x)),factor*s.tamano*.4f *0) );
    }
  }

  void dibujarDebug(Sistema s, PVector desde, PVector hasta, boolean atrae) {
    s.p5.pushStyle();
    s.p5.stroke( atrae?0:255, atrae?255:0, 0); 
    s.p5.line( desde.x, desde.y, hasta.x, hasta.y);
    s.p5.popStyle();
  }
              
  static Registrador<Mod_FuerzasPorSemejanza> registrador = new Registrador() {
    public String key() {
      return "Mod Fuerzas Por Semejanza";
    }
     public String categoria() {return "Fuerzas Por Semejanza";}
    public Mod_FuerzasPorSemejanza generarInstancia() {
      return new Mod_FuerzasPorSemejanza();
    }
  };
}

