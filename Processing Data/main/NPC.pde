class NPC {
  float xpos, ypos;
  PImage icona;
  NPC (float x, float y) {
    icona= loadImage("sprite.png");
    xpos=x-(icona.width/2);
    ypos=y-(icona.width/2);
    image(icona,xpos,ypos);
  } 
  
  void escape() {
  
    xpos= random(960);
    ypos= random(420);
    if (xpos>34 && xpos<120 &&ypos>70 &&ypos<125)
    ninja.escape(); //montagna
    if (xpos>0 && xpos<195 &&ypos>100 &&ypos<150)
    ninja.escape(); //montagna
    if (xpos>930 && xpos<1070 &&ypos>300 &&ypos<320)
    ninja.escape(); //casa
    if (xpos>890 && xpos<1100 &&ypos>320 &&ypos<440)
    ninja.escape(); //casa
    if (xpos>45 && xpos<250 &&ypos>480 &&ypos<600)
    ninja.escape(); //pozzo
    if (xpos>95 && xpos<220 &&ypos>330 &&ypos<360)
    ninja.escape(); //pozzo
     image(icona,xpos,ypos);
  }
}
