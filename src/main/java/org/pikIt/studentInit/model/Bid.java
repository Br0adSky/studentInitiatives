package org.pikIt.studentInit.model;


import javax.validation.Valid;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Bid {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    @NotNull
    @Valid
    private String text;
    @Valid
    private Integer priseFrom;
    @Valid
    private Integer priseTo;
    private String address;
    private String fileName;

    @CollectionTable(name = "bid_status", joinColumns = @JoinColumn(name = "bid_id"))
    @Enumerated(EnumType.STRING)
    private BidStatus status;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User author;

    public Bid(String text, User author) {
        this.text = text;
        this.author = author;
    }


    public Bid() {
    }

    public String getAuthorName() {
        return author.getName();
    }

}
