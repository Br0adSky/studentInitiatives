package org.pikIt.studentInit.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private Integer studentId;
    private String text;

    public Bid(Integer studentId, String text) {
        this.studentId = studentId;
        this.text = text;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Bid() {
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
