package com.cb.dicegame.controller;

import com.cb.dicegame.db.DiceGameRecord;
import com.cb.dicegame.db.DiceGameRecordRepository;
import com.cb.dicegame.db.Player;
import com.cb.dicegame.db.PlayerRepository;
import com.cb.dicegame.model.Recount;
import com.cb.dicegame.model.WowClass;
import com.cb.dicegame.service.DiceGameService;
import com.cb.dicegame.service.RecountService;
import com.cb.dicegame.util.DiceGameUtil;
import com.cb.dicegame.util.Log;
import com.cb.dicegame.util.SocketUtil;
import com.cb.dicegame.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@Controller
public class DiceGameController {

	private DiceGameService diceGameService;
	private RecountService recountService;
	private PlayerRepository playerRepository;
	private DiceGameRecordRepository diceGameRecordRepository;
	private SocketUtil socketUtil;

	@Autowired
	public DiceGameController(DiceGameService diceGameService, RecountService recountService,
			PlayerRepository playerRepository, DiceGameRecordRepository diceGameRecordRepository, SocketUtil socketUtil) {
		this.diceGameService = diceGameService;
		this.recountService = recountService;
		this.playerRepository = playerRepository;
		this.diceGameRecordRepository = diceGameRecordRepository;
		this.socketUtil = socketUtil;
	}

	@RequestMapping(value="/")
	public String index() {
		return "index"; // alias for src/main/resources/templates/ + x + .html
	}

	// the post mapping for the /login action is automatically handled by spring
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

	@PostMapping("/changeWowClass")
	@ResponseBody
	public Player changeWowClass(HttpServletRequest request) {
		Player p = getPlayer();
		if (p == null)
			return null;

		String newClassStr = request.getParameterMap().get("newWowClass")[0];
		WowClass newWowClass = WowClass.fromString(newClassStr);
		diceGameService.updatePlayerInfo(p, newWowClass);
		p.setWowClass(newWowClass);
		playerRepository.save(p);
		return p;
	}

	@GetMapping("/heartbeat")
	@ResponseBody
	public String heartbeat() {
		return "still alive";
	}

	@GetMapping("/enterLobby")
	@ResponseBody
	public Player enterLobby() {
		Player p = getPlayer();
		diceGameService.enterLobby(p);
		return p;
	}

	@GetMapping("/recount")
	@ResponseBody
	public List<Recount> recount() {
		return recountService.recount();
	}

	@GetMapping(value="/games")
	public String games() {
		return "games"; // alias for src/main/resources/templates/ + x + .html
	}

	@GetMapping(value="/gameHistory")
	@ResponseBody
	public List<DiceGameRecord> gameHistory() {
		Sort.Order one = Sort.Order.desc("gameTime");
		return diceGameRecordRepository.findAll(Sort.by(one));
	}

	/**
	 * Handles socket message sent to /app/chat
	 */
	@MessageMapping("/chat")
	public void chat(Principal principal, @RequestBody String msg) {
		Player p = getPlayer(principal);
		socketUtil.broadcastChat(p, msg);
	}

	/**
	 * Handles socket message sent to /app/lightUp.
	 * Needs a principal because SecurityContextHolder.getContext().getAuthentication()
	 * is null for some reason.
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

	@MessageMapping("/kick")
	public void kick(Principal principal, @RequestBody String username) {
		Player playerWhoKicked = getPlayer(principal);
		if (StringUtil.areEqual(playerWhoKicked.getName(), username)) {
			socketUtil.sendSystemChat(playerWhoKicked, "You can't kick yourself failkid");
			return;
		}

		Player p = playerRepository.findByName(username);
		if (p == null) {
			socketUtil.sendSystemChat(playerWhoKicked, String.format("%s is not a player", username));
			return;
		}

		if (!diceGameService.isPlayerInLobbyOrGame(p)) {
			socketUtil.sendSystemChat(playerWhoKicked, String.format("%s is not in the game", username));
			return;
		}

		String kickMsg = String.format("%s kicked by %s", username, playerWhoKicked.getName());
		Log.info(kickMsg);
		socketUtil.broadcastSystemChat(kickMsg);

		diceGameService.removePlayer(p);
	}

	@MessageMapping("/stuck")
	public void stuck(Principal principal) {
		Player p = getPlayer(principal);
		diceGameService.resetGame();
		socketUtil.broadcastSystemChat(String.format("%s reset the game", p.getName()));
	}

	/**
	 * This should be called from socket handler endpoints (the @MessageMapping methods)
	 * Because SecurityContextHolder.getContext().getAuthentication() is null
	 */
	private Player getPlayer(Principal p) {
		String username = DiceGameUtil.getPlayerName(p);
		return playerRepository.findByName(username);
	}

	/**
	 * This can be called from @GetMapping methods (because there is a security context)
	 */
	private Player getPlayer() {
		String username = DiceGameUtil.getPlayerName();
		return playerRepository.findByName(username);
	}

}
