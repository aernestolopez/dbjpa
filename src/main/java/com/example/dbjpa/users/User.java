package com.example.dbjpa.users;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="users")
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    private Integer age;


/*    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }*/
}
