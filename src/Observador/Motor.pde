import SimpleOpenNI.*;

class Motor{

  SimpleOpenNI  context;
  
  HashMap<Integer, UsuarioDesequilibrio> desequilibrios = new HashMap();
  HashMap<Integer, UsuarioNivel> niveles = new HashMap();
  HashMap<Integer, UsuarioCerrado> cerrados = new HashMap();
  
  int[] tiposDeJoint;
  int[][] paresDeJoints;
  String[] nombreDeJoint;
  
  ComunicacionOSC comunicacionOSC;
  
  PGraphics espacio3D;
  
  Interfaz interfaz;
    
  int estado;
  final int CAMARA_COMUN = 0;
  final int DEBUG_DESEQUILIBRIO = 1;
  final int DEBUG_NIVEL = 2;
  final int DEBUG_CERRADO = 3;
  
  final String[] NOMBRE_ESTADO = { "Camara Comun", "Debug Desequilibrio", "Debug Nivel", "Debug Cerrado" };
  
  //------------------------------------------------------- CONSTRUCTOR
  Motor(){

    context = new SimpleOpenNI( p5 );//,sketchPath("recorduno.oni"));
    
    if (context.isInit() == false)
    {
      println("No se pudo iniciar SimpleOpenNI, quizas la camara esta desconectada!"); 
      exit();
      return;
    }
  
    context.enableDepth();
    context.enableUser();
    
    tiposDeJoint = getTiposDeJoint();
    paresDeJoints = getParesDeJoints();
    nombreDeJoint = getNombreDeJoint();
    
    comunicacionOSC = new ComunicacionOSC();
    
    iniciarEspacio3D();
    
    interfaz = new Interfaz( NOMBRE_ESTADO );
        
    estado = CAMARA_COMUN;
    
  }
  
  void iniciarEspacio3D(){
    espacio3D = createGraphics( context.depthImage().width, context.depthImage().height, P3D );
    espacio3D.beginDraw();
    espacio3D.translate(width/2, height/2, 0);
    //espacio3D.lights();// esto moverlo al loop cuando sepa donde queda lindo y prolijito xD
    espacio3D.rotateX(PI);
    espacio3D.translate(0, 0, -1000);
    espacio3D.translate(0, 0, width*2);
    espacio3D.endDraw();
  }
  
  //------------- sets
  
  void setEstado( int e ){
    estado = e;
    interfaz.mostrarInterfazCorrespondiente( estado );
  }
  
  //------------------------------------------------------- ACTUALIZA Y DIBUJA
  void ejecutar(){

    context.update();
    
    background( 128, 128, 222 );
    
    espacio3D.beginDraw();
    espacio3D.background( 128, 128, 222 );
    
    espacio3D.translate(width/2, height/2, 0);
    espacio3D.lights();
    espacio3D.rotateX(PI);
    espacio3D.translate(0, 0, -1000);
    espacio3D.translate(0, 0, width*2);    
    
    
    int[] userList = context.getUsers();
    for (int i=0; i<userList.length; i++)
    {
      
      desequilibrio(  userList[i] );
  
      nivel( userList[i], 20 );
  
      cerrado( userList[i], 20 );
      
      comunicacionOSC.actualizarMensajes( userList[i], this ); //<>//
      
    }

    espacio3D.endDraw();
        
    if( estado == CAMARA_COMUN ) image( context.userImage(), 0, interfaz.barra.getHeight() + 30 );
    
    else if( estado == DEBUG_NIVEL || estado == DEBUG_CERRADO ){
      image( espacio3D, width*0.5 - espacio3D.width*0.5, height*0.5 - espacio3D.height*0.5 );
      image(context.userImage(), 0, interfaz.barra.getHeight(), context.depthWidth()/3, context.depthHeight()/3);
    }

    
  }
  
  void desequilibrio(  int usuario ) {  //un usuario desequilibrio, numero de usuario
    UsuarioDesequilibrio unUDesiq = desequilibrios.get(usuario);
    unUDesiq.actualizar();
    
    if( estado == DEBUG_DESEQUILIBRIO ){
      
      dibujarDebugDesequilibrio( unUDesiq, p5.g, 0, context.depthHeight()/4, 
      context.depthWidth(), context.depthHeight());

    }

  }
  
  void nivel( int usuario, float tam ) {// usuario, tamanio de las esferas
    UsuarioNivel unUNivel = niveles.get(usuario);
    unUNivel.actualizar();
    
    if( context.isTrackingSkeleton( usuario ) ){

      if( estado == DEBUG_NIVEL ){
        dibujarDebugNivel(unUNivel, espacio3D, 200);
        dibujarDebugEsqueleto( context, usuario, espacio3D, tiposDeJoint, paresDeJoints, tam );
      }
      
      
      if( interfaz.interfazDebugNivel.bang_calcularPiso ){
        boolean sePudoCalcular = unUNivel.calcularPiso(tiposDeJoint);
        
        if( sePudoCalcular )
          interfaz.interfazDebugNivel.setUsuarioNivel( unUNivel );
        else
          interfaz.interfazDebugNivel.setUsuarioNivel( null );
        
        interfaz.interfazDebugNivel.bang_calcularPiso = false;
      }
      
    }
    
    /*
    if (contexto.isTrackingSkeleton(usuario))
    {
      if (key=='p' && keyPressed) {
        unUNivel.calcularPiso(joints);
      }
      if (switchK == lugar) {
        dibujarDebugNivel(unUNivel, 200);
        dibujarDebugEsqueleto(contexto, usuario, joints, pJoints, tam);
      }
    }
    */
  }
  
  void cerrado( int usuario, float tam ) {// usuario, tamanio de las esferas
    UsuarioCerrado unUCerrado = cerrados.get(usuario);
    unUCerrado.actualizar();
    
    if( context.isTrackingSkeleton( usuario ) ){
      
      if( estado == DEBUG_CERRADO ){
        dibujarDebugCerrado( unUCerrado, espacio3D );
        dibujarDebugEsqueleto( context, usuario, espacio3D, tiposDeJoint, paresDeJoints, tam );
      }
      
    }
    
  }
  
  void keyPressed(){
    
    if( keyCode == RIGHT ){
      int nuevoEstado = estado + 1;
      if( nuevoEstado > NOMBRE_ESTADO.length - 1 ) estado = 0;
      setEstado( nuevoEstado );
    }else if( keyCode == LEFT ){
      int nuevoEstado = estado - 1;
      if( nuevoEstado < 0 ) estado = NOMBRE_ESTADO.length - 1;
      setEstado( nuevoEstado );
    }
    
  }
  
}
