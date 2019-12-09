import {wowClassFromEnum} from '../util.js';
import {normalizeWowClasses} from '../util.js';

const React = require('react');
const ReactDOM = require('react-dom');

class GameHistory extends React.Component {

	constructor(props) {
		super(props);
		this.state = { games: [] };
	}

	componentDidMount() {
		fetch('/gameHistory')
		.then((response) => response.text())
		.then((games) => {
			try {
				games = JSON.parse(games);
				for (let i = 0; i < games.length; i += 1) {
					let game = games[i];
					game.winningPlayer.wowClass = wowClassFromEnum(game.winningPlayer.wowClass);
					game.players = normalizeWowClasses(game.players);
				}
			} catch (e) {
				console.log("Error parsing game history", e);
				gameHistory = [];
			}

			this.setState({ games: games });
		}, (e) => { console.error(e); });
	}
d
	render() {
		return (
			<div className="dg-gh container">
				<div className="dg-gh-num-games">{this.state.games.length} games played</div>
				<table className="dg-recount table-dark table-sm table-striped table-hover">
					<thead>
						<tr>
							<th className="dg-gh-column">Game time</th>
							<th className="dg-gh-column">Winner</th>
							<th className="dg-gh-column">Players</th>
							<th className="dg-gh-column">Total number of rolls</th>
						</tr>
					</thead>
					<tbody>
						{this.state.games.map((game, i) => {
							return <tr key={i}>
								<td className="dg-gh-column">{game.gameTimeStr}</td>
								<td className={`${game.winningPlayer.wowClass} dg-gh-column`}>{game.winningPlayer.name}</td>
								<td className="dg-gh-column">
									{game.players.map((player, j) => {
										let playerName = player.name;
										if (j < game.players.length - 1)
											playerName += ", ";

										return <span key={j} className={`${player.wowClass}`}>{playerName}</span>
									})}
								</td>
								<td className="dg-gh-column">{game.numRolls}</td>
							</tr>;
						})}
					</tbody>
				</table>
			</div>
		)
	}
}

ReactDOM.render(
	<GameHistory/>,
	document.getElementById('react')
)