package org.pikIt.studentInit.model;

import lombok.Data;

import javax.persistence.*;
@Data
@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;

    private String text;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User author;

    public Bid(String text, User author) {
        this.text = text;
        this.author = author;
    }


    public Bid() {
    }

    public String getAuthorName(){
        return author.getName();
    }

}
