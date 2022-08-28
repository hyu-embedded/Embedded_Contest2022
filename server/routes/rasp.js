const express = require('express');
const router = express.Router();

const rpiClient = require('../managers/rpiClient');

// start
var id = 0;

var rpiClients = {};

//result

router.post('/start', (req, res, next) => {
    var {pos, floor, num_of_devices} = req.body;
    
    id += 1;
    var client = new rpiClient(id=id, position=pos, num_of_devices=num_of_devices);
    rpiClients[id] = client.getState();
    res.send(client.printInfo());
    console.log(`New clients with id ${id}:\nposition: loc=${pos['loc']}, lat=${pos['lat']}\nfloor=${floor}\nnumber of devices: ${num_of_devices}`);
});

router.post('/result', (req, res, next) => {
    console.log(req.body);
    res.send('Successfully get data');
});

module.exports = router;