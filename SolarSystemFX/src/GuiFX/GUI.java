package GuiFX;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;


public class GUI extends Application {
    public static void main(String[] args){
        launch(args);
    }


    @Override
    public void start(Stage primaryStage){

        primaryStage.setTitle("Solar System");
        GridPane root = new GridPane();
        Button button = new Button("sto");
        root.getChildren().add(button);


        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().add("flatterAdd.css");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
