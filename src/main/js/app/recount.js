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
				alert("Failed to load recount");
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
						<span className="dg-rc-cell-right dg-dkp-header" data-toggle="tooltip" title="DiceGame Kill Points: You get 1 DKP for each person you beat in a game">
							DKP
						</span>
					</div>

					<div>
						{players.map((player, i) => {
							let width = ((player.dkp / maxDkp) * 100) + '%';
							this.playerToDkpWidth.push({ player: player.name + '-rc', width: width });

							return <div key={i} className="dg-rc-row-container">
								<div className={`${player.wowClass}-bg progress-bar dg-rc-progress-bar`} id={`${player.name}-rc`}/>
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
