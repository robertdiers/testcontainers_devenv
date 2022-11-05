package com.example.demo.systemtest;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.demo.devenv.DevEnv;
import com.example.demo.devenv.Sleeper;
import com.example.demo.util.DbUrlPrinter;
import com.example.demo.util.S3Connector;
import com.example.demo.util.StringRowMapper;

import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
	"spring.datasource.url=jdbc:postgresql://localhost:5432/postgres",
	"spring.datasource.username=testuser",
	"spring.datasource.password=testpassword",
	"s3.url=http://localhost:29000",
	"s3.username=minio-root-user",
	"s3.password=minio-root-password"
})
@Slf4j
class MyTestcontainersSystemTest {

	@Autowired
	JdbcTemplate jdbc;

	@Autowired
	S3Connector s3Connector;

	@BeforeAll
	@SneakyThrows
	private static void beforeAll() {

		// init development environment
		DevEnv.init(true);
		log.info("started DevEnv for SystemTests");

		// wait for initializer
		Sleeper.sleep(10000);
		log.info("DevEnv should be ready");
	}

	@Test
	@SneakyThrows
	void testPostGre() {
		
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

	@Test
	@SneakyThrows
	void s3Test() {

		String filename = "systemtest.txt";
		String content = "HalloWeltSystemtest!";
		String bucket = "mybucket-a";

		s3Connector.store(filename, content, bucket);
		log.info("s3 stored");

		String res = s3Connector.read(filename, bucket);
		log.info(res);

		Assert.assertTrue(res.equals(content));

	}

}