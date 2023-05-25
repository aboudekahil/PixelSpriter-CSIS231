package com.abdelkader.image;

import com.abdelkader.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ResourceLoader resourceLoader;
    @Autowired
    public ImageService(ImageRepository imageRepository, UserRepository userRepository, ResourceLoader resourceLoader) {
        this.imageRepository = imageRepository;
        this.resourceLoader = resourceLoader;
        this.userRepository = userRepository;
    }

    public String saveImage(MultipartFile file) throws IOException {
        // Define the directory where images will be stored
        String uploadDir = "classpath:/static/images/";

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // Get the resource for the upload directory
        Resource resource = resourceLoader.getResource(uploadDir);

        // Get the absolute path of the upload directory
        String absoluteUploadDir = ResourceUtils.getFile(resource.getURL()).getAbsolutePath();

        // Create the directory if it doesn't exist
        File directory = new File(absoluteUploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the image file to the upload directory
        Path filePath = Path.of(absoluteUploadDir, fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Return the image file path
        return filePath.toAbsolutePath().toString();
    }

    public void saveImage(Image image) {
        imageRepository.save(image);
    }

    public Optional<Image> findByTitleAndAuthor(String title, Integer authorId) {
        return imageRepository.findByTitleAndAuthor(title, userRepository.findById(authorId).orElseThrow());
    }

    public boolean deleteImage(String imagePath) {
        File imageFile = new File(imagePath);
        if (imageFile.exists()) {
            return imageFile.delete();
        }
        return false;
    }

    public Optional<List<Image>> findByAuthor(Integer authorId){
        try{
            return Optional.of(
                    imageRepository.findAllByAuthor(
                            userRepository.findById(authorId).orElseThrow()
                    )
            );
        }catch (NoSuchElementException e){
            return Optional.empty();
        }
    }

    public boolean deleteImageFromId(Integer id) {
        Optional<Image> foundImage = imageRepository.findById(id);

        if(foundImage.isPresent()){
            imageRepository.deleteById(id);
            deleteImage(foundImage.get().getImage_path());
            return true;
        }
        return false;
    }

    public Optional<Image> getImageById(Integer id) {
        return imageRepository.findById(id);
    }
}
