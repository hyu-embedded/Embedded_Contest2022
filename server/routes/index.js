const express = require("express");
const router = express.Router();

var data = 1;

router.get("/", function (req, res, next) {
    res.send({'data': data});
});

router.post("/", (req, res, next) => {
    data = req.body.data;
    res.send(`Successfully get data: ${data}`);
})

module.exports = router;