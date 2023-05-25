package com.abdelkader.image;

import com.abdelkader.user.User;
import com.abdelkader.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;
    private final UserService userService;

    @Autowired
    public ImageController(ImageService imageService, UserService userService) {
        this.imageService = imageService;
        this.userService = userService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file,
                                              @RequestParam("title") String title,
                                              @RequestParam("description") String description,
                                              @RequestParam("gridWidth") Integer gridWidth,
                                              @RequestParam("gridHeight") Integer gridHeight,
                                              @RequestParam("authorId") Integer authorId) {
        try {
            Optional<Image> foundImage = imageService.findByTitleAndAuthor(title, authorId);

            if (foundImage.isEmpty()) {
                String imagePath = imageService.saveImage(file);
                Image image = new Image();
                image.setTitle(title);
                image.setDescription(description);
                image.setImage_path(imagePath);
                image.setUpdated_at(LocalDate.now());
                image.setCreated_at(LocalDate.now());
                image.setGrid_width(gridWidth);
                image.setGrid_height(gridHeight);
                User author = userService.getUserById(authorId);
                image.setAuthor(author);
                // Save the image entity in the database
                imageService.saveImage(image);
            } else {
                Image existingImage = foundImage.get();
                String existingImagePath = existingImage.getImage_path();

                // Delete existing image file
                boolean deleteSuccess = imageService.deleteImage(existingImagePath);

                if (deleteSuccess) {
                    // Save new image file
                    String imagePath = imageService.saveImage(file);
                    existingImage.setImage_path(imagePath);
                    existingImage.setDescription(description);
                    existingImage.setUpdated_at(LocalDate.now());
                    existingImage.setGrid_width(gridWidth);
                    existingImage.setGrid_height(gridHeight);
                    User author = userService.getUserById(authorId);
                    existingImage.setAuthor(author);
                    // Update the image entity in the database
                    imageService.saveImage(existingImage);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to overwrite the existing image.");
                }
            }

            return ResponseEntity.ok("Image uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload the image.");
        }
    }

    @GetMapping()
    public ResponseEntity<List<Image>> getImagesByUserId(@RequestParam("userid") Integer userId){
        Optional<List<Image>> foundImages = imageService.findByAuthor(userId);

        if(foundImages.isPresent())
            return ResponseEntity.ok(foundImages.get());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);

    }

    @DeleteMapping()
    public ResponseEntity<?> deleteImageFromId(@RequestParam("id") Integer id){
        if(imageService.deleteImageFromId(id)){
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/rename")
    public ResponseEntity<Image> renameImageFromId(@RequestParam("id") Integer id,
                                                   @RequestParam("title") String title,
                                                   @RequestParam("description") String description){
        Optional<Image> optionalImage = imageService.getImageById(id);

        if (optionalImage.isPresent()) {
            Image image = optionalImage.get();
            image.setTitle(title);
            image.setDescription(description);
            image.setUpdated_at(LocalDate.now());

            // Update the image entity in the database
            imageService.saveImage(image);

            return ResponseEntity.ok(image);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
