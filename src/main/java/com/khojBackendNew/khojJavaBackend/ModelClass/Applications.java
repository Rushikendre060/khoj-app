package com.khojBackendNew.khojJavaBackend.ModelClass;

import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Applications {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int job_id;
    private int user_id;
    private String status;
    private String application_date;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJob_id() {
        return job_id;
    }

    public void setJob_id(int job_id) {
        this.job_id = job_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApplication_date() {
        return application_date;
    }
    
    public void setApplication_date(String application_date) {
        this.application_date = application_date;
    }
}