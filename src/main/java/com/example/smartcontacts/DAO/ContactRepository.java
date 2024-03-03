package com.example.smartcontacts.DAO;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.smartcontacts.Entities.Contacts;

public interface ContactRepository extends JpaRepository<Contacts, Integer>{

    // Pagination

    @Query("from Contacts as c where c.user.userId = :userId")
    public Page<Contacts> findContactByUser(@Param("userId") Integer userId, Pageable pageable);
    
}
