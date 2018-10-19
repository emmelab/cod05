import java.io.File;
import processing.core.PImage;

class Mod_PaletaPersonalizada extends Modificador {
  
  int millisAnterior;
  static final int MILLIS_MAXIMO = 1000;

  void ejecutar(Sistema s) {
    
    Atr_Color colores = s.requerir(Atr_Color.manager, Atributo.OBLIGATORIO);
    
    int deltaMillis = s.p5.millis() - millisAnterior;
    millisAnterior = s.p5.millis();
    
    if( deltaMillis >= MILLIS_MAXIMO ){
      
      PImage paleta = getImagenPaleta( s );
      
      if( paleta != null ){
        
        paleta.loadPixels();
        
        int[] muestras = new int[ 5 ];
        for( int i = 0; i < muestras.length; i++ ){
          muestras[ i ] = paleta.pixels[ s.p5.floor( s.p5.random( paleta.pixels.length ) ) ];
        }
        
        for( int i = 0; i < s.tamano; i++ ){
          int azar = s.p5.floor( s.p5.random( muestras.length ) );
          colores.c[ i ] = muestras[ azar ];
        }
      }
      
    }
     
  }
  
  PImage getImagenPaleta( Sistema s ){
    
    File carpeta = new File( s.p5.dataPath("PaletaPersonalizada") );
    String[] archivos = carpeta.list();
    
    if( archivos != null && archivos.length > 0 ){
      
      int tresIntentos = 0;
      while( tresIntentos < 3 ){
        
        int azar = s.p5.floor( s.p5.random( archivos.length ) );
        PImage img = s.p5.loadImage( "PaletaPersonalizada/" + archivos[ azar ] );
        
        if( img != null )
          return img;
        else
          tresIntentos++;
          
      }
      return null;
      
    }else
      return null;
  }
  
  public void finalizar( Sistema s ){
    s.atributos.remove( Atr_Color.manager.key() );
  }
                  
  static Registrador<Mod_PaletaPersonalizada> registrador = new Registrador() {
    public String key() {
      return "Paleta Personalizada";
    }
     public String categoria() {return "Paleta Color";}
    public Mod_PaletaPersonalizada generarInstancia() {
      return new Mod_PaletaPersonalizada();
    }
  };
}
