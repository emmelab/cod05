import processing.core.PVector;

class Mod_AtraccionAlTorso extends Modificador {
  float factor = .001f;

  void ejecutar(Sistema s) {
    
    PVector torso = s.getManagerJoints().getPosicionJoint( ManagerJoints.TORSO );
    
    if( torso != null ){
      //Atr_Fuerza fuerzas = (Atr_Fuerza)sis     tema.requerir(Atr_Fuerza.manager, Atributo.OBLIGATORIO);
      Atr_Aceleracion acel = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);
      Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
      
      for (int i=0; i<s.tamano; i++) {
        PVector p=posiciones.p[i];
        acel.a[i].add(PVector.mult(PVector.fromAngle(s.p5.atan2( (torso.y*s.p5.height)-p.y, (torso.x*s.p5.width)-p.x)), s.tamano*factor) );
        //fuerzas.f[i].add( PVector.mult(PVector.fromAngle(atan2(height/2-p.y, width/2-p.x)), sistema.tamano*factor) );
      }
      
    }
    
  }
  
  static Registrador<Mod_AtraccionAlTorso> registrador = new Registrador(){
    public String key() {return "Atraccion Al Torso";}
     public String categoria() {return "Estimulos";}
    public Mod_AtraccionAlTorso generarInstancia(){return new Mod_AtraccionAlTorso();}
  };
}
