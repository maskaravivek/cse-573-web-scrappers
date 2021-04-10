package com.twitter;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

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




/**
 * @author Suhas
 * 
 * Selenium script to login to twitter and collect tweets along with comments,retweet and likes count for a given list of keywords. 
 *
 */

public class Application {
	
	static String userName="davidwarner2306@gmail.com";
	static String passWord="";
	static String twitterBaseURL = "https://twitter.com/";
	static String login = "login";
	static String home = "home";
	static int totalScrolls = 10;
	
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
		
		System.setProperty("webdriver.chrome.driver", "chromedriver");
		WebDriver driver = new ChromeDriver(options);
		
		
		login(driver);
		Thread.sleep(20000);
		
		
		String keyWords[] = new String[] {"#ElonMusk"};
		
		for(String keyWord:keyWords) {
			System.out.println("***************************START********************************");
			getTweetByKeywords(driver, keyWord, totalScrolls);
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
		WebElement tweetBox = driver.findElement(By.xpath("//div/input[@data-testid='SearchBox_Search_Input']"));
		tweetBox.clear();		
		tweetBox.sendKeys(keyWord);
		tweetBox.sendKeys(Keys.RETURN);
		
		Thread.sleep(20000);
		List<Tweet> tweetList = new ArrayList<Tweet>();
		while(totalScrolls>0){
			Thread.sleep(10000);
			List<WebElement> tweet_divs = driver.findElements(By.xpath("//div[@data-testid='tweet']")); 
			for(WebElement div:tweet_divs) {
				List<WebElement> spans = div.findElements(By.xpath(".//div/span")); 
				StringBuilder sb = new StringBuilder();
				System.out.println("Tweet Content: ");
				for(int i=0;i<spans.size()-3;i++) {
					WebElement span = spans.get(i);
					sb.append(span.getText()).append(" ");
				}
				System.out.println(sb.toString());
				String comments = spans.get(spans.size()-3).getText().isBlank() ? "0" : spans.get(spans.size()-3).getText();
				String retweets = spans.get(spans.size()-2).getText().isBlank() ? "0" : spans.get(spans.size()-2).getText();
				String likes = spans.get(spans.size()-1).getText().isBlank() ? "0" : spans.get(spans.size()-1).getText();
				
				System.out.println("Comments: "+comments); 
				System.out.println("Retweets: "+retweets);
				System.out.println("Likes: "+likes);
				System.out.println();
				
				tweetList.add(new Tweet(sb.toString(),comments,retweets,likes));
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
	                .withOrderedResults(false)
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
		
		String profileURL = new StringBuilder(twitterBaseURL).append(twitterUsername).toString();
		driver.get(profileURL);
		Thread.sleep(20000);
		List<Tweet> tweetList = new ArrayList<Tweet>();

		while(totalScrolls>0){
			Thread.sleep(10000);
			List<WebElement> tweet_divs = driver.findElements(By.xpath("//div[@data-testid='tweet']")); 
			for(WebElement div:tweet_divs) {
				List<WebElement> spans = div.findElements(By.xpath(".//div/span")); 
				StringBuilder sb = new StringBuilder();
				System.out.println("Tweet Content: ");
				for(int i=0;i<spans.size()-3;i++) {
					WebElement span = spans.get(i);
					sb.append(span.getText()).append(" ");
				}
				System.out.println(sb.toString());
				String comments = spans.get(spans.size()-3).getText().isBlank() ? "0" : spans.get(spans.size()-3).getText();
				String retweets = spans.get(spans.size()-2).getText().isBlank() ? "0" : spans.get(spans.size()-2).getText();
				String likes = spans.get(spans.size()-1).getText().isBlank() ? "0" : spans.get(spans.size()-1).getText();
				
				System.out.println("Comments: "+comments); 
				System.out.println("Retweets: "+retweets);
				System.out.println("Likes: "+likes);
				System.out.println();
				tweetList.add(new Tweet(sb.toString(),comments,retweets,likes));

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
	                .withOrderedResults(false)
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
