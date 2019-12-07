const React = require('react');

export class Chat extends React.Component {

	constructor(props) {
		super(props);
		this.sendChat = this.sendChat.bind(this);
		this.handleAdminCommand = this.handleAdminCommand.bind(this);
		this.clearChatInput = this.clearChatInput.bind(this);
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
			if (chatBoxElem)
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

		if (this.handleAdminCommand(msg)) {
			this.clearChatInput();
			return;
		}

		this.props.socket.send('/app/chat', {}, msg);
		this.clearChatInput();
	}

	clearChatInput() {
		document.getElementById('msg').value = '';
	}

	handleAdminCommand(msg) {
		let firstWord = msg.toLowerCase().split(' ')[0];
		let chatCmndFn = this.props.chatCommandMap[firstWord];
		if (typeof chatCmndFn === 'function') {
			try {
				chatCmndFn(msg);
			} catch (e) {
				console.error("Admin chat command did not work", msg);
				console.error(e);
			}

			return true;
		}

		return false;
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
						return <ChatMessage key={i} chat={chat}/>
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

class ChatMessage extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		let msg = this.props.chat.msg;
		let wowClass = this.props.chat.player.wowClass;
		let name = this.props.chat.player.name;
		let prfx = "";
		if (name !== 'system')
			prfx = name + ": ";

		return (
			<div className="message">
				<span className={`${wowClass}`}>{prfx}</span>
				<span>{msg}</span>
			</div>
		)
	}

}
