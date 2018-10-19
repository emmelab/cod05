ModoSoloKinect modoSoloKinect = new ModoSoloKinect();

class ModoSoloKinect {
  String carpetaSiluetas = "siluetas/";
  
  String[] abCerr = new String[]{"abierto_","cerrado_"};
  String[] niveles = new String[]{"alto_","medio_","bajo_"};
  String[] desequilibrio = new String[]{"izq","centro","der"};
  
  PImage[][][] imgSiluetaLiteral;
  boolean activo = true;
  
  ModoSoloKinect(){
  }
  
  void cargarImagenes(){
    imgSiluetaLiteral = new PImage[2][3][3];
    for (int ac = 0; ac < abCerr.length; ac++){
      for (int nv = 0; nv < niveles.length; nv++){
       for (int des = 0; des < desequilibrio.length; des++){
        imgSiluetaLiteral[ac][nv][des] =
        loadImage( dataPath( carpetaSiluetas + abCerr[ac] + niveles[nv] + desequilibrio[des] + ".png") );
      } 
      }
    }
  }
  
  void desactivar()
  {
    
  }
  
  void activar(){
    
  }
  
  void actualizar(float centroX, float centroY, float escala){
    if (imgSiluetaLiteral == null) cargarImagenes();
    
    pushStyle();
    pushMatrix();
    translate(centroX,centroY);
    scale(escala);
    imageMode(CENTER);
    //println(consola.cerrado+" "+consola.nivel+" "+consola.eje);
    int eje = consola.eje-1;
    if (eje < 0) eje = 0;
    else if (eje > 2) eje = 2;
    image( imgSiluetaLiteral[consola.cerrado][consola.nivel][eje] , 0,0 );
    popMatrix();
    popStyle();
  } 
}
