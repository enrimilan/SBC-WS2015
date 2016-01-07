package at.ac.tuwien.common.robot;

import at.ac.tuwien.common.connection.ConnectionException;
import at.ac.tuwien.common.connection.IConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public abstract class AbstractRobot {

    protected IConnection connection;
    protected UUID id;
    private BufferedReader in;

    public AbstractRobot(IConnection connection) throws ConnectionException {
        this.connection = connection;
        this.id = UUID.randomUUID();
    }

    public void startRobot(){
        this.in = new BufferedReader(new InputStreamReader(System.in));
        String s;
        System.out.println("Type q + ENTER to stop the robot");
        try {
            while ((s = in.readLine()) != null) {
                if(s.equals("q")){
                    break;
                }
            }
            stopRobot();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRobot(){
        try {
            connection.end();
            if(in!=null)
                in.close();
        } catch (ConnectionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("stopped.");
    }
}
