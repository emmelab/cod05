class InterfazModoObservador extends Auto implements AutoDraw {
  //boolean activo = true;
  PImage icoCam,icoKin,fondoToggle;
  
  BotonBasico botonCam, botonKin;
  ConfiguracionCOD05 config;
  
  InterfazModoObservador() {
    autoDraw.add(this);
    autoActivo = true;
    
    fondoToggle = iconos.get(dicIcos.fondoToggle);
    icoCam = iconos.get(dicIcos.webcam);
    icoKin = iconos.get(dicIcos.kinect);
    icoCam.resize(icoCam.width*3/4,icoCam.height*3/4);
    icoKin.resize(icoKin.width*3/4,icoKin.height*3/4);
    fondoToggle.resize(fondoToggle.width*3/4,fondoToggle.height*3/4);
    
    botonCam = new BotonBasico( width*0.4, height*0.5, 0, dicIcos.webcam, paleta.inactivo );
    botonCam.escala = 1.0f;
    botonCam.hoverEscala = new TwOutBack().inicializar(.25, 1, 1.1, 0);
    botonCam.toggleAlfa = new TwOutQuad().inicializar(.25, 255, 25, 0);
    botonCam.setAutoActivo( true );
    
    botonKin = new BotonBasico( width*0.6, height*0.5, 0, dicIcos.kinect, paleta.inactivo );
    botonKin.escala = 1.0f;
    botonKin.hoverEscala = new TwOutBack().inicializar(.25, 1, 1.1, 0);
    botonKin.toggleAlfa = new TwOutQuad().inicializar(.25, 255, 25, 0);
    botonKin.setAutoActivo( true );

  }
  
  void set( ConfiguracionCOD05 config ){
    this.config = config;
  }
  
  void reset(){
    botonCam.toggle = false;
    botonKin.toggle = false;
    setAutoActivo( true );
  }
  
  void draw() {
    if ( autoActivo ) {
      imageMode(CENTER);
      noStroke();
      fill(paleta.panelSuperior);
      ellipse( botonCam.pos.x, botonCam.pos.y, icoCam.width*botonCam.escala, icoCam.height*botonCam.escala );
      ellipse( botonKin.pos.x, botonCam.pos.y, icoKin.width*botonKin.escala, icoKin.height*botonKin.escala );
      
      if( botonCam.toggle ){
        setAutoActivo( false );
        interfaz.barraSuperior.botonAtras.setAutoActivo( true );
        config.modoObservador = ModoObservador.WEBCAM;
        interfaz.observador.setIcono( dicIcos.webcam, 0.84 );
      }else if( botonKin.toggle ){
        setAutoActivo( false );
        interfaz.barraSuperior.botonAtras.setAutoActivo( true );
        config.modoObservador = ModoObservador.KINECT;
        interfaz.observador.setIcono( dicIcos.kinect, 0.84 );
      }
      
    }
    
  }
  
  void setAutoActivo( boolean autoActivo ){
    super.setAutoActivo( autoActivo );
    botonCam.setAutoActivo( autoActivo );
    botonKin.setAutoActivo( autoActivo );
  }
  
}
