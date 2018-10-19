//actualizacion espacio toroidal con estimulos 09/09/2016
//Carolina y Hernan

import processing.core.PVector;

class Mod_Egoespacio extends Modificador {
  
  void ejecutar(Sistema s) {
    
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanios = s.requerir(Atr_Tamano.manager, Atributo.OPCIONAL);
    
    PVector torso = s.getManagerJoints().getPosicionJoint( ManagerJoints.TORSO );
    
    for (int i=0; i<s.tamano; i++) {
      PVector p = posiciones.p[i];
      float radio = (tamanios != null)? tamanios.d[i]*0.5f : 0 ;
      
      if( torso == null ){//comportamiento por defecto
        p.x = ( p.x < 0-radio )? s.p5.width+radio : ( p.x > s.p5.width+radio )? 0-radio : p.x;
        p.y = ( p.y < 0-radio )? s.p5.height+radio : ( p.y > s.p5.height+radio )? 0-radio : p.y;
      }else{
        if( p.x < 0-radio || p.x > s.p5.width+radio || p.y < 0-radio || p.y > s.p5.height+radio ){
          p.x = torso.x * s.p5.width;
          p.y = torso.y * s.p5.height;
        }
      }
      
    }
    
  }
      
  static Registrador<Mod_Egoespacio> registrador = new Registrador() {
    public String key() {
      return "Egoespacio";
    }
     public String categoria() {return "Estimulos";}
    public Mod_Egoespacio generarInstancia() {
      return new Mod_Egoespacio();
    }
  };
  
}
