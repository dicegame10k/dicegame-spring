package com.cb.dicegame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * For AWS deployment to elasticbeanstalk, look for the file application.properties.forDiceGameAWS
 * and use that as the application.properties
 */
@SpringBootApplication
public class DiceGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiceGameApplication.class, args);
	}

}
