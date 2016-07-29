package fi.vaasa.dnguyen.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SIDE_BOARD")
public class SideBoard implements Serializable {
	
	private static final long serialVersionUID = 6105871622472944211L;
	
	public static int MAX_PITS = 6;
	public static int GRAVA_HAL_PIT_INDEX = 7;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/* Determine which player this side board belongs to */
	@Enumerated(EnumType.STRING)
    @Column(name = "side_player")
	private PlayerIndex sidePlayer;
	
	@Column(name = "regular_pit_one")
	private int regularPitOne = 6;
	
	@Column(name = "regular_pit_two")
	private int regularPitTwo = 6;
	
	@Column(name = "regular_pit_three")
	private int regularPitThree = 6;
	
	@Column(name = "regular_pit_four")
	private int regularPitFour = 6;
	
	@Column(name = "regular_pit_five")
	private int regularPitFive = 6;
	
	@Column(name = "regular_pit_six")
	private int regularPitSix = 6;
	
	@Column(name = "gravahal_pit")
	private int gravaHalPit = 0;
	
	public SideBoard() {
		
	}
	
	public SideBoard(PlayerIndex playerIndex) {
		this.sidePlayer = playerIndex;
	}
	
	
	public boolean isNotEmpty(int pitIndex) {
		return isValidPit(pitIndex) && getPitContents(pitIndex) > 0; 
	}
	
	public boolean isValidPit(int pitIndex) {
		return pitIndex >= 0 && pitIndex < MAX_PITS;
	}
	
	public List<Integer> getRegularPits() {
		List<Integer> regularPits = new ArrayList<Integer>();
		regularPits.add(regularPitOne);
		regularPits.add(regularPitTwo);
		regularPits.add(regularPitThree);
		regularPits.add(regularPitFour);
		regularPits.add(regularPitFive);
		regularPits.add(regularPitSix);
		
		return regularPits;
	}
	
	public int getPitContents(int pitIndex) {
		if (isValidPit(pitIndex)) {
			return getRegularPits().get(pitIndex);
		} 
		else {
			return 0;
		}
	}
	
	/**
	 * Make a play starting at given pit index.
	 * @param pitIndex zero-based index of the pit to start
	 * @return contains remaining stones, whether the last stone landed in the Grava Hal, and index of the regular pit where last stone lands in
	 */
	public TurnResult playFrom(int pitIndex) {
		if (isValidPit(pitIndex)) {
			
			int stonesLeft = takeStones(pitIndex);
			
			return sowFrom(stonesLeft, pitIndex + 1, true);
		} 
		else {
			return new TurnResult(0, false, -1, false);
		}
	}
	
	/**
	 * Sow the hand given by stonesLeft into the pits starting at startIndex.
	 * @param stonesLeft amount of stones left after one turn
	 * @param startPitIndex zero-based index of start pit
	 * @param includeGravaHal whether to sow in the side's Grava Hal
	 * @return contains remaining stones, whether the last stone landed in the Grava Hal, and index of the regular pit where last stone lands in
	 */
	public TurnResult sowFrom(int stonesLeft, int startPitIndex, boolean includeGravaHal) {
		boolean lastInGravaHal = false;
		while (stonesLeft > 0 && startPitIndex < MAX_PITS) {
			stonesLeft = sowRegularPit(stonesLeft, startPitIndex);
			startPitIndex++;
		}

		if (stonesLeft > 0 && includeGravaHal) {
			stonesLeft = sowGravaHalPit(stonesLeft);
			lastInGravaHal = true;
		}
		return new TurnResult(stonesLeft, lastInGravaHal, lastInGravaHal ? GRAVA_HAL_PIT_INDEX : startPitIndex - 1, includeGravaHal);
	}
	
	private int sowRegularPit(int hand, int pitIndex) {
		putStone(pitIndex);
		return hand - 1;
	}
	
	private int sowGravaHalPit(int hand) {
		gravaHalPit++;
		return hand - 1;
	}
	
	public void putStone(int pitIndex) {
		switch(pitIndex) {
		case 0:
			regularPitOne++;
			break;
		case 1:
			regularPitTwo++;
			break;
		case 2:
			regularPitThree++;
			break;
		case 3:
			regularPitFour++;
			break;
		case 4:
			regularPitFive++;
			break;
		case 5:
			regularPitSix++;
			break;
		}
	}
	
	public int takeStones(int pitIndex) {
		int stonesLeft = 0;
		
		switch(pitIndex) {
		case 0:
			stonesLeft = regularPitOne;
			regularPitOne = 0;
			break;
		case 1:
			stonesLeft = regularPitTwo;
			regularPitTwo = 0;
			break;
		case 2:
			stonesLeft = regularPitThree;
			regularPitThree = 0;
			break;
		case 3:
			stonesLeft = regularPitFour;
			regularPitFour = 0;
			break;
		case 4:
			stonesLeft = regularPitFive;
			regularPitFive = 0;
			break;
		case 5:
			stonesLeft = regularPitSix;
			regularPitSix = 0;
			break;
		}
		
		return stonesLeft;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PlayerIndex getSidePlayer() {
		return sidePlayer;
	}

	public void setSidePlayer(PlayerIndex sidePlayer) {
		this.sidePlayer = sidePlayer;
	}

	public int getRegularPitOne() {
		return regularPitOne;
	}

	public void setRegularPitOne(int regularPitOne) {
		this.regularPitOne = regularPitOne;
	}

	public int getRegularPitTwo() {
		return regularPitTwo;
	}

	public void setRegularPitTwo(int regularPitTwo) {
		this.regularPitTwo = regularPitTwo;
	}

	public int getRegularPitThree() {
		return regularPitThree;
	}

	public void setRegularPitThree(int regularPitThree) {
		this.regularPitThree = regularPitThree;
	}

	public int getRegularPitFour() {
		return regularPitFour;
	}

	public void setRegularPitFour(int regularPitFour) {
		this.regularPitFour = regularPitFour;
	}

	public int getRegularPitFive() {
		return regularPitFive;
	}

	public void setRegularPitFive(int regularPitFive) {
		this.regularPitFive = regularPitFive;
	}

	public int getRegularPitSix() {
		return regularPitSix;
	}

	public void setRegularPitSix(int regularPitSix) {
		this.regularPitSix = regularPitSix;
	}

	public int getGravaHalPit() {
		return gravaHalPit;
	}

	public void setGravaHalPit(int gravaHalPit) {
		this.gravaHalPit = gravaHalPit;
	}
	
	public class TurnResult {
		private int stonesInHand;
		private boolean lastInGravaHal;
		private int lastInRegularPit; //index of pit where last stone lands in
		private boolean landInOwnPit; //whether the last stone lands in own pit;

		public TurnResult(int _stonesInHand, boolean _lastInGravaHal, int _lastInRegularPit, boolean _landInOwnPit) {
			stonesInHand = _stonesInHand;
			lastInGravaHal = _lastInGravaHal;
			lastInRegularPit = _lastInRegularPit;
			landInOwnPit = _landInOwnPit;
		}

		public int getStonesInHand() {
			return stonesInHand;
		}

		public boolean isLastInGravaHal() {
			return lastInGravaHal;
		}

		public int getLastInRegularPit() {
			return lastInRegularPit;
		}

		public boolean isLandInOwnPit() {
			return landInOwnPit;
		}
		
	}

	public boolean isEmptyBoard() {
		List<Integer> pits = getRegularPits();
		for (int pitContent : pits) {
			if(pitContent > 0)
				return false;
		}
		return true;
	}

	public int getTotalStones() {
		int totalStones = 0;
		
		List<Integer> pits = getRegularPits();
		for (int pitContent : pits) {
			totalStones += pitContent;
		}
		
		totalStones += gravaHalPit;
		
		return totalStones;
	}
	
}
