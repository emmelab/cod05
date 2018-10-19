import processing.core.PVector;

class Mod_FlockAlineamiento extends Modificador {
  Mod_FlockAlineamiento() {
  }

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion aceleraciones = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);

    for (int i = 0; i <  s.tamano; i++) {  
      PVector fuerzaA = alinear(i,s,posiciones,velocidades,aceleraciones);
      fuerzaA.mult(1.2f);//-----------------------------------------------------valor de fuerza de alineamiento (1.2) podria ser un atributo
      aceleraciones.a[i].add(fuerzaA);
    }
  }

  PVector alinear(int i, Sistema s, Atr_Posicion posiciones, Atr_Velocidad velocidades, Atr_Aceleracion aceleraciones) {    
    PVector suma = new PVector(0, 0, 0);
    int cuenta = 0;
    for (int j = 0; j < s.tamano; j++) {     
      if (i != j) {
        float distancia = s.p5.dist(posiciones.p[i].x, posiciones.p[i].y, posiciones.p[j].x, posiciones.p[j].y );


        if (distancia < 200) {
          suma.add(velocidades.v[j]);
          cuenta++;
        }
      }
    }

    if (cuenta > 0) {
      suma.div((float)cuenta);

      // implementar Reynolds: Conducir = deseo - velocidad
      suma.normalize();
      suma.mult(2);
      PVector conduccion = PVector.sub(suma, velocidades.v[i]);
      conduccion.limit(0.07f);
      return conduccion;
    } else {
      return new PVector(0, 0);
    }
  }
        
  static Registrador<Mod_FlockAlineamiento> registrador = new Registrador() {
    public String key() {
      return "Flock Alineamiento";
    }
     public String categoria() {return "Flocking";}
    public Mod_FlockAlineamiento generarInstancia() {
      return new Mod_FlockAlineamiento();
    }
  };
}
