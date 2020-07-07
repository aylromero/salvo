setTimeout(function() {
    document.getElementById('splash').style['display'] = 'none';
    document.getElementById('app').style['display'] = 'block';
}, 1800);
const app = new Vue({
    el: '#app',
    data: {
        games: [],
        gamePlayers: [],
        players: [],
        user: {},
    },
    created() {
        this.getGames();
    },
    methods: {
        getGames() {
            let url = "http://localhost:8080/api/games"
            fetch(url, {
                method: 'GET'
            }).then(function(respt) {
                if (respt.ok) {
                    return respt.json();
                } else throw new Error;
            }).then(function(json) {
                data = json;
                app.games = data["games"].reverse();
                app.user = data["player"];
                app.allPlayersWithGames();
                app.signIn();
            }).catch(function(error) {
                console.log(error);
            })
        },
        allPlayersWithGames() {
            this.players = [];
            let aux = 0;
            let auxPlayerControl = [];
            app.allGamePlayers();
            this.gamePlayers.forEach(gp => {
                if (gp.score != null) {
                    if (!auxPlayerControl.includes(gp.player.id)) {
                        auxPlayerControl.push(gp.player.id);
                        let player = {
                            idPLayer: null,
                            userName: null,
                            gamesWon: null,
                            gamesLost: null,
                            gamesTie: null,
                            scoreTotal: 0
                        };
                        player.idPLayer = gp.player.id;
                        player.userName = gp.player.userName;
                        player.scoreTotal += gp.score;
                        switch (gp.score) {
                            case 1.0:
                                player.gamesWon = 1;
                                player.gamesLost = 0;
                                player.gamesTie = 0;
                                break;
                            case 0.5:
                                player.gamesWon = 0;
                                player.gamesLost = 0;
                                player.gamesTie = 1;
                                break;
                            case 0.0:
                                player.gamesWon = 0;
                                player.gamesLost = 1;
                                player.gamesTie = 0;
                        }
                        this.players.push(player);
                    } else {
                        aux = app.getPlayer(gp.player.id);
                        this.players[aux].scoreTotal += gp.score;
                        gp.score == 1.0 ? this.players[aux].gamesWon += 1 : (gp.score == 0.5 ? this.players[aux].gamesTie += 1 : this.players[aux].gamesLost += 1)
                    }
                }
            })
        },
        getPlayer(iDplayer) {
            let i = 0;
            while (i < this.players.length) {
                if (this.players[i].idPLayer == iDplayer) return i;
                i++;
            }
        },
        allGamePlayers() {
            this.gamePlayers = [];
            this.games.forEach(game => game.gamePlayers.filter(gp => this.gamePlayers.push(gp)));
        },
        signIn() {
            if (app.user != 'Guest') {
                document.getElementById('userNow').innerHTML = `<h2 class="ml-1 mt-2 tracking-in-expand">Hello,  ${this.user.userName}!</h2>`
                document.getElementById('barra').classList.remove('d-none');
                document.getElementById("btn-login").classList.add('d-none');
                document.getElementById('imglogo').style.display = 'none';
            }
        },
        showLogIn() {
            if (app.user == 'Guest') {
                document.getElementById('barra').classList.add('d-none');
                document.getElementById("btn-login").classList.remove('d-none');
            }
        },
        register() {
            const data = new FormData(document.getElementById("register"));
            fetch('/api/players', {
                method: 'POST',
                body: data
            }).then(function(response) {
                if (response.ok) {
                    let userNew = document.getElementsByName('userName')[0].value;
                    document.getElementsByName('username')[0].value = userNew;
                    $('#signUp').modal('hide');
                    $('#logIn').modal('show');
                    app.showLogIn();
                    return response.text();
                } else {
                    return response.json();
                }
            }).then(function(texto) {
                console.log(texto)
            }).catch(function(err) {
                alert(err);
            });
        },
        showSignUp() {
            $(logIn).modal('hide');
        },
        logOut() {
            const data = new FormData(document.getElementById("form-logOut"));
            fetch('/api/logout', {
                method: 'POST',
                body: data
            }).then(function(response) {
                if (response.ok) {
                    app.user = 'Guest'
                    app.getGames()
                    app.showLogIn()
                    return response.text()
                } else {
                    return response.json()
                }
            }).then(function(texto) {
                console.log(texto);
            }).catch(function(err) {
                console.log(err);
            });
        },
        sendDate() {
            const data = new FormData(document.getElementById("form-logIn"));
            fetch('/api/login', {
                method: 'POST',
                body: data
            }).then(function(response) {
                if (response.ok) {
                    app.getGames();
                    document.querySelectorAll('.input').forEach(x => x.value = "");
                    $('#logIn').modal('hide');
                    $("#btn-newGame").tooltip('disable');
                    return response.json()
                } else {
                    return response.json()
                }
            }).then(function(texto) {
                alert(texto.message)
            }).catch(function(err) {
                console.log(err)
            });
        },
        createGame() {
            if (app.user != 'Guest') {
                fetch('/api/games', {
                    method: 'POST'
                }).then(function(response) {
                    if (response.ok) {
                        return response.json()
                    } else {
                        return Promise.reject(response.json());
                    }
                }).then(function(texto) {
                    location.href = '/web/game.html?gp=' + texto.idGp;
                }).catch(function(error) {
                    alert(error);
                })
            } else {
                $("#btn-newGame").tooltip('enable');
                $("#btn-newGame").tooltip('show');
            }
        },
        joinGame(gameId) {
            fetch('/api/games/' + gameId + '/players', {
                method: 'POST'
            }).then(function(response) {
                if (response.ok) {
                    return response.json()
                } else {
                    return Promise.reject(response.json());
                }
            }).then(function(texto) {
                location.href = '/web/game.html?gp=' + texto.idGp;
            }).catch(error => error).then(error => console.log(error))
        },
        viewGame(gamePlayers) {
            if (gamePlayers[0].player.userName == app.user.userName) location.href = '/web/game.html?gp=' + gamePlayers[0].idGp;
            else location.href = '/web/game.html?gp=' + gamePlayers[1].idGp;
        },
        tableGame() {
            let texto = `<div class = "blocks-game row" v-for = "game in games">
                    <div class="division col-4">
                      <p>Game: {{game.id}}</p>
                      <p>{{game.date}}</p>
                      <p>{{game.hour}}</p>
                    </div>
                    <div class="col-5">
                      <ul>
                          <li>{{game.startCreator}}</li>
                          <li v-if = "gp.player.userName != game.startCreator" v-for = "gp in game.gamePlayers" >{{gp.player.userName}}</li>
                      </ul>
                   </div>
                   <div class="col-3">
                      <div id="button-Game">
                          {{viewButton(game.id, game.startCreator, game.gamePlayers)}}
                      </div>
                   </div>
                </div>`;
            document.getElementById("games").innerHTML += texto;
        },
        viewButton(idGame, playerStartCreator, gamePlayersGame) {
            let button = document.getElementById("buttonGame");
            let texto = "";
            if (gamePlayersGame[0].score != null) {
                texto += `<p>Game Over</p>`
                let result = gamePlayersGame[0].score;
                if (result == 0.5) texto += `<h2>Tied!</h2>`;
                else if (result == 1.0) {
                    texto += `<h2>Win : {{game.gamePlayers[0].player.userName}}</h2>`
                } else {
                    texto += `<h2>Win : {{game.gamePlayers[0].player.userName}}</h2>`
                }
            } else if (gamePlayersGame.length == 1) {
                if (app.user.userName == playerStartCreator) {
                    texto += `<button type="button" @click = viewGame(user.id)> View Game</button>`
                } else {
                    texto += `<button type="button" @click = 'joinGame(idGame)'>Join Game</button>`
                }
            } else {
                if (gamePlayersGame[0].player.userName == user.userName || gamePlayersGame[1].player.userName == user.userName) texto += `<button type="button" @click = viewGame(user.id)> View Game</button>`
                else texto += `<h2>In course!</h2>`;
            }
            button.innerHTML = texto;
        }
    }
});