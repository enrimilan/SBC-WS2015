package at.ac.tuwien.common.view;

/**
 * Created by Arber on 07.12.2015.
 */
import at.ac.tuwien.MainRMI;
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.rmi.RmiConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.common.robot.AssemblyRobot;
import at.ac.tuwien.common.robot.CalibrationRobot;
import at.ac.tuwien.common.robot.LogisticRobot;
import at.ac.tuwien.common.robot.SupplierRobot;
import at.ac.tuwien.utils.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.rmi.RemoteException;

public class OverviewController {

    public OverviewController() {}
    private MainRMI main;


    @FXML
    private Label lbl_StockSize;
    @FXML
    private Label lbl_GoodDrones;
    @FXML
    private Label lbl_BadDrones;


    @FXML
    private TableView<Part> supplyTable;
    @FXML
    private TableColumn<Part, String> partId_supplyTable;
    @FXML
    private TableColumn<Part, String> partType_supplyTable;
    @FXML
    private TableColumn<Part, String> supplierId_supplyTable;



    @FXML
    private Button supplyButton;
    @FXML
    private Button startAssemblerButton;
    @FXML
    private Button startCalibratorButton;
    @FXML
    private TextField calibrationValueMIN_textfield;
    @FXML
    private TextField calibrationValueMAX_textfield;

    @FXML
    private Button startTesterButton;

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
                        SupplierRobot sR = new SupplierRobot(Utils.getConnection("rmi"), partType, amount);
                        Thread threadSupply = new Thread(sR);
                        threadSupply.start();
                    }
                }
            }
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAssemblerButtonAction(){
        try {
            AssemblyRobot aR = new AssemblyRobot(new RmiConnection());
            Thread threadAssemble = new Thread(aR);
            threadAssemble.start();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCalibratorButtonAction(){
        try {
            CalibrationRobot cR = new CalibrationRobot();
            Thread threadCalibrate = new Thread(cR);
            threadCalibrate.start();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleTesterButtonAction(){
        try {
            int min = Integer.parseInt(calibrationValueMIN_textfield.getCharacters().toString());
            int max = Integer.parseInt(calibrationValueMAX_textfield.getCharacters().toString());
            LogisticRobot lR = new LogisticRobot(min, max);
            Thread threadTester = new Thread(lR);
            threadTester.start();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /////////Drones Table //////////

    @FXML
    private TableView<Drone> dronesTable;
    @FXML
    private TableColumn<Drone, String> droneId;
    @FXML
    private TableColumn<Drone, String> droneStatus;

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

    ////  Modules ///

    @FXML
    private TableView<Module> moduleTableView;
    @FXML
    private TableColumn<Module, String> moduleType_moduleTableView;
    @FXML
    private TableColumn<Module, String> moduleStatus_moduleTableView;
    @FXML
    private TableColumn<Module, Integer> moduleCalibrationValue_moduleTableView;
    @FXML
    private TableColumn<Module, String> moduleAssemblerId_moduleTableView;
    @FXML
    private TableColumn<Module, String> moduleCalibratorrId_moduleTableView;


    @FXML
    private RadioButton rbGoodDrones;
    @FXML
    private RadioButton rbBadDrones;


    ////  Good Drones ///
    @FXML
    private TableView<Drone> goodDronesTable;
    @FXML
    private TableColumn<Drone, String> droneId_goodDronesTable;
    @FXML
    private TableColumn<Drone, String> assemblerId_goodDronesTable;


    ////  Bad Drones ///
    @FXML
    private TableView<Drone> badDronesTable;
    @FXML
    private TableColumn<Drone, String> droneId_badDronesTable;
    @FXML
    private TableColumn<Drone, String> assemblerId_badDronesTable;



    @FXML
    private void initialize() {
        badDronesTable.setVisible(false);
        supplyComboBox.setItems(supplyOptions);

        partId_supplyTable.setCellValueFactory(new PropertyValueFactory<>("partId"));
        partType_supplyTable.setCellValueFactory(new PropertyValueFactory<>("partType"));
        supplierId_supplyTable.setCellValueFactory(new PropertyValueFactory<>("supplierId"));

        droneId.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        droneStatus.setCellValueFactory(new PropertyValueFactory<>("status"));


        droneId_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        assemblerId_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));

        droneId_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        assemblerId_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));

        moduleType_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("moduleType"));
        moduleStatus_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("status"));
        moduleCalibrationValue_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("calibrationValue"));
        moduleAssemblerId_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));
        moduleCalibratorrId_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("calibratorId"));


        lbl_StockSize.setText(String.valueOf(supplyTable.getItems().size()));
        lbl_GoodDrones.setText(String.valueOf(goodDronesTable.getItems().size()));
        lbl_BadDrones.setText(String.valueOf(badDronesTable.getItems().size()));
    }

    public void setMain(MainRMI main) {
        this.main = main;

        supplyTable.setItems(main.getPartsData());
        dronesTable.setItems(main.getDronesData());
        moduleTableView.setItems(main.getModulesData());
        goodDronesTable.setItems(main.getGoodDronesData());
        badDronesTable.setItems(main.getBadDronesData());

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