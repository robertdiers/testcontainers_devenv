package com.example.demo;

import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.util.DbUrlPrinter;
import com.example.demo.util.StringRowMapper;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class DemoApplicationTests {

	@Autowired
	JdbcTemplate jdbc;

	@Test
	@SneakyThrows
	void postgesFakedWithH2Test() {

		DbUrlPrinter.printUrl(Optional.of(jdbc.getDataSource()));

		//create table
		jdbc.execute("CREATE TABLE IF NOT EXISTS testtable(testcolumn text, PRIMARY KEY( testcolumn ))");
		log.info("table created");

		//truncate table
		jdbc.execute("TRUNCATE TABLE testtable");
		log.info("truncate executed");

		//insert
		jdbc.execute("INSERT INTO testtable(testcolumn) values ('test')");
		log.info("insert executed");

		//select
		List<String> res = jdbc.query("select * from testtable", new StringRowMapper());
		res.stream().forEach(str -> log.info(str));

		Assert.assertTrue(res.size() == 1);
		Assert.assertTrue(res.get(0).equals("test"));

	}

}