package fi.vaasa.dnguyen.web.rest;

import fi.vaasa.dnguyen.Application;
import fi.vaasa.dnguyen.domain.GameState;
import fi.vaasa.dnguyen.domain.GravaHalGame;
import fi.vaasa.dnguyen.domain.PlayerIndex;
import fi.vaasa.dnguyen.repository.GravaHalGameRepository;
import fi.vaasa.dnguyen.service.GravaHalGameService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the GravaHalGameResource REST controller.
 *
 * @see GravaHalGameResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class GravaHalGameResourceTest {

    private static final String DEFAULT_PLAYER_ONE = "SAMPLE_TEXT";
    private static final String UPDATED_PLAYER_ONE = "UPDATED_TEXT";
    private static final String DEFAULT_PLAYER_TWO = "SAMPLE_TEXT";
    private static final String UPDATED_PLAYER_TWO = "UPDATED_TEXT";
    private static final PlayerIndex DEFAULT_CURRENT_TURN = PlayerIndex.PLAYER_ONE;
    private static final PlayerIndex UPDATED_CURRENT_TURN = PlayerIndex.PLAYER_TWO;

    @Inject
    private GravaHalGameRepository gravaHalGameRepository;
    
    @Inject
    private GravaHalGameService gravaHalGameService;

    private MockMvc restGravaHalGameMockMvc;

    private GravaHalGame gravaHalGame;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        GravaHalGameResource gravaHalGameResource = new GravaHalGameResource();
        ReflectionTestUtils.setField(gravaHalGameResource, "gravaHalGameRepository", gravaHalGameRepository);
        ReflectionTestUtils.setField(gravaHalGameResource, "gravaHalGameService", gravaHalGameService);
        this.restGravaHalGameMockMvc = MockMvcBuilders.standaloneSetup(gravaHalGameResource).build();
    }

    @Before
    public void initTest() {
        gravaHalGame = new GravaHalGame();
        gravaHalGame.setPlayerOne(DEFAULT_PLAYER_ONE);
        gravaHalGame.setPlayerTwo(DEFAULT_PLAYER_TWO);
        gravaHalGame.setCurrentTurn(DEFAULT_CURRENT_TURN);
    }

    @Test
    @Transactional
    public void createGravaHalGame() throws Exception {
        int databaseSizeBeforeCreate = gravaHalGameRepository.findAll().size();

        // Create the GravaHalGame
        restGravaHalGameMockMvc.perform(post("/api/gravaHalGames")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(gravaHalGame)))
                .andExpect(status().isCreated());

        // Validate the GravaHalGame in the database
        List<GravaHalGame> gravaHalGames = gravaHalGameRepository.findAll();
        assertThat(gravaHalGames).hasSize(databaseSizeBeforeCreate + 1);
        GravaHalGame testGravaHalGame = gravaHalGames.get(gravaHalGames.size() - 1);
        assertThat(testGravaHalGame.getPlayerOne()).isEqualTo(DEFAULT_PLAYER_ONE);
        assertThat(testGravaHalGame.getPlayerTwo()).isEqualTo(DEFAULT_PLAYER_TWO);
        assertThat(testGravaHalGame.getCurrentTurn()).isEqualTo(DEFAULT_CURRENT_TURN);
    }

    @Test
    @Transactional
    public void getAllGravaHalGames() throws Exception {
        // Initialize the database
        gravaHalGameRepository.saveAndFlush(gravaHalGame);

        // Get all the gravaHalGames
        restGravaHalGameMockMvc.perform(get("/api/gravaHalGames"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(gravaHalGame.getId().intValue())))
                .andExpect(jsonPath("$.[*].playerOne").value(hasItem(DEFAULT_PLAYER_ONE.toString())))
                .andExpect(jsonPath("$.[*].playerTwo").value(hasItem(DEFAULT_PLAYER_TWO.toString())))
                .andExpect(jsonPath("$.[*].nextTurn").value(hasItem(DEFAULT_CURRENT_TURN.toString())));
    }

    @Test
    @Transactional
    public void getGravaHalGame() throws Exception {
        // Initialize the database
        gravaHalGameRepository.saveAndFlush(gravaHalGame);

        // Get the gravaHalGame
        restGravaHalGameMockMvc.perform(get("/api/gravaHalGames/{id}", gravaHalGame.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(gravaHalGame.getId().intValue()))
            .andExpect(jsonPath("$.playerOne").value(DEFAULT_PLAYER_ONE.toString()))
            .andExpect(jsonPath("$.playerTwo").value(DEFAULT_PLAYER_TWO.toString()))
            .andExpect(jsonPath("$.nextTurn").value(DEFAULT_CURRENT_TURN.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGravaHalGame() throws Exception {
        // Get the gravaHalGame
        restGravaHalGameMockMvc.perform(get("/api/gravaHalGames/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGravaHalGame() throws Exception {
        // Initialize the database
        gravaHalGameRepository.saveAndFlush(gravaHalGame);

		int databaseSizeBeforeUpdate = gravaHalGameRepository.findAll().size();

        // Update the gravaHalGame
        gravaHalGame.setPlayerOne(UPDATED_PLAYER_ONE);
        gravaHalGame.setPlayerTwo(UPDATED_PLAYER_TWO);
        gravaHalGame.setCurrentTurn(UPDATED_CURRENT_TURN);
        restGravaHalGameMockMvc.perform(put("/api/gravaHalGames")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(gravaHalGame)))
                .andExpect(status().isOk());

        // Validate the GravaHalGame in the database
        List<GravaHalGame> gravaHalGames = gravaHalGameRepository.findAll();
        assertThat(gravaHalGames).hasSize(databaseSizeBeforeUpdate);
        GravaHalGame testGravaHalGame = gravaHalGames.get(gravaHalGames.size() - 1);
        assertThat(testGravaHalGame.getPlayerOne()).isEqualTo(UPDATED_PLAYER_ONE);
        assertThat(testGravaHalGame.getPlayerTwo()).isEqualTo(UPDATED_PLAYER_TWO);
        assertThat(testGravaHalGame.getCurrentTurn()).isEqualTo(UPDATED_CURRENT_TURN);
    }

    @Test
    @Transactional
    public void deleteGravaHalGame() throws Exception {
        // Initialize the database
        gravaHalGameRepository.saveAndFlush(gravaHalGame);

		int databaseSizeBeforeDelete = gravaHalGameRepository.findAll().size();

        // Get the gravaHalGame
        restGravaHalGameMockMvc.perform(delete("/api/gravaHalGames/{id}", gravaHalGame.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<GravaHalGame> gravaHalGames = gravaHalGameRepository.findAll();
        assertThat(gravaHalGames).hasSize(databaseSizeBeforeDelete - 1);
    }
    
    @Test
    @Transactional
    public void createGravaHalGameForUser() throws Exception {
        int databaseSizeBeforeCreate = gravaHalGameRepository.findAll().size();
        
        String loginname = "userTest";

        // Create the GravaHalGame
        restGravaHalGameMockMvc.perform(post("/api/gravaHalGames/createNewGame")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(loginname))
                .andExpect(status().isCreated());

        // Validate the GravaHalGame in the database
        List<GravaHalGame> gravaHalGames = gravaHalGameRepository.findAll();
        assertThat(gravaHalGames).hasSize(databaseSizeBeforeCreate + 1);
        GravaHalGame testGravaHalGame = gravaHalGames.get(gravaHalGames.size() - 1);
        assertThat(testGravaHalGame.getPlayerOne()).isEqualTo(loginname);
        assertThat(testGravaHalGame.getPlayerTwo()).isEqualTo(null);
        assertThat(testGravaHalGame.getCurrentTurn()).isEqualTo(PlayerIndex.PLAYER_ONE);
        assertThat(testGravaHalGame.getGameState()).isEqualTo(GameState.WAITING_FOR_PLAYER);
        assertThat(testGravaHalGame.getGameBoard().size()).isEqualTo(2);
        assertThat(testGravaHalGame.getGameBoard().get(0).getSidePlayer()).isEqualTo(PlayerIndex.PLAYER_ONE);
    }
}
