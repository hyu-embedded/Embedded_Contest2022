const express = require('express');
const router = express.Router();

var testData = {
    'count': 2,
    '0': {id: 0, lat: 37.421946, loc: -122.084573, floor: 1, waterlevel: 24, status: 5, isAssigned: false},
    '1': {id: 1, lat: 37.422182, loc: -122.0826, floor: 2, waterlevel: 24, status: 3, isAssigned: true},
}


router.get('/', (req, res, next) => {
    console.log(`Android request info...\n`)

    // res.json([
    //     {id: 0, loc: 35, lat: 120, floor: 1, waterlevel: 24, status: 3, isAssigned: false},
    //     {id: 1, loc: 35, lat: 121, floor: 2, waterlevel: 24, status: 3, isAssigned: true},
    //     {id: 2, loc: 35, lat: 122, floor: 4, waterlevel: 42, status: 5, isAssigned: false},
    //     {id: 3, loc: 35, lat: 123, floor: 3, waterlevel: 52, status: 3, isAssigned: false},
    //     {id: 4, loc: 35, lat: 124, floor: 5, waterlevel: 30, status: 1, isAssigned: true}
    // ])
    console.log(`Query: ${req.query.lat}`)

    res.json(testData)

})


router.get('/login', (req, res, next) => {
    console.log(`Login request:\nID: ${req.query.id}, Password: ${req.query.password}`)
    var id = req.body.id;
    var password = req.body.password;
    
    // Do login
    if (req.query.id == 'yunsang' && req.query.password == '1234') {
        console.log("okokok");
        res.json({"status":"ok"})
    } else {
        res.json({"status":"error"})
    }

})

router.get('/search', (req, res, next) => {
    
    var id = req.body.id;
    var pos = req.body.pos;
    var distance = req.body.distance;

    console.log(`Client${id} request searching...\n
    position: position: loc=${pos['loc']}, lat=${pos['lat']}\n
    distance: ${distance}`);

});



router.get('/update', (req, res, next) => {
    var id = req.query.id;
    
    testData[`${id}`].status = -1;
    console.log(`testData: ${JSON.stringify(testData)}`)
    res.json(testData);
});



module.exports = router;