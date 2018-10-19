import oscP5.*;
import netP5.*;
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
