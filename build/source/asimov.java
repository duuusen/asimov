import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class asimov extends PApplet {

Ship ship;
ArrayList<Star> stars = new ArrayList<Star>();
ArrayList<Asteroid> asteroids = new ArrayList<Asteroid>();


boolean keyLeft = false;
boolean keyRight = false;
boolean keyUp = false;
boolean keyDown = false;

int gameStatus = 0; // The integer stores status of the screen

// game constants
final int startScreen = 0;
final int playingGame = 1;
final int gameOver = 2;

public void setup() {
  
  
  gameSetup();
}
public void draw() {
  switch(gameStatus) {
case startScreen:
  drawStartScreen();
  break;
case playingGame: // If we are in the game, draw the game, etc
  drawGame();
  break;
case gameOver:
  drawGameOverScreen();
  break;
}
}
public void gameSetup() {
  ship = new Ship();
  // Initialize stars and asteroids
  for (int i = 0; i < width; i++) {
    stars.add(new Star());
  }
  for (int i = 0; i < 6; i++) {
    PVector asteroidLocation = new PVector(random(width+50,width+500),random(height)); // Initialize asteroids outside the screen and let them fly in
    asteroids.add(new Asteroid(asteroidLocation,random(5,25)));
  }
}
public void drawStartScreen() {
  background(0);
  textAlign(CENTER);
  textSize(40);
  fill(255);
  text("ASIMOV\nPRESS ENTER TO START", width/2, height/2);
}

public void drawGameOverScreen() {
  background(0);
  textAlign(CENTER);
  textSize(40);
  fill(255);
  text("GAME OVER\nPRESS ENTER TO RESTART", width/2, height/2);
}
public void drawGame() {
  background(0);
  // Ship
  ship.run();
  int shipAcceleration = 2;
  if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
    PVector up = new PVector(0,-shipAcceleration);
    ship.applyForce(up);
  }
  if (keyDown) {
    PVector down = new PVector(0,shipAcceleration);
    ship.applyForce(down);
  }
  if (keyRight) {
    PVector forward = new PVector(shipAcceleration,0);
    ship.applyForce(forward);
  }
  // Asteroids
  for (Asteroid a: asteroids) {
    PVector attractionForce = ship.attract(a);
    PVector force = new PVector(-1.5f,0);
    a.run();
    a.applyForce(force);
    a.applyForce(attractionForce);
  }
  // Star Parallax
  for (Star s: stars) {
    s.run();
    PVector standardAcc = new PVector(5,0);
    s.parallax(standardAcc);
    if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
      PVector up = new PVector(0,-10);
      s.parallax(up);
    }
    if (keyDown) {
      PVector down = new PVector(0,10);
      s.parallax(down);
    }
    if (keyRight) {
      PVector forward = new PVector(15,0);
      s.parallax(forward);
    }
  }
}
public void keyPressed() {

  if (keyCode == UP  ||key == 'W'||key == 'w') {
    keyUp = true;
  }
  if (keyCode == DOWN || key == 'S'|| key == 's') {
    keyDown = true;
  }
  if (keyCode == RIGHT || key == 'D'|| key== 'd') {
    keyRight = true;
  }
  if (key == ENTER) {
    if (gameStatus != playingGame) {
      gameSetup();
      gameStatus = playingGame;
    }
  }
}
public void keyReleased() { // without this, the ship only moves one time the key is pressed
  if (keyCode == UP || key == 'W'|| key == 'w') {
    keyUp = false;
  }
  if (keyCode == DOWN || key == 'S'|| key == 's') {
    keyDown = false;
  }
  if (keyCode == RIGHT || key == 'D'|| key == 'd') {
    keyRight = false;
  }
}
class Asteroid {
  PVector location;
  PVector velocity;
  PVector acceleration;
  float mass;
  float radius;

  Asteroid(PVector asteroidLocation, float m) {
    location = asteroidLocation;
    velocity = new PVector(0,0);
    acceleration = new PVector(0,0);
    mass = m; // Order has to be right! The passed value is always on the right side of the equation.
    radius = mass*2;
  }
  public void run() {
    update();
    checkEdges();
    display();
  }
  public void applyForce(PVector f) {
    PVector force = PVector.div(f, mass);
    acceleration.add(force);
  }
  public void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); // Resetting acceleration, very important!!!111!11!1!
    println(velocity);
  }
  public void checkEdges() {
    if (location.x+radius < 0) {
      location.x = random(width+50,width+500);
      location.y = random(height);
      velocity.mult(random(0.1f,1.2f)); // randomizes the new velocity of the "new" asteroids
    }
  }
  public void display() {
    fill(255);
    ellipse(location.x,location.y,2*radius,2*radius);
  }
}
class Ship {
  PVector location;
  PVector velocity;
  PVector acceleration;
  PVector pullBack;
  int screenLimit;
  float damper;
  float mass;
  float g;

  Ship() {
    location = new PVector(width/10, height/2);
    velocity = new PVector(0,0);
    acceleration = new PVector(0,0);
    pullBack = new PVector(-1,0);
    screenLimit = 30;
    damper = 0.95f;
    mass = 60;
    g = 0.4f; // Gravitational constant 'g'. Increase value here to make the attraction force stronger
  }
  public void run() {
    update();
    limit();
    display();
  }
  public void applyForce(PVector u) {
    PVector updateLocation = u.get();
    acceleration.add(updateLocation);
  }
  public void update() {
    velocity.add(acceleration);
    location.add(velocity);
    acceleration.mult(0); // Resets the acceleration, so that it does not build up over time
    velocity.mult(damper);
  }
  public void limit() {
    if (location.x > width/2) {
      location.x = width/2;
    }
    if (location.y < screenLimit) {
      location.y = screenLimit;
    }
    if (location.y > height-screenLimit) {
      location.y = height-screenLimit;
    }
    // Setting the position to the point of origin
    if (location.x < width/10) {
      location.x = width/10;
      velocity.mult(0); // Without this, the effect is basically the same, but the values are not. Here it is set to 0, whereas without this, processing is still calculating stuff setting the macbook on fire
    }
    // Pulling the ship back to point of origin
    if (location.x > width/10) {
      acceleration.add(pullBack);
    }
  }
  public PVector attract(Asteroid a) {
    PVector force = PVector.sub(location,a.location); // substracting the ship location and the location of the asteroids gives us the direction PVector
    float distance = force.mag(); // this gives us the magnitude (distance) of the two objects
    distance = constrain(distance,5.0f,25.0f); // constrain because when the objects are very close, we could actually divide by 0. Also constrain the maximum, because we want realistic looking attraction. Otherwise, the objects could be so far away, that the attraction force is too small and they don't attract each other anymore

    force.normalize(); // normalize the direction vector, so that we can apply strength to it by multiplicating the direction vector with strength
    float strength = (g * mass * a.mass) / (distance * distance); // formula for the strength
    force.mult(strength);

    return force;
  }
  public void display() {
    stroke(0);
    fill(175,70);
    rect(location.x,location.y,50,50);
  }
}
// Based on William Smith's "Parallax"
class Star {
  PVector origin;
  PVector location;
  PVector angle;
  float size;
  int c;

  Star() {
    // Ship Movement Vector controlling the parallax effect
    origin = new PVector(width / 10, height / 2); // Sets point of origin. Calculating distance and angle from that point will determine parallax effect
    size = random(0.1f,3);
    c = color(random(200,255),random(200,255),random(200,255));
    location = new PVector(random(width * map(size, 1, 7, 7, 1)), random(height * map(size, 1, 7, 7, 1)));

  }
  public void run() {
    display();
  }
  public void parallax(PVector a) {
    PVector updateAngle = a.get();
    angle = new PVector(5,0);
    angle.add(updateAngle);
    // angle = new PVector(mouseX - width / 10, mouseY - height / 2); // Calculating the direction vector in relevance to origin point. Has to be here. Probably because here it goes in draw and in Star() it is "fixed"
    angle.normalize();
    angle.mult(20);
    // angle.mult(dist(width / 10, height / 2, mouseX, mouseY) / 50);
    origin.add(angle); // angle is basically a vector with x and y coordinates, the bigger the value, the greater the parallax effect
    angle.mult(0);
  }
  public void display() {
    pushStyle();
      stroke(c);
      strokeWeight(size);
      int x = (int) ((location.x - origin.x) * size / 8) % width; // the bigger stars move faster than the small ones, creating the parallax effect
      int y = (int) ((location.y - origin.y) * size / 8) % height;
      if(x < 0) x += width;
      if(y < 0) y += height;
      point(x,y);
    popStyle();
  }
}
  public void settings() {  size(1200,700);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "asimov" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
