class Crediti {
  float xpos, ypos;
  PImage Crediti;
  Crediti (float x, float y, boolean credits) {
    if (credits==true)
    Crediti= loadImage("credits.png");
    else Crediti= loadImage("ResRecord.png");
    xpos=x-(Crediti.width/2);
    ypos=y-(Crediti.height/2);
    image(Crediti,xpos,ypos);
  }
}
