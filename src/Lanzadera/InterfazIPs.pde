class InterfazIPs implements AutoDraw,AutoKeyPressed,AutoMousePressed,AutoMouseReleased {

  String miIP;

  ArrayList<TextBox> cajasIP = new ArrayList<TextBox>();
  ArrayList<CheckBox> check = new ArrayList<CheckBox>();
  
  Boton ejecutar;
  Boton configuracion;
  Boton guardar;
  Scroll scroll;

  int textSize;

  PImage marca;

  int estado = 0;
  Boton aceptar;

  Conexiones gestorConexiones;


  InterfazIPs(Conexiones c) {
    autoDraw.add(this);
    autoKeyPressed.add(this);
    autoMousePressed.add(this);
    autoMouseReleased.add(this);
    
    
    textSize = 29;


    marca = loadImage( "Marca.png" );

    gestorConexiones = c;

    miIP = NetInfo.lan();

    ejecutar = new Boton( width/4 - ((width/4)/2)/2, height-(width/4)/2, "EJECUTAR", textSize, #bebe43 );
    ejecutar.setColor(#bebe43);
    configuracion = new Boton( width - width/4 - ((width/4)/2)/2, height-(width/4)/2, "CONFIGURACION", textSize, #bebe43 );
    configuracion.setColor(#bebe43);
    scroll = new Scroll( width - width/5, int(height/2.3), 15, 20, int(height/2.3), int(height - height/4.2), false );
    scroll.setColor(100);
    guardar = new Boton( width/2 - ((width/4)/2)/2, height-(width/4)/2, "GUARDAR", textSize, #bebe43 );
    guardar.setColor(#bebe43);

    aceptar = new Boton( int(width/2.3), int(height/1.8), "ACEPTAR", textSize, #bebe43 );
    cargarIPs();
  }


  private boolean esNumero(String str)
  {
    for (char c : str.toCharArray())
    {
      if (!Character.isDigit(c)) return false;
    }
    return true;
  }  

  void actualizarXML() {
    for (TextBox cajaIP : cajasIP) {
      if (cajaIP.getTextoIntroducido().indexOf(":") > 0) {
        String[] datosConexion = split(cajaIP.getTextoIntroducido(), ":");
        gestorConexiones.setIP(int(cajaIP.getId()), datosConexion[0]);
        if (esNumero(datosConexion[1])) {
          gestorConexiones.setPuerto(int(cajaIP.getId()), datosConexion[1]);
        }
      } else {
        gestorConexiones.setIP(int(cajaIP.getId()), cajaIP.getTextoIntroducido());
      }
    }
  }

  private void cargarIPs() {
    String[][] IPs = gestorConexiones.getConexiones();
    for (int i = 0; IPs.length > i; i++) {
      crearCampoIp(str(i), IPs[i][0], IPs[i][1] + ":" + IPs[i][2]);
      crearCheck( IPs[i][0] );
    }
  }

  void draw() {
    pushStyle();
    noStroke();
    textFont(openSans_Semibold);
    textSize(textSize);

    if ( estado == 0 ) {
      dibujarCheckBox();
    } else if ( estado == 1 ) {
      dibujarTextBox();
    } else if ( estado == 2 ) {
      fill( 50 );
      rect( width/4, height/4, width/2, height/2 );
      fill(255);
      textAlign(CENTER);
      text( "Conexiones actualizadas.", width/2, height/2.5 );
      aceptar.dibujar();
    }

    popStyle();
  }

  void dibujarCheckBox() {

    for (CheckBox check : check) {
      check.dibujar();
    }

    fill(#25292b);
    //rect( 0, 0, width, height/2.3 );
    //rect( 0, height - height/4.2, width, height/3 );

    imageMode(CENTER);
    image( marca, width/2, marca.width );

    fill(255);
    textAlign(CENTER);
    textLeading(textSize);
    text( "¿QUÉ VAS A USAR EN ESTA COMPUTADORA?", width/4, height/3, width/2, height/5 );

    ejecutar.dibujar();
    configuracion.dibujar();
  }

  void dibujarTextBox() {
    pushStyle();
    pushMatrix();

    if ( cajasIP.size() > 3 ) {
      translate( 0, scroll.getPosMapeada() );
    }

    for (TextBox cajaIP : cajasIP) {
      cajaIP.dibujar();
    }

    popMatrix();    

    fill(#25292b);
    //rect( 0, 0, width, height/2.3 );
    //rect( 0, height - height/4.2, width, height/3 );

    imageMode(CENTER);
    image( marca, width/2, marca.width );

    fill(255);
    textAlign(CENTER);
    textLeading(textSize);
    text( "CONECTATE CON OTRAS COMPUTADORAS", width/4, height/4, width/2, height/5 );

    fill(#bebe43);
    rectMode(CENTER);
    rect( width/2, height/4 + height/9 + textSize/1.5, width/3, textSize + 10 );

    rectMode(CORNER);
    fill( 255 );
    text( "MI IP ES " + miIP, width/4, height/4 + height/9, width/2, height/5 );

    guardar.dibujar();
    scroll.dibujar();
    popStyle();
  }


  void crearCampoIp( String id, String referencia, String campoMuestra ) {
    TextBox nuevoCampo = new TextBox(  id, width/2-60, height/2 + (52 + 58/2)*cajasIP.size(), width/4 + 50, 52, campoMuestra, textSize );
    nuevoCampo.setColorBox( #444444 );
    nuevoCampo.setColorText( #969696 );
    nuevoCampo.setCajaExterna( true );
    nuevoCampo.setCajaExterior( referencia, #b44343, #d3d3d3, 5, 5, 5, 200 );

    cajasIP.add( nuevoCampo );
    if ( cajasIP.size() > 3 ) {
      scroll.setVisible(true);
      scroll.setMapMax(-( (52 + 58/2)*cajasIP.size() - 52));
      scroll.setMapMin(0);
    }
  }

  void crearCheck( String nombre ) {
    CheckBox nuevoCheck = new CheckBox( width/2-60, height/2-50 + (40 * check.size()), 25, 25, nombre, color(255), #444444  );

    check.add( nuevoCheck );
  }

  void keyPressed() {
    if ( estado == 1 ) {
      for (TextBox cajaIP : cajasIP) {
        cajaIP.keyPressed();
      }
    }
  }

  void abrirAplicaciones() {
    for (CheckBox check : check) {
      switch(check.getNombre()) {
      case "Particulas":
        if (check.getEstado()) {
          launch(sketchPath() + "\\prototipo_master_05_09_2016.exe");
        }
        break;
      case "Interfaz":
         if (check.getEstado()) {
        launch(sketchPath() + "\\API_22_08_16.exe");
         }
        break;
      }
    }
  }

  void mousePressed() {
    if ( estado == 0 ) {

      if ( ejecutar.mousePressed() ) {
        abrirAplicaciones();
        //-----------------------------------------------------------------------------------
        //-------------PONER LO QUE PASA CUANDO PONES EJECUTAR-------------------------------
      }

      if (configuracion.mousePressed()) {
        estado = 1;

        for (CheckBox check : check) {
          String nombre = check.getNombre();
          Boolean estado = check.getEstado();
          if ( estado ) {
            for (TextBox cajaIP : cajasIP) {
              if ( nombre.equals(cajaIP.getReferencia()) ) {
                cajaIP.setTextoIntroducido("127.0.0.1" + ":" + gestorConexiones.getPuerto(cajaIP.getReferencia()) );
                //cajaIP.setModificable( false );
              }
            }
          }
        }
      }

      for (CheckBox check : check) {
        check.mousePressed();
      }
    } else if ( estado == 1 ) {
      if (guardar.mousePressed()) {
        actualizarXML();
        estado = 2;
      }

      for (TextBox cajaIP : cajasIP) {
        cajaIP.mousePressed( scroll.getPosMapeada() );
      }
      scroll.mousePressed();
    } else if ( estado == 2 ) {
      if ( aceptar.mousePressed() ) {
        estado = 0;
      }
    }
  }

  void mouseReleased() {
    if ( estado == 0 ) {
      ejecutar.mouseReleased();
      configuracion.mouseReleased();
    } else if ( estado == 1 ) {
      guardar.mouseReleased();
      scroll.mouseReleased();
    } else if ( estado == 2 ) {
      aceptar.mouseReleased();
    }
  }
}





//------------------------------------------------------------------------------------------------------------------------------------------------

class Scroll {
  int posX;
  int posY;
  int posMax;
  int posMin;
  int mapMax;
  int mapMin;
  int tamH;
  int tamV;

  boolean visible;

  color c;

  boolean activo = false;

  Scroll( int posX, int posY, int tamH, int tamV, int posMin, int posMax, boolean visible ) {
    this.posX = posX;
    this.posY = posY;
    this.tamH = tamH;
    this.tamV = tamV;
    this.posMax = posMax;
    this.posMin = posMin;
    this.visible = visible;
  }

  void dibujar() {
    if ( activo ) {
      if ( mouseY > posMin && mouseY < posMax ) {
        posY = mouseY - tamV/2;
      }
    }

    if ( visible ) {
      pushStyle();
      fill(c-50);
      rect(width - width/5, int(height/2.3), tamH, dist(posX, posMin, posX, posMax) );
      fill( c );
      rect( posX, posY, tamH, tamV );
      popStyle();
    }
  }

  void setMapMax( int m ) {
    mapMax = m;
  }

  void setMapMin( int m ) {
    mapMin = m;
  }

  float getPosMapeada() {
    return map( posY, posMin, posMax, mapMin, mapMax );
  }

  void setColor( color c ) {
    this.c = c;
  }

  void setVisible( boolean v ) {
    visible = v;
  }

  void mousePressed() {
    if ( mouseX > posX && mouseX < posX + tamH && mouseY > posY && mouseY < posY + tamV ) {
      activo = true;
    }
  }

  void mouseReleased() {
    activo = false;
  }
}