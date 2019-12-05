const React = require('react');

export class DiceGame extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		return (
			<div>
				<table className="dg-playing-field">
					<tr>
						<th><h3 className="dg-field-header">Lobby</h3></th>
						<th></th>
						<th><h3 className="dg-field-header">Graveyard</h3></th>
					</tr>
					<tr>
						<Lobby lobby={this.props.lobby}/>
						<Game gameState={this.props.gameState} player={this.props.player}
							lightUp={this.props.lightUp} roll={this.props.roll}/>
						<Graveyard graveyard={this.props.gameState.graveyard}/>
					</tr>
				</table>
			</div>
		)
	}

}

class Game extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		let myself = this.props.player;
		let gameInProgress = this.props.gameState.gameInProgress;
		let dgPlayers = this.props.gameState.dgPlayers;
		let currentlyRollingPlayer = this.props.gameState.currentlyRollingPlayer;
		let currentRoll = this.props.gameState.currentRoll;

		let lightUpBtn = gameInProgress ? '' :
			<button onClick={this.props.lightUp} className="btn btn-danger light-up-btn">Light Up</button>;

		let rollBtn = currentlyRollingPlayer.name !== myself.name ? '' :
			<button onClick={this.props.roll} id="rollButton" className={`btn roll-btn ${myself.wowClass}-bg`}>Roll</button>;

		let fireGif = !gameInProgress ? '' : <img id="fire" className="fire" src="/resources/static/images/fire.gif"/>;

		let dgPlayerCards = !gameInProgress ? '' :
			dgPlayers.map((player, i) => {
				return <div key={i} id={`${player.name}`}
						className={`wow-card-container text-center player-in-game rounded ${player.wowClass}-bg`}>
							<span className="dg-player-in-game-name">{player.name}</span>
					</div>
			});

		let currRollElem = !gameInProgress ? '' :
			<div id="currRoll" className="currentRoll">{currentRoll}</div>

		return (
			<td className="dg-game-td">
				{lightUpBtn}
				{fireGif}
				{dgPlayerCards}
				{currRollElem}
				{rollBtn}
			</td>
		)
	}
}

class Lobby extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		return (
			<td className="dg-lobby-td">
				<div id="lobby" className="lobby">
					{this.props.lobby.map((player, i) => {
						return <div key={i} className="card wow-card-container text-center mb-3">
							<div className={`card-body wow-card rounded ${player.wowClass}-bg`}>
								<h5 className="card-text">{player.name}</h5>
							</div>
						</div>
					})}
				</div>
			</td>
		)
	}
}

class Graveyard extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		return (
			<div>The Graveyard</div>
		)
	}
}