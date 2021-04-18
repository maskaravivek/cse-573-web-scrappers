# CSE 573 Web Scrappers

The repo is organized in the following way:

1) Each tool that we surveyed, has a separate folder.
2) Inside the folder we have code for the social media sites like Reddit, Twitter and Facebook.
3) The scraped data output that we generated is inside the folders.

# Presentations

- [Proposal](https://docs.google.com/presentation/d/1A60OiPRnWJ3sxCQvZCl3bA-z5YHV7PkLgeeBQsJ8JO8/edit)
- [Group demo](https://docs.google.com/presentation/d/1QcPta1kwV7eqlqRTU3g-KkIfS7azMmWFqFT0r50ewV4/edit)

## How to run the tools?

### Selenium

The demo app is coded in Java. You will need Java setup on your machine to run the demo.

- The [Selenium directory](Selenium/Automation/src/main/java/com/) has two sub folders for `reddit` and `twitter`. Browse to the respective directory to scrap the website.
- The directory contains an `Application.java` class which can be executed to run the demo
- The `Application.java` class contains `userName` and `passWord` fields. Replace the values with actual username/password.
- Run the `main()` function to run the demo

Output files: [Output Directory](Selenium/output)

### Scrapy

- Browse to the [Scrapy directory](Scrapy). The Spider directory has two crawlers for reddit and twitter.
- Put the input hashtags, user (in case of twitter) , topics(in case of reddit) in a .csv file format.
- Run the crawler for the respective website as below to run the demo
  - `scrapy crawl reddit -a filename=input_File_name -o outputfile_name`
  - `scrapy crawl twitter -a filename=input_File_name -o outputfile_name`

Output files: [Output Directory](Scrapy/output)

### Puppeteer

* Browse to the Puppeteer directory. The puppeteer directory has 3 folders facebook,twitter and reddit.
  * Install the libraries required to run puppeteer code.
    * Step 1: Node js can be installed using the following [link](https://nodejs.org/en/download/) for the required platform.
    * Step 2: install npm package. ` npm install`  will install all the puppeteer related libraries.
* Place the user and password details in account.js file in corresponding folders i.e. facebook, twitter.
* Put the input keywords in app.js to be searched in facebook, twitter and reddit.
* Run the node js code using the following commands.
  * command  is as follows `node app.js websitename pagescrolllength `
  * For facebook ` node app.js facebook 2`
  * For Twitter ` node app.js twitter 2`
  * For Reddit ` node app.js reddit`
  * If you don't pass the pagescroll length argument, the default page scroll length is taken as 1.
* The output will be saved in corresponding data folders of facebook, twitter and reddit
  * Facebook output [Output Directory](Puppeteer/facebook/data)
  * Twitter output [Output Directory](Puppeteer/twitter/data)
  * Reddit output [Output Directory](Puppeteer/reddit/data)
