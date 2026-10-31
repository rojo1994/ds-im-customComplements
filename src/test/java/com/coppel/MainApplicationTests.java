package com.coppel;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * El contexto levanta sin el application.yml local: sólo necesita las URL de ps y los orígenes de CORS.
 */
@SpringBootTest(properties = {
	"app.custom-complements.ps-url=http://localhost:8081/ps/im/custom-complements/api",
	"app.custom-complements.centrales-url=http://localhost:8082",
	"app.allowedOrigins=*",
	"app.allowedMethods=*",
	"app.allowedHeaders=*"
})
class MainApplicationTests {

	@Test
	void contextLoads() {
	}

}
