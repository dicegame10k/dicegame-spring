'use strict';

import {Lobby} from './lobby.js';
import {Graveyard} from './graveyard.js';
import {Leaderboard} from './leaderboard.js';
import {Chat} from './chat.js';

const React = require('react');
const ReactDOM = require('react-dom');

const SockJS = require('sockjs-client');
require('stompjs');

class DiceGame extends React.Component {

	constructor(props) {
		super(props);
		this.state = {leaderboardEntries: []};
		this.updateGameState = this.updateGameState.bind(this);
	}

	componentDidMount() {
		const sjs = SockJS('/10k'); // url endpoint that initiates the socket
        this.socket = Stomp.over(sjs); // the stompClient web socket
        this.socket.connect({}, () => {
        	// this.socket.debug = function(str) {}; uncomment to turn off console debugging messages
        	this.gameStateRegistration = this.socket.subscribe('/topic/gameState', this.updateGameState);
        	this.socket.send('/app/lightUp', {}, "the lightUp Body");
        }, (e) => {
			console.error("Failed to connect to server", e);
		});
	}

	updateGameState(message) {
		debugger;
		//<Leaderboard leaderboardEntries={this.state.leaderboardEntries}/>
	}

	render() {
		return (
			<div>
				<Lobby/>
				<Graveyard/>
				<Chat/>
			</div>
		)
	}
}

ReactDOM.render(
	<DiceGame/>,
	document.getElementById('react')
)