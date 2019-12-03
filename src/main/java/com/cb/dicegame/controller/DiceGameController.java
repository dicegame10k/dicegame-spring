package com.cb.dicegame.controller;

import com.cb.dicegame.model.Player;
import com.cb.dicegame.model.PlayerRepository;
import com.cb.dicegame.model.WowClass;
import com.cb.dicegame.service.DiceGameService;
import com.cb.dicegame.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.util.Map;

@Controller
public class DiceGameController {

	private DiceGameService diceGameService;
	private PlayerRepository playerRepository;

	@Autowired
	public DiceGameController(DiceGameService diceGameService, PlayerRepository playerRepository) {
		this.diceGameService = diceGameService;
		this.playerRepository = playerRepository;
	}

	@RequestMapping(value="/")
	public String index() {
		return "index"; // alias for src/main/resources/templates/ + x + .html
	}

	@GetMapping(value="/login")
	public String login() {
		return "login"; // alias for src/main/resources/templates/ + x + .html
	}

	@PostMapping(value="/signUp")
	@ResponseBody
	public String signUp(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		String username = parameterMap.get("username")[0];
		if (playerRepository.findByName(username) != null)
			return "player_already_exists";

		String password = parameterMap.get("password")[0];
		String wowclass = parameterMap.get("wowclass")[0];
		Player p = new Player(username, password, WowClass.DRUID, 0);
		playerRepository.save(p);
		Log.info(String.format("Successfully created player %s", username));
		return "success";
	}

	@PostMapping(value="/resetPassword")
	@ResponseBody
	public String resetPassword(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		String username = parameterMap.get("username")[0];
		if (playerRepository.findByName(username) == null)
			return "player_does_not_exist";

		// TODO: how to update player password?
		String password = parameterMap.get("password")[0];
		Log.info(String.format("Successfully reset player %s's password", username));
		return "success";
	}

	/**
	 * Handles socket message sent to /app/lightUp
	 */
	@MessageMapping("/lightUp")
	@SendTo("/topic/gameState")
	public String lightUp() {
		diceGameService.lightUp();
		return "sup boys";
	}

}
