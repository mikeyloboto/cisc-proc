package com.evilbas.discproc;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@PropertySource("classpath:application.properties")
@PropertySource("file:${APPLICATION_EXT_PROPERTIES}/discproc-ws/env.properties")
@MapperScan("com.evilbas.discproc.dao.sql")
@ComponentScan(basePackages = "com.evilbas.discproc")
@ComponentScan("com.evilbas.metrics")
@EnableAsync
public class ClientConfig {

	private Logger log = LoggerFactory.getLogger(ClientConfig.class);

}
