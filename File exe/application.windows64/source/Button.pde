class Button {
  float xpos, ypos;
  PImage Bottone;
  Button (float x, float y, boolean s_r) {
    if (s_r==true)
    Bottone= loadImage("start.png");
    else
    Bottone= loadImage("retry.png");
    xpos=x-(Bottone.width/2);
    ypos=y-(Bottone.height/2);
    image(Bottone,xpos,ypos);
  }
}
