package org.dataccs.document.processor.test;

import org.dataccs.document.processor.config.Configuration;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class TestConfiguration {

	@Test
	public void testConfiguration(){
		
		Configuration config = new Configuration();
		String value = config.get("processor.dataset.charset");
		System.out.println(value);
	}
}
