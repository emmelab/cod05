class Motor{
  
  private PApplet p5;
  private SimpleOpenNI kinect;
  
  private int estado;
  public static final int CAMARA_COMUN = 0;
  public static final int DEBUG_DESEQUILIBRIO = 1;
  public static final int DEBUG_NIVEL = 2;
  public static final int DEBUG_CERRADO = 3;
  public static final int DEBUG_ESPALDA = 4;
  public static final int DEBUG_VELOCIDAD = 5;
  
  public final String[] NOMBRE_ESTADO = { "Camara Comun", "Desequilibrio", "Nivel", "Cerrado", "Espalda", "Velocidad" };
  
  private GuiP5 guiP5;
  
  private PGraphics espacio3D;
  private boolean dibujarEspacio3D;
  
  private HashMap<Integer, Usuario> usuarios = new HashMap <Integer, Usuario> ();
  
  public int[] tiposDeJoint;
  public int[][] paresDeJoints;
  public String[] nombreDeJoint;
  
  private final float FACTOR_VENTANA = 0.8;
  private final int VENTANA_POSICION_Y;
  
  public Motor( PApplet p5 ){
    
    this.p5 = p5;
    
    kinect = new SimpleOpenNI( p5 );//,sketchPath("recorduno.oni"));
    
    if ( !kinect.isInit() ) {
      println("No se pudo iniciar SimpleOpenNI, quizas la camara esta desconectada!"); 
      //exit();
      //return;
    }else{
  
      kinect.enableDepth();
      kinect.enableUser();
      
      tiposDeJoint = getTiposDeJoint();
      paresDeJoints = getParesDeJoints();
      nombreDeJoint = getNombreDeJoint();
      
      for( int i = 0; i < tiposDeJoint.length; i++ ){
        println( "- " + nombreDeJoint[ i ] + " : " + tiposDeJoint[ i ] );
      }
      
      loadDatosXML();
      
      guiP5 = new GuiP5( p5, NOMBRE_ESTADO );
      
      iniciarEspacio3D();
    
    }
    
    VENTANA_POSICION_Y = ( kinect.isInit() )? 84 + round( ( height - 84 - kinect.userImage().height * FACTOR_VENTANA ) * 0.5 ) : 0 ;
    
  }
  
  //---------------------------------------- METODOS PUBLICOS
  
  //---- seters y geters
  public void setEstado( int estado ){
    this.estado = estado;
    if( estado == DEBUG_NIVEL || estado == DEBUG_CERRADO || estado == DEBUG_ESPALDA || estado == DEBUG_VELOCIDAD )
      setDibujarEspacio3D( true );
    else
      setDibujarEspacio3D( false );
    print( "Estado: " + this.estado );
    println( " -> " + NOMBRE_ESTADO[ this.estado ] );
  }
  
  public void setDibujarEspacio3D( boolean dibujarEspacio3D ){
    this.dibujarEspacio3D = dibujarEspacio3D;
  }
  
  public int getEstado(){
    return estado;
  }
  //----
  
  public void addUsuario( int idUsuario ){
    
    if( !usuarios.containsKey( idUsuario ) ){
      Usuario nuevoUsuario = new Usuario( kinect, idUsuario, estabilidadGeneral );
      usuarios.put( idUsuario, nuevoUsuario );
    }
    
  }
  
  public void ejecutar(){
    
    background( paleta.grisFondo );
    
    if( kinect.isInit() ){
      guiP5.ejecutar( estado );
      kinect.update(); 
      if( dibujarEspacio3D ) actualizarEspacio3D();
      actualizarUsuarios();
      if( dibujarEspacio3D ) espacio3D.endDraw();
      dibujarCamaraKinect();
    }else{
      fill( 255 );
      textSize( height * 0.04 );
      text( "Kinect no se pudo iniciar.\nAsegurase de que este conectada y reinicie el programa.", 20, height * 0.4 );
    }
  }
  
  public void keyPressed(){
    
    if( keyCode == RIGHT || keyCode == TAB ){
      subirEstado();
    }else if( keyCode == LEFT ){
      bajarEstado();
    }
    
  }
  
  int millisRueda;
  public void mouseWheel( MouseEvent e ){
    float movimiento = e.getCount();
    //println( movimiento );
    
    if( ( millis() - millisRueda ) > 200 ){
      millisRueda = millis();
      
      if( movimiento > 0 ){
        subirEstado();
      }else if( movimiento < 0 ){
        bajarEstado();
      }
      
    }
    
  }
  
  //------------------------------------------------- METODOS PRIVADOS
  
  private void subirEstado(){
    int nuevoEstado = estado + 1;
    nuevoEstado %= NOMBRE_ESTADO.length;
    setEstado( nuevoEstado );
    guiP5.setPestanaActiva( NOMBRE_ESTADO[ estado ] );
  }
  
  private void bajarEstado(){
    int nuevoEstado = estado - 1;
    nuevoEstado = ( nuevoEstado < 0 )? NOMBRE_ESTADO.length - 1 : nuevoEstado ;
    setEstado( nuevoEstado );
    guiP5.setPestanaActiva( NOMBRE_ESTADO[ estado ] );
  }
  
  private void iniciarEspacio3D(){
    espacio3D = createGraphics( kinect.depthImage().width, kinect.depthImage().height, P3D );
    espacio3D.beginDraw();
    espacio3D.translate(width/2, height/2, 0);
    //espacio3D.lights();// esto moverlo al loop cuando sepa donde queda lindo y prolijito xD
    espacio3D.rotateX(PI);
    espacio3D.translate(0, 0, -1000);
    espacio3D.translate(0, 0, width*2);
    espacio3D.endDraw();
  }
  
  
  private void actualizarEspacio3D(){
    espacio3D.beginDraw();
      espacio3D.background( paleta.grisClaro );
      
      espacio3D.translate(width/2, height/2, 0);
      espacio3D.lights();
      espacio3D.rotateX(PI);
      espacio3D.translate(0, 0, -1000);
      espacio3D.translate(0, 0, width*2);
            
  }
  
  private void actualizarUsuarios(){
    int[] userList = kinect.getUsers();
    for (int i=0; i<userList.length; i++)
    {
      
      Usuario u = usuarios.get( userList[i] );
      u.actualizar();
      
      debug( u );
      
      comunicacionOSC.enviarMensajesSISTEMA( u );
      
      if( u.getNivel().getPisoCalculado() )
        comunicacionOSC.enviarMensajesAPI( u );
      
    }
  }
  
  private void debug( Usuario usuario ){
    
    switch( estado ){
      
      case DEBUG_DESEQUILIBRIO:
        desequilibrio( usuario );
        break;
        
      case DEBUG_NIVEL:
        nivel( usuario, 20 );
        break;
        
      case DEBUG_CERRADO:
        cerrado( usuario, 20 );
        break;
        
      case DEBUG_ESPALDA:
        espalda( usuario );
        break;
        
      case DEBUG_VELOCIDAD:
        velocidad( usuario );
        break;
        
      default:
      
        break;
      
    }
    
  }
  
  private void desequilibrio( Usuario usuario ) {
    
    UsuarioDesequilibrio unUDesiq = usuario.getDesequilibrio();
    
    dibujarDebugDesequilibrio( unUDesiq, p5.g, width*0.5 - kinect.userImage().width * 0.5 * FACTOR_VENTANA, VENTANA_POSICION_Y * 0.8, 
    kinect.depthWidth() * FACTOR_VENTANA, kinect.depthHeight() * FACTOR_VENTANA );
    
  }
  
  
  private void nivel( Usuario usuario, float tam ) {// usuario, tamanio de las esferas

    UsuarioNivel unUNivel = usuario.getNivel();
    
    if( kinect.isTrackingSkeleton( usuario.getId() ) && dibujarEspacio3D ){
        dibujarDebugNivel(unUNivel, espacio3D, 200);
        dibujarDebugEsqueleto( kinect, usuario.getId(), espacio3D, tiposDeJoint, paresDeJoints, tam );
    }
    
  }
  
  private void cerrado( Usuario usuario, float tam ) {// usuario, tamanio de las esferas
  
    UsuarioCerrado unUCerrado = usuario.getCerrado();
    
    if( kinect.isTrackingSkeleton( usuario.getId() ) && dibujarEspacio3D ){
      
        dibujarDebugCerrado( unUCerrado, espacio3D );
        dibujarDebugEsqueleto( kinect, usuario.getId(), espacio3D, tiposDeJoint, paresDeJoints, tam );
      
    }
    
  }
  
  private void espalda( Usuario usuario ){
    
    UsuarioEspalda unUEspalda = usuario.getEspalda();
    
    if( kinect.isTrackingSkeleton( usuario.getId() ) && dibujarEspacio3D ){
      
        //dibujarDebugEsqueleto( kinect, usuario.getId(), espacio3D, tiposDeJoint, paresDeJoints, 20 );
        dibujarDebugEspalda( usuario, espacio3D );
      
    }
    
  }
  
  private void velocidad( Usuario usuario ){
    if( kinect.isTrackingSkeleton( usuario.getId() ) && dibujarEspacio3D ){
      dibujarDebugVelocidad( usuario, espacio3D );
      dibujarDebugEsqueleto( kinect, usuario.getId(), espacio3D, tiposDeJoint, paresDeJoints, 20 );
    }
  }
  
  private void dibujarCamaraKinect(){
    
    if( estado == CAMARA_COMUN ){
      
      pushMatrix();
      
        int escalaX = ( comunicacionOSC.getInvertidoEjeX() )? -1 : 1;
        int escalaY = ( comunicacionOSC.getInvertidoEjeY() )? -1 : 1;
        
        scale( escalaX, escalaY );
                
        float posX = ( escalaX == 1 )? width * 0.315 : -width * 0.315 - kinect.userImage().width * FACTOR_VENTANA ;
        float posY = ( escalaY == 1 )? VENTANA_POSICION_Y : -VENTANA_POSICION_Y - kinect.userImage().height * FACTOR_VENTANA ;
        
        image( kinect.userImage(), posX, posY, kinect.userImage().width * FACTOR_VENTANA, kinect.userImage().height * FACTOR_VENTANA);
        
      popMatrix();
      
    }
    
    else if( dibujarEspacio3D ){
      image( espacio3D, width*0.5 - espacio3D.width * 0.5 * FACTOR_VENTANA, VENTANA_POSICION_Y * 0.8, espacio3D.width * FACTOR_VENTANA, espacio3D.height * FACTOR_VENTANA );
      image(kinect.userImage(), width*0.5 - espacio3D.width * 0.5 * FACTOR_VENTANA, VENTANA_POSICION_Y * 0.8, kinect.depthWidth() * 0.25, kinect.depthHeight() * 0.25);
    }
  }
  
}