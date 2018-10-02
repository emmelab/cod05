class VerticeDeControl {
  PVector posInicial;
  PVector posControl;
  float direccion;
  float importancia;
  VerticeDeControl(float x_, float y_, float direccion_) {
    posInicial = new PVector(x_, y_);
    posControl = new PVector(x_, y_);
    direccion = direccion_;
    importancia = 1;
  }
  VerticeDeControl(float x_, float y_, float direccion_, float importancia_) {
    posInicial = new PVector(x_, y_);
    posControl = new PVector(x_, y_);
    direccion = direccion_;
    importancia = importancia_;
  }
  void setControl(PMoCap mC, PImage img) {
    float x = posInicial.x;
    float y = posInicial.y;
    boolean encontreUnPuntoMovido = mC.movEnPixel(int(x), int(y), img);
    float distancia = 0;
    while (!encontreUnPuntoMovido && distancia < 200) {  
      distancia++;
      x = x+cos(radians(direccion));
      y = y+sin(radians(direccion));
      encontreUnPuntoMovido = mC.movEnPixel(int(x), int(y), img);
    }
    if (distancia>=200) {
      x = posInicial.x;
      y = posInicial.y;
    }

    posControl.set(x, y);
  }
}
