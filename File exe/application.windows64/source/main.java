import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import ddf.minim.AudioPlayer; 
import ddf.minim.Minim; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class main extends PApplet {



JSONObject json;
int TEMPO=20;
int Record;

//-----SFONDO E IMG------
PImage img, fuji, casa, pozzo, ninja_menu, shuriken, RecordImg, ResRecord, CreditImg;

//----MUSICA E SUONI-----
Minim minim;
AudioPlayer MenuMusic, GameMusic, Shuriken, Shuriken2;
boolean shuri=true;

//--------MENU--------
boolean start=false, redo=false, Menu_crediti=false;
PImage Start_Button, Retry_Button, Start_Screen;

//-------FUMO----------
PImage fumo;
int ContatoreFumo=1, Punteggio=0, Punteggio_Prec=0;
boolean start_smoke=false;
float precNx, precNy;

//-------FONT-------
PFont font;

//---------NPC-----------
NPC ninja;

//---------BUTTTON----------
Button inizia;
Crediti crediti;
Crediti ResetRecord;

//-------TEMPO------------
int timeMax=TEMPO, t=0, Bonus=0;
boolean delay=true;
String time;

//-------RECORD--------
boolean superato=false;

public void setup() { 

  //---------RECORD-----------
  json = loadJSONObject ("ninja.json");
  Record=json.getInt("high_score");

  //------------FONT-------------
  font = loadFont ("LastNinja-48.vlw");
  textFont(font, 15);

  //------------SFONDO e IMG------------
  Start_Button=loadImage("start.png");
  Retry_Button=loadImage("retry.png");
  Start_Screen=loadImage("sfondo_menu.png");
  img=loadImage("Landscape.jpg");
  fuji=loadImage("Fuji.png");
  casa=loadImage("casa.png");
  pozzo=loadImage("pozzo.png");
  ninja_menu=loadImage("sprite.png");
  shuriken=loadImage("shuriken.png");
  RecordImg=loadImage("Record.png");
  CreditImg=loadImage("sfondo_credits.jpg");
  size(img.width, img.height);
  background(img);

  //-------MUSICA--------
  minim = new Minim(this);
  Shuriken = minim.loadFile("Shuriken.mp3", 1024);
  Shuriken2 = minim.loadFile("Shuriken.mp3", 1024);
  MenuMusic = minim.loadFile("Flute.mp3", 1024);
  GameMusic = minim.loadFile("Drums.mp3", 1024);
  MenuMusic.setGain(-10);
  GameMusic.setGain(-10);
  MenuMusic.loop();
}


public void draw() {


  //----Menu iniziale: il primo start indica che \u00e8 la prima partita ed \u00e8 settato a false, il redo indica che \u00e8 un altro tentativo ed \u00e8 settato a false
  if (start==false) {  

    //---------inizio musica+sfondo--------
    MenuMusic.play();
    background(Start_Screen);
    crediti=new Crediti(img.width*0.92f, img.height*0.95f, true);
    ResetRecord=new Crediti(img.width*0.08f, img.height*0.95f, false);

    if (superato==true)
      image(RecordImg, img.width*0.7f, img.height*0.02f);

    if (redo==false) 
      inizia= new Button(img.width/2, img.height/2, true);
    else {
      inizia= new Button(img.width/2, img.height/2, false);
      fill(250);
      text("Score: " +Punteggio_Prec, img.width*0.46f, img.height*0.7f);
    }
    //--------HIGH SCORE-------------

    text("Record: " +Record, img.width*0.455f, img.height*0.74f);

    //----cursore shuriken---
    cursor(shuriken, 20, 20);

    ButtonPressed();
  } else {
    //-----------TIME SET MAX-----------
    if (delay==true) {
      timeMax=timeMax+PApplet.parseInt(millis()/1000);
      delay=false;
    }


    //-----------------NINJA PRESO--------------------
    NinjaClickMove();  

    //-----------------ANIMAZIONE FUMO---------------
    if (ContatoreFumo <= 12 && start_smoke==true) {              //Inizio animazione del fumo, il true lo da solo se entra il NinjaClickMove()
      fumo= loadImage("fumo"+ContatoreFumo+".png");

      background(img);                                           //Refrash dello sfondo per l'animazione del fumo
      image(fumo, precNx, precNy);                              //le "prec" sono date da ninjaClickMove prima del ninja.escape()

      text("SCORE: "+Punteggio, img.width-115, img.height-10);  //aumento del punteggio

      image(ninja.icona, ninja.xpos, ninja.ypos);               //refersh del ninja altrimenti va sotto il background
      ContatoreFumo++;
    } else {
      ContatoreFumo=1;                                          //riazzero per il prossimo click
      start_smoke=false;
    }

    //-----------------GESTIONE TEMPO-----------------

    if (start==true) {
      t=timeMax-PApplet.parseInt(millis()/1000);
      time= nf(t, 2);
      fill(250);
      rect(10, 3, 120, 20);
      fill(0);
      text("TEMPO: "+time, 20, 20 );
    }
    //------RIAZZERAMENTO TIMER, RECORD E MENU RETRY----
    if (t==0) {
      if (Punteggio>Record) { 
        superato=true;
        Record=Punteggio;
        json.setInt("high_score", Record);
        saveJSONObject(json, "data/ninja.json");
      }
      Punteggio_Prec=Punteggio;
      redo=true;
      start=false;
      delay=true;
      Punteggio=0;
      timeMax=TEMPO;
      GameMusic.pause();
    }

    //----------------REFRESH OSTACOLI---------------
    image(fuji, 0, 0);
    image(casa, 0, 0);
    image(pozzo, 0, 0);
  }
}


public void ButtonPressed() {


  if (mouseX>crediti.xpos && mouseX<crediti.xpos+crediti.Crediti.width ) {
    if (mouseY>crediti.ypos && mouseY<crediti.ypos+crediti.Crediti.height ) {
      background(CreditImg); 
      text("Menu music: Mark of the Ninja - Ninja Intro", img.width*0.1f, img.height*0.1f);
      text("Game music: Mark of the Ninja - Ninja MI Drums", img.width*0.1f, img.height*0.2f);
      text("Created by: Marco Farina", img.width*0.1f, img.height*0.3f);
      text("Click for send me an e-mail to 'farina.marco94   gmail.com'", img.width*0.1f, img.height*0.4f);
      text("Tweet your high score to '   NinjAttackGame'", img.width*0.1f, img.height*0.5f);


      if (mouseButton==LEFT) {
        mouseButton=RIGHT;
        for (int i=0; i<200000000; i++) {

          if (i==0)  link("http://mailto:farina.marco94@gmail.com");
        }
      }
    }
  }

  if (mouseButton==LEFT) {
    if (mouseX>ResetRecord.xpos && mouseX<ResetRecord.xpos+ResetRecord.Crediti.width ) {
      if (mouseY>ResetRecord.ypos && mouseY<ResetRecord.ypos+ResetRecord.Crediti.height ) {     
        Record=0;
        json.setInt("high_score", Record);
        saveJSONObject(json, "data/ninja.json");
      }
    }

    if (mouseX>inizia.xpos && mouseX<inizia.xpos+inizia.Bottone.width ) {
      if (mouseY>inizia.ypos && mouseY<inizia.ypos+inizia.Bottone.height ) {     
        start=true;
        MenuMusic.loop();
        MenuMusic.pause();
        GameMusic.loop();

        //------------NINJA-------------
        background(img);
        ninja= new NPC(img.width/3, img.height/3);
      }
    }
  }
}

public void NinjaClickMove() {
  if (mouseButton==LEFT) {
    if (mouseX>ninja.xpos && mouseX<ninja.xpos+ninja.icona.width ) {
      if (mouseY>ninja.ypos && mouseY<ninja.ypos+ninja.icona.height ) {   
        if (shuri==true) {
          Shuriken.play();
          Shuriken.rewind();
          shuri=false;
        } else {
          Shuriken2.play();
          Shuriken2.rewind();
          shuri=true;
        }
        superato=false;
        start_smoke=true;
        Punteggio++;

        Bonus++;
        if (Bonus==5) {
          timeMax++;
          Bonus=0;
        }
        precNx=ninja.xpos;
        precNy=ninja.ypos;

        background(img);
        ninja.escape();
      }
    }
  }
}
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
class NPC {
  float xpos, ypos;
  PImage icona;
  NPC (float x, float y) {
    icona= loadImage("sprite.png");
    xpos=x-(icona.width/2);
    ypos=y-(icona.width/2);
    image(icona,xpos,ypos);
  } 
  
  public void escape() {
  
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
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "main" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
