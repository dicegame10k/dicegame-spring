import {loginParticlesJSON} from './loginParticles.js';
import {linesParticlesJSON} from './linesParticles.js';
import {snowParticlesJSON} from './snowParticles.js';

export const particlesConfig = {
	'login': loginParticlesJSON,
	'lines': linesParticlesJSON,
	'snow': snowParticlesJSON,
};

/* user facing */
export const particlesPrefs = ['lines', 'snow', 'off'];
