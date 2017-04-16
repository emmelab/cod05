Iconos iconos = new Iconos();

class Iconos implements AutoSetup{
  PImage iconoVacio_ref;
  HashMap<String, PImage> iconos;

  String[] nombres = {
    "lienzo", "observador", "carrete", "play"
  };

  Iconos() {
    autoSetup.add(this);
  }
  void setup(){
    iconos = new HashMap<String, PImage>();
    for (int i=0; i<nombres.length; i++) {
      PImage icono = loadImage("../iconos/"+ nombres[i]+".png");         
      iconos.put(nombres[i], icono);
    }
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

PImage get(String nombre){
  if (iconos == null){
    println("Error: pidiendo icono antes de ser cargado -> "+nombre);
    return null;
  }
  return iconos.get(nombre);
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
  }
}