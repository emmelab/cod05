void dibujarDebugDesequilibrio ( UsuarioDesequilibrio deseq, PGraphics g2d, float x, float y, float w, float h ) {
  
  g2d.pushStyle();
  
  PImage uMap = deseq.context.userImage();
  PVector offset = new PVector(uMap.width/2 - deseq.centroMasa2D.x , uMap.height/2 - deseq.centroMasa2D.y);
  
  uMap.copy(uMap, 0, 0, uMap.width, uMap.height,
  floor(offset.x) , floor(offset.y), uMap.width, uMap.height);
  
  g2d.image(uMap, x,y,w,h);
  
  g2d.noStroke();
  g2d.fill( deseq.izquierda || deseq.derecha ? 255:0 , deseq.izquierdaBruto || deseq.derechaBruto ? 255:0 , 255,110);
  g2d.rectMode(CORNERS);
  g2d.rect( x+w/2 ,y, x+w/2 -deseq.centroMasa2D.x+deseq.centroCaja.x ,y+h);
  
  g2d.stroke(#1FFF49);
  g2d.line(x + w/2, y , x+w/2, y+h);
  g2d.line(x , y+h/2 , x+w, y+h/2);
  
  g2d.stroke(#1D9534);
  float tamSegmento = 10;
  for (float offY = 0; offY < h; offY += tamSegmento*2) {
    g2d.line(x + w/2 - deseq.umbralMenor , y + offY , x + w/2 - deseq.umbralMenor , min(y + offY+tamSegmento, y+h));
    g2d.line(x + w/2 + deseq.umbralMenor , y + offY , x + w/2 + deseq.umbralMenor , min(y + offY+tamSegmento, y+h));
  }
  //line(x + w/2 - deseq.umbralMenor , y , x + w/2 - deseq.umbralMenor , y+ h);
  //line(x + w/2 + deseq.umbralMenor , y , x + w/2 + deseq.umbralMenor , y+ h);
  g2d.line(x + w/2 - deseq.umbralMaximo , y , x + w/2 - deseq.umbralMaximo , y+ h);
  g2d.line(x + w/2 + deseq.umbralMaximo , y , x + w/2 + deseq.umbralMaximo , y+ h);
  
  g2d.noStroke();
  
  //if (g.is3D())  translate(0,0,1);
  g2d.fill(0);
  g2d.rectMode(CENTER);
  g2d.textAlign(CENTER,CENTER);
  g2d.rect(x + w/2 , y+h*.05 , w*.18 , h*.1);
  g2d.fill(0,255,0);
  g2d.textSize(h*.07);
  g2d.text(nfs(deseq.desequilibrio,1,2),x+w/2, y + h*.05);
  //if (g.is3D())translate(0,0,-1);
  
  g2d.popStyle();

}
