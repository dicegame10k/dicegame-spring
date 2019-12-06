'use strict';

import {DiceGameNav} from './nav.js'
import {DiceGame} from './dicegame.js';
import {Recount} from './recount.js';

import {wowClassFromEnum} from './dicegameutil.js';
import {normalizeWowClasses} from './dicegameutil.js';
import {dicegameAscii} from './dicegameutil.js';

const React = require('react');
const ReactDOM = require('react-dom');

const SockJS = require('sockjs-client');
require('stompjs');

class DiceGameContainer extends React.Component {

	constructor(props) {
		super(props);
		this.state = {
			player: {},
			page: 'dicegame',
			chatMsgs: [],
			lobby: [],
			gameState: {
				gameInProgress: false,
				dgPlayers: [],
				graveyard: [],
				currentlyRollingPlayer: 'some_bottom_deeps_scrub_pug',
				currentRoll: 100,
			},
		};

		this.heartbeat = this.heartbeat.bind(this);
		this.receiveChat = this.receiveChat.bind(this);
		this.updateLobby = this.updateLobby.bind(this);
		this.updateGameState = this.updateGameState.bind(this);
		this.switchPage = this.switchPage.bind(this);
		this.lightUp = this.lightUp.bind(this);
		this.roll = this.roll.bind(this);
	}

	componentDidMount() {
		const sjs = SockJS('/10k'); // url endpoint that initiates the socket
        this.socket = Stomp.over(sjs); // the stompClient web socket
        this.socket.connect({}, () => {
        	// this.socket.debug = function(str) {}; uncomment to turn off console debugging messages
        	this.lobbyRegistration = this.socket.subscribe('/topic/lobby', this.updateLobby);
        	this.gameStateRegistration = this.socket.subscribe('/topic/gameState', this.updateGameState);
        	this.chatRegistration = this.socket.subscribe('/topic/chat', this.receiveChat);
        	// setup the heartbeat
        	setInterval(this.heartbeat.bind(this), 30 * 1000);

        	// registrations done, now try to enter the lobby
        	fetch('/enterLobby')
			.then((response) => response.text())
			.then((playerInfo) => {
				try {
					playerInfo = JSON.parse(playerInfo);
					playerInfo.wowClass = wowClassFromEnum(playerInfo.wowClass);
				} catch (e) {
					alert("Session expired. Redirecting to login");
					console.error("Error with /loadPlayer endpoint", e);
					window.location.href = '/logout';
				}

				// TODO: the spaces are getting stripped out
//				let chatMsgs = this.state.chatMsgs;
//				for (let i = 0; i < dicegameAscii.length; i += 1) {
//					let chatMsg = {};
//					 TODO: do i need to clone playerInfo?
//					chatMsg.player = playerInfo;
//					chatMsg.msg = dicegameAscii[i];
//					chatMsgs.push(chatMsg);
//				}

				this.setState({ player: playerInfo });
			});
        }, (e) => {
			console.error("Failed to setup connections to server", e);
		});
	}

	// checks if the session has expired and redirects the user back to the login page
	heartbeat() {
		fetch('/heartbeat')
		.then((response) => {
			if (response.redirected) {
				console.log("DiceGame heartbeat was redirected to " + response.url);
				window.location.href = response.url;
			}
		}, (e) => {});
	}

	lightUp() {
		this.socket.send('/app/lightUp');
	}

	roll() {
		this.socket.send('/app/roll');
	}

	// Receives response from /app/chat
	receiveChat(response) {
		let chatMsg;
		try {
			chatMsg = JSON.parse(response.body);
			chatMsg.player.wowClass = wowClassFromEnum(chatMsg.player.wowClass);
		} catch (e) {
			console.error("Failed to parse chat message", e);
			return;
		}

		let msgs = this.state.chatMsgs;
		msgs.push(chatMsg);
		this.setState({ chatMsgs: msgs });
	}

	updateLobby(lobbyResponse) {
		try {
			let lobby = normalizeWowClasses(JSON.parse(lobbyResponse.body));
			this.setState({ lobby: lobby });
		} catch (e) {
			alert("Failed to enter the lobby. Contact failbeats dev");
			console.error("Failed to enter the lobby", e);
		}
	}

	updateGameState(gameStateResponse) {
		try {
			let gameState = JSON.parse(gameStateResponse.body);
			normalizeWowClasses(gameState.dgPlayers);
			this.setState({ gameState: gameState });
		} catch (e) {
			alert("Failed to parse game state. Contact failbeats dev");
            console.error("Failed to parse game state", e);
		}
	}

	switchPage(page) {
		this.setState({ page: page });
	}

	render() {
		let page = <DiceGame player={this.state.player} socket={this.socket}
			lobby={this.state.lobby} gameState={this.state.gameState}
			lightUp={this.lightUp} roll={this.roll} chatMsgs={this.state.chatMsgs}/>;
		if (this.state.page === 'recount')
			page = <Recount player={this.state.player}/>;

		return (
			<div>
				<DiceGameNav player={this.state.player} socket={this.socket} switchPage={this.switchPage}/>
				{page}
			</div>
		)
	}
}

ReactDOM.render(
	<DiceGameContainer/>,
	document.getElementById('react')
)
