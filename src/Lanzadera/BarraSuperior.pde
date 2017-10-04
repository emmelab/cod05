class BarraSuperior implements AutoDraw {
  float margen, alto;
  PImage marca, ayuda, fondoIp;

  BarraSuperior() {
    marca = iconos.get(dicIcos.marca);
    ayuda = iconos.get(dicIcos.ayuda);
    fondoIp = iconos.get(dicIcos.fondoIp);
    
    fondoIp.resize(fondoIp.width/5,fondoIp.height/5);
    
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
      //fill(paleta.ips[0]);
      noTint();
      imageMode(CORNER);
      textSize(27);
      image(fondoIp,0,alto+fondoIp.height/2.2);
      fill(paleta.fondo);
      text(oscP5.ip(), margen, alto+48);
      popStyle();   
  }
}