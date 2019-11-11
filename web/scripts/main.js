var url = new URLSearchParams(location.search);
var gp = url.get('gp');

function getData(gpId) {
    fetch(`/api/game_view/${gpId}`, {
            method: 'GET'
        }).then(function(res){
    if(res.ok)
        return res.json();
    else
        throw new Error;
    }).then(function(json) {
        app.info = json;
        app.showShip();
        app.gp= gp;
        app.showSalvo();
            
     }).catch(function(err) {
        console.log(err);
    })
}
getData(gp);

const app = new Vue ({
    el:'#app',
    data: {
        info:[],
        gp:''
    }, 
    methods:{
        isHorizontal(list) {
            return list[0][0] == list[1][0]? 'horizontal': 'vertical';
        },
        showShip() {
            let ships= app.info.ships;
            for(let x=0; x<ships.length; x++ ){
                createShips(ships[x].type, ships[x].locations.length,app.isHorizontal(ships[x].locations),document.getElementById('ships'+ships[x].locations[0]),true)
            }
        },
        showSalvo() {
            let otro= app.info.salvo;
            if(app.info.gamePlayers.length > 1){
                createGrid(11, document.getElementById('grid-salvo'), 'salvo')
                if(otro.length>0){
                    otro.map(user => user.player == app.info.player ? app.salvos(user.location, user.turn, 'salvoY', 'salvo') : app.salvos(user.location, user.turn, 'salvoOpp', 'ships'));
    
                }else{
                    let noGame = document.getElementById('grid-salvo');
                    noGame.innerHTML += `<b>the game didn't start..</b>`    
                }
            }else{
                let noGame = document.getElementById('grid-salvo');
                    noGame.innerHTML += `<b>waiting for your opponent</b>`
            }
        },
        salvos(location, turn, style, grid) {
            for(let x = 0; x< location.length; x++){
                var cell = document.getElementById(grid+location[x]);
                if(style == 'salvoY'){
                    cell.classList.add(style);
                    cell.innerHTML = turn;
                }else{
                       if ( cell.classList.length == 1)
                        cell.classList.add(style+'None');
                    else
                        cell.classList.add(style+'Ok');
                  cell.innerHTML+=`<p>${turn}</p>`;
                }    
            }            
        }
    }
})
