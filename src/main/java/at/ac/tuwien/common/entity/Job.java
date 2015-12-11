package at.ac.tuwien.common.entity;

import java.io.Serializable;

public class Job implements Serializable {

    private int id;

    public Job(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
