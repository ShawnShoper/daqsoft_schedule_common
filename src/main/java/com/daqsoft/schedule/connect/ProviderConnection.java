package com.daqsoft.schedule.connect;

import java.util.concurrent.TimeUnit;
/**
 * Thrift Connection base info
 * 
 * @author ShawnShoper
 *
 */
public class ProviderConnection
{
	private String host;
	private int port;
	private String group;
	private String provideName;
	private int timeout;
	private TimeUnit unit;
	private String version;
	private String originPath;

	public String getOriginPath()
	{
		return originPath;
	}
	public void setOriginPath(String originPath)
	{
		this.originPath = originPath;
	}
	public String getID()
	{
		return host + ":" + port + (group == null ? "" : "->" + group);
	}
	public String getVersion()
	{
		return version;
	}
	public void setVersion(String version)
	{
		this.version = version;
	}
	public String getHost()
	{
		return host;
	}
	public void setHost(String host)
	{
		this.host = host;
	}
	public int getPort()
	{
		return port;
	}
	public void setPort(int port)
	{
		this.port = port;
	}
	public String getGroup()
	{
		return group;
	}
	public void setGroup(String group)
	{
		this.group = group;
	}

	public String getProvideName()
	{
		return provideName;
	}
	public void setProvideName(String provideName)
	{
		this.provideName = provideName;
	}
	public int getTimeout()
	{
		return timeout;
	}
	public void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}
	public TimeUnit getUnit()
	{
		return unit;
	}
	public void setUnit(TimeUnit unit)
	{
		this.unit = unit;
	}

	@Override
	public String toString()
	{
		return "ThriftConnection [host=" + host + ", port=" + port + ", group="
				+ group + ", provideName=" + provideName + ", timeout="
				+ timeout + ", unit=" + unit + ", version=" + version
				+ ", originPath=" + originPath + "]";
	}

}
