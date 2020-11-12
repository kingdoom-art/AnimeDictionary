const express = require("express");
const app = express();
const pg = require('pg');
const conString = 'postgres://postgres:625325@localhost:5432/AnimeInfo';

var client = new pg.Client(conString);
client.connect().then(()=>{
	app.listen(25525, function(){
    console.log("Подключено");
  });
}).catch(err=>console.log(err));

// /get_anime?page=&anime_number=
app.get("/get_anime", function(request, response){
	let page = request.query.page;
	let number = request.query.anime_number;
	client.query('select get_anime_info($1,$2);',[page, number], function(err, res){
		if(err) {
			return console.error('error running query', err);
		}
		response.send(res.rows[0].get_anime_info);
	})
});

app.get("/", function(request, response){
	console.log("/");
	response.send(JSON.stringify({"answer":1}));
	response.end();
});
app.get("/error", function(request, response){
    let  message = request.query.error;
	console.log(message);
});
// /load?page=
app.get("/load", function(request, response){
    let page = request.query.page;
	console.log("load page="+page);
	const spawn = require('child_process').spawn;
	const proc = spawn('./Server/Server/bin/Debug/Server.exe', [page]);
	proc.stdout.once('data', function (data) {
		var result = require('iconv-lite').decode(data, "utf8");
		console.log(result);
		response.end();
	})
});