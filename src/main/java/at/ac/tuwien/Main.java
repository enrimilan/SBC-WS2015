package at.ac.tuwien;

import at.ac.tuwien.entity.Part;
import at.ac.tuwien.server.Server;
import at.ac.tuwien.view.SupplyOverviewController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;

public class Main extends Application {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    private Stage primaryStage;
    private BorderPane rootLayout;

    boolean isActive = true;


    private ObservableList<Part> partsData = FXCollections.observableArrayList();


    public Main() { }

    public ObservableList<Part> getPartsData() {
        return partsData;
    }


    public static void main(String[] args) {
        logger.info("Application started.");
        try {
            new Server();
           launch(args);

        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Drone Factory");

        initRootLayout();
        showSuppliesOverview();

        Updater u = new Updater();
        u.start();
    }


    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showSuppliesOverview() {
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("OutputOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();
            rootLayout.setCenter(personOverview);

            SupplyOverviewController controller = loader.getController();
            controller.setMain(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }


    private class Updater extends Thread {
        @Override
        public void run() {
            while (isActive) {
                try {
                    partsData.addAll(Server.retrunAllParts());
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                   logger.error(ex.toString());
                }
            }
        }

    }
}
