import processing.core.PVector;

class Mod_EspacioCerrado extends Modificador {
  //con esta variable se setea cuanta "energia" NO se pierde, NO se absorbe, con el choque contra la pared
  //es decir si vale 1.0 no se pierde nada de energia, si vale 0.8 se pierde el 20%
  float restitucion = .9f;//Voy a aplicar lo que hiciste en "Gravedad" de usar un constructor, aunque aun no estoy seguro

  //Version anterior, genial el coment, pero lo volamos en la proxima
  /*********************** Hay dos opciones a disposicion *********************************
   
   VAMOS CON LA OPCION UNO, ya fue... de todas formas, ahora las velocidades estan en cartesiano
   opcion 1:
   Calcula las componentes X e Y del vector velocidad
   a la componente que choco contra la pared la multiplica por * -absorcionDelRebote
   para invertir la direccion y desacelerar su velocidad
   luego calcula la magnitud de la nueva velocidad usando una raiz cuadrada!!!
   por eso esta opcion consume mas recursos de la PC
   
   opcion 2:
   Calcula las componentes X e Y del vector velocidad
   a la componente que choco contra la pared la multiplica por * -1 para invertir su direccion
   luego multiplica la magnitud de la velocidad por * absorcionDelRebote
   esta opcion desacelera ambas componentes (X e Y) por cada choque
   dando por resultado un efecto menos real, pero sin tanto gasto de los recursos de la PC
   
   NOTA:
   No se si las operaciones coseno y seno consumen muchos recursos calculando las componentes
   pero si se me ocurrio una opcion para evadir eso, sin embargo me parecio que iba a quedar un codigo poco legible
   por lo tanto decidi no hacerlo
   
   **************************************************************************************/

  void ejecutar(Sistema s) {
    Atr_Posicion posiciones = s.requerir(Atr_Posicion.manager, Atributo.OBLIGATORIO);
    Atr_Velocidad velocidades = s.requerir(Atr_Velocidad.manager, Atributo.OBLIGATORIO);
    Atr_Tamano tamanios = s.requerir(Atr_Tamano.manager, Atributo.OPCIONAL);

    for (int i=0; i<s.tamano; i++) {

      PVector p = posiciones.p[i];
      PVector v = velocidades.v[i];

      float radio = (tamanios != null)? tamanios.d[i]/2 : 0 ;

      //De esta forma, la particula rebota solo si va hacia la pared
      if ( p.x < radio && v.x < 0 ) {
        v.x *= -restitucion;
      } else if (p.x > s.p5.width - radio && v.x > 0) {
        v.x *= -restitucion;
      }
      if ( p.y < radio && v.y < 0 ) {
        v.y *= -restitucion;
      } else if (p.y > s.p5.height - radio && v.y > 0) {
        v.y *= -restitucion;
      }
    }
  }
    
  static Registrador<Mod_EspacioCerrado> registrador = new Registrador() {
    public String key() {
      return "Espacio Cerrado";
    }
     public String categoria() {return "Escena";}
    public Mod_EspacioCerrado generarInstancia() {
      return new Mod_EspacioCerrado();
    }
  };
}
