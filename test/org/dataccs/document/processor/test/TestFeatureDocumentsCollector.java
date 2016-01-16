package org.dataccs.document.processor.test;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.dataccs.document.featurecollector.ChiFeatureWordCollector;
import org.dataccs.document.processor.common.Context;
import org.dataccs.document.processor.common.Label;
import org.dataccs.document.processor.common.Term;
import org.dataccs.document.processor.config.Configuration;
import org.junit.Test;

public class TestFeatureDocumentsCollector {

	@Test
	public void fire(){
		Configuration config = new Configuration("configuration/config.properties");
		Context context = new Context();
		ChiFeatureWordCollector bidc = new ChiFeatureWordCollector(config,context);
		bidc.select();
		
	}
}
