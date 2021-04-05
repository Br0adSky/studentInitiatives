package org.pikIt.studentInit.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Bid bid;
    @ManyToOne
    private User user;

    @NotBlank(message = "Заполните комментарий")
    private String commentText;
    @Temporal(TemporalType.TIMESTAMP)
    private Date messageDate;
    public Comment() {
    }

    public Comment(Bid bid, User user) {
        this.bid = bid;
        this.user = user;
    }
}
