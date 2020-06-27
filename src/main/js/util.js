import {particlesConfig} from './particles/particlesConfig.js';

require('particles.js');

export const wowClasses = ["death-knight", "demon-hunter", "druid", "hunter", "mage",
	"monk", "paladin", "priest", "rogue", "shaman", "warlock", "warrior"];

export function normalizeUsername(username) {
	return username.charAt(0).toUpperCase() + username.slice(1).toLowerCase();
}

export function wowClassFromEnum(enumStr) {
	return enumStr.toLowerCase().replace('_', '-');
}

export function normalizeWowClasses(listOfPlayers) {
	for (let i = 0; i < listOfPlayers.length; i += 1) {
		let player = listOfPlayers[i];
		player.wowClass = wowClassFromEnum(player.wowClass);
	}

	return listOfPlayers;
}

export const dicegameAscii = [
	"    ____  _           ______                   ",
	"   / __ \\(_)_______  / ____/___ _____ ___  ___ ",
	"  / / / / / ___/ _ \\/ / __/ __ `/ __ `__ \\/ _ \\",
	" / /_/ / / /__/  __/ /_/ / /_/ / / / / / /  __/",
	"/_____/_/\\___/\\___/\\____/\\__,_/_/ /_/ /_/\\___/"
];

export function verifyFormData(data) {
	let username = data.get('username');
	if (!username)
		return 'Enter a username';

	username = normalizeUsername(username);
	if (username.length > 8)
		return 'Username too long';

	if (username.indexOf('Nig') > -1)
		return "C'mon now Mike";

	data.set('username', username);
	let password = data.get('password');
	if (!password)
		return 'Enter a password';

	let passwordVerify = data.get('passwordVerify');
	if (!passwordVerify)
		return 'Verify your password';

	if (password !== passwordVerify)
		return 'Passwords do not match';

	return null;
}

export function setParticles(configName) {
	// destroy current config if it exists
	try {
		pJSDom[0].pJS.fn.vendors.destroypJS();
		pJSDom = [];
	} catch (e) {
		// happens before particlesJS has been loaded once
	}

	if (configName === 'off')
		return;

	// args are: id, config JSON
	particlesJS('particles', particlesConfig[configName]);
}
