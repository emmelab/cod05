import processing.core.PImage;

class Mod_PaletaDefault extends Modificador {

  void ejecutar(Sistema s) {
    
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OBLIGATORIO);
    
  }
  
  public void finalizar( Sistema s ){
    s.atributos.remove( Atr_Color.manager.key() );
  }
                  
  static Registrador<Mod_PaletaDefault> registrador = new Registrador() {
    public String key() {
      return "Paleta Default";
    }
     public String categoria() {return "Paleta Color";}
    public Mod_PaletaDefault generarInstancia() {
      return new Mod_PaletaDefault();
    }
  };
}
