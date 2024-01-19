package application;
	
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;


public class Main extends Application {
	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			Button btnQuit = new Button("Quit");
			btnQuit.setMaxSize(80, 40);
			
			btnQuit.setOnAction( e -> primaryStage.close());
			
			Button btnPlay = new Button("Play");
			btnPlay.setMaxSize(80, 40);
			btnPlay.setOnAction( e -> startGame());
			
			VBox vBox = new VBox();
			vBox.setAlignment(Pos.CENTER);
			vBox.setSpacing(20);
			vBox.getChildren().addAll(btnPlay, btnQuit);
			
			Scene scene = new Scene(vBox, 400, 600);
			scene.getStylesheets().add("application/mainmenu.css");
			
			primaryStage.setTitle("Space Invaders");
			primaryStage.setScene(scene);
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startGame() {
		GamePlay gamePlay = new GamePlay(); // Skapar upp en ny instans för varje gång spelet startas. pga av att ".root" annars redan är upptaget.
		gamePlay.gameStage();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
