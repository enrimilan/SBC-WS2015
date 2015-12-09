package at.ac.tuwien.view;

/**
 * Created by Arber on 07.12.2015.
 */
import at.ac.tuwien.Main;
import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.entity.Drone;
import at.ac.tuwien.entity.Part;
import at.ac.tuwien.entity.PartType;
import at.ac.tuwien.robot.SupplierRobot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class OverviewController {

    public OverviewController() {}
    private Main main;

    @FXML
    private TableView<Part> supplyTable;
    @FXML
    private TableColumn<Part, String> id;
    @FXML
    private TableColumn<Part, String> partType;
    @FXML
    private TableColumn<Part, String> supplierId;
    @FXML
    private Button supplyButton = new Button();

    @FXML
    private TextField supplyTxtField;
    ObservableList<PartType> supplyOptions =
            FXCollections.observableArrayList(
                    PartType.CASE,
                    PartType.CONTROL_UNIT,
                    PartType.MOTOR,
                    PartType.ROTOR
            );


    @FXML
    private ComboBox<PartType> supplyComboBox;
    @FXML
    private void handleSupplyButtonAction(){
        try {
            if(!supplyTxtField.getCharacters().toString().isEmpty()){
                int amount = Integer.parseInt(supplyTxtField.getCharacters().toString());
                PartType  partType = supplyComboBox.getValue();
                supplyTxtField.clear();
                if (amount > 0) {
                    if ( partType != null) {
                        SupplierRobot sR = new SupplierRobot(partType, amount);
                        Thread threadSupply = new Thread(sR);
                        threadSupply.start();
                    }
                }
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }


    ////////////////////////

    @FXML
    private TableView<Drone> dronesTable;



    @FXML
    private TableColumn<Drone, String> droneId;
    @FXML
    private TableColumn<Drone, String> droneStatus;
    @FXML
    private TableColumn<Drone, String> droneCalibrationSum;

    @FXML
    private Label label_droneId;

    @FXML
    private Label label_droneStatus;

    @FXML
    private Label label_moduleCaseControlUnitPair;


    private void showDroneDetails(Drone selectedDrone) {

        if (selectedDrone != null) {
            main.showDroneDetailsDialog(selectedDrone);

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(main.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Drone Selected");
            alert.setContentText("Please select a drone in the table.");

            alert.showAndWait();
        }

    }




    ////  Asmebly ///

    @FXML
    private TableView<Drone> assemblyTable;

    @FXML
    private TableColumn<Drone, String> droneId_assemblyTable;

    @FXML
    private TableColumn<Drone, String> partId_assembyTable;

    @FXML
    private TableColumn<Drone, String> partType_assembyTable;

    @FXML
    private TableColumn<Drone, String> assemblerId_assembyTable;



    @FXML
    private RadioButton rbGoodDrones;

    @FXML
    private RadioButton rbBadDrones;


    ////  Good Drones ///

    @FXML
    private TableView<Drone> goodDronesTable;

    @FXML
    private TableColumn<Drone, String> calibrationMinValue_goodDronesTable;

    @FXML
    private TableColumn<Drone, String> calibrationMaxValue_goodDronesTable;

    @FXML
    private TableColumn<Drone, String> droneId_goodDronesTable;

    @FXML
    private TableColumn<Drone, String> assemblerId_goodDronesTable;


    ////  Good Drones ///

    @FXML
    private TableView<Drone> badDronesTable;

    @FXML
    private TableColumn<Drone, String> calibrationMinValue_badDronesTable;

    @FXML
    private TableColumn<Drone, String> calibrationMaxValue_badDronesTable;

    @FXML
    private TableColumn<Drone, String> droneId_badDronesTable;

    @FXML
    private TableColumn<Drone, String> assemblerId_badDronesTable;



    @FXML
    private void initialize() {
        supplyComboBox.setItems(supplyOptions);
        id.setCellValueFactory(new PropertyValueFactory<>("partId"));
        partType.setCellValueFactory(new PropertyValueFactory<>("partType"));
        supplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));

        droneId.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        droneStatus.setCellValueFactory(new PropertyValueFactory<>("status"));


        droneId_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        assemblerId_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));

        droneId_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        assemblerId_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));
        badDronesTable.setVisible(false);

//        droneId_assemblyTable.setCellValueFactory(new PropertyValueFactory<>("droneId"));
//        partId_assembyTable.setCellValueFactory(new PropertyValueFactory<>("partId"));
//        partType_assembyTable.setCellValueFactory(new PropertyValueFactory<>("partType"));
//        assemblerId_assembyTable.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));

    }

    public void setMain(Main main) {
        this.main = main;
        supplyTable.setItems(main.getPartsData());
        dronesTable.setItems(main.getDronesData());

        goodDronesTable.setItems(main.getGoodDronesData());
        badDronesTable.setItems(main.getBadDronesData());
//        assemblyTable.setItems(main.g);

        dronesTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ee) {
                if (ee.isPrimaryButtonDown() && ee.getClickCount() == 2) {
                    showDroneDetails(dronesTable.getSelectionModel().getSelectedItem());
                }
            }
        });

        goodDronesTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ee) {
                if (ee.isPrimaryButtonDown() && ee.getClickCount() == 2) {
                    showDroneDetails(goodDronesTable.getSelectionModel().getSelectedItem());
                }
            }
        });

        badDronesTable.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent ee) {
                if (ee.isPrimaryButtonDown() && ee.getClickCount() == 2) {
                    showDroneDetails(badDronesTable.getSelectionModel().getSelectedItem());
                }
            }
        });

        rbGoodDrones.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                goodDronesTable.setVisible(true);
                badDronesTable.setVisible(false);
            }
        });

        rbBadDrones.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                goodDronesTable.setVisible(false);
                badDronesTable.setVisible(true);

            }
        });

    }
}