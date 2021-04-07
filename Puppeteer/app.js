'use strict';
const express = require('express');

const getFbPosts = require('./facebook/getFbPosts.js')
const getTwitterPosts = require('./twitter/getTweets.js')
const reddit = require("./reddit/getReddit.js");
// const db = require('./db.js');

const app = express();



const pageScrollLength = 1;
const accountNo = 1;

app.get('/', (req, res) => {
    res
    .status(200)
    .send('Come to decode facebook!')
    .end();
});


async function callReddit(){


(async () => {
  await reddit.initialize("asu", {
    headless: true,
    devtools: false
  });

  // const results = await reddit.get();
  // const results = await reddit.get({
  //   type: "new",
  //   number: 10
  // });
  // const results = await reddit.get({
  //   limit: 5
  // });

  // const results = await reddit.searchFor(["reminder", "appointment"]).get(5);
  // const results = await reddit
  //   .searchFor(["reminder", "appointment"])
  //   .get({ limit: 5 });

  // const results = await reddit.getLatestHot();
  // const results = await reddit.getLatestNew();

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
    console.log(`Link: ${result.link}`);
    console.log(`Time Posted: ${result.postTime}`);
    console.log(`Number of Comments: ${result.commentsNo}`);
    console.log(`Upvotes: ${result.score}`);
    console.log("\n");
  });

  await reddit.close();
})();
}
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