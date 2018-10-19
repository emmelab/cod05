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

  void inicializar(String[] nombresCategorias, boolean[]opcionesDeNavegacion, String[] nombresModificadores, String[] nombresModificadoresExistentes) {

    if (nombresCategorias != null && nombresModificadores != null) {
      setCategorias(nombresCategorias, opcionesDeNavegacion, nombresModificadores);
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
  void setCategorias(String[] nombresCategorias, boolean[]opcionesDeNavegacion, String[] nombresModificadores) {

    categorias = new ArrayList();
    ArrayList<Boolean> opcionesDeNav = new ArrayList<Boolean>();
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
        opcionesDeNav.add(opcionesDeNavegacion[i]);
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
      float angulo = radians(360*norm+90);
      //float anguloUnidad = radians(360*normUnidad);
      float diametroSelector = t*118/100;
      float x = bdd.ruedaX+(diametroSelector*cos(angulo));
      float y = bdd.ruedaY+(diametroSelector*sin(angulo));
      PVector pos = new PVector(x, y);

      println(c.nombre+": "+c.modificadores.size());
      //c.inicializar(colorsito, cant, pos, posCentro, t);
      c.inicializar(colorsito, cant, pos, posCentro, t, opcionesDeNav.get(i));
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

  color getColorSensible(int sensible) {
    color colorSensible;
    Modificador mod = listaMods.get(sensible);
    Categoria c = mod.categoria;   
    colorSensible = c.col;
    return colorSensible;
  }

  Modificador getModSensible(int sensible) {
    Modificador mod = listaMods.get(sensible);    
    return mod;
  }

  //---------------------MOUSE------------
  void mouse() {
    if (categorias != null) {
      for (int i=0; i<categorias.size (); i++) {
        Categoria c = (Categoria)categorias.get(i);
        c.setHover();
      }
    }
  }
  String getSensibleMouse() {
    String nombreSensibleMouse = null;
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      if (c.hover) {
        nombreSensibleMouse = c.getHover();
      }
    }
    return nombreSensibleMouse;
  }

  Modificador getSensibleMouse_modificador() {
    Modificador modificadorSensibleMouse = null;
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      if (c.hover) {
        modificadorSensibleMouse = c.getHover_modificador();
      }
    }
    return modificadorSensibleMouse;
  }

  color getColorSensibleMouse() {
    color colorSensibleMouse = color(0);
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);
      if (c.hover) {
        colorSensibleMouse = c.col;
      }
    }    
    return colorSensibleMouse;
  }

  //------------------------------------------------
  void imprimirMaquinaria() {
    String maquinaria = "";
    for (int i=0; i<categorias.size (); i++) {
      Categoria c = (Categoria)categorias.get(i);  
      c.setSensible(false);

      for (int j=0; j<c.modificadores.size (); j++) {
        Modificador m = (Modificador)c.modificadores.get(j); 
        if (m.mods>0) {
          maquinaria = maquinaria.equals("")?maquinaria+m.nombre:maquinaria+"|"+m.nombre;
        }
      }
    }
    println(maquinaria);
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
