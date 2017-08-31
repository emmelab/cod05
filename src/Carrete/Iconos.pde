//---- PARA LOS ICONOS:
//---- primero que las iamgenes tengan los nombres y no tenerlos todos aqui
//---- entonces el nombre sale de las iamgenes y no al reves
//---- segundo que haya un contructor donde le pasas un arreglo de nombres y te 
//---- crea un hashmap con lso iconos solo de esos string que encesesita asi no usa 
//---- espacio de memoria de gana
//---- tercero poner en lsonombre de lso iconos si son modificadores categorias u otros
//---- asi hay otor contructor al que le pasas solo un string diciendole modificadores o categorias
//---- y te pasa el hashmap de es gran conglomerado de cosas... por la misma razon que el anterior.
//---- como lso nombres estos tiene que coincidir con lso de los modificadores de el lienzo hay que
//---- crea algo para dividir las iamgenes algo como mod/nombre_del_mod.png asi se peude ahce run 
//---- split primeor por el '.' pra extraer el modo y segundo por el '/' pARA SACAr solo el nombre y 
//---- si es un modificador.

class Iconos {
  HashMap<String, PImage> iconos;
  float tamanioTexto;
  int w, h;
  String[] nombres = {
    "Sin Imagen", "Colision Con Joint", "Aplicar Colisiones", "Colision Simple", "Dibujar Reas", "Varios", 
    "Vizualizar Particulas", "Dibujar Circulo", "Aplicar Fuerza", "Atraccion Al Centro", "Friccion Global", 
    "Escena", "Dibujar Flecha", "Espacio Cerrado", "Espacio Toroidal", "Dibujar Rastro Circular", 
    "Rastro Normal", "Rastro Elastico", "Forma De Rastro", "Dibujar Rastro Triangular", "Dibujar Rastro", 
    "Flock Separacion", "Flock Cohesion", "Flocking", "Transparencia", "Alfa Segun Velocidad", 
    "Aplicar Movimiento", "Dibujar Rastro Lineal", "Reset Lluvia", "Fuerzas Por Semejanza", "Flock Alineamiento", 
    "Reset Lluvia", "Fuerzas Por Semejanza", "Mover", "Gravedad", "maquinarias", "espera", "opciones", "eliminar", 
    "agregar", "Lumiere", "Cohl", "Melies", "Guy Blache", "Estimulos", "Alfa Segun Cercania", 
    "Atraccion Al Torso", "Mod Fuerzas Por Semejanza", "Egoespacio", "Paleta Color", "Paleta Personalizada", "Paleta Default", 
    "Dibujar Rastro Cuadrado", "Dibujar Rastro Shape", "Dibujar Cuadrado", "cod05 1", "cod05 2", ABIERTO, CERRADO, N_MEDIO, 
    N_ALTO, N_BAJO, EJE_DERECHA, EJE_IZQUIERDA, EJE_CENTRO, MONITOR_BASE
  };

  Iconos(int t) {
    iconos = new HashMap<String, PImage>();
    PImage imagenVacia = imagenVacia();
    imagenVacia.resize(t, t);      
    iconos.put(nombres[0], imagenVacia);
    for (int i=1; i<66; i++) {
      PImage icono = loadImage("iconos/blancos/icono ("+(i)+").png");  
      int rw = icono.width>=icono.height?t:(icono.width*t/icono.height);
      int rh = icono.height>=icono.width?t:(icono.height*t/icono.width);
      w = rw;
      h = rh;
      icono.resize(rw, rh);      
      iconos.put(nombres[i], icono);
      tamanioTexto = t/3;
    }
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