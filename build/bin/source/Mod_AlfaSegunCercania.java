import processing.core.PVector;

class Mod_AlfaSegunCercania extends Modificador {
  int distanciaMinima=50;
  int alfaMenor = 80, alfaMayor = 255;
  int alfa;
  
  void ejecutar(Sistema s) {
    
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OBLIGATORIO);
    
    //recorro cada particula del sistema (PRIMER FOR)
    for (int i=0; i<s.tamano; i++) {
      
      PVector p1 = posiciones.p[i];//tomo la posicion de la particula "A"
      float distanciaAuxiliar = 9999999;
      
      //recorro nuevamente todo el sistema para comparar la distancia de la particula "A" con el resto
      for (int j=0; j<s.tamano; j++) {// (SEGUNDO FOR)
        
        if (i!=j) {//me aseguro de no comparar la particula con si misma ( i != j)
          
          PVector p2 = posiciones.p[j];//tomo la posicion de la particula "B"
          float distancia = s.p5.dist(p1.x, p1.y, p2.x, p2.y);//distancia entre la paricula "A" y la "B"
          
          if( distancia < distanciaAuxiliar ){
            distanciaAuxiliar = distancia;//en la variable "distanciaAuxiliar" guardo la distancia mas corta
            if(distanciaAuxiliar<distanciaMinima){
              break;
            }
          }
          
        }
        
      }// (SALGO DEL SEGUNDO FOR)
      
      //pregunto si la "distanciaAuxiliar" (que es la menor) es menor a la "distanciaMinima"
      if ( distanciaAuxiliar < distanciaMinima ) {
       alfa=alfaMayor;
      }else{
        alfa=alfaMenor;
      }
      
      //TECNICA MAGICA! aplico la transparencia a la particula "A"
      colores.c[i] = (colores.c[i] & 0x00FFFFFF) | alfa << 24; 
      
    }// (SALGO DEL PRIMER FOR)
    
  }
  
  static Registrador<Mod_AlfaSegunCercania> registrador = new Registrador(){
    public String key() {return "Alfa Segun Cercania";}
    public String categoria() {return "Transparencia";}
    public Mod_AlfaSegunCercania generarInstancia(){return new Mod_AlfaSegunCercania();}
  };
}
