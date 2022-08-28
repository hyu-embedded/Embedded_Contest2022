const express = require("express");
const router = express.Router();

var username = 'cho';


router.get("/", function (req, res, next) {
    res.send({'username': username});
});

router.post("/", (req, res, next) => {
    username = req.body.name;
    console.log(req.body);
    res.send(`Successfully get username: ${username}`);
})

module.exports = router;