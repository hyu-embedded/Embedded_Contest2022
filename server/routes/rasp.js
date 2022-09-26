const express = require('express');
const router = express.Router();
const Sensor = require("../models/Sensor");

const rpiClient = require('../managers/rpiClient');

// start
var id = 0;

var rpiClients = {};

//result
router.get('/', (req, res, next) => {
    res.json({'id': 'yunsang'})
})


router.post('/start', async (req, res, next) => {
    // var {pos, floor, num_of_devices} = req.body;
    
    // id += 1;
    // var client = new rpiClient(id=id, position=pos, num_of_devices=num_of_devices);
    // rpiClients[id] = client.getState();
    // res.send(client.printInfo());
    
    id += 1;
    var pos = req.body.pos;
    var floor = req.body.floor;
    var num_of_devices = req.body.num_of_devices;
    
    console.log(`New clients with id ${id}:\nposition: loc=${pos['loc']}, lat=${pos['lat']}\nfloor=${floor}\nnumber of devices: ${num_of_devices}`);
    

    const newSensor = new Sensor({
        id: id,
        loc: req.body.pos['loc'],
        lat: req.body.pos['lat'],
        floor: req.body.floor,
        waterlevel: 0,
        status: 0,
    });

    try {
        const savedSensor = await newSensor.save();
        //res.status(201).json(savedSensor);
        console.log(savedSensor)
        res.status(201).json({'id': id});
    } catch (err) {
        console.log(err);
        res.status(500).json(err);
    }    

});

// UPDATE
router.post('/result', (req, res, next) => {
    console.log(req.body);
    res.send('Successfully get data');
});

module.exports = router;