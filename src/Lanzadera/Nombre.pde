abstract class Nombre{
  String nombre;
  void setNombre( String nombre ){
    this.nombre = nombre;
    consola.printlnAlerta( getClass() + ".Nombre = " + nombre, color( 0,255, 224 ) );
  }
  String getNombre(){
    return nombre;
  }
}
