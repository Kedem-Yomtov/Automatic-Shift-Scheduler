package question2;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ScheduleController {

	Schedule workSchedule;
	File ScheduleFile;
	File ShiftFile;
	boolean gotSchedule = false;
	boolean gotShifts = false;

    @FXML
    private GridPane schedule;

    @FXML
    private TextArea scheduleLog;

    @FXML
    private Button scheduleButton;

    @FXML
    private Button shiftsButton;

    @FXML
    private Label scheduleLogLabel;

    @FXML
    private Button buildButton;

    private Stage stage;

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    @FXML
    void initialize()
    {
    	//fix later

    	schedule.setStyle("-fx-grid-lines-visible: true; -fx-border-width: 1; -fx-border-color: black; -fx-background-color: white;");
    	schedule.setAlignment(Pos.CENTER);
    	String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        for (int col = 0; col < weekdays.length; col++) {
        	
            Label label = new Label(weekdays[col]);
            Font boldFont = Font.font("Arial", FontWeight.BOLD, 12);
            label.setFont(boldFont);
            GridPane.setHalignment(label, HPos.CENTER);
            GridPane.setValignment(label, VPos.CENTER);
            schedule.add(label, col, 0); // Adding to the top row
        }
    	
    	
    }
    @FXML
    void buildButtonPressed(ActionEvent event) throws InterruptedException 
    {
    	if(!(gotSchedule == true && gotShifts == true))
    	{
        	buildButton.setStyle("-fx-background-color: red; -fx-text-fill: black;");
        	return;
    	}
    	buildButton.setStyle("-fx-background-color: green; -fx-text-fill: black;");
    	workSchedule = new Schedule(ScheduleFile, ShiftFile);
        scheduleLog.setText(workSchedule.Schedstring);
        String returnMessage = workSchedule.runScheduleAlgorithm();
        scheduleLog.setText(returnMessage);
        drawSchedule();
    }
    public void drawSchedule()
    {
    	schedule.getChildren().removeIf(node -> node instanceof Label);
    	 schedule.setAlignment(Pos.CENTER);
    		String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            for (int col = 0; col < weekdays.length; col++) {
                Label label = new Label(weekdays[col]);
                Font boldFont = Font.font("Arial", FontWeight.BOLD, 12);
                label.setFont(boldFont);
                GridPane.setHalignment(label, HPos.CENTER);
                GridPane.setValignment(label, VPos.CENTER);
                schedule.add(label, col, 0); // Adding to the top row
            }
    	int shiftsPerCube = 0;
    	for(Cube c : workSchedule.cubes)
    	{
    		if (c.shifts.size() > shiftsPerCube)
    		{
    			shiftsPerCube = c.shifts.size();
    		}
    	}
        for(Cube c : workSchedule.cubes)
        {
        	int temp = 0;
        	for(Shift s : c.shifts)
        	{
            	Label label = new Label(s.toStringforSchedule());
            	if(label.getText().startsWith("Missing Shift") || label.getText().contains("Missing"))
            	{
            		label.setStyle("-fx-text-fill: red;");
            	}
            	else
            	{
            		label.setStyle("-fx-text-fill: black;");

            	}
            	label.setFont(new Font(12));
            	 GridPane.setHalignment(label, HPos.CENTER);
                 GridPane.setValignment(label, VPos.CENTER);
        		schedule.add(label, c.dayOfWeek, temp + 1);
        		 temp++;
        	}
        }
    	schedule.setStyle("-fx-grid-lines-visible: true; -fx-border-width: 1; -fx-border-color: black; -fx-background-color: white;");
   		schedule.setAlignment(Pos.CENTER);

    }
    @FXML
    void scheduleButtonPressed(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        // Set initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Add extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the FileChooser and get the selected file
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) 
        {
        	ScheduleFile = selectedFile;
        	scheduleButton.setStyle("-fx-background-color: green; -fx-text-fill: black;");
        	gotSchedule = true;
        } 
        else 
        {
        	scheduleButton.setStyle("-fx-background-color: red; -fx-text-fill: black;");
        	gotSchedule = false;

        }
        
    }

    @FXML
    void shiftsButtonPressed(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a File");

        // Set initial directory (optional)
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        // Add extension filter for text files
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show the FileChooser and get the selected file
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) 
        {
        	ShiftFile = selectedFile;
        	shiftsButton.setStyle("-fx-background-color: green; -fx-text-fill: black;");
        	gotShifts = true;
        } 
        else 
        {
        	shiftsButton.setStyle("-fx-background-color: red; -fx-text-fill: black;");
        	gotShifts = false;
        }
    }
    
    @FXML
    void saveScheduleButtonPressed(ActionEvent event) {
    	FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        // Show the Save File dialog
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            // Append ".png" if not already specified
            if (!file.getName().toLowerCase().endsWith(".png")) {
                file = new File(file.getAbsolutePath() + ".png");
            }

            // Save the GridPane as an image
            WritableImage image = new WritableImage((int) schedule.getWidth(), (int) schedule.getHeight());
            schedule.snapshot(null, image);

            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                System.out.println("Saved to " + file.getAbsolutePath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
}
