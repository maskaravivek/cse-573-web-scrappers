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

### Scrapy
- Browse to the Scrapy directory. The Spider directory has two crawlers for reddit and twitter. 
- Put the input hashtags, user (in case of twitter) , topics(in case of reddit) in a .csv file format.
- Run the crawler for the respective website as below to run the demo
    - `scrapy crawl reddit -a filename=input_File_name -o outputfile_name`
    - `scrapy crawl twitter -a filename=input_File_name -o outputfile_name`
### Puppeteer
