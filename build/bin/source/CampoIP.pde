class CampoIP implements AutoDraw, AutoKeyPressed, AutoMousePressed {
  ConfiguracionCOD05.ConfigModulo config;
  boolean focus = false, focusPort = false, borrarTodo = true;
  PVector pos, tam, posInputPuerto,tamInputPuerto;
  String port = "12000", portInput = port;
  String ip = "127.0.0.1", ipInput = ip;
  color col;

  CampoIP(float x, float y, float w, float h, color col, float porcentajeTamInputPuerto) {
    pos = new PVector(x, y);
    tam = new PVector(w*(1-porcentajeTamInputPuerto)-3, h);
    posInputPuerto = new PVector(tam.x+3,0);
    tamInputPuerto = new PVector(w*porcentajeTamInputPuerto-6,h);

    this.col = col;

    autoDraw.add(this);
    autoMousePressed.add(this);
    autoKeyPressed.add(this);
  }

  void set(ConfiguracionCOD05.ConfigModulo config){
    this.config = config;
    set(config.ip,config.puerto);
  }

  boolean overIp(float x, float y) {
    return (x > pos.x && y > pos.y && x < pos.x+tam.x && y < pos.y+tam.y);
  }
  boolean overPuerto(float x, float y) {
    return (x > pos.x+posInputPuerto.x && y > pos.y+posInputPuerto.y &&
    x < pos.x+posInputPuerto.x+tamInputPuerto.x && y < pos.y+posInputPuerto.y+tamInputPuerto.y);
  }

  void mousePressed() {
    focusPort = focus = false;
    if (overIp(mouseX, mouseY)) {
      focus = true;
    }
    else if (overPuerto(mouseX,mouseY)){
      focusPort = true;
    }
    if(!focus && !focusPort){
      borrarTodo=true;
    }
    if(!focus){
        ipInput = ip;
    }
    if (!focusPort){
    portInput = port;
    }
  }

void set(String newIp, int newPort){
  ipInput = newIp;
  portInput = str(newPort);
  digerirPort();
  digerirIp();
}

void digerirPort(){
    focusPort = false;
    borrarTodo = true;
  int val = int(portInput);
  if (val < 1024) val = 1024;
  else if (val > 65534) val = 65534;
  port = portInput = str(val);
  if(config != null)config.puerto = val;
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
  if(config != null)config.ip = ip;
  }
  
  void keyPressed(){
    if(focus)keyPressedIp();
    else if (focusPort)keyPressedPort();
  }
  
  void keyPressedPort(){
    if (keyCode == ESC) {
        keyCode = RETURN;
        key = ' ';
        focusPort = false;
        borrarTodo = true;
        portInput = port;
      }
      else if (keyCode == BACKSPACE) {
        if (borrarTodo) portInput = "";
        else if (portInput.length()>0) portInput = portInput.substring(0,portInput.length()-1);
        borrarTodo = false;
      }
      else{
        borrarTodo = false;
        if (keyCode == ENTER || keyCode == RETURN){
        digerirPort();
      }
      else if (key >= '0' && key <= '9') {
         portInput += key;
      }
      }
  }
  void keyPressedIp(){
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
  

  void draw() {
    drawIp();
    drawPuerto();
  }
  
  void drawIp(){
    drawGeneric(focus,pos,tam,"999.999.999.999",ipInput);
    if (config.estado != EstadoModulo.REMOTO) {
    ipInput = "127.0.0.1";
    focus = false;
    }
  }
  void drawPuerto(){
    drawGeneric(focusPort,PVector.add(pos,posInputPuerto),tamInputPuerto,"65535",portInput);
    if (focusPort && config.estado == EstadoModulo.APAGADO) {
    focusPort = false;
    }
  }
  void drawGeneric(boolean focus, PVector pos, PVector tam, String tamRefText, String text){
    pushStyle();
    pushMatrix();
    if (focus) {
      stroke(paleta.play);
      noFill();
    } else {
      noStroke();
      fill(config.estado==EstadoModulo.APAGADO? paleta.inactivo:col);
    }
    translate(pos.x, pos.y);
    rect(0, 0, tam.x, tam.y);
    textSize(tam.y-5);
    textAlign(LEFT, CENTER);
    translate((tam.x-textWidth(tamRefText))/2, tam.y/2);
    fill(focus?paleta.play:paleta.fondo);
    text(text + (focus && frameCount%60<30?"|":""), 0, -textAscent()/6);
    popMatrix();
    popStyle();
  }
}
