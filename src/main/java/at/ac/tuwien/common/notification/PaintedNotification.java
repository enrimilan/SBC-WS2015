package at.ac.tuwien.common.notification;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;
import at.ac.tuwien.common.entity.Color;
import at.ac.tuwien.common.entity.Job;
import at.ac.tuwien.common.entity.JobStatus;
import at.ac.tuwien.common.entity.Part;
import at.ac.tuwien.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class PaintedNotification extends UnicastRemoteObject implements IPaintedNotification, Serializable {

    private final static Logger logger = LoggerFactory.getLogger(PaintedNotification.class);
    private final static int INTERVAL = 1000;
    private UUID paintingRobotId;
    private String host;
    private int port;

    public PaintedNotification(UUID paintingRobotId, String host, int port) throws RemoteException {
        super();
        this.paintingRobotId = paintingRobotId;
        this.host = host;
        this.port = port;
    }

    @Override
    public void paintPart(Part part, Color color, Job job, UUID orderId) throws RemoteException {
        logger.debug("painting part");
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    if(color == Color.GRAY){
                        part.setColor(Utils.generateRandomColor());
                    }
                    else{
                        part.setColor(color);
                    }
                    part.setPainterId(paintingRobotId);
                    part.setOrderId(orderId);
                    Thread.sleep(INTERVAL);
                    IConnection connection = Utils.getConnectionInstance();
                    connection.setHost(host);
                    connection.setPort(port);
                    job.setStatus(JobStatus.DONE);
                    connection.partPainted(part, job);
                    connection.registerPaintingRobot(PaintedNotification.this);
                } catch (ConnectionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
}
