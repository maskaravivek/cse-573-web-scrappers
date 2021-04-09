const puppeteer = require('puppeteer');
const jsdom = require("jsdom")
const fs = require('fs');
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


async function getTwitterPost(page,searchword,pageScrollLength){
   
    // await page.waitForTimeout(1000000);
    /* building the search url and navigating to that page */
    var url = "https://mobile.twitter.com/search?q="+searchword+"&src=typed_query";
    
    const response = await page.goto(url,{ waitUntil: 'networkidle2' });
    //console.log(response);

    /* declared variable to collect fetched tweets */
    var tweets = {}

    for(var i =0;i<pageScrollLength;i++){
        console.log("\nPage scroll : ", i,"\n");
        await autoScroll(page);
        /* loading the html content to read using json dom in javascript */
        const storyHtml = await page.content();
        const dom = new JSDOM(storyHtml);

        await page.waitForTimeout(1000);
        //console.log(storyHtml);
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
                tweet["tweet_id"] = tweet_id;
                tweet["tweet_link"] = tweet_link;
                tweet['tweet_user'] = header;
                tweet["tweet_text"] = tweet_content;
                tweet['replies_retweets_likes'] = tweet_likes;
                tweet['tweet_time'] = tweet_time;
                tweet_likes = tweet_likes.split(',');
                for(var r=0;r<tweet_likes.length;r++){
                    if(tweet_likes[r].includes("replies")){
                        tweet['tweet_reply'] = tweet_likes[r];
                    }
                    else if(tweet_likes[r].includes("Retweets")){
                        tweet['tweet_retweet'] = tweet_likes[r];
                    }
                    else if(tweet_likes[r].includes("likes")){
                        tweet['tweet_favorite'] = tweet_likes[r];
                    }
                }
                tweets[tweet_id]=tweet;
            }
        }
        await page.waitForTimeout(1000);
    }

    console.log(tweets);
    return Object.values(tweets);
}


async function logIn(page,email,password) {
       

        var page_code = false;
        var login_flag = false;
        try{
            //await page.setUserAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36');
            const response = await page.goto('https://mobile.twitter.com/login',{waitUntil: 'networkidle2'});
            //console.log("Response navigation code: ", response);
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
    const browser = await puppeteer.launch({headless:isHeadless,args: ['--no-sandbox', '--disable-setuid-sandbox','--enable-features=ExperimentalJavaScript','--enable-javascript-harmony'
    ,'--lang=en-US,en;q=0.9'],slowMo: 10,userDataDir: './twitter/myUserDataDir'})
    const page = await browser.newPage()
    //await page.setUserAgentString('Mozilla/5.0 (compatible, MSIE 11, Windows NT 6.3; Trident/7.0; rv:11.0) like Gecko')
    await page.setExtraHTTPHeaders({
        'Accept-Language': 'en-US,en;q=0.9',
        'server': 'tsa_a'
    });
    await page.setJavaScriptEnabled(true);
    console.log(await page.evaluate(()=>navigator.userAgent));
    const context = browser.defaultBrowserContext();
    context.overridePermissions("https://mobile.twitter.com", ["geolocation", "notifications"]);
    await page.setViewport({width: 1280, height: 800});
    await page.setUserAgent('Mozilla/5.0 (Macintosh; Intel Mac OS X 11_2_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36');

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