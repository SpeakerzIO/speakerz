import 'es6-shim';
import 'whatwg-fetch';

import React from 'react';
import ReactDOM from 'react-dom';
import Symbol from 'es-symbol';
import $ from 'jquery';

import { SpeakerProfile } from './components/SpeakerProfile';

if (!window.Symbol) {
  window.Symbol = Symbol;
}
window.$ = $;
window.jQuery = $;

$(document).ready(function(){
  $('.button-collapse').sideNav();
});

export function initEditView(speaker) {
  ReactDOM.render(<SpeakerProfile speaker={speaker} />, document.getElementById('app'));
}
