
   Registrador<Mod_FriccionGlobal> regFriccionGlobal = new Registrador(){
    public String key() {return "Friccion Global";}
     public String categoria() {return "Aplicar Fuerza";}
    public Mod_FriccionGlobal generarInstancia(){return new Mod_FriccionGlobal();}
  };

class Mod_FriccionGlobal extends Modificador {
  float factor = .001f;

  void ejecutar(Sistema s) {
    //Atr_Fuerza fuerzas = (Atr_Fuerza)sistema.requerir(Atr_Fuerza.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad vel = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    //Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);

    for (int i=0; i<s.tamano; i++) {
      factor = map( vel.v[i].magSq(), 0, 10000, 1.0, 0.001 );
      factor = constrain( factor, 0.001f, 1.0f );
      vel.v[i].mult(factor);
      //PVector p=posiciones.p[i];
      //acel.a[i].add(PVector.mult(PVector.fromAngle(s.p5.atan2(s.p5.height/2-p.y, s.p5.width/2-p.x)), s.tamano*factor) );
      //fuerzas.f[i].add( PVector.mult(PVector.fromAngle(atan2(height/2-p.y, width/2-p.x)), sistema.tamano*factor) );
    }
  }
  
}
