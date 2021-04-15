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

class RedditSpider(scrapy.Spider):
    name = 'reddit'
    allowed_domains = ['reddit.com']
    custom_settings = {
        'CONCURRENT_REQUESTS': 1, 'DOWNLOAD_DELAY': 10, 'LOG_LEVEL': 'INFO'}

    def __init__(self, filename=''):
        if not filename:
            sys.exit('Please provide the input ')
        self.filename = filename


    def start_requests(self):
        with open(self.filename, 'r') as f:
            subreddits = f.read().splitlines()
        if len(subreddits) == 0:
            sys.exit('Emplty File detected.Please provide hashtags separated by newlines')
        else:
            logging.info(f'{len(subreddits)} topics found')
        for topic in subreddits:
            if topic:
                search_url = "https://www.reddit.com/r/" + topic.lower()
                print(search_url)
                yield scrapy.Request(search_url, callback=self.parse, dont_filter=True)
    def parse(self, response):
      logging.info("%s page visited by reddit spider", response.url)
      """
      with open('output.json', 'w') as json_file:
            json.dump(response.text, json_file)"""
      try:
        titles = response.css('._eYtD2XCVieq6emjKBH3m::text').extract()       
        votes = response.css('._1rZYMD_4xY3gRcSS3p8ODO::text').extract()
        comments = response.css('.FHCV02u6Cp2zYL0fhQPsO::text').extract()
        Timeposted = response.css('._3jOxDPIQ0KaOWpzvSQo-1s::text').extract()
        #timeposted = response.css('a._3jOxDPIQ0KaOWpzvSQo-1s::text').extract()
        posturl = response.css('.SQnoC3ObvgnGjWt90zD9Z::attr(href)').extract()
        #postImageUrl = response.css("._2_tDEnGMLxpM6uOa2kaDB3::attr(src)").extract()
      except:
        print("==============Error=================")
       
      #Give the extracted content row wise
      for item in zip(titles, votes, comments, Timeposted, posturl):
          #create a dictionary to store the scraped info
          all_items = {
              'title' : item[0],
              'vote' : item[1],
              'comments': item[2],
              'Timeposted': item[3],
              'PostUrl': item[4]
          }
          print("========================")
          print("Title: {}".format(item[0]))
          print("vote: {}".format(item[1]))
          print("comments: {}".format(item[2]))
          print("Timeposted: {}".format(item[3]))
          print("posturl: {}".format(item[4]))
          #print("postImageUrl: {}".format(item[5]))
      
          yield all_items


    