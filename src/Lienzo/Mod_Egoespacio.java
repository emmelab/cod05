//actualizacion espacio toroidal con estimulos 09/09/2016
//Carolina y Hernan

import processing.core.PVector;

class Mod_Egoespacio extends Modificador {
  
  void ejecutar(Sistema s) {
    
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanios = s.requerir(Atr_Tamano.manager, Atributo.OPCIONAL);
    
    PVector[] torsos = s.getManagerUsuarios().getJoints( ManagerUsuarios.TORSO );
    float[] confianzasDeTorsos = s.getManagerUsuarios().getConfianzasJoints( ManagerUsuarios.TORSO );
    
    PVector torso = ( torsos != null && torsos.length > 0 )? torsos[ 0 ].get() : null;
    float confianza = ( confianzasDeTorsos != null && confianzasDeTorsos.length > 0 )? confianzasDeTorsos[ 0 ] : 0 ;
    
    for (int i=0; i<s.tamano; i++) {
      PVector p = posiciones.p[i];
      float radio = (tamanios != null)? tamanios.d[i]/2 : 0 ;
      
      if( torso == null || confianza < 0.1f ){//comportamiento por defecto
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
