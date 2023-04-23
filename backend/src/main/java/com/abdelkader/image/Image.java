package com.abdelkader.image;

import com.abdelkader.user.User;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "images")
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "image", nullable = false)
    private String image_path;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDate updated_at;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDate created_at;

    @Column(name = "grid_width", nullable = false)
    private Integer grid_width;

    @Column(name = "grid_height", nullable = false)
    private Integer grid_height;

    @ManyToMany
    @JoinTable(
            name = "`authors`",
            joinColumns = @JoinColumn(name = "userid"),
            inverseJoinColumns = @JoinColumn(name = "imageid")
    )
    private Set<User> authors = new HashSet<>();

    public Image() {
    }

    public Image(Integer id,
                 String title,
                 String description,
                 String image_path,
                 LocalDate updated_at,
                 LocalDate created_at,
                 Integer grid_width,
                 Integer grid_height,
                 Set<User> authors) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.image_path = image_path;
        this.updated_at = updated_at;
        this.created_at = created_at;
        this.grid_width = grid_width;
        this.grid_height = grid_height;
        this.authors = authors;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public Integer getGrid_width() {
        return grid_width;
    }

    public void setGrid_width(Integer grid_width) {
        this.grid_width = grid_width;
    }

    public Integer getGrid_height() {
        return grid_height;
    }

    public void setGrid_height(Integer grid_height) {
        this.grid_height = grid_height;
    }

    public Set<User> getAuthors() {
        return authors;
    }

    public void setAuthors(Set<User> authors) {
        this.authors = authors;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", image_path='" + image_path + '\'' +
                ", updated_at=" + updated_at +
                ", created_at=" + created_at +
                ", grid_width=" + grid_width +
                ", grid_height=" + grid_height +
                ", authors=" + authors +
                '}';
    }
}
