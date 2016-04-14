package com.daqsoft.schedule.module;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.stereotype.Component;

import com.daqsoft.schedule.conf.ApplicationInfo;
import com.daqsoft.schedule.conf.MongoInfo;
import com.daqsoft.schedule.conf.ZKInfo;
import com.daqsoft.schedule.manager.ZKModule;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.ServerAddress;

@Component
public class MongoModule extends ZKModule
{
	@Autowired
	ApplicationInfo applicationInfo;
	@Autowired
	ZKInfo zkInfo;
	@PostConstruct
	public void init()
	{
		super.setZkInfo(zkInfo);
	}
	@Override
	public int start()
	{
		try
		{
			super.start();
			connectMongo();
		} catch (KeeperException | InterruptedException e)
		{
			return 1;
		}
		return 0;
	}

	private MongoTemplate mongoTemplate;

	public synchronized MongoTemplate getMongoTemplate()
	{
		return mongoTemplate;
	}
	/**
	 * 连接 mongo 实例
	 * 
	 * @throws KeeperException
	 * @throws InterruptedException
	 */
	private synchronized void connectMongo()
			throws KeeperException, InterruptedException
	{
		String data = new String(
				getZkClient().showData(applicationInfo.getMongoPath()));
		MongoInfo mongoInfo = MongoInfo.parseObject(data);
		List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
		for (String serverAddress : mongoInfo.getServerAddress().split(";"))
		{
			String[] ip_port = serverAddress.split(":");
			try
			{
				serverAddresses.add(new ServerAddress(ip_port[0],
						Integer.valueOf(ip_port[1])));
			} catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		}
		// Build a MongoOptions instances
		Builder mongoClientOptions = MongoClientOptions.builder();
		mongoClientOptions.maxWaitTime(mongoInfo.getTimeout());
		mongoClientOptions.connectTimeout(mongoInfo.getTimeout());
		MongoDbFactory mongoDbFactory = new SimpleMongoDbFactory(
				new MongoClient(serverAddresses, mongoClientOptions.build()),
				mongoInfo.getDbName());
		MappingMongoConverter converter = new MappingMongoConverter(
				mongoDbFactory, new MongoMappingContext());
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		// Instancing a spring mongoTemplate
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory,
				converter);
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void dataChangeProcess(WatchedEvent event)
	{
		super.dataChangeProcess(event);
		try
		{
			connectMongo();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	@PreDestroy
	public void destroy()
	{
		stop();
	}
	@Override
	public void stop()
	{
		getZkClient().close();
	}
}
