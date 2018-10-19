class BDD {
  //--------------------- ESTRUCTURA RUEDA ---------------------
  float ruedaX = width/2;
  float ruedaY = height/5+(height-height/5-height/10)/2;
  float ruedaDiametro =width>height?(height*7/10)/3:(width*7/10)/3;

  //--------------------- ESTRUCTURA MONITOR ---------------------
  float monitorX = width/2;
  float monitorY = height/10;
  float monitorDiametro = width<height? width/17 : height/17;
  float baseMonitorX =0;
  float baseMonitorY =0;
  float baseMonitorAncho =width; 
  float baseMonitorAlto =height/5;

  //--------------------- ESTRUCTURA MAQUINARIAS ---------------------
  float baseMaquinariasX =0;
  float baseMaquinariasY =height-height/10;
  float baseMaquinariasAncho =width;
  float baseMaquinariasAlto =height/10;  

  //-----------------------globales de toda la vida
  boolean interaccionConMouse = false;

 
}
