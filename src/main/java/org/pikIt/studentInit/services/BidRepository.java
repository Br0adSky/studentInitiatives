package org.pikIt.studentInit.services;

import org.pikIt.studentInit.model.Bid;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends CrudRepository<Bid, Integer>{
    List<Bid> findByStudentId(Integer studentId);

}
