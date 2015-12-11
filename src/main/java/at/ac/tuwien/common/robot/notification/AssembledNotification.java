package at.ac.tuwien.common.robot.notification;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.ModuleType;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

public class AssembledNotification implements IAssembledNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(AssembledNotification.class);
    private final static int INTERVAL = 1000;
    private UUID assemblyRobotId;

    public AssembledNotification(UUID assemblyRobotId) {
        this.assemblyRobotId = assemblyRobotId;
    }

    @Override
    public void assembleMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors) {
        logger.debug("Assembling motor rotor pairs.");
        Thread thread = new Thread(){
            @Override
            public void run() {

                for(int i = 0; i<motors.size(); i++){
                    Module module = new Module(ModuleType.MOTOR_ROTOR_PAIR, assemblyRobotId);
                    module.addPart(motors.get(i));
                    module.addPart(rotors.get(i));
                    try {
                        Thread.sleep(INTERVAL);
                        IConnection connection = Utils.getConnectionInstance();
                        connection.moduleAssembled(module);
                        connection.registerAssemblyRobot(AssembledNotification.this);
                    } catch (InterruptedException e) {
                        logger.debug(e.getMessage());
                    } catch (ConnectionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void assembleCaseControlUnitPair(Part casePart, Part controlUnit) {
        logger.debug("Assembling case control unit pairs.");
        Thread thread = new Thread(){
            @Override
            public void run() {
                Module module = new Module(ModuleType.CASE_CONTROL_UNIT_PAIR, assemblyRobotId);
                module.addPart(casePart);
                module.addPart(controlUnit);
                try {
                    Thread.sleep(INTERVAL);
                    IConnection connection = Utils.getConnectionInstance();
                    connection.moduleAssembled(module);
                    connection.registerAssemblyRobot(AssembledNotification.this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void assembleDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs) {
        logger.debug("Assembling drone.");
        Thread thread = new Thread(){
            @Override
            public void run() {
                Drone drone = new Drone(caseControlUnitPair, assemblyRobotId);
                drone.setMotorRotorPairs(motorRotorPairs);
                try {
                    Thread.sleep(3*INTERVAL);
                    IConnection connection = Utils.getConnectionInstance();
                    connection.droneAssembled(drone);
                    connection.registerAssemblyRobot(AssembledNotification.this);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
