class Iconos {
  HashMap<String, PImage> iconos;
float tamanioTexto;
  String[] nombres = {
    "Sin Imagen", "Colision Con Joint", "Aplicar Colisiones", "Colision Simple", "Dibujar Reas", "Varios", 
    "Vizualizar Particulas", "Dibujar Circulo", "Aplicar Fuerza", "Atraccion Al Centro", "Friccion Global", 
    "Escena", "Dibujar Flecha", "Espacio Cerrado", "Espacio Toroidal", "Dibujar Rastro Circular", 
    "Rastro Normal", "Rastro Elastico", "Forma De Rastro", "Dibujar Rastro Triangular", "Dibujar Rastro", 
    "Flock Separacion", "Flock Cohesion", "Flocking", "Transparencia", "Alfa Segun Velocidad", 
    "Aplicar Movimiento", "Dibujar Rastro Lineal", "Reset Lluvia", "Fuerzas Por Semejanza", "Flock Alineamiento", 
    "Reset Lluvia", "Fuerzas Por Semejanza", "Mover", "Gravedad", "maquinarias", "espera", "opciones", "eliminar", 
    "agregar", "Lumiere", "Cohl", "Melies", "Guy Blache", "Estimulos"
  };

  Iconos() {
    iconos = new HashMap<String, PImage>();
    PImage imagenVacia = imagenVacia();
    iconos.put(nombres[0], imagenVacia);
    for (int i=1; i<45; i++) {
      PImage icono = loadImage("icono ("+(i)+").png");         
      iconos.put(nombres[i], icono);
      tamanioTexto = 12;
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
    PImage imagenVacia = imagenVacia();
    imagenVacia.resize(t, t);      
    iconos.put(nombres[0], imagenVacia);
    for (int i=1; i<45; i++) {
      PImage icono = loadImage("iconos/blancos/icono ("+(i)+").png");  
      icono.resize(t, t);      
      iconos.put(nombres[i], icono);
      tamanioTexto = t/3;
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

  PImage imagenVacia() {
    PImage img = createImage(100, 100, ARGB);
    img.loadPixels();
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 100; j++) {
        int pixelIndex = i+j*img.width;
        if (((i-j)<4 && (i-j)>-4 && dist(i, j, 50, 50) <= 50) || (dist(i, j, 50, 50) <= 50 && dist(i, j, 50, 50)>50-4) ) {
          img.pixels[pixelIndex] = color(255);
        } else  /*if (dist(i, j, 50, 50)<50) {
         img.pixels[pixelIndex] = color(255);
         } else */ {
          img.pixels[pixelIndex] = color(255, 0);
        }
      }
    }
    img.updatePixels();
    return img;
  }

  void dibujar(String nombre, float x, float y) {
    imageMode(CENTER);
    PImage icono = iconos.get(nombre);
    if (icono!=null) {
      image(icono, x, y);
    } else {
      icono = iconos.get("Sin Imagen");
      image(icono, x, y);
      if (nombre!=null) {
        pushStyle();
        fill(255);
        textAlign(CENTER, CENTER);
        textSize(tamanioTexto);
        text(nombre, x, y);
        popStyle();
      }
    }
  }
}

