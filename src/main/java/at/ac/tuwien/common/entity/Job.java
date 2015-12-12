package at.ac.tuwien.common.entity;

import java.io.Serializable;

public class Job implements Serializable {

    private int id;
    private JobStatus status;

    public Job(int id) {
        this.id = id;
        this.status = JobStatus.WORKING;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        return id == job.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
