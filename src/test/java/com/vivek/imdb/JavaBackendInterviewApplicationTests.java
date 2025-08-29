package com.vivek.imdb;

import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MockServerTest
class JavaBackendInterviewApplicationTests {

	MockServerClient client;

	@Test
	void contextLoads() {

	}

}
