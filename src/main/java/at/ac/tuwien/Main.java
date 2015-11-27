package at.ac.tuwien;

import at.ac.tuwien.server.Server;
import javafx.application.Application;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application started.");
        //launch(args);
        try {
            new Server();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.show();
    }
}
