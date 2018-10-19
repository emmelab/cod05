import processing.core.PVector;

class Mod_FlockSeparacion extends Modificador {
  Mod_FlockSeparacion() {
  }

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    Atr_Aceleracion aceleraciones = s.requerir(Atr_Aceleracion.manager, Atributo.OBLIGATORIO);

    for (int i = 0; i <  s.tamano; i++) {  
      PVector fuerzaS = separacion(i,s,posiciones,velocidades,aceleraciones);
      fuerzaS.mult(1.7f);
      aceleraciones.a[i].add(fuerzaS); //-----------------------------------------------------valor de fuerza de cohesion (1.2) podria ser un atributo
    }
  }

  PVector separacion(int i, Sistema s, Atr_Posicion posiciones, Atr_Velocidad velocidades, Atr_Aceleracion aceleraciones) {
    //separacion
    PVector conduccion = new PVector(0, 0, 0);
    int cuenta = 0;
    for (int j = 0; j <  s.tamano; j++) {     
      if (i != j) {

        float distancia = s.p5.dist(posiciones.p[i].x, posiciones.p[i].y, posiciones.p[j].x, posiciones.p[j].y );

        if (distancia < 100 && distancia > 0) {//-----------------------------------------------------el espacio de cohesion no es variable por ahora

          PVector diferencia = PVector.sub(posiciones.p[i], posiciones.p[j]);
          diferencia.normalize();
          diferencia.div(distancia);        //dar una fuerza segun al distancia
          conduccion.add(diferencia);
          cuenta++;   //saber cuantas relaciones
        }
      }
    }

    if (cuenta > 0) {
      conduccion.div((float)cuenta);//promedio de la conduccion
    }

    if (conduccion.mag() > 0) {

      // implementar Reynolds: Conducir = deseo - velocidad
      conduccion.normalize();
      conduccion.mult(2);//----------------------------------------------------------------------------------------- velocidad maxima no varibale por ahora
      conduccion.sub(velocidades.v[i]);
      conduccion.limit(0.07f);// v-----------------------------------------------------------------------------alor maximo para la conduccion no variable por ahora
    }
    return conduccion;
  }
            
  static Registrador<Mod_FlockSeparacion> registrador = new Registrador() {
    public String key() {
      return "Flock Separacion";
    }
     public String categoria() {return "Flocking";}
    public Mod_FlockSeparacion generarInstancia() {
      return new Mod_FlockSeparacion();
    }
  };
}
