package question2;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class otherMain extends Application{
	
	
	public void start(Stage stage) throws Exception
	{
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/question2/scheduleGraphics.fxml"));
		Parent root = loader.load(); // Load the FXML through the loader
		Scene scene = new Scene(root);

		// Get the controller and pass the stage
		ScheduleController controller = loader.getController();
		controller.setStage(stage);
		stage.setScene(scene);
		stage.setTitle("Calendar");
		stage.show();
	}
	
}
