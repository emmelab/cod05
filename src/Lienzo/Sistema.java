import processing.core.PApplet;
import java.util.HashMap;
import java.util.ArrayList;

class Sistema {
  static char separadorMaquinarias = '|';
  static HashMap<String, Registrador<Modificador>> registroModificadores;
  static void registrar(Registrador regist) {
    if (registroModificadores == null) registroModificadores = new HashMap();
    registroModificadores.put(regist.key(), regist);
  }

  boolean debug = false;
  final ArrayList<Modificador> modificadores = new ArrayList();
  final HashMap<String, Modificador> directorioModificadores = new HashMap();

  final PApplet p5;
  final int tamano;
  final HashMap<String, Atributo> atributos = new HashMap();
  
  private ManagerJoints managerJoints;
  
  Sistema(PApplet p5, int tamano) {
    this.p5 = p5;
    this.tamano = tamano;
  }
  
  Sistema(PApplet p5, int tamano, ManagerJoints managerJoints) {
    this.p5 = p5;
    this.tamano = tamano;
    this.managerJoints = managerJoints;
  }
  
  ManagerJoints getManagerJoints(){
    return managerJoints;
  }

  void actualizar() {
    for (int i=modificadores.size ()-1; i>=0; i--) {
      //for (Modificador m : modificadores) {
      modificadores.get(i).modificar(this);
    }
  }
  void reset() {
    atributos.clear();
  }

  Atributo recobrar(String llave) {
    return atributos.get(llave);
  }
  <T extends Atributo> T requerir(Atributo.Manager<T> manager, boolean obligatorio) {
    if (atributos.containsKey(manager.key())) return (T)atributos.get(manager.key());
    else if (obligatorio) {
      atributos.put(manager.key(), manager.generarInstancia(this));
      return (T)atributos.get(manager.key());
    } else return null;
  }


  int getCantidadModificadores() {
    return modificadores.size();
  }
  String[] getDirectorioModificadores() {
    return directorioModificadores.keySet().toArray(new String[0]);
  }
  Modificador getModificador(int posicion) {
    return modificadores.get(posicion);
  }
  Modificador getModificador(String nombre) {
    return directorioModificadores.get(nombre);
  }
  boolean hasModificador(String nombre) {
    return directorioModificadores.containsKey(nombre);
  }

  boolean getEstado(String nombre) {
    Modificador m = directorioModificadores.get(nombre);
    if (m!=null) return m.estado;
    else return false;
  }
  boolean getEstado(int posicion) {
    return modificadores.get(posicion).estado;
  }
  void setEstado(String nombre, boolean estado) {
    Modificador m = directorioModificadores.get(nombre);
    if (m!=null)m.estado = estado;
  }
  void setEstado(int posicion, boolean estado) {
    modificadores.get(posicion).estado = estado;
  }
  void prender(String nombre) {
    Modificador m = directorioModificadores.get(nombre);
    if (m!=null)m.estado = true;
  }
  void prender(int posicion) {
    modificadores.get(posicion).estado = true;
  }
  void apagar(int posicion) {
    modificadores.get(posicion).estado = false;
  }
  void apagar(String nombre) {
    Modificador m = directorioModificadores.get(nombre);
    if (m!=null)m.estado = false;
  }

  Modificador agregar(String registrador) {
    Registrador<Modificador> r = registroModificadores.get(registrador);
    if (r == null) return null;
    else return agregar(r);
  }
  <T extends Modificador> T agregar(Registrador<T> registrador) {
    String nombre = registrador.key()+"_0";
    for (int watchDog = 1; watchDog < 1000; watchDog++) {
      if (directorioModificadores.containsKey(nombre)) return null;//nombre = registrador.key()+"_"+watchDog;
      else break;
    }
    return agregar(nombre, registrador.generarInstancia());
  }
  <T extends Modificador> T agregar(String nombre, T modificador) {
    directorioModificadores.put(nombre, modificador);
    modificadores.add(modificador);
    return modificador;
  }

  Modificador agregar(String registrador, int posicion) {
    Registrador<Modificador> r = registroModificadores.get(registrador);
    if (r == null) return null;
    else return agregar(r, posicion);
  }
  Modificador agregar(Registrador<Modificador> registrador, int posicion) {
    String nombre = registrador.key()+"_0";
    for (int watchDog = 1; watchDog < 1000; watchDog++) {
      if (directorioModificadores.containsKey(nombre)) return null;//nombre = registrador.key()+"_"+watchDog;
      else break;
    }
    return agregar(nombre, registrador.generarInstancia(), posicion);
  }
  Modificador agregar(String nombre, Modificador modificador, int posicion) {
    directorioModificadores.put(nombre, modificador);
    modificadores.add(posicion, modificador);
    return modificador;
  }

  void eliminar(int posicion) {
    Modificador m = modificadores.get(posicion);
    String nombre = "";
    for (java.util.Map.Entry<String, Modificador> entry : directorioModificadores.entrySet ()) {
      if (m == entry.getValue()) {
        nombre = entry.getKey();
      }
    }
    directorioModificadores.remove(nombre);
    modificadores.remove(posicion);
  }

  Modificador eliminar(String nombre) {
    Modificador m = directorioModificadores.get(nombre);
    if (m != null) {
      m.finalizar( this );
      modificadores.remove(m);
      directorioModificadores.remove(nombre);
    }    
    return m;
  }

  String modificadoresActivos_lista() {     
    String nombres = "vacio";   
    p5.println("modificadores: " +modificadores.size());
    p5.println("directorio: " +directorioModificadores.size());
    for (java.util.Map.Entry<String, Modificador> entry : directorioModificadores.entrySet ()) {
      String codigo = entry.getKey(); //tiene la forma ......... nombre_0      
      nombres = (nombres.equals("vacio"))?codigo:nombres+separadorMaquinarias+codigo;
    }     
    if (nombres.equals("vacio")) {
      nombres = null;
    }
    return nombres;
  }

  void vaciarModificadores() {
    String[] lista = new String[modificadores.size()];
    int contador = 0;  
    for (java.util.Map.Entry<String, Modificador> entry : directorioModificadores.entrySet ()) {
      String codigo = entry.getKey(); //tiene la forma ......... nombre_0     
      lista[contador] = codigo;
      contador++;
    }   
    for (int i=0; i<lista.length; i++) {
      Modificador m = directorioModificadores.get(lista[i]);
      if (m != null) {
        modificadores.remove(m);
        directorioModificadores.remove(lista[i]);
      }
    }
  }

  void rellenar(String cadenaDeModificadores) {
  }
}

abstract class Registrador<T extends Modificador> {
  Registrador() {
    Sistema.registrar(this);
  }
  abstract String key();
  abstract String categoria();
  abstract T generarInstancia();
}

abstract class Modificador {
  boolean estado = true;
  abstract void ejecutar(Sistema s);
  public void modificar(Sistema s) {
    if (estado)ejecutar(s);
  }
  public void finalizar(Sistema s){}
}

abstract class Atributo {
  final static boolean OBLIGATORIO = true, OPCIONAL = false;
  Sistema sistema;
  Atributo(Sistema s) {
    sistema = s;
  }

  interface Manager<T extends Atributo> {
    String key();
    T generarInstancia(Sistema s);
  }
}
