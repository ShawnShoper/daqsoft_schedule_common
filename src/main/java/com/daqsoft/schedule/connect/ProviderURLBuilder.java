package com.daqsoft.schedule.connect;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.shoper.util.StringUtil;
import org.springframework.util.Assert;
/**
 * thrift连接 url 以及对象互转构造器...
 * 
 * @author ShawnShoper
 *
 */
public class ProviderURLBuilder
{
	public static ThriftURL Builder()
	{
		return new ThriftURL();
	}

	public static class ThriftURL
	{
		private String protocol = "thrift://";

		public String build(ProviderConnection tc)
		{
			checkParamter(tc);
			String url = protocol + tc.getHost() + ":" + tc.getPort();
			StringBuilder parameter = new StringBuilder();
			parameter.append("timeout=" + tc.getTimeout() + "&timeUnit="
					+ StringUtil.urlEncode(tc.getUnit().name()));
			if (tc.getGroup() != null && !tc.getGroup().isEmpty())
			{
				parameter.append("&group=" + tc.getGroup());
			}
			if (tc.getVersion() != null && !tc.getVersion().isEmpty())
			{
				parameter.append(
						"&version=" + StringUtil.urlEncode(tc.getVersion()));
			}
			if (!tc.getProvideName().isEmpty())
			{
				parameter.append("&provides=" + tc.getProvideName());
			}
			url = url + "?" + parameter;
			return url;
		}

		public ProviderConnection deBuild(String url)
		{
			checkProtocol(url);
			ProviderConnection thriftConnection = null;
			try
			{

				// 欺骗一下 URL 的 url handler..
				thriftConnection = new ProviderConnection();
				URL purl = new URL(url.replace(protocol, "http://"));
				thriftConnection.setOriginPath(url);
				thriftConnection.setHost(purl.getHost());
				thriftConnection.setPort(purl.getPort() == -1
						? purl.getDefaultPort()
						: purl.getPort());
				String query = purl.getQuery();
				String[] qs = query.split("&");
				for (String q : qs)
				{
					String[] q_v = q.split("=");
					String queryName = q_v[0];
					String value = q_v[1];
					try
					{
						value = StringUtil.urlDecode(value);
						switch (queryName)
						{
							case "version" :
								thriftConnection.setVersion(value);
								break;
							case "group" :
								thriftConnection.setGroup(value);
								break;
							case "timeout" :
								thriftConnection
										.setTimeout(Integer.valueOf(value));
								break;
							case "timeUnit" :
								thriftConnection
										.setUnit(TimeUnit.valueOf(value));
								break;
							case "provides" :
								thriftConnection.setProvideName(value);
								break;
							default :
								break;
						}
					} catch (UnsupportedEncodingException e1)
					{
						e1.printStackTrace();
					}
				}

			} catch (MalformedURLException e)
			{
				e.printStackTrace();
			}
			return thriftConnection;
		}

		private void checkProtocol(String url)
		{
			if (!url.startsWith(protocol))
				throw new IllegalArgumentException(
						"The protocol is invalid,modify the protocol to [thrift://]");
		}

		private void checkParamter(ProviderConnection tc)
		{
			Assert.notNull(tc, "the [thriftConnection] argument can not null");
			Assert.notNull(tc.getHost(), "the [host] argument can not be null");
			if (tc.getPort() < 0 || tc.getPort() > 65535)
				throw new IllegalArgumentException(
						"the [port] argument must be 0-65535");
			Assert.notNull(tc.getProvideName(),
					"the [provideName] argument can not null");
			if (tc.getTimeout() == 0)
				tc.setTimeout(20);
			if (tc.getUnit() == null)
				tc.setUnit(TimeUnit.SECONDS);
		}
	}
}
