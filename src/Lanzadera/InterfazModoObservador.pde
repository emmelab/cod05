class InterfazModoObservador extends Auto implements AutoDraw, AutoKeyPressed {
  //boolean activo = true;
  PImage icoCam,icoKin,fondoToggle;
  
  BotonBasico botonCam, botonKin;
  
  InterfazModoObservador( String nombre ) {
    autoDraw.add(this);
    autoKeyPressed.add( this );
    autoActivo = true;
    
    fondoToggle = iconos.get(dicIcos.fondoToggle);
    icoCam = iconos.get(dicIcos.webcam);
    icoKin = iconos.get(dicIcos.kinect);
    icoCam.resize(icoCam.width*3/4,icoCam.height*3/4);
    icoKin.resize(icoKin.width*3/4,icoKin.height*3/4);
    fondoToggle.resize(fondoToggle.width*3/4,fondoToggle.height*3/4);
    
    botonCam = new BotonBasico( width*2.5/8, height*0.5, 0, dicIcos.webcam, color( 255 ) );
    botonCam.escala = 1.0f;
    botonCam.hoverEscala = new TwOutBack().inicializar(.25, 1, 1.1, 0);
    botonCam.toggleAlfa = new TwOutQuad().inicializar(.25, 255, 25, 0);
    botonCam.setAutoActivo( true );
    
    botonKin = new BotonBasico( width*5.5/8, height*0.5, 0, dicIcos.kinect, color( 255 ) );
    botonKin.escala = 1.0f;
    botonKin.hoverEscala = new TwOutBack().inicializar(.25, 1, 1.1, 0);
    botonKin.toggleAlfa = new TwOutQuad().inicializar(.25, 255, 25, 0);
    botonKin.setAutoActivo( true );
    
    setNombre( nombre );
    debug( true );
  }
  
  void draw() {
    if ( autoActivo ) {
      imageMode(CENTER);
      noStroke();
      fill(paleta.panelSuperior);
      //tint(paleta.fondo);
      ellipse(width*2.5/8,height/2,icoCam.width-3,icoCam.height-3);
      //image(icoCam,width*2.5/8,height/2);
      ellipse(width*5.5/8,height/2,icoKin.width-3,icoKin.height-3);
      //image(icoKin,width*5.5/8,height/2);
      //ellipse(width/4,height/2,icoCam.width-1,icoCam.height-1);
      //image(fondoToggle,width/2,height/2);
      
      if( botonCam.toggle ){
        setAutoActivo( false );
      }else if( botonKin.toggle ){
        setAutoActivo( false );
      }
      
    }
    debug( false );
  }
  
  void setAutoActivo( boolean autoActivo ){
    super.setAutoActivo( autoActivo );
    botonCam.setAutoActivo( autoActivo );
    botonKin.setAutoActivo( autoActivo );
  }
  
  void keyPressed(){
    if( key == 'q' || key == 'Q' ) autoActivo = false;
  }
  
    //Implementaciones Debug
  void debug( boolean setup ){
    if( setup ) consola.printlnAlerta( "Construccion -> InterfazModoObservador <- Interfaces: " + getImplementaciones() );
    else consola.println( "InterfazModoObservador->Interfaces: " + getImplementaciones() );
  }
  
  String getImplementaciones(){
    return "AutoDraw";
  }
}
