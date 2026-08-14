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
    public void removeFavorite(Integer favoriteId, Integer currentUserId) {
        favoriteColleagueRepository.deleteByUserIdAndFavoriteColleagueId(currentUserId, favoriteId);
    }

    public void addFavorite(Integer favoriteId, Integer currentUserId) {
        if (currentUserId.equals(favoriteId)) {
            throw new RuntimeException("Nu poti sa te adaugi la favoriti");
        }
        if (favoriteColleagueRepository.existsByUserIdAndFavoriteColleagueId(favoriteId, currentUserId)) {
            return;
        }

        User currentUser = userRepository.findById(currentUserId).orElse(null);
        User favoriteUser =  userRepository.findById(favoriteId).orElse(null);

        FavoriteColleague favoriteColleague = new FavoriteColleague();
        favoriteColleague.setUser(currentUser);
        favoriteColleague.setFavoriteColleague(favoriteUser);
        favoriteColleagueRepository.save(favoriteColleague);
    }

}
