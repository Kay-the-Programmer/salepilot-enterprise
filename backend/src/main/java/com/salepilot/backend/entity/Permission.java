package com.salepilot.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Permission entity for fine-grained access control.
 */
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name; // CREATE_USER, DELETE_ORDER, etc.

    @Column(name = "description")
    private String description;

    @Column(name = "resource", length = 50)
    private String resource; // user, product, order, etc.

    @Column(name = "action", length = 50)
    private String action; // create, read, update, delete

    @ManyToMany(mappedBy = "permissions")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Constructor for creating a permission with name only
     */
    public Permission(String name) {
        this.name = name;
    }
}
