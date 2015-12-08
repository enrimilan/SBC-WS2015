package at.ac.tuwien.view;

/**
 * Created by Arber on 07.12.2015.
 */
import at.ac.tuwien.Main;
import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.entity.Part;
import at.ac.tuwien.entity.PartG;
import at.ac.tuwien.entity.PartType;
import at.ac.tuwien.robot.SupplierRobot;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class SupplyOverviewController {

    public SupplyOverviewController() {}
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

    @FXML
    private void initialize() {
        supplyComboBox.setItems(supplyOptions);
        id.setCellValueFactory(new PropertyValueFactory<>("partId"));
        partType.setCellValueFactory(new PropertyValueFactory<>("partType"));
        supplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
    }

    public void setMain(Main main) {
        this.main = main;
        supplyTable.setItems(main.getPartsData());
    }
}