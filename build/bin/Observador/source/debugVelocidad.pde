void dibujarDebugVelocidad( Usuario usuario, PGraphics p3d ){
  
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
