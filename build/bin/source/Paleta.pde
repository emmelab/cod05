class Paleta implements AutoSetup {
  color play,inactivo,ayuda,mas,fondo,panelSuperior,marca;
  color[] ips;
  
  Paleta(){
    autoSetup.add(this);
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
  }
}