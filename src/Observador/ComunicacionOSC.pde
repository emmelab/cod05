import oscP5.*;
import netP5.*;

class ComunicacionOSC{
  
  OscP5 oscP5;
  NetAddress direccionAPI;
  NetAddress direccionSistema;
  
  ComunicacionOSC(){
    
    oscP5 = new OscP5( p5, 13500); 
    direccionAPI = new NetAddress("192.168.0.102", 13000 );
    //direccionSistema = new NetAddress( "127.0.0.1", 12000 );
    direccionSistema = new NetAddress( "169.254.254.166", 12000 );
    
  }
  
  void actualizarMensajes( int user, Motor m ) {
    
    //-------------------------------------------------- HACIA LA API
    
    UsuarioCerrado uCerrado = m.cerrados.get(user);
    UsuarioNivel uNivel = m.niveles.get(user);
    UsuarioDesequilibrio uDeseq = m.desequilibrios.get(user);
    
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
    else if (uNivel.medio || uNivel.bajo) {
      /*if (uDeseq.entroIzquierda) {
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
      }*/
    }
    else if (uNivel.entroAlto) {
      if (uCerrado.cerrado) {
        enviarMensajes("/Cancelar");
      }
      else {
        enviarMensajes("/Aceptar");
      }
    }
    
    PVector jointCursor = new PVector();
    
    m.context.getJointPositionSkeleton( user, SimpleOpenNI.SKEL_RIGHT_HAND, jointCursor );
  
    float xCursor = m.espacio3D.screenX( jointCursor.x, jointCursor.y, jointCursor.z ) / m.espacio3D.width;
    float yCursor = m.espacio3D.screenY( jointCursor.x, jointCursor.y, jointCursor.z ) / m.espacio3D.height;
    
    enviarMensajeJoint( "/cursor", xCursor, yCursor, direccionAPI );
    
    //-------------------------------------------------- HACIA EL SISTEMA
    
    PVector joint = new PVector();
    
    for( int i = 0; i < m.tiposDeJoint.length; i++ ){
      
      m.context.getJointPositionSkeleton( user, i, joint );
      
      String nombreJoint = m.nombreDeJoint[ m.tiposDeJoint[ i ] ];
      float x = m.espacio3D.screenX( joint.x, joint.y, joint.z ) / m.espacio3D.width;
      float y = m.espacio3D.screenY( joint.x, joint.y, joint.z ) / m.espacio3D.height;
      
      enviarMensajeJoint( "/enviar/estimulos", nombreJoint, x, y, direccionSistema );
  
    }
    
  }
  
  void enviarMensajes(String addPat) {
    OscMessage myMessage = new OscMessage(addPat);  
    oscP5.send(myMessage, direccionAPI);
  }
  void enviarMensaje(String addPat, int data) {
    OscMessage myMessage = new OscMessage(addPat);
    myMessage.add(data);
    oscP5.send(myMessage, direccionAPI);
    oscP5.send(new OscMessage("/actualizarMovimiento"), direccionAPI);
  }
  
  void enviarMensajeJoint( String addPat, float x, float y, NetAddress direccion ){
    OscMessage mensaje = new OscMessage( addPat );
    mensaje.add( x );
    mensaje.add( y );
    oscP5.send( mensaje, direccion);
  }
  
  void enviarMensajeJoint( String addPat, String nombre, float x, float y, NetAddress direccion ){
    OscMessage mensaje = new OscMessage( addPat );
    mensaje.add( nombre );
    mensaje.add( x );
    mensaje.add( y );
    oscP5.send( mensaje, direccion);
  }

}
