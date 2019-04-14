package com.mrg.apigenerator.exception;

public class DataSourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1226439803994500725L;

	public DataSourceNotFoundException(String msg){
		super(msg);
	}
	
}
