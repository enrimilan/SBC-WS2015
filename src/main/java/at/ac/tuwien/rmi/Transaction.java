package at.ac.tuwien.rmi;

import at.ac.tuwien.common.entity.Drone;
import at.ac.tuwien.common.entity.Module;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.entity.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Transaction implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(Transaction.class);
    private ArrayList<Part> parts;
    private ArrayList<Module> modules;
    private ArrayList<Drone> drones;
    private AtomicReference<TransactionStatus> status;
    private IRMIServer server;
    private int timeToLive;

    public Transaction(IRMIServer server, int timeToLive){
        this.parts = new ArrayList<>();
        this.modules = new ArrayList<>();
        this.drones = new ArrayList<>();
        this.timeToLive = timeToLive;
        this.status = new AtomicReference<>();
        this.server = server;
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
        logger.debug("Committing transaction");
        status.set(TransactionStatus.COMMITTED);
    }

    public void rollback() {
        logger.debug("Rolling back transaction");
        status.set(TransactionStatus.ABORTED);

        try{
            logger.debug("Returning back " + parts.size() + " parts...");
            for(Part p : parts){
                server.supply(p);
            }
            logger.debug("Returning back " + modules.size() + " modules...");
            for(Module m : modules){
                if(m.getStatus() == Status.ASSEMBLED){
                    server.moduleAssembled(m, null);
                }
                if(m.getStatus() == Status.CALIBRATED){
                    server.motorRotorPairCalibrated(m, null);
                }
            }
            logger.debug("Returning back " + drones.size() + " drones...");
            for(Drone d : drones){
                if(d.getStatus() == Status.ASSEMBLED){
                    server.droneAssembled(d, null);
                }
                if(d.getStatus() == Status.CALIBRATED){
                    server.droneCalibrated(d, null);
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }

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
