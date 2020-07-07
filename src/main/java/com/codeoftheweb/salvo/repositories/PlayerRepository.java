package com.codeoftheweb.salvo.repositories;

import com.codeoftheweb.salvo.entities.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findByUserName(@Param("userName") String userName);
    // List<Player> findByLastName(String lastName);
    Player findByEmail(@Param("email") String email);
}
