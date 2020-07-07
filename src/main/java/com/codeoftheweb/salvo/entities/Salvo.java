package com.codeoftheweb.salvo.entities;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class Salvo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private long id;
    private int turn;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gamePlayer_id")
    private GamePlayer gamePlayer;

    @ElementCollection
    @Column(name = "salvoLocation")
    private List<String> locations = new ArrayList<>();

    public Salvo() {
    }

    public Salvo(int turn, List<String> locations) {
        this.turn = turn;
        this.locations = locations;
    }

    public Salvo(int turn, GamePlayer gp, List<String> locations) {
        this.turn = turn;
        this.gamePlayer = gp;
        this.locations = locations;
    }

    public long getId() {
        return id;
    }

    public int getTurn() {
        return turn;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public List<String> getLocations() {
        return locations;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public void setLocations(List<String> locations) {
        this.locations = locations;
    }

    // DTO
	public Map<String, Object> salvoDTO() {
		Map<String, Object> dto = new LinkedHashMap<>();
		dto.put("turn", this.getTurn());
		dto.put("player", this.getGamePlayer().getPlayer().getUserName());
		dto.put("location", this.getLocations());

		GamePlayer gpOpponent = this.getGamePlayer().getOpponent();
		if (gpOpponent != null) {
			dto.put("hits", salvoOk(this.getLocations(), gpOpponent.getShips()));
			Set<Salvo> myShoots = this.getGamePlayer().getSalvos().stream().filter(salvo -> salvo.getTurn() <= this.getTurn()).collect(Collectors.toSet());
			dto.put("sunken", shipsSunken(myShoots, gpOpponent.getShips()).stream().map(Ship::shipDTO));
		}
		return dto;
	}

    public Set<String> salvoOk(List<String> myShoot, Set<Ship> shipsOther) {
        List<String> allShipsOther = new ArrayList<>();
        shipsOther.forEach(ship -> allShipsOther.addAll(ship.getLocations()));
        return myShoot.stream().filter(shoot -> allShipsOther.stream().anyMatch(x -> x.equals(shoot))).collect(Collectors.toSet());
    }

    public Set<Ship> shipsSunken(Set<Salvo> myShoots, Set<Ship> shipsOther) {
        List<String> allMyShoots = new ArrayList<>();
        myShoots.forEach(shoot -> allMyShoots.addAll(shoot.getLocations()));
        return shipsOther.stream().filter(ship -> allMyShoots.containsAll(ship.getLocations())).collect(Collectors.toSet());
    }
}
