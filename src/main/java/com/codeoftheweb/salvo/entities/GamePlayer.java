package com.codeoftheweb.salvo.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class GamePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime joinDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "player_id")
    private Player player;

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Ship> ships = new HashSet<>();

    @OneToMany(mappedBy = "gamePlayer", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Salvo> salvos = new HashSet<>();

    public GamePlayer() {
        this.joinDate = LocalDateTime.now();
    }

    public GamePlayer(Player player, Game game) {
        this.joinDate = LocalDateTime.now();
        this.player = player;
        this.game = game;
    }

    public GamePlayer(Player player, Game game, LocalDateTime createGame) {
        this.joinDate = createGame;
        this.player = player;
        this.game = game;
    }

    public long getId() {
        return id;
    }

    public Game getGame() {
        return this.game;
    }

    public Player getPlayer() {
        return this.player;
    }

    public LocalDateTime getJoinDate() {
        return this.joinDate;
    }

    public Set<Ship> getShips() {
        return this.ships;
    }

    public Set<Salvo> getSalvos() {
        return this.salvos;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Score getScore() {
        return this.getPlayer().getScore(this.getGame());
    }

    public void addShip(Ship ship) {
        this.ships.add(ship);
        ship.setGamePlayer(this);
    }

    public void addSalvo(Salvo salvo) {
        this.salvos.add(salvo);
        salvo.setGamePlayer(this);
    }

    public Map<String, Object> gamePlayerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("idGp", getId());
        dto.put("player", this.getPlayer().playerDTO());
        if (this.getScore() != null)
            dto.put("score", this.getScore().getPoints());
        else
            dto.put("score", null);
        return dto;
    }

    public GamePlayer getOpponent() {
        return this.getGame().getGamePlayer().stream().filter(gp -> gp.getId() != this.getId()).findFirst().orElse(null);
    }
}
