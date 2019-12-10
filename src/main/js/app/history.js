import ReactTooltip from 'react-tooltip';

import {wowClassFromEnum} from '../util.js';
import {normalizeWowClasses} from '../util.js';

const React = require('react');

export class GameHistory extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			games: [],
			endpoint: '/allGameHistory',
			playerFilter: '',
			sort: {
				column: 'gameTime',
				order: 'desc',
			},
		};

		this.loadGames = this.loadGames.bind(this);
		this.updateSort = this.updateSort.bind(this);
		this.filterByWins = this.filterByWins.bind(this);
		this.filterByPlayerHistory = this.filterByPlayerHistory.bind(this);
		this.clearFilter = this.clearFilter.bind(this);
		this.getFilterMessageElem = this.getFilterMessageElem.bind(this);
		this.getURL = this.getURL.bind(this);
	}

	componentDidMount() {
		this.loadGames(this.state.endpoint, this.state.playerFilter, this.state.sort);
	}

	loadGames(endpoint, playerFilter, sort) {
		let endpointURL = this.getURL(endpoint, playerFilter, sort);
		fetch(endpointURL)
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
				endpoint: endpoint,
				playerFilter: playerFilter,
				sort: sort,
			});
		}, (e) => { console.error(e); });
	}

	updateSort(event) {
		let currSortColumn = this.state.sort.column;
		let currSortOrder = this.state.sort.order;
		let newSortColumn = event.target.id;
		if (!newSortColumn)
			newSortColumn = event.target.parentElement.id;

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

		this.loadGames(this.state.endpoint, this.state.playerFilter, sort);
	}

	filterByWins(event) {
		let playerName = event.target.innerText;
		this.loadGames('/winHistory', playerName, this.state.sort);
	}

	filterByPlayerHistory(event) {
		let playerName = event.target.innerText;
		// strip out the comma
		let commaIndex = playerName.indexOf(',');
		if (commaIndex > -1)
			playerName = playerName.substring(0, commaIndex);

		this.loadGames('/playerHistory', playerName, this.state.sort);
	}

	clearFilter() {
		this.loadGames('/allGameHistory', '', this.state.sort);
	}

	getURL(endpoint, playerFilter, sort) {
		let sortParams = 'column=' + sort.column + '&order=' + sort.order;
		let endpointURL = endpoint;
		if (endpoint == '/allGameHistory')
			endpointURL += '?' + sortParams;
		else
			endpointURL += '?player=' + playerFilter + '&' + sortParams;

		return endpointURL;
	}

	getFilterMessageElem() {
		let games = this.state.games;
		let endpoint = this.state.endpoint;
		let player = this.state.playerFilter;
		let filterElem = '';
		if (endpoint === '/allGameHistory')
			filterElem = <div className="dg-gh-num-games">{games.length} games played</div>;
		else {
			let filterWord = (endpoint === '/winHistory') ? 'won' : 'played';
			filterElem = <div className="dg-gh-num-games">
				Filtering on: Games {filterWord} by {player} ({games.length} found)
				<button className="btn btn-link" onClick={this.clearFilter}>Clear filter</button>
			</div>;
		}

		return filterElem;
	}

	render() {
		let sortColumn = this.state.sort.column;
		let arrowClass = (this.state.sort.order === 'desc') ? 'dg-down-arrow' : 'dg-up-arrow';
		let filterElem = this.getFilterMessageElem();

		return (
			<div className="dg-gh">
				{filterElem}
				<table className="dg-recount table-dark table-sm table-striped table-hover">
					<thead>
						<tr>
							<th id="gameTime" className="dg-gh-column dg-gh-pointer" onClick={this.updateSort} data-tip data-for="sortTooltip">
								Game time (Pacific)
								<span className={(sortColumn == 'gameTime') ? `${arrowClass}` : ''}/>
							</th>
							<th className="dg-gh-column">Winner</th>
							<th id="numPlayers" className="dg-gh-column dg-gh-pointer" onClick={this.updateSort} data-tip data-for="sortTooltip">
								Players
								<span className={(sortColumn == 'numPlayers') ? `${arrowClass}` : ''}/>
							</th>
							<th id="numRolls" className="dg-gh-column dg-gh-pointer" onClick={this.updateSort} data-tip data-for="sortTooltip">
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
								<td onClick={this.filterByWins} className={`${game.winningPlayer.wowClass} dg-gh-column dg-gh-pointer`}>
									{game.winningPlayer.name}
								</td>
								<td className="dg-gh-column">
									{game.players.map((player, j) => {
										let playerName = player.name;
										if (j < game.players.length - 1)
											playerName += ", ";

										return <span key={j} className={`${player.wowClass} dg-gh-pointer`} onClick={this.filterByPlayerHistory}
											title={`${j} DKP won`}>
											{playerName}
										</span>
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
