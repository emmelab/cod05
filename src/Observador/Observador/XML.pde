void saveDatosXML(){
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

void loadDatosXML(){
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

void loadDatos_UsuarioCerrado( XML hijo ){
  
  if( hijo != null ){
    float factorUmbral = hijo.getFloat("factorUmbral");
    UsuarioCerrado.setFactorUmbral( factorUmbral );
  }
  
}

void loadDatos_UsuarioNivel( XML hijo ){
  
  if( hijo != null){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    UsuarioNivel.setFactorUmbralBajo( factorUmbralBajo );
    
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");
    UsuarioNivel.setFactorUmbalAlto( factorUmbralAlto );
    
  }
  
}

void loadDatos_UsuarioDesequilibrio( XML hijo ){
  
  if( hijo != null ){
    
    float factorUmbralBajo = hijo.getFloat("factorUmbralBajo");
    UsuarioDesequilibrio.setUmbralMenor( factorUmbralBajo );
    
    float factorUmbralAlto = hijo.getFloat("factorUmbralAlto");
    UsuarioDesequilibrio.setUmbralMaximo( factorUmbralAlto );
    
  }
  
}

void loadDatos_Espejo( XML hijo ){
  if( hijo != null ){
    
    boolean espejoX = ( hijo.getInt( "espejoX" ) > 0 )? true : false ;
    boolean espejoY = ( hijo.getInt( "espejoY" ) > 0 )? true : false ;
    
    if( comunicacionOSC != null ){
      comunicacionOSC.setInvertidoEjeX( espejoX );
      comunicacionOSC.setInvertidoEjeY( espejoY );
    }
    
  }
}
