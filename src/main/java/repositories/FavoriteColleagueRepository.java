package repositories;

import entities.FavoriteColleague;
import entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FavoriteColleagueRepository extends JpaRepository<FavoriteColleague, Integer> {
    Optional<User> findByUserIdAndFavoriteColleagueId(Integer userId, Integer favoriteColleagueId);

    boolean existsByUserIdAndFavoriteColleagueId(Integer userId, Integer favoriteColleagueId)
            ;

    void deleteByUserIdAndFavoriteColleagueId(Integer userId, Integer favoriteColleagueId);

    @Query("""
            SELECT fc.favoriteColleague
            FROM FavoriteColleague fc
            WHERE fc.user.id = :userId   
            """)
    List<User> findFavoriteUsersByUserId(@Param("userId") Integer userId);
}
