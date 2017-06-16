import processing.core.PVector;

class Mod_AtraccionALaMano extends Modificador {
  float factor = .001f;

  void ejecutar(Sistema s) {
    
    PVector[] torsos = s.getManagerUsuarios().getJoints( ManagerUsuarios.TORSO );
    float[] confianzasDeTorsos = s.getManagerUsuarios().getConfianzasJoints( ManagerUsuarios.TORSO );
    PVector promedioTorsos = promedioTorsos( torsos, confianzasDeTorsos );
    
    //Atr_Fuerza fuerzas = (Atr_Fuerza)sis     tema.requerir(Atr_Fuerza.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion acel = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    
    if( promedioTorsos != null ){
      for (int i=0; i<s.tamano; i++) {
        PVector p=posiciones.p[i];
        acel.a[i].add(PVector.mult(PVector.fromAngle(s.p5.atan2( (promedioTorsos.y*s.p5.height)-p.y, (promedioTorsos.x*s.p5.width)-p.x)), s.tamano*factor) );
        //fuerzas.f[i].add( PVector.mult(PVector.fromAngle(atan2(height/2-p.y, width/2-p.x)), sistema.tamano*factor) );
      }
    }
  }
  
  private PVector promedioTorsos( PVector[] torsos, float[] confianzasDeTorsos ){
    
    if( torsos != null && torsos.length > 0 && confianzasDeTorsos != null && torsos.length == confianzasDeTorsos.length ){
      
      PVector promedio = new PVector(0,0);
      for( int i = 0; i < torsos.length; i++ ){
        if( confianzasDeTorsos[ i ] > 0.2f ){
          promedio.x += torsos[i].x;
          promedio.y += torsos[i].y;
        }
      }
      promedio.x /= torsos.length;
      promedio.y /= torsos.length;
      return promedio;
      
    }else{
      return null;
    }
    
    
  }
  
  static Registrador<Mod_AtraccionALaMano> registrador = new Registrador(){
    public String key() {return "Atraccion Al Torso";}
     public String categoria() {return "Estimulos";}
    public Mod_AtraccionALaMano generarInstancia(){return new Mod_AtraccionALaMano();}
  };
}
