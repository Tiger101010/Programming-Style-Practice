class TheOne {
    constructor(value) {
        this._value = value;
    }

    bind(func) {
        this._value = func(this._value);
        return this;
    }
}

function readFile(path) {
    var fs = require("fs");
    var data = fs.readFileSync(path, "utf8", (error, data) => {
        if(error) {
            throw error;
        }});
    return data;
}

function splitWords(data) {
    var words = data.toLowerCase()
                .replace(/[^0-9a-z]/gi, ' ')
                .split(" ");
    return words;
}


function filterStopWords(words) {
    var fs = require("fs");
    stopWords = fs.readFileSync("../stop_words.txt", "utf8", (error, data) => {
        if(error) {
            throw error;
        }}).toLowerCase().split(",")
    const wordsWOStopwords = words.filter(word => {
        return (!(stopWords.includes(word)) && word.length > 1)
        });
    return wordsWOStopwords;
}

function frequencyCount(wordsWOStopwords) {
    var wf = {};
    wordsWOStopwords.forEach(word => {
        if(word in wf) {
            wf[word] += 1;
        } else {
            wf[word] = 1;
        }
    });
    return wf;
}

function sortFrequency(wf) {
    sortedWf = Object.entries(wf).sort((a, b) => b[1] - a[1]);
    return sortedWf;
}

function printResult(wf) {
    for ( [key, value] of wf.slice(0,25)) {
        console.log(`${key}   -   ${value}`);
    }
    return wf;
}

new TheOne(process.argv[2])
.bind(readFile)
.bind(splitWords)
.bind(filterStopWords)
.bind(frequencyCount)
.bind(sortFrequency)
.bind(printResult);

