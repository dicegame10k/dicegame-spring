'use strict';

import {DiceGameNav} from './nav.js'
import {DiceGame} from './dicegame.js';
import {Recount} from './recount.js';

import {normalizeUsername} from './dicegameutil.js';
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
		this.logout = this.logout.bind(this);

		this.lightUp = this.lightUp.bind(this);
		this.stuck = this.stuck.bind(this);
		this.roll = this.roll.bind(this);
		this.forceRoll = this.forceRoll.bind(this);
		this.slashKick = this.slashKick.bind(this);
		this.kick = this.kick.bind(this);
		this.chatCommandMap = {
			'/stuck': this.stuck,
			'/roll': this.roll,
			'/force_roll': this.forceRoll,
			'/kick': this.slashKick,
		};
	}

	componentDidMount() {
		const sjs = SockJS('/10k'); // url endpoint that initiates the socket
        this.socket = Stomp.over(sjs); // the stompClient web socket
        this.socket.connect({}, () => {
        	// this.socket.debug = function(str) {}; TODO: uncomment to turn off console debugging messages
        	// topic is for broadcasted messages, user/queue is for individual messages
        	this.lobbyTopicRegistration = this.socket.subscribe('/topic/lobby', this.updateLobby);
        	this.lobbyQueueRegistration = this.socket.subscribe('/user/queue/lobby', this.updateLobby);
        	this.gameStateTopicRegistration = this.socket.subscribe('/topic/gameState', this.updateGameState);
        	this.gameStateQueueRegistration = this.socket.subscribe('/user/queue/gameState', this.updateGameState);
        	this.chatTopicRegistration = this.socket.subscribe('/topic/chat', this.receiveChat);
        	this.chatQueueRegistration = this.socket.subscribe('/user/queue/chat', this.receiveChat);
        	this.logoutRegistration = this.socket.subscribe('/user/queue/logout', this.logout);
        	// setup the heartbeat
        	setInterval(this.heartbeat.bind(this), 10 * 1000);

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

	logout() {
		console.log("Server told us to logout");
		window.location.href = '/logout';
	}

	lightUp() {
		this.socket.send('/app/lightUp');
	}

	roll() {
		this.socket.send('/app/roll');
		// make the roll button disappear immediately after a roll
		let gameState = this.state.gameState;
		gameState.currentlyRollingPlayer = 'some_bottom_deeps_scrub_pug';
		this.setState({ gameState: gameState });
	}

	forceRoll() {
		this.socket.send('/app/forceRoll');
	}

	stuck() {
		this.socket.send('/app/stuck');
	}

	slashKick(chatMsg) {
		let playerToKick = chatMsg.split(' ')[1];
		if (!playerToKick)
			return;

		this.kick(playerToKick);
	}

	kick(username) {
		username = normalizeUsername(username);
		this.socket.send('/app/kick', {}, username);
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
			normalizeWowClasses(gameState.graveyard);
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
			lobby={this.state.lobby} gameState={this.state.gameState} lightUp={this.lightUp} roll={this.roll}
			forceRoll={this.forceRoll} kick={this.kick} chatMsgs={this.state.chatMsgs}
			chatCommandMap={this.chatCommandMap}/>;
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
