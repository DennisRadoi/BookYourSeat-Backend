package services;

import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repositories.UserRepository;
import entities.User;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProfilePhotoService {

    private final UserRepository userRepository;
    private final SseService sseService;

    @Value("${app.upload.profile-photos-dir:uploads/profile-photos}")
    private String profilePhotosDir;

    @Value("${app.upload.max-file-size:5242880}") // 5MB default
    private long maxFileSize;

    @Value("${app.upload.profile-photo-url-prefix:/api/uploads/profile-photos}")
    private String profilePhotoUrlPrefix;

    public String uploadProfilePhoto(Integer userId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Fișierul nu poate fi gol.");
        }

        if (!isValidImageFile(file)) {
            throw new IllegalArgumentException("Doar fișierele imagine sunt permise.");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("Poza de profil trebuie să fie mai mică de 5 MB.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        // Delete old photo if exists
        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            deleteProfilePhotoFile(user.getProfilePhoto());
        }

        // Generate unique filename
        String filename = generateUniqueFilename(file.getOriginalFilename());
        String photoPath = savePhotoFile(file, filename);

        // Update user entity
        user.setProfilePhoto(photoPath);
        userRepository.save(user);

        // Notify SSE listeners about the change
        try {
            sseService.broadcastUserUpdated(userId, photoPath);
        } catch (Exception ignored) {
        }

        return photoPath;
    }

    public void deleteProfilePhoto(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            String old = user.getProfilePhoto();
            deleteProfilePhotoFile(old);
            user.setProfilePhoto(null);
            userRepository.save(user);

            try {
                sseService.broadcastUserUpdated(userId, null);
            } catch (Exception ignored) {
            }
        }
    }

    private boolean isValidImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    private String generateUniqueFilename(String originalFilename) {
        if (originalFilename == null) {
            originalFilename = "image.jpg";
        }
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID() + "." + extension;
    }

    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
        }
        return "jpg";
    }

    private String savePhotoFile(MultipartFile file, String filename) throws IOException {
        Path uploadDir = Paths.get(profilePhotosDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        Path filePath = uploadDir.resolve(filename);
        Files.write(filePath, file.getBytes());

        return filename;
    }

    private void deleteProfilePhotoFile(String filename) {
        try {
            Path filePath = Paths.get(profilePhotosDir).resolve(filename).toAbsolutePath().normalize();
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // Log error but don't throw - deletion failure shouldn't block user operations
            System.err.println("Eroare la ștergerea fișierului de profil: " + e.getMessage());
        }
    }

    public String getProfilePhotoUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        return profilePhotoUrlPrefix + "/" + filename;
    }
}
