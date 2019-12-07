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