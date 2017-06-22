class BarraSuperior implements AutoDraw {
  float margen,alto;
  PImage marca, ayuda;

  BarraSuperior() {
    marca = iconos.get(dicIcos.marca);
    ayuda = iconos.get(dicIcos.ayuda);
    alto = marca.height*1.5;
    margen = alto/2;
    autoDraw.add(this);
  }

  void draw() {
    pushStyle();
    noStroke();
    imageMode(CENTER);
    fill(paleta.panelSuperior);
    tint(paleta.inactivo);
      rect(0, 0, width, alto);
      image(marca, marca.width/2+ margen/2, margen);
      image(ayuda, width - ayuda.width/2 - margen/2, margen);
    fill(paleta.ips[0]);
    textSize(32);
    text(oscP5.ip(),margen/2,alto+48);
    popStyle();
  }
}