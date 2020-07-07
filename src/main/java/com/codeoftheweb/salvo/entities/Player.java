package com.codeoftheweb.salvo.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private String userName;
    private String firstName;
    // private String lastName;
    private String email;
    private String password;
    private boolean admin;

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<GamePlayer> gamePlayers = new HashSet<>();

    @OneToMany(mappedBy = "player", fetch = FetchType.EAGER)
    private Set<Score> scores = new HashSet<>();

    public Player() {
    }

    public Player(String userName, String firstName, String email, String password) {
        this.userName = userName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
    }

    public Player(String userName, String firstName, String email, String password, boolean admin) {
        this.userName = userName;
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.admin = admin;
    }

    public long getId() {
        return this.id;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<GamePlayer> getGamePlayer() {
        return this.gamePlayers;
    }

    public Set<Score> getScores() {
        return this.scores;
    }

    public Score getScore(Game game) {
        for (Score score : this.scores) {
            if (game.getId() == score.getGame().getId())
                return score;
        }
        return null;
    }

    public void addScore(Score score) {
        this.scores.add(score);
        score.setPlayer(this);
    }

    public Map<String, Object> playerDTO() {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("id", getId());
        dto.put("userName", getUserName());
        return dto;
    }
}
