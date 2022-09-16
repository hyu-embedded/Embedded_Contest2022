const express = require('express');
const router = express.Router();

router.get('/', (req, res, next) => {
    console.log(`Android request info...\n`)

    res.json([
        {id: 0, loc: 35, lat: 120, floor: 1, waterlevel: 24, status: 3},
        {id: 1, loc: 35, lat: 121, floor: 2, waterlevel: 24, status: 3},
        {id: 2, loc: 35, lat: 122, floor: 4, waterlevel: 42, status: 5},
        {id: 3, loc: 35, lat: 123, floor: 3, waterlevel: 52, status: 3},
        {id: 4, loc: 35, lat: 124, floor: 5, waterlevel: 30, status: 1}
    ])

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