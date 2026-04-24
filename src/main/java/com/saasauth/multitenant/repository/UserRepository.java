package com.saasauth.multitenant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasauth.multitenant.model.Tenant;
import com.saasauth.multitenant.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
     Optional<User> findByEmail(String email);

     List<User> findAllByTenant(Tenant tenant);

     boolean existsByEmail(String email);

     Optional<User> findByEmailAndTenant(String email, Tenant tenant);
}