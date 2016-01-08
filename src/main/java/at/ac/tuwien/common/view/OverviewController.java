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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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

    /// Supply Table ///
    @FXML
    private TableView<Part> supplyTable;
    @FXML
    private TableColumn<Part, String> partId_supplyTable;
    @FXML
    private TableColumn<Part, String> partType_supplyTable;
    @FXML
    private TableColumn<Part, String> supplierId_supplyTable;
    @FXML
    private TableColumn<Part, String> painterId_supplyTable;


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
    private void handleConsolePainterButtonAction(){
        try {
            Path currentRelativePath = null;
            if(this.gui.getType().equals("rmi")){
                currentRelativePath = Paths.get(getClass().getClassLoader().getResource("painter-rmi.bat").toURI());
            } else {
                currentRelativePath = Paths.get(getClass().getClassLoader().getResource("painter-xvsm.bat").toURI());
            }
            String command = "cmd.exe /c start \"\" " + currentRelativePath.toString() + " " + gui.getHost() + " " + gui.getPort();
            Runtime.getRuntime().exec(command);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
                                partIdentifier = "A";
                            } else if( partType == PartType.CONTROL_UNIT){
                                partIdentifier = "B";
                            } else if(partType == PartType.MOTOR){
                                partIdentifier = "C";
                            } else if (partType == PartType.ROTOR){
                                partIdentifier = "D";
                            }

                            String command = "cmd.exe /C start \"\" " + currentRelativePath.toString() + " " + partIdentifier + " " + amount + " " + gui.getHost()+":"+gui.getPort();
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
                        String command = "cmd.exe /c start " + currentRelativePath.toString() + " " + gui.getHost() + " " + gui.getPort() ;
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
            String command = "cmd.exe /c start \"\" " + currentRelativePath.toString() + " " + gui.getHost() + " " + gui.getPort();
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
                        String command = "cmd.exe /c start \"\" " + currentRelativePath.toString() + " " + min + " " + max + " " + gui.getHost() + " " + gui.getPort();
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

    @FXML
    private void handleCreateOrderButtonAction(){

        if(!dronesNumber_textfield.getCharacters().toString().isEmpty()) {
            int orderSize = Integer.parseInt(dronesNumber_textfield.getCharacters().toString());
            CaseType caseType = caseTypeComboBox.getValue();
            Color droneColor = droneColorComboBox.getValue();
            dronesNumber_textfield.clear();

            Order order = new Order(orderSize, caseType, droneColor);
            gui.addOrder(order);

            // TODO
        }
    }

    /////////Drones Table //////////

    @FXML
    private TableView<Drone> dronesTable;
    @FXML
    private TableColumn<Drone, String> droneId;
    @FXML
    private TableColumn<Drone, String> orderId;
    @FXML
    private TableColumn<Drone, CaseType> droneType;
    @FXML
    private TableColumn<Drone, String> droneStatus;
    @FXML
    private TableColumn<Drone, Color> droneColor;

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
    private TableColumn<Drone, CaseType> droneType_goodDronesTable;
    @FXML
    private TableColumn<Drone, String> assemblerId_goodDronesTable;
    @FXML
    private TableColumn<Drone, Color> droneColor_goodDronesTable;

    ////  Bad Drones ///
    @FXML
    private TableView<Drone> badDronesTable;
    @FXML
    private TableColumn<Drone, String> droneId_badDronesTable;
    @FXML
    private TableColumn<Drone, CaseType> droneType_badDronesTable;
    @FXML
    private TableColumn<Drone, String> assemblerId_badDronesTable;
    @FXML
    private TableColumn<Drone, Color> droneColor_badDronesTable;


    ////  Orders ///
    @FXML
    private TableView<Order> ordersTable;
    @FXML
    private TableColumn<Order, String> orderId_ordersTable;
    @FXML
    private TableColumn<Order, String> dronesNumber_ordersTable;
    @FXML
    private TableColumn<Order, String> caseType_ordersTable;
    @FXML
    private TableColumn<Order, Color> caseColor_ordersTable;
    @FXML
    private TableColumn<Order, String> status_ordersTable;

    @FXML
    private TextField dronesNumber_textfield;

    @FXML
    private ComboBox<CaseType> caseTypeComboBox;
    ObservableList<CaseType> caseTypeOptions =
            FXCollections.observableArrayList(
                    CaseType.NORMAL,
                    CaseType.PACKAGE_HOLDER,
                    CaseType.CAMERA_HOLDER
            );

    @FXML
    private ComboBox<Color> droneColorComboBox;
    ObservableList<Color> droneColorsOptions =
            FXCollections.observableArrayList(
                    Color.BLUE,
                    Color.GREEN,
                    Color.RED
            );

    @FXML
    private void initialize() {
        badDronesTable.setVisible(false);
        supplyComboBox.setItems(supplyOptions);
        caseTypeComboBox.setItems(caseTypeOptions);
        droneColorComboBox.setItems(droneColorsOptions);

        partId_supplyTable.setCellValueFactory(new PropertyValueFactory<>("partId"));
        partType_supplyTable.setCellValueFactory(new PropertyValueFactory<>("partWithType"));
        partType_supplyTable.setCellFactory(new Callback<TableColumn<Part, String>, TableCell<Part, String>>() {
            @Override
            public TableCell<Part, String> call(TableColumn<Part, String> param) {
                return new TableCell<Part, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        setAlignment(Pos.CENTER);
                        if (item == null || empty) {
                            if (!empty) {
                                this.setText(null);
                            }
                            setStyle("-fx-background-color: none");
                            this.setText(null);
                        } else {
                            if (item.endsWith("BLUE")) {
                                setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            } else if (item.endsWith("RED")) {
                                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            } else if (item.endsWith("GREEN")) {
                                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            } else if (item.endsWith("GRAY")) {
                                setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
                            } else setTextFill(javafx.scene.paint.Color.BLACK);
                            this.setText(item.split(":")[0]);
                        }
                    }
                };
            }
        });

        supplierId_supplyTable.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        painterId_supplyTable.setCellValueFactory(new PropertyValueFactory<>("painterId"));

        droneId.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        orderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        droneStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        droneType.setCellValueFactory(new PropertyValueFactory<>("droneType"));
        droneColor.setCellValueFactory(new PropertyValueFactory<>("droneColor"));
        droneColor.setCellFactory(new Callback<TableColumn<Drone, Color>, TableCell<Drone, Color>>() {
            @Override
            public TableCell<Drone, Color> call(TableColumn<Drone, Color> param) {
                return new TableCell<Drone, Color>() {
                    @Override
                    public void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            if (!empty) {
                                this.setText(null);
                            }
                            setStyle("-fx-background-color: none");
                        } else {
                            if (item == Color.BLUE) {
                                setStyle("-fx-background-color: blue");
                            } else if (item == Color.RED) {
                                setStyle("-fx-background-color: red");
                            } else if (item == Color.GREEN) {
                                setStyle("-fx-background-color: green");
                            } else if (item == Color.GRAY) {
                                setStyle("-fx-background-color: gray");
                            }
                        }
                    }
                };
            }
        });

        droneId_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        droneType_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneType"));
        assemblerId_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));
        droneColor_goodDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneColor"));
        droneColor_goodDronesTable.setCellFactory(new Callback<TableColumn<Drone, Color>, TableCell<Drone, Color>>() {
            @Override
            public TableCell<Drone, Color> call(TableColumn<Drone, Color> param) {
                return new TableCell<Drone, Color>() {
                    @Override
                    public void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            if (!empty) {
                                this.setText(null);
                            }
                            setStyle("-fx-background-color: none");
                        } else {
                            if (item == Color.BLUE) {
                                setStyle("-fx-background-color: blue");
                            } else if (item == Color.RED) {
                                setStyle("-fx-background-color: red");
                            } else if (item == Color.GREEN) {
                                setStyle("-fx-background-color: green");
                            } else if (item == Color.GRAY) {
                                setStyle("-fx-background-color: gray");
                            }
                        }
                    }
                };
            }
        });

        droneId_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneId"));
        droneType_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneType"));
        assemblerId_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));
        droneColor_badDronesTable.setCellValueFactory(new PropertyValueFactory<>("droneColor"));
        droneColor_badDronesTable.setCellFactory(new Callback<TableColumn<Drone, Color>, TableCell<Drone, Color>>() {
            @Override
            public TableCell<Drone, Color> call(TableColumn<Drone, Color> param) {
                return new TableCell<Drone, Color>() {
                    @Override
                    public void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            if (!empty) {
                                this.setText(null);
                            }
                            setStyle("-fx-background-color: none");
                        } else {
                            if (item == Color.BLUE) {
                                setStyle("-fx-background-color: blue");
                            } else if (item == Color.RED) {
                                setStyle("-fx-background-color: red");
                            } else if (item == Color.GREEN) {
                                setStyle("-fx-background-color: green");
                            } else if (item == Color.GRAY) {
                                setStyle("-fx-background-color: gray");
                            }
                        }
                    }
                };
            }
        });

        moduleType_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("moduleType"));
        moduleStatus_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("status"));
        moduleCalibrationValue_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("calibrationValue"));
        moduleAssemblerId_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("assemblerId"));
        moduleCalibratorrId_moduleTableView.setCellValueFactory(new PropertyValueFactory<>("calibratorId"));

        orderId_ordersTable.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        dronesNumber_ordersTable.setCellValueFactory(new PropertyValueFactory<>("orderSize"));
        caseType_ordersTable.setCellValueFactory(new PropertyValueFactory<>("caseType"));
        caseColor_ordersTable.setCellValueFactory(new PropertyValueFactory<>("droneColor"));
        caseColor_ordersTable.setCellFactory(new Callback<TableColumn<Order, Color>, TableCell<Order, Color>>() {
            @Override
            public TableCell<Order, Color> call(TableColumn<Order, Color> param) {
                return new TableCell<Order, Color>() {
                    @Override
                    public void updateItem(Color item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item != null) {
                            setText(item.name());
                            if (item == Color.BLUE) {
                                setStyle("-fx-text-fill: blue; -fx-font-weight: bold;");
                            } else if (item == Color.RED) {
                                setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            } else if (item == Color.GREEN) {
                                setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            } else if (item == Color.GRAY) {
                                setStyle("-fx-text-fill: gray; -fx-font-weight: bold;");
                            }
                        }
                    }
                };
            }
        });



        status_ordersTable.setCellValueFactory(new PropertyValueFactory<>("status"));
        status_ordersTable.setCellFactory(new Callback<TableColumn<Order, String>, TableCell<Order, String>>() {
            @Override
            public TableCell<Order, String> call(TableColumn<Order, String> param) {
                return new TableCell<Order, String>() {
                    @Override
                    public void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if(item != null){
                            String created = item.split("/")[0];
                            String total = item.split("/")[1];
                            if(created.equals(total)){
                                setText(item + " READY");
                                setStyle("-fx-font-weight: bold");
                            } else  setText(item);
                        }
                    }
                };
            }
        });
    }

    public void setGui(GUI gui) {
        this.gui = gui;

        supplyTable.setItems(gui.getPartsData());
        dronesTable.setItems(gui.getDronesData());
        moduleTableView.setItems(gui.getModulesData());
        goodDronesTable.setItems(gui.getGoodDronesData());
        badDronesTable.setItems(gui.getBadDronesData());
        ordersTable.setItems(gui.getOrdersData());

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