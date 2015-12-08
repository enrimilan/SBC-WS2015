package at.ac.tuwien;

import at.ac.tuwien.entity.Part;
import at.ac.tuwien.entity.PartG;
import at.ac.tuwien.server.Server;
import at.ac.tuwien.view.NotificationCallback;
import at.ac.tuwien.view.SupplyOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private boolean isActive = true;
    private ObservableList<Part> partsData = FXCollections.observableArrayList();
    private Server s ;
    public ObservableList<Part> getPartsData() {
        return partsData;
    }


    public static void main(String[] args) {
        logger.info("Application started.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("OutputOverview.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Drone Factory");
        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(we -> System.exit(0));

        SupplyOverviewController controller = loader.getController();
        controller.setMain(this);

        Updater u = new Updater();
        u.start();
    }


    private class Updater extends Thread {
        @Override
        public void run() {
            try {
                s =   new Server();
                s.registerNotificatioCallback(new NotificationCallback() {
                    @Override
                    public void notifyGUI(Part part) {
                        partsData.add(part);
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AlreadyBoundException e) {
                e.printStackTrace();
            }
            /*while (isActive) {
                try {
                    partsData.setAll(s.returnAllCases());
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                   logger.error(ex.toString());
                }
            }*/
        }
    }
}
