package fi.vaasa.dnguyen.web.rest;

import com.codahale.metrics.annotation.Timed;
import fi.vaasa.dnguyen.domain.GravaHalGame;
import fi.vaasa.dnguyen.repository.GravaHalGameRepository;
import fi.vaasa.dnguyen.security.SecurityUtils;
import fi.vaasa.dnguyen.service.GravaHalGameService;
import fi.vaasa.dnguyen.web.websocket.dto.GameActivityDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing GravaHalGame.
 */
@RestController
@RequestMapping("/api")
public class GravaHalGameResource {

    private final Logger log = LoggerFactory.getLogger(GravaHalGameResource.class);

    @Inject
    private GravaHalGameRepository gravaHalGameRepository;
    
    @Inject 
    private GravaHalGameService gravaHalGameService;
    
    @Inject
    SimpMessageSendingOperations messagingTemplate;

    /**
     * POST  /gravaHalGames -> Create a new gravaHalGame.
     */
    @RequestMapping(value = "/gravaHalGames",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GravaHalGame> create(@RequestBody GravaHalGame gravaHalGame) throws URISyntaxException {
        log.debug("REST request to save GravaHalGame : {}", gravaHalGame);
        if (gravaHalGame.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new gravaHalGame cannot already have an ID").body(null);
        }
        GravaHalGame result = gravaHalGameRepository.save(gravaHalGame);
        return ResponseEntity.created(new URI("/api/gravaHalGames/" + gravaHalGame.getId())).body(result);
    }

    /**
     * PUT  /gravaHalGames -> Updates an existing gravaHalGame.
     */
    @RequestMapping(value = "/gravaHalGames",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GravaHalGame> update(@RequestBody GravaHalGame gravaHalGame) throws URISyntaxException {
        log.debug("REST request to update GravaHalGame : {}", gravaHalGame);
        if (gravaHalGame.getId() == null) {
            return create(gravaHalGame);
        }
        GravaHalGame result = gravaHalGameRepository.save(gravaHalGame);
        return ResponseEntity.ok().body(result);
    }

    /**
     * GET  /gravaHalGames -> get all the gravaHalGames.
     */
    @RequestMapping(value = "/gravaHalGames",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<GravaHalGame> getAll() {
        log.debug("REST request to get all GravaHalGames");
        return gravaHalGameRepository.findAll();
    }

    /**
     * GET  /gravaHalGames/:id -> get the "id" gravaHalGame.
     */
    @RequestMapping(value = "/gravaHalGames/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GravaHalGame> get(@PathVariable Long id) {
        log.debug("REST request to get GravaHalGame : {}", id);
        return Optional.ofNullable(gravaHalGameRepository.findOne(id))
            .map(gravaHalGame -> new ResponseEntity<>(
                gravaHalGame,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /gravaHalGames/:id -> delete the "id" gravaHalGame.
     */
    @RequestMapping(value = "/gravaHalGames/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete GravaHalGame : {}", id);
        gravaHalGameRepository.delete(id);
    }
    
    
    /**
     * POST  /gravaHalGames/createNewGame -> Create a new gravaHalGame for user.
     */
    @RequestMapping(value = "/gravaHalGames/createNewGame",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GravaHalGame> createNewGame(@RequestBody String loginname) throws URISyntaxException {
        log.debug("REST request to create new GravaHalGame for username : {}", loginname);
        GravaHalGame result = gravaHalGameService.createNewGameForUser(loginname);
        return ResponseEntity.created(new URI("/api/gravaHalGames/" + result.getId())).body(result);
    }
    
    /**
     * GET  /gravaHalGames/getUnfinishedGames -> get all not-completed gravaHalGames.
     */
    @RequestMapping(value = "/gravaHalGames/getUnfinishedGames",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<GravaHalGame> getUnfinishedGames() {
        log.debug("REST request to get all not-completed GravaHalGames");
        return gravaHalGameService.findUnfinisedGames();
    }
    
    /**
     * POST  /gravaHalGames/joinTable -> Update GravaHalGame when user joins table.
     */
    @RequestMapping(value = "/gravaHalGames/joinTable",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GravaHalGame> joinTable(@RequestParam(value="gameId") Long gameId, @RequestParam(value="loginName") String loginname) throws URISyntaxException {
        log.debug("REST request to join GravaHalGame for username : {}", loginname);
        GravaHalGame result = gravaHalGameService.joinTable(gameId, loginname);
        
        //send update to connected client
        GameActivityDTO gameActivityDTO = new GameActivityDTO();
        gameActivityDTO.setGameId(result.getId());
        gameActivityDTO.setGameState(result.getGameState());
        gameActivityDTO.setCurrentTurn(result.getCurrentTurn());
        gameActivityDTO.setUserLogin(SecurityUtils.getCurrentLogin());
        messagingTemplate.convertAndSend("/gravahal/gamestate", gameActivityDTO);
        
        return ResponseEntity.ok().body(result);
    }
    
    /**
     * POST  /gravaHalGames/sowPit -> Sow pit from pitIndex.
     */
    @RequestMapping(value = "/gravaHalGames/sowPit",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GravaHalGame> sowPit(@RequestParam(value="gameId") Long gameId, @RequestParam(value="loginName") String loginname, @RequestParam(value="pitIndex") int pitIndex) throws URISyntaxException {
        log.debug("REST request to sow pit for username : {} at pitIndex : {}", loginname, pitIndex);
        GravaHalGame result = gravaHalGameService.sowPit(gameId, loginname, pitIndex);
        
        //send update to connected client
        GameActivityDTO gameActivityDTO = new GameActivityDTO();
        gameActivityDTO.setGameId(result.getId());
        gameActivityDTO.setGameState(result.getGameState());
        gameActivityDTO.setCurrentTurn(result.getCurrentTurn());
        gameActivityDTO.setUserLogin(SecurityUtils.getCurrentLogin());
        messagingTemplate.convertAndSend("/gravahal/gamestate", gameActivityDTO);
        
        return ResponseEntity.ok().body(result);
    }
}
