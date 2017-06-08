class CampoIP implements AutoDraw, AutoKeyPressed, AutoMousePressed {
  boolean focus = false;
  PVector pos, tam;
  int seccion, caracter;
  String port = "50000";
  char[][] ips = new char[4][3];
  String ip = "127.0.0.1";
  color col;

  CampoIP(float x, float y, float w, float h, color col) {
    pos = new PVector(x, y);
    tam = new PVector(w, h);
    ips = new char[][]{new char[]{'1', '2', '7'}, new char[]{'0', '0', '0'}, 
      new char[]{'0', '0', '0'}, new char[]{'0', '0', '1'}};

    this.col = col;

    autoDraw.add(this);
    autoMousePressed.add(this);
    autoKeyPressed.add(this);
  }

  boolean over(float x, float y) {
    return (x > pos.x && y > pos.y && x < pos.x+tam.x && y < pos.y+tam.y);
  }

  void mousePressed() {
    focus = false;
    if (over(mouseX, mouseY)) {
      focus = true;
    }
  }

  void keyPressed() {
    if (focus) {
      if (keyCode == ENTER || keyCode == RETURN) {
        focus = false;
      } else if (keyCode == BACKSPACE) {
        caracter--;
        if (caracter < 0) {
          seccion --;
          if (seccion < 0) {
            seccion = 0;
            caracter = 0;
          } else {
            caracter = ips[seccion].length-1;
          }
        }
      } else if (key=='.') {
        if (caracter > 0) {
          seccion ++;
          if (seccion >= ips.length) {
            seccion = ips.length-1;
            caracter = ips[seccion].length-1;
            focus = false;
          } else {
            caracter = 0;
          }
        }
      } else if (key >= '0' && key <= '9') {
        if (seccion < 0) seccion = 0;
        else if (seccion >= ips.length) seccion = ips.length-1;
        if (caracter < 0) caracter = 0;
        else if (caracter >= ips[seccion].length) caracter = ips[seccion].length-1;
        ips[seccion][caracter] = key;
        caracter++;
        if (caracter >= ips[seccion].length) {
          caracter = 0;
          seccion++;
        }
      }
    }
  }

  void draw() {
    pushStyle();
    pushMatrix();
    if (focus) {
      stroke(255);
      noFill();
    } else {
      noStroke();
      fill(col);
    }
    translate(pos.x, pos.y);
    rect(0, 0, tam.x, tam.y);
    textSize(tam.y-5);
    textAlign(LEFT, CENTER);
    translate((tam.x-textWidth("999.999.999.999"))/2, tam.y/2);
    fill(focus?255:paleta.fondo);
    ip = "";
    for (int i=0; i<ips.length; i++) {
      if (i>0)ip += ".";
      boolean showZero = false || focus;
      for (int j=0; j<ips[i].length; j++) {
        if (focus && i>=seccion && j >= caracter) break;
        if (ips[i][j] != '0' || showZero || j==ips[i].length-1) {
          ip += ips[i][j];
          showZero = true;
        }
      }
      if (focus && i>=seccion) break;
    }
    text(ip + (focus && frameCount%60<30?"|":""), 0, -textAscent()/6);
    popMatrix();
    popStyle();
  }
  
  String masticarIP(String aMasticar) {
    String masticado = "";
    
    return masticado;
  }
}