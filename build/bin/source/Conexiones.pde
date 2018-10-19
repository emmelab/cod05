/*final class Conexiones {
 
 private String nombreArchivo;
 private XML configuracion;
 
 Conexiones() {
 nombreArchivo = "configuracion.xml";
 configuracion = cargarConfiguracion(nombreArchivo);
 }
 
 Conexiones(String rutaArchivo) {
 nombreArchivo = rutaArchivo;
 configuracion = cargarConfiguracion(nombreArchivo);
 }  
 
 public String[][] getConexiones() {
 XML[] conexiones = configuracion.getChildren("conexion");
 String[][] lista = new String[conexiones.length][3];
 for (int i = 0; i < conexiones.length; i++) {
 lista[i][0] = (conexiones[i].hasAttribute("nombre") ? conexiones[i].getString("nombre") : "No especificado");
 lista[i][1] = (conexiones[i].hasAttribute("ip") ? conexiones[i].getString("ip") : "No especificado");
 lista[i][2] = (conexiones[i].hasAttribute("puerto") ? conexiones[i].getString("puerto") : "No especificado");
 }
 return lista;
 }
 
 private XML cargarConfiguracion(String nombre) {
 XML archivo;
 try {
 archivo = loadXML(nombre);
 }
 catch(Exception e) {
 archivo = crearArchivoXML();
 guardarXML(archivo);
 }
 return archivo;
 }
 
 private void guardarXML(XML archivo) {
 saveXML(archivo, "configuracion.xml");
 }
 
 public void agregarConexion(String nombre, String ip, int puerto) {
 XML nueva = configuracion.addChild("conexion");
 nueva.setString("id", str(configuracion.getChildren("conexion").length-1));
 nueva.setString("nombre", nombre);
 nueva.setString("ip", ip);
 nueva.setInt("puerto", puerto);
 guardarXML(configuracion);
 }
 
 //Crea un archivo default de XML en caso de no encontrarse
 private XML crearArchivoXML() {
 String estructura = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><gestor><conexion id=\"0\" nombre=\"Kinect\" ip=\"127.0.0.1\" puerto=\"11000\"></conexion><conexion id=\"1\" nombre=\"Interfaz\" ip=\"127.0.0.1\" puerto=\"11000\"></conexion><conexion id=\"2\" nombre=\"Particulas\" ip=\"127.0.0.1\" puerto=\"11000\"></conexion></gestor>";
 return parseXML(estructura);
 }
 
 //Devuelve la IP buscando por nombre, en caso de no encontrar devuelve null
 public String getIP(String nombre) {
 return getAtributo("nombre", nombre, "ip");
 }
 
 //Devuelve la IP buscando por id, en caso de no encontrar devuelve null
 public String getIP(int id) {
 return getAtributo("id", str(id), "ip");
 }
 
 public int getPuerto(String nombre) {
 String puerto = getAtributo("nombre", nombre, "puerto");
 if (puerto == null) {
 return 0;
 } else {
 return int(puerto);
 }
 }
 
 public int getPuerto(int id) {
 String puerto = getAtributo("id", str(id), "puerto");
 if (puerto == null) {
 return 0;
 } else {
 return int(puerto);
 }
 }
 
 public boolean setPuerto(String nombre, String valor) {
 return setAtributo("nombre", nombre, "puerto", valor);
 }
 
 public boolean setPuerto(int id, String valor) {
 return setAtributo("id", str(id), "puerto", valor);
 }
 
 public boolean setIP(String nombre, String valor) {
 return setAtributo("nombre", nombre, "ip", valor);
 }
 
 public boolean setIP(int id, String valor) {
 return setAtributo("id", str(id), "ip", valor);
 }
 
 
 private String getAtributo(String atributo, String busqueda, String devuelve) {
 XML[] IPs = configuracion.getChildren("conexion");
 for (int i = 0; i < IPs.length; i++) {
 if (IPs[i].hasAttribute(atributo) && IPs[i].getString(atributo).equals(busqueda)) {
 return IPs[i].getString(devuelve);
 }
 }
 return null;
 }
 
 private boolean setAtributo(String atributo, String busqueda, String cambiaAtributo, String guarda) {
 XML[] IPs = configuracion.getChildren("conexion");
 for (int i = 0; i < IPs.length; i++) {
 if (IPs[i].hasAttribute(atributo) && IPs[i].getString(atributo).equals(busqueda)) {
 IPs[i].setString(cambiaAtributo, guarda);
 guardarXML(configuracion);
 return true;
 }
 }
 return false;
 }
 }*/
