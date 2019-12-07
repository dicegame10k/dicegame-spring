package com.cb.dicegame.config;

import com.cb.dicegame.model.Player;
import com.cb.dicegame.model.PlayerRepository;
import com.cb.dicegame.service.DiceGameService;
import com.cb.dicegame.util.Log;
import com.cb.dicegame.util.SocketUtil;
import com.cb.dicegame.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class DiceGameLogoutSuccessHandler implements LogoutSuccessHandler {

	private DiceGameService diceGameService;
	private PlayerRepository playerRepository;
	private SocketUtil socketUtil;

	@Autowired
	public DiceGameLogoutSuccessHandler(DiceGameService diceGameService,
				PlayerRepository playerRepository, SocketUtil socketUtil) {
		this.diceGameService = diceGameService;
		this.playerRepository = playerRepository;
		this.socketUtil = socketUtil;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication auth)
			throws IOException, ServletException {
		if (auth != null) {
			String username = auth.getName();
			if (StringUtil.notNil(username)) {
				String logoutMsg = String.format("%s logged out", username);
				Log.info(logoutMsg);
				socketUtil.broadcastSystemChat(logoutMsg);

				Player p = playerRepository.findByName(username);
				diceGameService.removePlayer(p);
			}
		}

		// redirect back to the homepage... which will then get redirected back to the login
		String URL = request.getContextPath() + "/";
		response.setStatus(HttpStatus.OK.value());
		response.sendRedirect(URL);
	}

}
