const express = require('express');
const router = express.Router();

var testData = {
    'count': 2,
    '0': {id: 0, lat: 37.556132, loc: 127.049934, floor: 1, waterlevel: 56, status: 2, isAssigned: false},
    '1': {id: 1, lat: 37.556945, loc: 127.049108, floor: 2, waterlevel: 38, status: 1, isAssigned: false},
}

const validate_user = (id, password) => {


}

const send_login_result = () => {

}

const send_neighbor_info = (loc, lat, zoom) => {


}

const update_database = (id, status) => {
    
}



router.get('/', (req, res, next) => {
    console.log(`Android request info...\n`)

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

    res.json(testData);

});


router.get('/update', (req, res, next) => {
    var id = req.query.id;
    
    testData[`${id}`].isAssigned = true;
    console.log(`testData: ${JSON.stringify(testData)}`)
    res.json(testData);
});


router.get('/done', (req, res, next) => {
    var id = req.query.id;

    testData[`${id}`].status = -1;
    res.json(testData);
})


module.exports = router;