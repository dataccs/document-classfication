package org.dataccs.document.processor.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class Configuration {

	private static final String DEFAULT_CONFIG_FILE = "configuration/config.properties";
	private String pro;
	private final Properties properties = new Properties();
	
	public Configuration(String config){
		super();
		this.pro = config;
		load();
	}
	
	public Configuration(){
		super();
		this.pro = DEFAULT_CONFIG_FILE;
		load();
	}
	
	private void load(){
		System.out.println("load properties file: "+pro);
		FileInputStream in = null;
		try{
			in = new FileInputStream(new File(pro));
			properties.load(in);
			
		} catch (Exception e){
			System.out.println("载入"+pro+"文件失败!");
			e.printStackTrace();
		}
	}	
	
	public String get(String key) {
		String value = properties.getProperty(key);
		return value;
	}
	
	public String get(String key, String defaultValue) {
		String value = defaultValue;
		String v = properties.getProperty(key);
		if(v != null) {
			value = v;
		}
		return value;
	}
	
	public int getInt(String key){
		int value = 0;
		String v = properties.getProperty(key);
		try {
			value = Integer.parseInt(v);
		} catch (Exception e) {e.printStackTrace(); }
		return value;
	}
	
	public int getInt(String key, int defaultValue) {
		int value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Integer.parseInt(v);
		} catch (Exception e) {e.printStackTrace(); }
		return value;
	}
	
	public long getLong(String key) {
		long value = 0;
		String v = properties.getProperty(key);
		try {
			value = Long.parseLong(v);
		} catch (Exception e) {e.printStackTrace(); }
		return value;
	}
	
	public long getLong(String key, long defaultValue) {
		long value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Long.parseLong(v);
		} catch (Exception e) {e.printStackTrace(); }
		return value;
	}
	
	public double getDouble(String key) {
		double value = 0;
		String v = properties.getProperty(key);
		try {
			value = Double.parseDouble(v);
		} catch (Exception e) {e.printStackTrace(); }
		return 0;
	}
	
	public double getDouble(String key, double defaultValue) {
		double value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Double.parseDouble(v);
		} catch (Exception e) {e.printStackTrace(); }
		return value;
	}
	
	public boolean getBoolean(String key) {
		boolean value = false;
		String v = properties.getProperty(key);
		try {
			value = Boolean.parseBoolean(v);
		} catch (Exception e) {e.printStackTrace(); }
		return value;
	}
	
	public boolean getBoolean(String key, boolean defaultValue) {
		boolean value = defaultValue;
		String v = properties.getProperty(key);
		try {
			value = Boolean.parseBoolean(v);
		} catch (Exception e) {e.printStackTrace(); }
		return value;
	}
}
