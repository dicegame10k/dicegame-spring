import OverlayTrigger from 'react-bootstrap/OverlayTrigger';
import Popover from 'react-bootstrap/Popover';
import ReactModal from 'react-modal';

import {wowClasses} from '../util.js';
import {particlesPrefs} from '../particles/particlesConfig.js';
import {setParticles} from '../util.js';

const React = require('react');

ReactModal.setAppElement('#react');

export class DiceGameNav extends React.Component {

	constructor(props) {
		super(props);
		this.state = { showSettingsModal: false };
		this.toggleSettingsModal = this.toggleSettingsModal.bind(this);
		this.addNavItemHoverClass = this.addNavItemHoverClass.bind(this);
		this.removeNavItemHoverClass = this.removeNavItemHoverClass.bind(this);
		this.showDiceGame = this.showDiceGame.bind(this);
		this.showRecount = this.showRecount.bind(this);
		this.showHistory = this.showHistory.bind(this);
	}

	toggleSettingsModal(shouldShow) {
		this.setState({ showSettingsModal: shouldShow });
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

	showHistory() {
		this.props.switchPage('history');
	}

	render() {
		let settingsModal = '';
		if (this.state.showSettingsModal)
			settingsModal = <SettingsModal player={this.props.player} changeWowClass={this.props.changeWowClass}
				toggleSettingsModal={this.toggleSettingsModal} showModal={this.state.showSettingsModal}
				prefs={this.props.prefs} changePrefs={this.props.changePrefs}/>;

		return (
			<nav className="dicegame-nav dg-opaque">
				<div className="dicegame-nav-item" onMouseEnter={this.addNavItemHoverClass}
					onMouseLeave={this.removeNavItemHoverClass} onClick={this.showDiceGame}>DiceGame</div>
				<div className="dicegame-nav-item" onMouseEnter={this.addNavItemHoverClass}
					onMouseLeave={this.removeNavItemHoverClass} onClick={this.showRecount}
					title="Deeps meter">Recount</div>
				<div className="dicegame-nav-item" onMouseEnter={this.addNavItemHoverClass}
					onMouseLeave={this.removeNavItemHoverClass} onClick={this.showHistory}>Logs</div>
				<PlayerProfile player={this.props.player} toggleSettingsModal={this.toggleSettingsModal}/>
				{settingsModal}
			</nav>
		)
	}

}

class PlayerProfile extends React.Component {

	constructor(props) {
		super(props);
		this.logout = this.logout.bind(this);
		this.openSettingsModal = this.openSettingsModal.bind(this);
	}

	logout() {
		window.location.href = '/logout';
	}

	openSettingsModal() {
		this.props.toggleSettingsModal(true);
		// close the profile popover
		let profileElem = document.getElementById('playerProfile');
		if (profileElem)
			profileElem.click();
	}

	render() {
		let popover =
			<Popover>
				<Popover.Content className="table-hover text-white dg-settings-popover">
					<table>
						<tbody>
							<tr>
								<td onClick={this.openSettingsModal} className="dicegame-nav-item">Settings</td>
							</tr>
							<tr>
								<td onClick={this.logout} className="dicegame-nav-item death-knight">Logout</td>
							</tr>
						</tbody>
					</table>
				</Popover.Content>
			</Popover>;

		return (
			<OverlayTrigger trigger="click" placement="bottom" overlay={popover}>
				<div className="dicegame-nav-item dicegame-nav-username" id="playerProfile">
					<div className={`${this.props.player.wowClass}`}
							data-tip data-for="playerProfile">
						{this.props.player.name}
						<span className="dg-down-arrow"/>
					</div>
				</div>
			</OverlayTrigger>
		)
	}

}

class SettingsModal extends React.Component {

	constructor(props) {
		super(props);
		this.newWowClass = null;
		this.change = this.change.bind(this);
		this.closeModal = this.closeModal.bind(this);
		this.selectClass = this.selectClass.bind(this);
		this.changeParticles = this.changeParticles.bind(this);
	}

	change(event) {
		event.preventDefault();
		const data = new FormData(event.target);
		data.set('newWowClass', this.newWowClass);
		// only update server if class actually changed
		if (this.newWowClass && (this.props.player.wowClass !== this.newWowClass))
			this.props.changeWowClass(data);

		this.closeModal();
	}

	changeParticles(event) {
		// update particleJS immediately
		let particles = event.target.value;
		setParticles(particles);

		// update pref on the server
		let prefs = this.props.prefs;
		prefs['particles'] = particles;
		this.props.changePrefs(prefs);
	}

	closeModal() {
		this.props.toggleSettingsModal(false);
	}

	selectClass(event) {
		let prevSelectedClass = document.getElementsByClassName('wow-class-icon-selected')[0];
		if (prevSelectedClass)
			prevSelectedClass.classList.remove('wow-class-icon-selected');

		this.newWowClass = event.target.id;
		event.target.classList.add("wow-class-icon-selected");
	}

	render() {
		let currClass = this.props.player.wowClass;
		return (
			<ReactModal isOpen={this.props.showModal} className="dg-noop-class-for-some-reason-it-is-necessary">
				<div className="dg-settings-modal table-dark">
					<div className="dg-particles-container">
						<span className="dg-particles-label">Background: </span>
						<select onChange={this.changeParticles} defaultValue={this.props.prefs['particles']}>
							{particlesPrefs.map((particleOpt) => {
								return <option key={particleOpt} value={particleOpt}>{particleOpt}</option>
							})}
						</select>
					</div>
					<form onSubmit={this.change}>
						<input type="hidden"/>
						<div className="form-group">
							<div>Choose a new class:</div>
							<div className="dg-wowclass-container">
								{wowClasses.map((wowClass) => {
									let selectedClass = '';
									if (wowClass === currClass)
										selectedClass = "wow-class-icon-selected";

									return <span onClick={this.selectClass} className={`${selectedClass} wow-class-icon rounded ${wowClass}-bg`} id={wowClass} key={wowClass}/>;
								})}
							</div>
						</div>
						<button type="submit" className="btn btn-info dg-btn-space">Save</button>
						<button className="btn btn-light" onClick={this.closeModal}>Cancel</button>
					</form>
				</div>
			</ReactModal>
		)
	}

}
