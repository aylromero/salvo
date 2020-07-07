package com.codeoftheweb.salvo.controller;

import com.codeoftheweb.salvo.entities.*;
import com.codeoftheweb.salvo.repositories.GamePlayerRepository;
import com.codeoftheweb.salvo.repositories.GameRepository;
import com.codeoftheweb.salvo.repositories.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {

    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GamePlayerRepository gamePlayerRepository;
    @Autowired
    private PlayerRepository playerRepository;
    @Autowired
    public PasswordEncoder passwordEncoder;

    @RequestMapping("/games")
    public Map<String, Object> getAllGames(Authentication authentication) {
        Map<String, Object> dto = new HashMap<>();
        if (!isGuest(authentication))
            dto.put("player", playerRepository.findByUserName(authentication.getName()).playerDTO());
        else
            dto.put("player", "Guest");
        dto.put("games", gameRepository.findAll().stream().map(Game::gameDTO).collect(Collectors.toList()));
        return dto;
    }

    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createGame(Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication))
            response = new ResponseEntity<>(makeMap("error", "you are not logged in"), HttpStatus.UNAUTHORIZED);
        else {
            Player player = playerRepository.findByUserName(authentication.getName());
            Game newGame = gameRepository.save(new Game(LocalDateTime.now()));
            GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, newGame, newGame.getCreateDate()));
            response = new ResponseEntity<>(makeMap("idGp", gamePlayer.getId()), HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping(path = "/games/{gameId}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(Authentication authentication, @PathVariable long gameId) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "you are not logged in"), HttpStatus.UNAUTHORIZED);
        } else {
            Game game = gameRepository.findById(gameId).orElse(null);
            if (game == null) {
                response = new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.FORBIDDEN);
            } else if (game.getGamePlayer().size() > 1) {
                response = new ResponseEntity<>(makeMap("error", "Game is full"), HttpStatus.FORBIDDEN);
            } else {
                Player player = playerRepository.findByUserName(authentication.getName());
                if (game.getGamePlayer().stream().anyMatch(gp -> gp.getPlayer().getUserName().equals(player.getUserName()))) {
                    response = new ResponseEntity<>(makeMap("error", "you can't play againtst yuourself"), HttpStatus.FORBIDDEN);
                } else {
                    GamePlayer gamePlayer = gamePlayerRepository.save(new GamePlayer(player, game));
                    response = new ResponseEntity<>(makeMap("idGp", gamePlayer.getId()), HttpStatus.CREATED);
                }
            }
        }
        return response;
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }

    @RequestMapping("/game_view/{gamePlayerId}")
    public ResponseEntity<Map<String, Object>> getGameView(@PathVariable long gamePlayerId, Authentication authentication) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "you are not logged in"), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gamePlayer = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(authentication.getName());
            if (gamePlayer == null) {
                response = new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.UNAUTHORIZED);
            } else if (!gamePlayer.getPlayer().getUserName().equals(player.getUserName())) {
                response = new ResponseEntity<>(makeMap("error", "this game is not yours"), HttpStatus.UNAUTHORIZED);
            } else {
                response = new ResponseEntity<>(gameViewDTO(gamePlayer), HttpStatus.OK);
            }
        }
        return response;
    }

    public Map<String, Object> gameViewDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        if (gamePlayer != null) {
            dto.put("idGame", gamePlayer.getGame().getId());
            dto.put("date", gamePlayer.getGame().getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
            dto.put("hour", gamePlayer.getGame().getCreateDate().format(DateTimeFormatter.ISO_LOCAL_TIME));
            dto.put("startCreator", gamePlayer.getGame().getStartCreator());
            dto.put("gamePlayers", gamePlayer.getGame().getGamePlayer().stream().map(GamePlayer::gamePlayerDTO));
            dto.put("player", gamePlayer.getPlayer().getUserName());
            dto.put("ships", gamePlayer.getShips().stream().map(Ship::shipDTO));
            dto.put("salvo", gamePlayer.getGame().getGamePlayer().stream().flatMap(gp -> gp.getSalvos().stream().map(Salvo::salvoDTO)));
        } else {
            dto.put("error", "no such game");
        }
        return dto;
    }

    @RequestMapping(path = "/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> register(@RequestParam String userName, @RequestParam String firstName, @RequestParam String email, @RequestParam String password) {
        ResponseEntity<Map<String, Object>> response;
        Player player = playerRepository.findByUserName(userName);
        if ((userName.isEmpty() || userName.contains(" ")) || firstName.isEmpty() || email.isEmpty() || (password.isEmpty() || password.contains(" "))) {
            response = new ResponseEntity<>(makeMap("error", "Missing data"), HttpStatus.FORBIDDEN);
        } else if (player != null) {
            response = new ResponseEntity<>(makeMap("error", "Name already in use"), HttpStatus.FORBIDDEN);
        } else {
            Player newPlayer = playerRepository.save(new Player(userName, firstName, email, passwordEncoder.encode(password)));
            response = new ResponseEntity<>(makeMap("userName", newPlayer.getUserName()), HttpStatus.CREATED);
        }
        return response;
    }

    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShip(Authentication authentication, @PathVariable long gamePlayerId, @RequestBody List<Ship> ships) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "you are not logged in"), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(authentication.getName());
            if (gp == null) {
                response = new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.UNAUTHORIZED);
            } else if (!player.getUserName().equals(gp.getPlayer().getUserName())) {
                response = new ResponseEntity<>(makeMap("error", "this game is not yours"), HttpStatus.UNAUTHORIZED);
            } else if (gp.getShips().size() > 0) {
                response = new ResponseEntity<>(makeMap("error", "you already have ships"), HttpStatus.FORBIDDEN);
            } else if (ships.size() != 5) {
                response = new ResponseEntity<>(makeMap("error", "you must have 5 ships"), HttpStatus.FORBIDDEN);
            } else {
                if (ships.stream().anyMatch(ship -> isOutOfRange(ship))) {
                    response = new ResponseEntity<>(makeMap("error", "your ships are out of range"), HttpStatus.FORBIDDEN);
                } else if (ships.stream().anyMatch(ship -> isNotConsecutive(ship))) {
                    response = new ResponseEntity<>(makeMap("error", "your ships aren't consecutives"), HttpStatus.FORBIDDEN);
                } else if (overlappingShips(ships)) {
                    response = new ResponseEntity<>(makeMap("error", "your ships are overlapping"), HttpStatus.FORBIDDEN);
                } else {
                    ships.stream().forEach(ship -> gp.addShip(ship));
                    gamePlayerRepository.save(gp);
                    response = new ResponseEntity<>(makeMap("OK", "adding ships"), HttpStatus.OK);
                }
            }
        }
        return response;
    }

    private boolean overlappingShips(List<Ship> ships) {
        List<String> cells = new ArrayList<>();
        ships.forEach(ship -> cells.addAll(ship.getLocations()));
        for (int i = 0; i < cells.size(); i++) {
            for (int j = i + 1; j < cells.size(); j++) {
                if (cells.get(i).equals(cells.get(j)))
                    return true;
            }
        }
        return false;
    }

    public boolean isOutOfRange(Ship ship) {
        for (String xy : ship.getLocations()) {
            if (!(xy instanceof String) || xy.length() < 2) {
                return true;
            }
            char x = xy.charAt(0);
            int y;
            try {
                y = Integer.parseInt(xy.substring(1));
            } catch (NumberFormatException e) {
                y = 999;
            }
            ;
            if (x < 'A' || x > 'J' || y < 1 || y > 10) {
                return true;
            }
        }
        return false;
    }

    private boolean isNotConsecutive(Ship ship) {
        List<String> cell = ship.getLocations();
        for (int i = 0; i < cell.size() - 1; i++) {
            if (cell.get(i).charAt(0) == cell.get(i + 1).charAt(0)) {
                Integer y = Integer.parseInt(cell.get(i).substring(1));
                Integer yNext = Integer.parseInt(cell.get(i + 1).substring(1));
                if (yNext - y != 1)
                    return true;
            } else {
                char x = cell.get(i).charAt(0);
                char xNext = cell.get(i + 1).charAt(0);
                if (xNext - x != 1) {
                    return true;
                }
                Integer y = Integer.parseInt(cell.get(i).substring(1));
                Integer yNext = Integer.parseInt(cell.get(i + 1).substring(1));
                if (yNext - y != 0)
                    return true;
            }
        }
        return false;
    }

    @RequestMapping(path = "/games/players/{gamePlayerId}/salvoes", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvoes(Authentication authentication, @PathVariable long gamePlayerId, @RequestBody List<String> salvoes) {
        ResponseEntity<Map<String, Object>> response;
        if (isGuest(authentication)) {
            response = new ResponseEntity<>(makeMap("error", "you are not logged in"), HttpStatus.UNAUTHORIZED);
        } else {
            GamePlayer gp = gamePlayerRepository.findById(gamePlayerId).orElse(null);
            Player player = playerRepository.findByUserName(authentication.getName());
            if (gp == null) {
                response = new ResponseEntity<>(makeMap("error", "no such game"), HttpStatus.UNAUTHORIZED);
            } else if (!player.getUserName().equals(gp.getPlayer().getUserName())) {
                response = new ResponseEntity<>(makeMap("error", "this game is not yours"), HttpStatus.UNAUTHORIZED);
            } else if (salvoes.size() != 5) {
                response = new ResponseEntity<>(makeMap("error", "you must have 5 salvoes"), HttpStatus.FORBIDDEN);
            } else {
                int turno = gp.getSalvos().size() + 1;
                Salvo salvo = new Salvo(turno, salvoes);
                gp.addSalvo(salvo);
                gamePlayerRepository.save(gp);
                response = new ResponseEntity<>(makeMap("OK", "adding salvoes"), HttpStatus.OK);
            }
        }
        return response;
    }
}
