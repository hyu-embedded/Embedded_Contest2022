const express = require('express');
const app = express();
const port = 3000;      // Server port number

var data = 1;

app.get('/', (req, res) => {
    res.json({ 'data': data});
});

app.listen(port, () => {
    console.log(`Executing server...http://localhost:${port}`)
});