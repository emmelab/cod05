/*class ColeccionEstimulos {
  int contador;
  int cant;
  PVector posCentro;
  ArrayList estimulos;  
  color[][] paleta;
  float t;

  ColeccionEstimulos() {
     paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
      paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionEstimulos(color[][] paleta_,PVector posCentro_,float t_) {
    paleta = paleta_;
    t = t_;
    posCentro = posCentro_;
  }

  void inicializar(String[] nombresEstimulos) {
    
    if (nombresEstimulos != null) {
      setEstimulos(nombresEstimulos);
    }
  }

  void setEstimulos(String[] nombresEstimulos) {

    estimulos = new ArrayList();
    for (int i=0; i<nombresEstimulos.length; i++) {
      Estimulo e = new Estimulo(nombresEstimulos[i], paleta);       
      estimulos.add(e);
    }
    cant = estimulos.size();
    int contadorDeListaDeModificadores = 0;

    for (int i=0; i<estimulos.size (); i++) {
      Estimulo e = (Estimulo)estimulos.get(i);
      pushStyle();
      colorMode(HSB);
      float hue = map(i, 0, cant, 0, 255);
      color colorsito = color(hue, 100, 200);
      float norm = map(i, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
        float diametroSelector = t*118/100;
        float x = width/2+(diametroSelector*cos(angulo));
      float y = height/1.7+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);

      //println(e.nombre+": "+e.modificadores.size());
      e.inicializar(colorsito, cant, pos, posCentro,t);
      popStyle();
    }
    println(estimulos.size());
  }

  void dibujar() {   
    if (estimulos!=null) {
      for (int i=0; i<estimulos.size (); i++) {
        pushStyle();
        colorMode(HSB);  

        Estimulo c = (Estimulo)estimulos.get(i);     
        c.dibujar();

        popStyle();
      }
    }
  }

  void setSensible(int sensible) {
    for (int i=0; i<estimulos.size (); i++) {
      Estimulo e = (Estimulo)estimulos.get(i);  
      e.setSensible(false);
    }
    Estimulo e = (Estimulo)estimulos.get(sensible);
    e.setSensible(true);
  }

  String getSensible(int sensible) {
    String nombreSensible;
    Estimulo e = (Estimulo)estimulos.get(sensible);
    nombreSensible = e.getNombre();

    return nombreSensible;
  }

  int getContadorSeleccionEstimulo(PVector cursor) {
    boolean seleccionando = false;
    for (int i=0; i<estimulos.size (); i++) {
      Estimulo e = (Estimulo)estimulos.get(i);  
      if (dist(e.pos.x, e.pos.y, cursor.x, cursor.y)<30 ) {
        if ( contador < 420)
          contador+=5;
        seleccionando = true;
      }
    }
    if (!seleccionando && contador > 0) {
      contador--;
    }

    return contador;
  }

  boolean getSeleccionarEstimulo() {
    boolean sE = contador > 400?true:false;
    return sE;
  }
}*/
