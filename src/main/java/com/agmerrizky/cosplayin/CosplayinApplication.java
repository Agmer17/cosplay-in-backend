package com.agmerrizky.cosplayin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CosplayinApplication {

	public static void main(String[] args) {
		SpringApplication.run(CosplayinApplication.class, args);
	}

}
