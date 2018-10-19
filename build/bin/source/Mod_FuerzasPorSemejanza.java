import processing.core.PVector;

class Mod_FuerzasPorSemejanza extends Modificador {
  float factor = .001f;//.001f

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion fuerzas = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanos = s.requerir(Atr_Tamano.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OPCIONAL);
    
    float rangoMenor = ( colores != null )? 255 * .25f : 0 ;
    float rangoMayor = ( colores != null )? 255 * .75f : Atr_Tamano.inicialMaximo2Dados * 2 * .04f ;
    
    for (int i=0; i<s.tamano; i++) {
      PVector p=posiciones.p[i];
      PVector f=fuerzas.a[i];
      PVector atraccionFinal = new PVector();
      
      float criterio = getCriterioDeComparacion( s, i, tamanos, colores );
      
      for( int j=0; j<s.tamano; j++ ){
        if (i!=j) {
          
          PVector pj = posiciones.p[j];
          float criterioJ = getCriterioDeComparacion( s, j, tamanos, colores );
          
          float diferencia = s.p5.abs( criterioJ - criterio );

          //calculo la direccion entre la particula y la otra
          float angulo = s.p5.atan2( p.y-pj.y, p.x-pj.x );
          
          if ( diferencia <= rangoMenor || diferencia > rangoMayor ) {
            atraccionFinal.add ( PVector.fromAngle(angulo + s.p5.PI) );
          } else {
            atraccionFinal.add ( PVector.fromAngle(angulo) );
          }
          
          if (s.debug) {
            if (s.p5.dist(s.p5.mouseX, s.p5.mouseY, p.x, p.y) < 15) dibujarDebug(s,p, pj, diferencia <= rangoMenor || diferencia > rangoMayor);
          }
          
        }
      }
      
      f.add(atraccionFinal);
      f.mult( factor );
      //f.add( PVector.mult(PVector.fromAngle(s.p5.atan2(s.p5.height/2-p.y,s.p5.width/2-p.x)),factor*s.tamano*.4f *0) );
      
    }
    
  }
  
  float getCriterioDeComparacion( Sistema s, int indice, Atr_Tamano tamanos, Atr_Color colores ){
    
    if( colores != null ){
      int c = colores.c[ indice ];
      return s.p5.hue(c);
    }else{
      return tamanos.d[ indice ];
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
