let url = new URLSearchParams(location.search);
let gp= url.get('gp');

function getData(gpId){

        fetch(`/api/game_view/${gpId}`,{
              method: 'GET'
          }).then(function(respt){
        if(respt.ok)
            return respt.json();
        else
            throw new Error;
        }).then(function(json){
            app.info = json;
            app.mostrarShip();
            app.gp= gp;
            app.mostrarSalvo();
            
        }).catch(function(error){
            console.log(error);
        })
}
getData(gp);

const app = new Vue({
    el:'#app',
    data: {
        info:[],
        gp:''
    }, 
    methods:{
        isHorizontal(list){
            return list[0][0] == list[1][0]? 'horizontal': 'vertical';
        },
        mostrarShip(){
            let ships= app.info.ships;
            for(let x=0; x<ships.length; x++ ){
                createShips(ships[x].type, ships[x].locations.length,app.isHorizontal(ships[x].locations),document.getElementById('ships'+ships[x].locations[0]),true)
            }
        },
        mostrarSalvo(){
            let otro= app.info.salvo;//tengo todos los tiros de ambos jugadores(si es que los hay)
            if(app.info.gamePlayers.length > 1){
                createGrid(11, document.getElementById('grid-salvo'), 'salvo')
                if(otro.length>0){
                    otro.map(user => user.player == app.info.player ? app.salvos(user.location, user.turn, 'salvoMe', 'salvo') :app.salvos(user.location, user.turn, 'salvoOther', 'ships'));
    
                }else{
                    let noGame = document.getElementById('grid-salvo');
                    noGame.innerHTML += `<p>
hasn't started the game..</p>`    
                }
            }else{
                let noGame = document.getElementById('grid-salvo');
                    noGame.innerHTML += `<p>Waiting opponent..</p>`
            }
        },
        salvos(location, turn, style, grid){
            
            for(let x = 0; x< location.length; x++){
                var cell = document.getElementById(grid+location[x]);
                if(style == 'salvoMe'){
                    cell.classList.add(style);
                    cell.innerHTML = turn;
                }
                else{
                    if( cell.classList.length == 1)
                        cell.classList.add(style+'None');
                    else
                        cell.classList.add(style+'Ok');
                cell.innerHTML+=`<p>${turn}</p>`;
                }
                
            }            
        }
    }
})
