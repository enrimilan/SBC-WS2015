package at.ac.tuwien.gui;

import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application started.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();
    }
}
