class UsuarioDesequilibrio {
  SimpleOpenNI  context;
  int idUsuario;

  int estabilidad, minimoEstabilidad;

  PVector centroMasa2D, centroMasa, centroCaja; //centro del boundingbox  
  float umbralMenor,umbralMaximo;
  int[] userMap;

  float[] memoriaDesequilibrio;
  int punteroMemoriaDesequilibrio;
  float desequilibrioBruto,desequilibrio;
  boolean izquierda, derecha;
  boolean izquierdaBruto, derechaBruto;
  boolean entroIzquierda, salioIzquierda, entroDerecha, salioDerecha;
  boolean entroIzquierdaBruto, salioIzquierdaBruto, entroDerechaBruto, salioDerechaBruto;

  UsuarioDesequilibrio(SimpleOpenNI context, int idUsuario, int minimoEstabilidad, float umbralMenor, float umbralMaximo) {
    this.context = context;
    this.idUsuario = idUsuario;
    this.minimoEstabilidad = minimoEstabilidad;
    this.umbralMenor = umbralMenor;
    this.umbralMaximo = umbralMaximo;
    memoriaDesequilibrio = new float[minimoEstabilidad];

    centroMasa2D = new PVector();
    centroMasa = new PVector();
    centroCaja = new PVector();
  }

  void actualizar() {
    if (context.getCoM(idUsuario, centroMasa)) {
      context.convertRealWorldToProjective(centroMasa, centroMasa2D);
      actualizarCentroCaja();
      
      actualizarValor();
      actualizarBooleanas();
    }
  }

  void actualizarValor(){
    desequilibrioBruto = (centroCaja.x - centroMasa2D.x) / umbralMaximo;
    
    memoriaDesequilibrio[punteroMemoriaDesequilibrio] = desequilibrioBruto;
    punteroMemoriaDesequilibrio++;
    if (punteroMemoriaDesequilibrio >= memoriaDesequilibrio.length) punteroMemoriaDesequilibrio %= memoriaDesequilibrio.length;
    
    desequilibrio = 0;
    for(Float f : memoriaDesequilibrio){
      desequilibrio += f;
    }
    desequilibrio /= memoriaDesequilibrio.length;
  }
  void actualizarBooleanas() {
    
    boolean izqAnt = izquierda;
    boolean derAnt = derecha;
    boolean izqBrutAnt = izquierdaBruto;
    boolean derBrutAnt = derechaBruto;

    if (centroCaja.x < centroMasa2D.x - umbralMenor) {
      izquierdaBruto = true;
      derechaBruto = false;
    } else if (centroCaja.x > centroMasa2D.x + umbralMenor) {
      izquierdaBruto = false;
      derechaBruto = true;
    } else {
      izquierdaBruto = derechaBruto = false;
    }

    entroIzquierdaBruto = izquierdaBruto && !izqBrutAnt;
    salioIzquierdaBruto = !izquierdaBruto && izqBrutAnt;
    entroDerechaBruto = derechaBruto && !derBrutAnt;
    salioDerechaBruto = !derechaBruto && derBrutAnt;

    if (entroIzquierdaBruto || salioIzquierdaBruto || entroDerechaBruto || salioDerechaBruto) {
      estabilidad = 0;
    } else {
      if (estabilidad < minimoEstabilidad) {
        estabilidad++;
      } else {
        izquierda = izquierdaBruto;
        derecha = derechaBruto;
      }
    }

    entroIzquierda = izquierda && !izqAnt;
    salioIzquierda = !izquierda && izqAnt;
    entroDerecha = derecha && !derAnt;
    salioDerecha = !derecha && derAnt;
  }

  void actualizarCentroCaja() {
    if (userMap == null) userMap = context.userMap();
    else context.userMap(userMap);
    
    int ancho = context.depthWidth();
    int alto = context.depthHeight();
    int minX = ancho;
    int minY = alto;
    int maxX = 0;
    int maxY = 0;
    for (int i=0; i<userMap.length; i++) {
      if (userMap[i] == idUsuario) {
        int x = i%ancho;
        int y = i/ancho;
        if (x < minX) minX = x;
        if (x > maxX) maxX = x;
        if (y < minY) minY = y;
        if (y > maxY) maxY = y;
      }
    }
    centroCaja.set( (maxX+minX)/2, (maxY+minY)/2, 0);
  }

}
