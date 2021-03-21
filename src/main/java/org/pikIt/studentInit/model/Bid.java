package org.pikIt.studentInit.model;

import javax.persistence.*;

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


    public String getText() {
        return text;
    }
    public String getAuthorName(){
        return author.getName();
    }

    public void setText(String text) {
        this.text = text;
    }
    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }
}
