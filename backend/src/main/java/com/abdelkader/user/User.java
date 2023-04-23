package com.abdelkader.user;

import com.abdelkader.country.Country;
import com.abdelkader.image.Image;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "users")
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "email_UNIQUE", columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userID", updatable = false)
    private Integer id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "`password`", nullable = false)
    private String password;

    @Column(name = "email", nullable = false)
    private String email;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDate created_at;
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDate updated_at;

    @ManyToOne(optional = false)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;

    @ManyToMany(mappedBy = "authors")
    private Set<Image> images = new HashSet<>();


    public User() {
    }

    public User(
            Integer id,
            String username,
            String password,
            String email,
            LocalDate created_at,
            LocalDate updated_at,
            Country country,
            Set<Image> images) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.country = country;
        this.images = images;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", created_at=" + created_at +
                ", updated_at=" + updated_at +
                ", country=" + country +
                ", images=" + images +
                '}';
    }
}
