class ColeccionOpciones {
  int contador;
  int cant;
  PVector posCentro;
  ArrayList opciones;  
  color[][] paleta;
  float t;

  ColeccionOpciones() {
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionOpciones(color[][] paleta_, PVector posCentro_, float t_) {
    paleta = paleta_;
    t = t_;
    posCentro = posCentro_;
  }

  void inicializar(String[] nombresOpciones) {

    if (nombresOpciones != null) {
      setOpciones(nombresOpciones);
    }
  }


  void setOpciones(String[] nombresOpciones) {

    opciones = new ArrayList();
    for (int i=0; i<nombresOpciones.length; i++) {
      Opcion c = new Opcion(nombresOpciones[i], paleta);       
      opciones.add(c);
    }
    cant = opciones.size();
    int contadorDeListaDeModificadores = 0;

    for (int i=0; i<opciones.size (); i++) {
      Opcion c = (Opcion)opciones.get(i);
      pushStyle();
      colorMode(HSB);
      float hue = map(i, 0, cant, 0, 255);
      color colorsito = color(hue, 100, 200);
      float norm = map(i, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
      //float diametroSelector = width<height? width/4 : height/4;
      float diametroSelector = t*118/100;
      float x = posCentro.x+(diametroSelector*cos(angulo));
      float y = posCentro.y+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);


      c.inicializar(colorsito, cant, pos, posCentro, t);
      popStyle();
    }
    println(opciones.size());
  }

  void dibujar() {   
    if (opciones!=null) {
      for (int i=0; i<opciones.size (); i++) {
        pushStyle();
        colorMode(HSB);  

        Opcion c = (Opcion)opciones.get(i);     
        c.dibujar();

        popStyle();
      }
    }
  }

  void setSensible(int sensible) {
    for (int i=0; i<opciones.size (); i++) {
      Opcion c = (Opcion)opciones.get(i);  
      c.setSensible(false);
    }
    Opcion c = (Opcion)opciones.get(sensible);
    c.setSensible(true);
  }

  String getSensible(int sensible) {
    String nombreSensible;
 Opcion c = (Opcion)opciones.get(sensible);
    nombreSensible = c.getNombre();

    return nombreSensible;
  }
  
  color getColorSensible(int sensible) {
    color colorSensible;
    Opcion o = (Opcion)opciones.get(sensible);
    colorSensible = o.col;

    return colorSensible;
  }
}
