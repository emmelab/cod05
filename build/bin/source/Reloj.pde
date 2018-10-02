public class Reloj{
  
  private int millisActual, millisAnterior, deltaMillis;
  
  public Reloj(){
    
  }
  
  public int getDeltaMillis(){
    return deltaMillis;
  }
  
  public void actualizar(){
    millisAnterior = millisActual;
    millisActual = millis();
    deltaMillis = millisActual - millisAnterior;
  }
  
}
