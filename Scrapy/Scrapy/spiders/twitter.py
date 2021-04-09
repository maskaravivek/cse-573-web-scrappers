import scrapy
import ipdb
import re
from dateutil import parser
import sys
from scrapy.crawler import CrawlerProcess
import logging
from scrapy.selector import Selector
from datetime import datetime
import json

class TwitterSpider(scrapy.Spider):
    name = 'twitter'
    allowed_domains = ['twitter.com']
    #start_urls = ['http://twitter.com/']
    custom_settings = {
        'USER_AGENT': 'Mozilla/5.0 AppleWebKit/537.36 (KHTML, like Gecko; compatible; Googlebot/2.1; +http://www.google.com/bot.html) Safari/537.36',
        'CONCURRENT_REQUESTS': 2, 'DOWNLOAD_DELAY': 1, 'LOG_LEVEL': 'INFO'}

    def __init__(self, filename=''):
        if not filename:
            sys.exit('Please provide the input ')
        self.filename = filename


    def start_requests(self):
        with open(self.filename, 'r') as f:
            hashtags = f.read().splitlines()
        if len(hashtags) == 0:
            sys.exit('Emplty File detected.Please provide hashtags separated by newlines')
        else:
            logging.info(f'{len(hashtags)} hashtags or user found')
        for hashtag in hashtags:
            if hashtag:
                if hashtag[0] =='#':
                    search_url = "https://mobile.twitter.com/hashtag/" + hashtag[1:].lower()
                    print(search_url)
                    yield scrapy.Request(search_url, callback=self.find_tweets, dont_filter=True)
                else:
                    search_url = "https://mobile.twitter.com/"+ hashtag[1:].lower()
                    print(search_url)
                    yield scrapy.Request(search_url, callback=self.find_tweets_user, dont_filter=True)





    def find_tweets(self, response):
        res1 = []
        tweets = response.xpath('//li[@data-item-type="tweet"]/div').get()
        #print(tweets)
        logging.info(f'{len(tweets)} tweets found')
        for tweet in response.xpath('//li[@data-item-type="tweet"]/div'):
            username = tweet.xpath('.//span[@class="username u-dir u-textTruncate"]/b/text()').extract()[0]
            ID = tweet.xpath('.//@data-tweet-id').extract()
            tweet_text = ' '.join(tweet.xpath('.//div[@class="js-tweet-text-container"]/p//text()').extract()).replace(' # ','#').replace(' @ ', '@')
            tweet_url = tweet.xpath('.//@data-permalink-path').extract()[0]
            no_retweet = tweet.css('span.ProfileTweet-action--retweet > span.ProfileTweet-actionCount').xpath('@data-tweet-stat-count').extract()
            no_favorite = tweet.css('span.ProfileTweet-action--favorite > span.ProfileTweet-actionCount').xpath('@data-tweet-stat-count').extract()
            no_reply = tweet.css('span.ProfileTweet-action--reply > span.ProfileTweet-actionCount').xpath('@data-tweet-stat-count').extract()
            tweet_datetime= datetime.fromtimestamp(int(tweet.xpath('.//div[@class="stream-item-header"]/small[@class="time"]/a/span/@data-time').extract()[0])).strftime('%Y-%m-%d %H:%M:%S')

            result_hashtag = {
            'username': username,
            'tweet_url': str(tweet_url),
            'tweet_text': tweet_text,
            'tweet_datetime': str(tweet_datetime),
            'number_of_likes': str(no_favorite[0]),
            'no_of_retweets': str(no_retweet[0]),
            'no_of_replies': str(no_reply[0]),
            'ID': str(ID[0]),
            }
            print(result_hashtag)
            res1.append(result_hashtag)
        with open('outputHashtag.json', 'w') as json_file:
            json.dump(res1, json_file)

            # res.append([username,ID[0],tweet_datetime,tweet_url,no_retweet[0],no_favorite[0],no_reply[0],tweet_text])
            # print(username,ID[0],tweet_datetime,tweet_url,no_retweet[0],no_favorite[0],no_reply[0],tweet_text)

        next_page = response.xpath(
            '//*[@class="w-button-more"]/a/@href').get(default='')
        logging.info('Next page found:')
        if next_page != '':
            next_page = 'https://mobile.twitter.com' + next_page
            yield scrapy.Request(next_page, callback=self.find_tweets)

    def find_tweets_user(self, response):
        res2 = []
        tweets = response.xpath('//li[@data-item-type="tweet"]/div').get()
        #print(tweets)
        logging.info(f'{len(tweets)} tweets found')
        for tweet in response.xpath('//li[@data-item-type="tweet"]/div'):
            username = tweet.xpath('.//span[@class="username u-dir u-textTruncate"]/b/text()').extract()
            ID = tweet.xpath('.//@data-tweet-id').extract()
            tweet_text = ' '.join(tweet.xpath('.//div[@class="js-tweet-text-container"]/p//text()').extract()).replace(' # ','#').replace(' @ ', '@')
            tweet_url = tweet.xpath('.//@data-permalink-path').extract()
            no_retweet = tweet.css('span.ProfileTweet-action--retweet > span.ProfileTweet-actionCount').xpath('@data-tweet-stat-count').extract()
            no_favorite = tweet.css('span.ProfileTweet-action--favorite > span.ProfileTweet-actionCount').xpath('@data-tweet-stat-count').extract()
            no_reply = tweet.css('span.ProfileTweet-action--reply > span.ProfileTweet-actionCount').xpath('@data-tweet-stat-count').extract()
            tweet_datetime= tweet.css('.tweet-timestamp::attr("title")').get()
            if username:
                result = {
            'username': username[0],
            'tweet_url': str(tweet_url[0]),
            'tweet_text': tweet_text,
            'tweet_datetime': str(tweet_datetime),
            'number_of_likes': str(no_favorite[0]),
            'no_of_retweets': str(no_retweet[0]),
            'no_of_replies': str(no_reply[0]),
            'ID': str(ID[0]),
            } 
                #print(username,tweet_datetime,ID,tweet_url,no_retweet,no_favorite,no_reply,tweet_text)
                print(result)
                res2.append(result)
        with open('outputUser.json', 'w') as json_file:
            json.dump(res2, json_file)
                #res.append([username,ID[0],tweet_datetime,tweet_url,no_retweet[0],no_favorite[0],no_reply[0],tweet_text])
        #print(res)


        next_page = response.xpath(
            '//*[@class="w-button-more"]/a/@href').get(default='')
        logging.info('Next page found:')
        if next_page != '':
            next_page = 'https://mobile.twitter.com' + next_page
            yield scrapy.Request(next_page, callback=self.find_tweets_user)
    # def parse(self, response):
    #     pass
