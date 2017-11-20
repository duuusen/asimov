Ship ship;
ArrayList<Star> stars = new ArrayList<Star>();
ArrayList<Asteroid> asteroids;


boolean keyLeft = false;
boolean keyRight = false;
boolean keyUp = false;
boolean keyDown = false;

int gameStatus = 0; // The integer stores status of the screen

// game constants
final int startScreen = 0;
final int playingGame = 1;
final int gameOver = 2;

void setup() {
  size(1200,700);
  smooth();
  // Setting up the stars once, they don't need to be reloaded like the gameSetup
  for (int i = 0; i < width; i++) {
    stars.add(new Star());
  }
  gameSetup();
}
void draw() {
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
void gameSetup() {
  ship = new Ship(new PVector(width/10, height/2));
  // Initialize asteroids
  asteroids = new ArrayList<Asteroid>(); // This was the missing line of code. Before, the array was created above setup(), now a new array is created everytime the game reloads
  for (int i = 0; i < 6; i++) {
    PVector asteroidLocation = new PVector(random(width+50,width+500),random(height)); // Initialize asteroids outside the screen and let them fly in
    asteroids.add(new Asteroid(asteroidLocation,random(5,25)));
  }
}
void drawStartScreen() {
  background(0);
  textAlign(CENTER);
  textSize(40);
  fill(255);
  textSize(72);
  text("ASIMOV\n", width/2, height/2);
  textSize(46);
  text("PRESS ENTER TO START",width/2,height/1.5);
}

void drawGameOverScreen() {
  background(0);
  textAlign(CENTER);
  textSize(40);
  fill(255);
  textLeading(160);
  text("GAME OVER\nPRESS ENTER TO RESTART", width/2, height/2);
}
void drawGame() {
  background(0);
  // Ship
  ship.run();
  float shipAcceleration = 0.5;
  if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
    PVector up = new PVector(0,-shipAcceleration);
    ship.applyForce(up);
  }
  if (keyDown) {
    PVector down = new PVector(0,shipAcceleration);
    ship.applyForce(down);
  }
  if (keyRight) {
    PVector forward = new PVector(shipAcceleration+1,0); // +1 because the forward force needs to be stronger than the pullBack force
    ship.applyForce(forward);
  }
  // Asteroids
  for (Asteroid a: asteroids) {
    PVector attractionForce = ship.attract(a);
    PVector acceleration = new PVector(-0.3,0);
    a.run();
    a.applyForce(acceleration);
    a.applyForce(attractionForce);
  }
  // Star Parallax
  for (Star s: stars) {
    s.run();
    PVector standardAcc = new PVector(5,0);
    s.parallax(standardAcc);
    if (keyUp) { // Stuff like this has to be in this for loop, otherwise it won't work
      PVector up = new PVector(0,-15);
      s.parallax(up);
    }
    if (keyDown) {
      PVector down = new PVector(0,15);
      s.parallax(down);
    }
    if (keyRight) {
      PVector forward = new PVector(60,0);
      s.parallax(forward);
    }
  }
  checkCollision();
}
void checkCollision() {
  for (int i = 0; i < asteroids.size(); i++) {
    Asteroid asteroidObject  = asteroids.get(i);
    // check asteroid against ship
    if (asteroidObject.checkCollision(ship)) {
      gameStatus = gameOver;
    }
  }
}
void keyPressed() {

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
void keyReleased() { // without this, the ship only moves one time the key is pressed
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
