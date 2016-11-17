void dibujarDebugEsqueleto (SimpleOpenNI curContext, int userId, PGraphics p3d, int[] tipos, int[][] pares, float tam) {
  
  p3d.pushStyle();
  
  p3d.noFill();
  p3d.stroke(#97F5F2);

  PVector pos = new PVector();
  for (int tipoDeJoint : tipos) {
    float confianza = curContext.getJointPositionSkeleton(userId, tipoDeJoint, pos);
    p3d.pushMatrix();
    p3d.translate(pos.x, pos.y, pos.z);

    p3d.ellipse(0, 0, tam*5.6, tam*5.6);
    if (confianza >= .5) {
      p3d.line(-tam, +tam, +tam, -tam);
      p3d.line(+tam, +tam, -tam, -tam);
    }
    if (confianza == 1) {
      p3d.ellipse(0, 0, tam*4.8, tam*4.8);
      p3d.ellipse(0, 0, tam*4, tam*4);
    }
    p3d.popMatrix();
  }
  p3d.stroke(#97F5F2, 120);
  PVector pos2 = new PVector();
  for (int[] par : pares) {
    float confianza1 = curContext.getJointPositionSkeleton(userId, par[0], pos);
    float confianza2 = curContext.getJointPositionSkeleton(userId, par[1], pos2);
    float ang = atan2(pos.y-pos2.y, pos.x-pos2.x) + HALF_PI;
    p3d.pushMatrix();
    p3d.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
    if (confianza2 >= .5) {
      p3d.translate( tam*.5*5.6*cos(ang), tam*.5*5.6*sin(ang) );
      p3d.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
      p3d.translate( -tam*5.6*cos(ang), -tam*5.6*sin(ang) );
      p3d.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
    }
    p3d.popMatrix();
  }
  
  p3d.popStyle();
}
