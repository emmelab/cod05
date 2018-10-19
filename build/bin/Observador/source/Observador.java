import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import SimpleOpenNI.*; 
import oscP5.*; 
import netP5.*; 
import controlP5.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Observador extends PApplet {



ComunicacionOSC comunicacionOSC;
Paleta paleta;
Motor motor;

int estabilidadGeneral = 6;

public void setup(){
  size( 800, 600, P2D );
  
  comunicacionOSC = new ComunicacionOSC( this );
  paleta = new Paleta();
  motor = new Motor(this);

}

public void draw(){
  frame.setTitle( "fps: " + frameRate ); 
  motor.ejecutar();
}

public void keyPressed(){
  motor.keyPressed();
}

public void mouseWheel(MouseEvent e){
  motor.mouseWheel( e );
}

//---------------------------------------------- EVENTOS KINECT
public void onNewUser(SimpleOpenNI curContext, int userId)
{
  println("onNewUser - userId: " + userId);
  println("\tstart tracking skeleton");

  curContext.startTrackingSkeleton(userId);
  
  motor.addUsuario( userId );

}

public void onLostUser(SimpleOpenNI curContext, int userId)
{
  println("onLostUser - userId: " + userId);
  //motor.removerUsuario( userId );
}

public void onVisibleUser(SimpleOpenNI curContext, int userId)
{
}



ConfiguracionCOD05 config;

public class ComunicacionOSC{
  
  private OscP5 oscP5;
  private NetAddress direccionAPI;
  private NetAddress direccionSistema;
  
  private boolean invertidoEjeX, invertidoEjeY;
  
  public ComunicacionOSC( PApplet p5 ){
    
    if (config == null) config = new ConfiguracionCOD05();
    XML xmlConfig = null;
    if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
    if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagEjecucion);
  
    config.cargar(xmlConfig);
    
    //oscP5 = new OscP5( p5, 12300); 
    //direccionAPI = new NetAddress("127.0.0.1", 13000 );
    //direccionSistema = new NetAddress( "127.0.0.1", 12000 );
    
    oscP5 = new OscP5( p5, config.observador.puerto); 
    direccionAPI = new NetAddress( config.carrete.ip, config.carrete.puerto );
    direccionSistema = new NetAddress( config.lienzo.ip, config.lienzo.puerto );
    
    invertidoEjeX = true;
    
  }
  
  //-------------------------------------------------- METODOS PUBLICOS
  
  //---- gets y sets
  public void setInvertidoEjeX( boolean invertidoEjeX ){
    this.invertidoEjeX = invertidoEjeX;
  }
  
  public void setInvertidoEjeY( boolean invertidoEjeY ){
    this.invertidoEjeY = invertidoEjeY;
  }
  
  public boolean getInvertidoEjeX(){
    return invertidoEjeX;
  }
  
  public boolean getInvertidoEjeY(){
    return invertidoEjeY;
  }
  //----
  
  //----- HACIA LA API
  public void enviarMensajesAPI( Usuario usuario ){
    
    UsuarioCerrado uCerrado = usuario.getCerrado();
    UsuarioNivel uNivel = usuario.getNivel();
    UsuarioDesequilibrio uDeseq = usuario.getDesequilibrio();
    
    if (uNivel.entroAlto) enviarMensaje("/nivel",0);
    else if (uNivel.entroMedio) enviarMensaje("/nivel",1);
    else if (uNivel.entroBajo) enviarMensaje("/nivel",2);
    if (uCerrado.abrio) enviarMensaje("/cerrado",0);
    else if (uCerrado.cerro)enviarMensaje("/cerrado",1);
    
    if (uDeseq.desequilibrio <= -1) enviarMensaje("/desequilibrio",0);
    else if (uDeseq.desequilibrio >= 1) enviarMensaje("/desequilibrio",4);
    else if (uDeseq.izquierda) enviarMensaje("/desequilibrio",1);
    else if (uDeseq.derecha) enviarMensaje("/desequilibrio",3);
    else if (uDeseq.salioDerecha || uDeseq.salioIzquierda) enviarMensaje("/desequilibrio",2);
    
    
      if (uDeseq.entroIzquierda) {
        enviarMensajes("/MenuNavegarIzquierda");
      }
      else if (uDeseq.entroDerecha) {
        enviarMensajes("/MenuNavegarDerecha");
      }
      else if (abs(uDeseq.desequilibrio) >= 1) {
        if (frameCount % 12 == 0){
         if (uDeseq.desequilibrio > 0) enviarMensajes("/MenuNavegarDerecha");
         else enviarMensajes("/MenuNavegarIzquierda");
        }
      }
    
    if (/*uNivel.entroMedio ||*/ uNivel.entroBajo) {
      if (uCerrado.cerrado) {
        enviarMensajes("/MenuQuitarModificador");
      }
      else {
        enviarMensajes("/MenuAgregarModificador");
      }
    }
    /*
    else if (uNivel.medio || uNivel.bajo) {
      if (uDeseq.entroIzquierda) {
        enviarMensajes("/MenuNavegarIzquierda");
      }
      else if (uDeseq.entroDerecha) {
        enviarMensajes("/MenuNavegarDerecha");
      }
      else if (abs(uDeseq.desequilibrio) >= 1) {
        if (frameCount % 12 == 0){
         if (uDeseq.desequilibrio > 0) enviarMensajes("/MenuNavegarDerecha");
         else enviarMensajes("/MenuNavegarIzquierda");
        }
      }
    }
    */
    else if (uNivel.entroAlto) {
      if (uCerrado.cerrado) {
        enviarMensajes("/Cancelar");
      }
      else {
        enviarMensajes("/Aceptar");
      }
    }
    
    /*
    PVector jointCursor = new PVector();
    
    m.kinect.getJointPositionSkeleton( user, SimpleOpenNI.SKEL_RIGHT_HAND, jointCursor );
  
    float xCursor = m.espacio3D.screenX( jointCursor.x, jointCursor.y, jointCursor.z ) / m.espacio3D.width;
    float yCursor = m.espacio3D.screenY( jointCursor.x, jointCursor.y, jointCursor.z ) / m.espacio3D.height;
    
    enviarMensajeJoint( "/cursor", xCursor, yCursor, direccionAPI );
    */
  }
  
  //----- HACIA EL SISTEMA
  public void enviarMensajesSISTEMA( Usuario usuario ){
    
    PVector[] posicionesJoints = usuario.getPosicionesJoints();
    PVector[] velocidadesJoints = usuario.getVelocidadesJoints();
    float[] confianzasJoints = usuario.getConfianzasJoints();
    
    for( int i = 0; i < posicionesJoints.length; i++ ){
      
      String nombreJoint = Usuario.nombresDeJoints[ i ];
      float x = motor.espacio3D.screenX( posicionesJoints[ i ].x, posicionesJoints[ i ].y, posicionesJoints[ i ].z ) / motor.espacio3D.width;
      float y = motor.espacio3D.screenY( posicionesJoints[ i ].x, posicionesJoints[ i ].y, posicionesJoints[ i ].z ) / motor.espacio3D.height;
      
      //NO ENVIO MAS VELOCIDAD
      //float velocidadX = motor.espacio3D.screenX( velocidadesJoints[ i ].x, velocidadesJoints[ i ].y, velocidadesJoints[ i ].z ) / motor.espacio3D.width;
      //float velocidadY = motor.espacio3D.screenY( velocidadesJoints[ i ].x, velocidadesJoints[ i ].y, velocidadesJoints[ i ].z ) / motor.espacio3D.height;
      
      if( invertidoEjeX ) {
        x = 1 - x;
        //velocidadX = 1 - velocidadX;
      }
      
      if( invertidoEjeY ) {
        y = 1 - y;
        //velocidadY = 1 - velocidadY;
      }
      
      enviarMensajeJoint( "/enviar/usuario/joint", usuario.getId(), nombreJoint, x, y, confianzasJoints[ i ], direccionSistema );
      
    }
    
  }
  
  //-------------------------------------------------- METODOS PRIVADOS
  
  private void enviarMensajes(String addPat) {
    OscMessage myMessage = new OscMessage(addPat);  
    oscP5.send(myMessage, direccionAPI);
  }
  
  private void enviarMensaje(String addPat, int data) {
    OscMessage myMessage = new OscMessage(addPat);
    myMessage.add(data);
    oscP5.send(myMessage, direccionAPI);
    oscP5.send(new OscMessage("/actualizarMovimiento"), direccionAPI);
  }
  
  private void enviarMensajeJoint( String addPat, float x, float y, NetAddress direccion ){
    OscMessage mensaje = new OscMessage( addPat );
    mensaje.add( x );
    mensaje.add( y );
    oscP5.send( mensaje, direccion);
  }
  
  private void enviarMensajeJoint( String addPat, int user, String nombre, float x, float y, float confianza, NetAddress direccion ){
    OscMessage mensaje = new OscMessage( addPat );
    mensaje.add( user );
    mensaje.add( nombre );
    mensaje.add( x );
    mensaje.add( y );
    //YA NO mensaje.add( velocidadX ); mensaje.add( velocidadY );
    mensaje.add( confianza );
    oscP5.send( mensaje, direccion);
  }
  
  private void enviarMensajeRemoverUsuario( int user ){
    OscMessage mensaje = new OscMessage( "/remover/usuario" );
    mensaje.add( user );
    oscP5.send( mensaje, direccionSistema );
  }

}
//v 22/06/2017
String archivoConfigXML = "../../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

final EstadoModulo[] EstadoModuloList = new EstadoModulo[]{EstadoModulo.APAGADO, EstadoModulo.LOCAL, EstadoModulo.REMOTO};
public int EstadoModuloToInt(EstadoModulo estado) {
  return estado==EstadoModulo.APAGADO?0:estado==EstadoModulo.LOCAL?1:2;
};

class ConfiguracionCOD05 {
  ConfigModulo lienzo, observador, carrete;
  boolean panelConexiones = false;

  class ConfigModulo {
    String id = "indefinido";
    String ip = "127.0.0.1";
    int puerto = 12000;
    EstadoModulo estado = EstadoModulo.LOCAL;

    public ConfigModulo Iniciar(String id, int puerto) {
      this.id = id;
      this.puerto = puerto;
      return this;
    }

    public void cargar(XML xml) {
      id = xml.getString("id", id);
      ip = xml.getString("ip", ip);
      puerto = xml.getInt("puerto", puerto);
      int estadoInt = xml.getInt("estado", -1);
      if (estadoInt != -1) estado = EstadoModuloList[estadoInt];
    }    
    public XML generar() {
      XML xml = new XML("ConfigModulo");
      xml.setString("id", id);
      xml.setString("ip", ip);
      xml.setInt("puerto", puerto);
      xml.setInt("estado", EstadoModuloToInt(estado));
      return xml;
    }
  }
  public void cargar(XML xml) {
    lienzo = new ConfigModulo().Iniciar("lienzo", 12010);
    observador = new ConfigModulo().Iniciar("observador", 12020);
    carrete = new ConfigModulo().Iniciar("carrete", 12030);
    if (xml != null) {
      panelConexiones = xml.getInt("panelConexiones", panelConexiones?1:0)==1;
      XML[] configs = xml.getChildren("ConfigModulo");
      for (ConfigModulo cm : new ConfigModulo[]{lienzo, observador, carrete}) {
        for (XML cxml : configs) {
          if (cm.id.equals(cxml.getString("id", ""))) cm.cargar(cxml);
        }
      }
    }
  }
  public XML guardar(String nombre) {
    XML xml = new XML(nombre);
    xml.setInt("panelConexiones", panelConexiones?1:0);
    for (ConfigModulo cm : new ConfigModulo[]{lienzo, observador, carrete}) {
      xml.addChild(cm.generar());
    }
    return xml;
  }
}
class GuiCerrado{
  
  private Slider factorCerrado;
  
  GuiCerrado( GuiP5 guiP5, String pestana ){
    
    factorCerrado = guiP5.addSlider( "sliderFactorCerrado" )
                          .setBroadcast(false)
                          .setLabel( "Umbral cerrado" )
                          .setSize( 300, 20 )
                          .setPosition( width*0.5f - 256, height - 70 )
                          .setRange( 0, 5 )
                          .setValue( UsuarioCerrado.getFactorUmbral() )
                          .setSliderMode(Slider.FLEXIBLE)
                          .setBroadcast(true)
                          .moveTo( pestana )
                          ;    
     
     factorCerrado.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
     
     guiP5.addButton( "resetCerrado" )
     //primero que nada desactivo el "desencadenamiento de enventos"
     .setBroadcast(false)
     .setLabel( "Reestablecer" )
     .setWidth( 100 )
     .setHeight( 20 )
     .setPosition( width * 0.5f + 156, height - 70 )
     //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
     .setBroadcast(true)
     .moveTo( pestana );
     
  }
}

public void sliderFactorCerrado( float factor ){
  UsuarioCerrado.setFactorUmbral( factor );
  saveDatosXML();
}

public void resetCerrado(){
  XML xml = loadXML( "DefaultUmbrales.xml" );
  XML hijo = xml.getChild( "UsuarioCerrado" );
  if( hijo != null ){
    
    float factorUmbralCerrado = hijo.getFloat("factorUmbral");
    UsuarioCerrado.setFactorUmbral( factorUmbralCerrado );

    motor.getGuiP5().get( Slider.class, "sliderFactorCerrado" ).setValue( UsuarioCerrado.getFactorUmbral() );
    
    saveDatosXML();
  }  
}
class GuiDesequilibrio{
  
  private Range umbralesDesequilibrio;
  
  GuiDesequilibrio( GuiP5 guiP5, String pestana ){
    
    umbralesDesequilibrio = guiP5.addRange( "umbralesDesequilibrio" )
                            //primero que nada desactivo el "desencadenamiento de enventos"
                            .setBroadcast(false)
                            .setLabel( "Umbrales de desequilibrio" )
                            .setPosition( width*0.5f - 256, height - 70 )
                            .setSize( 300, 20 )
                            .setRange( 0, UsuarioDesequilibrio.MAXIMO_VALOR_UMBRAL )
                            .setRangeValues( UsuarioDesequilibrio.getUmbralMenor(), UsuarioDesequilibrio.getUmbralMaximo() )
                            //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
                            .setBroadcast(true)
                            .moveTo( pestana );
                            
    umbralesDesequilibrio.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
    
    guiP5.addButton( "resetDesequilibrio" )
     //primero que nada desactivo el "desencadenamiento de enventos"
     .setBroadcast(false)
     .setLabel( "Reestablecer" )
     .setWidth( 100 )
     .setHeight( 20 )
     .setPosition( width * 0.5f + 156, height - 70 )
     //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
     .setBroadcast(true)
     .moveTo( pestana );
    
  }
  
}

public void resetDesequilibrio(){
  XML xml = loadXML( "DefaultUmbrales.xml" );
  XML hijo = xml.getChild( "UsuarioDesequilibrio" );
  if( hijo != null ){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");    
    
    UsuarioDesequilibrio.setUmbrales( factorUmbralBajo, factorUmbralAlto );
    
    motor.getGuiP5().get( Range.class, "umbralesDesequilibrio" ).setRangeValues( UsuarioDesequilibrio.getUmbralMenor(), UsuarioDesequilibrio.getUmbralMaximo() );
    
    saveDatosXML();
  }  
}
class GuiNivel{
  
  GuiNivel( GuiP5 guiP5, String pestana ){
      guiP5.addRange( "umbralesNivel" )
            //primero que nada desactivo el "desencadenamiento de enventos"
            .setBroadcast(false)
            .setLabel( "Factor nivel bajo y alto" )
            .setPosition( width*0.5f - 256, height - 70 )
            .setSize( 300, 20 )
            .setRange( 0, 1 )
            .setRangeValues( UsuarioNivel.getFactorUmbralBajo(), UsuarioNivel.getFactorUmbralAlto() )
            //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
            .setBroadcast(true)
            .moveTo( pestana );
            
      guiP5.getController("umbralesNivel").getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(15);
      
      guiP5.addButton( "resetNivel" )
      //primero que nada desactivo el "desencadenamiento de enventos"
      .setBroadcast(false)
      .setLabel( "Reestablecer" )
      .setWidth( 100 )
      .setHeight( 20 )
      .setPosition( width * 0.5f + 156, height - 70 )
      //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
      .setBroadcast(true)
      .moveTo( pestana );
      
  }
  
}

public void resetNivel(){
  XML xml = loadXML( "DefaultUmbrales.xml" );
  XML hijo = xml.getChild( "UsuarioNivel" );
  if( hijo != null ){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");
    
    UsuarioNivel.setUmbrales( factorUmbralBajo, factorUmbralAlto );

    motor.getGuiP5().get( Range.class, "umbralesNivel" ).setRangeValues( UsuarioNivel.getFactorUmbralBajo(), UsuarioNivel.getFactorUmbralAlto() );
    
    saveDatosXML();
  }  
}


class GuiP5 extends ControlP5{
  
  final String titulo = "CONFIGURADOR DEL OBSERVADOR";
  final PImage logoCOD05, logoObservador;
  
  //HashMap<String, Tab> pestanas = new HashMap<String, Tab>();
  
  GuiDesequilibrio guiDesequilibrio;
  GuiCerrado guiCerrado;
  GuiNivel guiNivel;
  
  public GuiP5( PApplet p5, String[] pestanas ){
    super( p5 );
    
    logoCOD05 = loadImage( "logoCOD05.png" );
    logoObservador = loadImage( "observador.png" );
    logoObservador.resize( round( logoObservador.width * 0.45f ), round( logoObservador.height * 0.45f ) );
    
    PFont fuente = loadFont( "MyriadPro-Regular-14.vlw" );
    setFont( fuente );
    textFont( fuente );
    setColorCaptionLabel( paleta.blanco );
    
    controlWindow.setPositionOfTabs( (int) controlWindow.getPositionOfTabs()[ 0 ], 42 );

    //------------ PESTANAS
    for( int i = 1; i < pestanas.length; i++ ){
      addTab( pestanas[ i ] )
      .activateEvent( true )
      .setId( i )
      .setWidth( (width / pestanas.length) - 5 )
      .setHeight( 42 )
      .getCaptionLabel().alignX( ControlP5.CENTER )
      ;
      
      ControllerGroup<Tab> grupo = ( ControllerGroup<Tab> ) getTab( pestanas[ i ] );
      grupo.setColorBackground( paleta.grisClaro );
      grupo.setColorActive( paleta.amarillo );
      grupo.setColorForeground( paleta.negro ); 
      
    }
    
    Tab t = getTab("default");
    t.setLabel( pestanas[ 0 ] );
    t.activateEvent( true );
    t.setId( 0 );
    t.setWidth( width / pestanas.length );
    t.setHeight( 42 );
    t.getCaptionLabel().alignX( ControlP5.CENTER );
    
    ControllerGroup<Tab> grupo = ( ControllerGroup<Tab> ) getTab("default");
    grupo.setColorBackground( paleta.grisClaro );
    grupo.setColorActive( paleta.amarillo );
    grupo.setColorForeground( paleta.negro ); 
    grupo.getCaptionLabel().setColor( paleta.negro );
    //------------
    
    //PESTANA 0 - DEFAULT CAMARA COMUN
    addToggle("espejoEjeX")
     .setPosition( 75, 84 + (height - 84) * 0.65f - 25 )
     .setSize(20,20)
     .setValue( comunicacionOSC.getInvertidoEjeX() )
     .setLabel( "Espejo eje X" )
     .setColorForeground( paleta.grisClaro ) 
     .getCaptionLabel().alignX( ControlP5.LEFT ).alignY( ControlP5.CENTER ).toUpperCase( false ).setPaddingX( 30 )
     ;
     
     addToggle("espejoEjeY")
     .setPosition( 75, 84 + (height - 84) * 0.65f + 25 )
     .setSize(20,20)
     .setValue( comunicacionOSC.getInvertidoEjeY() )
     .setLabel( "Espejo eje Y" )
     .setColorForeground( paleta.grisClaro ) 
     .getCaptionLabel().alignX( ControlP5.LEFT ).alignY( ControlP5.CENTER ).toUpperCase( false ).setPaddingX( 30 )
     ;
    
    //PESTANA 1 - DEBUG DESEQUILIBRIO
    guiDesequilibrio = new GuiDesequilibrio( this, pestanas[ 1 ] );
    
    //PESTANA 2 - DEBUG NIVEL
    guiNivel = new GuiNivel( this, pestanas[ 2 ] );
    
    //PESTANA 3 - DEBUG CERRADO
    guiCerrado = new GuiCerrado( this, pestanas[ 3 ] );
    
    //PESTANA 4 - DEBUG ESPALDA
    
  }
  
  //---------------------------------------- METODOS PUBLICOS
  
  //seters y geters
  public void setPestanaActiva( String pestana ){
    if( !pestana.equals( motor.NOMBRE_ESTADO[ 0 ] ) ){
      getTab( pestana ).bringToFront();
    }else{
      getTab( "default" ).bringToFront();
    }
    actualizarColorPestanas();
  }
  
  public void actualizarColorPestanas(){
    
    ControllerList listaPestanas = controlWindow.getTabs();
    
    for( int i = 0; i < listaPestanas.size(); i++ ){
      Tab t = (Tab) listaPestanas.get( i );
      if( t.isActive() ) t.getCaptionLabel().setColor( paleta.negro );
      else t.getCaptionLabel().setColor( paleta.blanco );
    }

  }
  
  public void ejecutar( int estado ){
    pushStyle();
    
    fill( paleta.negro );
    noStroke();
    rect( 0, 0, width, 42 );
    
    image( logoCOD05, 21, 0 );
    
    fill( paleta.grisClaro );//grisFondo
    textAlign( CENTER, CENTER );
    text( titulo, width * 0.5f, 21 );
    
    fill( paleta.grisClaro );
    rect( 0, 42, width, 42 );
    
    if( estado == Motor.CAMARA_COMUN ){
      fill( paleta.negro );
      rect( width * 0.05f, 84 + ( (height - 84) * 0.2f ), width * 0.215f, (height - 84) * 0.6f );
      
      fill( paleta.blanco );
      textAlign( CENTER, CENTER );
      text( "Espejar hacia\nCOD05 Lienzo", width * 0.1575f, 84 + ( (height - 84) * 0.525f ) );
      
      imageMode( CENTER );
      image( logoObservador, width * 0.1575f, 84 + ( (height - 84) * 0.25f ) + logoObservador.height * 0.5f );
      
    }else if( estado == Motor.DEBUG_DESEQUILIBRIO ){
      fill( paleta.grisClaro );
      rect( 144, 120, 512, 384 );
    }
    
    popStyle();
  }
 
}

public void controlEvent(ControlEvent evento) {
  
  if( evento.isTab() ) {
    motor.setEstado( evento.getTab().getId() );
  }else if( evento.isFrom( "umbralesDesequilibrio" ) ){
    
    UsuarioDesequilibrio.setUmbralMenor( evento.getController().getArrayValue( 0 ) );
    UsuarioDesequilibrio.setUmbralMaximo( evento.getController().getArrayValue( 1 ) );
    saveDatosXML();
  
  }else if( evento.isFrom( "umbralesNivel" ) ){
    
    UsuarioNivel.setFactorUmbralBajo( evento.getController().getArrayValue( 0 ) );
    UsuarioNivel.setFactorUmbalAlto( evento.getController().getArrayValue( 1 ) );
    saveDatosXML();
    
  }
}

public void espejoEjeX(boolean valor) {
  println("Eje X invertido:", valor);
  comunicacionOSC.setInvertidoEjeX( valor );
  saveDatosXML();
}

public void espejoEjeY(boolean valor) {
  println("Eje Y invertido:", valor);
  comunicacionOSC.setInvertidoEjeY( valor );
  saveDatosXML();
} 
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
  
  private final float FACTOR_VENTANA = 0.8f;
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
    
    VENTANA_POSICION_Y = ( kinect.isInit() )? 84 + round( ( height - 84 - kinect.userImage().height * FACTOR_VENTANA ) * 0.5f ) : 0 ;
    
  }
  
  //---------------------------------------- METODOS PUBLICOS
  
  //---- seters y geters
  public void setEstado( int estado ){
    this.estado = estado;
    if( estado == DEBUG_NIVEL || estado == DEBUG_CERRADO || estado == DEBUG_ESPALDA || estado == DEBUG_VELOCIDAD )
      setDibujarEspacio3D( true );
    else
      setDibujarEspacio3D( false );
    guiP5.actualizarColorPestanas();
    print( "Estado: " + this.estado );
    println( " -> " + NOMBRE_ESTADO[ this.estado ] );
  }
  
  public void setDibujarEspacio3D( boolean dibujarEspacio3D ){
    this.dibujarEspacio3D = dibujarEspacio3D;
  }
  
  public int getEstado(){
    return estado;
  }
  
  public GuiP5 getGuiP5(){
    return guiP5;
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
      fill( paleta.blanco );
      textSize( height * 0.035f );
      text( "Kinect no se pudo iniciar.\nAseg\u00farese de que est\u00e9 conectada y reinicie el programa.", 20, height * 0.43f );
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

    dibujarDebugDesequilibrio( unUDesiq, p5.g, width*0.5f - kinect.userImage().width * 0.5f * FACTOR_VENTANA, VENTANA_POSICION_Y * 0.8f, 
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
                
        float posX = ( escalaX == 1 )? width * 0.315f : -width * 0.315f - kinect.userImage().width * FACTOR_VENTANA ;
        float posY = ( escalaY == 1 )? VENTANA_POSICION_Y : -VENTANA_POSICION_Y - kinect.userImage().height * FACTOR_VENTANA ;
        
        image( kinect.userImage(), posX, posY, kinect.userImage().width * FACTOR_VENTANA, kinect.userImage().height * FACTOR_VENTANA);
        
      popMatrix();
      
    }
    
    else if( dibujarEspacio3D ){
      image( espacio3D, width*0.5f - espacio3D.width * 0.5f * FACTOR_VENTANA, VENTANA_POSICION_Y * 0.8f, espacio3D.width * FACTOR_VENTANA, espacio3D.height * FACTOR_VENTANA );
      image(kinect.userImage(), width*0.5f - espacio3D.width * 0.5f * FACTOR_VENTANA, VENTANA_POSICION_Y * 0.8f, kinect.depthWidth() * 0.25f, kinect.depthHeight() * 0.25f);
    }
  }
  
}
class Paleta{
  
  final int amarillo, rojo, grisFondo, grisClaro, negro, blanco;
  
  Paleta(){
    amarillo = color( 0xffBDBD3F );
    rojo = color( 0xffDF564C );
    grisFondo = color( 0xff272B2D );
    grisClaro = color( 0xff3A3D40 );
    negro = color( 0xff141516 );
    blanco = color( 0xffCCCCCC );
  }
}
public void saveDatosXML(){
  String baseXML = "<UsuarioUmbrales></UsuarioUmbrales>";
  
  XML xml = parseXML( baseXML );
  
  if( xml == null ){
    println("ERROR: parseXML");
  }else{
    
    XML hijo = xml.addChild( "UsuarioCerrado" );
    hijo.setFloat( "factorUmbral", UsuarioCerrado.getFactorUmbral() );
    
    hijo = xml.addChild( "UsuarioNivel" );
    hijo.setFloat( "factorUmbralBajo", UsuarioNivel.getFactorUmbralBajo() );
    hijo.setFloat( "factorUmbralAlto", UsuarioNivel.getFactorUmbralAlto() );
    
    hijo = xml.addChild( "UsuarioDesequilibrio" );
    hijo.setFloat( "factorUmbralBajo", UsuarioDesequilibrio.getUmbralMenor() );
    hijo.setFloat( "factorUmbralAlto", UsuarioDesequilibrio.getUmbralMaximo() );
    
    hijo = xml.addChild( "Espejo" );
    hijo.setInt( "espejoX", ( comunicacionOSC.getInvertidoEjeX() )? 1 : 0 );
    hijo.setInt( "espejoY", ( comunicacionOSC.getInvertidoEjeY() )? 1 : 0 );
    
    saveXML( xml, "data/UsuarioUmbrales.xml" );
    
  }
  
}

public void loadDatosXML(){
  XML xml = loadXML( "UsuarioUmbrales.xml" );
  
  XML hijo = xml.getChild( "UsuarioCerrado" );
  loadDatos_UsuarioCerrado( hijo );
  
  hijo = xml.getChild( "UsuarioNivel" );
  loadDatos_UsuarioNivel( hijo );
  
  hijo = xml.getChild( "UsuarioDesequilibrio" );
  loadDatos_UsuarioDesequilibrio( hijo );
  
  hijo = xml.getChild( "Espejo" );
  loadDatos_Espejo( hijo );
  
}

public void loadDatos_UsuarioCerrado( XML hijo ){
  
  if( hijo != null ){
    float factorUmbral = hijo.getFloat("factorUmbral");
    UsuarioCerrado.setFactorUmbral( factorUmbral );
  }
  
}

public void loadDatos_UsuarioNivel( XML hijo ){
  
  if( hijo != null){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    UsuarioNivel.setFactorUmbralBajo( factorUmbralBajo );
    
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");
    UsuarioNivel.setFactorUmbalAlto( factorUmbralAlto );
    
  }
  
}

public void loadDatos_UsuarioDesequilibrio( XML hijo ){
  
  if( hijo != null ){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    UsuarioDesequilibrio.setUmbralMenor( factorUmbralBajo );
    
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");
    UsuarioDesequilibrio.setUmbralMaximo( factorUmbralAlto );
    
  }
  
}

public void loadDatos_Espejo( XML hijo ){
  if( hijo != null ){
    
    boolean espejoX = ( hijo.getInt( "espejoX" ) > 0 )? true : false ;
    boolean espejoY = ( hijo.getInt( "espejoY" ) > 0 )? true : false ;
    
    if( comunicacionOSC != null ){
      comunicacionOSC.setInvertidoEjeX( espejoX );
      comunicacionOSC.setInvertidoEjeY( espejoY );
    }
    
  }
}
public void dibujarDebugCerrado ( UsuarioCerrado cerrado, PGraphics p3d ) {
  
  p3d.pushStyle();
  
  p3d.fill(cerrado.cerradoBruto?255:0, cerrado.cerrado?255:0, 255, 150);
  p3d.noStroke();
  p3d.hint(DISABLE_DEPTH_MASK);
  p3d.pushMatrix();
  p3d.translate(cerrado.centroPromedio.x, cerrado.centroPromedio.y, cerrado.centroPromedio.z);
  p3d.sphere(cerrado.umbralCerrado*0.25f);
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
    p3d.stroke( PVector.dist(cerrado.joints.get(j), cerrado.centroPromedio) < cerrado.umbralCerrado || cerrado.confianza.get(j) <= .5f ? 255:0, 0, 255);
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
public void dibujarDebugDesequilibrio ( UsuarioDesequilibrio deseq, PGraphics g2d, float x, float y, float w, float h ) {
  
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
  
  g2d.stroke(0xff1FFF49);
  g2d.line(x + w/2, y , x+w/2, y+h);
  g2d.line(x , y+h/2 , x+w, y+h/2);
  
  g2d.stroke(0xff1D9534);
  float tamSegmento = 10;
  for (float offY = 0; offY < h; offY += tamSegmento*2) {
    g2d.line(x + w/2 - UsuarioDesequilibrio.getUmbralMenor() , y + offY , x + w/2 - UsuarioDesequilibrio.getUmbralMenor() , min(y + offY+tamSegmento, y+h));
    g2d.line(x + w/2 + UsuarioDesequilibrio.getUmbralMenor() , y + offY , x + w/2 + UsuarioDesequilibrio.getUmbralMenor() , min(y + offY+tamSegmento, y+h));
  }
  //line(x + w/2 - UsuarioDesequilibrio.getUmbralMenor() , y , x + w/2 - UsuarioDesequilibrio.getUmbralMenor() , y+ h);
  //line(x + w/2 + UsuarioDesequilibrio.getUmbralMenor() , y , x + w/2 + UsuarioDesequilibrio.getUmbralMenor() , y+ h);
  g2d.line(x + w/2 - UsuarioDesequilibrio.getUmbralMaximo() , y , x + w/2 - UsuarioDesequilibrio.getUmbralMaximo() , y+ h);
  g2d.line(x + w/2 + UsuarioDesequilibrio.getUmbralMaximo() , y , x + w/2 + UsuarioDesequilibrio.getUmbralMaximo() , y+ h);
  
  g2d.noStroke();
  
  //if (g.is3D())  translate(0,0,1);
  g2d.fill(0);
  g2d.rectMode(CENTER);
  g2d.textAlign(CENTER,CENTER);
  g2d.rect(x + w/2 , y+h*.05f , w*.18f , h*.1f);
  g2d.fill(0,255,0);
  g2d.textSize(h*.07f);
  g2d.text(nfs(deseq.desequilibrio,1,2),x+w/2, y + h*.05f);
  //if (g.is3D())translate(0,0,-1);
  
  g2d.popStyle();

}
public void dibujarDebugEspalda( Usuario usuario, PGraphics p3d ){
  
  UsuarioEspalda espalda = usuario.getEspalda();
  PVector[] posicionesJoints = usuario.getPosicionesJoints();
  PMatrix3D[] orientacionesJoints = espalda.getOrientacionesJoints();
  int factor = 200;
 
  p3d.pushStyle();
  
  for( int i = 0; i < posicionesJoints.length; i++ ){
    
    p3d.pushMatrix();
      
      p3d.translate( posicionesJoints[ i ].x, posicionesJoints[ i ].y, posicionesJoints[ i ].z );
      
      boolean dibujarEjes = false;
      int j;
      for( j = 0; j < UsuarioEspalda.joints.length; j++ ){
        if( i == UsuarioEspalda.joints[ j ] ){
          dibujarEjes = true;
          break;
        }
      }
      
      if( dibujarEjes ){
        
        p3d.noFill();
        p3d.strokeWeight( 3 );
    
        p3d.stroke( 255, 0, 0 );
        p3d.line( 0, 0, 0, orientacionesJoints[ j ].m00 * factor, orientacionesJoints[ j ].m10 * factor, orientacionesJoints[ j ].m20 * factor );
        
        p3d.stroke( 0, 255, 0 );
        p3d.line( 0, 0, 0, orientacionesJoints[ j ].m01 * factor, orientacionesJoints[ j ].m11 * factor, orientacionesJoints[ j ].m21 * factor );
        
        p3d.stroke( 0, 0, 255 );
        p3d.line( 0, 0, 0, orientacionesJoints[ j ].m02 * factor, orientacionesJoints[ j ].m12 * factor, orientacionesJoints[ j ].m22 * factor );
        
      }else{
        p3d.noStroke();
        //p3d.strokeWeight( 1 );
        //p3d.stroke(#97F5F2);
        if( espalda.getEspalda() )
          p3d.fill( 0xff0000FF );
        else
          p3d.fill( 0xffFF0000 );
        p3d.ellipse( 0, 0, 100, 100 );
      }
      
    p3d.popMatrix();
    
  }
  
  p3d.popStyle();
  
}
public void dibujarDebugEsqueleto (SimpleOpenNI curContext, int userId, PGraphics p3d, int[] tipos, int[][] pares, float tam) {
  
  p3d.pushStyle();
  
  p3d.noFill();
  p3d.stroke(0xff97F5F2);

  PVector pos = new PVector();
  for (int tipoDeJoint : tipos) {
    float confianza = curContext.getJointPositionSkeleton(userId, tipoDeJoint, pos);
    p3d.pushMatrix();
    p3d.translate(pos.x, pos.y, pos.z);

    p3d.ellipse(0, 0, tam*5.6f, tam*5.6f);
    if (confianza >= .5f) {
      p3d.line(-tam, +tam, +tam, -tam);
      p3d.line(+tam, +tam, -tam, -tam);
    }
    if (confianza == 1) {
      p3d.ellipse(0, 0, tam*4.8f, tam*4.8f);
      p3d.ellipse(0, 0, tam*4, tam*4);
    }
    p3d.popMatrix();
  }
  p3d.stroke(0xff97F5F2, 120);
  PVector pos2 = new PVector();
  for (int[] par : pares) {
    float confianza1 = curContext.getJointPositionSkeleton(userId, par[0], pos);
    float confianza2 = curContext.getJointPositionSkeleton(userId, par[1], pos2);
    float ang = atan2(pos.y-pos2.y, pos.x-pos2.x) + HALF_PI;
    p3d.pushMatrix();
    p3d.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
    if (confianza2 >= .5f) {
      p3d.translate( tam*.5f*5.6f*cos(ang), tam*.5f*5.6f*sin(ang) );
      p3d.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
      p3d.translate( -tam*5.6f*cos(ang), -tam*5.6f*sin(ang) );
      p3d.line(pos.x, pos.y, pos.z, pos2.x, pos2.y, pos2.z);
    }
    p3d.popMatrix();
  }
  
  p3d.popStyle();
}
public void dibujarDebugNivel (UsuarioNivel nivel, PGraphics p3d, int tam) {
  
  p3d.pushStyle();
  
  p3d.fill(0xff4D5CF0,150);
  //p3d.stroke(#2731A0);
  p3d.noStroke();
  //p3d.hint(DISABLE_DEPTH_MASK);
  
  p3d.pushMatrix();
  p3d.translate(nivel.centroMasa.x,nivel.piso.y,nivel.centroMasa.z);
  p3d.rect(-tam/16, nivel.centroMasa.y - nivel.piso.y, tam/8, - nivel.centroMasa.y + nivel.piso.y);
  p3d.rotateX(-HALF_PI);
  p3d.fill(0xff4D5CF0);
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
public void dibujarDebugVelocidad( Usuario usuario, PGraphics p3d ){
  
  p3d.pushStyle();
 
  p3d.strokeWeight( 3 );
  
  PVector[] posicionesJoints = usuario.getPosicionesJoints();
  PVector[] velocidadesJoints = usuario.getVelocidadesJoints();
  float[] confiazasJoints = usuario.getConfianzasJoints();
  
  for( int i = 0; i < posicionesJoints.length; i++ ){
    
    p3d.stroke( map( confiazasJoints[ i ], 0, 1, 0, 255 ), 0, map( confiazasJoints[ i ], 0, 1, 255, 0 ) );
    
    PVector posicionAnterior = PVector.sub( posicionesJoints[ i ], velocidadesJoints[ i ] );
    p3d.line( posicionAnterior.x, posicionAnterior.y, posicionAnterior.z, posicionesJoints[ i ].x, posicionesJoints[ i ].y, posicionesJoints[ i ].z );
    
  }
  
  p3d.popStyle();
  
}
public String[] getNombreDeJoint(){
  String[] nombreDeJoint = {
    "CABEZA", "CUELLO", "HOMBRO_IZQUIERDO", "HOMBRO_DERECHO", "CODO_IZQUIERDO", "CODO_DERECHO", "MANO_IZQUIERDA", "MANO_DERECHA", "TORSO",
    "CADERA_IZQUIERDA", "CADERA_DERECHA", "RODILLA_IZQUIERDA", "RODILLA_DERECHA", "PIE_IZQUIERDO", "PIE_DERECHO"
  };
  return nombreDeJoint;
}

/*// VIEJO ORDEN
String[] getNombreDeJoint(){
  String[] nombreDeJoint = {
    "CABEZA", "CUELLO", "TORSO", "HOMBRO_IZQUIERDO", "CODO_IZQUIERDO", "MANO_IZQUIERDA", "HOMBRO_DERECHO", "CODO_DERECHO", "MANO_DERECHA",
    "CADERA_IZQUIERDA", "RODILLA_IZQUIERDA", "PIE_IZQUIERDO", "CADERA_DERECHA", "RODILLA_DERECHA", "PIE_DERECHO"
  };
  return nombreDeJoint;
}
*/

public int[] getTiposDeJoint(){
  int[] tiposDeJoint = {
    SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_RIGHT_ELBOW, 
    SimpleOpenNI.SKEL_LEFT_HAND, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_RIGHT_HIP, 
    SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT, SimpleOpenNI.SKEL_RIGHT_FOOT
  };
  return tiposDeJoint;
}

/*// VIEJO ORDEN
int[] getTiposDeJoint(){
  int[] tiposDeJoint = {
    SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND, 
    SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE, 
    SimpleOpenNI.SKEL_LEFT_FOOT, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT
  };
  return tiposDeJoint;
}
*/

public int[][] getParesDeJoints(){
  int[][] paresDeJoints = {
    {SimpleOpenNI.SKEL_HEAD,SimpleOpenNI.SKEL_NECK},
    {SimpleOpenNI.SKEL_NECK,SimpleOpenNI.SKEL_TORSO},
    {SimpleOpenNI.SKEL_LEFT_HAND,SimpleOpenNI.SKEL_LEFT_ELBOW},
    {SimpleOpenNI.SKEL_LEFT_ELBOW,SimpleOpenNI.SKEL_LEFT_SHOULDER},
    {SimpleOpenNI.SKEL_LEFT_SHOULDER,SimpleOpenNI.SKEL_NECK},
    {SimpleOpenNI.SKEL_TORSO,SimpleOpenNI.SKEL_LEFT_HIP},
    {SimpleOpenNI.SKEL_LEFT_HIP,SimpleOpenNI.SKEL_LEFT_KNEE},
    {SimpleOpenNI.SKEL_LEFT_KNEE,SimpleOpenNI.SKEL_LEFT_FOOT},
    {SimpleOpenNI.SKEL_RIGHT_HAND,SimpleOpenNI.SKEL_RIGHT_ELBOW},
    {SimpleOpenNI.SKEL_RIGHT_ELBOW,SimpleOpenNI.SKEL_RIGHT_SHOULDER},
    {SimpleOpenNI.SKEL_RIGHT_SHOULDER,SimpleOpenNI.SKEL_NECK},
    {SimpleOpenNI.SKEL_TORSO,SimpleOpenNI.SKEL_RIGHT_HIP},
    {SimpleOpenNI.SKEL_RIGHT_HIP,SimpleOpenNI.SKEL_RIGHT_KNEE},
    {SimpleOpenNI.SKEL_RIGHT_KNEE,SimpleOpenNI.SKEL_RIGHT_FOOT},
  };
  return paresDeJoints;
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Observador" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
