// The following short XML file called "mammals.xml" is parsed 
// in the code below. It must be in the project's "data" folder.
//
// <?xml version="1.0"?>
// <maquinarias>
//   <maquinaria id="0" nombre="Lumiere">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
//   <maquinaria id="1" nombre="Cohl">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
//   <maquinaria id="2" nombre="Melies">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
//   <maquinaria id="3" nombre="Guy Blaché">Mover|Dibujar Circulo|Espacio Cerrado</maquinaria>
// </maquinarias>

/*
"Colision Con Joint", "Aplicar Colisiones", "Colision Simple", "Dibujar Reas", "Varios", 
 "Vizualizar Particulas", "Dibujar Circulo", "Aplicar Fuerza", "Atraccion Al Centro", "Friccion Global", 
 "Escena", "Dibujar Flecha", "Espacio Cerrado", "Espacio Toroidal", "Dibujar Rastro Circular", 
 "Rastro Normal", "Rastro Elastico", "Forma De Rastro", "Dibujar Rastro Triangular", "Dibujar Rastro", 
 "Flock Separacion", "Flock Cohesion", "Flocking", "Transparencia", "Alfa Segun Velocidad", 
 "Aplicar Movimiento", "Dibujar Rastro Lineal", "Reset Lluvia", "Fuerzas Por Semejanza", "Flock Alineamiento", 
 "Reset Lluvia", "Fuerzas Por Semejanza", "Mover", "Gravedad"
 */

import java.util.Map;

class Maquinarias {

  int cantidad;
  // ArrayList maquinarias<Maquinarias> = new ArrayList<Maquinarias>();  
  HashMap<String, String[]> registroMaquinarias = new HashMap<String, String[]>();
  String[] listaMaquinarias;
  char separador = '|';
  Maquinarias(XML xml) {

    XML[] children = xml.getChildren("maq");
    listaMaquinarias = new String[children.length];
    for (int i = 0; i < children.length; i++) {
      int id = children[i].getInt("id");
      String nombre = children[i].getString("nombre");
      String modificadores = children[i].getContent();
      listaMaquinarias[i] = nombre;
      String[ ]mods = split(modificadores, separador);
      registroMaquinarias.put(nombre, mods);

      /* Maquinaria maq = new Maquinaria(id, nombre, modificadores, separador);
       maquinarias.add(maq);*/
    }
  }

  String[] getListaMaquinarias() {
    return listaMaquinarias;
  }

  String[] getMaquinaria(String nombre) {
    String[] listaMods = registroMaquinarias.get(nombre);
    return listaMods;
  }
}
/*class Maquinaria {
 int id;
 String nombre;
 String[] modificadores;
 Maquinaria(int id_, String nombre, String modificadores, char separador) {
 id = id_;
 nombre = nombre_;
 }
 
 getMods() {
 }
 }*/

// Sketch prints:
// 0, Capra hircus, Goat
// 1, Panthera pardus, Leopard
// 2, Equus zebra, Zebra
