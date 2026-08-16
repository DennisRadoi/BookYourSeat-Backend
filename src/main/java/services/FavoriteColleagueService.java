package services;

import entities.FavoriteColleague;
import entities.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import repositories.FavoriteColleagueRepository;
import repositories.UserRepository;

@Service
public class FavoriteColleagueService {
    private final FavoriteColleagueRepository favoriteColleagueRepository;
    private final UserRepository userRepository;
    public FavoriteColleagueService(FavoriteColleagueRepository favoriteColleagueRepository,
                                    UserRepository userRepository) {
        this.favoriteColleagueRepository = favoriteColleagueRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public boolean removeFavorite(Integer favoriteId, Integer currentUserId) {
        if (favoriteColleagueRepository.existsByUserIdAndFavoriteColleagueId(currentUserId, favoriteId)) {
            favoriteColleagueRepository.deleteByUserIdAndFavoriteColleagueId(currentUserId, favoriteId);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean addFavorite(Integer favoriteId, Integer currentUserId) {
        if (currentUserId.equals(favoriteId)) {
            throw new RuntimeException("You can't add yourself to favorites.");
        }

        if (favoriteColleagueRepository.existsByUserIdAndFavoriteColleagueId(currentUserId, favoriteId)) {
            return false;
        }

        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Current user does not exist"));
        User favoriteUser =  userRepository.findById(favoriteId)
                .orElseThrow(() -> new RuntimeException("Favorite user does not exist"));

        FavoriteColleague favoriteColleague = new FavoriteColleague();
        favoriteColleague.setUser(currentUser);
        favoriteColleague.setFavoriteColleague(favoriteUser);
        favoriteColleagueRepository.save(favoriteColleague);

        return true;
    }

}
