const puppeteer = require('puppeteer');
const jsdom = require("jsdom")
const fs = require('fs');
const { url } = require('inspector');
const {JSDOM} = jsdom
global.DOMParser = new JSDOM().window.DOMParser

const isHeadless = false


async function scroll(page, scrollDelay = 1000) {
    let previousHeight;
    try {
        while (mutationsSinceLastScroll > 0 || initialScrolls > 0) {
            mutationsSinceLastScroll = 0;
            initialScrolls--;
            previousHeight = await page.evaluate(
                'document.body.scrollHeight'
            );
            await page.evaluate(
                'window.scrollTo(0, document.body.scrollHeight)'
            );
            await page.waitForFunction(
                `document.body.scrollHeight > ${previousHeight}`,
                {timeout: 600000}
            ).catch(e => console.log('scroll failed'));
            await page.waitForTimeout(scrollDelay);
        }
    } catch (e) {
        console.log(e);
    }
}

async function autoScroll(page) {
    await page.evaluate(async () => {
        await new Promise((resolve, reject) => {
            var totalHeight = 0;
            var distance = 100;
            var timer = setInterval(() => {
                var scrollHeight = document.body.scrollHeight;
                window.scrollBy(0, distance);
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
    var url = "https://mobile.twitter.com/search?q="+searchword+"&src=typed_query"
    await page.goto(url,
    {waitUntil: 'networkidle2'})
    var posts = []
    for(var ltt =0;ltt<pageScrollLength;ltt++){
        await autoScroll(page);
        console.log(ltt);
        const storyHtml = await page.content();
        const dom = new JSDOM(storyHtml);
        articles = dom.window.document.querySelectorAll('article');
        for(var k =0;k<articles.length;k++){
            var post = {}
            if(articles[k].querySelector('a[aria-label]')!=undefined){
                var id = (articles[k].querySelectorAll('a[aria-label]')[0]).href;
                id = id.split("/");
                var tweetid = id[id.length-1];
                var tweetlink = (articles[k].querySelectorAll('a[aria-label]')[0]).href;
                var tweetContent = articles[k].textContent;
                post["id"] = tweetid;
                post["tweetlink"] = tweetlink;
                post["tweetcontent"] = tweetContent;
                posts.push(post);
            }
            
        }

        await page.waitForTimeout(1000);

    }

    console.log(posts);
    return posts;
}


async function logIn(page,email,password) {
        await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3419.0 Safari/537.36');
        await page.goto('https://mobile.twitter.com/login',
        {waitUntil: 'networkidle2'})
        const storyHtml = await page.content();
        const dom = new JSDOM(storyHtml);

        
        await page.waitForSelector('input[name="session[username_or_email]"]')
        await page.type('input[name="session[username_or_email]"]', email)
        await page.type('input[name="session[password]"]', password)
            
        //await page.waitForSelector('div[data-focusable="true"]');
        await page.waitForSelector('div[data-testid="LoginForm_Login_Button"]');
        await page.click('div[data-testid="LoginForm_Login_Button"]')
        await page.waitForTimeout(1000);
        
}



exports.gotopage = async function(pageScrollLength){
    const browser = await puppeteer.launch({headless: isHeadless,args: ['--no-sandbox', '--disable-setuid-sandbox'],slowMo: 10,userDataDir: './twitter/myUserDataDir'})
    const page = await browser.newPage()
    await page.setUserAgent('Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3419.0 Safari/537.36');
    const context = browser.defaultBrowserContext();
    context.overridePermissions("https://mobile.twitter.com", ["geolocation", "notifications"]);

    await page.setViewport({width: 1280, height: 800})
    //pass the id here
    var fbLogins;
    
    fbLogins = fs.readFileSync('./twitter/accounts.json'); 
    fbLogins = JSON.parse(fbLogins);
    var rand = 1;
    rand = rand-1;
    
    console.log("Account chosen for scrapping: email ",fbLogins[rand].email);

    //await logIn(page,fbLogins[rand].email,fbLogins[rand].password)
    //await logIn(page,"michelwilliam199207@gmail.com","#Facebook1234#")
    await page.waitForTimeout(1000);
    
    keywords = ["diabetes","Insulin","neuropathy","humalog"]


    allPostsData = [];

    
    for(var k=0;k<keywords.length;k++){
        var searchword = keywords[k];
        var allposts = await getTwitterPost(page,searchword,pageScrollLength);
        for(var i=0;i<allposts.length;i++){
            allPostsData.push(allposts[i]);
        }
    }
   
    var d = new Date();
    var fileName= d.getTime();
    var dir = './twitter/data';

    if (!fs.existsSync(dir)){
        fs.mkdirSync(dir);
    }

    fs.writeFile("./twitter/data/"+String(fileName)+'.json', JSON.stringify(allPostsData), (err) => {
        // throws an error, you could also catch it here
        if (err) throw err;
        // success case, the file was saved
        console.log('Posts are Saved in the file! ' + String(fileName) );
    });
    console.log("\nFinally retrevied posts are start: \n");
    console.log(JSON.stringify(allPostsData));
    console.log("\nFinally retrevied posts are End :\n");

    await page.waitForTimeout(1000);

    await browser.close();

}