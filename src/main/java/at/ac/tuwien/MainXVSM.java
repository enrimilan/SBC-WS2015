package at.ac.tuwien;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.server.IServer;
import at.ac.tuwien.common.view.GUI;
import at.ac.tuwien.common.view.OverviewController;
import at.ac.tuwien.utils.Utils;
import at.ac.tuwien.xvsm.XVSMServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainXVSM extends Application {

    private static Logger logger = LoggerFactory.getLogger(MainXVSM.class);

    public static void main(String[] args) {
        logger.info("Application started.");
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        IConnection connection = Utils.getConnectionInstance("xvsm");
        IServer server = new XVSMServer();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getClassLoader().getResource("OutputOverview.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Drone Factory");
        primaryStage.setMaximized(true);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setOnCloseRequest(we -> System.exit(0));
        GUI gui = new GUI(primaryStage, server);
        gui.setType("xvsm");
        OverviewController controller = loader.getController();
        controller.setGui(gui);
    }
}
