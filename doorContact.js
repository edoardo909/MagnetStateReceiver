var onoff = require('onoff'); //#A
var admin = require('firebase-admin')
var express = require('express')
var server = express();
var serviceAccount = require('./MagnetStateReceiver-16008d26d1a8.json');
var mongoClient = require('mongodb').MongoClient;
var bodyParser = require('body-parser');

var dbURL = 'mongodb://localhost:27017/AndroidTokens';

admin.initializeApp({
	credential: admin.credential.cert(serviceAccount),
	databaseURL: "https://magnetstatereceiver.firebaseio.com/"
});

server.use(bodyParser.json());
server.use(bodyParser.urlencoded({
	extended: true
}));

var Gpio = onoff.Gpio,
  magnets = new Gpio(19, 'in', 'none'), //#B
  interval;
var wasNotificationSent = false;

interval = setInterval(function () { //#C
	getFirebaseToken();
}, 1000);


function myIntervalTimer(firebaseToken){
	var value = magnets.readSync();
	console.log("Magnet state is: ", value)
	if(value == 0 && wasNotificationSent == false){
		console.log("FirebaseToken: ", firebaseToken)
		sendDoorOpenNotification(firebaseToken);
		wasNotificationSent = true;
        	console.log("sent magnet data");
		console.log("FRIDGE ALARM!!");
	}
	if(value == 1){
		wasNotificationSent = false;
		console.log("fridge door is closed, we are saved");
	}

}

process.on('SIGINT', function () { //#F
  clearInterval(interval);
  magnets.unexport();
  console.log('Bye, bye!');
  process.exit();
});

server.get("/", function(request, response){
	console.log("Home");
	response.send("HomePage");
});

server.post("/token", function(request, response){
	saveToken(request);
	response.send("receiving token");
});

function saveToken(request){
	mongoClient.connect(dbURL, function(error, db){
	var collection = db.collection('token');
	if(error) throw error;
	var requestBodyObject = eval(request.body);
	for(key in requestBodyObject){
		collection.insert({'token':key}, function (error, result){
		if(error) throw error;
		console.log(result);	
		})
	}
	})
}

function getFirebaseToken(){
	if (wasNotificationSent == true){
		myIntervalTimer();
		return;
	}else{
		mongoClient.connect(dbURL, function(error, db){
			if(error) throw error;
			var collection = db.collection('token');
			collection.find({},{"_id":0}).toArray(function(error, dataArray){
				if (error) return error;
				var token = new Array;
				for (var playerID in dataArray){
					token[playerID] = dataArray[playerID].token;
				}
				myIntervalTimer(token);
			})
		})
	}
}

var payload = {
	notification: {
		title: "The Fridge was Opened!!!!",
		body: "Release the hounds!!!"
	}
}

var options = {
	priority: "high",
	timeToLive: 60*60*24
}

function sendDoorOpenNotification(firebaseToken){
	admin.messaging().sendToDevice(firebaseToken, payload, options).then(function(response){
		console.log("Successfully sent message: ", response);
	}).catch(function(error){
		console.log("Error sending message: ", response);
	});
}

server.listen(8888, '192.168.1.116' , function(){
	console.log("Server started on 192.168.1.116:8888/");
});

