import java.util.Map;
HashMap<String, PVector> estimulos = new HashMap<String, PVector>();
PVector estimuloActual;

void recibirUnEstimulo(String nombre, int x, int y) {
  PVector posActual = new PVector(x, y); 

  boolean existe = false;
  for (String n : estimulos.keySet()) {
    if (n.equals(nombre))
      existe = true;
  }

  if (!existe) {
    estimulos.put(nombre, posActual);
  } else {
    PVector posicion = estimulos.get(nombre);
    posicion.set(posActual);
  }
}

void enviarEstimulos() { 
  for (String n : estimulos.keySet()) {
    mensaje_NOMBRE_ESTADO("/estimulos/totales", n, 0);
  }
  mensaje("/estimulos/totales/listo");
} 

