class BotonBasico implements AutoDraw, AutoMousePressed {
  PVector pos;
  PImage icono;
  float escala = 1;
  
  color col;
  
  boolean toggle,presionado;
  
  BotonBasico(float x, float y, float angulo, String icono, color col) {
    pos = new PVector(x,y,angulo);
    this.icono = iconos.get(icono);
    this.col = col;
    autoDraw.add(this);
    autoMousePressed.add(this);
  }
  
  void draw(){
    presionado = false;
    pushStyle();
    imageMode(CENTER);
    pushMatrix();
    translate(pos.x,pos.y);
    rotate(pos.z);
    scale(escala);
    tint(col);
    image(icono,0,0);
    popMatrix();
    popStyle();
  }
  void mousePressed(){
    if (over(mouseX,mouseY)){
      presionado = true;
      toggle = !toggle;
    }
  }
  
  boolean over(float x, float y) {
    return dist(x,y,pos.x,pos.y) < icono.width*escala/2;
  }
}