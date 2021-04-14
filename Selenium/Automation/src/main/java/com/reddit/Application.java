package com.reddit;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.twitter.CustomMappingStrategy;
import com.twitter.Tweet;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Vivek
 * <p>
 * Selenium script to login to twitter and collect tweets along with comments,retweet and likes count for a given list of keywords.
 */

public class Application {

    static String userName = "WorthAd6175";
    static String passWord = "7~9$r5fZ2xR9#pQ";
    static String redditBaseURL = "https://reddit.com/";
    static String login = "login";
    static String home = "home";
    static int totalScrolls = 10;

    public static void main(String[] args) throws InterruptedException, IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");
		options.addArguments("--disable-notifications");

        System.setProperty("webdriver.chrome.driver", "chromedriver");
        WebDriver driver = new ChromeDriver(options);


//		login(driver);
        Thread.sleep(1000);

        driver.get(redditBaseURL + "r/baseball/");

        getPostsFromSubreddit("r/baseball/", driver, 5);
//		Thread.sleep(20000);
        driver.quit();
    }

    public static void getPostsFromSubreddit(String subreddit, WebDriver driver, int totalScrolls) throws InterruptedException, IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        Thread.sleep(5000);

        List<RedditPost> redditPosts = new ArrayList<>();
        driver.get(redditBaseURL + subreddit);

        while (totalScrolls > 0) {
            Thread.sleep(20000);
            List<WebElement> postDivs = driver.findElements(By.className("scrollerItem"));

            int postsPerPage = 7;
            int idx = 0;
            for (WebElement postDiv : postDivs) {
                if (idx > postsPerPage) {
                    break;
                }
                String postTitle = postDiv.findElement(By.xpath(".//div/div/div/a/div/h3")).getText();
                String postUrl = postDiv.findElements(By.xpath(".//div/div/div/a")).get(1).getAttribute("href");
                String votes = postDiv.findElement(By.xpath(".//div/div/div")).getText();
                String postedBy = postDiv.findElement(By.xpath(".//div/div/div/div/div/a")).getText();
                String comments = postDiv.findElement(By.xpath(".//div/div/div/a/span")).getText();
                String postedAt = postDiv.findElements(By.xpath(".//div/div/div/div/a")).get(1).getText();

                System.out.println(postTitle);
                System.out.println(postUrl);
                System.out.println(votes);
                System.out.println(postedBy);
                System.out.println(comments);
                System.out.println(postedAt);
                System.out.println("-----------------------------------------------------");

                RedditPost redditPost = new RedditPost(postTitle, postUrl, votes, postedBy, comments, postedAt);
                redditPosts.add(redditPost);
                idx++;

            }
            scroll(driver);
            totalScrolls--;
        }

        CustomMappingStrategy<RedditPost> mappingStrategy = new CustomMappingStrategy<>();
        mappingStrategy.setType(RedditPost.class);

        String fileName = new StringBuilder().append(subreddit).append(".csv").toString();
        Writer writer = new FileWriter(fileName);
        StatefulBeanToCsv<RedditPost> csvwriter = new StatefulBeanToCsvBuilder<RedditPost>(writer)
                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                .withOrderedResults(true)
                .withMappingStrategy(mappingStrategy)
                .build();
        csvwriter.write(redditPosts);
        writer.close();
    }

    /**
     * Login the user to Reddit
     *
     * @param driver
     * @throws InterruptedException
     */

    public static void login(WebDriver driver) throws InterruptedException {
        String twitterLoginURL = new StringBuilder(redditBaseURL).append(login).toString();
        driver.get(twitterLoginURL);
        driver.manage().window().maximize();
        Thread.sleep(1000);
        WebElement userTextField = driver.findElement(By.id("loginUsername"));
        userTextField.sendKeys(userName);

        WebElement PassTextField = driver.findElement(By.id("loginPassword"));
        PassTextField.sendKeys(passWord);

        driver.findElement(By.className("AnimatedForm__submitButton")).click();
    }


    /**
     * Scroll the screen.
     *
     * @param driver
     */

    public static void scroll(WebDriver driver) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }
}
