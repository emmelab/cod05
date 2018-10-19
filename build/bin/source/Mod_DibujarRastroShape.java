// 4/5/2016

import processing.core.PVector;

class Mod_DibujarRastroShape extends Modificador {

  void ejecutar(Sistema sistema) {
    Sistema s = sistema;
    Atr_Rastro rastro = s.requerir(Atr_Rastro.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanos = s.requerir(Atr_Tamano.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OPCIONAL);

    s.p5.pushStyle(); 

    for (int i = 0; i < sistema.tamano; i++) {   
      
      s.p5.beginShape();
      s.p5.noFill();
      s.p5.strokeWeight( tamanos.d[i] * 0.2f );
      if (colores != null)
        s.p5.stroke(colores.c[i]);
      else
        s.p5.stroke( 255 );
      
      for( int j = 0; j < rastro.r[ i ].length; j++ ){
        PVector p = rastro.r[ i ][ j ];
        s.p5.curveVertex( p.x, p.y );
        if( j == 0 || j == rastro.r[ i ].length - 1 ) s.p5.curveVertex( p.x, p.y );;
      }
      
      s.p5.endShape();
    }
    s.p5.popStyle(); 
  }

  static Registrador<Mod_DibujarRastroShape> registrador = new Registrador() {
    public String key() {
      return "Dibujar Rastro Shape";
    }
    public String categoria() {
      return "Forma De Rastro";
    }
    public Mod_DibujarRastroShape generarInstancia() {
      return new Mod_DibujarRastroShape();
    }
  };
}
