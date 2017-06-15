class CampoIP implements AutoDraw, AutoKeyPressed, AutoMousePressed {
  boolean focus = false, borrarTodo = true;
  PVector pos, tam;
  int seccion, caracter;
  String port = "12000", portInput;
  //char[][] ips = new char[4][3];
  String ip = "127.0.0.1", ipInput = ip;
  color col;

  CampoIP(float x, float y, float w, float h, color col) {
    pos = new PVector(x, y);
    tam = new PVector(w, h);
    /*ips = new char[][]{new char[]{'1', '2', '7'}, new char[]{'0', '0', '0'}, 
      new char[]{'0', '0', '0'}, new char[]{'0', '0', '1'}};*/

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
    if(!focus){
      borrarTodo=true;
        ipInput = ip;
    }
  }

  void digerirIp(){
    focus = false;
    borrarTodo = true;
    String[] partesIp = ipInput.split("\\.");
    ipInput = "";
    for (int i=0; i<4; i++){
      if (i!=0)ipInput+=".";
      if (i<partesIp.length){
        if (partesIp[i].length() == 0) ipInput += "0";
        else if (partesIp[i].length() > 3) ipInput += partesIp[i].substring(0,3);
        else ipInput += partesIp[i];
      }
      else ipInput += "0";
    }
    ip = ipInput;
  }

  void keyPressed(){
    if (focus) {
      if (keyCode == ESC) {
        keyCode = RETURN;
        key = ' ';
        focus = false;
        borrarTodo = true;
        ipInput = ip;
      }
      else if (keyCode == BACKSPACE) {
        if (borrarTodo) ipInput = "";
        else if (ipInput.length()>0) ipInput = ipInput.substring(0,ipInput.length()-1);
        borrarTodo = false;
      }
      else{
        borrarTodo = false;
        if (keyCode == ENTER || keyCode == RETURN){
        digerirIp();
      }
      else if (key == '.') {
        if (ipInput.matches("[0-9]*\\.[0-9]*\\.[0-9]*\\.[0-9]*")){
          digerirIp();
        }
        else ipInput += key;
      }
      else if (key >= '0' && key <= '9') {
         ipInput += key;
      }}
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
    /*for (int i=0; i<ips.length; i++) {
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
    }*/
    text(ipInput + (focus && frameCount%60<30?"|":""), 0, -textAscent()/6);
    popMatrix();
    popStyle();
  }
  
  String masticarIP(String aMasticar) {
    String masticado = "";
    
    return masticado;
  }
}