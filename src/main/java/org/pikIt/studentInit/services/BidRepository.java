package org.pikIt.studentInit.services;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    List<Bid> findBidByAuthor(User author);
    List<Bid> findBidByTextContaining(String text);
    @Query("select b from Bid b WHERE b.author.name like %:name% and b.author.surname like %:surname% ")
    List<Bid> findByNameAndSurnameContains(String name, String surname);
}
