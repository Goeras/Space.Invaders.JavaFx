package application;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GamePlay {

	private Pane root = new Pane();

	private double time = 0;
	private int level = 1;
	private boolean gameOverShown = false;
	private List<Enemy> enemyList = new ArrayList<>();
	private Player player = new Player(300, 700, 70, 70, "player", Color.BLUE);
	Stage stage;


	private Parent createContent() {
		root.setPrefSize(600, 800);

		Image img = new Image(getClass().getResource("Falcon.PixelArt.jpg").toExternalForm());
		player.setFill(new ImagePattern(img));
		root.getChildren().add(player);

		AnimationTimer timer = new AnimationTimer() {
			@Override
			public void handle(long now) {
				if(!player.isDead()) {
					update();
					
				}
				else { 
					if (!gameOverShown) {
                        stage.close();
                        gameOverScene();
                        gameOverShown = true;
					}
				}
			}
		};

		timer.start();

		nextLevel();

		return root;
	}
	
	public void gameOverScene() {
		Stage gameOverStage = new Stage();
		gameOverStage.setTitle("You're Dead!");
		
		Button btnExit = new Button("OK");
		btnExit.setOnAction(e -> gameOverStage.close());
		
		//Image img = new Image(getClass().getResource("GameOver.jpg").toExternalForm());
		
		Label label = new Label();
		label.setAlignment(Pos.CENTER);
		label.setText("Game Over!");
		
		VBox vBox = new VBox();
		vBox.setAlignment(Pos.BOTTOM_CENTER);
		vBox.setSpacing(200);
		vBox.setPadding(new Insets(0, 0, 150, 0));
		vBox.getChildren().addAll(btnExit);
		
		Scene scene = new Scene(vBox, 600, 800);
		
		scene.getStylesheets().add("application/gameover.css");
		
		gameOverStage.setScene(scene);
		gameOverStage.show();
		
		
		
	}

	public void nextLevel() {
		enemyList.clear();
		for (int i = 0; i<5; i++) {
			Enemy enemy = new Enemy(90 + i*100, 150, 50, 50, "enemy", Color.RED);
			Image img = new Image(getClass().getResource("TIE-Fighter.PixelArt.jpg").toExternalForm());
			enemy.setFill(new ImagePattern(img));
			root.getChildren().add(enemy);
			enemyList.add(enemy);
		}
	}

	private List<Enemy> listOfEnemies(){
		return root.getChildren().stream()
				.filter(n -> n instanceof Enemy) // Filtrera endast ut objekt som är instanser av Shot
				.map(n -> (Enemy) n)
				.collect(Collectors.toList());
	}

	private List<Shot> listOfShots(){
		return root.getChildren().stream()
				.filter(n -> n instanceof Shot) // Filtrera endast ut objekt som är instanser av Shot
				.map(n -> (Shot) n)
				.collect(Collectors.toList());
	}

	public void update() {
		time += 0.016;
		levelComplete();

		listOfShots().forEach(s -> {
			switch (s.getType()) {
			case "enemybullet":
				s.moveDown();
				if(s.getBoundsInParent().intersects(player.getBoundsInParent())) {
					player.setDead(true);
					s.setDead(true);
				}
				break;
			case "playerbullet":
				s.moveUp();
				listOfEnemies().stream().filter(e -> "enemy".equals(e.getType())).forEach(enemy ->{
					if(s.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
						enemy.setDead(true);
						s.setDead(true);
					}
				});
				break;

			}
		});

		for(Enemy e : enemyList)
			if(time > 2 && !e.isDead()) {
				if(Math.random() < 0.3) {
					enemyShot(e);
				}
			}
		// För Player
		root.getChildren().removeIf(n -> {
			if (n instanceof Player) {
				Player player = (Player) n;
				return player.isDead();
			}
			return false; // Om noden inte är en Player
		});

		// För Enemy
		root.getChildren().removeIf(n -> {
			if (n instanceof Enemy) {
				Enemy enemy = (Enemy) n;
				return enemy.isDead();
			}
			return false; // Om noden inte är en Enemy
		});

		// För Shot
		root.getChildren().removeIf(n -> {
			if (n instanceof Shot) {
				Shot shot = (Shot) n;
				return shot.isDead();
			}
			return false; // Om noden inte är en Shot
		});
		if(time > 2) {
			time = 0;
		}
	}

	public void levelComplete() {
		int enemiesDeadCounter = 0;
		for(Enemy s : enemyList) {
			if(s.isDead() == true) {
				enemiesDeadCounter++;
			}
		}
		if(enemiesDeadCounter == enemyList.size()) {
			PauseTransition pause = new PauseTransition(Duration.seconds(2));
			pause.setOnFinished( e -> nextLevel());
			pause.play();
		}

	}

	public void gameStage() {
		stage = new Stage();
		Scene scene =  new Scene(createContent());

		scene.setOnKeyPressed(e -> {
			switch (e.getCode()) {
			case A:
				player.moveLeft();
				break;
			case D:
				player.moveRight();
				break;
			case SPACE:
				playerShot(player);
				break;
			default:
				break;
			}
		});

		scene.getStylesheets().add("application/application.css");
		stage.setScene(scene);
		stage.show();
	}

	private void playerShot(Player player) {

		Shot shot = new Shot((int)player.getTranslateX() + 20, (int)player.getTranslateY(), 5, 20, player.getType() + "bullet", Color.WHITE);

		root.getChildren().add(shot);


	}

	private void enemyShot(Enemy enemy) {

		Shot shot = new Shot((int)enemy.getTranslateX() + 20, (int)enemy.getTranslateY(), 5, 20, enemy.getType() + "bullet", Color.WHITE);

		root.getChildren().add(shot);

	}

}
