package com.cb.dicegame;

import com.cb.dicegame.model.LobbyRepository;
import com.cb.dicegame.model.Player;
import com.cb.dicegame.model.PlayerRepository;
import com.cb.dicegame.model.WowClass;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class DiceGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiceGameApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(LobbyRepository lr, PlayerRepository pr) {
		return (args) -> {
			Arrays.asList("Deebz,Kron,Chapu,Rhotwo,Terwine,Tyenne,Saacul,Dantodan".split(","))
				.forEach((name) -> {
					Player p = new Player(name, "password", WowClass.DRUID, 0);
					lr.save(p);
					pr.save(p);
				});

			lr.findAll().forEach(System.out::println);
		};
	}

}
