import processing.core.PVector;

class Mod_FlockCohesion extends Modificador {
  Mod_FlockCohesion() {
  }

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion aceleraciones = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);

    for (int i = 0; i <  s.tamano; i++) {  
      PVector fuerzaC = cohesion(i,s,posiciones,velocidades,aceleraciones);
      fuerzaC.mult(1.2f);
      aceleraciones.a[i].add(fuerzaC); //-----------------------------------------------------valor de fuerza de cohesion (1.2) podria ser un atributo
    }
  }

  PVector cohesion(int i, Sistema s, Atr_Posicion posiciones, Atr_Velocidad velocidades, Atr_Aceleracion aceleraciones) {
    PVector suma = new PVector(0, 0, 0);
    int cuenta = 0;

    for (int j = 0; j <  s.tamano; j++) {  
      if (i != j) {

        float distancia = s.p5.dist(posiciones.p[i].x, posiciones.p[i].y, posiciones.p[j].x, posiciones.p[j].y );


        if (distancia < 100 && distancia > 0) {//-----------------------------------------------------el espacio de cohesion no es variable por ahora
          suma.add(posiciones.p[j]);
          cuenta++;
        }
      }
    }


    if (cuenta > 0) {
      suma.div(cuenta);
      return buscar(suma, i,  posiciones,  velocidades, aceleraciones);  // buscar e ir a la ubicacion
    } else {
      return new PVector(0, 0);
    }
  }

  PVector buscar(PVector objetivo, int i, Atr_Posicion posiciones, Atr_Velocidad velocidades, Atr_Aceleracion aceleraciones) {
    PVector deseo = PVector.sub(objetivo, posiciones.p[i]);  // vector desde mi hasta el objetivo
    // escalar el vector a maxima velocidad
    deseo.normalize();
    deseo.mult(2); //----------------------------------------------------------------------------------------- velocidad maxima no varibale por ahora
    // Steering = Desired minus Velocity ---- formula del algun tipo
    PVector conduccion = PVector.sub(deseo, velocidades.v[i]);
    conduccion.limit(0.07f);  // v-----------------------------------------------------------------------------alor maximo para la conduccion no variable por ahora
    return conduccion;
  }
          
  static Registrador<Mod_FlockCohesion> registrador = new Registrador() {
    public String key() {
      return "Flock Cohesion";
    }
     public String categoria() {return "Flocking";}
    public Mod_FlockCohesion generarInstancia() {
      return new Mod_FlockCohesion();
    }
  };
}
