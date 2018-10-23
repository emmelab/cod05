void dibujarDebugNivel (UsuarioNivel nivel, PGraphics p3d, int tam) {
  
  p3d.pushStyle();
  
  p3d.fill(#4D5CF0,150);
  //p3d.stroke(#2731A0);
  p3d.noStroke();
  //p3d.hint(DISABLE_DEPTH_MASK);
  
  p3d.pushMatrix();
  p3d.translate(nivel.centroMasa.x,nivel.piso.y,nivel.centroMasa.z);
  p3d.rect(-tam/16, nivel.centroMasa.y - nivel.piso.y, tam/8, - nivel.centroMasa.y + nivel.piso.y);
  p3d.rotateX(-HALF_PI);
  p3d.fill(#4D5CF0);
  p3d.ellipse(0,0,tam*2,tam*2);
  p3d.translate(0,0,+nivel.umbralBajo -nivel.piso.y);
  p3d.fill(nivel.bajoBruto? 255:0 , nivel.bajo? 255:0, 255);
  p3d.box(tam,tam,tam/10);//ellipse(0,0,tam,tam);
  p3d.translate(0,0,+nivel.umbralAlto -nivel.umbralBajo);
  p3d.fill(nivel.bajoBruto || nivel.medioBruto ? 255:0 , nivel.bajo || nivel.medio ? 255:0, 255);
  p3d.box(tam,tam,tam/10);//ellipse(0,0,tam,tam);
  p3d.popMatrix();
  
  //p3d.hint(ENABLE_DEPTH_MASK);
  
  p3d.popStyle();
}
