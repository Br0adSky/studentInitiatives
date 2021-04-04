package org.pikIt.studentInit.repositorys;

import org.pikIt.studentInit.model.Bid;
import org.pikIt.studentInit.model.BidStatus;
import org.pikIt.studentInit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    List<Bid> findBidByAuthor(User author);

    List<Bid> findBidByTextContaining(String text);

    List<Bid> findByStatus(BidStatus status);

    @Query("select b from Bid b where b.author.name like %:name% and b.author.surname like %:surname% ")
    List<Bid> findByNameAndSurnameContains(String name, String surname);
}
