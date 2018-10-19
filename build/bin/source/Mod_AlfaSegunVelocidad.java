import processing.core.PVector;

class Mod_AlfaSegunVelocidad extends Modificador {
  float velocidadMenor = 0, velocidadMayor = 5;
  int alfaMenor = 0, alfaMayor = 255;
  
  void ejecutar(Sistema s) {
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OBLIGATORIO);

    for (int i=0; i<s.tamano; i++) {
      float magnitud2V = velocidades.v[i].magSq();
      //PVector v = velocidades.v[i];

      //calculo alfa segun la velocidad
      int alfa = (magnitud2V <= velocidadMenor ? alfaMenor : 
      ( magnitud2V < velocidadMayor )? s.p5.floor( s.p5.map(magnitud2V, velocidadMenor, velocidadMayor, alfaMenor, alfaMayor) ) : alfaMayor );

      //asi que tuve que hacer asi
      //colores.c[i] = s.p5.color( s.p5.red(colores.c[i]), s.p5.green(colores.c[i]), s.p5.blue(colores.c[i]), alfa );
      
      //TECNICA MAGICA!
      colores.c[i] = (colores.c[i] & 0x00FFFFFF) | alfa << 24; 
    }
  }
  
  static Registrador<Mod_AlfaSegunVelocidad> registrador = new Registrador(){
    public String key() {return "Alfa Segun Velocidad";}
    public String categoria() {return "Transparencia";}
    public Mod_AlfaSegunVelocidad generarInstancia(){return new Mod_AlfaSegunVelocidad();}
  };
}
