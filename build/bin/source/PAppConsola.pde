import java.io.InputStreamReader;

class PAppConsola extends PApplet {

  String que = "nada";

  public void settings() {
    size(400, 600);
  }

  void draw() {
    background(0);
    textSize(24);
    translate(24, 48);
    text( addLinebreakForWidth(que, width-48, 0, null), 0, 0);
  }

  void pasarStream(Process process) {
    try {
      process.waitFor();
      println(process.exitValue());
      que = getStringFromInputStream(process.getErrorStream());
    }
    catch(Exception e) {
      que = e.getMessage();
    }
  }

  String getStringFromInputStream(InputStream is) {

    BufferedReader br = null;
    StringBuilder sb = new StringBuilder();

    String line;
    try {

      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 
    finally {
      if (br != null) {
        try {
          br.close();
        } 
        catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    return sb.toString();
  }

  String addLinebreakForWidth(String originalText, float maxWidth, float textSize, PFont font) {
    pushStyle();  
    if (font != null) textFont(font);
    if (textSize > 0)textSize(textSize);
    String construct = "";
    String[] lines = originalText.split("\n");
    for (int i=0; i<lines.length; i++) {
      String[] words = lines[i].split(" ");
      if (i!=0)construct += "\n";
      construct += words[0];
      float currentWidth = textWidth(words[0]);
      for (int j=1; j<words.length; j++) {
        float addedWidth = textWidth(" "+words[j]);
        if (currentWidth+addedWidth > maxWidth) {
          construct += "\n"+words[j];
          currentWidth = textWidth(words[j]);
        } else {
          currentWidth += addedWidth;
          construct += " "+words[j];
        }
      }
    }
    popStyle();
    return construct;
  }
}
