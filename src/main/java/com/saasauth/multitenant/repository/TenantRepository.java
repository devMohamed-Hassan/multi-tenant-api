package com.saasauth.multitenant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saasauth.multitenant.model.Tenant;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
     Optional<Tenant> findByDomain(String domain);

     Optional<Tenant> findByName(String name);

     boolean existsByDomain(String domain);

     boolean existsByName(String name);
}