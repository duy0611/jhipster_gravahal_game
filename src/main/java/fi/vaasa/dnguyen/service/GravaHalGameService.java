package fi.vaasa.dnguyen.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fi.vaasa.dnguyen.domain.GameState;
import fi.vaasa.dnguyen.domain.GravaHalGame;
import fi.vaasa.dnguyen.domain.PlayerIndex;
import fi.vaasa.dnguyen.domain.SideBoard;
import fi.vaasa.dnguyen.repository.GravaHalGameRepository;
import fi.vaasa.dnguyen.service.util.StringUtil;

@Service
@Transactional
public class GravaHalGameService {

    private final Logger log = LoggerFactory.getLogger(GravaHalGameService.class);

    @Inject
    GravaHalGameRepository gravaHalGameRepository;
    
    public GravaHalGame createNewGameForUser(String loginname) {
    	
    	log.info("creating new game for user: " + loginname);
    	
    	GravaHalGame game = new GravaHalGame();
    	game.setPlayerOne(loginname);
    	game.setCurrentTurn(PlayerIndex.PLAYER_ONE);
    	game.setGameState(GameState.WAITING_FOR_PLAYER);
    	
    	List<SideBoard> gameBoard = new ArrayList<SideBoard>();
    	gameBoard.add(new SideBoard(PlayerIndex.PLAYER_ONE));
    	gameBoard.add(new SideBoard(PlayerIndex.PLAYER_TWO));
    	
    	game.setGameBoard(gameBoard);
    	
    	gravaHalGameRepository.save(game);
    	
    	return game;
    }
    
    public List<GravaHalGame> findUnfinisedGames() {
    	return gravaHalGameRepository.findByGameStateIn(Arrays.asList(new GameState[] { GameState.WAITING_FOR_PLAYER, GameState.GAME_STARTED }));
    }

	public GravaHalGame joinTable(Long gameId, String loginname) {
		
		log.info("user " + loginname + " has joined table: " + gameId);
		
		GravaHalGame dbGame = gravaHalGameRepository.getOne(gameId);
		
		//check if there is any user join the tables
		//check first playerOne, then playerTwo respectively
		if(StringUtil.isNullOrEmpty(dbGame.getPlayerOne())) {
			dbGame.setPlayerOne(loginname);
		}
		else if(dbGame.getPlayerOne().equals(loginname)) {
			//do nothing as this user has already joined the table
			return dbGame;
		}
		else if(StringUtil.isNullOrEmpty(dbGame.getPlayerTwo())) {
			dbGame.setPlayerTwo(loginname);
		}
		else if(dbGame.getPlayerTwo().equals(loginname)) {
			//do nothing as this user has already joined the table
			return dbGame;
		}
		
		//set gameState to STARTED when there are 2 players
		if(!StringUtil.isNullOrEmpty(dbGame.getPlayerOne()) && !StringUtil.isNullOrEmpty(dbGame.getPlayerTwo())) {
			dbGame.setGameState(GameState.GAME_STARTED);
		}
		
		gravaHalGameRepository.save(dbGame);
		
		return dbGame;
	}

	public GravaHalGame sowPit(Long gameId, String loginName, int pitIndex) {
		
		log.info("Sow pit at pitIndex: " + pitIndex + " for user " + loginName + " at table " + gameId);
		
		GravaHalGame dbGame = gravaHalGameRepository.getOne(gameId);
		
		PlayerIndex sideIndex = null;
		if(dbGame.getPlayerOne() != null && dbGame.getPlayerOne().equals(loginName)) {
			sideIndex = PlayerIndex.PLAYER_ONE;
		}
		else if(dbGame.getPlayerTwo() != null && dbGame.getPlayerTwo().equals(loginName)) {
			sideIndex = PlayerIndex.PLAYER_TWO;
		}
		
		//do validation if the pit to sow is valid!
		if(sideIndex != null) {
			dbGame.playFrom(sideIndex, pitIndex);
			gravaHalGameRepository.save(dbGame);
		}
		
		return dbGame;
	}
}
