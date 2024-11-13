package com.unidawgs.le5.clubdawgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Minigame1 extends Application{
	//variables
	private static final Random RAND = new Random();
	private static final int WIDTH = 800;
	private static final int HEIGHT = 600;
	private static final int PLAYER_SIZE = 60;
	final Image PLAYER_IMG = new Image(getClass().getResource("mg1-player.png").toExternalForm());
	final Image EXPLOSION_IMG = new Image(getClass().getResource("mg1-explosion.png").toExternalForm());
	static final int EXPLOSION_W = 128;
	static final int EXPLOSION_ROWS = 3;
	static final int EXPLOSION_COL = 3;
	static final int EXPLOSION_H = 128;
	static final int EXPLOSION_STEPS = 15;
	

	final Image BOMBS_IMG[] = new Image[10]; //Turned into an array of Image objects (see line 38-49) then 65-67
	final int MAX_BOMBS = 10,  MAX_SHOTS = MAX_BOMBS * 2;
	boolean gameOver = false;
	private GraphicsContext gc;
	
	Rocket player;
	List<Shot> shots;
	List<Universe> univ;
	List<Bomb> Bombs;
	private List<PowerUp> powerUps = new ArrayList<>(); // This ensures powerUps is always initialized
	
	private double mouseX;
	private int score;

	boolean powerUpsSpawnedThisRound = false; // Initialize this variable to false

	 // Add background images for the game
	 private final Image[] BACKGROUNDS = new Image[]{
        new Image(getClass().getResource("bg1.jpg").toExternalForm()), // Background 1
        new Image(getClass().getResource("bg2.jpg").toExternalForm()), // Background 2
        new Image(getClass().getResource("bg3.png").toExternalForm())  // Background 3
    };
    private int currentBackgroundIndex = 0;
    private long lastBackgroundChangeTime = System.currentTimeMillis(); // Time when background was last changed


	 //Sound FX
	 private MediaPlayer mainMenuPlayer;
    private MediaPlayer bgSoundPlayer;
    private MediaPlayer gameOverPlayer;
    private MediaPlayer scoreSoundPlayer;

	 private void loadSounds() {
		mainMenuPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_mainmenusound.mp3");
		bgSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_gamebgsound.mp3");
		gameOverPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_gameoversound.mp3");
		scoreSoundPlayer = createMediaPlayer("/com/unidawgs/le5/clubdawgs/mg1_scoresound-1.mp3");
  	}

	private MediaPlayer createMediaPlayer(String filePath) {
        Media sound = new Media(getClass().getResource(filePath).toExternalForm());
        return new MediaPlayer(sound);
    }


	// Constructor or Initialization Block to populate BOMBS_IMG
   public Minigame1() {
		for (int i = 0; i < BOMBS_IMG.length; i++) {
			 BOMBS_IMG[i] = new Image(getClass().getResource("mg1-alien" + (i + 1) + ".png").toExternalForm());
		}
  	}

	public void startGame(Stage stage){
		Canvas canvas = new Canvas(WIDTH, HEIGHT);
		gc = canvas.getGraphicsContext2D();
		Timeline timeline = new Timeline(new KeyFrame(Duration.millis(100), e -> run(gc)));
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.play();
		canvas.setCursor(Cursor.MOVE);
		canvas.setOnMouseMoved(e -> mouseX = e.getX());
		canvas.setOnMouseClicked(e -> {
			if(shots.size() < MAX_SHOTS) shots.add(player.shoot());
			if(gameOver) {
				gameOver = false;
				setup();
			}
		});
		setup();
		stage.setScene(new Scene(new StackPane(canvas)));
		stage.setTitle("Catvasion");
		stage.setResizable(false);
		stage.setOnCloseRequest(x -> {
			x.consume();
			stage.close();
		});
		stage.show();

		loadSounds();
		bgSoundPlayer.play();
	}
	//setup the game
	private void setup() {
		univ = new ArrayList<>();
		shots = new ArrayList<>();
		Bombs = new ArrayList<>();
		player = new Rocket(WIDTH / 2, HEIGHT - PLAYER_SIZE, PLAYER_SIZE, PLAYER_IMG);
		score = 0;
		IntStream.range(0, MAX_BOMBS).mapToObj(i -> this.newBomb()).forEach(Bombs::add);
		lastBackgroundChangeTime = System.currentTimeMillis(); // Reset the background change timer
	}
	
	//run Graphics
	private void run(GraphicsContext gc) {
		 // Handle background switch after 1 minute
		 long currentTime = System.currentTimeMillis();
		 if (currentTime - lastBackgroundChangeTime >= 30000) { // If 30 seconds have passed
			 currentBackgroundIndex = (currentBackgroundIndex + 1) % BACKGROUNDS.length; // Cycle through the backgrounds
			 lastBackgroundChangeTime = currentTime; // Update the last background change time
		 }
 
		 // Draw the current background
		 gc.drawImage(BACKGROUNDS[currentBackgroundIndex], 0, 0, WIDTH, HEIGHT); // THIS IS A CHANGE
		//gc.setFill(Color.grayRgb(20));
		//gc.fillRect(0, 0, WIDTH, HEIGHT);
		gc.setTextAlign(TextAlignment.CENTER);
		gc.setFont(Font.font(20));
		gc.setFill(Color.WHITE);
		gc.fillText("Score: " + score, 60,  20);

		for (PowerUp powerUp : powerUps) {
			powerUp.update(); // Update the position
			powerUp.draw();   // Draw the power-up
		}
	
		
		if(gameOver) {
			gc.setFont(Font.font(35));
			gc.setFill(Color.YELLOW);
			gc.fillText("Game Over \n Your Score is: " + score + " \n Click to play again", WIDTH / 2, HEIGHT /2.5);
		//	return;
		}
		univ.forEach(Universe::draw);
	
		player.update();
		player.draw();
		player.posX = (int) mouseX;
		
		Bombs.stream().peek(Rocket::update).peek(Rocket::draw).forEach(e -> {
			if(player.colide(e) && !player.exploding) {
				player.explode();
				playGameOverSound(); //Sound FX
			}
		});

				// Handle Power-ups Collision
				for (int i = powerUps.size() - 1; i >= 0; i--) { //THIS IS A CHANGE
					PowerUp powerUp = powerUps.get(i); //THIS IS A CHANGE
					if (player.colide(powerUp) && !powerUp.destroyed) { //THIS IS A CHANGE
						powerUp.activate(player); //THIS IS A CHANGE
						powerUp.destroyed = true; //THIS IS A CHANGE
					}
					if (powerUp.destroyed) { //THIS IS A CHANGE
						powerUps.remove(i); //THIS IS A CHANGE
					}
				}
		
		
		for (int i = shots.size() - 1; i >=0 ; i--) {
			Shot shot = shots.get(i);
			if(shot.posY < 0 || shot.toRemove)  { 
				shots.remove(i);
				continue;
			}
			shot.update();
			shot.draw();
			for (Bomb bomb : Bombs) {
				if (shot.colide(bomb) && !bomb.exploding) {
					scoreSoundPlayer.play(); //Sound FX
					scoreSoundPlayer.stop(); //Sound FX
						score++; // Regular points
					bomb.explode();
					shot.toRemove = true;
				}
			}
		}
		
		for (int i = Bombs.size() - 1; i >= 0; i--){  
			if(Bombs.get(i).destroyed)  {
				Bombs.set(i, newBomb());
			}
		}
	
		gameOver = player.destroyed;
		if(RAND.nextInt(10) > 2) {
			univ.add(new Universe());
		}
		for (int i = 0; i < univ.size(); i++) {
			if(univ.get(i).posY > HEIGHT)
				univ.remove(i);
		}
	// Spawn Power-ups every 20 points (Change 20 to your desired score threshold)
	if (score % 20 == 0 && score > 0 && !powerUpsSpawnedThisRound) { //THIS IS A CHANGE
		// Spawn the Treat power-up
		PowerUpTreat TreatPowerUp = new PowerUpTreat(50 + RAND.nextInt(WIDTH - 100), 0); //THIS IS A CHANGE
		TreatPowerUp.initialize(); // Call initialize to set the image //THIS IS A CHANGE
		powerUps.add(TreatPowerUp); //THIS IS A CHANGE

		powerUpsSpawnedThisRound = true; // Mark that power-ups were spawned for this score threshold
	} else if (score % 20 != 0) {
		powerUpsSpawnedThisRound = false; // Reset the spawn flag when the player hasn't reached the next threshold
	}
	}

	//player
	public class Rocket {

		int posX, posY, size;
		boolean exploding, destroyed;
		Image img;
		int explosionStep = 0;
		
		public Rocket(int posX, int posY, int size,  Image image) {
			this.posX = posX;
			this.posY = posY;
			this.size = size;
			img = image;
		}
		
		public Shot shoot() {
			return new Shot(posX + size / 2 - Shot.size / 2, posY - Shot.size);
		}

		public void update() {
			if(exploding) explosionStep++;
			destroyed = explosionStep > EXPLOSION_STEPS;
		}
		
		public void draw() {
			if(exploding) {
				gc.drawImage(EXPLOSION_IMG, explosionStep % EXPLOSION_COL * EXPLOSION_W, (explosionStep / EXPLOSION_ROWS) * EXPLOSION_H + 1,
						EXPLOSION_W, EXPLOSION_H,
						posX, posY, size, size);
				if(explosionStep == 0){
					scoreSoundPlayer.play(); //Sound FX
				}
			}
			else {
				gc.drawImage(img, posX, posY, size, size);
			}
		}
	
		public boolean colide(Rocket other) {
			int d = distance(this.posX + size / 2, this.posY + size /2, 
							other.posX + other.size / 2, other.posY + other.size / 2);
			return d < other.size / 2 + this.size / 2 ;
		}

		public boolean colide(PowerUp powerUp) { //THIS IS A CHANGE
			return Math.abs(posX - powerUp.posX) < size / 2 + 20 && Math.abs(posY - powerUp.posY) < size / 2 + 20; // Adjust the collision radius as necessary
		}
		
		public void explode() {
			exploding = true;
			explosionStep = -1;
		}

	}

	//Base PowerUp class (abstract)
	public abstract class PowerUp {
		int posX, posY;
		Image img;
		boolean destroyed = false;
	
		public PowerUp(int x, int y) {
			this.posX = x;
			this.posY = y;
			this.img = null; // Initialize img to null
		}
	
		public void update() {
			posY += 5; // Move the power-up down the screen
		}
	
		public void draw() {
			if (img != null) {
				gc.drawImage(img, posX, posY, 40, 40); // Draw the power-up
			}
		}
	
		public abstract void activate(Rocket player);
		public void initialize() {} // This method can be overridden to initialize the image later
	}

// Subclass of PowerUp for doubling points
public class PowerUpTreat extends PowerUp {
    private long activationTime; // To track when it was activated
    private static final long DURATION = 10000; // Power-up duration (10 seconds)

    public PowerUpTreat(int x, int y) {
        super(x, y); // Call superclass constructor
        initialize(); // Initialize the image
    }

    @Override
    public void initialize() {
        this.img = new Image(getClass().getResource("treat.jpg").toExternalForm()); // Set the image here
        this.activationTime = System.currentTimeMillis();
    }

    @Override
    public void activate(Rocket player) {
        // Activate the Treat power-up 
        score += 10;;
    }

    @Override
    public void update() {
        super.update();
        // Deactivate the power-up after the duration ends
        if (System.currentTimeMillis() - activationTime > DURATION) {
            destroyed = true; // Mark the power-up as destroyed after the duration
        }
    }

    @Override
    public void draw() {
        super.draw();
    }
}

	//computer player
	public class Bomb extends Rocket {
		
		int SPEED = (score/5)+5;
		
		public Bomb(int posX, int posY, int size, Image image) {
			super(posX, posY, size, image);
		}
		
		public void update() {
			super.update();
			if(!exploding && !destroyed) posY += SPEED;
			if(posY > HEIGHT) destroyed = true;
		}
	}

	//bullets
	public class Shot {
		
		public boolean toRemove;

		int posX, posY = 10;
		int speed = 20; //speed of the bullet
		static final int size = 6;
			
		public Shot(int posX, int posY) {
			this.posX = posX;
			this.posY = posY;
		}

		public void update() {
			posY-=speed;
		}
		

		public void draw() {
			gc.setFill(Color.web("#A4D143"));
			if (score >=50 && score<=70 || score>=120) {
				gc.setFill(Color.YELLOWGREEN);
				speed = 50;
				gc.fillRect(posX-5, posY-10, size+10, size+30);
			} else {
			gc.fillOval(posX, posY, size, size);
			}
		}
		
		public boolean colide(Rocket Rocket) {
			int distance = distance(this.posX + size / 2, this.posY + size / 2, 
					Rocket.posX + Rocket.size / 2, Rocket.posY + Rocket.size / 2);
			return distance  < Rocket.size / 2 + size / 2;
		} 
		
		
	}
	
	//environment
	public class Universe {
		int posX, posY;
		private int h, w, r, g, b;
		private double opacity;
		
		public Universe() {
			posX = RAND.nextInt(WIDTH);
			posY = 0;
			w = RAND.nextInt(5) + 1;
			h =  RAND.nextInt(5) + 1;
			r = RAND.nextInt(100) + 150;
			g = RAND.nextInt(100) + 150;
			b = RAND.nextInt(100) + 150;
			opacity = RAND.nextFloat();
			if(opacity < 0) opacity *=-1;
			if(opacity > 0.5) opacity = 0.5;
		}
		
		public void draw() {
			if(opacity > 0.8) opacity-=0.01;
			if(opacity < 0.1) opacity+=0.01;
			gc.setFill(Color.rgb(r, g, b, opacity));
			gc.fillOval(posX, posY, w, h);
			posY+=20;
		}
	}
	
	
	Bomb newBomb() {
		return new Bomb(50 + RAND.nextInt(WIDTH - 100), 0, PLAYER_SIZE, BOMBS_IMG[RAND.nextInt(BOMBS_IMG.length)]);
	}
	
	int distance(int x1, int y1, int x2, int y2) {
		return (int) Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}
	public void start(Stage primaryStage) {
		try {
			Minigame1ViewManager manager = new Minigame1ViewManager();
			primaryStage = manager.getMainStage();
			primaryStage.setTitle("Catvasion");
			primaryStage.setResizable(false);
			primaryStage.setOnCloseRequest(x ->{
				x.consume();

				Platform.exit();

			});
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}


	// Methods to play sounds
		 private void playMainMenuSound() {
			mainMenuPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			mainMenuPlayer.play();
	  }
 
	  private void playBGSound() {
			bgSoundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
			bgSoundPlayer.play();
	  }
 
	  private void playGameOverSound() {
			gameOverPlayer.stop();
			gameOverPlayer.play();
	  }
 
	  private void playScoreSound() {
			scoreSoundPlayer.stop();
			scoreSoundPlayer.play();
	  }

}
