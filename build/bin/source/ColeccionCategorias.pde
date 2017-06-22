class ColeccionCategorias {
  int contador;
  int cant;
  PVector posCentro;
  ArrayList categorias;  
  HashMap<Integer, Modificador> listaMods;
  HashMap<String, Modificador> listaModsPorNombre;
  color[][] paleta;
  float t;
  // boolean dosNivelesDeJerarquia;

  ColeccionCategorias() {
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
      paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
  }
  ColeccionCategorias(color[][] paleta_, PVector posCentro_, float t_) {
    paleta = paleta_;
    t = t_;
    posCentro = posCentro_;
  }

  void inicializar(String[] nombresCategorias, String[] nombresModificadores, String[] nombresModificadoresExistentes) {

    if (nombresCategorias != null && nombresModificadores != null) {
      setCategorias(nombresCategorias, nombresModificadores);
    }

    if (nombresModificadoresExistentes != null) {
      setModificadoresExistentesEnCategorias(nombresModificadoresExistentes);
    }
  }

  void setModificadoresExistentesEnCategorias(String[] nombresModificadoresExistentes) {

    for (int i=0; i<cant; i++) {
      Categoria c = (Categoria)categorias.get(i);
      c.resetMods();
      for (int k=0; k<c.modificadores.size (); k++) {
        Modificador m = (Modificador)c.modificadores.get(k);
        m.resetMods();
        for (int l=0; l<nombresModificadoresExistentes.length; l++) {
          if (m.nombre.equals(nombresModificadoresExistentes[l])) {
            c.addMod();
            m.addMod();
          }
        }
        //---------------k
      }
      //---------------j
    }
    //--------------i
  }
  void setCategorias(String[] nombresCategorias, String[] nombresModificadores) {

    categorias = new ArrayList();
    for (int i=0; i<nombresCategorias.length; i++) {

      boolean existe = false;
      int indiceCategoria = 0;
      for (int j=0; j<categorias.size (); j++) {
        Categoria c = (Categoria)categorias.get(j);
        if (c.getNombre().equals(nombresCategorias[i])) {
          existe = true;
          indiceCategoria = j;
        }
      }

      if (!existe) {
        pushStyle();
        colorMode(HSB);
        Categoria c = new Categoria(nombresCategorias[i], paleta);       
        c.aniadir(nombresModificadores[i], paleta);
        categorias.add(c);
        popStyle();
      } else {
        Categoria c = (Categoria)categorias.get(indiceCategoria);
        c.aniadir(nombresModificadores[i], paleta);
      }
    }
    cant = categorias.size();
    int contadorDeListaDeModificadores = 0;
    listaMods =  new HashMap<Integer, Modificador>();
    listaModsPorNombre =  new HashMap<String, Modificador>();
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      for (int j=0; j<c.modificadores.size (); j++) {
        Modificador m = (Modificador)c.modificadores.get(j);
        listaMods.put(contadorDeListaDeModificadores, m);
        listaModsPorNombre.put(m.getNombre(), m);
        contadorDeListaDeModificadores++;
      }
      pushStyle();
      colorMode(HSB);
      float hue = map(i, 0, cant, 0, 255);
      color colorsito = color(hue, 150, 220);
      float norm = map(i, 0, cant, 0, 1);
      //float normUnidad = map(1, 0, cant, 0, 1);
      float angulo = radians(360*norm+270);
      //float anguloUnidad = radians(360*normUnidad);
      float diametroSelector = t*118/100;
      float x = width/2+(diametroSelector*cos(angulo));
      float y = height/1.7+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);

      println(c.nombre+": "+c.modificadores.size());
      c.inicializar(colorsito, cant, pos, posCentro, t);
      popStyle();
    }
    println(categorias.size());
  }

  void dibujar() {   
    if (categorias!=null) {
      for (int i=0; i<categorias.size (); i++) {
        pushStyle();
        colorMode(HSB);  

        Categoria c = (Categoria)categorias.get(i);     
        c.dibujar();

        popStyle();
      }
    }
  }

  void dibujarCategoria() {   
    if (categorias!=null) {
      for (int i=0; i<categorias.size (); i++) {
        pushStyle();
        colorMode(HSB);  

        Categoria c = (Categoria)categorias.get(i);     
        c.dibujarCategoria();

        popStyle();
      }
    }
  }

  void setSensible(int sensible) {
    String nombreSensible = listaMods.get(sensible).getNombre();
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);  
      c.setSensible(false);

      for (int j=0; j<c.modificadores.size (); j++) {
        Modificador m = (Modificador)c.modificadores.get(j); 
        m.setSensible(false);   
        if (m.nombre.equals(nombreSensible)) {
          c.setSensible(true);
          m.setSensible(true);
        }
      }
    }
  }

  String getSensible(int sensible) {
    String nombreSensible;
    nombreSensible = listaMods.get(sensible).getNombre();
    return nombreSensible;
  }

  Modificador getModSensible(int sensible) {
    Modificador mod = listaMods.get(sensible);    
    return mod;
  }
  ///////////////////////------------------------------------MALSISISMO
  int getContadorSeleccionEstimulo(PVector cursor) {
    boolean seleccionando = false;
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);  
      if (dist(c.pos.x, c.pos.y, cursor.x, cursor.y)<30 ) {
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

  void agregar(String cual) {
    Modificador m = listaModsPorNombre.get(cual);
    m.categoria.addMod();
    m.addMod();
  }
  void quitar(String cual) {
    String[] n = split(cual, "_");
    Modificador m = listaModsPorNombre.get(n[0]);
    m.categoria.removerMod();
    m.removerMod();
  }
}