const React = require('react');

export class Chat extends React.Component {

	constructor(props) {
		super(props);
		this.sendChat = this.sendChat.bind(this);
		this.toggleAutoscroll = this.toggleAutoscroll.bind(this);
		this.turnOnAutoscroll = this.turnOnAutoscroll.bind(this);
		this.turnOffAutoscroll = this.turnOffAutoscroll.bind(this);
		this.isMobileDevice = /Mobi|Android/i.test(navigator.userAgent);
		this.turnOnAutoscroll();
	}

	componentWillUnmount() {
		this.turnOffAutoscroll();
	}

	toggleAutoscroll() {
		var isChecked = document.getElementById('autoscrollCheckbox').checked;
		if (isChecked)
			this.turnOnAutoscroll();
		else
			this.turnOffAutoscroll();
	}

	turnOnAutoscroll() {
		this.chatWindowScrollIntvlId = setInterval(() => {
			var chatBoxElem = document.getElementById('allChatMessages');
			chatBoxElem.scrollTop = chatBoxElem.scrollHeight;
		}, 100);
	}

	turnOffAutoscroll() {
		clearInterval(this.chatWindowScrollIntvlId);
	}

	sendChat(event) {
		event.preventDefault();
		let data = new FormData(event.target);
		let msg = data.get('msg');
		if (!msg)
			return;

		// TODO: handle admin commands
		this.props.socket.send('/app/chat', {}, msg);
		document.getElementById('msg').value = '';
	}

	render() {
		// TODO: this doesn't really work
		let autoScrollCheckboxMobile = !this.isMobileDevice ? '' :
			<div>
				<input type="checkbox" id="autoscrollCheckbox" name="autoscrollChat" onChange={this.toggleAutoscroll} checked/>
				<label htmlFor="autoscrollCheckbox">Autoscroll Chat</label>
			</div>;

		return (
			<div id="chat" className="chat">
				{autoScrollCheckboxMobile}
				<div id="allChatMessages" className="all-chat-messages border border-dark rounded"
					onMouseEnter={this.turnOffAutoscroll} onMouseLeave={this.turnOnAutoscroll}>
					{this.props.chatMsgs.map((chat, i) => {
						return <div className="message" key={i}>
							<span className={`${chat.player.wowClass}`}>{chat.player.name}: </span>
							<span>{chat.msg}</span>
						</div>;
					})}
				</div>
				<form onSubmit={this.sendChat}>
					<div className="form-inline dg-chat-controls">
						<input id="msg" name="msg" className="form-control dg-chat-input" autoComplete="off" />
						<button type="submit"
							className={`form-control btn dg-chat-btn ${this.props.player.wowClass}-bg`}>Send</button>
					</div>
				</form>
			</div>
		)
	}

}
