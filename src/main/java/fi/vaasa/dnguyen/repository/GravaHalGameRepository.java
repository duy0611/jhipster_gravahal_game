package fi.vaasa.dnguyen.repository;

import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import fi.vaasa.dnguyen.domain.GameState;
import fi.vaasa.dnguyen.domain.GravaHalGame;

/**
 * Spring Data JPA repository for the GravaHalGame entity.
 */
public interface GravaHalGameRepository extends JpaRepository<GravaHalGame,Long> {

	List<GravaHalGame> findByGameState(GameState gameState);
	
	List<GravaHalGame> findByGameStateIn(Collection<GameState> gameStates);
	
}
