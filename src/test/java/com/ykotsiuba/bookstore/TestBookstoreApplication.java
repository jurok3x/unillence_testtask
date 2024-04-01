package com.ykotsiuba.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@TestPropertySource(properties = {
		"spring.liquibase.enabled=true",
		"spring.liquibase.change-log=classpath:db/changelog/test-changelog-master.xml"
})
public class TestBookstoreApplication {

	private static final String DATABASE_NAME = "testdb";
	private static final String USERNAME = "testuser";
	private static final String PASSWORD = "testpass";

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"))
				.withDatabaseName(DATABASE_NAME)
				.withUsername(USERNAME)
				.withPassword(PASSWORD);
	}

	public static void main(String[] args) {
		SpringApplication.from(BookstoreApplication::main).with(TestBookstoreApplication.class).run(args);
	}

}
