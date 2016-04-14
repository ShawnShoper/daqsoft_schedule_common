package com.daqsoft.schedule.builder;

import org.junit.Test;
import org.shoper.util.Common;

import com.daqsoft.schedule.connect.ProviderConnection;
import com.daqsoft.schedule.connect.ProviderURLBuilder;

public class ThriftURLBuilder_Test {
	private static String URL = null;
	@Test
	public void build_Test(){
		ProviderConnection thriftConnection = new ProviderConnection();
		thriftConnection.setGroup("test");
		thriftConnection.setVersion("1.1.1");
		thriftConnection.setHost(Common.getCurrIP());
		thriftConnection.setPort(2222);
		thriftConnection.setProvideName("test");
		String url = ProviderURLBuilder.Builder().build(thriftConnection);
		URL = url;
		System.out.println(url);
	}
	@Test
	public void deBuild_Test(){
		ProviderConnection thriftConnection = ProviderURLBuilder.Builder().deBuild(URL);
		System.out.println(thriftConnection);
	}
}
