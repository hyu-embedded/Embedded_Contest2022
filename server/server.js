const express = require('express');
const app = express();
const mongoose = require('mongoose');
const dotenv = require('dotenv');

const bodyParser = require('body-parser')
const cors = require('cors')

const indexRouter = require('./routes/index')
const raspRouter = require('./routes/rasp')
const androidRouter = require('./routes/android')

dotenv.config();

 
async function main() {
  await mongoose.connect(process.env.MONGO_URL);
}

main()
    .then(()=>console.log("DB connection Successfull!!"))
    .catch(err => console.log(err));



app.use(bodyParser.urlencoded({extended: true}))
app.use(bodyParser.json())
app.use(cors());

app.use(indexRouter);
app.use('/rasp', raspRouter);


const port = process.env.PORT || 3000;


app.listen(port, () => {
    console.log(`Executing server...http://localhost:${port}`)
});

