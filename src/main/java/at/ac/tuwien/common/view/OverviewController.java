package at.ac.tuwien.common.view;

/**
 * Created by Arber on 07.12.2015.
 */
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.common.entity.Color;
import at.ac.tuwien.common.robot.*;
import at.ac.tuwien.utils.Utils;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.*;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class OverviewController {

    public OverviewController() {}
    private GUI gui;


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
    private TableColumn<Part, Color> caseColor_supplyTable;


    @FXML
    private Button supplyButton;
    @FXML
    private Button consoleSupplyButton;
    @FXML
    private Button startAssemblerButton;
    @FXML
    private Button consoleAssemblerButton;
    @FXML
    private Button startCalibratorButton;
    @FXML
    private Button consoleCalibratorButton;
    @FXML
    private TextField calibrationValueMIN_textfield;
    @FXML
    private TextField calibrationValueMAX_textfield;
    @FXML
    private Button startTesterButton;
    @FXML
    private Button consoleTesterButton;
    @FXML
    private Button startPainterButton;
    @FXML
    private Button consolePainterButton;

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


    ObservableList<CaseType> caseTypeOptions =
            FXCollections.observableArrayList(
                    CaseType.NORMAL,
                    CaseType.PACKAGE_HOLDER,
                    CaseType.CAMERA_HOLDER
            );


    @FXML
    private ComboBox<CaseType> caseTypeComboBox;

    ObservableList<Color> droneColorsOptions =
            FXCollections.observableArrayList(
                    Color.BLUE,
                    Color.GRAY,
                    Color.GREEN,
                    Color.RED
            );


    @FXML
    private ComboBox<Color> dronoColorComboBox;

    @FXML
    private void handleSupplyButtonAction(){
        try {
            if(!supplyTxtField.getCharacters().toString().isEmpty()){
                int amount = Integer.parseInt(supplyTxtField.getCharacters().toString());
                PartType  partType = supplyComboBox.getValue();
                supplyTxtField.clear();
                if (amount > 0) {
                    if ( partType != null) {
                        ArrayList<String> addresses = new ArrayList<>();
                        addresses.add(gui.getHost()+":"+gui.getPort());
                        SupplierRobot sR = new SupplierRobot(Utils.getConnectionInstance(), partType, amount, addresses);
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
    private void handlePainterButtonAction(){
        try {
            PaintingRobot pR = new PaintingRobot(Utils.getConnectionInstance(), gui.getHost(), gui.getPort());
            Thread threadSupply = new Thread(pR);
            threadSupply.start();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConsoleSupplyButtonAction(){
            if(!supplyTxtField.getCharacters().toString().isEmpty()){
                int amount = Integer.parseInt(supplyTxtField.getCharacters().toString());
                PartType  partType = supplyComboBox.getValue();
                supplyTxtField.clear();
                if (amount > 0) {
                    if ( partType != null) {
                        Path currentRelativePath = null;
                        try {
                            if(this.gui.getType().equals("rmi")){
                                currentRelativePath = Paths.get(getClass().getClassLoader().getResource("supplier-rmi.bat").toURI());
                            } else {
                                currentRelativePath = Paths.get(getClass().getClassLoader().getResource("supplier-xvms.bat").toURI());
                            }
                            String partIdentifier = "";
                            if(partType == PartType.CASE){
                                partIdentifier = " A ";
                            } else if( partType == PartType.CONTROL_UNIT){
                                partIdentifier = " B ";
                            } else if(partType == PartType.MOTOR){
                                partIdentifier = " C ";
                            } else if (partType == PartType.ROTOR){
                                partIdentifier = " D ";
                            }

                            String command = "cmd.exe /c start " + currentRelativePath.toString() + partIdentifier + amount ;
                            Runtime.getRuntime().exec(command);
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    }

    @FXML
    private void handleConsoleAssemblerButtonAction(){
                    try {
                        Path currentRelativePath = null;
                        if(this.gui.getType().equals("rmi")){
                            currentRelativePath = Paths.get(getClass().getClassLoader().getResource("assembler-rmi.bat").toURI());
                        } else {
                            currentRelativePath = Paths.get(getClass().getClassLoader().getResource("assembler-xvsm.bat").toURI());
                        }
                        String command = "cmd.exe /c start " + currentRelativePath.toString() ;
                        Runtime.getRuntime().exec(command);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
    }

    @FXML
    private void handleConsoleCalibratorButtonAction(){
        try {
            Path currentRelativePath = null;
            if(this.gui.getType().equals("rmi")){
                currentRelativePath = Paths.get(getClass().getClassLoader().getResource("calibrator-rmi.bat").toURI());
            } else {
                currentRelativePath = Paths.get(getClass().getClassLoader().getResource("calibrator-xvsm.bat").toURI());
            }
            String command = "cmd.exe /c start " + currentRelativePath.toString() ;
            Runtime.getRuntime().exec(command);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleConsoleTesterButtonAction(){
        int min = Integer.parseInt(calibrationValueMIN_textfield.getCharacters().toString());
        int max = Integer.parseInt(calibrationValueMAX_textfield.getCharacters().toString());

        Path currentRelativePath = null;
                    try {
                        if(this.gui.getType().equals("rmi")){
                            currentRelativePath = Paths.get(getClass().getClassLoader().getResource("tester-rmi.bat").toURI());
                        } else {
                            currentRelativePath = Paths.get(getClass().getClassLoader().getResource("tester-xvsm.bat").toURI());
                        }
                        String command = "cmd.exe /c start " + currentRelativePath.toString() + " " + min + " " + max;
                        Runtime.getRuntime().exec(command);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
    }

    @FXML
    private void handleAssemblerButtonAction(){
        try {
            AssemblyRobot aR = new AssemblyRobot(Utils.getConnectionInstance(), gui.getHost(), gui.getPort());
            Thread threadAssemble = new Thread(aR);
            threadAssemble.start();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCalibratorButtonAction(){
        try {
            CalibrationRobot cR = new CalibrationRobot(Utils.getConnectionInstance(), gui.getHost(), gui.getPort());
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
            LogisticRobot lR = new LogisticRobot(Utils.getConnectionInstance(), min, max, gui.getHost(), gui.getPort());
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
            gui.showDroneDetailsDialog(selectedDrone);

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(gui.getPrimaryStage());
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
        caseTypeComboBox.setItems(caseTypeOptions);
        dronoColorComboBox.setItems(droneColorsOptions);

        partId_supplyTable.setCellValueFactory(new PropertyValueFactory<>("partId"));
        partType_supplyTable.setCellValueFactory(new PropertyValueFactory<>("partType"));
        supplierId_supplyTable.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        caseColor_supplyTable.setCellValueFactory(new PropertyValueFactory<>("caseColor"));
        caseColor_supplyTable.setCellFactory(new Callback<TableColumn<Part, Color>, TableCell<Part, Color>>() {
            @Override
            public TableCell<Part, Color> call(TableColumn<Part, Color> param) {
                return new TableCell<Part, Color>(){
                    @Override
                    public void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);
                        setAlignment(Pos.CENTER);
                        if (item == null || empty) {
                            if(!empty){
                                setText("N/A");
                                setStyle("-fx-background-color: none");
                            }
                        }else {
                            setText(item.name());

                            if(item == Color.BLUE){
                                setTextFill(javafx.scene.paint.Color.WHITE);
                                setStyle("-fx-background-color: blue");
                            }else if(item == Color.RED){
                                setTextFill(javafx.scene.paint.Color.WHITE);
                                setStyle("-fx-background-color: red");
                            }else if(item == Color.GREEN){
                                setTextFill(javafx.scene.paint.Color.WHITE);
                                setStyle("-fx-background-color: green");
                            }else if(item == Color.GRAY){
                                setTextFill(javafx.scene.paint.Color.WHITE);
                                setStyle("-fx-background-color: gray");
                            }
                        }
                    }
                };
            }
        });

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
    }

    public void setGui(GUI gui) {
        this.gui = gui;

        supplyTable.setItems(gui.getPartsData());
        dronesTable.setItems(gui.getDronesData());
        moduleTableView.setItems(gui.getModulesData());
        goodDronesTable.setItems(gui.getGoodDronesData());
        badDronesTable.setItems(gui.getBadDronesData());

        gui.getPartsData().addListener(new ListChangeListener<Part>() {
            @Override
            public void onChanged(Change<? extends Part> c) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lbl_StockSize.setText(String.valueOf(supplyTable.getItems().size()));
                    }
                });
            }
        });
        gui.getGoodDronesData().addListener(new ListChangeListener<Drone>() {
            @Override
            public void onChanged(Change<? extends Drone> c) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lbl_GoodDrones.setText(String.valueOf(goodDronesTable.getItems().size()));
                    }
                });
            }
        });
        gui.getBadDronesData().addListener(new ListChangeListener<Drone>() {
            @Override
            public void onChanged(Change<? extends Drone> c) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        lbl_BadDrones.setText(String.valueOf(badDronesTable.getItems().size()));
                    }
                });
            }
        });

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