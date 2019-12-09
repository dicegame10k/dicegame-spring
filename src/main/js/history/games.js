import ReactTooltip from 'react-tooltip';

import {wowClassFromEnum} from '../util.js';
import {normalizeWowClasses} from '../util.js';

const React = require('react');
const ReactDOM = require('react-dom');

class GameHistory extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			games: [],
			sort: {
				column: 'gameTime',
				order: 'desc',
			},
		};

		this.loadGames = this.loadGames.bind(this);
		this.updateSort = this.updateSort.bind(this);
	}

	componentDidMount() {
		this.loadGames(this.state.sort);
	}

	loadGames(sort) {
		let endpoint = '/gameHistory?column=' + sort.column + '&order=' + sort.order;
		fetch(endpoint)
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

			this.setState({
				games: games,
				sort: sort,
			});
		}, (e) => { console.error(e); });
	}

	updateSort(event) {
		let currSortColumn = this.state.sort.column;
		let currSortOrder = this.state.sort.order;
		let newSortColumn = event.target.id;
		let newSortOrder = 'desc';
		// sort order only switches if clicking on the same column
		if (currSortColumn === newSortColumn) {
			if (currSortOrder === 'desc')
				newSortOrder = 'asc';
		}

		let sort = {
			column: newSortColumn,
			order: newSortOrder
		};

		this.loadGames(sort);
	}

	render() {
		let sortColumn = this.state.sort.column;
		let arrowClass = (this.state.sort.order === 'desc') ? 'dg-down-arrow' : 'dg-up-arrow';
		return (
			<div className="dg-gh container">
				<div className="dg-gh-num-games">{this.state.games.length} games played</div>
				<table className="dg-recount table-dark table-sm table-striped table-hover">
					<thead>
						<tr>
							<th id="gameTime" className="dg-gh-column dg-gh-sort" onClick={this.updateSort} data-tip data-for="sortTooltip">
								Game time
								<span className={(sortColumn == 'gameTime') ? `${arrowClass}` : ''}/>
							</th>
							<th className="dg-gh-column">Winner</th>
							<th id="numPlayers" className="dg-gh-column dg-gh-sort" onClick={this.updateSort} data-tip data-for="sortTooltip">
								Players
								<span className={(sortColumn == 'numPlayers') ? `${arrowClass}` : ''}/>
							</th>
							<th id="numRolls" className="dg-gh-column dg-gh-sort" onClick={this.updateSort} data-tip data-for="sortTooltip">
								Number of rolls
								<span className={(sortColumn == 'numRolls') ? `${arrowClass}` : ''}/>
							</th>
							<ReactTooltip id="sortTooltip" effect="solid" place="bottom">
								<span>Click to sort</span>
							</ReactTooltip>
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