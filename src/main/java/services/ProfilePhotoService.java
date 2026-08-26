package services;

import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repositories.UserRepository;
import entities.User;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class ProfilePhotoService {

    private final UserRepository userRepository;
    private final SseService sseService;

    @Value("${app.upload.max-file-size:5242880}") // 5MB default
    private long maxFileSize;

    /**
     * Now stores the profile photo directly in the database as a data URL (base64).
     * The stored string has the form: data:<contentType>;base64,<base64data>
     */
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

        // Delete old photo value if exists
        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            user.setProfilePhoto(null);
        }

        // Read bytes and convert to data URL
        byte[] bytes = file.getBytes();
        String base64 = Base64.getEncoder().encodeToString(bytes);
        String contentType = file.getContentType() == null ? "image/jpeg" : file.getContentType();
        String dataUrl = "data:" + contentType + ";base64," + base64;

        // Update user entity
        user.setProfilePhoto(dataUrl);
        userRepository.save(user);

        // Notify SSE listeners about the change
        try {
            sseService.broadcastUserUpdated(userId, dataUrl);
        } catch (Exception ignored) {
        }

        return dataUrl;
    }

    public void deleteProfilePhoto(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
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

    /**
     * For compatibility the service provides this method but now it's simply an identity
     * since we store the full data URL in the DB.
     */
    public String getProfilePhotoUrl(String dataUrl) {
        return dataUrl;
    }
}
