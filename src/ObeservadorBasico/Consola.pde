/* Consola v1.22 14/08/2017
 * Nuevo: Nuevo color alerta, nuevo metodo printlnError() color rojo;
 * Hernán GM - hernangonzalezmoreno@gmail.com
*/

public final class Consola{
  
  private String texto;
  private ArrayList<Alerta> alertas = new ArrayList<Alerta>();
  private color colorTexto, colorAlerta;
  private int tamanoTexto, tamanoAlerta;
  private boolean debug;
  
  private boolean verFps, verDatos, verAlertas;
  
  private static final float LEADIN = 1.5; //--- NUEVO!
  
  public Consola(){
    texto = "";
    colorTexto = color( #000000 );//color( 255 );
    colorAlerta = color( #CC9900 );
    tamanoTexto = int( height * 0.023 ); //int( height * 0.023 ); //tamanoTexto = 20;
    tamanoAlerta = int( height * 0.023 ); //int( height * 0.023 ); //tamanoAlerta = 20;
    
    debug = verFps = verDatos = verAlertas = true;
  }
  
  //--------------------------------------- METODOS PUBLICOS
  
  //GETERS AND SETERS
  public void setDebug( boolean debug ){
    this.debug = debug;
  }
  
  public void setVerFps( boolean verFps ){
    this.verFps = verFps;
  }
  
  public void setVerDatos( boolean verDatos ){
    this.verDatos = verDatos;
  }
  
  public void setVerAlertas( boolean verAlertas ){
    this.verAlertas = verAlertas;
  }
  
  public boolean getDebug(){
    return debug;
  }

  public boolean getVerFps(){
    return verFps;
  }

  public boolean getVerDatos(){
    return verDatos;
  }

  public boolean getVerAlertas(){
    return verAlertas;
  }
  //--------
  
  public void println( String texto ){
    this.texto += texto + "\n";
  }
  
  public void printlnAlerta( String alerta ){
    alertas.add( new Alerta( alerta ) );
    System.out.println( alerta );
  }
  
  public void printlnAlerta( String alerta, color colorPersonalizado ){
    alertas.add( new Alerta( alerta, colorPersonalizado ) );
    System.out.println( alerta );
  }
  
  public void printlnError( String alerta ){
    alertas.add( new Alerta( alerta, color( #FF0000 ) ) );
    System.err.println( alerta );
  }
  
  public void ejecutar(){
    
    if( !verDatos ) texto = "";
    if( verFps ) texto = "fps: " + nf( frameRate, 0, 2 ) + "\n" + texto;
    
    if( debug ) ejecutarDebug();
    else ejecutarNoDebug();
    texto = "";
  }
  
  //--------------------------------------- METODOS PRIVADOS
  
  private void ejecutarDebug(){
    pushStyle();
      
      textAlign( LEFT, TOP );
      textSize( tamanoTexto );
      textLeading( tamanoTexto * LEADIN ); 
      
      noStroke();
      rectMode( CORNER );
      
      //NUEVO rectangulo negro de fondo

      fill( 255 );
      int desde = 0, hasta = 0, iteracion = 0;
      while( texto.indexOf( "\n", desde ) > 0 ){

        hasta = texto.indexOf( "\n", desde );
        String aux = texto.substring( desde, hasta );
        
        rect( 0, iteracion * (tamanoTexto * LEADIN), textWidth( aux ) + 3, tamanoTexto * ( LEADIN * 1.1666666 ) );
        
        desde = hasta + 1;
        iteracion++;
      }
      
      //
      
      fill( colorTexto );
      text( texto, 0, 3 );
      if( !texto.equals("") ) System.out.println( texto );
      
      textAlign( RIGHT, BOTTOM );
      textSize( tamanoAlerta );
      imprimirAlertas( verAlertas );
      
    popStyle();
  }
  
  private void ejecutarNoDebug(){
    if( !texto.equals("") ) System.out.println( texto );
    imprimirAlertas( false );
  }
  
  private void imprimirAlertas( boolean debug ){
    
    float posY = tamanoAlerta + tamanoAlerta * (LEADIN * 0.16666666) ;//0.25
    
    for( int i = alertas.size() - 1; i >= 0; i-- ){
      
      Alerta a = alertas.get( i );
      a.ejecutar();
      
      if( a.getEstado() == Alerta.ESTADO_ELIMINAR ){
        alertas.remove( i );
      }else if( debug ){
        
        //------ NUEVO rectangulo negro de fondo
        
        if( a.getEstado() == Alerta.ESTADO_MOSTRAR )
          fill( 0 );
        else
          fill( 0, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );
        
        rect( width - textWidth( a.getAlerta() ) - 5, posY- tamanoAlerta * ( LEADIN * 0.875 ), textWidth( a.getAlerta() ) + 5, tamanoAlerta * LEADIN );
        
        //------
        
        color auxColorAlerta = a.isPersonalizado() ? a.getColorPersonalizado() : colorAlerta ;
        if( a.getEstado() == Alerta.ESTADO_MOSTRAR )
          fill( auxColorAlerta );
        else
          fill( auxColorAlerta, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );
        
        text( a.getAlerta(), width, posY );
        posY += tamanoAlerta * LEADIN;
        
        if( posY > height && i - 1 >= 0 ){
          removerAlertasFueraDePantalla( i - 1 );
          return;
        }
        
      }
      
    }//end for
    
  }
  
  private void removerAlertasFueraDePantalla( int desde ){
    for( int i = desde; i >= 0; i-- )
      alertas.remove( i );
  }
  
  //clase interna y miembro
  public class Alerta{
    
    private String alerta;
    private color colorPersonalizado;
    private boolean personalizado;
    
    private int estado;
    public static final int
    ESTADO_MOSTRAR = 0,
    ESTADO_DESAPARECER = 1,
    ESTADO_ELIMINAR = 2;
    
    private int tiempo;
    public static final int
    TIEMPO_MOSTRAR = 5000,//3000
    TIEMPO_DESAPARECER = 2000;
    
    
    //------------------------------ CONSTRUCTORES
    
    public Alerta( String alerta ){
      this.alerta = alerta;
      estado = ESTADO_MOSTRAR;
    }
    
    public Alerta( String alerta, color colorPersonalizado ){
      this.alerta = alerta;
      this.colorPersonalizado = colorPersonalizado;
      personalizado = true;
      estado = ESTADO_MOSTRAR;
    }
    
    //------------------------------ METODOS PUBLICOS
    
    public String getAlerta(){
      return alerta;
    }
    
    public int getEstado(){
      return estado;
    }
    
    public int getTiempo(){
      return tiempo;
    }
    
    public boolean isPersonalizado(){
      return personalizado;
    }
    
    public color getColorPersonalizado(){
      return colorPersonalizado;
    }
    
    public void ejecutar(){
      tiempo += reloj.getDeltaMillis();
      if( estado == ESTADO_MOSTRAR && tiempo > TIEMPO_MOSTRAR ){
        estado = ESTADO_DESAPARECER;
        tiempo = 0;
      }else if( estado == ESTADO_DESAPARECER && tiempo > TIEMPO_DESAPARECER ){
        estado = ESTADO_ELIMINAR;
      }
    }
    
  }
  
}