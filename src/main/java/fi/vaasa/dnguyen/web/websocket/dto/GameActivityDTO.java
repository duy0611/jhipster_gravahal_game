package fi.vaasa.dnguyen.web.websocket.dto;

import fi.vaasa.dnguyen.domain.GameState;
import fi.vaasa.dnguyen.domain.PlayerIndex;

public class GameActivityDTO {

    private String userLogin;
    
    private Long gameId;
    
    private GameState gameState;
    
    private PlayerIndex currentTurn;
    

	public String getUserLogin() {
		return userLogin;
	}

	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}

	public Long getGameId() {
		return gameId;
	}

	public void setGameId(Long gameId) {
		this.gameId = gameId;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public PlayerIndex getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(PlayerIndex currentTurn) {
		this.currentTurn = currentTurn;
	}
    
    
}
