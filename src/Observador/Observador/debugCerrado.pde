void dibujarDebugCerrado ( UsuarioCerrado cerrado, PGraphics p3d ) {
  
  p3d.pushStyle();
  
  p3d.fill(cerrado.cerradoBruto?255:0, cerrado.cerrado?255:0, 255, 150);
  p3d.noStroke();
  p3d.hint(DISABLE_DEPTH_MASK);
  p3d.pushMatrix();
  p3d.translate(cerrado.centroPromedio.x, cerrado.centroPromedio.y, cerrado.centroPromedio.z);
  p3d.sphere(cerrado.umbralCerrado);
  p3d.popMatrix();
  p3d.hint(ENABLE_DEPTH_MASK);
  for (int j : cerrado.jointsExtremidades) {
    p3d.pushMatrix();
    p3d.translate(cerrado.centroPromedio.x, cerrado.centroPromedio.y, cerrado.centroPromedio.z);
    PVector p = PVector.sub(cerrado.joints.get(j), cerrado.centroPromedio);
    p.normalize();
    p.mult(cerrado.umbralCerrado*3);
    p3d.stroke(0, 100);
    p3d.line(0, 0, 0, p.x, p.y, p.z);
    p.div(3);
    p3d.stroke( PVector.dist(cerrado.joints.get(j), cerrado.centroPromedio) < cerrado.umbralCerrado || cerrado.confianza.get(j) <= .5 ? 255:0, 0, 255);
    p3d.line(0, 0, 0, p.x, p.y, p.z);
    p3d.popMatrix();
  }
  
  p3d.popStyle();
  
  /*
  pushMatrix();
   translate(nivel.centroMasa.x,nivel.piso.y,nivel.centroMasa.z);
   rect(-tam/16, nivel.centroMasa.y - nivel.piso.y, tam/8, - nivel.centroMasa.y + nivel.piso.y);
   rotateX(-HALF_PI);
   fill(#4D5CF0);
   ellipse(0,0,tam*2,tam*2);
   translate(0,0,+nivel.umbralBajo -nivel.piso.y);
   fill(nivel.bajoBruto? 255:0 , nivel.bajo? 255:0, 255);
   box(tam,tam,tam/10);//ellipse(0,0,tam,tam);
   translate(0,0,+nivel.umbralAlto -nivel.umbralBajo);
   fill(nivel.bajoBruto || nivel.medioBruto ? 255:0 , nivel.bajo || nivel.medio ? 255:0, 255);
   box(tam,tam,tam/10);//ellipse(0,0,tam,tam);
   popMatrix();*/
}
