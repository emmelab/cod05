void dibujarDebugEspalda( Usuario usuario, PGraphics p3d ){
  
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
          p3d.fill( #0000FF );
        else
          p3d.fill( #FF0000 );
        p3d.ellipse( 0, 0, 100, 100 );
      }
      
    p3d.popMatrix();
    
  }
  
  p3d.popStyle();
  
}
