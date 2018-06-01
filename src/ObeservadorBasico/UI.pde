class UI {
  ControlP5 cp5;
  int cant = 0;
  PVector pos;
  float alto;
  UI(PApplet p5, float x, float y, float a) {
    pos = new PVector(x, y);
    alto = a;
    cp5 = new ControlP5(p5);
  }
  void crearSlider(String n, float min, float max, float v) {
    cp5.addSlider(n)
      .setPosition(pos.x, pos.y+alto*cant+alto*cant+alto)
      .setSize(100, int(alto))
      .setRange(min, max)
      .setValue(v)
      ;

    cant++;
  }
}