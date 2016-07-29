package fi.vaasa.dnguyen.domain;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import fi.vaasa.dnguyen.domain.SideBoard.TurnResult;


/**
 * A GravaHalGame.
 */
@Entity
@Table(name = "GRAVAHAL_GAME")
public class GravaHalGame implements Serializable {

	private static final long serialVersionUID = -6362552641376917797L;
	
	public static String DRAW_GAME = "DRAW";

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	/* The game has 2 players: playerOne (the one who join first or the one who create the game) and playerTwo */
    @Column(name = "player_one")
    private String playerOne;

    @Column(name = "player_two")
    private String playerTwo;

    /* currentTurn: the player who goes at this turn */
    @Enumerated(EnumType.STRING)
    @Column(name = "current_turn")
    private PlayerIndex currentTurn;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "game_state")
    private GameState gameState = GameState.WAITING_FOR_PLAYER;
    
    @Column(name = "winner")
    private String winner;
    
    /* The game board has 2 sides, one for playerOne and the another one for playerTwo */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval=true, fetch = FetchType.EAGER)
    @JoinColumn(name="gravahal_game_id", referencedColumnName="id")
    private List<SideBoard> gameBoard = new ArrayList<SideBoard>(2);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerOne() {
        return playerOne;
    }

    public void setPlayerOne(String playerOne) {
        this.playerOne = playerOne;
    }

    public String getPlayerTwo() {
        return playerTwo;
    }

    public void setPlayerTwo(String playerTwo) {
        this.playerTwo = playerTwo;
    }

    public PlayerIndex getCurrentTurn() {
		return currentTurn;
	}

	public void setCurrentTurn(PlayerIndex currentTurn) {
		this.currentTurn = currentTurn;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public String getWinner() {
		return winner;
	}

	public void setWinner(String winner) {
		this.winner = winner;
	}

	public List<SideBoard> getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(List<SideBoard> gameBoard) {
		this.gameBoard = gameBoard;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GravaHalGame gravaHalGame = (GravaHalGame) o;

        if ( ! Objects.equals(id, gravaHalGame.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "GravaHalGame{" +
                "id=" + id +
                ", playerOne='" + playerOne + "'" +
                ", playerTwo='" + playerTwo + "'" +
                ", currentTurn='" + currentTurn + "'" +
                '}';
    }
    
    public SideBoard getPlayerSideBoard(PlayerIndex sideIndex) {
    	for (SideBoard sb : this.getGameBoard()) {
    		if(sideIndex.equals(sb.getSidePlayer()))
    			return sb;
    	}
    	throw new RuntimeException("InvalidArgumentException at getPlayerSideBoard() method for GravaHalGame Id: " + this.getId());
    }
    
	/**
	 * Make a play on player's side, taken from the given index. Updates the
	 * board with the new pit contents.
	 * 
	 * @param playerIndex the side to play from. either PlayerOne or PlayerTwo
	 * @param pitIndex zero-based index of the pit to take the stones from.
	 */
	public void playFrom(PlayerIndex playerIndex, int pitIndex) {
		if (canPlay(playerIndex, pitIndex)) {
			SideBoard playerSide = getPlayerSideBoard(playerIndex);
			
			SideBoard currentSide = playerSide;

			TurnResult result = currentSide.playFrom(pitIndex);
			while (result.getStonesInHand() > 0) {
				currentSide = getNextSideBoard(currentSide);
				result = currentSide.sowFrom(result.getStonesInHand(), 0, currentSide.getSidePlayer().equals(playerSide.getSidePlayer()));
			}
			// capture stones - checking
			if (!result.isLastInGravaHal() 
					&& (result.getLastInRegularPit() >= 0 && result.getLastInRegularPit() < SideBoard.MAX_PITS)
					&& result.isLandInOwnPit()) {
				// Always when the last stone lands in an own empty pit, the
				// player captures his own stone and all stones in the opposite
				// pit (the other players' pit) and puts them in his own Grava
				
				SideBoard playerBoard = getPlayerSideBoard(currentTurn);
				
				//if land in own empty pit!
				if(playerBoard.getPitContents(result.getLastInRegularPit()) == 1) {
					doCaptureStones(currentTurn, result.getLastInRegularPit());
				}
			}
			currentTurn = changePlayer(result);
			
			checkGameEnd();
		}
	}
	
	private void doCaptureStones(PlayerIndex playerIndex, int pitIndex) {
		SideBoard playerBoard = getPlayerSideBoard(currentTurn);
		SideBoard opponentBoard = getNextSideBoard(playerBoard);
		
		//take stones from own pit and OPPOSITE pit!
		int captureStones = playerBoard.takeStones(pitIndex) + opponentBoard.takeStones(SideBoard.MAX_PITS - pitIndex - 1);
		
		playerBoard.setGravaHalPit(playerBoard.getGravaHalPit() + captureStones);
	}
	
	private void checkGameEnd() {
		SideBoard playerOneBoard = getPlayerSideBoard(PlayerIndex.PLAYER_ONE);
		SideBoard playerTwoBoard = getPlayerSideBoard(PlayerIndex.PLAYER_TWO);
		
		if(playerOneBoard.isEmptyBoard() || playerTwoBoard.isEmptyBoard()) {
			
			//set GameState to FINISHED
			this.setGameState(GameState.FINISHED);
			
			//set winner...
			int playerOneBoardStones = playerOneBoard.getTotalStones();
			int playerTwoBoardStones = playerTwoBoard.getTotalStones();
			
			if(playerOneBoardStones < playerTwoBoardStones) {
				this.setWinner(playerTwo);
			}
			else if(playerOneBoardStones > playerTwoBoardStones) {
				this.setWinner(playerOne);
			}
			else {
				this.setWinner(DRAW_GAME);
			}
		}
	}

	private PlayerIndex changePlayer(TurnResult turnResult) {
		if (turnResult.isLastInGravaHal()) {
			return currentTurn;
		} else {
			return currentTurn.equals(PlayerIndex.PLAYER_ONE) ? PlayerIndex.PLAYER_TWO : PlayerIndex.PLAYER_ONE;
		}
	}
	
	private boolean canPlay(PlayerIndex playerIndex, int pitIndex) {
		SideBoard sideBoard = getPlayerSideBoard(playerIndex);
		return playerIndex.equals(currentTurn) && sideBoard.isNotEmpty(pitIndex);
	}
	
	private SideBoard getNextSideBoard(SideBoard sideBoard) {
		if(PlayerIndex.PLAYER_ONE.equals(sideBoard.getSidePlayer())) {
			return getPlayerSideBoard(PlayerIndex.PLAYER_TWO);
		}
		else if(PlayerIndex.PLAYER_TWO.equals(sideBoard.getSidePlayer())) {
			return getPlayerSideBoard(PlayerIndex.PLAYER_ONE);
		}
		throw new RuntimeException("InvalidArgumentException at getNextSideBoard() method for GravaHalGame Id: " + this.getId());
	}
}
