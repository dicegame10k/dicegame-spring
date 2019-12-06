import {verifyFormData} from './loginutil.js';
import {normalizeUsername} from './loginutil.js';
import {wowClasses} from './loginutil.js';

const React = require('react');

export class SignUp extends React.Component {

	constructor(props) {
		super(props);
		this.wowClass = null;
		this.signUp = this.signUp.bind(this);
		this.selectClass = this.selectClass.bind(this);
		this.props.setInfoMsg("Password not sent securely. I'm too poor to afford an HTTPS certificate");
	}

	signUp(event) {
		event.preventDefault();
		const data = new FormData(event.target);
		const formErrorMsg = verifyFormData(data);
		if (formErrorMsg) {
			this.props.setErrorMsg(formErrorMsg);
			return;
		}

		if (!this.wowClass) {
			this.props.setErrorMsg('Choose a class');
			return;
		}

		data.set('wowclass', this.wowClass);

		const username = normalizeUsername(data.get("username"));
		fetch('/signUp', {
			method: 'POST',
			body: data,
		}).then((response) => response.text())
		.then((responseText) => {
			if (responseText == 'player_already_exists') {
				this.props.setErrorMsg("Player '" + username + "' already exists");
				return;
			}

			document.getElementById('signUp').style.display = 'none';
			this.props.setInfoMsg("Successfully created player '" + username + "'");
		}).catch((error) => {
			console.error(error);
			this.props.setErrorMsg('Error processing sign up attempt. Contact failbeats dev');
		});
	}

	selectClass(event) {
		let prevSelectedClass = document.getElementsByClassName('wow-class-icon-selected')[0];
		if (prevSelectedClass)
			prevSelectedClass.classList.remove('wow-class-icon-selected');

		this.wowClass = event.target.id;
		event.target.classList.add("wow-class-icon-selected");
	}

	render() {
		return (
			<div>
				<form onSubmit={this.signUp}>
					<p>
						<label htmlFor="username" className="sr-only">Enter a username</label>
						<input type="text" name="username" id="username" placeholder="Enter a username" className="form-control" autoComplete="off" onFocus={this.props.clearAlerts}></input>
					</p>
					<p>
						<label htmlFor="password" className="sr-only">Enter a password</label>
						<input type="password" name="password" id="password" placeholder="Enter a password" className="form-control" autoComplete="new-password" onFocus={this.props.clearAlerts}></input>
					</p>
					<p>
						<label htmlFor="password" className="sr-only">Verify your password</label>
						<input type="password" name="passwordVerify" id="passwordVerify" placeholder="Verify your password" className="form-control" onFocus={this.props.clearAlerts}></input>
					</p>
					<div className="form-group">
						<div>Choose a class:</div>
						<div className="dg-wowclass-container">
							{wowClasses.map((wowClass) => {
								return <span onClick={this.selectClass} className={`wow-class-icon rounded ${wowClass}-bg`} id={wowClass} key={wowClass}/>;
							})}
						</div>
					</div>
					<button type="submit" id="signUp" className="btn btn-info dg-btn-space">Sign up</button>
					<button onClick={this.props.back} className="btn btn-light">Back</button>
				</form>
			</div>
		)
	}
}
