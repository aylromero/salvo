const app = new Vue({
    el: '#app',
    data: {
        games: [],
        gamePlayers: [],
        players: []
    },
    created: () => {
        let url = "http://localhost:8080/api/game"
        fetch(url, {
            method: 'GET'
        }).then(function(res) {
            if (res.ok) return res.json();
            else throw new Error;
        }).then(function(json) {
            data = json;
            app.games = data
            app.allPlayers()
        }).catch(function(err) {
            console.log(err);
        })
    },
    methods: {
        allPlayers() {
            this.players = [];
            let aux = 0;
            let playerControl = [];
            app.allGamePlayers();
            this.gamePlayers.forEach(gp => {
                if (gp.score != null) {
                    if (!playerControl.includes(gp.player.id)) {
                        playerControl.push(gp.player.id);
                        let player = {
                            idPlayer: null,
                            userName: null,
                            gamesWon: null,
                            gamesLost: null,
                            gamesTie: null,
                            scoreTotal: 0
                        };
                        player.idPlayer = gp.player.id;
                        player.userName = gp.player.userName;
                        player.scoreTotal += gp.score;
                        if (gp.score == 1.0) {
                            player.gamesWon = 1;
                            player.gamesLost = 0;
                            player.gamesTie = 0;
                        } else {
                            if (gp.score == 0.5) {
                                player.gamesWon = 0;
                                player.gamesLost = 0;
                                player.gamesTie = 1;
                            } else if (gp.score == 0.0) {
                                player.gamesWon = 0;
                                player.gamesLost = 1;
                                player.gamesTie = 0;
                            }
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
        getPlayer(playerId) {
            let i = 0;
            while (i < this.players.length) {
                if (this.players[i].idPlayer == playerId) return i;
                i++;
            }
        },
        allGamePlayers() {
            this.gamePlayers = [];
            this.games.forEach(game => game.gamePlayers.filter(gp => this.gamePlayers.push(gp)));
        },
        scoreByPlayer(playerId) {
            let scoreTotal = 0;
            this.gamePlayers.forEach(gp => gp.player.id == playerId ? scoreTotal += gp.score : null);
            return scoreTotal;
        }
    }
});