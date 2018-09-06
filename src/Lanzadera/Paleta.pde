class Paleta implements AutoSetup {
  color play,inactivo,ayuda,mas,fondo,panelSuperior,marca;
  color[] ips;
  
  Paleta(){
    autoSetup.add(this);
  }
  
  Paleta(String nombre){
    autoSetup.add(this);
    setNombre( nombre );
  }
  
  void setup() {
    play = color(#BEBE40);
    inactivo = color(#3E4545);
    ips = new color[]{color(#B44343),color(#B44382),color(#7543B4)};
    ayuda = color(#43B478);
    mas = color(#3A4040);
    fondo = color(#1A1D1E);
    panelSuperior = color(#141516);
    marca = color(#42494A);
    debug( true );
  }
  
    //Implementaciones Nombre
  String nombre = "<vacio>";
  void setNombre( String nombre ){
    this.nombre = nombre;
    consola.printlnAlerta( "Paleta.Nombre ", color( 0, 0, 255 ) );
  }
  
            //Implementaciones Debug
  void debug( boolean setup ){
    if( setup ) consola.printlnAlerta( "Construccion -> Paleta <- Interfaces: " + getImplementaciones() );
    else consola.println( "Paleta->Interfaces: " + getImplementaciones() );
  }
  /*
  void debug(){
    consola.printlnAlerta( "Paleta->Interfaces: " + getImplementaciones() );
  }*/
  
  String getImplementaciones(){
    return "AutoSetup";
  }
}
