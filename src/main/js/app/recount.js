import ReactTooltip from 'react-tooltip';

import {wowClassFromEnum} from './dicegameutil.js';
import {normalizeWowClasses} from './dicegameutil.js';

const React = require('react');

export class Recount extends React.Component {

	constructor(props) {
		super(props);
		this.state = { players: [] };
		this.playerToDkpWidth = [];
	}

	componentDidMount() {
		fetch('/recount')
		.then((response) => response.text())
		.then((players) => {
			//TODO: check response code for a 302 redirect to the login (everywhere not just here)
			try {
				players = normalizeWowClasses(JSON.parse(players));
			} catch (e) {
				players = [];
				console.error("Failed to load recount", e);
			}

			this.setState({ players: players });
		});
	}

	componentDidUpdate() {
		window.requestAnimationFrame(() => {
			for (let i = 0; i < this.playerToDkpWidth.length; i += 1) {
				let p = this.playerToDkpWidth[i];
				let progressBarElem = document.getElementById(p.player);
				if (progressBarElem)
					progressBarElem.style.width = p.width;
			}
		});
	}

	render() {
		this.playerToDkpWidth = [];
		let players = this.state.players;
		let maxDkp = 1;
		if (players[0] && players[0].dkp > 0)
			maxDkp = players[0].dkp;

		return (
			<div>
				<div className="dg-recount table-dark table-sm">
					<div className="dg-rc-header">
						<span className="dg-rc-cell-left">Username</span>
						<span className="dg-rc-cell-right dg-dkp-header" data-tip data-for="dkpHeader">
							DKP
						</span>
						<ReactTooltip id="dkpHeader" effect="solid">
							<span>DiceGame Kill Points: You get 1 DKP for each person you beat in a game</span>
						</ReactTooltip>
					</div>

					<div>
						{players.map((player, i) => {
							let width = ((player.dkp / maxDkp) * 100) + '%';
							this.playerToDkpWidth.push({ player: player.name + '-rc', width: width });

							return <div key={i} className="dg-rc-row-container" data-tip data-for={`${player.name}-rc-tt`}>
								<div className={`${player.wowClass}-bg progress-bar dg-rc-progress-bar`} id={`${player.name}-rc`}
									/>
								<ReactTooltip id={`${player.name}-rc-tt`} place="right">
									<div>Games played: {player.dkp}</div>
									<div>Games won: {player.dkp}</div>
									<div>Win %: {player.dkp}</div>
									<div>Avg dkp/game: {player.dkp / 5}</div>
									<div>Avg # players/game: {player.dkp / 5}</div>
								</ReactTooltip>
								<div className="dg-rc-row">
									<span className="dg-rc-cell-left">{player.name}</span>
									<span className="dg-rc-cell-right">{player.dkp}</span>
								</div>
							</div>;
						})}
					</div>
				</div>
			</div>
		)
	}
}
