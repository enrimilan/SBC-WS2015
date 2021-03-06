package at.ac.tuwien.common.view;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.common.entity.Status;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Created by Arber on 08.12.2015.
 */
public class DroneDetailsDialogController {

    @FXML
    private Label label_droneId;

    @FXML
    private Label label_droneStatus;

    @FXML
    private Label label_orderID;

    @FXML
    private Label label_droneCaseId;

    @FXML
    private Label label_droneCaseSupplierId;

    @FXML
    private Label label_droneCasePainterId;

    @FXML
    private Label label_caseColor;

    @FXML
    private Label label_droneCUId;

    @FXML
    private Label label_droneCUSupplierId;

    @FXML
    private Label label_droneModuleCCUassemblerId;

    @FXML
    private Label label_droneModuleCCUcalibratorId;

    @FXML
    private Label label_droneModuleCCUcalibrationValue;


    /****** MotorRotor 1 ****/
    @FXML
    private Label lbl_CP1_MotorId;
    @FXML
    private Label lbl_CP1_MotorSupplierId;

    @FXML
    private Label lbl_CP1_RotorId;
    @FXML
    private Label lbl_CP1_RotorSupplierId;

    @FXML
    private Label lbl_CP1_AssemblerId;
    @FXML
    private Label lbl_CP1_CalibratorId;
    @FXML
    private Label lbl_CP1_CalibrationVlue;

    /****** MotorRotor 2 ****/
    @FXML
    private Label lbl_CP2_MotorId;
    @FXML
    private Label lbl_CP2_MotorSupplierId;

    @FXML
    private Label lbl_CP2_RotorId;
    @FXML
    private Label lbl_CP2_RotorSupplierId;

    @FXML
    private Label lbl_CP2_AssemblerId;
    @FXML
    private Label lbl_CP2_CalibratorId;
    @FXML
    private Label lbl_CP2_CalibrationVlue;


    /****** MotorRotor 2 ****/
    @FXML
    private Label lbl_CP3_MotorId;
    @FXML
    private Label lbl_CP3_MotorSupplierId;

    @FXML
    private Label lbl_CP3_RotorId;
    @FXML
    private Label lbl_CP3_RotorSupplierId;

    @FXML
    private Label lbl_CP3_AssemblerId;
    @FXML
    private Label lbl_CP3_CalibratorId;
    @FXML
    private Label lbl_CP3_CalibrationVlue;



    private Stage dialogStage;
    private Drone drone;
    private boolean okClicked = false;

    @FXML
    private void initialize() {
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setDrone(Drone drone) {
        this.drone = drone;

        label_droneId.setText(drone.getDroneId().toString());
        label_droneStatus.setText(drone.getStatus().name());

        if(drone.getOrderId() != null){
            label_orderID.setText(drone.getOrderId().toString());
        } else {
            label_orderID.setText("N/A");
        }

       if(drone.getCaseControlUnitPair().getParts().get(0).getPartType() == PartType.CASE){
           label_droneCaseId.setText(drone.getCaseControlUnitPair().getParts().get(0).getPartId().toString());
           label_droneCaseSupplierId.setText(drone.getCaseControlUnitPair().getParts().get(0).getSupplierId().toString());

           if(drone.getCaseControlUnitPair().getParts().get(0).getPainterId() != null){
               label_droneCasePainterId.setText(drone.getCaseControlUnitPair().getParts().get(0).getPainterId().toString());
           } else {
               label_droneCasePainterId.setText("N/A");
           }
           String color = drone.getCaseControlUnitPair().getParts().get(0).getColor().name();
           if (color.equals("BLUE")){
               label_caseColor.setTextFill(Color.BLUE);
           } else if (color.equals("RED")){
               label_caseColor.setTextFill(Color.RED);
           } else if (color.equals("GREEN")){
               label_caseColor.setTextFill(Color.GREEN);
           } else color = "N/A";
           label_caseColor.setText(color);
           label_droneCUId.setText(drone.getCaseControlUnitPair().getParts().get(1).getPartId().toString());
           label_droneCUSupplierId.setText(drone.getCaseControlUnitPair().getParts().get(1).getSupplierId().toString());
       } else {
           label_droneCaseId.setText(drone.getCaseControlUnitPair().getParts().get(1).getPartId().toString());
           label_droneCaseSupplierId.setText(drone.getCaseControlUnitPair().getParts().get(1).getSupplierId().toString());
           label_droneCUId.setText(drone.getCaseControlUnitPair().getParts().get(0).getPartId().toString());
           label_droneCUSupplierId.setText(drone.getCaseControlUnitPair().getParts().get(0).getSupplierId().toString());
       }

        if(drone.getStatus() == Status.ASSEMBLED){

            label_droneModuleCCUassemblerId.setText(drone.getCaseControlUnitPair().getAssemblerId().toString());
            label_droneModuleCCUcalibratorId.setText("N/A");
            label_droneModuleCCUcalibrationValue.setText("N/A");


            /*** CP1 ***/

            lbl_CP1_MotorId.setText(drone.getMotorRotorPairs().get(0).getParts().get(0).getPartId().toString());
            lbl_CP1_MotorSupplierId.setText(drone.getMotorRotorPairs().get(0).getParts().get(0).getSupplierId().toString());

            lbl_CP1_RotorId.setText(drone.getMotorRotorPairs().get(0).getParts().get(1).getPartId().toString());
            lbl_CP1_RotorSupplierId.setText(drone.getMotorRotorPairs().get(0).getParts().get(1).getSupplierId().toString());

            lbl_CP1_AssemblerId.setText(drone.getMotorRotorPairs().get(0).getAssemblerId().toString());
            lbl_CP1_CalibratorId.setText("N/A");
            lbl_CP1_CalibrationVlue.setText("N/A");

            /*** CP2 ***/

            lbl_CP2_MotorId.setText(drone.getMotorRotorPairs().get(1).getParts().get(0).getPartId().toString());
            lbl_CP2_MotorSupplierId.setText(drone.getMotorRotorPairs().get(1).getParts().get(0).getSupplierId().toString());

            lbl_CP2_RotorId.setText(drone.getMotorRotorPairs().get(1).getParts().get(1).getPartId().toString());
            lbl_CP2_RotorSupplierId.setText(drone.getMotorRotorPairs().get(1).getParts().get(1).getSupplierId().toString());

            lbl_CP2_AssemblerId.setText(drone.getMotorRotorPairs().get(1).getAssemblerId().toString());
            lbl_CP2_CalibratorId.setText("N/A");
            lbl_CP2_CalibrationVlue.setText("N/A");


            /*** CP3 ***/

            lbl_CP3_MotorId.setText(drone.getMotorRotorPairs().get(2).getParts().get(0).getPartId().toString());
            lbl_CP3_MotorSupplierId.setText(drone.getMotorRotorPairs().get(2).getParts().get(0).getSupplierId().toString());

            lbl_CP3_RotorId.setText(drone.getMotorRotorPairs().get(2).getParts().get(1).getPartId().toString());
            lbl_CP3_RotorSupplierId.setText(drone.getMotorRotorPairs().get(2).getParts().get(1).getSupplierId().toString());

            lbl_CP3_AssemblerId.setText(drone.getMotorRotorPairs().get(2).getAssemblerId().toString());
            lbl_CP3_CalibratorId.setText("N/A");
            lbl_CP3_CalibrationVlue.setText("N/A");

        } else {

            label_droneModuleCCUassemblerId.setText(drone.getCaseControlUnitPair().getAssemblerId().toString());
            label_droneModuleCCUcalibratorId.setText(drone.getCaseControlUnitPair().getCalibratorId().toString());
            label_droneModuleCCUcalibrationValue.setText(String.valueOf(drone.getCaseControlUnitPair().getCalibrationValue()));


            /*** CP1 ***/

            lbl_CP1_MotorId.setText(drone.getMotorRotorPairs().get(0).getParts().get(0).getPartId().toString());
            lbl_CP1_MotorSupplierId.setText(drone.getMotorRotorPairs().get(0).getParts().get(0).getSupplierId().toString());

            lbl_CP1_RotorId.setText(drone.getMotorRotorPairs().get(0).getParts().get(1).getPartId().toString());
            lbl_CP1_RotorSupplierId.setText(drone.getMotorRotorPairs().get(0).getParts().get(1).getSupplierId().toString());

            lbl_CP1_AssemblerId.setText(drone.getMotorRotorPairs().get(0).getAssemblerId().toString());
            lbl_CP1_CalibratorId.setText(drone.getMotorRotorPairs().get(0).getCalibratorId().toString());
            lbl_CP1_CalibrationVlue.setText(String.valueOf(drone.getMotorRotorPairs().get(0).getCalibrationValue()));

            /*** CP2 ***/

            lbl_CP2_MotorId.setText(drone.getMotorRotorPairs().get(1).getParts().get(0).getPartId().toString());
            lbl_CP2_MotorSupplierId.setText(drone.getMotorRotorPairs().get(1).getParts().get(0).getSupplierId().toString());

            lbl_CP2_RotorId.setText(drone.getMotorRotorPairs().get(1).getParts().get(1).getPartId().toString());
            lbl_CP2_RotorSupplierId.setText(drone.getMotorRotorPairs().get(1).getParts().get(1).getSupplierId().toString());

            lbl_CP2_AssemblerId.setText(drone.getMotorRotorPairs().get(1).getAssemblerId().toString());
            lbl_CP2_CalibratorId.setText(drone.getMotorRotorPairs().get(1).getCalibratorId().toString());
            lbl_CP2_CalibrationVlue.setText(String.valueOf(drone.getMotorRotorPairs().get(1).getCalibrationValue()));


            /*** CP3 ***/

            lbl_CP3_MotorId.setText(drone.getMotorRotorPairs().get(2).getParts().get(0).getPartId().toString());
            lbl_CP3_MotorSupplierId.setText(drone.getMotorRotorPairs().get(2).getParts().get(0).getSupplierId().toString());

            lbl_CP3_RotorId.setText(drone.getMotorRotorPairs().get(2).getParts().get(1).getPartId().toString());
            lbl_CP3_RotorSupplierId.setText(drone.getMotorRotorPairs().get(2).getParts().get(1).getSupplierId().toString());

            lbl_CP3_AssemblerId.setText(drone.getMotorRotorPairs().get(2).getAssemblerId().toString());
            lbl_CP3_CalibratorId.setText(drone.getMotorRotorPairs().get(2).getCalibratorId().toString());
            lbl_CP3_CalibrationVlue.setText(String.valueOf(drone.getMotorRotorPairs().get(2).getCalibrationValue()));
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }




}
