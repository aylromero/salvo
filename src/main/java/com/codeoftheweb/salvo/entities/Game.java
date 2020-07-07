package com.codeoftheweb.salvo.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

// JPA -> mapeo entre los objetos y las db

@Entity // una entidad es una representacióon de un objeto
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "game", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();


    public Game() {
        this.createDate = LocalDateTime.now();
    }

    public Game(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public long getId() {
        return id;
    }

    public LocalDateTime getCreateDate() {
        return this.createDate;
    }

    public void addgp(GamePlayer gp) {
        this.gamePlayers.add(gp);
        gp.setGame(this);
    }

    public Set<GamePlayer> getGamePlayer() {
        return this.gamePlayers;
    }

    public Set<Score> getScores() {
        return this.scores;
    }

    public void addScore(Score score) {
        this.scores.add(score);
        score.setGame(this);
    }

    // DTO
    public Map<String, Object> gameDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", this.getId());
        dto.put("startCreator", getStartCreator());
        dto.put("date", getCreateDate().format(DateTimeFormatter.ISO_LOCAL_DATE));
        dto.put("hour", getCreateDate().format(DateTimeFormatter.ISO_LOCAL_TIME));
        dto.put("state", getState());
        dto.put("gamePlayers", this.gamePlayers.stream().map(GamePlayer::gamePlayerDTO));
        return dto;
    }

    public String getStartCreator() {
        String startCreator = "";
        if (this.getGamePlayer().size() > 1) {
            for (GamePlayer gp : this.gamePlayers) {
                if (gp.getJoinDate().equals(this.createDate)) {
                    startCreator = gp.getPlayer().getUserName();
                }
            }
        } else {
            Iterator<GamePlayer> player = gamePlayers.iterator();
            startCreator = player.next().getPlayer().getUserName();
        }
        return startCreator;
    }

    public String getState() {
        String state = "";
        if (this.getGamePlayer().size() == 1) {
            return state = "joined";
        } else {
            Iterator<GamePlayer> gamePlayer = gamePlayers.iterator();
            if (gamePlayer.next().getPlayer().getScore(this) == null)
                return state = "inCourse";
            else
                return state = "finish";
        }
    }
}
