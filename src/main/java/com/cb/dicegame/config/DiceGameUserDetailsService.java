package com.cb.dicegame.config;

import com.cb.dicegame.db.Player;
import com.cb.dicegame.db.PlayerRepository;
import com.cb.dicegame.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class DiceGameUserDetailsService implements UserDetailsService {

	private final PlayerRepository playerRepository;

	@Autowired
	public DiceGameUserDetailsService(PlayerRepository playerRepository) {
		this.playerRepository = playerRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
		Player player = this.playerRepository.findByName(name);
		if (player == null) {
			String msg = String.format("'%s' attempted to login. Player not found", name);
			Log.info(msg);
			throw new UsernameNotFoundException(msg);
		}

		Log.info(String.format("'%s' attempted to login. Player found", name));
		return new User(player.getName(), player.getPassword(),
				AuthorityUtils.createAuthorityList("ROLE_DICEGAME_PLAYER"));
	}

}
