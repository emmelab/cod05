class Categoria extends Opcion {

  ArrayList modificadores;
  int mods = 0;


  Categoria(String nombre_) {
    nombre = nombre_;
    modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = new color[4][12];
    for (int i=0; i<4; i++) {
      for (int j=0; j<12; j++) {
        paleta[i][j] = color(random(255), random(255), random(255));
      }
    }
    iconos = new Iconos(int(t));
    pos = new PVector();
    posCentro = new PVector();
  }

  Categoria(String nombre_, color[][] paleta_) {
    nombre = nombre_;
    modificadores = new ArrayList();
    col = color(255);
    t = 50;
    d = t*5;
    paleta = paleta_;
    iconos = new Iconos(int(t));
    pos = new PVector();
    posCentro = new PVector();
    conIcono = true;
  }
  /*void inicializar( float t_, color col_, int cant_, PVector pos_, PVector posCentro_) {
   col = col_;
   cant = cant_;    
   pos = pos_;
   posCentro = posCentro_;
   t = t_;
   }
   
   String getNombre() {
   String n = "no tengo";
   
   if (nombre!=null)
   n = nombre;
   return n;
   }*/

  void addMod() {
    mods++;
  }
  void removerMod() {

    if (mods>0)
      mods--;
  }

  void resetMods() {
    mods=0;
  }

  void aniadir(String nombre, color[][] paleta) {
    Modificador m = new Modificador(nombre, this, paleta);
    modificadores.add(m);
  }


  void aniadir(String nombre) {
    Modificador m = new Modificador(nombre, this);
    modificadores.add(m);
  }


  void dibujarMods() {
    float angulo = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
    float contraAngulo = angulo-PI;
    if (modificadores.size()>1) {
      float espacioAngular = radians(map(modificadores.size(), 0, 20, 0, 360));
      for (int i=0; i<modificadores.size (); i++) {
        Modificador m = (Modificador) modificadores.get(i);
        float espacioAngularDistribuido = map(i, 0, modificadores.size()-1, 0, espacioAngular);
        float x;
        float y;
        if (sensible) {
          x = pos.x+/*(t*42/100*cos(contraAngulo))+*/(t*42/100*cos(angulo-espacioAngular/2+espacioAngularDistribuido));
          y = pos.y+/*(t*42/100*sin(contraAngulo))+*/(t*42/100*sin(angulo-espacioAngular/2+espacioAngularDistribuido));
        } else {
          x = pos.x+(t*31/100*cos(angulo-espacioAngular/2+espacioAngularDistribuido));
          y = pos.y+(t*31/100*sin(angulo-espacioAngular/2+espacioAngularDistribuido));
        }
        //m.dibujar(x, y, t*0.6, col);// dibujar texto y ellipses
        m.dibujarIconos(x, y, t, col);// dibujar iconos de imagenes
      }
    } else {
      Modificador m = (Modificador) modificadores.get(0);
      float x;
      float y;
      if (sensible) {
        x = pos.x+(t*42/100*cos(angulo));
        y = pos.y+(t*42/100*sin(angulo));
      } else {
        x = pos.x+(t*31/100*cos(angulo));
        y = pos.y+(t*31/100*sin(angulo));
      }
      //m.dibujar(x, y, t*0.6, col);// dibujar texto y ellipses
      m.dibujarIconos(x, y, t, col);// dibujar iconos de imagenes
    }
  }

  void dibujarCategoria() {
    dibujar();
    dibujarMods();
    displayModificadoresExistentes();
  }

  //  void setSensible(boolean sensible_/*, boolean estado_*/) {
  /*   sensible = sensible_;
   // estado = estado_;
   }
   
   void enSensible() {
   pushStyle();
   fill(paleta[0]);
   ellipse(pos.x, pos.y, t*1.5, t*1.5);
   float angulo = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
   float d = t*2;
   
   for (int j =0; j< 5; j++) {
   float cX = posCentro.x+t/3*cos(angulo);
   float cY = posCentro.y+t/3*sin(angulo);
   float x1 = (cX)+((d/4)*j)*cos(angulo);
   float y1 = (cY)+((d/4)*j)*sin(angulo);
   float x2 = x1+(d/10)*cos(angulo);
   float y2 = y1+(d/10)*sin(angulo);
   float x3 = x1+(d/5.75)*cos(angulo);
   float y3 = y1+(d/5.75)*sin(angulo);
   stroke(paleta[0]);
   strokeWeight(3);
   line(x1, y1, x2, y2);          
   
   strokeWeight(5);
   point(x3, y3);
   }
   popStyle();
   }*/

  void displayModificadoresExistentes() {
    if (mods>0) {
      /*  if (coneccion == null) {
       coneccion = loadImage("coneccion.png");
       coneccion.resize(t*135/100, t*135/100);
       }*/

      float ang = atan2(pos.y-posCentro.y, pos.x-posCentro.x);
      float px = pos.x-(t*13/100/2)*cos(ang);
      float py = pos.y-(t*13/100/2)*sin(ang);
      float x = posCentro.x+(t/2-t*13/100/2)*cos(ang);
      float y = posCentro.y+(t/2-t*13/100/2)*sin(ang);

      float diam = dist(x, y, px, py);

      pushStyle();
      strokeWeight(2);
      stroke(150, 150, 220);
      noFill();
      float xar1 = pos.x+t*16/100*cos(ang+radians(90));
      float yar1 = pos.y+t*16/100*sin(ang+radians(90));
      float xab1 = posCentro.x+t*16/100*cos(ang+radians(90));
      float yab1 = posCentro.y+t*16/100*sin(ang+radians(90));
      float xar2 = pos.x+t*16/100*cos(ang-radians(90));
      float yar2 = pos.y+t*16/100*sin(ang-radians(90));
      float xab2 = posCentro.x+t*16/100*cos(ang-radians(90));
      float yab2 = posCentro.y+t*16/100*sin(ang-radians(90));
      line(xar1, yar1, xab1, yab1);
      line(xar2, yar2, xab2, yab2);
      arc(pos.x, pos.y, t*32/100, t*32/100, ang+radians(270), ang+radians(270)+radians(180));
      popStyle();
      /*  popMatrix();
       rotate(ang);
       translate(cx,cy);
       image(coneccion, 0, 0);
       pushMatrix();*/
      if (mods<4) {        
        for (int i=0; i<mods; i++) {
          x = px-diam/(mods+1)*(i+1)*cos(ang);
          y = py-diam/(mods+1)*(i+1)*sin(ang);
          pushStyle();
          fill(paleta[2][3]);
          ellipse(x, y, t*13/100, t*13/100);
          /*textAlign(CENTER, CENTER);
           fill(255);
           textSize(15);
           text(mods, x, y);*/
          popStyle();
        }
      } else {
        for (int i=0; i<3; i++) {
          x = px-diam/(4)*(i+1)*cos(ang);
          y = py-diam/(4)*(i+1)*sin(ang);
          pushStyle();
          fill(paleta[2][3]);
          ellipse(x, y, t*13/100, t*13/100);
          popStyle();
        }
      }
    }
  }
}