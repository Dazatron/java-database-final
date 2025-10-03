package com.project.code.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.code.Model.Store;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query("SELECT s FROM Store s WHERE lower(s.name) LIKE CONCAT('%', :pname, '%')")
    List<Store> findBySubName(@Param("pname") String pname);

}
