package com.zeromarket.server;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "ZeroMarket Server",
        version = "v1",
        description = "ZeroMarket Spring Boot API & Admin Backend"
    )
)
@SpringBootApplication
public class ZeromarketServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeromarketServerApplication.class, args);
	}

}
