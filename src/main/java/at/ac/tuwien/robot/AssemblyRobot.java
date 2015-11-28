package at.ac.tuwien.robot;

import at.ac.tuwien.connection.Connection;
import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.entity.*;

import java.util.ArrayList;
import java.util.UUID;

public class AssemblyRobot implements Runnable, IAssemblyRobotNotification {

    private final static int INTERVAL = 1000;
    private Connection connection;
    private UUID id;
    private AssemblyRobot assemblyRobot;

    public AssemblyRobot() throws ConnectionException {
        this.connection = new Connection();
        connection.establish();
        this.id = UUID.randomUUID();
        this.assemblyRobot = this;
    }

    @Override
    public void run() {
        try {
            connection.registerAssemblyRobot(this);

        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void assembleMotorRotorPairs(final ArrayList<Part> motors, final ArrayList<Part> rotors) {
        Thread thread = new Thread(){
            @Override
            public void run() {

                for(int i = 0; i<motors.size(); i++){
                    Module module = new Module(ModuleType.MOTOR_ROTOR_PAIR, id);
                    module.addPart(motors.get(i));
                    module.addPart(rotors.get(i));
                    try {
                        Thread.sleep(INTERVAL);
                        connection.moduleAssembled(module);
                        connection.registerAssemblyRobot(assemblyRobot);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ConnectionException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.start();
    }

    @Override
    public void assembleCaseControlUnitPair(final Part casePart, final Part controlUnit) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                Module module = new Module(ModuleType.CASE_CONTROL_UNIT_PAIR, id);
                module.addPart(casePart);
                module.addPart(controlUnit);
                try {
                    Thread.sleep(INTERVAL);
                    connection.moduleAssembled(module);
                    connection.registerAssemblyRobot(assemblyRobot);
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
    public void assembleDrone(final Module caseControlUnitPair, final ArrayList<Module> motorRotorPairs) {
        Thread thread = new Thread(){
            @Override
            public void run() {
                Drone drone = new Drone(caseControlUnitPair, id);
                drone.setMotorRotorPairs(motorRotorPairs);
                try {
                    Thread.sleep(3*INTERVAL);
                    connection.droneAssembled(drone);
                    connection.registerAssemblyRobot(assemblyRobot);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectionException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public static void main(String[] args){
        if(args.length != 0){
            throw new IllegalArgumentException("Usage: AssemblyRobot");
        }

        try {
            AssemblyRobot assemblyRobot = new AssemblyRobot();
            assemblyRobot.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }
}
