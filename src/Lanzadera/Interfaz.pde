{
  Interfaz interfaz = new Interfaz();
}

class Interfaz implements AutoSetup, AutoDraw {
  BotonModulo lienzo, observador, carrete;

  Interfaz() {
    autoSetup.add(this);
    autoDraw.add(this);
  }
  void setup() {
    {
      float verti = height/2;
      float sepHoriz = 160;
      lienzo = new BotonModulo(new PVector(width/2-sepHoriz, verti), "lienzo", color(200, 100, 100));
      observador = new BotonModulo(new PVector(width/2, verti), "observador", color(100, 200, 100));
      carrete = new BotonModulo(new PVector(width/2+sepHoriz, verti), "carrete", color(100, 100, 200));
    }
  }
  void draw() {
    if(introActiva)intro();
  }
  
  float introTime = 0;
  boolean introActiva = true;
  void intro(){
    float introBotonModuloBase = 2, introBotonModuloSep = .05f;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*0))lienzo.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*1))observador.mostrar = true;
    if (introCheck(introBotonModuloBase+introBotonModuloSep*2))carrete.mostrar = true;
    
    introTime += dt;
    if (introTime > 999) introActiva = false;
  }
  boolean introCheck(float t){
    return introTime < t && introTime+dt >= t;
  }
}