import {verifyFormData} from './loginutil.js';
import {normalizeUsername} from './loginutil.js';

const React = require('react');

export class ForgotPassword extends React.Component {

	constructor(props) {
		super(props);
		this.resetPassword = this.resetPassword.bind(this);
		// TODO: link to icyveins?
		this.props.setInfoMsg("Wow really? Did you forget your rotation too?");
	}

	resetPassword(event) {
		event.preventDefault();
		const data = new FormData(event.target);
		const formErrorMsg = verifyFormData(data);
		if (formErrorMsg) {
			this.props.setErrorMsg(formErrorMsg);
			return;
		}

		const username = normalizeUsername(data.get("username"));
		fetch('/resetPassword', {
			method: 'POST',
			body: data,
		}).then((response) => response.text())
		.then((responseText) => {
			if (responseText == 'player_does_not_exist') {
				this.props.setErrorMsg("Player '" + username + "' does not exist");
				return;
			}

			document.getElementById('reset').style.display = 'none';
			this.props.setInfoMsg("Successfully reset " + username + "'s password");
		}).catch((error) => {
			console.error(error);
			this.props.setErrorMsg('Error processing password reset attempt. Contact failbeats dev');
		});
	}

	render() {
		return (
			<div>
				<form onSubmit={this.resetPassword}>
					<p>
						<label htmlFor="username" className="sr-only">Enter your username</label>
						<input type="text" name="username" id="username" placeholder="Enter your username" className="form-control" autoComplete="off" onFocus={this.props.clearAlerts}></input>
					</p>
					<p>
						<label htmlFor="password" className="sr-only">Enter a new password</label>
						<input type="password" name="password" id="password" placeholder="Enter a new password" className="form-control" onFocus={this.props.clearAlerts}></input>
					</p>
					<p>
						<label htmlFor="password" className="sr-only">Verify your new password</label>
						<input type="password" name="passwordVerify" id="passwordVerify" placeholder="Verify your new password" className="form-control" onFocus={this.props.clearAlerts}></input>
					</p>
					<button type="submit" id="reset" className="btn btn-info dg-btn-space">Reset</button>
					<button onClick={this.props.back} className="btn btn-light">Back</button>
				</form>
			</div>
		)
	}
}
