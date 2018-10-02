import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import oscP5.*; 
import netP5.*; 
import controlP5.*; 
import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ObeservadorBasico extends PApplet {

PApplet p5;
Reloj reloj;
Consola consola;
Paleta paleta;
Motor motor;
GuiP5 gui;

boolean test = false;

public void setup(){
  
  p5 = this;
  reloj = new Reloj();
  consola = new Consola();
  consola.setVerFps( false );
  consola.setDebug( true );
  paleta = new Paleta();
  motor = new Motor();
  gui = new GuiP5( p5 );
}

public void draw(){
  background( paleta.grisFondo );
  reloj.actualizar();
  motor.ejecutar();
  gui.ejecutar();
  consola.ejecutar();
}
float umbralEje = 10f;
float umbralNivel = 0.6f;
float umbralCerrado = 0.2f;

class UsuarioNivel {
  boolean entroAlto;
  boolean entroMedio;
  boolean entroBajo;
  int nivel;
  int p_nivel;
  UsuarioNivel() {
  }
  public void set(int v) {
    nivel = v;
    actualizar();
  }
  public void actualizar() {
    entroAlto = false;
    entroMedio = false;
    entroBajo = false;
    if (p_nivel != nivel) {
      if (p_nivel==0) {
        if (nivel == 1) {
          entroMedio = true;
        } else if (nivel == 2) {
          entroBajo = true;
        }
      }
      if (p_nivel==1) {
        if (nivel == 0) {
          entroAlto = true;
        } else if (nivel == 2) {
          entroBajo = true;
        }
      }
      if (p_nivel==2) {
        if (nivel == 0) {
          entroAlto = true;
        } else if (nivel == 1) {
          entroMedio = true;
        }
      }
      p_nivel = nivel;
    }
  }
}
class UsuarioCerrado {
  boolean cerro;
  boolean abrio;
  boolean cerrado;
  int cerrado_valor;
  int p_cerrado_valor;
  UsuarioCerrado() {
  }
  public void set(int v) {
    cerrado_valor = v;
    cerrado = (cerrado_valor == 0);
    actualizar();
  }
  public void actualizar() {
    cerro = false;
    abrio = false;
    if (p_cerrado_valor != cerrado_valor) {
      if (p_cerrado_valor==0) {        
        cerro = true;
      } else if (p_cerrado_valor==1) {
        abrio = true;
      }
    }
    p_cerrado_valor = cerrado_valor;
  }
}
class UsuarioDesequilibrio { 
  /*boolean izquierda;
   boolean derecha;*/  // esto podriamos agregarlo si es necesario
  boolean salioDerecha;
  boolean salioIzquierda;
  int desequilibrio;
  int p_desequilibrio;
  UsuarioDesequilibrio() {
  }
  public void set(int v) {
    desequilibrio = v;
    actualizar();
  }
  public void actualizar() {
    salioDerecha = false;
    salioIzquierda = false;

    if (p_desequilibrio != desequilibrio && desequilibrio == 0) {    
      if (p_desequilibrio == 1) {        
        salioDerecha = true;
      }
      if (p_desequilibrio == -1) {       
        salioIzquierda = true;
      }
    }
    p_desequilibrio = desequilibrio;
  }
} 


ConfiguracionCOD05 config;// = new ConfiguracionCOD05();
public class ComunicacionOSC {

  private OscP5 oscP5;
  private NetAddress direccionAPI;
  private NetAddress direccionSistema;

  private boolean invertidoEjeX, invertidoEjeY;

  public ComunicacionOSC( PApplet p5 ) {

    if (config == null) config = new ConfiguracionCOD05();
    XML xmlConfig = null;
    if (new File(sketchPath(archivoConfigXML)).exists()) xmlConfig = loadXML( archivoConfigXML );
    if (xmlConfig != null) xmlConfig = xmlConfig.getChild(xmlTagEjecucion);

    config.cargar(xmlConfig);

    oscP5 = new OscP5( p5, 12020); 
    direccionAPI = new NetAddress("127.0.0.1", 12030 );
    direccionSistema = new NetAddress( "127.0.0.1", 12010 );

    /*oscP5 = new OscP5( p5, config.observador.puerto); 
     direccionAPI = new NetAddress( config.carrete.ip, config.carrete.puerto );
     direccionSistema = new NetAddress( config.lienzo.ip, config.lienzo.puerto );*/

    invertidoEjeX = true;
  }

  //-------------------------------------------------- METODOS PUBLICOS

  //---- gets y sets
  public void setInvertidoEjeX( boolean invertidoEjeX ) {
    this.invertidoEjeX = invertidoEjeX;
  }

  public void setInvertidoEjeY( boolean invertidoEjeY ) {
    this.invertidoEjeY = invertidoEjeY;
  }

  public boolean getInvertidoEjeX() {
    return invertidoEjeX;
  }

  public boolean getInvertidoEjeY() {
    return invertidoEjeY;
  }
  //----

  //----- HACIA LA API
  public void enviarMensajesAPI( UsuarioDesequilibrio uDeseq, UsuarioNivel uNivel, UsuarioCerrado uCerrado ) {
    if (uNivel.entroAlto) enviarMensaje("/nivel", 0);
    else if (uNivel.entroMedio) enviarMensaje("/nivel", 1);
    else if (uNivel.entroBajo) enviarMensaje("/nivel", 2);
    if (uCerrado.abrio) enviarMensaje("/cerrado", 0);
    else if (uCerrado.cerro)enviarMensaje("/cerrado", 1);

    if (uDeseq.desequilibrio <= -1) enviarMensaje("/desequilibrio", 0);
    else if (uDeseq.desequilibrio >= 1) enviarMensaje("/desequilibrio", 4);
    /*else if (uDeseq.izquierda) enviarMensaje("/desequilibrio", 1);
     else if (uDeseq.derecha) enviarMensaje("/desequilibrio", 3);*/
    else if (uDeseq.salioDerecha || uDeseq.salioIzquierda) enviarMensaje("/desequilibrio", 2);


    //---- esto navega de uno en uno o lo uso para la camara web quiza si leugo otor dia o hoy mismo pero luego si funciona bien
    /* if (uDeseq.entroIzquierda) {
     enviarMensajes("/MenuNavegarIzquierda");
     } else if (uDeseq.entroDerecha) {
     enviarMensajes("/MenuNavegarDerecha");
     } else*/
    if (abs(uDeseq.desequilibrio) >= 1) {
      if (frameCount % 12 == 0) {
        if (uDeseq.desequilibrio > 0) enviarMensajes("/MenuNavegarDerecha");
        else enviarMensajes("/MenuNavegarIzquierda");
      }
    }

    if (/*uNivel.entroMedio ||*/ uNivel.entroBajo) {
      if (uCerrado.cerrado) {
        enviarMensajes("/MenuQuitarModificador");
      } else {
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
      } else {
        enviarMensajes("/Aceptar");
        println("SIIIIIIISIIII OHO SISAOIDOIASHDOIIOOIIOASDOIOIASOI");
      }
    }
  }

  //----- HACIA EL SISTEMA
  /*public void enviarMensajesSISTEMA( Usuario usuario ) {
   
   PVector[] posicionesJoints = usuario.getPosicionesJoints();
   PVector[] velocidadesJoints = usuario.getVelocidadesJoints();
   float[] confianzasJoints = usuario.getConfianzasJoints();
   
   for ( int i = 0; i < posicionesJoints.length; i++ ) {
   
   String nombreJoint = Usuario.nombresDeJoints[ i ];
   float x = motor.espacio3D.screenX( posicionesJoints[ i ].x, posicionesJoints[ i ].y, posicionesJoints[ i ].z ) / motor.espacio3D.width;
   float y = motor.espacio3D.screenY( posicionesJoints[ i ].x, posicionesJoints[ i ].y, posicionesJoints[ i ].z ) / motor.espacio3D.height;
   
   //NO ENVIO MAS VELOCIDAD
   //float velocidadX = motor.espacio3D.screenX( velocidadesJoints[ i ].x, velocidadesJoints[ i ].y, velocidadesJoints[ i ].z ) / motor.espacio3D.width;
   //float velocidadY = motor.espacio3D.screenY( velocidadesJoints[ i ].x, velocidadesJoints[ i ].y, velocidadesJoints[ i ].z ) / motor.espacio3D.height;
   
   if ( invertidoEjeX ) {
   x = 1 - x;
   //velocidadX = 1 - velocidadX;
   }
   
   if ( invertidoEjeY ) {
   y = 1 - y;
   //velocidadY = 1 - velocidadY;
   }
   
   enviarMensajeJoint( "/enviar/usuario/joint", usuario.getId(), nombreJoint, x, y, confianzasJoints[ i ], direccionSistema );
   }
   }*/

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

  /*private void enviarMensajeJoint( String addPat, float x, float y, NetAddress direccion ) {
   OscMessage mensaje = new OscMessage( addPat );
   mensaje.add( x );
   mensaje.add( y );
   oscP5.send( mensaje, direccion);
   }
   
   private void enviarMensajeJoint( String addPat, int user, String nombre, float x, float y, float confianza, NetAddress direccion ) {
   OscMessage mensaje = new OscMessage( addPat );
   mensaje.add( user );
   mensaje.add( nombre );
   mensaje.add( x );
   mensaje.add( y );
   //YA NO mensaje.add( velocidadX ); mensaje.add( velocidadY );
   mensaje.add( confianza );
   oscP5.send( mensaje, direccion);
   }
   
   private void enviarMensajeRemoverUsuario( int user ) {
   OscMessage mensaje = new OscMessage( "/remover/usuario" );
   mensaje.add( user );
   oscP5.send( mensaje, direccionSistema );
   }*/
}
//v 22/06/2017
String archivoConfigXML = "../configcod05.xml";
String xmlTagPanel = "panel", xmlTagEjecucion = "ejecucion";

enum EstadoModulo {
  APAGADO, LOCAL, REMOTO
}
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
/* Consola v1.22 14/08/2017
 * Nuevo: Nuevo color alerta, nuevo metodo printlnError() color rojo;
 * Hernán GM - hernangonzalezmoreno@gmail.com
*/

public final class Consola{
  
  private String texto;
  private ArrayList<Alerta> alertas = new ArrayList<Alerta>();
  private int colorTexto, colorAlerta;
  private int tamanoTexto, tamanoAlerta;
  private boolean debug;
  
  private boolean verFps, verDatos, verAlertas;
  
  private static final float LEADIN = 1.5f; //--- NUEVO!
  
  public Consola(){
    texto = "";
    colorTexto = color( 0xff000000 );//color( 255 );
    colorAlerta = color( 0xffCC9900 );
    tamanoTexto = PApplet.parseInt( height * 0.023f ); //int( height * 0.023 ); //tamanoTexto = 20;
    tamanoAlerta = PApplet.parseInt( height * 0.023f ); //int( height * 0.023 ); //tamanoAlerta = 20;
    
    debug = verFps = verDatos = verAlertas = true;
  }
  
  //--------------------------------------- METODOS PUBLICOS
  
  //GETERS AND SETERS
  public void setDebug( boolean debug ){
    this.debug = debug;
  }
  
  public void setVerFps( boolean verFps ){
    this.verFps = verFps;
  }
  
  public void setVerDatos( boolean verDatos ){
    this.verDatos = verDatos;
  }
  
  public void setVerAlertas( boolean verAlertas ){
    this.verAlertas = verAlertas;
  }
  
  public boolean getDebug(){
    return debug;
  }

  public boolean getVerFps(){
    return verFps;
  }

  public boolean getVerDatos(){
    return verDatos;
  }

  public boolean getVerAlertas(){
    return verAlertas;
  }
  //--------
  
  public void println( String texto ){
    this.texto += texto + "\n";
  }
  
  public void printlnAlerta( String alerta ){
    alertas.add( new Alerta( alerta ) );
    System.out.println( alerta );
  }
  
  public void printlnAlerta( String alerta, int colorPersonalizado ){
    alertas.add( new Alerta( alerta, colorPersonalizado ) );
    System.out.println( alerta );
  }
  
  public void printlnError( String alerta ){
    alertas.add( new Alerta( alerta, color( 0xffFF0000 ) ) );
    System.err.println( alerta );
  }
  
  public void ejecutar(){
    
    if( !verDatos ) texto = "";
    if( verFps ) texto = "fps: " + nf( frameRate, 0, 2 ) + "\n" + texto;
    
    if( debug ) ejecutarDebug();
    else ejecutarNoDebug();
    texto = "";
  }
  
  //--------------------------------------- METODOS PRIVADOS
  
  private void ejecutarDebug(){
    pushStyle();
      
      textAlign( LEFT, TOP );
      textSize( tamanoTexto );
      textLeading( tamanoTexto * LEADIN ); 
      
      noStroke();
      rectMode( CORNER );
      
      //NUEVO rectangulo negro de fondo

      fill( 255 );
      int desde = 0, hasta = 0, iteracion = 0;
      while( texto.indexOf( "\n", desde ) > 0 ){

        hasta = texto.indexOf( "\n", desde );
        String aux = texto.substring( desde, hasta );
        
        rect( 0, iteracion * (tamanoTexto * LEADIN), textWidth( aux ) + 3, tamanoTexto * ( LEADIN * 1.1666666f ) );
        
        desde = hasta + 1;
        iteracion++;
      }
      
      //
      
      fill( colorTexto );
      text( texto, 0, 3 );
      if( !texto.equals("") ) System.out.println( texto );
      
      textAlign( RIGHT, BOTTOM );
      textSize( tamanoAlerta );
      imprimirAlertas( verAlertas );
      
    popStyle();
  }
  
  private void ejecutarNoDebug(){
    if( !texto.equals("") ) System.out.println( texto );
    imprimirAlertas( false );
  }
  
  private void imprimirAlertas( boolean debug ){
    
    float posY = tamanoAlerta + tamanoAlerta * (LEADIN * 0.16666666f) ;//0.25
    
    for( int i = alertas.size() - 1; i >= 0; i-- ){
      
      Alerta a = alertas.get( i );
      a.ejecutar();
      
      if( a.getEstado() == Alerta.ESTADO_ELIMINAR ){
        alertas.remove( i );
      }else if( debug ){
        
        //------ NUEVO rectangulo negro de fondo
        
        if( a.getEstado() == Alerta.ESTADO_MOSTRAR )
          fill( 0 );
        else
          fill( 0, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );
        
        rect( width - textWidth( a.getAlerta() ) - 5, posY- tamanoAlerta * ( LEADIN * 0.875f ), textWidth( a.getAlerta() ) + 5, tamanoAlerta * LEADIN );
        
        //------
        
        int auxColorAlerta = a.isPersonalizado() ? a.getColorPersonalizado() : colorAlerta ;
        if( a.getEstado() == Alerta.ESTADO_MOSTRAR )
          fill( auxColorAlerta );
        else
          fill( auxColorAlerta, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );
        
        text( a.getAlerta(), width, posY );
        posY += tamanoAlerta * LEADIN;
        
        if( posY > height && i - 1 >= 0 ){
          removerAlertasFueraDePantalla( i - 1 );
          return;
        }
        
      }
      
    }//end for
    
  }
  
  private void removerAlertasFueraDePantalla( int desde ){
    for( int i = desde; i >= 0; i-- )
      alertas.remove( i );
  }
  
  //clase interna y miembro
  public class Alerta{
    
    private String alerta;
    private int colorPersonalizado;
    private boolean personalizado;
    
    private int estado;
    public static final int
    ESTADO_MOSTRAR = 0,
    ESTADO_DESAPARECER = 1,
    ESTADO_ELIMINAR = 2;
    
    private int tiempo;
    public static final int
    TIEMPO_MOSTRAR = 5000,//3000
    TIEMPO_DESAPARECER = 2000;
    
    
    //------------------------------ CONSTRUCTORES
    
    public Alerta( String alerta ){
      this.alerta = alerta;
      estado = ESTADO_MOSTRAR;
    }
    
    public Alerta( String alerta, int colorPersonalizado ){
      this.alerta = alerta;
      this.colorPersonalizado = colorPersonalizado;
      personalizado = true;
      estado = ESTADO_MOSTRAR;
    }
    
    //------------------------------ METODOS PUBLICOS
    
    public String getAlerta(){
      return alerta;
    }
    
    public int getEstado(){
      return estado;
    }
    
    public int getTiempo(){
      return tiempo;
    }
    
    public boolean isPersonalizado(){
      return personalizado;
    }
    
    public int getColorPersonalizado(){
      return colorPersonalizado;
    }
    
    public void ejecutar(){
      tiempo += reloj.getDeltaMillis();
      if( estado == ESTADO_MOSTRAR && tiempo > TIEMPO_MOSTRAR ){
        estado = ESTADO_DESAPARECER;
        tiempo = 0;
      }else if( estado == ESTADO_DESAPARECER && tiempo > TIEMPO_DESAPARECER ){
        estado = ESTADO_ELIMINAR;
      }
    }
    
  }
  
}


class GuiP5 extends ControlP5{
  
  final String titulo = "CONFIGURADOR DEL OBSERVADOR WEBCAM";
  final PImage logoCOD05, logoObservador;
  final PFont fuentePequena, fuenteGrande;
  
  String pestanaActiva;
  static final String
  PESTANA_CAMARA = "Cámara",
  PESTANA_PROCESOS = "Proceso",
  PESTANA_DESEQUILIBRIO = "Desequilibrio",
  PESTANA_NIVEL = "Nivel",
  PESTANA_CERRADO = "Cerrado";
  
  //Pestana principal - CAMARA
  ScrollableList listaDeCamaras;
  
  //Pestanas
  GuiPestanaProceso pestanaProceso;
  GuiPestanaDesequilibrio pestanaDesequilibrio;
  
  GuiP5( PApplet p5 ){
    super( p5 );
    addFrameRate().setInterval(5).setPosition(0,height - 15).moveTo( "global" );
    
    logoCOD05 = loadImage( "logoCOD05.png" );
    logoObservador = loadImage( "observador.png" );
    
    fuentePequena = loadFont( "MyriadPro-Regular-14.vlw" );
    fuenteGrande = loadFont( "MyriadPro-Regular-48.vlw" );
    setFont( fuentePequena );
    textFont( fuentePequena );
    setColorCaptionLabel( paleta.blanco );
    
    controlWindow.setPositionOfTabs( (int) controlWindow.getPositionOfTabs()[ 0 ], 42 );

  }
  
  public void iniciarPestanas(){
    String[] pestanas = { PESTANA_CAMARA, PESTANA_PROCESOS, PESTANA_DESEQUILIBRIO, PESTANA_NIVEL, PESTANA_CERRADO };
    pestanaActiva = PESTANA_CAMARA;
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
    iniciarContenidoPestanas();
  }
  
  public void iniciarContenidoPestanas(){
    
    //Global
    gui.addButton( "nuevoFondo" )
    //primero que nada desactivo el "desencadenamiento de enventos"
    .setBroadcast(false)
    .setLabel( "Nuevo fondo" )
    .setWidth( 100 )
    .setHeight( 20 )
    .setPosition( width * 0.825f, 540 )
    //una vez configurado todo, vuelvo a activar el "desencadenamiento de enventos"
    .setBroadcast(true)
    .moveTo( "global" );
    
    //PESTANA 0 - DEFAULT CAMARA
    listaDeCamaras = addScrollableList("SeleccionarCamara")
      .setBroadcast(false)
     .setPosition(width*0.05f, height*0.35f)
     .setSize(150, 200)
     .setBarHeight(40)
     .setItemHeight(30)
     .addItems( motor.getNombresCamarasAptas() )
     .setLabel( "Seleccionar camara" )
     .setValue( 0 )
     //.setName( "listaSeleccionarPuerto" )
     // .setType(ScrollableList.LIST) // currently supported DROPDOWN and LIST
     .setBroadcast(true)
     ;
     
    //PESTANA 1 - Procesos
    pestanaProceso = new GuiPestanaProceso();
    
    //PESTANA 2 - Desequilibrio
    pestanaDesequilibrio = new GuiPestanaDesequilibrio();
   
  }
  
  public void crearSlider( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto, float minimo, float maximo, float valor ){
    Slider s = gui.addSlider( nombre )
    .setBroadcast(false)
    .setLabel( etiqueta )
    .setSize( 130, 20 )
    .setPosition( x, y )
    .setRange( minimo, maximo )
    .setValue( valor/*motor.movimiento.!!variable correspondiente!!*/ )
    .setSliderMode(Slider.FLEXIBLE)
    .setBroadcast(true)
    .moveTo( pestana )
    ;
    //gui.getController(nombre).getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
    s.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
  }
  
  //seters y geters
  public String getPestanaActiva(){
    ControllerList listaPestanas = controlWindow.getTabs();
    for( int i = 0; i < listaPestanas.size(); i++ ){
      Tab t = (Tab) listaPestanas.get( i );
      if( t.isActive() ) return t.getCaptionLabel().getText();//opcion 1, pestana 0 devuelve como Camara
      //if( t.isActive() ) return t.getName();//opcion 2, pestana 0 devuelve como default
    }
    return null;
  }
  
  public void setPestanaActiva( int numero ){
    switch( numero ){
      case 4:
        pestanaActiva = PESTANA_CERRADO;
        break;
      case 3:
        pestanaActiva = PESTANA_NIVEL;
        break;
      case 2:
        pestanaActiva = PESTANA_DESEQUILIBRIO;
        break;
      case 1:
        pestanaActiva = PESTANA_PROCESOS;
        break;
      default:
        pestanaActiva = PESTANA_CAMARA;
        break;
    }
  }
  
  public void actualizarColorPestanas(){
    
    ControllerList listaPestanas = controlWindow.getTabs();
    
    for( int i = 0; i < listaPestanas.size(); i++ ){
      Tab t = (Tab) listaPestanas.get( i );
      if( t.isActive() ) t.getCaptionLabel().setColor( paleta.negro );
      else t.getCaptionLabel().setColor( paleta.blanco );
    }

  }
  
  public void ejecutar(){
    //background( paleta.grisFondo );
    
    pushStyle();
    
    fill( paleta.negro );
    noStroke();
    rect( 0, 0, width, 42 );
    
    image( logoCOD05, 21, 0 );
    
    fill( paleta.grisClaro );//grisFondo
    textAlign( CENTER, CENTER );
    text( titulo, width * 0.5f, 21 );
    
    switch( motor.estado ){
      case Motor.ESTADO_COMPROBANDO_CAMARAS:
        comprobandoCamaras();
        break;
      case Motor.ESTADO_CAMARAS_NO_COMPATIBLES:
        camarasNoCompatibles();
        break;
      case Motor.ESTADO_CORRIENDO:
        pestanas();
        break;
    }
    
    popStyle();
    if( motor.movimiento != null ) motor.movimiento.conteo();
  }
  
  public void comprobandoCamaras(){
    fill( paleta.grisClaro );
    textFont(fuenteGrande);
    textSize( 30 );
    text( "Comprobando cámaras", width*0.5f, height*0.5f );
  }
  
  public void camarasNoCompatibles(){
    fill( paleta.grisClaro );
    textFont(fuenteGrande);
    textSize( 30 );
    text( "La webcam no es compatible con (320x240,fps30)", width*0.5f, height*0.5f );
  }
  
  public void pestanas(){
    //barra pestanas
    fill( paleta.grisClaro );
    rect( 0, 42, width, 42 );
    //
    
    switch( pestanaActiva ){
      case PESTANA_CAMARA:
        pestanaCamara();
        break;
      case PESTANA_PROCESOS:
        pestanaProceso.ejecutar();
        break;
      case PESTANA_DESEQUILIBRIO:
        pestanaDesequilibrio.ejecutar();
        break;
      case PESTANA_NIVEL:
        break;
      case PESTANA_CERRADO:
        break;
      default:
       break;
    }
    
  }
  
  public void pestanaCamara(){
    image( motor.camara, width*0.45f, height * 0.35f );
  }
  
}

public void controlEvent(ControlEvent evento) {
  
  if( evento.isTab() ) {
    gui.setPestanaActiva( evento.getTab().getId() );
    gui.actualizarColorPestanas();
  }
  
}

public void SeleccionarCamara( int numeroCamara ){
  motor.camaraSeleccionada = motor.camarasAptas[ numeroCamara ];
  motor.estado = Motor.ESTADO_INICIAR_CAMARA;
}
class GuiPestanaDesequilibrio{
  
  GuiPestanaDesequilibrio(){
    
    float posX = width*0.5f + 50;
    int posY = 175, saltoY = 100;
    
    crearSlider( "sliderUmbralEje", "Umbral eje", posX, posY, 0f, 100f, 10f );
    crearSlider( "sliderUmbralNivel", "Umbral nivel", posX, posY += saltoY, 0f, 1f, 0.6f );
    crearSlider( "sliderUmbralCerrado", "Umbral cerrado", posX, posY += saltoY, 0f, 0.3f, 0.2f );
    
  }
  
  public void crearSlider( String nombre, String etiqueta, float x, float y, float minimo, float maximo, float valor ){
    gui.crearSlider( nombre, etiqueta, GuiP5.PESTANA_DESEQUILIBRIO, x, y, 200, 20, minimo, maximo, valor );
  }
  
  public void ejecutar(){
    PImage resultado = motor.movimiento.getImagenAnalisis();
    image(resultado, width*0.5f-Motor.ANCHO_CAMARA, height*0.5f-Motor.ALTO_CAMARA*0.5f );
    
    UsuarioDesequilibrio d = motor.uDeseq;
    UsuarioNivel n = motor.uNivel;
    UsuarioCerrado c = motor.uCerrado;
    
    String texto = "Deseq: " + ( d.salioIzquierda? "salio IZQ" : d.salioDerecha? "salio DER" : "..." );
    texto += "\nNivel: " + (n.entroBajo? "entro bajo" : n.entroMedio? "entro medio" : n.entroAlto? "entro alto" : "...");
    texto += "\nCerrado: " + ( c.cerro? "cerro" : c.abrio? "abrio" : "..." );
    texto += "\n\t" + c.cerrado;
    
    textoDebug( texto );
    
  }
  
  public void textoDebug( String texto ){
    float posX = width*0.5f-Motor.ANCHO_CAMARA;
    float posY = height*0.5f+Motor.ALTO_CAMARA*0.5f+50;
    fill( paleta.blanco );
    textAlign( LEFT );
    text( texto, posX, posY );
  }
}

public void sliderUmbralEje( float valor ){
  umbralEje = valor;
}

public void sliderUmbralNivel( float valor ){
  umbralNivel = valor;
}

public void sliderUmbralCerrado( float valor ){
  umbralCerrado = valor;
}
class GuiPestanaProceso{
  
  GuiPestanaProceso(){
    float posX = width * 0.825f;
    int posY = 140, saltoY = 100;
    crearSlider( "sliderConvolucion", "Convolucion", posX, posY, 3, 10, 7 );
    crearSlider( "sliderUmbral", "Umbral", posX, posY += saltoY, 0, 255, 50 );
    crearSlider( "sliderFotogramasRetardo", "Fotogramas retardo", posX, posY += saltoY, 1, 120, 2 );
    crearSlider( "sliderBlend", "Blend", posX, posY += saltoY, 0, 0.2f, 0.2f );
  }
  
  public void crearSlider( String nombre, String etiqueta, float x, float y, float minimo, float maximo, float valor ){
    gui.crearSlider( nombre, etiqueta, GuiP5.PESTANA_PROCESOS, x, y, 130, 20, minimo, maximo, valor );
  }
  
  public void ejecutar(){
    image( motor.camara, 10, 100 );
    image( motor.movimiento.imagenesAcomparar[1], 10+Motor.ANCHO_CAMARA, 100 );
    image( motor.movimiento.substraccion, 10, 100+Motor.ALTO_CAMARA );
    //image( movimiento.bitonal, ancho, 0 );
    
    PImage resultado = motor.movimiento.getImagenAnalisis();
    image(resultado, 10+Motor.ANCHO_CAMARA, 100+Motor.ALTO_CAMARA );
  }
}

public void sliderConvolucion( int valor ){
  motor.movimiento.convolucion = valor;
}

public void sliderUmbral( float valor ){
  motor.movimiento.umbral = valor;
}

public void sliderFotogramasRetardo( int valor ){
  motor.movimiento.fotogramasRetardo = valor;
}

public void sliderBlend( float valor ){
  motor.movimiento.blend = valor;
}

public void nuevoFondo(){
  motor.movimiento.recapturarFondo();
}


class Motor{
  
  String estado;
  static final String
  ESTADO_COMPROBANDO_CAMARAS = "Comprobando camaras",
  ESTADO_CAMARAS_NO_COMPATIBLES = "Camaras no compatibles",
  ESTADO_INICIAR_GUI_PESTANAS = "Iniciar GUI Pestanas",
  ESTADO_INICIAR_CAMARA = "Iniciar Camara",
  ESTADO_CORRIENDO = "Corriendo";
  
  DetallesCamara[] camarasAptas;
  DetallesCamara camaraSeleccionada;
  Capture camara;
  static final int ANCHO_CAMARA = 320, ALTO_CAMARA = 240, FPS_CAMARA = 30;
  
  PMoCap movimiento;
  UsuarioNivel uNivel = new UsuarioNivel();
  UsuarioCerrado uCerrado = new UsuarioCerrado();
  UsuarioDesequilibrio uDeseq = new UsuarioDesequilibrio();
  
  ComunicacionOSC osc;
  
  Motor(){
    osc = new ComunicacionOSC( p5 );
    estado = ESTADO_COMPROBANDO_CAMARAS;
    //println( Capture.list() );
    thread( "reconocerCamaras" );
  }
  
  public String[] getNombresCamarasAptas(){
    String[] nombres = new String[ camarasAptas.length ];
    for( int i = 0; i < camarasAptas.length; i++ )
      nombres[ i ] = camarasAptas[ i ].nombre;
    return nombres;
  }
  
  public void ejecutar(){
    
    switch( estado ){
      case ESTADO_COMPROBANDO_CAMARAS:
        break;
      case ESTADO_INICIAR_GUI_PESTANAS:
        iniciarGuiPestanas();
        break;
      case ESTADO_INICIAR_CAMARA:
        iniciarCamara();
        break;
      case ESTADO_CORRIENDO:
        corriendo();
        break;
      default:
        break;
    }
    
  }
  
  public void iniciarGuiPestanas(){
    try{
      gui.iniciarPestanas();
      estado = ESTADO_INICIAR_CAMARA;
    }catch( Exception e ){
      consola.printlnError( "Exception iniciar gui pestanas: " + e.getMessage() );
    }
  }
  
  public void iniciarCamara(){
    if( camara != null ){
      camara.stop();
      camara = null;
    }
    if( camaraSeleccionada != null ){
      camara = new Capture( p5, camaraSeleccionada.ancho, camaraSeleccionada.alto, camaraSeleccionada.nombre, camaraSeleccionada.fps );
      camara.start();
      
      float umbral = 50;
      int cantidadDeCuadrosDeRetardo = 2;
      
      movimiento = new PMoCap( p5, camaraSeleccionada.ancho, camaraSeleccionada.alto, umbral, 
      cantidadDeCuadrosDeRetardo, 7 );
      
    }
    estado = ESTADO_CORRIENDO;
  }
  
  public void corriendo(){
    if( camara == null ){
      consola.printlnError( "camara null en corriendo" );
      return;
    }
    if( camara.available() ){
      camara.read();
      movimiento.capturar( camara );
      uNivel.set( movimiento.getNivel( umbralNivel ) );
      uCerrado.set( movimiento.getCerrado( umbralCerrado ) );
      uDeseq.set( movimiento.getDesequilibrio( umbralEje ) );
      osc.enviarMensajesAPI( uDeseq, uNivel, uCerrado );
    }
  }
  
}

int iReco, iRecoMax=1;
public void reconocerCamaras(){
  String[] listaCamaras = Capture.list();
  iRecoMax = listaCamaras.length;
  DetallesCamara[] todasLasCamaras = new DetallesCamara[ iRecoMax ];
  IntList idCamarasAptas = new IntList();
  for( iReco = 0; iReco < iRecoMax; iReco++ ){
    todasLasCamaras[ iReco ] = new DetallesCamara( listaCamaras[ iReco ] );
    if( todasLasCamaras[iReco].ancho == Motor.ANCHO_CAMARA &&
        todasLasCamaras[iReco].alto == Motor.ALTO_CAMARA &&
        todasLasCamaras[iReco].fps == Motor.FPS_CAMARA
    ){
      idCamarasAptas.append( iReco );
    }
  }
  
  if( idCamarasAptas.size() > 0 ){
    motor.camarasAptas = new DetallesCamara[ idCamarasAptas.size() ];
    for( int i = 0; i < motor.camarasAptas.length; i++ ){
      motor.camarasAptas[ i ] = todasLasCamaras[ idCamarasAptas.get( i ) ];
    }
    motor.camaraSeleccionada = todasLasCamaras[ idCamarasAptas.get( 0 ) ];
    motor.estado = Motor.ESTADO_INICIAR_GUI_PESTANAS;
  }else{
    motor.estado = Motor.ESTADO_CAMARAS_NO_COMPATIBLES;
  }
  
}

class DetallesCamara{
  
  String nombre;
  int ancho, alto;
  int fps;
  
  DetallesCamara( String info ){
    nombre = tomarValor( "name=", info );
    String tamano = tomarValor( "size=", info );
    int separador = tamano.indexOf( "x" );
    if( separador != -1 ){
      ancho = Integer.parseInt( tamano.substring( 0, separador ) );
      alto = Integer.parseInt( tamano.substring( separador + 1 ) );
    }
    fps = Integer.parseInt( tomarValor( "fps=", info ) ); 
  }
  
  public String tomarValor( String variable, String datos ){
    String valor = "";
    if( datos.indexOf( variable ) != -1 ){
      int desde = datos.indexOf( variable ) + variable.length();
      int hasta = datos.indexOf( ",", desde );
      valor = hasta != -1 ? datos.substring( desde, hasta ) : datos.substring( desde );
    }
    return valor;
  }
  
}
//---------------------------------------------
// version 1.10 actualizada al 24/Nov/2013
// clase Processing Motion Capture
// Emiliano Causa @2011
// emiliano.causa@gmail.com
//---------------------------------------------
/*
============ PMoCap =========================================================================
 
 constructor: PMoCap( int ancho_, int alto_, float umbral_, int fotogramasRetardo, boolean comparaConFondo_ )
 
 ancho y alto: dimensiones del cuadro
 umbral: es el umbral de diferencia entre (0,255).
 fotogramasRetardo: cuadros de retardo para hacer la captura.
 comparaConFondo: true -> la substraccion se hace con un fondo fijo
 false -> la substraccion se hace contra un cuadro retardado
 
 void capturar( Capture entrada ) - hace el proceso de captura de movimiento
 
 void recapturarFondo() - toma el siguiente cuadro como fondo fijo para la substraccion
 
 PImage getalisis() - devuelve una PImage con la imagen bitonal y el analisis del movimiento
 
 boolean movEnPixel( int x, int y ) - verifica si hay movimiento en el pixel (x,y)
 boolean movEnPixel( int x, int y, PImage imagen ) - idem. pero sobreimprime el resultado en el PImage pasado como parametro
 
 float movEnArea( int x, int y, int ancho_, int alto_ ) - devuelve el porcentaje normalizado de movimiento dentro de un area rectangular
 float movEnArea( int x, int y, int ancho_, int alto_, PImage imagen ) - idem. pero sobreimprime el resultado en el PImage pasado como parametro
 
 variables:
 .area = superficie del movimiento
 .x e .y = posicion del centro de gravedad del movimiento
 .arriba .abajo .derecha .izquierda = limites del area de movimiento
 .imagenAcomparar = PImage del imagen para la substraccion
 .substraccion = PImage de la substraccion para la substraccion
 .bitonal = PImage resultado de la substraccion pasada por el umbral
 
 ======== BufferVideo ========================================================================
 
 constructor:
 BufferVideo( int ancho_, int alto_, int cantidad_ )
 ancho y alto: dimensiones
 cantidad: cantidad de fotogramas de la secuencia (tiempo del retardo)
 
 void cargar( Capture imagen ) - carga una nueva imagen.
 
 PImage punteroPrimero() - muestra el primer fotograma que debe salir de la espera.
 
 */
class PMoCap {

  // ancho y alto de la imagen para procesar
  int ancho, alto;
  // cantidad de pixeles de la imagen
  int largo;
  // variable que indica si existe actualmente un fondo captado
  boolean fondoTomado;
  // variable que indica si existe movimiento en el fotograma actual
  boolean hayMovimiento;
  // variable que indica el tipo de captura, "compara con fondo" o "compara con fotograma anterior"
  //boolean comparaConFondo;

  // objetos PImage para almacenar el fondo actual de comparación
  PImage[] imagenesAcomparar = new PImage[2];
  // objetos PImage para almacenar el fondo fijo de para todo el proceso
  PImage fondoFijo;
  // objetos PImage para almacenar el resultado de la substracción
  PImage substraccion;
  // objetos PImage para almacenar el resultado del filtro bitonal
  PImage bitonal;

  // variable del valor de umbral del filtro bitonal
  float umbral;

  // variables que indican las posiciones límites del área de movimiento
  int arriba, abajo, derecha, izquierda;
  // area del movimiento
  int area;
  // X e Y del centro de gravedad del área del movimiento
  float x, y;
  //X e Y del centro del bounding box
  float xBoundingBox, yBoundingBox;
  // variables de ancho y alto del bounding box
  int anchoBoundingBox, altoBoundingBox;
  int convolucion;
  // X e Y del centro de gravedad del área del movimiento
  VerticeDeControl[] verticesDeControl = new VerticeDeControl[16];

  // buffer de almacenamiento de imágenes
  BufferVideo retardo;  
  int fotogramasRetardo;
  float blend;
  //UI
  //-_-//UI ui;
  //-------------------------------
  //constructor de la clase
  PMoCap(PApplet p5, int ancho_, int alto_, float umbral_, int fotogramasRetardo_, /*boolean comparaConFondo_,*/ int convolucion_) {
    convolucion = convolucion_;


    //-_-//ui = new UI(p5, width-150, 0, 20);
    //-_-//ui.crearSlider("convolucion", 3, 10, convolucion_);
    //se inician las propiedades con los parametros
    //comparaConFondo = comparaConFondo_;
    ancho = ancho_;
    alto = alto_;
    largo = ancho*alto;

    umbral = umbral_;
    //-_-//ui.crearSlider("umbral", 0, 255, umbral_);
    fotogramasRetardo = fotogramasRetardo_;
    //-_-//ui.crearSlider("fotogramasRetardo", 1, 120, fotogramasRetardo_);
    //indica que aun no hay ningún fondo captado
    fondoTomado = false;

    //inicializa el buffer
    retardo = new BufferVideo( ancho, alto, fotogramasRetardo_ );

    //inicializa los objetos PImage
    fondoFijo = createImage( ancho, alto, RGB );
    substraccion = createImage( ancho, alto, RGB );
    bitonal = createImage( ancho, alto, RGB );
    
    //blend = fotogramasRetardo_;
    blend = 0.2f;
    //-_-//ui.crearSlider("blend", 0, 0.2, fotogramasRetardo_);

    //-_-//ui.cp5.addButton("nuevofondo")
      //.setValue(0)
      //.setPosition(width-150, height-100)
      //.setSize(100, 19)
      //;
  }


  //---------------------------------------------------
  // método de captura

  public float sqDif(float y, float y_, float limite) {
    return map(sq(y-y_), 0, sq(limite), 0, limite);
  }

  public void actualizar() {
    if (convolucion%2==0)
      convolucion++;
    /*//-_-//
    convolucion = int(ui.cp5.getController("convolucion").getValue());
    if (convolucion%2==0)
      convolucion++;
    fotogramasRetardo = int(ui.cp5.getController("fotogramasRetardo").getValue());
    umbral = int(ui.cp5.getController("umbral").getValue());
    blend = ui.cp5.getController("blend").getValue();*/
  }

  float cont = 3;
  float decremento = 0.1f;

  public void capturar( PImage entrada ) {
    actualizar();
    //println(convolucion);
    // se decide en función del método de captura "compara con fondo" 
    // o "compara con fotograma anterior"
    //if ( !comparaConFondo ) {
    // siendo el método "comparar con un fotograma anterior"
    // carga la nueva imagen en el buffer
    retardo.cargar( entrada );
    // toma una imagen del buffer como fondo actual para comparar
    imagenesAcomparar[0] = retardo.punteroPrimero();
    //} else {
    //cuando el método es "comparar con fondo" 
    if ( !fondoTomado ) {
      if (cont <= 0) {
        if (test) {
          //---- esto es para crear un fondo blanco para testear
          fondoFijo.loadPixels();
          for (int i = 0; i < fondoFijo.pixels.length; i++) {
            fondoFijo.pixels[i] = color(255);
          }
          //se indica que el fondo fijo ha sido tomado
          fondoTomado = true;
        } else {
          //si aun no se a tomado un fondo, se toma la imagen actual como nuevo fondo fijo
          fondoFijo.copy( entrada, 0, 0, ancho, alto, 0, 0, ancho, alto);

          //se indica que el fondo fijo ha sido tomado
          fondoTomado = true;
        }
      } else {
        cont-=decremento;
        //println(cont);
      }
    }
    //tomar la imagen del fondo fijo como imagen para comparar
    blendFondo(entrada, blend);
    imagenesAcomparar[1] = fondoFijo;
    //}

    //propara las dos imagenes para leer sus pixeles
    entrada.loadPixels();
    imagenesAcomparar[0].loadPixels();
    imagenesAcomparar[1].loadPixels();

    //inicializa las variables
    hayMovimiento = false;
    area = 0;
    x = 0;
    y = 0;

    int posx, posy;
    long totx = 0;
    long toty = 0;

    //usa el ciclo for para recorrer los pixeles de las imágenes
    int inicio = (convolucion-1)/2;
    int aumento = 3;
    for ( int x=inicio; x<ancho-inicio; x+=aumento) {
      for ( int y=inicio; y<alto-inicio; y+=aumento) {

        //obtiene la posicion en X e Y en función del orden del pixel
        int i = y*ancho+x;
        posx = i % ancho;
        posy = i / ancho;
        float dif = getConv_dif(entrada, imagenesAcomparar[1], i, convolucion) ;
        setConv(substraccion, i, aumento, color(dif));
        //substraccion.pixels[i] = color(dif);
        //si la diferencia supera el valor de umbral, se lo considera movimiento

        boolean conMovimiento = (dif>umbral);

        //variable de color para la image bitonal
        int bitono;

        //si hay movimiento en este pixel
        if ( conMovimiento ) {

          //el pixel de la image bitonal sera blanco
          bitono = color(255, 255, 255);

          //si no había movimiento en los pixeles anteriores
          if ( !hayMovimiento ) {
            //marca la existencia de movimiento
            hayMovimiento = true;
            //inicia los valores de los bordes del área de movimiento
            abajo = arriba = posy;
            derecha = izquierda = posx;
          }
          //actualiza los valores de los bordes del área de movimiento si
          //la posición es de un borde
          abajo = max( abajo, posy );
          arriba = min( arriba, posy );
          derecha = max( derecha, posx );
          izquierda = min( izquierda, posx );
          //suma la posición para obtener la posición promedio
          totx += posx;
          toty += posy;
          //contabiliza el nuevo pixel en el área de movimiento
          area ++;
        } else {
          // si no hubo movimiento el pixel de la imagen bitonal será negro
          bitono = color(0, 0, 0);
        }
        //pinta el pixel de la imagen bitonal
        setConv(bitonal, i, aumento, bitono);
        //bitonal.pixels[i] = bitono;
      }
    }
    //el alncho y alto dle bounding box
    anchoBoundingBox = abs(izquierda-derecha);
    altoBoundingBox = abs(abajo-arriba);

    xBoundingBox = derecha-anchoBoundingBox/2; 
    yBoundingBox = abajo-altoBoundingBox/2;
    if ( area>0 ) {
      x = totx / area;
      y = toty / area;
    }

    //actualiza los pixeles de las imagenes resultantes de la substracción y el filtro bitonal
    substraccion.updatePixels();
    bitonal.updatePixels();


    verticesDeControl[0] = new VerticeDeControl(izquierda, arriba, 45);
    for (int i=1; i<4; i++) {
      verticesDeControl[i] = new VerticeDeControl(izquierda+anchoBoundingBox/4*i, arriba, 90, 0.63f);
    }    
    verticesDeControl[4] = new VerticeDeControl(derecha, arriba, 45+90);
    for (int i=1; i<4; i++) {
      verticesDeControl[i+4] = new VerticeDeControl(derecha, arriba+altoBoundingBox/4*i, 90+90);
    }
    verticesDeControl[8] = new VerticeDeControl(derecha, abajo, 45+180);
    for (int i=1; i<4; i++) {
      verticesDeControl[i+8] = new VerticeDeControl(derecha-anchoBoundingBox/4*i, abajo, 90+180, 0.2f);
    }
    verticesDeControl[12] = new VerticeDeControl(izquierda, abajo, 45+270);
    for (int i=1; i<4; i++) {
      verticesDeControl[i+12] = new VerticeDeControl(izquierda, abajo-altoBoundingBox/4*i, 90+270);
    }

    for (int i=0; i<verticesDeControl.length; i++) {
      verticesDeControl[i].setControl(this, null);
    }
    setDistanciaPromedio();
    setDistanciaLateralPromedio();
  }

  public void conteo() {
    if ( !fondoTomado ) {
      pushStyle();
      textAlign(CENTER, CENTER);
      textSize(50);
      fill(0, 255, 0);
      text(PApplet.parseInt(cont), width/2, height/2);
      popStyle();
    }
  }
  //---------------------------------------------------
  // sobrecarga del método con parámetro del tipo Capture
  public void capturar( Capture entrada ) {
    capturar( (PImage) entrada );
  }

  public void setConv(PImage deQue, int pixel, int t, int c) {
    if (t%2==0) {
      t++;
    }
    int mov = (t-1)/2;
    for (int i=(mov*-1); i<=mov; i++) {
      for (int j=(mov*-1); j<=mov; j++) {
        int indice = pixel+i*deQue.width+j;
        deQue.pixels[indice] = c;
      }
    }
  }

  public float getConv_dif(PImage entrada, PImage imagenAcomparar, int cual, int t) {
    float difRed = 0;
    float difGreen = 0;
    float difBlue = 0;
    /*float difHue = 0;
     float difBri = 0;
     float difSat = 0;*/
    if (imagenAcomparar==null) {
      return 0;
    }
    if (t%2==0) {
      t++;
    }
    int mov = (t-1)/2;
    float cuantasSumas = 0;
    for (int i=(mov*-1); i<=mov; i++) {
      for (int j=(mov*-1); j<=mov; j++) {
        int indice = cual+i*entrada.width+j;
        int actual = entrada.pixels[indice];
        int delFondo = imagenAcomparar.pixels[indice];
        float importancia = convolucion2D(i, j, mov);
        difRed += abs( red(actual)-red(delFondo))*importancia;
        difGreen += abs( green(actual)-green(delFondo))*importancia;
        difBlue += abs( blue(actual)-blue(delFondo))*importancia;
        /* difBri += sqDif( brightness(actual), brightness(delFondo), 255)*importancia;
         difSat += sqDif( saturation(actual), saturation(delFondo), 255)*importancia;*/
        cuantasSumas+=importancia;
      }
    }
    difRed /= cuantasSumas;
    difGreen /= cuantasSumas;
    difBlue /= cuantasSumas;

    /*difBri /= cuantasSumas;
     difSat /= cuantasSumas;  */

    float difMaxima = max(max(difRed, difGreen), difBlue);
    float dif = difMaxima;
    return dif;
  }

  public float convolucion2D(float x, float y, int rango) {
    x = map(x, 0, rango, 0, 3);
    y = map(y, 0, rango, 0, 3);
    float media = 0;
    float sigma = 1.0f;
    float zx = (exp(-1*(pow(x-media, 2.0f)/(2.0f*pow(sigma, 2.0f)))) * (1.0f/(sigma*sqrt(2.0f*PI))));
    float zy = (exp(-1*(pow(y-media, 2.0f)/(2.0f*pow(sigma, 2.0f)))) * (1.0f/(sigma*sqrt(2.0f*PI))));
    float z = (zx+zy)/2;
    return map(z, 0, 0.3989423f, 0, 1);
  }

  //-------------------------------
  // método para volver a tomar una imagen de fondo
  public void recapturarFondo() {
    fondoTomado = false;
    cont = 3;
  }
  //-------------------------------
  // método para construir la imagen resultante del análisis del movimiento
  public PImage getImagenAnalisis() {

    //se inician el grafico y la imagen donde se devolverá el resultado
    PGraphics grafico = createGraphics( ancho, alto );
    PImage resultado = createImage( ancho, alto, RGB );
    // en Processing 2.0
    // PGraphics grafico = createGraphics( ancho, alto );
    // PImage resultado = createImage( ancho, alto , RGB );

    //se inicia el dibujo del grafico
    grafico.beginDraw();
    //se dibuja la imagen bitonal
    grafico.image( bitonal, 0, 0 );
    //si hay movimiento
    if ( hayMovimiento ) {
      //se dibujan el centro y el borde del area de movimiento
      grafico.stroke(0, 255, 0);
      grafico.noFill();
      grafico.rectMode( CORNERS );
      grafico.ellipse(x, y, 5, 5);
      grafico.ellipse(xBoundingBox, yBoundingBox, 5, 5);
      grafico.line(x, y, xBoundingBox, yBoundingBox);
      grafico.rect( izquierda, arriba, derecha, abajo );
      grafico.text("Area: "+area, izquierda, arriba-7);
      grafico.text("Proporcion: "+anchoBoundingBox/constrain(altoBoundingBox, width, 0.01f), izquierda, arriba-16);
      grafico.stroke(255, 255, 0);
      for (int i = 0; i<verticesDeControl.length; i++) {
        VerticeDeControl v = verticesDeControl[i];
        if (i==5 || i==15) {
          grafico.fill(255, 0, 0);
        } else if (i==6 || i==14) {
          grafico.fill(0, 255, 0);
        } else if (i==7 || i==13) {
          grafico.fill(0, 0, 255);
        } else {
          grafico.fill(255);
        }
        grafico.ellipse(v.posInicial.x, v.posInicial.y, 10, 10);
        grafico.line(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y);
        grafico.ellipse(v.posControl.x, v.posControl.y, 10, 10);
      }
    }
    // se cierra el dibujo
    grafico.endDraw();
    //se copia el grafico a la imagen
    resultado.copy( grafico, 0, 0, ancho, alto, 0, 0, ancho, alto );

    return resultado;
  }
  float distanciaPromedio = 0;
  public void setDistanciaPromedio() {

    float divisorPromedio = 0;
    for (VerticeDeControl v : verticesDeControl) {
      distanciaPromedio += (dist(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y)*v.importancia);
      divisorPromedio += v.importancia;
    }
    distanciaPromedio = distanciaPromedio/divisorPromedio;
  }

  float distanciaLateralPromedio = 0;
  public void setDistanciaLateralPromedio() {

    float divisorPromedio = 0;
    for (int i=4; i<8; i++) {
      VerticeDeControl v = verticesDeControl[i];
      distanciaLateralPromedio += (dist(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y)*v.importancia);
      divisorPromedio += v.importancia;
    }
    for (int i=12; i<16; i++) {
      VerticeDeControl v = verticesDeControl[i];
      distanciaLateralPromedio += (dist(v.posInicial.x, v.posInicial.y, v.posControl.x, v.posControl.y)*v.importancia);
      divisorPromedio += v.importancia;
    }
    distanciaLateralPromedio = distanciaLateralPromedio/divisorPromedio;
  }
  float anchoEntreVerticesDeControl = 0;
  public void setAnchoEntreVerticesDeControl() {
    float record = 0;
    float[] ancho = new float[3];
    VerticeDeControl v1_alto = verticesDeControl[5];
    VerticeDeControl v2_alto = verticesDeControl[15];
    ancho[0] = (abs(v1_alto.posControl.x - v2_alto.posControl.x));
    VerticeDeControl v1_medio = verticesDeControl[6];
    VerticeDeControl v2_medio = verticesDeControl[14];
    ancho[1] = (abs(v1_medio.posControl.x - v2_medio.posControl.x));
    VerticeDeControl v1_bajo = verticesDeControl[7];
    VerticeDeControl v2_bajo = verticesDeControl[13];
    ancho[2] = (abs(v1_bajo.posControl.x - v2_bajo.posControl.x));
    for (int i=0; i<3; i++) {
      if (record<ancho[i]) {      
        record = ancho[i];
      }
    }

    anchoEntreVerticesDeControl = record;
  }

  public int getCerrado(float umbral) {
    int cerrado = 0;
    fill(255, 0, 0);
    text("este es el cerrado: "+ distanciaPromedio/altoBoundingBox, 50, 50);
    if (distanciaPromedio/altoBoundingBox>umbral) {
      cerrado = 1;
    }
    return cerrado;
  }
  public int getSuperCerrado() {
    return getCerrado(0.08f);
  }
  public int getNivel(float umbral) {
    int nivel = 0;
    if (getSuperCerrado()==0) {
      setAnchoEntreVerticesDeControl();
    }
    //float altura = getCerrado(umbralCerrado)<1?anchoBoundingBox/max(0.001, altoBoundingBox):0.4*(anchoBoundingBox/max(0.001, altoBoundingBox))/(0.01*distanciaPromedio);
    float altura = anchoEntreVerticesDeControl/max(0.001f, altoBoundingBox);
    String print = anchoEntreVerticesDeControl+" / "+max(0.001f, altoBoundingBox)+" = "+altura;
    text(print, 0, height-50);
    //if (getCerrado(umbralCerrado)==0) {
    if (altura>umbral/1.5f) {
      nivel = 1;
    } 
    if (altura>umbral) {
      nivel = 2;
    }
    /*} else {
     if (altura>umbral/1.5*1.2) {
     nivel = 1;
     } 
     if (altura>umbral*1.2) {
     nivel = 2;
     }
     }*/

    return nivel;
  }
  public int getDesequilibrio(float umbral) {
    int eje = 0;
    if (distanciaPromedio>5) {
      //println(distanciaPromedio);
      //println(x-xBoundingBox);
      if (x-xBoundingBox>umbral) {
        eje = -1;
      } else if (x-xBoundingBox<umbral*-1) {
        eje = 1;
      }
    }
    return eje;
  }

  //-------------------------------
  // método que responde si hay movimiento en un pixel determinado
  // y dibuja el movimiento con el pixel en cuestion
  public boolean movEnPixel( int x, int y, PImage imagen ) {

    boolean valor = blue( bitonal.get( x, y ) ) > 127;
    int margen = 5;

    if ( imagen != null ) {
      int c = ( valor ? color(0, 255, 0) : color(255, 0, 0) );

      for ( int i = max(0, x-margen); i<min(x+margen, ancho); i++ ) {
        imagen.set( i, y, c );
      }
      for ( int i = max(0, y-margen); i<min(y+margen, alto); i++ ) {
        imagen.set( x, i, c );
      }
    }

    return valor;
  }
  //-------------------------------
  //sobrecarga del método anterior
  public boolean movEnPixel( int x, int y ) {
    return movEnPixel( x, y, null );
  }
  //-------------------------------
  // método que evalua la cantidad porcentual de movimiento en una región
  public float movEnArea( int x, int y, int ancho_, int alto_, PImage imagen ) {

    float cuantos = 0;

    for ( int i=0; i<ancho_; i++ ) {
      for ( int j=0; j<alto_; j++ ) {
        int posx = x+i;
        int posy = y+j;
        boolean valor = blue( bitonal.get( posx, posy ) ) > 127;

        if ( valor ) {
          cuantos++;
          if ( imagen != null ) {
            imagen.set( posx, posy, color(0, 255, 0) );
          }
        }
      }
    }

    if ( imagen != null ) {

      int c = color(255, 0, 0);
      if ( cuantos>0 ) {
        c = color(0, 255, 0);
      }

      for ( int i=x; i<x+ancho_; i++ ) {
        imagen.set( i, y, c );
        imagen.set( i, y+alto_-1, c );
      }

      for ( int i=y; i<y+alto_; i++ ) {
        imagen.set( x, i, c );
        imagen.set( x+ancho_-1, i, c );
      }
    }
    return cuantos / ( ancho_ * alto_ );
  }
  //-------------------------------
  //sobrecarga del método anterior
  public float movEnArea( int x, int y, int ancho_, int alto_ ) {
    return movEnArea( x, y, ancho_, alto_, null );
  }
  //-------------------------------

  public int getArriba() {
    return arriba;
  };

  //-------------------------------

  public int getAbajo() {
    return abajo;
  }
  //-------------------------------

  public int getDerecha() {
    return derecha;
  }
  //-------------------------------

  public int getIzquierda() {
    return izquierda;
  }
  //-------------------------------

  public int getArea() {
    return area;
  }
  //-------------------------------

  public float getX() {
    return x;
  }
  //-------------------------------

  public float getY() {
    return y;
  }
  //-------------------------------

  public float getUmbral() {
    return umbral;
  }
  //-------------------------------

  public void setUmbral( float nuevoUmbral ) {
    umbral = nuevoUmbral;
  }
  //-------------------------------
  public void blendFondo(PImage entrada, float factor2) {
    float escala = 1000;
    factor2 = factor2*escala;
    float factor1 = escala-factor2;

    if (frameCount%fotogramasRetardo == 0) {
      fondoFijo.loadPixels();
      entrada.loadPixels();
      /*PImage imagen = createImage(entrada.width, entrada.height, RGB);
       imagen.loadPixels();*/
      for (int i = 0; i < fondoFijo.pixels.length; i++) {
        float dif = brightness(entrada.pixels[i])- brightness(imagenesAcomparar[0].pixels[i]);
        /*int x = i%bitonal.width;
         int y = i/bitonal.width;*/
        if (dif<10) {
          float r = (red(fondoFijo.pixels[i])*escala*factor1+red(entrada.pixels[i])*escala*factor2)/(escala*escala);
          float g = (green(fondoFijo.pixels[i])*escala*factor1+green(entrada.pixels[i])*escala*factor2)/(escala*escala);
          float b = (blue(fondoFijo.pixels[i])*escala*factor1+blue(entrada.pixels[i])*escala*factor2)/(escala*escala);
          fondoFijo.pixels[i] = color(r, g, b);
        }
      }
      fondoFijo.updatePixels();
    }
    //se indica que el fondo
  }
}
//---------------------------------------------

class BufferVideo {

  PImage buffer[];
  int cantidad;
  int cabeza;
  int ancho, alto;

  //-------------------------------

  BufferVideo( int ancho_, int alto_, int cantidad_ ) {

    cantidad = cantidad_;
    cabeza = 0;
    ancho = ancho_;
    alto = alto_;

    buffer = new PImage[ cantidad ];
    for ( int i=0; i<cantidad; i++ ) {
      buffer[i] = createImage( ancho, alto, RGB );
    }
  }
  //-------------------------------

  public void cargar( Capture imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
    //buffer[ cabeza ] = blendImage(buffer[ cabeza-1 ], imagen, 0.999999999, 0.0001);
    cabeza = ( cabeza+1 ) % cantidad;
  }
  //-------------------------------

  public void cargar( PImage imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
    /*int indicePrevia = cabeza-1>0?cabeza-1:buffer.length-1;
     buffer[ cabeza ] = blendImage(buffer[ indicePrevia ], imagen, 0.7, 0.3);*/
    cabeza = ( cabeza+1 ) % cantidad;
  }
  //-------------------------------

  public PImage punteroPrimero() {
    return buffer[ cabeza ];
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
public class Reloj{
  
  private int millisActual, millisAnterior, deltaMillis;
  
  public Reloj(){
    
  }
  
  public int getDeltaMillis(){
    return deltaMillis;
  }
  
  public void actualizar(){
    millisAnterior = millisActual;
    millisActual = millis();
    deltaMillis = millisActual - millisAnterior;
  }
  
}
class VerticeDeControl {
  PVector posInicial;
  PVector posControl;
  float direccion;
  float importancia;
  VerticeDeControl(float x_, float y_, float direccion_) {
    posInicial = new PVector(x_, y_);
    posControl = new PVector(x_, y_);
    direccion = direccion_;
    importancia = 1;
  }
  VerticeDeControl(float x_, float y_, float direccion_, float importancia_) {
    posInicial = new PVector(x_, y_);
    posControl = new PVector(x_, y_);
    direccion = direccion_;
    importancia = importancia_;
  }
  public void setControl(PMoCap mC, PImage img) {
    float x = posInicial.x;
    float y = posInicial.y;
    boolean encontreUnPuntoMovido = mC.movEnPixel(PApplet.parseInt(x), PApplet.parseInt(y), img);
    float distancia = 0;
    while (!encontreUnPuntoMovido && distancia < 200) {  
      distancia++;
      x = x+cos(radians(direccion));
      y = y+sin(radians(direccion));
      encontreUnPuntoMovido = mC.movEnPixel(PApplet.parseInt(x), PApplet.parseInt(y), img);
    }
    if (distancia>=200) {
      x = posInicial.x;
      y = posInicial.y;
    }

    posControl.set(x, y);
  }
}
  public void settings() {  size( 800, 600 ); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ObeservadorBasico" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
