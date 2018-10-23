Reloj reloj = new Reloj();
ConsolaDebug consolaDebug = new ConsolaDebug();

public void consolaDebug() {
  reloj.actualizar();
  consolaDebug.ejecutar();
}

public final class ConsolaDebug {

  private String texto;
  private ArrayList<Alerta> alertas = new ArrayList<Alerta>();
  private color colorTexto, colorAlerta, colorSuperAlerta;
  private int tamanoTexto, tamanoAlerta;
  private boolean debug;

  private boolean verFps, verDatos, verAlertas;

  private static final float LEADIN = 1.5; //--- NUEVO!

  public ConsolaDebug() {
    texto = "";
    colorTexto = color( 255 );//color( 255 );
    colorAlerta = color(175, 194, 43);//#FF0000
    tamanoTexto = int( height * 0.12 ); //int( height * 0.023 ); //tamanoTexto = 20;
    tamanoAlerta = int( height * 0.12 ); //int( height * 0.023 ); //tamanoAlerta = 20;

    verFps = false;
    debug = verDatos = verAlertas = true;
  }

  //--------------------------------------- METODOS PUBLICOS

  //GETERS AND SETERS
  public void setDebug( boolean debug ) {
    this.debug = debug;
  }

  public void setVerFps( boolean verFps ) {
    this.verFps = verFps;
  }

  public void setVerDatos( boolean verDatos ) {
    this.verDatos = verDatos;
  }

  public void setVerAlertas( boolean verAlertas ) {
    this.verAlertas = verAlertas;
  }

  public boolean getDebug() {
    return debug;
  }

  public boolean getVerFps() {
    return verFps;
  }

  public boolean getVerDatos() {
    return verDatos;
  }

  public boolean getVerAlertas() {
    return verAlertas;
  }
  //--------

  public void println( String texto ) {
    this.texto += texto + "\n";
  }

  public void printlnAlerta( String alerta ) {
    alertas.add( new Alerta( alerta ) );
    System.out.println( alerta );
  }

  public void printlnAlerta( String alerta, color c ) {
    alertas.add( new Alerta( alerta, c ) );
    System.out.println( alerta );
  }

  public void ejecutar() {

    if ( !verDatos ) texto = "";
    if ( verFps ) texto = "fps: " + nf( frameRate, 0, 2 ) + "\n" + texto;

    if ( debug ) ejecutarDebug();
    else ejecutarNoDebug();
    texto = "";
  }

  //--------------------------------------- METODOS PRIVADOS

  private void ejecutarDebug() {
    pushStyle();

    textAlign( LEFT, TOP );
    textSize( tamanoTexto );
    textLeading( tamanoTexto * LEADIN ); 

    noStroke();

    //NUEVO rectangulo negro de fondo

    fill( 255 );
    int desde = 0, hasta = 0, iteracion = 0;
    while ( texto.indexOf( "\n", desde ) > 0 ) {

      hasta = texto.indexOf( "\n", desde );
      String aux = texto.substring( desde, hasta );

      //rect( 0, iteracion * (tamanoTexto * LEADIN), textWidth( aux ) + 3, tamanoTexto * ( LEADIN * 1.1666666 ) );

      desde = hasta + 1;
      iteracion++;
    }

    //

    fill( colorTexto );
    text( texto, 0, 3 );
    if ( !texto.equals("") ) System.out.println( texto );

    textAlign( RIGHT, BOTTOM );
    textSize( tamanoAlerta );
    imprimirAlertas( verAlertas );

    popStyle();
  }

  private void ejecutarNoDebug() {
    if ( !texto.equals("") ) System.out.println( texto );
    imprimirAlertas( false );
  }

  private void imprimirAlertas( boolean debug ) {

    float posY = tamanoAlerta + tamanoAlerta * (LEADIN * 0.16666666) ;//0.25

    for ( int i = alertas.size() - 1; i >= 0; i-- ) {

      Alerta a = alertas.get( i );
      a.ejecutar();

      if ( a.getEstado() == Alerta.ESTADO_ELIMINAR ) {
        alertas.remove( i );
      } else if ( debug ) {

        //------ NUEVO rectangulo negro de fondo

        if ( a.getEstado() == Alerta.ESTADO_MOSTRAR )
          fill( 0 );
        else
          fill( 0, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );

        rect( width - textWidth( a.getAlerta() ) - 5, posY- tamanoAlerta * ( LEADIN * 0.875 ), textWidth( a.getAlerta() ) + 5, tamanoAlerta * LEADIN );

        //------
        color colorTexto = a.tengoColor?a.m_color:colorAlerta;
        if ( a.getEstado() == Alerta.ESTADO_MOSTRAR ) {
          fill( colorTexto );
        } else {
          fill( colorTexto, map( a.getTiempo(), 0, Alerta.TIEMPO_DESAPARECER, 255, 0 ) );
        }
        text( a.getAlerta(), width, posY );
        posY += tamanoAlerta * LEADIN;

        if ( posY > height && i - 1 >= 0 ) {
          removerAlertasFueraDePantalla( i - 1 );
          return;
        }
      }
    }//end for
  }

  private void removerAlertasFueraDePantalla( int desde ) {
    for ( int i = desde; i >= 0; i-- )
      alertas.remove( i );
  }

  //clase interna y miembro
  public class Alerta {

    private String alerta;
    color m_color;
    boolean tengoColor;
    private int estado;
    public static final int
      ESTADO_MOSTRAR = 0, 
      ESTADO_DESAPARECER = 1, 
      ESTADO_ELIMINAR = 2;

    private int tiempo;
    public static final int
      TIEMPO_MOSTRAR = 5000, //3000
      TIEMPO_DESAPARECER = 2000;

    public Alerta( String alerta ) {
      this.alerta = alerta;
      estado = ESTADO_MOSTRAR;
      tengoColor = false;
    }

    public Alerta( String alerta, color c ) {
      this.alerta = alerta;
      m_color = c;
      estado = ESTADO_MOSTRAR;
      tengoColor = true;
    }

    //------------------------------ METODOS PUBLICOS

    public String getAlerta() {
      return alerta;
    }

    public int getEstado() {
      return estado;
    }

    public int getTiempo() {
      return tiempo;
    }

    public void ejecutar() {
      tiempo += reloj.getDeltaMillis();
      if ( estado == ESTADO_MOSTRAR && tiempo > TIEMPO_MOSTRAR ) {
        estado = ESTADO_DESAPARECER;
        tiempo = 0;
      } else if ( estado == ESTADO_DESAPARECER && tiempo > TIEMPO_DESAPARECER ) {
        estado = ESTADO_ELIMINAR;
      }
    }
  }
}

public class Reloj {

  private int millisActual, millisAnterior, deltaMillis;

  public Reloj() {
  }

  public int getDeltaMillis() {
    return deltaMillis;
  }

  public void actualizar() {
    millisAnterior = millisActual;
    millisActual = millis();
    deltaMillis = millisActual - millisAnterior;
  }
}
