const app = new Vue({
    el: '#app',
    data: {
        games: []
    },
    created: () => {
        let url = "http://localhost:8080/api/game"
      fetch(url, {
          method: 'GET'
      }).then(function(respt){
          if(respt.ok)
              return respt.json();
          else
              throw new Error;
      }).then(function(json){
          data = json;
          app.games=data
      }).catch(function(error){
          console.log(error);
      }) 
    }
});