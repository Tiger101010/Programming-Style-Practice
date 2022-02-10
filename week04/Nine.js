function readFile(path, func) {
    var fs = require("fs");
    var data = fs.readFileSync(path, "utf8", (error, data) => {
        if(error) {
            throw error;
        }});
    func(data, filterStopWords);
}

function splitWords(data, func) {
    var words = data.toLowerCase()
                .replace(/[^0-9a-z]/gi, ' ')
                .split(" ");
    func(words, frequencyCount);
}


function filterStopWords(words, func) {
    var fs = require("fs");
    stopWords = fs.readFileSync("../stop_words.txt", "utf8", (error, data) => {
        if(error) {
            throw error;
        }}).toLowerCase().split(",")
    const wordsWOStopwords = words.filter(word => {
        return (!(stopWords.includes(word)) && word.length > 1)
        });
    func(wordsWOStopwords, sortFrequency);
}

function frequencyCount(wordsWOStopwords, func) {
    var wf = {};
    wordsWOStopwords.forEach(word => {
        if(word in wf) {
            wf[word] += 1;
        } else {
            wf[word] = 1;
        }
    });
    func(wf, printResult);
}

function sortFrequency(wf, func) {
    sortedWf = Object.entries(wf).sort((a, b) => b[1] - a[1]);
    func(sortedWf);
}

function printResult(wf) {
    for ( [key, value] of wf.slice(0,25)) {
        console.log(`${key}   -   ${value}`);
    }
}

// main function
readFile(process.argv[2], splitWords);