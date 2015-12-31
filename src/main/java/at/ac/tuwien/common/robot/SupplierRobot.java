package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.common.entity.PartType;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class SupplierRobot extends AbstractRobot implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(SupplierRobot.class);
    private final static int INTERVAL = 1000;
    private PartType partType;
    private int amount;
    private ArrayList<String> addresses;

    public SupplierRobot(IConnection connection, PartType partType, int amount, ArrayList<String> addresses) throws ConnectionException {
        super(connection);
        this.partType = partType;
        this.amount = amount;
        this.addresses = addresses;
    }

    @Override
    public void run() {
        try {
            for(int i = 0; i<amount; i++){
                String address = getFactoryAddressWithMinimalAmount();
                String data[] = address.split(":");
                connection.establish(data[0], Integer.valueOf(data[1]));
                logger.debug("Supplying part.");
                connection.supply(new Part(id, partType));
                Thread.sleep(INTERVAL);
            }
            stopRobot();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        } catch (InterruptedException e) {
            logger.debug(e.getMessage());
        }
    }

    private String getFactoryAddressWithMinimalAmount(){
        String address = "";
        int minAmount = Integer.MAX_VALUE;

        for(String s : addresses){
            String data[] = s.split(":");
            try {
                connection.establish(data[0], Integer.valueOf(data[1]));
                int amount = connection.getAmount(partType);
                if(amount<minAmount){
                    minAmount = amount;
                    address = s;
                }
            } catch (ConnectionException e) {
                logger.error(e.getMessage());
            }
        }

        return address;
    }

    public static void main(String[] args){
        if(args.length < 4){
            throw new IllegalArgumentException("Usage: SupplierRobot PART_TYPE AMOUNT rmi|xvsm SERVER_HOST:SERVER_PORT...");
        }
        PartType partType = Utils.getPartType(args[0]);
        int amount = Integer.valueOf(args[1]);
        IConnection connection = Utils.getConnectionInstance(args[2]);
        ArrayList<String> addresses = new ArrayList<>();
        for(int i = 3; i<args.length; i++){
            addresses.add(args[i]);
        }
        try {
            SupplierRobot supplierRobot = new SupplierRobot(connection, partType, amount, addresses);
            supplierRobot.run();
        } catch (ConnectionException e) {
            logger.debug(e.getMessage());
        }
    }

}
