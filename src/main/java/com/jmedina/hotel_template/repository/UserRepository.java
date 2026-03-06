package com.jmedina.hotel_template.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jmedina.hotel_template.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{

    //Buscar usuario por email - Para Login
    Optional<User> findByEmail(String email);

    //Verficar si ya existe un email - Para el registro
    boolean existsByEmail(String email);

    //Buscar usuarios activos por rol - para panel de admin
    java.util.List<User> findByRoleAndActiveTrue(User.Role role);

}
