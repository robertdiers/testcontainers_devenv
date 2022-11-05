package com.example.demo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.example.demo.util.DbUrlPrinter;
import com.example.demo.util.S3Connector;
import com.example.demo.util.StringRowMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DemoComponent {

    @Autowired
	JdbcTemplate jdbc;

	@Autowired
	S3Connector s3Connector;

	@Value( "${testmode:false}" )
    private boolean testmode;

    @PostConstruct
	@SneakyThrows
	void postgesTest() {

		if (testmode) {
			DbUrlPrinter.printUrl(Optional.of(jdbc.getDataSource()));
			
			//create table
			jdbc.execute("CREATE TABLE IF NOT EXISTS testtable(testcolumn text, PRIMARY KEY( testcolumn ))");
			log.info("table created");

			//insert
			jdbc.execute("INSERT INTO testtable(testcolumn) values ('"+UUID.randomUUID().toString()+"')");
			log.info("insert executed");

			//select
			List<String> res = jdbc.query("select * from testtable", new StringRowMapper());
			res.stream().forEach(str -> log.info(str));
		}

	}

	@PostConstruct
	@SneakyThrows
	void s3Test() {

		if (testmode && s3Connector.isInitialized()) {
			String filename = "test.txt";
			String content = "HalloWelt!";
			String bucket = "mybucket-a";

			s3Connector.store(filename, content, bucket);
			log.info("s3 stored");

			String res = s3Connector.read(filename, bucket);
			log.info(res);
		}

	}
    
}