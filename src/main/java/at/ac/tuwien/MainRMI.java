package at.ac.tuwien;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.view.GUI;
import at.ac.tuwien.common.view.OverviewController;
import at.ac.tuwien.rmi.RMIServer;
import at.ac.tuwien.utils.Utils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainRMI extends Application {

    private static Logger logger = LoggerFactory.getLogger(MainRMI.class);

    public static void main(String[] args) {
        logger.info("Application started.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        IConnection connection = Utils.getConnectionInstance("rmi");
        IServer server = new RMIServer();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("OutputOverview.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Drone Factory");
        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(we -> System.exit(0));
        GUI gui = new GUI(primaryStage, server);
        OverviewController controller = loader.getController();
        controller.setGui(gui);
    }
}
