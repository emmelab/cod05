  import processing.core.PVector;

class Mod_AtraccionAlCentro extends Modificador {
  float factor = .001f;

  void ejecutar(Sistema s) {
    //Atr_Fuerza fuerzas = (Atr_Fuerza)sis     tema.requerir(Atr_Fuerza.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion acel = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);

    for (int i=0; i<s.tamano; i++) {
      PVector p=posiciones.p[i];
      acel.a[i].add(PVector.mult(PVector.fromAngle(s.p5.atan2(s.p5.height/2-p.y, s.p5.width/2-p.x)), s.tamano*factor) );
      //fuerzas.f[i].add( PVector.mult(PVector.fromAngle(atan2(height/2-p.y, width/2-p.x)), sistema.tamano*factor) );
    }
  }
  
  static Registrador<Mod_AtraccionAlCentro> registrador = new Registrador(){
    public String key() {return "Atraccion Al Centro";}
     public String categoria() {return "Aplicar Fuerza";}
    public Mod_AtraccionAlCentro generarInstancia(){return new Mod_AtraccionAlCentro();}
  };
}
