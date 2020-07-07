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
            let otro= app.info.salvo
            if()
        }
    },
    computed: {
        mostrarInfoGame(){
            let view =[];
            
        }
    }
})
