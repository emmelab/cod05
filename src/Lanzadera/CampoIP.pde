class CampoIP implements AutoDraw,AutoKeyPressed {
  PVector pos,tam;
  int seccion,caracter;
  String port = "50000";
  char[][] ips = new char[4][3];
  
  CampoIP(float x, float y, float w,float h) {
    pos = new PVector(x,y);
    tam = new PVector(w,h);
  ips = new char[][]{new char[]{'1','2','7'},new char[]{'0','0','0'},
        new char[]{'0','0','0'},new char[]{'0','0','1'}};
        
    autoDraw.add(this);
    autoKeyPressed.add(this);  
  }
  
  void draw() {
    pushStyle();
    pushMatrix();
    noStroke();
    translate(pos.x,pos.y);
    fill(paleta.ips[0]);
    rect(0,0,tam.x,tam.y);
    textSize(tam.y-20);
    textAlign(TOP);
    translate((tam.x-textWidth("999.999.999.999"))/2,10);
    fill(paleta.fondo);
    String ip = "";
    for (int i=0; i<ips.length; i++) {
      for (int j=0; j<ips[i].length; j++) ip += ips[i][j];
    }
    text(ip,0,0);
    popMatrix();
    popStyle();
  }
}