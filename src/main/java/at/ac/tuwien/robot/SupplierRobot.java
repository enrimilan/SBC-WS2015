package at.ac.tuwien.robot;

import at.ac.tuwien.connection.Connection;
import at.ac.tuwien.connection.ConnectionException;
import at.ac.tuwien.entity.Part;
import at.ac.tuwien.entity.PartType;
import at.ac.tuwien.utils.Utils;

import java.util.UUID;

public class SupplierRobot implements Runnable {

    private final static int INTERVAL = 1000;
    private Connection connection;
    private PartType partType;
    private int amount;
    public UUID id;

    public SupplierRobot(PartType partType, int amount) throws ConnectionException {
        this.connection = new Connection();
        connection.establish();
        this.id = UUID.randomUUID();
        this.partType = partType;
        this.amount = amount;
    }

    @Override
    public void run() {
        for(int i = 0; i<amount; i++){
            try {
                connection.supply(new Part(id, partType));
                Thread.sleep(1000);
            } catch (ConnectionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        if(args.length != 2){
            throw new IllegalArgumentException("Usage: SupplierRobot PART_TYPE AMOUNT");
        }

        PartType partType = Utils.getPartType(args[0]);
        int amount = Integer.valueOf(args[1]);
        try {
            SupplierRobot supplierRobot = new SupplierRobot(partType, amount);
            supplierRobot.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

}
