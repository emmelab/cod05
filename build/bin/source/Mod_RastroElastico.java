class Mod_RastroElastico extends Modificador {
  float factor = .08f;

  void ejecutar(Sistema sistema) {
    Atr_Rastro rastro = sistema.requerir(Atr_Rastro.manager, Atributo.OBLIGATORIO);
    //Atr_Velocidades velocidades = sistema.requerir(Atr_Velocidades.manager,Atributo.OBLIGATORIO);

    for (int i = 0; i < sistema.tamano; i++) {
      for (int j = 1; j < rastro.r[i].length; j++) {
        rastro.r[i][j].lerp( rastro.r[i][j-1], factor );
      }
    }
  }
                     
  static Registrador<Mod_RastroElastico> registrador = new Registrador() {
    public String key() {
      return "Rastro Elastico";
    }
     public String categoria() {return "Dibujar Rastro";}
    public Mod_RastroElastico generarInstancia() {
      return new Mod_RastroElastico();
    }
  };
}
