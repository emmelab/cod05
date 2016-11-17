class UsuarioCerrado {
  SimpleOpenNI context;
  int idUsuario;
  //static final int contadorMaximo = 10;
  int estabilidad;
  int minimoEstabilidad;
  //boolean iniciarContador;
  boolean cerradoBruto, cerroBruto, abrioBruto;
  boolean cerrado, cerro, abrio;

  //PVector manoDerecha, codoDerecha, hombroDerecha;
  //PVector manoIzquierda, codoIzquierda, hombroIzquierda;
  //PVector torso;

  //float confianza_manoDerecha, confianza_codoDerecha, confianza_manoIzquierda, confianza_codoIzquierda;
  int[] jointsExtremidades, jointsCentro;
  HashMap<Integer, Float> confianza;
  HashMap<Integer, PVector> joints;
  PVector centroPromedio;

  float umbralCerrado;
  float factorUmbral = 1.9;

  //---------------------------------------------- CONSTRUCTOR

  UsuarioCerrado( SimpleOpenNI context, int idUsuario, int minimoEstabilidad, float factorUmbral, int[] jointsExtremidades, int[] jointsCentro) {
    this.context = context;
    this.idUsuario = idUsuario;

    this.jointsExtremidades = jointsExtremidades;
    this.jointsCentro = jointsCentro;
    generarJointsUsados();

    this.minimoEstabilidad = minimoEstabilidad;
    this.factorUmbral = factorUmbral;

    centroPromedio = new PVector();

    //manoDerecha = new PVector();
    //codoDerecha = new PVector();
    //hombroDerecha = new PVector();

    //manoIzquierda = new PVector();
    //codoIzquierda = new PVector();
    //hombroIzquierda = new PVector();

    //torso = new PVector();
  }

  void generarJointsUsados() {
    ArrayList<Integer> usados = new ArrayList();
    for (Integer i : jointsExtremidades) {
      usados.add(i);
    }
    for (Integer i : jointsCentro) {
      if (!usados.contains(i)) usados.add(i);
    }

    joints = new HashMap();
    confianza = new HashMap();
    for (Integer i : usados) {
      joints.put(i, new PVector());
      confianza.put(i, 0f);
    }
  }

  //---------------------------------------------- ACTUALIZAR
  void actualizar( ) {

    if (context.isTrackingSkeleton(idUsuario)) {
      //actualizo los Joints del usuario
      leerJoints( );

      //actualizo el centro promedio entre su torso y sus hombros
      calcularCentroPromedio();

      //calculo el umbral
      calcularUmbral();

      //actualizo el valor de cerrado y cerrado_valorBruto
      actualizarEstado();
    }
  }

  void leerJoints( ) {
    for (Integer i : joints.keySet ()) {
      confianza.put(i, context.getJointPositionSkeleton(idUsuario, i, joints.get(i) ) );
    }    
    //confianza_manoDerecha = context.getJointPositionSkeleton( id, SimpleOpenNI.SKEL_RIGHT_HAND, manoDerecha );
    //confianza_codoDerecha = context.getJointPositionSkeleton( id, SimpleOpenNI.SKEL_RIGHT_ELBOW, codoDerecha );
    //context.getJointPositionSkeleton( id, SimpleOpenNI.SKEL_RIGHT_SHOULDER, hombroDerecha );

    //confianza_manoIzquierda = context.getJointPositionSkeleton( id, SimpleOpenNI.SKEL_LEFT_HAND, manoIzquierda );
    //confianza_codoIzquierda = context.getJointPositionSkeleton( id, SimpleOpenNI.SKEL_LEFT_ELBOW, codoIzquierda );
    //context.getJointPositionSkeleton( id, SimpleOpenNI.SKEL_LEFT_SHOULDER, hombroIzquierda );

    //context.getJointPositionSkeleton( id, SimpleOpenNI.SKEL_TORSO, torso );
  }

  void actualizarEstado() {
    boolean cerradoAnterior = cerrado;
    boolean cerradoBrutoAnterior = cerradoBruto;

    cerradoBruto = true;
    for (Integer i : jointsExtremidades) {
      if (confianza.get(i) > .5) {
        float d = PVector.dist(centroPromedio, joints.get(i) );
        if (d > umbralCerrado) {
          cerradoBruto = false;
          break;
        }
      }
    }
    if (cerradoBruto == cerradoBrutoAnterior) {
      cerroBruto = abrioBruto = false;
      if (estabilidad < minimoEstabilidad) {
        estabilidad++;
      } else {
        cerrado = cerradoBruto;
      }
    } else {
      estabilidad = 0;
      cerroBruto = cerradoBruto;
      abrioBruto = !cerradoBruto;
    }

    if (cerrado == cerradoAnterior) {
      cerro = abrio = false;
    } else {
      cerro = cerrado;
      abrio = !cerrado;
    }


    //boolean nuevo_cerrado_valorBruto = calcular_Cerrado_ValorBruto();
    /*
    if( nuevo_cerrado_valorBruto != cerrado_valorBruto ){
     iniciarContador = !iniciarContador;
     if( !iniciarContador ) contador = 0;
     cerrado_valorBruto = nuevo_cerrado_valorBruto;
     }
     
     if( iniciarContador ){
     
     if( contador < contadorMaximo ){
     contador++;
     }else{
     iniciarContador = false;
     contador = 0;
     cerrado = cerrado_valorBruto;
     
     //disparo eventos!!
     if( cerrado )
     evento_Cerrado();
     else
     evento_Abierto();
     
     }
     
     }
     
     if( cerradoValorAnterior != cerrado ){
     cerro = cerrado;
     abrio = !cerro;
     }else{
     cerro = abrio = false;
     }*/
  }

  void calcularCentroPromedio() {    
    centroPromedio.set(0, 0, 0);
    for (Integer i : jointsCentro) {
      centroPromedio.add( joints.get(i) );
    }
    centroPromedio.div(jointsCentro.length);
    //float promedioX = (hombroDerecha.x + hombroIzquierda.x + torso.x) / 3;
    //float promedioY = (hombroDerecha.y + hombroIzquierda.y + torso.y) / 3;
    //float promedioZ = (hombroDerecha.z + hombroIzquierda.z + torso.z) / 3;

    //PVector c = new PVector( promedioX, promedioY, promedioZ );

    //return c;
  }

  void calcularUmbral() {
    umbralCerrado = 0;
    for (Integer i : jointsCentro) {
      umbralCerrado += PVector.dist( joints.get(i), centroPromedio);
    }
    umbralCerrado = factorUmbral * umbralCerrado / jointsCentro.length;
    //float distancia_1 = dist( centroPromedio.x, centroPromedio.y, centroPromedio.z, hombroDerecha.x, hombroDerecha.y, hombroDerecha.z );
    //float distancia_2 = dist( centroPromedio.x, centroPromedio.y, centroPromedio.z, hombroIzquierda.x, hombroIzquierda.y, hombroIzquierda.z );
    //float distancia_3 = dist( centroPromedio.x, centroPromedio.y, centroPromedio.z, torso.x, torso.y, torso.z );

    //umbralCerrado = factorUmbral * (distancia_1 + distancia_2 + distancia_3) / 3;
  }

  /*
  boolean calcular_Cerrado_ValorBruto(){//este metodo va a ser remplazado por el de abajo
   boolean c = false;
   
   boolean[] b_joints = new boolean[4];
   PVector[] joints = { manoIzquierda, codoIzquierda, manoDerecha, codoDerecha };
   float[] confianza = { confianza_manoIzquierda, confianza_codoIzquierda, confianza_manoDerecha, confianza_codoDerecha };
   
   for( int i = 0; i < b_joints.length; i++ ){
   if( confianza[i] > 0.5 ){
   if( dist( joints[i].x, joints[i].y, joints[i].z, centroPromedio.x, centroPromedio.y, centroPromedio.z ) <= umbral )
   b_joints[i] = true;
   else
   b_joints[i] = false;
   }else{
   b_joints[i] = true;
   }
   }
   
   if( b_joints[0] && b_joints[1] && b_joints[2] && b_joints[3] )
   c = true;
   
   return c;
   }
   */

  /*boolean calcular_Cerrado_ValorBruto(){
   
   PVector[] joints = { manoIzquierda, codoIzquierda, manoDerecha, codoDerecha };
   float[] confianza = { confianza_manoIzquierda, confianza_codoIzquierda, confianza_manoDerecha, confianza_codoDerecha };
   
   for( int i = 0; i < joints.length; i++ ){
   if( confianza[i] > 0.5 ){
   if( dist( joints[i].x, joints[i].y, joints[i].z, centroPromedio.x, centroPromedio.y, centroPromedio.z ) > umbral ){
   return false;
   }
   }
   }
   
   return true;
   }*/

  //---------------------------------------------- DIBUJAR
  /*void dibujar(){
   
   sphereDetail(5);
   dibujarEsfera( manoIzquierda );
   dibujarEsfera( codoIzquierda );
   dibujarEsfera( manoDerecha );
   dibujarEsfera( codoDerecha );
   
   sphereDetail(10);
   if( !cerrado ){
   dibujarEsfera( centroPromedio, umbral, color(0,0,255,128) );
   }else{
   dibujarEsfera( centroPromedio, umbral, color(0,255,0,128) );
   }
   }*/

  //mi evento
  /*void evento_Cerrado(){
   println("El usuario N° " + id +" se a CERRADO.");
   }*/

  /*void evento_Abierto(){
   println("El usuario N° " + id +" se a ABIERTO.");
   }*/
}

