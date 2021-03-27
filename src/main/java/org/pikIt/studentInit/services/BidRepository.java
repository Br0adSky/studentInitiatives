package org.pikIt.studentInit.services;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    List<Bid> findBidByAuthor(User author);
    List<Bid> findBidByTextContaining(String text);
    List<Bid> findBidByAuthor_NameContaining(String authorName);
}
