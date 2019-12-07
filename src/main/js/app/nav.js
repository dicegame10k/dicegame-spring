import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import ReactModal from 'react-modal';

const React = require('react');

ReactModal.setAppElement('#react');

export class DiceGameNav extends React.Component {

	constructor(props) {
		super(props);
		this.state = { showChangeClassModal: false };
		this.toggleWowClassModal = this.toggleWowClassModal.bind(this);
		this.addNavItemHoverClass = this.addNavItemHoverClass.bind(this);
		this.removeNavItemHoverClass = this.removeNavItemHoverClass.bind(this);
		this.showDiceGame = this.showDiceGame.bind(this);
		this.showRecount = this.showRecount.bind(this);
	}

	toggleWowClassModal(shouldShow) {
		this.setState({ showChangeClassModal: shouldShow });
	}

	addNavItemHoverClass(event) {
		event.target.classList.add(this.props.player.wowClass);
	}

	removeNavItemHoverClass(event) {
		event.target.classList.remove(this.props.player.wowClass);
	}

	showDiceGame() {
		this.props.switchPage('dicegame');
	}

	showRecount() {
		this.props.switchPage('recount');
	}

	render() {
		let changeClassModal = '';
		if (this.state.showChangeClassModal)
			changeClassModal = <ChangeClassModal player={this.props.player}
				toggleWowClassModal={this.toggleWowClassModal} showModal={this.showChangeClassModal}/>;

		return (
			<nav className="dicegame-nav">
				<div className="dicegame-nav-item" onMouseEnter={this.addNavItemHoverClass}
					onMouseLeave={this.removeNavItemHoverClass} onClick={this.showDiceGame}>DiceGame</div>
				<div className="dicegame-nav-item" onMouseEnter={this.addNavItemHoverClass}
					onMouseLeave={this.removeNavItemHoverClass} onClick={this.showRecount}
					data-toggle="tooltip" title="Deeps meter">Recount</div>
				<PlayerProfile player={this.props.player} toggleWowClassModal={this.toggleWowClassModal}/>
				{changeClassModal}
			</nav>
		)
	}

}

class PlayerProfile extends React.Component {

	constructor(props) {
		super(props);
		this.logout = this.logout.bind(this);
	}

	logout() {
		window.location.href = '/logout';
	}

	render() {
		let logoutStyle = {color: "red"};
		let popover =
			<Popover>
				<Popover.Content className="table-dark table-hover text-white">
					<table>
						<tbody>
							<tr>
								<td onClick={() => {this.props.toggleWowClassModal(true)}} className="dicegame-nav-item">Change class</td>
							</tr>
							<tr>
								<td onClick={this.logout} className="dicegame-nav-item" style={logoutStyle}>Logout</td>
							</tr>
						</tbody>
					</table>
				</Popover.Content>
			</Popover>;

		return (
			<OverlayTrigger trigger="click" placement="bottom" overlay={popover}>
				<div className="dicegame-nav-item dicegame-nav-username">
					<div className={`${this.props.player.wowClass}`}
							data-tip data-for="playerProfile">
						{this.props.player.name + "  "}
						<span className="dg-dropdown-caret"/>
					</div>
				</div>
			</OverlayTrigger>
		)
	}

}

class ChangeClassModal extends React.Component {

	constructor(props) {
		super(props);
	}

	render() {
		return (
			<ReactModal isOpen={this.props.showModal}>
				<div>In the modal</div>
			</ReactModal>
		)
	}

}
