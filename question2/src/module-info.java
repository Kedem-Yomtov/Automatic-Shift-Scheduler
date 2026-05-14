/**
 * 
 */
/**
 * 
 */
module question2 {
	requires transitive javafx.graphics;
    requires transitive javafx.controls;
	requires javafx.fxml;
	requires javafx.base;
	requires java.desktop;
	requires javafx.swing;
	
    opens question2 to javafx.fxml,javafx.graphics;  
	exports question2;

}