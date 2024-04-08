package com.ykotsiuba.bookstore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(TestBookstoreApplication.class)
@TestPropertySource(properties = {
		"spring.liquibase.enabled=true",
		"spring.liquibase.change-log=classpath:db/changelog/test-changelog-master.xml"
})
class BookstoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
