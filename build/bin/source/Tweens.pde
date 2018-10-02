//      10/5/2017
class Tweener {
  boolean limitado = true;
  float valorMenor = 0, valorMayor = 1;
  float duracion = 1;
  float estado = 0;
  
  private boolean errorDuracion = true;
  
  float actualizar(float dt){
    estado += dt;
    if (limitado) {
      if (duracion <= 0 && errorDuracion){
        println("ERROR duracion menor que zero");
        errorDuracion = false;
      }
      if (estado < 0) estado = 0;
      else if (estado >= duracion) estado = duracion;
    }
    return valor();
  }
  float valor(){
    return lerp(valorMenor,valorMayor,estado/duracion);
  }
  
  Tweener(){
  }
  Tweener inicializar(Tweener otro){
    return inicializar(otro.duracion,otro.valorMenor,otro.valorMayor,otro.estado,otro.limitado);
  }
  Tweener inicializar(float duracion){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  Tweener inicializar(float duracion, float valorMenor, float valorMayor){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  Tweener inicializar(float duracion, float estado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  Tweener inicializar(float duracion, float valorMenor, float valorMayor, float estado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }
  Tweener inicializar(boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  Tweener inicializar(float duracion,boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  Tweener inicializar(float duracion, float valorMenor, float valorMayor,boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  Tweener inicializar(float duracion, float estado,boolean limitado){
    return inicializar(duracion,valorMenor,valorMayor,estado,limitado);
  }  
  Tweener inicializar(float duracion, float valorMenor, float valorMayor, float estado, boolean limitado){
    this.duracion = duracion;
    this.valorMenor = valorMenor;
    this.valorMayor = valorMayor;
    this.estado = estado;
    this.limitado = limitado;
    return this;
  }
}
class TwOutQuad extends Tweener {
  float valor(){
    return lerp( valorMenor, valorMayor, pow(estado/duracion,2));
  }
}
class TwOutBack extends Tweener {
    float s = 1.70158f;
  float valor(){
    float t = estado;
    return lerp(valorMenor, valorMayor, ((t=t/duracion-1)*t*((s+1)*t + s) + 1));
  }
}
class TwInOutBack extends Tweener {
    float s = 1.70158f;
  float valor(){
    float tempS = s;
    float t = estado;
    if ((t/=duracion/2) < 1) return lerp(valorMenor,valorMayor,1./2*(t*t*(((tempS*=(1.525f))+1)*t - tempS)));
    return lerp(valorMenor,valorMayor, 1./2*((t-=2)*t*(((tempS*=(1.525f))+1)*t + tempS) + 2));
  }
}
