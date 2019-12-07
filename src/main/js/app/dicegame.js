import {Chat} from './chat.js';

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
						<Chat player={this.props.player} socket={this.props.socket} chatMsgs={this.props.chatMsgs}/>
						<Footer/>
					</tr>
				</table>
			</div>
		)
	}

}

class Game extends React.Component {

	constructor(props) {
		super(props);
		this.positionEverything = this.positionEverything.bind(this);
		this.positionFire = this.positionFire.bind(this);
		this.calculateOffsetStyle = this.calculateOffsetStyle.bind(this);
	}

	positionEverything() {
		if (!this.props.gameState.gameInProgress)
			return;

		window.requestAnimationFrame(() => {
			let playerCards = document.getElementsByClassName('player-in-game');
			if (playerCards.length === 0)
				return;

			// calculate offset of the roll number, button, and fire
			let currRollElem = document.getElementById('currRoll');
			if (currRollElem)
				currRollElem.style = this.calculateOffsetStyle(currRollElem);
			let rollButtonElem = document.getElementById('rollButton');
			if (rollButtonElem)
				rollButtonElem.style = this.calculateOffsetStyle(rollButtonElem, currRollElem.offsetHeight / 2);

			this.positionFire();

			// calculate offset of the cards
			let offsetStyle = this.calculateOffsetStyle(playerCards[0]);
			for (let i = 0; i < playerCards.length; i++) {
				let offsetAngle = 360 / playerCards.length;
				let rotateAngle = offsetAngle * i;
				playerCards[i].style = offsetStyle +
					"transform : rotate(" + rotateAngle + "deg) translate(0, -200px) rotate(-" + rotateAngle + "deg)";
			}
		});
	}

	positionFire() {
		let fireGifElem = document.getElementById('fire');
		if (fireGifElem)
			fireGifElem.style = this.calculateOffsetStyle(fireGifElem);
	}

	calculateOffsetStyle(element, additionalOffsetTop) {
		let offsetLeft = document.body.offsetWidth / 2 - element.offsetWidth / 2;
		let offsetTop = document.body.offsetHeight / 2 - element.offsetHeight / 2;
		if (typeof additionalOffsetTop == "number")
			offsetTop += additionalOffsetTop;

		let offsetStyle = "left: " + offsetLeft + "px; top: " + offsetTop + "px; ";
		return offsetStyle;
	}

	/**
	 * Position the cards the around the fire
	 */
	 componentDidMount() {
		this.positionEverything();
	 }

	/**
	 * Position the cards the around the fire
	 */
	componentDidUpdate() {
		this.positionEverything();
	}

	render() {
		let myself = this.props.player;
		let gameInProgress = this.props.gameState.gameInProgress;
		let dgPlayers = this.props.gameState.dgPlayers;
		let currentlyRollingPlayer = this.props.gameState.currentlyRollingPlayer;
		let currentRoll = this.props.gameState.currentRoll;

		let lightUpBtn = '';
		if (!gameInProgress)
			lightUpBtn = <button onClick={this.props.lightUp} className="btn btn-danger light-up-btn">Light Up</button>;

		let rollBtn = '';
		if (gameInProgress && currentlyRollingPlayer && currentlyRollingPlayer.name === myself.name)
			rollBtn = <button onClick={this.props.roll} id="rollButton" className={`btn roll-btn ${myself.wowClass}-bg`}>Roll</button>;

		// this needs an onLoad because the image needs to be fetched from the server and then repositioned when it is loaded
		let fireGif = '';
		if (gameInProgress)
			fireGif = <img id="fire" className="fire" src="/images/fire.gif" onLoad={this.positionFire}/>;

		let dgPlayerCards = '';
		if (gameInProgress) {
			dgPlayerCards = dgPlayers.map((player, i) => {
				return <GameCard key={i} player={player} currentlyRollingPlayer={currentlyRollingPlayer}/>;
			});
		}

		let currRollElem = '';
		if (gameInProgress)
			currRollElem = <div id="currRoll" className="currentRoll">{currentRoll}</div>;

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
						return <Card key={i} player={player}/>;
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
			<td className="dg-graveyard-td">
				<div id="graveyard" className="graveyard">
					{this.props.graveyard.map((player, i) => {
						return <Card key={i} player={player}/>;
					})}
				</div>
			</td>
		)
	}
}

class GameCard extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		let player = this.props.player;
		let currentlyRollingPlayer = this.props.currentlyRollingPlayer;

		// highlight the card if it's their turn to roll
		let playerRollingClassName = '';
		if (currentlyRollingPlayer && currentlyRollingPlayer.name === player.name)
			playerRollingClassName = "player-rolling";

		return (
			<div className={`wow-card-container text-center player-in-game rounded ${player.wowClass}-bg ${playerRollingClassName}`}>
				<span className="dg-player-in-game-name">{player.name}</span>
			</div>
		)
	}

}

// lobby and graveyard cards
class Card extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		let player = this.props.player;
		return (
			<div className="card wow-card-container text-center mb-3">
				<div className={`card-body wow-card rounded ${player.wowClass}-bg`}>
					<h5 className="card-text">{player.name}</h5>
				</div>
			</div>
		)
	}
}

class Footer extends React.Component {

	render() {
		return(
			<footer className="footer text-muted">
				© 2020 <a target="_blank" href="https://www.wowprogress.com/guild/us/emerald-dream/Chill+Beats">&lt;Chill Beats&gt;</a>
			</footer>
		)
	}
}
