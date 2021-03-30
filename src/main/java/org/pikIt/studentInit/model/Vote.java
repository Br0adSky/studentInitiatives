package org.pikIt.studentInit.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    @OneToOne
    @JoinColumn(name = "bid_id")
    private Bid bid;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "voter_id")
    private User user;
    private Integer votesFor;
    private Integer votesAgainst;

    public Vote(Bid bid, User user) {
        this.bid = bid;
        this.user = user;
    }

    public Vote() {

    }
}
