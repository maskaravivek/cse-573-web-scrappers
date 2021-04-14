package com.twitter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;


import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;




/**
 * @author Suhas
 * 
 * Selenium script to login to twitter and collect tweets along with comments,retweet and likes count for a given list of keywords. 
 *
 */

public class Application {
	
//	static String userName="kjamieson679@gmail.com";
//	static String passWord="";

	static String userName="davidwarner2306@gmail.com";
	static String passWord="";
	static String twitterBaseURL = "https://twitter.com/";
	static String mobileTwitterBaseURL = "https://mobile.twitter.com/";
	static String login = "login";
	static String home = "home";
	static int totalScrolls = 10;
	static String mobileTwitterSearchStart = "https://mobile.twitter.com/search?q=";
	static String mobileTwitterSearchEnd = "&src=typed_query";

	public static void main(String[] args) throws InterruptedException, IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException{
		
		ChromeOptions options = new ChromeOptions();
		options.addArguments("start-maximized"); 
		options.addArguments("enable-automation"); 
		options.addArguments("--no-sandbox"); 
		options.addArguments("--disable-infobars");
		options.addArguments("--disable-dev-shm-usage");
		options.addArguments("--disable-browser-side-navigation"); 
		options.addArguments("--disable-gpu");
		options.addArguments("--disable-notifications");
		options.addArguments("--enable-precise-memory-info"); 

	    LoggingPreferences logPrefs = new LoggingPreferences();
	    logPrefs.enable( LogType.PERFORMANCE, Level.ALL );
	    options.setCapability( "goog:loggingPrefs", logPrefs );
	    options.setExperimentalOption("w3c", false);

		System.setProperty("webdriver.chrome.driver", "chromedriver");
		WebDriver driver = new ChromeDriver(options);
		

		login(driver);
		Thread.sleep(20000);
		
		
		String keyWords[] = new String[] {"ElonMusk"};
		
		for(String keyWord:keyWords) {
			System.out.println("***************************START********************************");
			getTweetByKeywords(driver,keyWord,totalScrolls);
			System.out.println("***************************END********************************");
		}
		
		
		String userProfiles[] = new String[] {"imvkohli"};
		for(String user:userProfiles) {
			System.out.println("***************************START********************************");
			getAllTweetsOfAUser(driver, user, totalScrolls);
			System.out.println("***************************END********************************");
		}
		
		
		String tweets[] = new String[] {"Tesla accepting bitcoins is great step towards making world accept cryptocurrency!"};
		for(String tweetBody:tweets) {
			System.out.println("***************************TWEETING********************************");
			tweet(driver, tweetBody);
			System.out.println("***************************FINISHED********************************");
		}
		
		Thread.sleep(20000);
		logout(driver);
		Thread.sleep(20000);
		
		List<LogEntry> logs = driver.manage().logs().get(LogType.PERFORMANCE).getAll();
		for(LogEntry log:logs) {
			for(String key : log.toJson().keySet())
				System.out.println(log.toJson().get(key));
		}
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		long value = (long) executor.executeScript("return window.performance.memory.usedJSHeapSize");
		long valueInMB = value / (1024 * 1024);
		System.out.println("Heap Size: "+valueInMB);
		driver.quit();

	}
	
	/**
	 *  Gets all the tweets for a given hashtag/keyword
	 * 
	 * @param driver
	 * @param keyWord
	 * @param totalScrolls
	 * @throws InterruptedException
	 * @throws CsvRequiredFieldEmptyException 
	 * @throws CsvDataTypeMismatchException 
	 * @throws IOException 
	 */
	
	public static void getTweetByKeywords(WebDriver driver,String keyWord,int totalScrolls) throws InterruptedException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, IOException{
		List<Tweet> tweetList = new ArrayList<Tweet>();
		String url = new StringBuilder(mobileTwitterSearchStart).append(keyWord).append(mobileTwitterSearchEnd).toString();
		System.out.println(url);
		driver.get(url);

		while(totalScrolls>0){

			Thread.sleep(20000);
			List<WebElement> articles = driver.findElements(By.xpath(".//article")); 
			for(WebElement article:articles) {
				if(article.findElements(By.xpath(".//a[@aria-label]")).size()>0) {
					String tweetLink = article.findElement(By.xpath(".//a[@dir='auto']")).getAttribute("href");
					String userName = article.findElement(By.xpath(".//div[@dir='ltr']")).getText();
					String time = article.findElement(By.tagName("time")).getAttribute("datetime");
					String tweetContent = article.getText();
					String likes = article.findElement(By.xpath(".//div[@aria-label][@role='group']")).getAttribute("aria-label");
					
					
	                String tweetLikes[] = likes.split(",");
	                String replyCount = "0 replies";
	                String retweetCount = "0 Retweets";
	                String likeCount = "0 likes";
	                for(int i=0;i<tweetLikes.length;i++){
	                    if(tweetLikes[i].contains("replies")){
	                    	replyCount = tweetLikes[i];
	                    }
	                    else if(tweetLikes[i].contains("Retweets")){
	                    	retweetCount = tweetLikes[i];
	                    }
	                    else if(tweetLikes[i].contains("likes")){
	                    	likeCount = tweetLikes[i];
	                    }
	                }
	                String tweetBody = tweetContent.replaceAll("[\\t\\n\\r]+"," ").replaceAll(",", " ");

	                String arr[] = tweetLink.split("/");
	                String id = arr[arr.length-1];
	                System.out.println("************************");
					System.out.println(tweetLink);
					System.out.println("----------");
					System.out.println(id);
					System.out.println("----------");
					System.out.println(userName);
					System.out.println("----------");
					System.out.println(time);
					System.out.println("----------");
					System.out.println(tweetBody);
					System.out.println("----------");
					System.out.println(likes);
					System.out.println("----------");
					System.out.println(replyCount);
					System.out.println("----------");
					System.out.println(retweetCount);
					System.out.println("----------");
					System.out.println(likeCount);
					System.out.println("----------");
					System.out.println("************************");
					
					Tweet tweet = new Tweet();
					tweet.setBody(tweetBody);
					tweet.setCommentCount(replyCount);
					tweet.setId(id);
					tweet.setLikeCount(likeCount);
					tweet.setLink(tweetLink);
					tweet.setTime(time);
					tweet.setUsername(userName);
					tweet.setRetweetCount(retweetCount);
					tweetList.add(tweet);
				}
				
			}
			scroll(driver);
			totalScrolls--;

		}
		CustomMappingStrategy<Tweet> mappingStrategy = new CustomMappingStrategy<>();
		mappingStrategy.setType(Tweet.class);

		String fileName = new StringBuilder().append(keyWord).append(".csv").toString();
	    Writer writer = new FileWriter(fileName);
	    StatefulBeanToCsv<Tweet> csvwriter = new StatefulBeanToCsvBuilder<Tweet>(writer)
	                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
	                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
	                .withOrderedResults(true)
	                .withMappingStrategy(mappingStrategy)
	                .build();
	    csvwriter.write(tweetList);
	    writer.close();

		
	}

	
	
	
	/**
	 *  Gets all the tweets for a given user
	 * @param driver
	 * @param twitterUsername
	 * @param totalScrolls
	 * @throws InterruptedException
	 * @throws IOException 
	 * @throws CsvRequiredFieldEmptyException 
	 * @throws CsvDataTypeMismatchException 
	 */
	
	public static void getAllTweetsOfAUser(WebDriver driver,String twitterUsername,int totalScrolls) throws InterruptedException, IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException{
		
		String profileURL = new StringBuilder(mobileTwitterBaseURL).append(twitterUsername).toString();
		driver.get(profileURL);
		System.out.println(profileURL);
		List<Tweet> tweetList = new ArrayList<Tweet>();

		while(totalScrolls>0){

			Thread.sleep(20000);
			List<WebElement> articles = driver.findElements(By.xpath(".//article")); 
			for(WebElement article:articles) {
				if(article.findElements(By.xpath(".//a[@aria-label]")).size()>0) {
					String tweetLink = article.findElement(By.xpath(".//a[@dir='auto']")).getAttribute("href");
					String userName = article.findElement(By.xpath(".//div[@dir='ltr']")).getText();
					String time = article.findElement(By.tagName("time")).getAttribute("datetime");
					String tweetContent = article.getText();
					String likes = article.findElement(By.xpath(".//div[@aria-label][@role='group']")).getAttribute("aria-label");
					
					
	                String tweetLikes[] = likes.split(",");
	                String replyCount = "0 replies";
	                String retweetCount = "0 Retweets";
	                String likeCount = "0 likes";
	                for(int i=0;i<tweetLikes.length;i++){
	                    if(tweetLikes[i].contains("replies")){
	                    	replyCount = tweetLikes[i];
	                    }
	                    else if(tweetLikes[i].contains("Retweets")){
	                    	retweetCount = tweetLikes[i];
	                    }
	                    else if(tweetLikes[i].contains("likes")){
	                    	likeCount = tweetLikes[i];
	                    }
	                }
	                String tweetBody = tweetContent.replaceAll("[\\t\\n\\r]+"," ").replaceAll(",", " ");

	                String arr[] = tweetLink.split("/");
	                String id = arr[arr.length-1];
	                System.out.println("************************");
					System.out.println(tweetLink);
					System.out.println("----------");
					System.out.println(id);
					System.out.println("----------");
					System.out.println(userName);
					System.out.println("----------");
					System.out.println(time);
					System.out.println("----------");
					System.out.println(tweetBody);
					System.out.println("----------");
					System.out.println(likes);
					System.out.println("----------");
					System.out.println(replyCount);
					System.out.println("----------");
					System.out.println(retweetCount);
					System.out.println("----------");
					System.out.println(likeCount);
					System.out.println("----------");
					System.out.println("************************");
					
					Tweet tweet = new Tweet();
					tweet.setBody(tweetBody);
					tweet.setCommentCount(replyCount);
					tweet.setId(id);
					tweet.setLikeCount(likeCount);
					tweet.setLink(tweetLink);
					tweet.setTime(time);
					tweet.setUsername(userName);
					tweet.setRetweetCount(retweetCount);
					tweetList.add(tweet);
				}
				
			}
			scroll(driver);
			totalScrolls--;

		}
		CustomMappingStrategy<Tweet> mappingStrategy = new CustomMappingStrategy<>();
		mappingStrategy.setType(Tweet.class);

		String fileName = new StringBuilder().append(twitterUsername).append(".csv").toString();
	    Writer writer = new FileWriter(fileName);
	    StatefulBeanToCsv<Tweet> csvwriter = new StatefulBeanToCsvBuilder<Tweet>(writer)
	                .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
	                .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
	                .withOrderedResults(true)
	                .withMappingStrategy(mappingStrategy)
	                .build();
	    csvwriter.write(tweetList);
	    writer.close();

	}
	

	
	/**
	 * Login the user to Twitter
	 * 
	 * @param driver
	 * @throws InterruptedException
	 */
	
	public static void login(WebDriver driver) throws InterruptedException{
		String twitterLoginURL = new StringBuilder(twitterBaseURL).append(login).toString();
		driver.get(twitterLoginURL);
		driver.manage().window().maximize();
		Thread.sleep(20000);
		WebElement userTextField = driver.findElement(By.name("session[username_or_email]"));
		userTextField.sendKeys(userName);

		WebElement PassTextField = driver.findElement(By.name("session[password]"));
		PassTextField.sendKeys(passWord);

		driver.findElement(By.xpath("//div[@data-testid='LoginForm_Login_Button']")).click();
	}
	
	
	
	
	/**
	 * Logs out the user from twitter.
	 * 
	 * @param driver
	 * @throws InterruptedException
	 */
	
	public static void logout(WebDriver driver) throws InterruptedException
	{
		WebElement prelogout = driver.findElement(By.xpath("//div[@data-testid='SideNav_AccountSwitcher_Button']"));
		prelogout.click();
		Thread.sleep(10000);
		WebElement logout = driver.findElement(By.xpath("//a[@data-testid='AccountSwitcher_Logout_Button']"));
		logout.click();
		Thread.sleep(10000);
		WebElement logoutconfirmation = driver.findElement(By.xpath("//div[@data-testid='confirmationSheetConfirm']"));
		logoutconfirmation.click();
	}
	
	
	
	/**
	 * Scroll the screen.
	 * 
	 * @param driver
	 * 
	 */
	
	public static void scroll(WebDriver driver){
		JavascriptExecutor jse = (JavascriptExecutor)driver;
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");
		jse.executeScript("window.scrollTo(0, document.body.scrollHeight);");
	}
	
	
	/**
	 * Posts a new Tweet with the given content. 
	 * @param driver
	 * @param tweetBody
	 * @throws InterruptedException
	 */
	
	public static void tweet(WebDriver driver,String tweetBody) throws InterruptedException{
		String homeURL = new StringBuilder(twitterBaseURL).append(home).toString();
		driver.get(homeURL);
		Thread.sleep(10000);
		WebElement tweetTextBox = driver.findElement(By.xpath("//div[@data-testid='tweetTextarea_0']/div/div/div/span"));
		tweetTextBox.sendKeys(tweetBody);
		Thread.sleep(10000);
		WebElement tweetButton = driver.findElement(By.xpath("//div[@data-testid='tweetButtonInline']"));
		tweetButton.click();
	}
	
	
	

	
}
