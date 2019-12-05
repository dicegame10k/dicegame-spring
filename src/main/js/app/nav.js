const React = require('react');

export class DiceGameNav extends React.Component {

	constructor(props) {
		super(props);
		this.addNavItemHoverClass = this.addNavItemHoverClass.bind(this);
		this.removeNavItemHoverClass = this.removeNavItemHoverClass.bind(this);
		this.showDiceGame = this.showDiceGame.bind(this);
		this.showLeaderboard = this.showLeaderboard.bind(this);
		this.toggleProfileEdit = this.toggleProfileEdit.bind(this);
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

	showLeaderboard() {
		this.props.switchPage('leaderboard');
	}

	toggleProfileEdit() {

	}

	render() {
		return (
			<nav className="dicegame-nav">
				<div className="dicegame-nav-item" onMouseEnter={this.addNavItemHoverClass}
					onMouseLeave={this.removeNavItemHoverClass} onClick={this.showDiceGame}>DiceGame</div>
				<div className="dicegame-nav-item" onMouseEnter={this.addNavItemHoverClass}
					onMouseLeave={this.removeNavItemHoverClass} onClick={this.showLeaderboard}
					data-toggle="tooltip" title="Deeps meter">Recount</div>
				<div className={`dicegame-nav-item dicegame-nav-username ${this.props.player.wowClass}`}
					onClick={this.toggleProfileEdit}>{this.props.player.name}</div>
			</nav>
		)
	}

}
