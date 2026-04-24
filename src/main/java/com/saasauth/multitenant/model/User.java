package com.saasauth.multitenant.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

     @Column(nullable = false)
     private String name;

     @Column(unique = true, nullable = false)
     private String email;

     @Column(nullable = false)
     private String password;

     @Enumerated(EnumType.STRING)
     private Role role;

     @ManyToOne
     @JoinColumn(name = "tenant_id", nullable = false)
     private Tenant tenant;
}