//;;;; Daniel-san
//;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; CREADO 17_08_15
import processing.core.PVector;

class Mod_DibujarFlecha extends Modificador { 
float factorVelocidad = 15;
float anguloPuntaFlecha = 150; 
  
  void ejecutar(Sistema s) {
    Atr_Posicion pos = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OPCIONAL);
    
    float anguloPuntaFlechaRad = s.p5.radians(anguloPuntaFlecha);
    
        s.p5.pushStyle();
    for (int i=0; i<s.tamano; i++) {
      PVector p = pos.p[i];
      float magVel = velocidades.v[i].mag()*factorVelocidad;
      float angulo = velocidades.v[i].heading();

      if (colores != null) {
        s.p5.stroke(colores.c[i]);
      } else{
      s.p5.stroke(255);
      }
      
      float dx = p.x-velocidades.v[i].x*factorVelocidad;
      float dy = p.y-velocidades.v[i].y*factorVelocidad;
      s.p5.line(p.x, p.y, dx, dy);
      
      float dx2 = p.x+(magVel/2)*s.p5.cos(angulo-anguloPuntaFlechaRad);
      float dy2 = p.y+(magVel/2)*s.p5.sin(angulo-anguloPuntaFlechaRad);
       float dx3 = p.x+(magVel/2)*s.p5.cos(angulo+anguloPuntaFlechaRad);
      float dy3 = p.y+(magVel/2)*s.p5.sin(angulo+anguloPuntaFlechaRad);
      
      s.p5.line(p.x, p.y, dx2,dy2);
      s.p5.line(p.x, p.y, dx3,dy3);

    }
        s.p5.popStyle();
  }
  
      
  static Registrador<Mod_DibujarFlecha> registrador = new Registrador() {
    public String key() {
      return "Dibujar Flecha";
    }
     public String categoria() {return "Vizualizar Particulas";}
    public Mod_DibujarFlecha generarInstancia() {
      return new Mod_DibujarFlecha();
    }
  };
}
