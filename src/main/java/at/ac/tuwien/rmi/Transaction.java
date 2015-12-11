package at.ac.tuwien.rmi;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction implements Runnable {

    private ArrayList<Part> parts;
    private ArrayList<Module> modules;
    private ArrayList<Drone> drones;
    private AtomicReference<TransactionStatus> status;
    private int timeToLive;

    public void Transaction(int timeToLive){
        this.parts = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.drones = new ArrayList<>();
        this.timeToLive = timeToLive;
        this.status = new AtomicReference<>();
    }


    public void addPart(Part part){
        parts.add(part);
    }

    public void addModule(Module module){
        modules.add(module);
    }

    public void addDrone(Drone drone){
        drones.add(drone);
    }

    public void commit(){
        status.set(TransactionStatus.COMMITTED);
    }

    public void rollback(){
        status.set(TransactionStatus.ABORTED);
        //TODO re-add things back to the respective lists
    }

    @Override
    public void run() {
        status.set(TransactionStatus.RUNNING);
        try {
            //wait some seconds until the robot has done its job
            Thread.sleep(timeToLive);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //waited too long but the robot still hasn't finished work -> rollback the transaction
        if(status.get() == TransactionStatus.RUNNING){
            rollback();
        }
    }

    public TransactionStatus getStatus(){
        return status.get();
    }
}
