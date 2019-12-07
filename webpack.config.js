var path = require('path');

module.exports = {
	entry: {
		app: ['./src/main/js/app/app.js'],
		login: ['./src/main/js/login/login.js'],
	},
	devtool: 'sourcemaps',
	cache: true,
	mode: 'development',
	output: {
		path: __dirname,
		filename: './src/main/resources/static/built/[name].js',
	},
	resolve: {
		alias: {
			'stompjs': __dirname + '/node_modules' + '/stompjs/lib/stomp.js',
		}
	},
	module: {
		rules: [
			{
				test: path.join(__dirname, '.'),
				exclude: /(node_modules)/,
				use: [{
					loader: 'babel-loader',
					options: {
						presets: ["@babel/preset-env", "@babel/preset-react"]
					}
				}]
			}
		]
	}
};