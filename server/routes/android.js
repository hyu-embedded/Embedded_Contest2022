const express = require('express');
const router = express.Router();

router.get('/', (req, res, next) => {
    console.log(`Android request info...\n`)

    res.json([
        {id: 0, loc: 35, lat: 120, floor: 1, waterlevel: 24, status: 3, isAssigned: false},
        {id: 1, loc: 35, lat: 121, floor: 2, waterlevel: 24, status: 3, isAssigned: true},
        {id: 2, loc: 35, lat: 122, floor: 4, waterlevel: 42, status: 5, isAssigned: false},
        {id: 3, loc: 35, lat: 123, floor: 3, waterlevel: 52, status: 3, isAssigned: false},
        {id: 4, loc: 35, lat: 124, floor: 5, waterlevel: 30, status: 1, isAssigned: true}
    ])

})


router.get('/login', (req, res, next) => {
    console.log(``)
    var id = req.body.id;
    var password = req.body.password;
    
    // Do login
    if (id == 'yunsang' && password == '1234') {
        res.status(200).send('OK');
    } else {
        res.status(404).send('Not Found');
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



module.exports = router;