const puppeteer = require('puppeteer');
const jsdom = require("jsdom")
const fs = require('fs');
const { exit } = require('process');
const { pathToFileURL } = require('url');
const {JSDOM} = jsdom;
global.DOMParser = new JSDOM().window.DOMParser;
const isHeadless = false;


async function autoScroll(page) {
    await page.evaluate(async () => {
        await new Promise((resolve, reject) => {
            var totalHeight = 0;
            var distance = 300;
            var timer = setInterval(() => {
                var scrollHeight = document.body.scrollHeight;
                window.scrollBy(0, distance);
                clearInterval(timer);
                resolve();
                totalHeight += distance;
                if (totalHeight >= scrollHeight) {
                    clearInterval(timer);
                    resolve();
                }
            }, 100);
        });
    });
}

async function getTweetData(page,tweet_url,tweets_meta){
    /* building the search url and navigating to that page */
    await page.goto(tweet_url,{ waitUntil: 'domcontentloaded' });
    await page.waitForTimeout(1000);

    const storyHtml = await page.content();
    const dom = new JSDOM(storyHtml);
    await page.waitForTimeout(10000);
    article = dom.window.document.querySelector('article');
    tweet_content = {}
    tweet_content['tweet_id'] = tweets_meta['id'];
    tweet_content['tweet_link'] = tweet_url;
    tweet_content['tweet_raw'] = tweets_meta['tweet_content'];
    tweet_content['header'] = '';
    tweet_content['body'] = '';
    tweet_content['footer'] = '';
    if(article!=undefined){
        header = article.querySelector('div[data-testid="tweet"]').textContent;
        tweet_body = article.querySelector('div[lang="en"]').textContent
        tweet_remaining = article.textContent.replace(header+tweet_body,"");
        tweet_content['header'] = header;
        tweet_content['body'] = tweet_body;
        tweet_content['footer'] = tweet_remaining;
    }
    return tweet_content;
}

async function getTwitterPost(page,searchword,pageScrollLength){
   
    /* building the search url and navigating to that page */
    var url = "https://twitter.com/search?q="+searchword+"&src=typed_query";
    // await page.setUserAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_1) AppleWebKit/537.36 (KHTML, like Gecko) HeadlessChrome/73.0.3683.75 Safari/537.36');
    // await page.setExtraHTTPHeaders({
    //     'Accept-Language': 'en-US,en;q=0.9'
    // });
    const response = await page.goto(url,{ waitUntil: 'domcontentloaded' });
    //console.log(response);

    /* declared variable to collect fetched tweets */
    var tweets = []

    for(var i =0;i<pageScrollLength;i++){
        console.log("\nPage scroll : ", i,"\n");
        await autoScroll(page);
        /* loading the html content to read using json dom in javascript */
        const storyHtml = await page.content();
        const dom = new JSDOM(storyHtml);

        await page.waitForTimeout(1000);
        console.log(storyHtml);
        /* evaluating all the tweets from the html page */
        articles = dom.window.document.querySelectorAll('article');
        // console.log(articles);
        for(var k =0;k<articles.length;k++){
            var tweet = {}
            if(articles[k].querySelector('a[aria-label]')!=undefined){
                var id = (articles[k].querySelectorAll('a[aria-label]')[0]).href;
                id = id.split("/");
                var tweet_id = id[id.length-1];
                var tweet_link = (articles[k].querySelectorAll('a[aria-label]')[0]).href;
                var tweet_likes = (articles[k].querySelector('div[aria-label][role="group"]')).getAttribute('aria-label');
                var header = articles[k].querySelector('div[dir="ltr"]').textContent;
                var tweet_content = articles[k].textContent;
                var tweet_time = articles[k].querySelector('time').getAttribute('datetime');
                tweet["id"] = tweet_id;
                tweet["tweetlink"] = tweet_link;
                tweet['header'] = header;
                tweet["tweetcontent"] = tweet_content;
                tweet['footer'] = tweet_likes;
                tweet['tweet_time'] = tweet_time;
                tweets.push(tweet);
            }
        }
        await page.waitForTimeout(1000);
    }
    // tweets_clean = [];
    // for(var i=0;i<tweets.length;i++){
    //     console.log(tweets[i].tweetlink);
    //     tweet_url = "https://mobile.twitter.com"+tweets[i].tweetlink;
    //     tweet_data = await getTweetData(page,tweet_url,tweets[i]);
    //     tweets_clean.push(tweet_data);
    // }

    console.log(tweets);
    return tweets;
}


async function logIn(page,email,password) {
       

        var page_code = false;
        var login_flag = false;
        try{
           
            // await page.setUserAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36');
            // await page.setExtraHTTPHeaders({
            //     'Accept-Language': 'en-US,en;q=0.9'
            // });
            const response = await page.goto('https://twitter.com/login', {
                timeout: 0,
                waitUntil: "domcontentloaded"
              });
            console.log("Response navigation code: ", response);
            
            page_code =true;

            try {
                await page.waitForSelector('input[name="session[username_or_email]"]', {timeout: 4000});
                // await page.waitForSelector('input[name="session[username_or_email]"]')
                await page.type('input[name="session[username_or_email]"]', email)
                await page.type('input[name="session[password]"]', password)
                //await page.waitForSelector('div[data-focusable="true"]');
                await page.waitForSelector('div[data-testid="LoginForm_Login_Button"]');
                await page.click('div[data-testid="LoginForm_Login_Button"]')
                await page.waitForTimeout(1000);
                login_flag = true; 
            } catch (e) {
                
                // Do something if this is a timeout.
                console.log("The user is already logged in. Returning from login section.");
                console.log(e);
            }
        }
        catch(e){
            console.log("unable to navigate to this link. Following is the error occured. \n");
            console.log(e);
        }
        
        return [page_code,login_flag];
        
        
}



exports.scrapeTweets = async function(pageScrollLength,accountNo,keywords){

    /* Defining our custom browser setup here */
    const browser = await puppeteer.launch({headless:false,args: ['--no-sandbox', '--disable-setuid-sandbox','--enable-features=ExperimentalJavaScript','--enable-javascript-harmony'
    ,'--lang=en-US,en;q=0.9'],slowMo: 10,userDataDir: './twitter/myUserDataDir'})
    const page = await browser.newPage()
    await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36');
    await page.setExtraHTTPHeaders({
        'Accept-Language': 'en-US,en;q=0.9',
        'server': 'tsa_a',
        'content-security-policy': "connect-src 'self' blob: https://*.giphy.com https://*.pscp.tv https://*.video.pscp.tv https://*.twimg.com https://api.twitter.com https://api-stream.twitter.com https://ads-api.twitter.com https://caps.twitter.com https://media.riffsy.com https://pay.twitter.com https://sentry.io https://ton.twitter.com https://twitter.com https://upload.twitter.com https://www.google-analytics.com https://app.link https://api2.branch.io https://bnc.lt https://vmap.snappytv.com https://vmapstage.snappytv.com https://vmaprel.snappytv.com https://vmap.grabyo.com https://dhdsnappytv-vh.akamaihd.net https://pdhdsnappytv-vh.akamaihd.net https://mdhdsnappytv-vh.akamaihd.net https://mdhdsnappytv-vh.akamaihd.net https://mpdhdsnappytv-vh.akamaihd.net https://mmdhdsnappytv-vh.akamaihd.net https://mdhdsnappytv-vh.akamaihd.net https://mpdhdsnappytv-vh.akamaihd.net https://mmdhdsnappytv-vh.akamaihd.net https://dwo3ckksxlb0v.cloudfront.net ; default-src 'self'; form-action 'self' https://twitter.com https://*.twitter.com; font-src 'self' https://*.twimg.com; frame-src 'self' https://twitter.com https://mobile.twitter.com https://pay.twitter.com https://cards-frame.twitter.com ; img-src 'self' blob: data: https://*.cdn.twitter.com https://ton.twitter.com https://*.twimg.com https://analytics.twitter.com https://cm.g.doubleclick.net https://www.google-analytics.com https://www.periscope.tv https://www.pscp.tv https://media.riffsy.com https://*.giphy.com https://*.pscp.tv https://*.periscope.tv https://prod-periscope-profile.s3-us-west-2.amazonaws.com https://platform-lookaside.fbsbx.com https://scontent.xx.fbcdn.net https://*.googleusercontent.com; manifest-src 'self'; media-src 'self' blob: https://twitter.com https://*.twimg.com https://*.vine.co https://*.pscp.tv https://*.video.pscp.tv https://*.giphy.com https://media.riffsy.com https://dhdsnappytv-vh.akamaihd.net https://pdhdsnappytv-vh.akamaihd.net https://mdhdsnappytv-vh.akamaihd.net https://mdhdsnappytv-vh.akamaihd.net https://mpdhdsnappytv-vh.akamaihd.net https://mmdhdsnappytv-vh.akamaihd.net https://mdhdsnappytv-vh.akamaihd.net https://mpdhdsnappytv-vh.akamaihd.net https://mmdhdsnappytv-vh.akamaihd.net https://dwo3ckksxlb0v.cloudfront.net; object-src 'none'; script-src 'self' 'unsafe-inline' https://*.twimg.com   https://www.google-analytics.com https://twitter.com https://app.link  'nonce-ZWI3NTg5YjYtNGY5Yi00OWI2LWE1NmYtNThkYzZiY2IxNjAy'; style-src 'self' 'unsafe-inline' https://*.twimg.com; worker-src 'self' blob:; report-uri https://twitter.com/i/csp_report?a=O5RXE%3D%3D%3D&ro=false"
    });
    await page.setJavaScriptEnabled(true);
    console.log(await page.evaluate(()=>navigator.userAgent));
    const context = browser.defaultBrowserContext();
    context.overridePermissions("https://twitter.com", ["geolocation", "notifications"]);
    await page.setViewport({width: 1280, height: 800});
    
    /* Reading twitter logins from accounts.json file */
    var twitterLogin;
    twitterLogin = fs.readFileSync('./twitter/accounts.json'); 
    twitterLogin = JSON.parse(twitterLogin);
    console.log("Account chosen for scrapping: email ",twitterLogin[accountNo-1].email);

    /* twitter login code goes here */
    var login_response = await logIn(page,twitterLogin[accountNo-1].email,twitterLogin[accountNo-1].password);

    if(login_response[0] == true){
        if(login_response[1] == true){
            console.log("User Logged in sucessfully.");
        }
        else{
            console.log("User already logged in");
        }
        /* Passing the keywords that we want to search for in twitter */
        console.log("We are searching for the following keywords on twitter ", keywords);


        tweets_collected = [];

        /* Iterating over each word in keywords */
        for(var k=0;k<keywords.length;k++){
            var searchword = keywords[k];
            console.log("Currently searching for the keyword ", searchword);

            /* calling the fetching tweets function */
            var tweets = await getTwitterPost(page,searchword,pageScrollLength);
            tweets_collected.push.apply(tweets_collected,tweets);
            console.log("No of tweets scraped for keyword are : ", searchword, tweets.length);
            
        }

        /* storing all the tweets in a file */
        var dir = './twitter/data';
        var date_now = new Date();
        var fileName= date_now.getTime();
        
        if (!fs.existsSync(dir)){
            fs.mkdirSync(dir);
        }

        fs.writeFile("./twitter/data/"+String(fileName)+'.json', JSON.stringify(tweets_collected), (err) => {
            if (err) throw err;
            console.log('All tweets scraped are saved in a file with name! ' + String(fileName) );
        });
    }
    else{
        console.log("\n navigation failed. \n");
    }
    /* closing the browser after scraping the tweets */
    await browser.close();

}