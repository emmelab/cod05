class Estimulo extends Opcion{

   Estimulo(String nombre_) {
    nombre = nombre_;
    //  modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = new color[4][12];
    for (int i=0; i<5; i++) {
      for (int j=0; j<12; j++) {
      paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    iconos = new Iconos(int(t));
    pos = new PVector();
    posCentro = new PVector();
  }

  Estimulo(String nombre_, color[][] paleta_) {
    nombre = nombre_;
    //modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = paleta_;
    iconos = new Iconos(int(t));
    pos = new PVector();
    posCentro = new PVector();
  }
 
}
