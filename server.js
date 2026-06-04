const express = require("express");

const app = express();

app.get("/", (req, res) => {
    res.send("Welcome to NodeJS CI/CD Pipeline Demo and Dockerization");
});

app.get("/health", (req, res) => {
    res.status(200).json({
        status: "UP"
    });
});

const PORT = process.env.PORT || 3000;

app.listen(PORT, () => {
    console.log(`Application running on port ${PORT}`);
});

module.exports = app;
