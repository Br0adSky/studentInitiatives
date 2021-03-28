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
    private boolean confirmed;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User author;

    public Bid(String text, User author, boolean confirmed) {
        this.text = text;
        this.author = author;
        this.confirmed = confirmed;
    }


    public Bid() {
    }

    public String getAuthorName() {
        return author.getName();
    }

}
