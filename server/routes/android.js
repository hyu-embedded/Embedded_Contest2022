const express = require('express');
const router = express.Router();
const Sensor = require("../models/Sensor");
const User = require("../models/User");

const validate_user = async (username, password) => {
    const users = await User.aggregate([
        {$match: {"username": username, "password": password}}
    ]);

    if (users.length == 0) {
        return false;
    }
    return true;
}

const send_login_result = async (username, password) => {
    if (validate_user(username, password)) {
        return {"status":"ok"};
    } else {
        return {"status":"error"};
    }
}

const send_neighbor_info = async (loc, lat, zoom) => {
    const diff = Math.pow(2, 20 - zoom)

    const neighbors = Sensor.find({
        loc: {
            $gte: target_client.loc - diff,
            $lt: target_client.loc + diff
        },
        lat: {
            $gte: target_client.lat - diff,
            $lt: target_client.lat + diff
        },
    })

    var result = {
        'count': neighbors.length
    }

    var idx = 0;
    neighbors.foreach((neighbor) => {
        result[`${idx}`] = neighbor;
        idx += 1;
    });

    return result;
}

const update_database = async (username, status) => {

    try {

        const target_client = await Sensor.findOne({"username": username});
        target_client.status = status;
        await target_client.save();
        return true;

    } catch(err) {
        return false;
    }

    return false;
    
}



router.get('/', (req, res, next) => {
    console.log(`Android request info...\n`)

})


router.get('/login', (req, res, next) => {
    console.log(`Login request:\nID: ${req.query.id}, Password: ${req.query.password}`)
    var id = req.body.id;
    var password = req.body.password;
    
    res.json(send_login_result(id, password));

})

router.get('/search', (req, res, next) => {
    
    var lat = req.query.lat;
    var loc = req.query.loc;
    var zoom = req.query.zoom;

    if (testData[0].waterlevel < 100) {
        testData[0].status = 2
        testData[0].waterlevel += Math.floor(Math.random() * 10)
    } else {
        if (testData[0].status == 2) {
            testData[0].status = 3;
        }
    }

    res.json(send_neighbor_info(loc, lat, zoom));
});


router.get('/update', (req, res, next) => {
    var id = req.query.id;
    
    update_database(id, -1);
});


router.get('/done', (req, res, next) => {
    var id = req.query.id;

    update_database(id, 1);
})


module.exports = router;