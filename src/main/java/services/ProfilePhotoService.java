package services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import repositories.UserRepository;
import entities.User;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProfilePhotoService {

    private final UserRepository userRepository;
    private final SseService sseService;
    private final Cloudinary cloudinary;

    @Value("${app.upload.max-file-size:5242880}") // 5MB default
    private long maxFileSize;

    @Value("${cloudinary.cloud-name:}")
    private String cloudName;

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

        if (cloudName.isBlank()) {
            throw new IllegalStateException("Cloudinary nu este configurat. Adaugă CLOUDINARY_CLOUD_NAME, CLOUDINARY_API_KEY și CLOUDINARY_API_SECRET.");
        }

        Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "public_id", "bys/profile-photos/user-" + userId,
                "overwrite", true,
                "invalidate", true,
                "resource_type", "image"
        ));
        String photoUrl = String.valueOf(uploadResult.get("secure_url"));
        if (photoUrl == null || photoUrl.isBlank() || "null".equals(photoUrl)) {
            throw new IOException("Cloudinary nu a returnat URL-ul pozei.");
        }

        user.setProfilePhoto(photoUrl);
        userRepository.save(user);

        // Notify SSE listeners about the change
        try {
            sseService.broadcastUserUpdated(userId, photoUrl);
        } catch (Exception ignored) {
        }

        return photoUrl;
    }

    public void deleteProfilePhoto(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (user.getProfilePhoto() != null && !user.getProfilePhoto().isEmpty()) {
            if (!cloudName.isBlank()) {
                try {
                    cloudinary.uploader().destroy("bys/profile-photos/user-" + userId,
                            ObjectUtils.asMap("invalidate", true));
                } catch (IOException ignored) {
                    // Ștergerea locală a referinței din DB rămâne posibilă dacă
                    // fișierul a fost deja eliminat din Cloudinary.
                }
            }
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
        return Set.of("image/jpeg", "image/png", "image/webp").contains(contentType);
    }

}
