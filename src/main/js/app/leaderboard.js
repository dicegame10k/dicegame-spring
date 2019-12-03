const React = require('react');

export class Leaderboard extends React.Component {

	render() {
		const entries = this.props.leaderboardEntries.map((entry) => {
			return <LeaderboardEntry entry={entry}/>
		});
		return (
			 <table>
				 <thead>
					 <tr>
						 <th>Name</th>

					 </tr>
				 </thead>
				 <tbody>
					 {entries}
				 </tbody>
			 </table>
		)
	}
}

class LeaderboardEntry extends React.Component {

	render() {
		return (
			<tr>
				<td>{this.props.entry.name}</td>
			</tr>
		)
	}

}