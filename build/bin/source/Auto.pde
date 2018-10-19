abstract class Nombre{
  String nombre;
  void setNombre( String nombre ){
    this.nombre = nombre;
    //consola.printlnAlerta( getClass() + ".Nombre = " + nombre, color( 0,255, 224 ) );
  }
  String getNombre(){
    return nombre;
  }
}

abstract class Auto extends Nombre { 
  boolean autoActivo; 
  void setAutoActivo( boolean autoActivo ){ 
    this.autoActivo = autoActivo; 
  } 
  boolean getAutoActivo(){ 
    return autoActivo; 
  } 
} 
