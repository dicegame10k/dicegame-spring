package com.cb.dicegame;

import com.cb.dicegame.model.Player;
import com.cb.dicegame.model.PlayerRepository;
import com.cb.dicegame.model.WowClass;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.HashMap;

@SpringBootApplication
public class DiceGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiceGameApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(PlayerRepository pr) {
		return (args) -> {
			HashMap<String, WowClass> map = new HashMap<String, WowClass>();
			map.put("Deeb", WowClass.MONK);
			map.put("Kron", WowClass.DRUID);
			map.put("Chapu", WowClass.PALADIN);
			map.put("Rhotwo", WowClass.SHAMAN);
			map.put("Terwine", WowClass.HUNTER);
			map.put("Tyenne", WowClass.ROGUE);
			map.put("Saacul", WowClass.WARRIOR);
			map.put("Dantodan", WowClass.WARLOCK);
			map.put("Z", WowClass.PRIEST);
			map.put("Vierth", WowClass.MAGE);
			map.put("John", WowClass.DEMON_HUNTER);
			map.put("Karidia", WowClass.DEATH_KNIGHT);
			map.put("Grockley", WowClass.WARLOCK);
			map.put("Trendeeb", WowClass.HUNTER);
			map.put("Fiend", WowClass.ROGUE);
			map.put("Bublekid", WowClass.WARRIOR);
			map.put("Braedin", WowClass.MONK);
			Arrays.asList("Deeb,Kron,Chapu,Rhotwo,Terwine,Tyenne,Saacul,Dantodan,Z,Vierth,John,Karidia,Grockley,Trendeeb,Fiend,Bublekid,Braedin"
				.split(","))
				.forEach((name) -> {
					Player p = new Player(name, "p", map.get(name), name.length());
					pr.save(p);
				});

			pr.findAll(Sort.by(Sort.Direction.DESC, "dkp")).forEach(System.out::println);
		};
	}

}
