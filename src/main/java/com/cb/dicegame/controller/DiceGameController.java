package com.cb.dicegame.controller;

import com.cb.dicegame.model.ChatMessage;
import com.cb.dicegame.model.Player;
import com.cb.dicegame.model.PlayerRepository;
import com.cb.dicegame.model.WowClass;
import com.cb.dicegame.service.DiceGameService;
import com.cb.dicegame.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;
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
		WowClass wowclass = WowClass.fromString(parameterMap.get("wowclass")[0]);
		Player p = new Player(username, password, wowclass, 0);
		playerRepository.save(p);
		Log.info(String.format("Successfully created player %s", username));
		return "success";
	}

	@PostMapping(value="/resetPassword")
	@ResponseBody
	public String resetPassword(HttpServletRequest request) {
		Map<String, String[]> parameterMap = request.getParameterMap();
		String username = parameterMap.get("username")[0];
		Player p = playerRepository.findByName(username);
		if (p == null)
			return "player_does_not_exist";

		String password = parameterMap.get("password")[0];
		p.setPassword(password);
		playerRepository.save(p);
		Log.info(String.format("Successfully reset player %s's password", username));
		return "success";
	}

	@GetMapping("/heartbeat")
	@ResponseBody
	public String heartbeat() {
		return "still alive";
	}

	@GetMapping("/enterLobby")
	@ResponseBody
	public Player enterLobby(Principal principal) {
		Player p = getPlayer(principal);
		diceGameService.enterLobby(p);
		return p;
	}

	@GetMapping("/recount")
	@ResponseBody
	public List<Player> recount() {
		Sort.Order one = Sort.Order.desc("dkp");
		Sort.Order two = Sort.Order.asc("name");
		return playerRepository.findAll(Sort.by(one, two));
	}

	/**
	 * Handles socket message sent to /app/chat
	 */
	@MessageMapping("/chat")
	@SendTo("/topic/chat")
	public ChatMessage chat(Principal principal, @RequestBody String msg) {
		Player p = getPlayer(principal);
		return new ChatMessage(p, msg);
	}

	/**
	 * Handles socket message sent to /app/lightUp
	 */
	@MessageMapping("/lightUp")
	public void lightUp(Principal principal) {
		Player p = getPlayer(principal);
		diceGameService.lightUp(p);
	}

	@MessageMapping("/roll")
	public void roll(Principal principal) {
		Player p = getPlayer(principal);
		diceGameService.roll(p, false);
	}

	@MessageMapping("/forceRoll")
	public void forceRoll(Principal principal) {
		Player p = getPlayer(principal);
		diceGameService.roll(p, true);
	}

	private Player getPlayer(Principal principal) {
		String username = principal.getName();
		return playerRepository.findByName(username);
	}

}
