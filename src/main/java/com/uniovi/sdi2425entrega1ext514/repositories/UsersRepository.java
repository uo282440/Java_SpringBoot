package com.uniovi.sdi2425entrega1ext514.repositories;

import com.uniovi.sdi2425entrega1ext514.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;

public interface UsersRepository extends CrudRepository<User, Long> {

    User findByDni(String dni);

    Page<User> findAll(Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.dni = :dni")
    void updatePasswordByDni(@Param("dni") String dni, @Param("password") String password);
}
