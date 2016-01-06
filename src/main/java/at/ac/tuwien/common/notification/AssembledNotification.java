package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.*;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.UUID;

public class AssembledNotification extends UnicastRemoteObject implements IAssembledNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(AssembledNotification.class);
    private final static int INTERVAL = 1000;
    private UUID assemblyRobotId;
    private String host;
    private int port;

    public AssembledNotification(UUID assemblyRobotId, String host, int port) throws RemoteException {
        super();
        this.assemblyRobotId = assemblyRobotId;
        this.host = host;
        this.port = port;
    }

    @Override
    public void assembleMotorRotorPairs(ArrayList<Part> motors, ArrayList<Part> rotors, Job job) {
        logger.debug("Assembling motor rotor pairs.");
        Thread thread = new Thread(){
            @Override
            public void run() {
                IConnection connection = Utils.getConnectionInstance();
                connection.setHost(host);
                connection.setPort(port);
                for(int i = 0; i<motors.size(); i++){
                    Module module = new Module(ModuleType.MOTOR_ROTOR_PAIR, assemblyRobotId);
                    module.addPart(motors.get(i));
                    module.addPart(rotors.get(i));
                    try {
                        Thread.sleep(INTERVAL);
                        if(i==motors.size()-1){
                            //this the last element, the mark the job as done
                            job.setStatus(JobStatus.DONE);
                        }
                        connection.moduleAssembled(module, job);
                    } catch (InterruptedException e) {
                        logger.debug(e.getMessage());
                    } catch (ConnectionException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    connection.registerAssemblyRobot(AssembledNotification.this);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    @Override
    public void assembleCaseControlUnitPair(Part casePart, Part controlUnit, Job job) {
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
                    connection.setHost(host);
                    connection.setPort(port);
                    job.setStatus(JobStatus.DONE);
                    connection.moduleAssembled(module, job);
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
    public void assembleDrone(Module caseControlUnitPair, ArrayList<Module> motorRotorPairs, Job job) {
        logger.debug("Assembling drone.");
        Thread thread = new Thread(){
            @Override
            public void run() {
                Drone drone = new Drone(caseControlUnitPair, assemblyRobotId);
                drone.setMotorRotorPairs(motorRotorPairs);
                try {
                    Thread.sleep(3*INTERVAL);
                    IConnection connection = Utils.getConnectionInstance();
                    connection.setHost(host);
                    connection.setPort(port);
                    job.setStatus(JobStatus.DONE);
                    connection.droneAssembled(drone, job);
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
