const express = require('express');
const { Model } = require('mongoose');
const router = express.Router();
const Sensor = require("../models/Sensor");

// start
var id = 0;

var rpiClients = {};

const create_new_client = async (lat, loc, floor) => {
    
    // Get ID
    var new_id = 1;
    Sensor.find().sort("-id").limit(1).exec((err, val)=>{
        if (err) {
            console.log(err);
        } else {
           new_id = val[0].id + 1; 
        }
    })

    const new_sensor = new Sensor({
        id: new_id,
        loc: lat,
        lat: loc,
        floor: floor,
        waterlevel: 0,
        status: 1,
    });

    return {
        id: new_id,
        sensor: new_sensor
    };
}

const update_database = async (msg_type, id, target_sensor) => {
    
    if (msg_type == 'new_sensor') {

        try {
            await target_sensor.save();
            return true;
    
        } catch (err){
            console.log(err);
            return false;
        }
    } else if (msg_type == 'update') {
        try {
            const target_client = await Sensor.findOne({"id": id});
            target_client.waterlevel = target_sensor.waterlevel;
            await target_client.save();
            return true;

        } catch (err) {
            console.log(err);
            return false;
        }
    }
    return false;
}

const send_client_id = async () => {
    var id = -1;
    Sensor.find().sort("-id").limit(1).exec((err, val)=>{
        if (err) {
            console.log(err);
        } else {
           id = val[0].id; 
        }
    })
    return id;
}

const validate_data = async (id, waterlevel, diff_loc, diff_lat, alpha) => {
    const target_client = await Sensor.findById(id)
    const previous_waterlevel = target_client.waterlevel

    if (Math.abs(target_client.waterlevel - waterlevel) > alpha * target_client.waterlevel) {
        return false;
    }

    const neighbors = Sensor.find({
        loc: {
            $gte: target_client.loc - diff_loc,
            $lt: target_client.loc + diff_loc
        },
        lat: {
            $gte: target_client.lat - diff_lat,
            $lt: target_client.lat + diff_lat
        },
    }).filter(Math.abs(target_client.waterlevel - waterlevel) > alpha * target_client.waterlevel);

    if (neighbors.length > 0) {
        return false;
    }

    return true;
}

router.get('/start', async (req, res, next) => {
        
    var lat = req.body.pos.lat;
    var loc = req.body.pos.loc;
    var floor = req.body.floor;
    var timestamp = req.body.timestamp;
    
    console.log(`New clients with id ${id}:\nposition: loc=${pos['loc']}, lat=${pos['lat']}\nfloor=${floor}\nnumber of devices: ${num_of_devices}`);
    
    const new_client_info = create_new_client(lat, loc, floor);
    const new_id = (await new_client_info).id;
    const new_client = (await new_client_info).sensor;

    if (update_database("new_sensor", new_id, new_client)) {
        res.json({'id': send_client_id()})
    } else {
        res.json({'id': -1})
    }
    
});

// UPDATE
router.post('/update', (req, res, next) => {
    //res.send('Successfully get data');
    const target_id = req.body.id;
    const target_waterlevel = req.body.waterlevel;
    const target_sensor = Sensor.findOne({"id": target_id});

    if (validate_data(target_id, target_waterlevel, 1.0, 1.0, 0.8)) {
        update_database("update", target_id, target_sensor);
    }
});

module.exports = router;