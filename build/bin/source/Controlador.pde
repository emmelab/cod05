class Controlador {

  Consola consola;

  Controlador(Consola consola_) {

    consola = consola_;
  }


  void aceptar() {
    //consola.activar();
    consola.botonesAccionesN2();
    /* if (consola.modos.getModo().equals(ESPERA)) {
     consola.mandarMensaje("/holi...ten un buen dia");
     } else if (consola.modos.getModo().equals(AGREGAR)) {
     consola.mandarMensaje("/pedir/modificadores/existentes");
     } else if (consola.modos.getModo().equals(ELIMINAR)) {
     consola.mandarMensaje("/pedir/modificadores/existentes");
     } else if (consola.modos.getModo().equals(ESTIMULOS)) {
     consola.mandarMensaje("/pedir/estimulos/totales");
     } else if (consola.modos.getModo().equals(OPCIONES)) {
     consola.mandarMensaje("/pedir/opciones");
     }*/
  }

  void izquierda() {
    consola.decrementoSelector();
  }

  void derecha() {
    consola.aumentoSelector();
  }

  void anadir() {
    if (!(consola.modos.getModo().equals(MAQUINARIAS))) {
      consola.activarAnadir();
    }
  }
  void quitar() {
    consola.activarQuitar();
  }
  void opciones() {
    consola.activarOpciones();
  }
  /*void estimulos() {
    consola.activarEstimulos();
  }*/

  void cancelar() {    

    //consola.cancelar();
  }

  void actualizarIconos(int cerrado, int nivel, int eje) {   
    consola.actualizarIconos(cerrado, nivel, eje);
  }
}
