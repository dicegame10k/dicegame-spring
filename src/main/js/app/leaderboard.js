import {wowClassFromEnum} from './dicegameutil.js';
import {normalizeWowClasses} from './dicegameutil.js';

const React = require('react');

export class Leaderboard extends React.Component {

	constructor(props) {
		super(props);
		this.state = { players: [] };
	}

	componentDidMount() {
		fetch('/leaderboard')
		.then((response) => response.text())
        .then((players) => {
			try {
				players = normalizeWowClasses(JSON.parse(players));
			} catch (e) {
				players = [];
				alert("Failed to load leaderboard");
				console.error("Failed to load leaderboard", e);
			}

			this.setState({ players: players });
        });
	}

	render() {
		//TODO: table hover doesn't work
		//TODO: needs a max height/scrollbar
		return (
			<div className="dg-leaderboard-container">
				<table className="dicegame-leaderboard table table-striped table-dark table-bordered table-hover table-sm">
					<thead>
						<th className="dicegame-leaderboard-header">Username</th>
						<th className="dicegame-leaderboard-header">
							<div className="dg-dkp-header" data-toggle="tooltip"
									title="DiceGame Kill Points: You get 1 DKP for each person you beat in a game">
								DKP
							</div>
						</th>
					</thead>
					<tbody id="leaderboardBody">
						{this.state.players.map((player, i) => {
							return <tr key={i} className={`${player.wowClass}-bg dg-leaderboard-text`}>
								<td className={`dicegame-leaderboard-cell`}>{player.name}</td>
								<td className={`dicegame-leaderboard-cell`}>{player.dkp}</td>
							</tr>;
						})}
					</tbody>
				</table>
			</div>
		)
	}
}
