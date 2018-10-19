void setPaleta() {
  paleta = new color[4][];

  color[] x1 = {
    color(#000000), color(#FFFFFF)
  };
  paleta[0] = x1;

  color[] x2 = {
    color(#25282D)/*baseDeLaBaseDeLaRueda*/, color(#2D3235)/*baseCirculardelarueda*/, color(#202529)/*baseDeMonitoryMaquinarias*/, color(#818181)
  };

  paleta[1] = x2;

  color[] x3 = {
    color(#1b1922), color(#25282D), color(#AFC22B), color(#BE4041), color(#1F2227)//conector
  };

  paleta[2] = x3;

  color[] x4 = {
    color(#B44343), color(#B47A43), color(#B4B243), color(#7DB443), color(#45B443), color(#43B478), 
    color(#43B4B0), color(#4380B4), color(#4348B4), color(#7543B4), color(#AD43B4), color(#B44382)
  };

  paleta[3] = x4;
}
