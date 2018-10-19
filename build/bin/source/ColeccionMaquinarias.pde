class ColeccionMaquinarias {
  int contador;
  int cant;
  PVector posInicial;
  ArrayList maquinarias;  
  color[][] paleta;
  float t;
  int selector; //--- el selector

    ColeccionMaquinarias() {
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionMaquinarias(color[][] paleta_, float t_) {
    paleta = paleta_;
    t = t_;
  }

  void inicializar(String[] nombresMaquinarias) {
    if (nombresMaquinarias != null) {
      setMaquinarias(nombresMaquinarias);
    }
  }

  void setMaquinarias(String[] nombresMaquinarias) {
    maquinarias = new ArrayList();
    for (int i=0; i<nombresMaquinarias.length; i++) {
      Maquinaria m = new Maquinaria(nombresMaquinarias[i], paleta);       
      maquinarias.add(m);
    }
    cant = maquinarias.size();
    //int contadorDeListaDeModificadores = 0;

    for (int i=0; i<maquinarias.size (); i++) {
      Maquinaria m = (Maquinaria)maquinarias.get(i);
      pushStyle();
      colorMode(HSB);
      float hue = map(0, 0, cant, 0, 255);
      color colorsito = color(hue, 100, 200);
      float norm = map(0, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      //float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
      //float diametroSelector = t*50/100;

      //PVector pos = new PVector(x, y);      
      //println(e.nombre+": "+e.modificadores.size());
      m.inicializar(colorsito, cant, new PVector(), new PVector(bdd.ruedaX, bdd.ruedaY), t*25/100);
      popStyle();
    }
    println(maquinarias.size());
  }
  void dibujarBase(float a, float b, float ancho, float alto) {
    pushStyle();
    rectMode(CORNER);
    noStroke();
    fill(paleta[1][2]);
    rect(a, b, ancho, alto);
    popStyle();
  }

  void dibujar() {    
    dibujarBase(bdd.baseMaquinariasX, bdd.baseMaquinariasY, bdd.baseMaquinariasAncho, bdd.baseMaquinariasAlto);
    int cantidadMaqsVisibles=8;//cuantos se van a ver en el pie
    float espacioMaqsX = width/cantidadMaqsVisibles;//cuanto espacio ocupa cada uno incluido bordes
    float alCentro = espacioMaqsX/2;//cuanto hay desde el borde hasta el centro
    float x = 0;
    float y = height-bdd.baseMaquinariasAlto/2;
    if (maquinarias!=null) {
      pushStyle();
      colorMode(HSB); 
      int contadorColores = 0;
      int contadorOrden = 0;
      Maquinaria m = null;
      for (int i=selector-1; i>=0; i--) { 
        contadorOrden++;
        x = width/2-contadorOrden*espacioMaqsX;//+espacioMaqsX;        
        m = (Maquinaria)maquinarias.get(i);
        m.dibujarIconos(x, y, t, paleta[3][contadorColores]);
        contadorColores++;
        contadorColores = (contadorColores+paleta[3].length)%paleta[3].length;
      }
      x = width/2;
      m = (Maquinaria)maquinarias.get(selector);     
        m.dibujarIconos(x, y, t, paleta[3][contadorColores]);
      contadorColores++;
      contadorColores = (contadorColores+paleta[3].length)%paleta[3].length;
      contadorOrden=0;
      for (int i=selector+1; i<maquinarias.size (); i++) {   
        contadorOrden++;     
        x = width/2+contadorOrden*espacioMaqsX;//+espacioMaqsX;
        m = (Maquinaria)maquinarias.get(i);
        m.dibujarIconos(x, y, t, paleta[3][contadorColores]);
        contadorColores++;
        contadorColores = (contadorColores+paleta[3].length)%paleta[3].length;
      }
      popStyle();
    }
  }

  void setSensible(int sensible) {
    selector = sensible;
    if (sensible>-1) {
      for (int i=0; i<maquinarias.size (); i++) {
        Maquinaria e = (Maquinaria)maquinarias.get(i);  
        e.setSensible(false);
      }
      Maquinaria e = (Maquinaria)maquinarias.get(sensible);
      e.setSensible(true);
    }
  }

  String getSensible(int sensible) {
    String nombreSensible;
    Maquinaria e = (Maquinaria)maquinarias.get(sensible);
    nombreSensible = e.getNombre();

    return nombreSensible;
  }

  color getColorSensible(int sensible) {
    color colorSensible;
    Maquinaria maq = (Maquinaria)maquinarias.get(sensible);
    colorSensible = maq.col;
    return colorSensible;
  }

  Maquinaria getMaqSensible(int sensible) {
    Maquinaria maq = (Maquinaria)maquinarias.get(sensible);
    return maq;
  }
}
