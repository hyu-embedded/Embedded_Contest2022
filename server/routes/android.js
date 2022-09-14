const express = require('express');
const router = express.Router();


router.get('/search', (req, res, next) => {
    
    var id = req.body.id;
    var pos = req.body.pos;
    var distance = req.body.distance;

    console.log(`Client${id} request searching...\n
    position: position: loc=${pos['loc']}, lat=${pos['lat']}\n
    distance: ${distance}`);




});



module.exports = router;