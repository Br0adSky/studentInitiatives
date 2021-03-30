package org.pikIt.studentInit.services;


import org.pikIt.studentInit.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
    @Query("select u from User u WHERE u.name like %:name% and u.surname like %:surname% ")
    List<User> findByNameAndSurnameContains(String name, String surname);
}
