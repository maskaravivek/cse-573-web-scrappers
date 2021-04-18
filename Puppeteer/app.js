'use strict';
const express = require('express');

const getFbPosts = require('./facebook/getFbPosts.js')
const getTwitterPosts = require('./twitter/getTweets.js')
const reddit = require("./reddit/getReddit.js");
// const db = require('./db.js');
var fs = require("fs");

async function callReddit(){

  (async () => {
    await reddit.initialize("asu", {
      headless: true,
      devtools: false
    });
    const results = await reddit.getLatest({
      type: "hot",
      number: 150
      // keywords: ["appointment", "reminder"]
    });
  
    if (!results.length) {
      console.log("No results");
    }
  
    results.forEach(result => {
      console.log("\n");
      // console.log(result)
      console.log(`Title: ${result.title}`);
      console.log(`Number of Comments: ${result.commentsNo}`);
      console.log(`Number of Upvotes: ${result.score}`);
      console.log(`Posted By: ${result.authorName}`)
      console.log(`Time Posted: ${result.postTime}`);
      console.log(`Post URL: ${result.link}`);
    //   console.log("\n");
    });
    
    var d = new Date();
    var fileName= d.getTime();
    var dir = './reddit/data';

    if (!fs.existsSync(dir)){
        fs.mkdirSync(dir);
    }

    fs.writeFile("./reddit/data/"+String(fileName)+'.json', JSON.stringify(results), (err) => {
        // throws an error, you could also catch it here
        if (err) throw err;
        // success case, the file was saved
		console.log("\n");
        console.log('Posts are Saved in the file! ' + String(fileName) );
    });


    await reddit.close();
  })();
}

/*const app = express();

const pageScrollLength = 5;
const accountNo = 1;

app.get('/', (req, res) => {
    res
    .status(200)
    .send('Come to decode facebook!')
    .end();
});



// URL: http://0.0.0.0:9000/facebook/getPosts

app.get('/facebook/getPosts', async (req, res) => {

    await getFbPosts.getAllGroup(pageScrollLength,accountNo);
    res
        .status(200)
        .send('Facebook_group logged in!')
        .end();
});

app.get('/twitter/getPosts', async (req, res) => {

    await getTwitterPosts.gotopage(pageScrollLength);
    res
        .status(200)
        .send('Facebook_group logged in!')
        .end();
});

app.get('/reddit/getPosts', async (req, res) => {

    await callReddit();
    res
        .status(200)
        .send('Facebook_group logged in!')
        .end();
});

const http = require('http');

const hostname = '0.0.0.0';


// Start the server
const PORT = process.env.PORT || 9000;
app.listen(PORT,hostname, () => {
  console.log(`App listening on port  ${hostname} ${PORT}`);
  console.log('Press Ctrl+C to quit.');
});
// [END gae_node_request_example]
module.exports = app;
*/

var arg_values = process.argv.slice(2);
console.log('arguments passed: ', arg_values);

var pageScrollLength = 1;
var accountNo = 1;

switch (arg_values[0]) {
    case 'facebook':
        pageScrollLength = arg_values[1];
        (async () => {
          await getFbPosts.getAllGroup(pageScrollLength,accountNo);
        })();
        break;
    case 'twitter':
        (async () => {
          var keywords = ["diabetes","Insulin","neuropathy","humalog"];
          pageScrollLength = arg_values[1];
          await getTwitterPosts.scrapeTweets(pageScrollLength,accountNo,keywords);
        })();
        break;
    case 'reddit':
        (async () => {
		await reddit.scrapReddit();
        })(); 
        break;
    default:
        console.log('Sorry unable to run the script, passed wrong arguments.');
        break;
}