package at.ac.tuwien.view;

/**
 * Created by Arber on 07.12.2015.
 */
import at.ac.tuwien.Main;
import at.ac.tuwien.entity.Part;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class SupplyOverviewController {

    @FXML
    private TableView<Part> supplyTable;
    @FXML
    private TableColumn<Part, String> id;
    @FXML
    private TableColumn<Part, String> partType;
    @FXML
    private TableColumn<Part, String> supplierId;


    public SupplyOverviewController() {}

    private Main main;


    @FXML
    private void initialize() {
        id.setCellValueFactory(cellData -> cellData.getValue().partIdProperty().asString());
        partType.setCellValueFactory(cellData -> cellData.getValue().partTypeProperty().asString());
        supplierId.setCellValueFactory(cellData -> cellData.getValue().supplierIdProperty().asString());
    }

    public void setMain(Main main) {
        this.main = main;
        supplyTable.setItems(main.getPartsData());
    }
}