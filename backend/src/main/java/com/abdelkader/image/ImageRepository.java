package com.abdelkader.image;

import com.abdelkader.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Integer> {
    Optional<Image> findByTitleAndAuthor(String title, User author);

    List<Image> findAllByAuthor(User author);
}
