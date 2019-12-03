import {ForgotPassword} from './forgotpassword.js';
import {SignUp} from './signup.js';
import {normalizeUsername} from './loginutil.js';

const React = require('react');
const ReactDOM = require('react-dom');

class LoginContainer extends React.Component {

	constructor(props) {
		super(props);
		this.state = { page: 'login' };
		this.changePage = this.changePage.bind(this);
		this.back = this.back.bind(this);
		this.setErrorMsg = this.setErrorMsg.bind(this);
		this.setInfoMsg = this.setInfoMsg.bind(this);
		this.clearAlerts = this.clearAlerts.bind(this);
	}

	back() {
		this.setState({ page: 'login', errorMsg: '', infoMsg: '' });
	}

	changePage(page) {
		this.setState({ page: page, errorMsg: '', infoMsg: '' });
	}

	setErrorMsg(msg) {
		this.setState({ errorMsg: msg, infoMsg: '' });
	}

	setInfoMsg(msg) {
		this.setState({infoMsg: msg, errorMsg: ''})
	}

	clearAlerts() {
		this.setState({infoMsg: '', errorMsg: ''});
	}

	render() {
		// TODO: add <input name="_csrf" type="hidden">
		let page;
		switch (this.state.page) {
			case 'forgotPassword':
				page = <ForgotPassword back={this.back} setErrorMsg={this.setErrorMsg} clearAlerts={this.clearAlerts}
					setInfoMsg={this.setInfoMsg}/>;
				break;
			case 'signUp':
				page = <SignUp back={this.back} setErrorMsg={this.setErrorMsg} clearAlerts={this.clearAlerts}
					setInfoMsg={this.setInfoMsg}/>;
				break;
			default:
				page = <Login changePage={this.changePage} setErrorMsg={this.setErrorMsg} clearAlerts={this.clearAlerts}
					setInfoMsg={this.setInfoMsg}/>;
				break;
		}

		const errorMsg = this.state.errorMsg;
		const infoMsg = this.state.infoMsg;
		return (
			<div className="container dg-login">
				<h1 className="welcome-msg">Welcome to DiceGame</h1>
				{errorMsg ? <div className="alert alert-danger" role="alert">{errorMsg}</div> : ''}
				{infoMsg ? <div className="alert alert-success" role="alert">{infoMsg}</div> : ''}
				{page}
			</div>
		)
	}
}

class Login extends React.Component {

	constructor(props) {
		super(props);
		this.state = { errorMsg: '' };
		this.showForgotPassword = this.showForgotPassword.bind(this);
		this.showSignUp = this.showSignUp.bind(this);
		this.login = this.login.bind(this);
	}

	showForgotPassword() {
		this.props.changePage('forgotPassword');
	}

	showSignUp() {
		this.props.changePage('signUp');
	}

	login(event) {
		event.preventDefault();
		const data = new FormData(event.target);
		let username = data.get('username');
		if (!username) {
			this.props.setErrorMsg('Enter a username');
			return;
		}

		if (!data.get('password')) {
			this.props.setErrorMsg('Enter a password');
			return;
		}

		data.set('username', normalizeUsername(username));

		fetch('/login', {
			method: 'POST',
			body: data,
		}).then((response) => {
			if (response.url.indexOf('login?error') > -1) {
				this.props.setErrorMsg('Invalid credentials');
				return;
			}

			window.location.href = response.url;
		}).catch((error) => {
			console.error(error);
			this.props.setErrorMsg('Server failed to process login attempt. Contact failbeats dev');
		});
	}

	render() {
		return (
			<form onSubmit={this.login}>
				<p>
					<label htmlFor="username" className="sr-only">Username</label>
					<input type="text" name="username" id="username" placeholder="Username" className="form-control" autoComplete="off" onFocus={this.props.clearAlerts}></input>
				</p>
				<p>
					<label htmlFor="password" className="sr-only">Password</label>
					<input type="password" name="password" id="password" placeholder="Password" className="form-control" onFocus={this.props.clearAlerts}></input>
				</p>
				<button type="submit" className="btn btn-info">Login</button>
				<button type="button" className="btn btn-link" onClick={this.showForgotPassword}>Forgot password?</button>
				<button type="button" className="btn btn-link dg-no-left-padding" onClick={this.showSignUp}>Sign up</button>
			</form>
		)
	}
}

ReactDOM.render(
	<LoginContainer/>,
	document.getElementById('react')
)