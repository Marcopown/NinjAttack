import ddf.minim.AudioPlayer; //<>//
import ddf.minim.Minim;
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

void setup() { 

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


void draw() {


  //----Menu iniziale: il primo start indica che è la prima partita ed è settato a false, il redo indica che è un altro tentativo ed è settato a false
  if (start==false) {  

    //---------inizio musica+sfondo--------
    MenuMusic.play();
    background(Start_Screen);
    crediti=new Crediti(img.width*0.92, img.height*0.95, true);
    ResetRecord=new Crediti(img.width*0.08, img.height*0.95, false);

    if (superato==true)
      image(RecordImg, img.width*0.7, img.height*0.02);

    if (redo==false) 
      inizia= new Button(img.width/2, img.height/2, true);
    else {
      inizia= new Button(img.width/2, img.height/2, false);
      fill(250);
      text("Score: " +Punteggio_Prec, img.width*0.46, img.height*0.7);
    }
    //--------HIGH SCORE-------------

    text("Record: " +Record, img.width*0.455, img.height*0.74);

    //----cursore shuriken---
    cursor(shuriken, 20, 20);

    ButtonPressed();
  } else {
    //-----------TIME SET MAX-----------
    if (delay==true) {
      timeMax=timeMax+int(millis()/1000);
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
      t=timeMax-int(millis()/1000);
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


void ButtonPressed() {


  if (mouseX>crediti.xpos && mouseX<crediti.xpos+crediti.Crediti.width ) {
    if (mouseY>crediti.ypos && mouseY<crediti.ypos+crediti.Crediti.height ) {
      background(CreditImg); 
      text("Menu music: Mark of the Ninja - Ninja Intro", img.width*0.1, img.height*0.1);
      text("Game music: Mark of the Ninja - Ninja MI Drums", img.width*0.1, img.height*0.2);
      text("Created by: Marco Farina", img.width*0.1, img.height*0.3);
      text("Click for send me an e-mail to 'farina.marco94   gmail.com'", img.width*0.1, img.height*0.4);
      text("Tweet your high score to '   NinjAttackGame'", img.width*0.1, img.height*0.5);


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

void NinjaClickMove() {
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
