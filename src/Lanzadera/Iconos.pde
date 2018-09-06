class DiccionarioIconos {
  String
    aroCerrado = "aro-cerrado", 
    aroAbierto = "aro-abierto", 
    play = "play", 
    marca = "marca", 
    mas = "mas", 
    menos = "menos", 
    ayuda = "ayuda", 
    observador = "observador", 
    carrete = "carrete", 
    lienzo = "lienzo", 
    conexion = "conexion";
}

class Iconos implements AutoSetup {
  PImage iconoVacio_ref;
  HashMap<String, PImage> iconos;

  String[] preload = {
    dicIcos.lienzo, 
    dicIcos.carrete, 
    dicIcos.observador, 
    dicIcos.play, 
    dicIcos.aroCerrado, 
    dicIcos.aroAbierto,
    dicIcos.mas,
    dicIcos.menos,
  };

  Iconos() {
    autoSetup.add(this);
  }
  void setup() {
    iconos = new HashMap<String, PImage>();
    for (int i=0; i<preload.length; i++) {
      PImage icono = loadImage("../iconos/"+ preload[i]+".png");         
      iconos.put(preload[i], icono);
    }
    debug( true );
  }

  PImage iconoVacio() {
    if (iconoVacio_ref ==null) {
      PGraphics graf = createGraphics(32, 32);
      graf.beginDraw();
      graf.background(0);
      graf.noStroke();
      graf.fill(255);
      graf.ellipse(graf.width/2, graf.height/2, graf.width, graf.height);
      graf.fill(0);
      graf.textAlign(CENTER);
      graf.textSize(8);
      graf.text("Icono\nperdido", graf.width/2, graf.height/2);
      graf.endDraw();
      iconoVacio_ref = graf.get();
      iconoVacio_ref.mask(iconoVacio_ref);
    }
    return iconoVacio_ref;
  }

  PImage get(String nombre) {
    if (iconos == null) {
      println("Error: pidiendo icono antes de ser cargado -> "+nombre);    
      return null;
    } else {
      PImage icono = iconos.get(nombre);
      if (icono==null) {
        icono = loadImage("../iconos/"+ nombre+".png");
        if (icono == null) {
          println(nombre+ " no encontrado");
          icono = iconoVacio();
        }
        iconos.put(nombre, icono);
      }
      return icono;
    }
  }

  void dibujar(String nombre, float x, float y) {
    imageMode(CENTER);
    PImage icono = iconos.get(nombre);
    if (icono!=null)
      image(icono, x, y);
    else {
      icono = loadImage("../iconos/"+ nombre+".png");
      iconos.put(nombre, icono);
      if (icono != null) image(icono, x, y);
      else {
        println(nombre+ " no encontrado");
        iconos.put(nombre, iconoVacio());
        image(icono, x, y);
      }
    }
    debug( false );
  }
  
    //Implementaciones Debug
  void debug( boolean setup ){
    if( setup ) consola.printlnAlerta( "Construccion -> Iconos <- Interfaces: " + getImplementaciones() );
    else consola.println( "Iconos->Interfaces: " + getImplementaciones() );
  }
  /*
  void debug(){
    consola.printlnAlerta( "Iconos->Interfaces: " + getImplementaciones() );
  }*/
  
  String getImplementaciones(){
    return "AutoSetup";
  }
}
