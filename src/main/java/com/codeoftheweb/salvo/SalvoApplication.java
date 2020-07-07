package com.codeoftheweb.salvo;

import com.codeoftheweb.salvo.entities.*;
import com.codeoftheweb.salvo.repositories.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class SalvoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SalvoApplication.class, args);
    }

    @Autowired
    PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository, ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
        return (args) -> {
            Player player1 = new Player("luigi", "Luigi", "luigi@gmail.com", passwordEncoder.encode("5678"));
            Player player2 = new Player("peach", "Peach", "peach@gmail.com", passwordEncoder.encode("9012"));
            Player player3 = new Player("yoshi", "Yoshi", "yoshi@gmail.com", passwordEncoder.encode("3456"), true);
            Player player4 = new Player("toadette", "Toadette", "toadette@gmail.com", passwordEncoder.encode("7890"));

            Game game1 = new Game();
            Game game2 = new Game(LocalDateTime.now().plusHours(1));
            Game game3 = new Game(LocalDateTime.now().plusHours(2));
            Game game4 = new Game(LocalDateTime.now().plusHours(3));
            Game game5 = new Game(LocalDateTime.now().plusHours(4));

            GamePlayer gp1 = new GamePlayer(player1, game1);
            GamePlayer gp2 = new GamePlayer(player2, game2);
            GamePlayer gp3 = new GamePlayer(player3, game2);
            GamePlayer gp4 = new GamePlayer(player4, game3);
            GamePlayer gp5 = new GamePlayer(player2, game3);
            GamePlayer gp6 = new GamePlayer(player4, game4);
            GamePlayer gp7 = new GamePlayer(player2, game4);
            GamePlayer gp8 = new GamePlayer(player3, game1);
            GamePlayer gp9 = new GamePlayer(player1, game5, game5.getCreateDate());

            Ship ship = new Ship();
            Ship ship1 = new Ship("destroyer", Arrays.asList("H2", "H3", "H4"));
            Ship ship2 = new Ship("submarine", gp1, Arrays.asList("D2", "D3", "D4"));
            Ship ship3 = new Ship("patrol_boat", gp1, Arrays.asList("D6", "D7"));
            Ship ship6 = new Ship("destroyer", gp2, Arrays.asList("E5", "E6", "E7"));
            Ship ship7 = new Ship("patrol_boat", gp2, Arrays.asList("D2", "D3"));
            Ship ship8 = new Ship("submarine", gp3, Arrays.asList("J1", "J2", "J3"));
            Ship ship9 = new Ship("patrol_boat", gp3, Arrays.asList("F6", "F7"));
            Ship ship10 = new Ship("submarine", gp4, Arrays.asList("G2", "H3", "I4"));
            Ship ship11 = new Ship("patrol_boat", gp4, Arrays.asList("C6", "C7"));
            gp1.addShip(ship1);

            Salvo salvo1 = new Salvo(1, Arrays.asList("H3", "H4", "H5"));
            Salvo salvo2 = new Salvo(1, Arrays.asList("F4", "F6"));
            Salvo salvo3 = new Salvo(2, Arrays.asList("D2", "D3"));
            Salvo salvo4 = new Salvo(2, Arrays.asList("J1", "J2", "J3"));
            Salvo salvo5 = new Salvo(1, Arrays.asList("A2", "A4", "G6"));
            Salvo salvo6 = new Salvo(1, Arrays.asList("A5", "D5", "D6"));
            Salvo salvo7 = new Salvo(2, Arrays.asList("A3", "H6"));
            Salvo salvo8 = new Salvo(2, Arrays.asList("C5", "C6"));
            Salvo salvo9 = new Salvo(1, Arrays.asList("G6", "H6", "A4"));
            Salvo salvo10 = new Salvo(1, Arrays.asList("H1", "B2", "J1"));
            Salvo salvo11 = new Salvo(2, Arrays.asList("A2", "A3", "D8"));
            Salvo salvo12 = new Salvo(2, Arrays.asList("E1", "F2", "G3"));

            Score score1 = new Score(1, LocalDateTime.now().plusHours(1), game1, player1);
            Score score2 = new Score(0, LocalDateTime.now().plusHours(1), game1, player2);
            Score score3 = new Score(0.5, LocalDateTime.now().plusHours(2), game2, player1);
            Score score4 = new Score(0.5, LocalDateTime.now().plusHours(2), game2, player2);
            Score score5 = new Score(1, LocalDateTime.now().plusHours(3), game3, player2);
            Score score6 = new Score(0, LocalDateTime.now().plusHours(3), game3, player4);
            Score score7 = new Score(0.5, LocalDateTime.now().plusHours(4), game4, player2);
            Score score8 = new Score(0.5, LocalDateTime.now().plusHours(4), game4, player1);

            gp1.addSalvo(salvo1);
            gp1.addSalvo(salvo2);
            gp1.addSalvo(salvo3);
            gp2.addSalvo(salvo4);
            gp2.addSalvo(salvo5);
            gp2.addSalvo(salvo6);
            gp3.addSalvo(salvo7);
            gp3.addSalvo(salvo8);
            gp3.addSalvo(salvo9);
            gp4.addSalvo(salvo10);
            gp4.addSalvo(salvo11);
            gp4.addSalvo(salvo12);

            playerRepository.save(player1);
            playerRepository.save(player2);
            playerRepository.save(player3);
            playerRepository.save(player4);

            gameRepository.save(game1);
            gameRepository.save(game2);
            gameRepository.save(game3);
            gameRepository.save(game4);
            gameRepository.save(game5);

            gamePlayerRepository.save(gp1);
            gamePlayerRepository.save(gp2);
            gamePlayerRepository.save(gp3);
            gamePlayerRepository.save(gp4);
            gamePlayerRepository.save(gp5);
            gamePlayerRepository.save(gp6);
            gamePlayerRepository.save(gp7);
            gamePlayerRepository.save(gp8);
            gamePlayerRepository.save(gp9);

            shipRepository.save(ship1);
            shipRepository.save(ship2);
            shipRepository.save(ship3);
            shipRepository.save(ship6);
            shipRepository.save(ship7);
            shipRepository.save(ship8);
            shipRepository.save(ship9);
            shipRepository.save(ship10);
            shipRepository.save(ship11);

            salvoRepository.save(salvo1);
            salvoRepository.save(salvo2);
            salvoRepository.save(salvo3);
            salvoRepository.save(salvo4);
            salvoRepository.save(salvo5);
            salvoRepository.save(salvo6);
            salvoRepository.save(salvo7);
            salvoRepository.save(salvo8);
            salvoRepository.save(salvo9);
            salvoRepository.save(salvo10);
            salvoRepository.save(salvo11);
            salvoRepository.save(salvo12);

            scoreRepository.save(score1);
            scoreRepository.save(score2);
            scoreRepository.save(score3);
            scoreRepository.save(score4);
            scoreRepository.save(score5);
            scoreRepository.save(score6);
            scoreRepository.save(score7);
            scoreRepository.save(score8);

        };
    }
}
