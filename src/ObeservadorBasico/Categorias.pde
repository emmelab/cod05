class UsuarioNivel {
  boolean entroAlto;
  boolean entroMedio;
  boolean entroBajo;
  int nivel;
  int p_nivel;
  UsuarioNivel() {
  }
  void set(int v) {
    nivel = v;
    actualizar();
  }
  void actualizar() {
    entroAlto = false;
    entroMedio = false;
    entroBajo = false;
    if (p_nivel != nivel) {
      if (p_nivel==0) {
        if (nivel == 1) {
          entroMedio = true;
        } else if (nivel == 2) {
          entroBajo = true;
        }
      }
      if (p_nivel==1) {
        if (nivel == 0) {
          entroAlto = true;
        } else if (nivel == 2) {
          entroBajo = true;
        }
      }
      if (p_nivel==2) {
        if (nivel == 0) {
          entroAlto = true;
        } else if (nivel == 1) {
          entroMedio = true;
        }
      }
      p_nivel = nivel;
    }
  }
}
class UsuarioCerrado {
  boolean cerro;
  boolean abrio;
  boolean cerrado;
  int cerrado_valor;
  int p_cerrado_valor;
  UsuarioCerrado() {
  }
  void set(int v) {
    cerrado_valor = v;
    cerrado = (cerrado_valor == 0);
    actualizar();
  }
  void actualizar() {
    cerro = false;
    abrio = false;
    if (p_cerrado_valor != cerrado_valor) {
      if (p_cerrado_valor==0) {        
        cerro = true;
      } else if (p_cerrado_valor==1) {
        abrio = true;
      }
    }
    p_cerrado_valor = cerrado_valor;
  }
}
class UsuarioDesequilibrio { 
  /*boolean izquierda;
   boolean derecha;*/  // esto podriamos agregarlo si es necesario
  boolean salioDerecha;
  boolean salioIzquierda;
  int desequilibrio;
  int p_desequilibrio;
  UsuarioDesequilibrio() {
  }
  void set(int v) {
    desequilibrio = v;
    actualizar();
  }
  void actualizar() {
    salioDerecha = false;
    salioIzquierda = false;

    if (p_desequilibrio != desequilibrio && desequilibrio == 0) {    
      if (p_desequilibrio == 1) {        
        salioDerecha = true;
      }
      if (p_desequilibrio == -1) {       
        salioIzquierda = true;
      }
    }
    p_desequilibrio = desequilibrio;
  }
} 