var info;
var url = new URLSearchParams(location.search);
var gp = url.get('gp');

function getData(gpId) {
    document.getElementById('dock').innerHTML = `<div id="display">
                                                    </div>    
                                                    <div id="shooting">
                                                        </div>`;
    document.getElementById("datosPartida").innerHTML = "";
    document.getElementById('grid').innerHTML = "";
    createGrid(11, document.getElementById('grid'), 'ships')
    fetch(`/api/game_view/${gpId}`, {
        method: 'GET'
    }).then(function(res) {
        if (res.ok) return res.json();
        else throw new Error;
    }).then(function(json) {
        info = json;
        gp = gp;
        showData();
        showShip();
        showGrid();
        showSalvo();
    }).catch(function(err) {
        console.log(err);
    })
}
getData(gp);
addSalvoesMe();

function showData() {
    let datos = document.getElementById("datosPartida");
    let aux = `<p>Game: ${info.idGame}&nbsp;</p>
               <p>&nbsp;${info.startCreator} vs</p>`;
    for (let i = 0; i < info.gamePlayers.length; i++) {
        if (info.gamePlayers[i].player.userName != info.startCreator) aux += `<p> &nbsp;${info.gamePlayers[i].player.userName} (you)</p>`;
    }
    datos.innerHTML += aux;
}

function isHorizontal(list) {
    return list[0][0] == list[1][0] ? 'horizontal' : 'vertical';
}

function showShip() {
    let ships = info.ships;
    if (ships.length > 0) {
        for (let x = 0; x < ships.length; x++) {
            createShips(ships[x].type, ships[x].locations.length, isHorizontal(ships[x].locations), document.getElementById('ships' + ships[x].locations[0]), true)
        }
    } else {
        document.getElementById('display').innerHTML += `<p> Welcome...</p>`
        document.getElementById("shooting").innerHTML += '<div><button type="button" id="btn-AddShip" onclick="getShipsLocations()" disabled>Ready</button></div>'
        createShips('carrier', 5, 'horizontal', document.getElementById('dock'), false)
        createShips('battleship', 4, 'horizontal', document.getElementById('dock'), false)
        createShips('submarine', 3, 'horizontal', document.getElementById('dock'), false)
        createShips('destroyer', 3, 'horizontal', document.getElementById('dock'), false)
        createShips('patrol_boat', 2, 'horizontal', document.getElementById('dock'), false)
    }
}

function showGrid() {
    if (info.gamePlayers.length > 1) {
        if (info.ships.length > 0) {
            document.getElementById('grid-salvo').style.display = "block";
            document.getElementById('shooting').innerHTML += `<div class="btn-Fire mt-5"><button type="button" id="btn-Fire" onclick="getSalvoes()">Fire!</button></div>`
        } else document.getElementById('grid-salvo').style.display = "none";
    } else {
        document.getElementById('display').innerHTML = `<p>Waiting opponent..</p>`;
        document.getElementById('grid-salvo').style.display = "none";
    }
}

function showSalvo() {
    let salvoes = info.salvo;
    let sunken = [];
    if (salvoes.length > 0) {
        info.salvo.forEach(shoot => info.player == shoot.player ? (shoot.sunken.length > 0 ? (shoot.sunken.forEach(sunk => sunk.locations.forEach(x => !sunken.includes(x) ? sunken.push(x) : null))) : null) : null)
        salvoes.map(user => user.player == info.player ? salvos(user, sunken, 'salvoBD', 'salvo') : salvos(user, sunken, 'salvoOther', 'ships'));
    }
}

function salvos(obj, sunks, style, grid) {
    let hits = [];
    for (let x = 0; x < obj.location.length; x++) {
        var cell = document.getElementById(grid + obj.location[x]);
        if (style == 'salvoBD') {
            cell.classList.remove('shootEnabled');
            if (obj.hits.includes(obj.location[x])) {
                if (sunks.includes(obj.location[x])) {
                    cell.classList.add('salvoBD_sunken')
                } else cell.classList.add('salvoBD_hits')
            } else cell.classList.add(style)
            cell.innerHTML = obj.turn;
        } else {
            if (cell.classList.length == 1) cell.classList.add(style + 'None');
            else cell.classList.add(style + 'Ok');
            cell.innerHTML += `<p>${obj.turn}</p>`;
        }
    }
}

function getShipsLocations() {
    let ships = [];
    document.querySelectorAll(".grid-item").forEach(cell => {
        let ship = {
            "type": cell.id
        };
        ship.locations = [];
        if (cell.dataset.orientation == "horizontal") {
            for (i = 0; i < cell.dataset.length; i++) {
                ship.locations.push(cell.dataset.y + (parseInt(cell.dataset.x) + i))
            }
        } else {
            for (i = 0; i < cell.dataset.length; i++) {
                ship.locations.push(String.fromCharCode(cell.dataset.y.charCodeAt() + i) + cell.dataset.x)
            }
        }
        ships.push(ship);
    });
    sendShips(gp, ships);
}

function sendShips(gamePlayerId, ships) {
    let url = '/api/games/players/' + gamePlayerId + '/ships';
    let init = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(ships)
    }
    fetch(url, init).then(res => {
        if (res.ok) {
            return res.json()
        } else {
            return Promise.reject(res.json())
        }
    }).then(json => {
        getData(gp)
    }).catch(function(err) {
        console.log(err);
    }).then(function(json) {
        alert(json["error"])
    })
}

function addSalvoesMe() {
    for (let i = 0; i < 10; i++) {
        for (let j = 0; j < 10; j++) {
            document.getElementById('salvo' + String.fromCharCode(i + 65) + (j + 1)).addEventListener('click', function(event) {
                addClassAtSalvo(event)
            });
        }
    }
}

function addClassAtSalvo(ev) {
    let id = ev.target.id;
    let cell = ev.target;
    if (cell.classList.contains('shootEnabled')) {
        if (document.querySelectorAll('.salvoMe').length < 5 || document.getElementById(id).classList.contains('salvoMe')) document.getElementById(id).classList.toggle('salvoMe')
    };
}

function getSalvoes() {
    let salvoes = [];
    document.querySelectorAll('.salvoMe').forEach(cell => {
        salvoes.push(String.fromCharCode(cell.dataset.y.charCodeAt()) + cell.dataset.x)
    })
    sendSalvoes(gp, salvoes);
}

function sendSalvoes(gamePlayerId, salvoes) {
    let url = '/api/games/players/' + gamePlayerId + '/salvoes';
    let init = {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(salvoes)
    }
    fetch(url, init).then(res => {
        if (res.ok) {
            return res.json()
        } else {
            return Promise.reject(res.json())
        }
    }).then(json => {
        document.querySelectorAll('.salvoMe').forEach(cell => {
            cell.classList.remove('salvoMe')
        });
        getData(gp)
    }).catch(function(err) {
        console.log(err);
    })
}