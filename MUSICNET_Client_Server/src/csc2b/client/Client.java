package csc2b.client;

//These are the packages I will need in this class.
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * @author Motsoetsoana M.R.
 * @version - Practical 6
 * 
 * This is the entry point for the MUSICNET client application.
 */
public class Client extends Application 
{
	
	public static void main(String[] args) 
	{
		//Launch JavaFX.
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception 
	{
		MusicClientPane root = new MusicClientPane();
		Scene scene = new Scene(root, 700, 400);
		
		primaryStage.setScene(scene);
		primaryStage.setTitle("MUSICNET Client");
		primaryStage.show();
	}

}
