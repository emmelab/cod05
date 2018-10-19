class Opcion {
  color col;
  int cant;
  boolean sensible;
  // boolean estado;
  String nombre;
  PVector pos;
  PVector posCentro;
  float t ;
  float d ;
  color[][] paleta;
  boolean conIcono = false;
  float tamFigura;
  Iconos iconos;

  Opcion() {
  }

  Opcion(String nombre_) {
    nombre = nombre_;
    //  modificadores = new ArrayList();
    col = color(255);
    t = 50;
    tamFigura = t*26/100;
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

  Opcion(String nombre_, color[][] paleta_) {
    nombre = nombre_;
    //modificadores = new ArrayList();
    col = color(255);
    t = 50;
    tamFigura = t*26/100;
    d = t*5;
    paleta = paleta_;
    iconos = new Iconos(int(t));
    pos = new PVector();
    posCentro = new PVector();
  }
  void inicializar( color col_, int cant_, PVector pos_, PVector posCentro_, float t_) {
    col = col_;
    cant = cant_;    
    pos = pos_;
    posCentro = posCentro_;
    t = t_;//width>height?height/4:width/4;
    tamFigura = t*26/100;
    iconos = new Iconos(int(tamFigura));
  }

  String getNombre() {
    String n = "no tengo";

    if (nombre!=null)
      n = nombre;
    return n;
  }

  void dibujar() {
    if (sensible)
      enSensible();

    if (conIcono) {
      dibujarIcono();
    } else {
      dibujarElipse();
    }
  }

  void setSensible(boolean sensible_/*, boolean estado_*/) {
    sensible = sensible_;
    // estado = estado_;
  }

  void dibujarIcono() {
    tint(col);
    iconos.dibujar(nombre, pos.x, pos.y);

    //-- dibujar FLecha Maquinarias.....
    if (nombre.equals(MAQUINARIAS)) {
      fill(paleta[2][0]);
      triangle(pos.x, pos.y+tamFigura*1.3, pos.x-7, pos.y+tamFigura*1.3-10, pos.x+7, pos.y+tamFigura*1.3-10);
    }
  }

  void dibujarElipse() {
    fill(col);
    noStroke();
    ellipse(pos.x, pos.y, tamFigura, tamFigura);
    textSize(15);
    fill(255);
    text(nombre, pos.x, pos.y);
  }

  void enSensible() {
    pushStyle();
    stroke(paleta[2][4]);
    fill(paleta[2][4]);
    ellipse(pos.x, pos.y, tamFigura*1.5, tamFigura*1.5);

    float angulo = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
    float d = t*59/100;

    /*for (int j =0; j< 2; j++) {
     float cX = posCentro.x+t/3*cos(angulo);
     float cY = posCentro.y+t/3*sin(angulo);
     float x1 = (cX)+((d/4)*j)*cos(angulo);
     float y1 = (cY)+((d/4)*j)*sin(angulo);
     float x2 = x1+(d/10)*cos(angulo);
     float y2 = y1+(d/10)*sin(angulo);
     float x3 = x1+(d/5.75)*cos(angulo);
     float y3 = y1+(d/5.75)*sin(angulo);
     strokeWeight(10);
     line(x1, y1, x2, y2);          
     
     strokeWeight(10);
     point(x3, y3);
     }*/
    float cX = posCentro.x+t/2*cos(angulo);
    float cY = posCentro.y+t/2*sin(angulo);
    float x1 = cX+(dist(pos.x, pos.y, cX, cY)-tamFigura/2)*cos(angulo);
    float y1 = cY+(dist(pos.x, pos.y, cX, cY)-tamFigura/2)*sin(angulo);
    strokeWeight(10);
    line(cX, cY, x1, y1);         
    popStyle();
  }
}
