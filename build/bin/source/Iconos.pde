class Iconos {
  HashMap<String, PImage> iconos;

  String[] nombres = {
    "Colision Con Joint", "Aplicar Colisiones", "Colision Simple", "Dibujar Reas", "Varios", 
    "Vizualizar Particulas", "Dibujar Circulo", "Aplicar Fuerza", "Atraccion Al Centro", "Friccion Global", 
    "Escena", "Dibujar Flecha", "Espacio Cerrado", "Espacio Toroidal", "Dibujar Rastro Circular", 
    "Rastro Normal", "Rastro Elastico", "Forma De Rastro", "Dibujar Rastro Triangular", "Dibujar Rastro", 
    "Flock Separacion", "Flock Cohesion", "Flocking", "Transparencia", "Alfa Segun Velocidad", 
    "Aplicar Movimiento", "Dibujar Rastro Lineal", "Reset Lluvia", "Fuerzas Por Semejanza", "Flock Alineamiento", 
    "Reset Lluvia", "Fuerzas Por Semejanza", "Mover", "Gravedad"
  };

  Iconos() {
    iconos = new HashMap<String, PImage>();
    for (int i=0; i<34; i++) {
      PImage icono = loadImage("icono ("+(i+1)+").png");         
      iconos.put(nombres[i], icono);
    }
    /* PImage icono;
     icono = loadImage("icono (29).png"); 
     iconos.put( nombres[31], icono); 
     icono = loadImage("icono (30).png"); 
     iconos.put( nombres[32], icono);
     icono = loadImage("icono (27).png");
     iconos.put( nombres[33], icono);*/
  }

  Iconos(int t) {
    iconos = new HashMap<String, PImage>();
    for (int i=0; i<34; i++) {
      PImage icono = loadImage("iconos/blancos/icono ("+(i+1)+").png");  
      icono.resize(t, t);      
      iconos.put(nombres[i], icono);
     
    }
    /* PImage icono;
     icono = loadImage("iconos/icono (29).png"); 
     icono.resize(t, t);  
     iconos.put( nombres[31], icono); 
     icono = loadImage("iconos/icono (30).png"); 
     icono.resize(t, t);  
     iconos.put( nombres[32], icono);
     icono = loadImage("iconos/icono (27).png");
     icono.resize(t, t);  
     iconos.put( nombres[33], icono);*/
  }

  void dibujar(String nombre, float x, float y) {
    imageMode(CENTER);
    PImage icono = iconos.get(nombre);
    if (icono!=null)
      image(icono, x, y);
    else
      println(nombre+ " no sirve por alguna razon");
  }
}