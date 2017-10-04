class InterfazModoObservador implements AutoDraw {
  boolean activo = true;
  PImage icoCam,icoKin,fondoToggle;
  
  InterfazModoObservador() {
    autoDraw.add(this);
    
    fondoToggle = iconos.get(dicIcos.fondoToggle);
    icoCam = iconos.get(dicIcos.webcam);
    icoKin = iconos.get(dicIcos.kinect);
    icoCam.resize(icoCam.width*3/4,icoCam.height*3/4);
    icoKin.resize(icoKin.width*3/4,icoKin.height*3/4);
    fondoToggle.resize(fondoToggle.width*3/4,fondoToggle.height*3/4);
  }
  
  void draw() {
    if (activo) {
      imageMode(CENTER);
      noStroke();
      fill(paleta.panelSuperior);
      tint(paleta.fondo);
      ellipse(width/4,height/2,icoCam.width-3,icoCam.height-3);
      image(icoCam,width/4,height/2);
      ellipse(width*3/4,height/2,icoKin.width-3,icoKin.height-3);
      image(icoKin,width*3/4,height/2);
      //ellipse(width/4,height/2,icoCam.width-1,icoCam.height-1);
      //image(fondoToggle,width/2,height/2);
    }
    
  }
}