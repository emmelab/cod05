class Atr_Color extends Atributo {
  static int inicialSaturacion = 255;
  static int inicialBrillo = 255;

  int[] c;

  Atr_Color(Sistema s) {
    
    super(s);
    c = new int[s.tamano];
    s.p5.pushStyle();
    s.p5.colorMode(s.p5.RGB);
    
    float auxiliarPaleta = s.p5.random(5);
    //float auxiliarPaleta = 0;
    
    for (int i=0; i<c.length; i++) {
      if(auxiliarPaleta<1){
        paleta1(s,i);
      }else if(auxiliarPaleta<2){
        paleta2(s,i);
      }else if(auxiliarPaleta<3){
        paleta3(s,i);
      }else if(auxiliarPaleta<4){
        paleta4(s,i);
      }else{
        paleta5(s,i);
      }

      //c[i] = s.p5.color(s.p5.random(255), inicialSaturacion, inicialBrillo);//Si, hacer random en el primer momento de alguna cosa, no queda mal
      //c[i] = s.p5.color( (255f*i)/coleccion.length , 255, 255); //Esto reparte los colores equitativamente
    }
    //s.p5.random(100)
    s.p5.popStyle();
    
  }
  
  
  private void paleta1(Sistema s, int i){
    float auxColor=s.p5.random(5);
    if(auxColor<1){
      c[i] = s.p5.color(250,10,91); 
    }else if(auxColor<2){
       c[i] = s.p5.color(125,67,186);
    }else if(auxColor<3){
      c[i] = s.p5.color(0,196,196);
    }else if(auxColor<4){
       c[i] = s.p5.color(255,214,61);
    }else{
        c[i] = s.p5.color(229,12,118);
    }  
  }
  
  private void paleta2(Sistema s, int i){
    float auxColor=s.p5.random(5);
    if(auxColor<1){
      c[i] = s.p5.color(0,208,236); 
    }else if(auxColor<2){
       c[i] = s.p5.color(118,199,31);
    }else if(auxColor<3){
      c[i] = s.p5.color(255,167,0);
    }else if(auxColor<4){
       c[i] = s.p5.color(253,81,0);
    }else{
        c[i] = s.p5.color(64,207,181);
    }  
  }
  
  private void paleta3(Sistema s, int i){
    float auxColor=s.p5.random(5);
    if(auxColor<1){
      c[i] = s.p5.color(76,15,75); 
    }else if(auxColor<2){
       c[i] = s.p5.color(217,0,0);
    }else if(auxColor<3){
      c[i] = s.p5.color(255,45,0);
    }else if(auxColor<4){
       c[i] = s.p5.color(255,140,0);
    }else{
        c[i] = s.p5.color(4,117,111);
    }
  }
  
  private void paleta4(Sistema s, int i){
    float auxColor=s.p5.random(5);
    if(auxColor<1){
      c[i] = s.p5.color(112,61,71); 
    }else if(auxColor<2){
       c[i] = s.p5.color(168,67,92);
    }else if(auxColor<3){
      c[i] = s.p5.color(222,224,240);
    }else if(auxColor<4){
       c[i] = s.p5.color(117,179,189);
    }else{
        c[i] = s.p5.color(88,60,63);
    }
  }
  
  private void paleta5(Sistema s, int i){
    float auxColor=s.p5.random(4);
    if(auxColor<1){
      c[i] = s.p5.color(245,1,0); 
    }else if(auxColor<2){
       c[i] = s.p5.color(250,217,0);
    }else if(auxColor<3){
       c[i] = s.p5.color(25,177,51);
    }else {
      c[i] = s.p5.color(27,89,252);
    }
  }

  static Manager<Atr_Color> manager = new Manager() {
    public String key() { 
      return "Color";
    }
    public Atr_Color generarInstancia(Sistema s) { 
      return new Atr_Color(s);
    }
  };
}
