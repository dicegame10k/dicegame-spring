import ReactTooltip from 'react-tooltip';

import {wowClassFromEnum} from '../util.js';

const React = require('react');

export class Recount extends React.Component {

	constructor(props) {
		super(props);
		this.state = { recountList: [] };
		this.playerToDkpWidth = [];
	}

	componentDidMount() {
		fetch('/recount')
		.then((response) => response.text())
		.then((recountList) => {
			try {
				recountList = JSON.parse(recountList);
				for (let i = 0; i < recountList.length; i += 1) {
					let recount = recountList[i];
					recount.player.wowClass = wowClassFromEnum(recount.player.wowClass);
				}
			} catch (e) {
				recountList = [];
				console.error("Failed to load recount", e);
			}

			this.setState({ recountList: recountList });
		});
	}

	componentDidUpdate() {
		window.requestAnimationFrame(() => {
			for (let i = 0; i < this.playerToDkpWidth.length; i += 1) {
				let p = this.playerToDkpWidth[i];
				let progressBarElem = document.getElementById(p.player);
				if (progressBarElem)
					progressBarElem.style.width = p.width;
			}
		});
	}

	render() {
		this.playerToDkpWidth = [];
		let recountList = this.state.recountList;
		let maxDkp = 1;
		if (recountList[0] && recountList[0].player && recountList[0].player.dkp > 0)
			maxDkp = recountList[0].player.dkp;

		return (
			<div>
				<div className="dg-recount table-sm dg-opaque">
					<div className="dg-rc-header">
						<span className="dg-rc-cell-left">Username</span>
						<span className="dg-rc-cell-right dg-dkp-header" data-tip data-for="dkpHeader">
							DKP
						</span>
						<ReactTooltip id="dkpHeader" effect="solid">
							<span>DiceGame Kill Points: You get 1 DKP for each person you beat in a game</span>
						</ReactTooltip>
					</div>

					<div>
						{recountList.map((recount, i) => {
							let player = recount.player;
							let width = ((player.dkp / maxDkp) * 100) + '%';
							this.playerToDkpWidth.push({ player: player.name + '-rc', width: width });

							return <div key={i} className="dg-rc-row-container" data-tip data-for={`${player.name}-rc-tt`}>
								<div className={`${player.wowClass}-bg progress-bar dg-rc-progress-bar`} id={`${player.name}-rc`}
									/>
								<ReactTooltip id={`${player.name}-rc-tt`} place="right">
									<div>Games played: {recount.numGamesPlayed}</div>
									<div>Games won: {recount.numGamesWon}</div>
									<div>Win %: {recount.winPercentage}</div>
									<div>Avg dkp/game: {recount.avgDkpPerGame}</div>
									<div>Avg # players/game: {recount.avgNumPlayersPerGame}</div>
								</ReactTooltip>
								<div className="dg-rc-row">
									<span className="dg-rc-cell-left">{player.name}</span>
									<span className="dg-rc-cell-right">{player.dkp}</span>
								</div>
							</div>;
						})}
					</div>
				</div>
			</div>
		)
	}
}
