import controlP5.*;

class Interfaz{
    
  ControlP5 cp5;
  
  ButtonBar barra;
  
  InterfazDebugNivel interfazDebugNivel;
  
  Interfaz( String[] itemsBarra ){
        
    cp5 = new ControlP5( p5 );
    
    barra = cp5.addButtonBar( "barra" )
            .setPosition( 0, 0 )
            .setSize( p5.g.width, 20 )
            .addItems( itemsBarra );
        
    interfazDebugNivel = new InterfazDebugNivel( cp5 );
    
  }
  
  void mostrarInterfazCorrespondiente( int estado ){
    
    interfazDebugNivel.hide( estado );
    
  }
  
  
}

void barra(int n) {
  motor.setEstado( n );
}
