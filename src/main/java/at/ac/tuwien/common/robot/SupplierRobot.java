package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class SupplierRobot extends AbstractRobot implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(SupplierRobot.class);
    private final static int INTERVAL = 1000;
    private PartType partType;
    private int amount;
    public UUID id;

    public SupplierRobot(IConnection connection, PartType partType, int amount) throws ConnectionException {
        super(connection);
        this.id = UUID.randomUUID();
        this.partType = partType;
        this.amount = amount;
    }

    @Override
    public void run() {
        for(int i = 0; i<amount; i++){
            try {
                logger.debug("Supplying part.");
                connection.supply(new Part(id, partType));
                Thread.sleep(INTERVAL);
            } catch (ConnectionException e) {
                logger.debug(e.getMessage());
            } catch (InterruptedException e) {
                logger.debug(e.getMessage());
            }
        }
    }

    public static void main(String[] args){
        if(args.length != 3){
            throw new IllegalArgumentException("Usage: SupplierRobot rmi|xvsm PART_TYPE AMOUNT");
        }

        IConnection connection = Utils.getConnection(args[0]);
        PartType partType = Utils.getPartType(args[1]);
        int amount = Integer.valueOf(args[2]);
        try {
            SupplierRobot supplierRobot = new SupplierRobot(connection, partType, amount);
            supplierRobot.run();
        } catch (ConnectionException e) {
            e.printStackTrace();
        }
    }

}
