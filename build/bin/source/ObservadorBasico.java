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

public class ObservadorBasico extends PApplet {

PApplet p5;
Reloj reloj;
Consola consola;
Paleta paleta;
Motor motor;
GuiP5 gui;

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
  public void enviarMensajesAPI( Posturas p ) {
    if( millis() < 10000 ) return;
    
    Posturas.Nivel uNivel = p.nivel;
    Posturas.Apertura uCerrado = p.apertura;
    Posturas.Desequilibrio uDeseq = p.desequilibrio;
    
    if (uNivel.entroAlto) enviarMensaje("/nivel", 0);
    else if (uNivel.entroMedio) enviarMensaje("/nivel", 1);
    else if (uNivel.entroBajo) enviarMensaje("/nivel", 2);
    if (uCerrado.abrio) enviarMensaje("/cerrado", 0);
    else if (uCerrado.cerro)enviarMensaje("/cerrado", 1);

    if ( uDeseq.estado == EstadoDesequilibrio.IZQUIERDA ) enviarMensaje("/desequilibrio", 0);
    else if ( uDeseq.estado == EstadoDesequilibrio.DERECHA ) enviarMensaje("/desequilibrio", 4);
    /*else if (uDeseq.izquierda) enviarMensaje("/desequilibrio", 1);
     else if (uDeseq.derecha) enviarMensaje("/desequilibrio", 3);*/
    else if (uDeseq.salioDerecha || uDeseq.salioIzquierda) enviarMensaje("/desequilibrio", 2);


    //---- esto navega de uno en uno o lo uso para la camara web quiza si leugo otor dia o hoy mismo pero luego si funciona bien
    /* if (uDeseq.entroIzquierda) {
     enviarMensajes("/MenuNavegarIzquierda");
     } else if (uDeseq.entroDerecha) {
     enviarMensajes("/MenuNavegarDerecha");
     } else*/
     
    /*
    ACAAA CAMBIO -BIENAL-
    if (abs(uDeseq.desequilibrio) >= 1) {
      if (frameCount % 12 == 0) {
        if (uDeseq.desequilibrio > 0) enviarMensajes("/MenuNavegarDerecha");
        else enviarMensajes("/MenuNavegarIzquierda");
      }
    }*/
    //Por lo siguiente -BIENAL-
    if ( uDeseq.estado != EstadoDesequilibrio.NULO && uDeseq.estado != EstadoDesequilibrio.CENTRO ) {
      if (frameCount % 12 == 0) {
        if ( uDeseq.estado == EstadoDesequilibrio.DERECHA ) enviarMensajes("/MenuNavegarDerecha");
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
  PESTANA_CAPTURA = "Captura",
  PESTANA_POSTURAS = "Posturas";
  
  ScrollableList listaDeCamaras;
  
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
    String[] pestanas = { PESTANA_CAMARA, PESTANA_CAPTURA, PESTANA_POSTURAS };
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
    addButton( "nuevoFondo" )
    //primero que nada desactivo el "desencadenamiento de enventos"
    .setBroadcast(false)
    .setLabel( "Capturar fondo" )
    .setWidth( 125 )
    .setHeight( 30 )
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
     
     //PESTANA 1 - CAPTURA
     float posX = width * 0.825f;
     int posY = 140, saltoY = 80, saltoCorto = 50;
     crearSlider(  "sliderUmbral", "Umbral", PESTANA_CAPTURA, posX, posY, 130, 20, 0, 255, 100 );
     crearToggle( "toggleActivarAmortiguacion", "Amortiguar", PESTANA_CAPTURA, posX, posY += saltoY, 30, 20 );
     Slider sAmor = crearSlider(  "sliderAmortiguacion", "Factor amortiguador", PESTANA_CAPTURA, posX, posY += saltoCorto, 130, 20, 0.0f, 1.0f, 0.2f );
     sAmor.setVisible( false );
     crearToggle( "toggleAutocaptura", "Autocaptura", PESTANA_CAPTURA, posX, posY += saltoY, 30, 20 );
     Slider sAuto = crearSlider(  "sliderSensibilidadAutocaptura", "Sensibilidad autocap.", PESTANA_CAPTURA, posX, posY += saltoCorto, 130, 20, 0.0f, 1.0f, 0.1f );
     sAuto.setVisible( false );
     Slider sTAuto = crearSlider(  "sliderTiempoAutocaptura", "Tiempo autocaptura", PESTANA_CAPTURA, posX, posY += saltoCorto, 130, 20, 0, 3000, 2000 );
     sTAuto.setVisible( false );
     
     //PESTANA 2 - POSTURAS
     posX = width * 0.6f;
     /*int*/ posY = 140;/*,*/ saltoY = 100;
     crearSlider(  "sliderUmbralDesequilibrio", "Umbral desequilibrio", PESTANA_POSTURAS, posX, posY, 200, 20, 0, 80, 10 );
     crearRango( "rangoNivel", "Umbrales de nivel", PESTANA_POSTURAS, posX, posY += saltoY, 200, 20, 0.0f, 1.0f, 0.45f, 0.55f );
     crearRango( "rangoApertura", "Umbrales de apertura", PESTANA_POSTURAS, posX, posY += saltoY, 200, 20, 0.0f, 1.0f, 0.45f, 0.55f );
   
  }
  
  public Slider crearSlider( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto, float minimo, float maximo, float valor ){
    Slider s = addSlider( nombre )
    .setBroadcast(false)
    .setLabel( etiqueta )
    .setSize( round(ancho), round(alto) )
    .setPosition( x, y )
    .setRange( minimo, maximo )
    .setValue( valor/*motor.movimiento.!!variable correspondiente!!*/ )
    .setSliderMode(Slider.FLEXIBLE)
    .setBroadcast(true)
    .moveTo( pestana )
    ;
    //gui.getController(nombre).getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
    s.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
    return s;
  }
  
  public void crearRango( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto, float minimo, float maximo, float valorMinimo, float valorMaximo ){
    Range r = addRange( nombre )
   // disable broadcasting since setRange and setRangeValues will trigger an event
   .setBroadcast(false) 
   .setLabel( etiqueta )
   .setPosition(x,y)
   .setSize(round(ancho),round(alto))
   .setHandleSize(20)
   .setRange(minimo,maximo)
   .setRangeValues(valorMinimo,valorMaximo)
   // after the initialization we turn broadcast back on again
   .setBroadcast(true)
   //.setColorForeground(color(255,40))
   //.setColorBackground(color(255,40))  
   .moveTo( pestana )
   ;
   
   r.getCaptionLabel().align(ControlP5.LEFT, ControlP5.BOTTOM_OUTSIDE).setPaddingY(5).toUpperCase( false );
   
  }
  
  public void crearToggle( String nombre, String etiqueta, String pestana, float x, float y, float ancho, float alto ){
    Toggle t = addToggle( nombre )
    .setBroadcast(false) 
    .setLabel( etiqueta )
    .setPosition( x, y )
    .setSize( round(ancho), round(alto) )
    .setBroadcast(true)
    .moveTo( pestana );
    t.getCaptionLabel().toUpperCase( false );
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
      case 2:
        pestanaActiva = PESTANA_POSTURAS;
        break;
      case 1:
        pestanaActiva = PESTANA_CAPTURA;
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
      case PESTANA_CAPTURA:
        pestanaProcesos();
        break;
      case PESTANA_POSTURAS:
        pestanaPosturas();
        break;
      default:
       break;
    }
    
  }
  
  public void pestanaCamara(){
    image( motor.camara, width*0.45f, height * 0.35f );
  }
  
  public void pestanaProcesos(){
    
    image( motor.camara, 10, 100 );
    image( motor.movimiento.imagenAcomparar, 10+Motor.ANCHO_CAMARA, 100 );
    image( motor.movimiento.substraccion, 10, 100+Motor.ALTO_CAMARA );
    //image( movimiento.bitonal, ancho, 0 );
    image( motor.movimiento.getImagenAnalisis(), 10+Motor.ANCHO_CAMARA, 100+Motor.ALTO_CAMARA );
    
  }
  
  public void pestanaPosturas(){
    image( motor.movimiento.getImagenAnalisis(), 10, 100 );
    textAlign( LEFT );
    fill( paleta.amarillo );
    text( "Desequilibrio: " + motor.posturas.desequilibrio.estado.toString(), 10, 440 );
    text( "Nivel: " + motor.posturas.nivel.estado.toString(), 10, 470 );
    text( "Apertura: " + motor.posturas.apertura.estado.toString(), 10, 500 );
  }
  
}

public void controlEvent(ControlEvent evento) {
  
  if( evento.isTab() ) {
    gui.setPestanaActiva( evento.getTab().getId() );
    gui.actualizarColorPestanas();
  }else if( evento.isFrom( "rangoNivel" ) ){
    motor.posturas.nivel.rangoInferior = evento.getController().getArrayValue( 0 );
    motor.posturas.nivel.rangoSuperior = evento.getController().getArrayValue( 1 );
  }else if( evento.isFrom( "rangoApertura" ) ){
    motor.posturas.apertura.rangoInferior = evento.getController().getArrayValue( 0 );
    motor.posturas.apertura.rangoSuperior = evento.getController().getArrayValue( 1 );
  }
  
}

public void SeleccionarCamara( int numeroCamara ){
  motor.camaraSeleccionada = motor.camarasAptas[ numeroCamara ];
  motor.estado = Motor.ESTADO_INICIAR_CAMARA;
}

public void nuevoFondo(){
  motor.movimiento.recapturarFondo();
}

public void sliderUmbral( float valor ){
  motor.movimiento.setUmbral( valor );
}

public void toggleActivarAmortiguacion( boolean toggle ){
  motor.movimiento.amortiguacionActivada = toggle;
  gui.getController( "sliderAmortiguacion" ).setVisible( toggle );
}

public void sliderAmortiguacion( float valor ){
  motor.movimiento.factorAmortiguacion = valor;
}

public void toggleAutocaptura( boolean toggle ){
  motor.movimiento.autocaptura = toggle;
  gui.getController( "sliderSensibilidadAutocaptura" ).setVisible( toggle );
  gui.getController( "sliderTiempoAutocaptura" ).setVisible( toggle );
}

public void sliderSensibilidadAutocaptura( float valor ){
  motor.movimiento.setSensibilidadAutocaptura( valor );
}

public void sliderTiempoAutocaptura( int valor ){
  motor.movimiento.tiempoTolenranciaAutocaptura = valor;
}

public void sliderUmbralDesequilibrio( float valor ){
  motor.posturas.desequilibrio.umbralDesequilibrio = valor;
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
  
  Movimiento movimiento;
  Posturas posturas;
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

      movimiento = new Movimiento( camaraSeleccionada.ancho, camaraSeleccionada.alto, 100, 5, true );
      posturas = new Posturas( movimiento );
     
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
      posturas.ejecutar();
      osc.enviarMensajesAPI( posturas );
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
    try{
      todasLasCamaras[ iReco ] = new DetallesCamara( listaCamaras[ iReco ] );
      if( todasLasCamaras[iReco].ancho == Motor.ANCHO_CAMARA &&
          todasLasCamaras[iReco].alto == Motor.ALTO_CAMARA &&
          todasLasCamaras[iReco].fps == Motor.FPS_CAMARA
      ){
        idCamarasAptas.append( iReco );
      }
    }catch( Exception e ){
      println( "Exception 'reconocerCamaras()': " + e.getMessage() );
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
class Movimiento extends PMoCap{
  
  boolean amortiguacionActivada;
  float factorAmortiguacion = 0.2f;
  float amor_x, amor_y, amor_yBoundingBox, amor_xBoundingBox;
  int amor_arriba, amor_abajo, amor_izquierda, amor_derecha;
  
  boolean autocaptura;
  int umbralAutocaptura, tiempoAutocaptura, tiempoTolenranciaAutocaptura = 2000;
  final int UMBRAL_MAXIMO_AUTOCAPTURA;
  
  Movimiento( int ancho, int alto, float umbral, int fotogramasRetardo, boolean comparaConFondo ){
    super( ancho, alto, umbral, fotogramasRetardo, comparaConFondo );
    UMBRAL_MAXIMO_AUTOCAPTURA = round((ancho*alto)*0.25f);
    umbralAutocaptura = round(UMBRAL_MAXIMO_AUTOCAPTURA*0.1f);
  }
  
  public void setSensibilidadAutocaptura( float sensibilidad ){
    sensibilidad = constrain( sensibilidad, 0.0f, 1.0f );
    umbralAutocaptura = round(UMBRAL_MAXIMO_AUTOCAPTURA*sensibilidad);
  }
  
  public void capturar( PImage entrada ){
    super.capturar( entrada );
    if( amortiguacionActivada ) actualizarAmortiguaciones();
    if( autocaptura ) autocaptura();
  }
  
  public void actualizarAmortiguaciones(){
    amor_x = amortiguar( x, amor_x );
    amor_y = amortiguar( y, amor_y );
    
    amor_xBoundingBox = amortiguar( xBoundingBox, amor_xBoundingBox );
    amor_yBoundingBox = amortiguar( yBoundingBox, amor_yBoundingBox );
    
    amor_arriba = amortiguar( arriba, amor_arriba );
    amor_abajo = amortiguar( abajo, amor_abajo );
    amor_izquierda = amortiguar( izquierda, amor_izquierda );
    amor_derecha = amortiguar( derecha, amor_derecha );
  }
  
  public float amortiguar( float valorCrudo, float valorFiltrado ){
    return valorCrudo * factorAmortiguacion + valorFiltrado * ( 1.0f - factorAmortiguacion );
  }
  
  public int amortiguar( int valorCrudo, int valorFiltrado ){
    return round( valorCrudo * factorAmortiguacion + valorFiltrado * ( 1.0f - factorAmortiguacion ) );
  }
  
  public void autocaptura(){
    if( hayMovimiento && area < umbralAutocaptura ){
      tiempoAutocaptura += reloj.getDeltaMillis();
      if( tiempoAutocaptura > tiempoTolenranciaAutocaptura ){
        recapturarFondo();
        consola.printlnAlerta("Autocaptura!!");
        tiempoAutocaptura = 0;
      }
    }else
      tiempoAutocaptura = 0;
  }
  
  //Metodos gets
  
  public PImage getImagenAnalisis() {
    
    if( amortiguacionActivada && hayMovimiento ){
      PGraphics grafico = createGraphics( ancho, alto, P2D );
      PImage resultado = super.getImagenAnalisis();
      //se inicia el dibujo del grafico
      grafico.beginDraw();
      grafico.image( resultado, 0, 0 );
      //se dibujan el centro y el borde del area de movimiento
      grafico.stroke( paleta.amarillo );
      grafico.noFill();
      grafico.ellipse( amor_x, amor_y, 5, 5);
      grafico.rect( amor_xBoundingBox, amor_yBoundingBox, 5, 5 );
      grafico.rectMode( CORNERS );
      grafico.rect( amor_izquierda, amor_arriba, amor_derecha, amor_abajo );
      grafico.endDraw();
      //se copia el grafico a la imagen
      resultado.copy( grafico, 0, 0, grafico.width, grafico.height, 0, 0, resultado.width, resultado.height );
      return resultado;
    }else
      return super.getImagenAnalisis();
  }
  
  public float getX(){
    return amortiguacionActivada? amor_x : x;
  }
  
  public float getY(){
    return amortiguacionActivada? amor_y : y;
  }
  
  public float getXBoundingBox(){
    return amortiguacionActivada? amor_xBoundingBox : xBoundingBox;
  }
  
  public float getYBoundingBox(){
    return amortiguacionActivada? amor_yBoundingBox : yBoundingBox;
  }
  
  public int getArriba(){
    return amortiguacionActivada? amor_arriba : arriba;
  }
  
  public int getAbajo(){
    return amortiguacionActivada? amor_abajo : abajo;
  }
  
  public int getIzquierda(){
    return amortiguacionActivada? amor_izquierda : izquierda;
  }
  
  public int getDerecha(){
    return amortiguacionActivada? amor_derecha : derecha;
  }
  //end metodos gets
  
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
 
 PImage imagenAnalisis() - devuelve una PImage con la imagen bitonal y el analisis del movimiento
 
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
  boolean comparaConFondo;

  // objetos PImage para almacenar el fondo actual de comparación
  PImage imagenAcomparar;
  // objetos PImage para almacenar el fondo fijo de para todo el proceso
  PImage fondoFijo;
  // objetos PImage para almacenar el resultado de la substracción
  PImage substraccion;
  // objetos PImage para almacenar el resultado del filtro bitonal
  PImage bitonal;

  // variable del valor de umbral del filtro bitonal
  float umbral;

  // variables que indican las posiciones límites del área de movimiento
  protected int arriba, abajo, derecha, izquierda;
  // area del movimiento
  protected int area;
  // X e Y del centro de gravedad del área del movimiento
  protected float x, y;
  // X e Y del centro del Bounding Box
  protected float xBoundingBox, yBoundingBox;

  // buffer de almacenamiento de imágenes
  BufferVideo retardo;  

  //-------------------------------
  //constructor de la clase
  PMoCap( int ancho_, int alto_, float umbral_, int fotogramasRetardo, boolean comparaConFondo_ ) {

    //se inician las propiedades con los parametros
    comparaConFondo = comparaConFondo_;
    ancho = ancho_;
    alto = alto_;
    largo = ancho*alto;
    umbral = umbral_;

    //indica que aun no hay ningún fondo captado
    fondoTomado = false;

    //inicializa el buffer
    retardo = new BufferVideo( ancho, alto, fotogramasRetardo );

    //inicializa los objetos PImage
    fondoFijo = createImage( ancho, alto, RGB );
    substraccion = createImage( ancho, alto, RGB );
    bitonal = createImage( ancho, alto, RGB );
  }
  //---------------------------------------------------
  // método de captura
  public void capturar( PImage entrada ) {

    // se decide en función del método de captura "compara con fondo" 
    // o "compara con fotograma anterior"
    if ( !comparaConFondo ) {
      // siendo el método "comparar con un fotograma anterior"
      // carga la nueva imagen en el buffer
      retardo.cargar( entrada );
      // toma una imagen del buffer como fondo actual para comparar
      imagenAcomparar = retardo.punteroPrimero();
    }
    else {
      //cuando el método es "comparar con fondo" 
      if ( !fondoTomado ) {
        //si aun no se a tomado un fondo, se toma la imagen actual como nuevo fondo fijo
        fondoFijo.copy( entrada, 0, 0, ancho, alto, 0, 0, ancho, alto);
        //se indica que el fondo fijo ha sido tomado
        fondoTomado = true;
      }
      //tomar la imagen del fondo fijo como imagen para comparar
      imagenAcomparar = fondoFijo;
    }

    //propara las dos imagenes para leer sus pixeles
    entrada.loadPixels();
    imagenAcomparar.loadPixels();

    //inicializa las variables
    hayMovimiento = false;
    area = 0;
    x = 0;
    y = 0;

    int posx, posy;
    long totx = 0;
    long toty = 0;

    //usa el ciclo for para recorrer los pixeles de las imágenes
    for ( int i=0 ; i<largo ; i++ ) {

      //obtiene la posicion en X e Y en función del orden del pixel
      posx = i % ancho;
      posy = i / ancho;

      //toma el color del pixel i-ésimo de cada una de las imágenes
      int actual = entrada.pixels[i];
      int delFondo = imagenAcomparar.pixels[i];

      // oftiene la diferencia (absoluta, es decir en valor positivo) de cada uno de los componentes
      // color: rojo, verde y azul
      float difRed = abs( red(actual) - red(delFondo) );
      float difGreen = abs( green(actual) - green(delFondo) );
      float difBlue = abs( blue(actual) - blue(delFondo) );
      // obtiene la diferencia promedio
      float gris = ( difRed + difGreen + difBlue ) / 3.0f;

      //carga el resultado de la substracción en la imagen para tal fin
      substraccion.pixels[i] = color( gris, gris, gris );

      //si la diferencia supera el valor de umbral, se lo considera movimiento
      boolean conMovimiento = gris>umbral;

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
      }
      else {
        // si no hubo movimiento el pixel de la imagen bitonal será negro
        bitono = color(0, 0, 0);
      }
      //pinta el pixel de la imagen bitonal
      bitonal.pixels[i] = bitono;
    }
    if ( area>0 ) {
      x = totx / area;
      y = toty / area;
      xBoundingBox = izquierda + (derecha-izquierda) * 0.5f;
      yBoundingBox = arriba + (abajo-arriba) * 0.5f;
    }

    //actualiza los pixeles de las imagenes resultantes de la substracción y el filtro bitonal
    substraccion.updatePixels();
    bitonal.updatePixels();
    //actualizamos entrada nomas para que podamos dibujar el PImage en un metodo image() - hgm
    entrada.updatePixels();
  }
  //---------------------------------------------------
  // sobrecarga del método con parámetro del tipo Capture
  public void capturar( Capture entrada ) {
    capturar( (PImage) entrada );
  }

  //-------------------------------
  // método para volver a tomar una imagen de fondo
  public void recapturarFondo() {
    fondoTomado = false;
  }
  //-------------------------------
  // método para construir la imagen resultante del análisis del movimiento
  public PImage getImagenAnalisis() {

    //se inician el grafico y la imagen donde se devolverá el resultado
    PGraphics grafico = createGraphics( ancho, alto, P2D );
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
      grafico.stroke( paleta.rojo );
      grafico.noFill();
      grafico.ellipse(x, y, 5, 5);
      grafico.rect( xBoundingBox, yBoundingBox, 5, 5 );
      grafico.rectMode( CORNERS );
      grafico.rect( izquierda, arriba, derecha, abajo );
    }
    // se cierra el dibujo
    grafico.endDraw();
    //se copia el grafico a la imagen
    resultado.copy( grafico, 0, 0, ancho, alto, 0, 0, ancho, alto );

    return resultado;
  }

  //-------------------------------
  // método que responde si hay movimiento en un pixel determinado
  // y dibuja el movimiento con el pixel en cuestion
  public boolean movEnPixel( int x, int y, PImage imagen ) {

    boolean valor = blue( bitonal.get( x, y ) ) > 127;
    int margen = 5;

    if ( imagen != null ) {
      int c = ( valor ? color(0, 255, 0) : color(255, 0, 0) );

      for ( int i = max(0,x-margen) ; i<min(x+margen,ancho) ; i++ ) {
        imagen.set( i, y, c );
      }
      for ( int i = max(0,y-margen) ; i<min(y+margen,alto) ; i++ ) {
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

    for ( int i=0 ; i<ancho_ ; i++ ) {
      for ( int j=0 ; j<alto_ ; j++ ) {
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

      for ( int i=x ; i<x+ancho_ ; i++ ) {
        imagen.set( i, y, c );
        imagen.set( i, y+alto_-1, c );
      }

      for ( int i=y ; i<y+alto_ ; i++ ) {
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
    for ( int i=0 ; i<cantidad ; i++ ) {
      buffer[i] = createImage( ancho, alto, RGB );
    }
  }
  //-------------------------------

  public void cargar( Capture imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
    cabeza = ( cabeza+1 ) % cantidad;
  }
  //-------------------------------

  public void cargar( PImage imagen ) {
    buffer[ cabeza ].copy( imagen, 0, 0, ancho, alto, 0, 0, ancho, alto);
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
enum EstadoDesequilibrio{ NULO, IZQUIERDA, CENTRO, DERECHA };
enum EstadoNivel{ NULO, ARRIBA, MEDIO, ABAJO };
enum EstadoApertura{ NULO, ABIERTO, INTERMEDIO, CERRADO };

class Posturas{
  
  Movimiento movimiento;
  Desequilibrio desequilibrio;
  Nivel nivel;
  Apertura apertura;
  
  Posturas( Movimiento movimiento ){
    this.movimiento = movimiento;
    desequilibrio = new Desequilibrio();
    nivel = new Nivel();
    apertura = new Apertura();
  }
  
  public void ejecutar(){
    desequilibrio.analizar();
    nivel.analizar();
    apertura.analizar();
  }
  
  //Clase interna Desequilibrio
  class Desequilibrio{
  
    EstadoDesequilibrio estado = EstadoDesequilibrio.NULO, estadoPrevio = estado;
    float umbralDesequilibrio = 10;
    
    //banderas
    boolean salioDerecha;
    boolean salioIzquierda;
    
    public void analizar(){
      
      if( !movimiento.hayMovimiento ){
        estado = EstadoDesequilibrio.NULO;
        return;
      }
      
      float diferencia = movimiento.getXBoundingBox() - movimiento.getX();
      if( abs( diferencia ) > umbralDesequilibrio ){
        estado = diferencia > 0? EstadoDesequilibrio.IZQUIERDA : EstadoDesequilibrio.DERECHA;
      }else estado = EstadoDesequilibrio.CENTRO;
      
      actualizarBanderas();
    }
    
    public void actualizarBanderas() {
      salioDerecha = salioIzquierda = false;
  
      if ( estadoPrevio != estado && estado == EstadoDesequilibrio.CENTRO ) {    
        if ( estadoPrevio == EstadoDesequilibrio.DERECHA ) {        
          salioDerecha = true;
        }
        if ( estadoPrevio == EstadoDesequilibrio.IZQUIERDA ) {       
          salioIzquierda = true;
        }
      }
      estadoPrevio = estado;
    }
    
  }
  
  //Clase interna Nivel
  class Nivel{
    
    EstadoNivel estado = EstadoNivel.NULO, estadoPrevio = estado;
    float rangoInferior = 0.45f;
    float rangoSuperior = 0.55f;
    
    //banderas
    boolean entroAlto;
    boolean entroMedio;
    boolean entroBajo;
    
    public void analizar(){
      
      if( !movimiento.hayMovimiento ){
        estado = EstadoNivel.NULO;
        return;
      }
      
      float alto = movimiento.getAbajo() - movimiento.getArriba();
      if( alto > movimiento.alto * rangoSuperior )
        estado = EstadoNivel.ARRIBA;
      else if( alto > movimiento.alto * rangoInferior )
        estado = EstadoNivel.MEDIO;
      else
        estado = EstadoNivel.ABAJO;
      
      actualizarBanderas();
      
    }
    
    public void actualizarBanderas(){
      entroAlto = entroMedio = entroBajo = false;
      
      if (estadoPrevio != estado) {
        if ( estadoPrevio == EstadoNivel.ARRIBA ) {
          if ( estado == EstadoNivel.MEDIO ) {
            entroMedio = true;
          } else if ( estado == EstadoNivel.ABAJO ) {
            entroBajo = true;
          }
        }
        if ( estadoPrevio == EstadoNivel.MEDIO ) {
          if ( estado == EstadoNivel.ARRIBA ) {
            entroAlto = true;
          } else if ( estado == EstadoNivel.ABAJO ) {
            entroBajo = true;
          }
        }
        if ( estadoPrevio == EstadoNivel.ABAJO ) {
          if ( estado == EstadoNivel.ARRIBA ) {
            entroAlto = true;
          } else if ( estado == EstadoNivel.MEDIO ) {
            entroMedio = true;
          }
        }
        estadoPrevio = estado;
      }
    }
    
  }
  
  //Clase interna Apertura
  class Apertura{
    
    EstadoApertura estado = EstadoApertura.NULO, estadoPrevio = estado;
    float rangoInferior = 0.45f;
    float rangoSuperior = 0.55f;
    
    //banderas
    boolean cerrado, cerro, abrio;
    
    public void analizar(){
      
      if( !movimiento.hayMovimiento ){
        estado = EstadoApertura.NULO;
        return;
      }
      
      float apertura = movimiento.getDerecha() - movimiento.getIzquierda();
      if( apertura > movimiento.ancho * rangoSuperior )
        estado = EstadoApertura.ABIERTO;
      else if( apertura > movimiento.ancho * rangoInferior )
        estado = EstadoApertura.INTERMEDIO;
      else
        estado = EstadoApertura.CERRADO;
      
      actualizarBanderas();
      
    }
    
    public void actualizarBanderas(){
      cerrado = estado == EstadoApertura.CERRADO;
      cerro = abrio = false;
      if ( estadoPrevio != estado ) {
        if ( estadoPrevio == EstadoApertura.ABIERTO ) {        
          cerro = true;
        } else if ( estadoPrevio == EstadoApertura.CERRADO ) {
          abrio = true;
        }
      }
      estadoPrevio = estado;
    }
    
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
  public void settings() {  size( 800, 600, P2D ); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ObservadorBasico" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
