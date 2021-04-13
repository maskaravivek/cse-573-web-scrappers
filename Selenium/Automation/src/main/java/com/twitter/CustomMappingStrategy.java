package com.twitter;

import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class  CustomMappingStrategy<T> extends ColumnPositionMappingStrategy<T> {
    private final String[] HEADER = new String[]{"Link", "Id", "User name", "time","Tweet","Likes","Replies","Retweets"};

    @Override
    public String[] generateHeader(T data) throws CsvRequiredFieldEmptyException {
    	super.generateHeader(data);
    	return HEADER;
    }
} 


