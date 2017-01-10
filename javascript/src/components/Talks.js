import React, { Component } from 'react';
import { Talk } from './Talk';
import uuidV4 from 'uuid/v4';

export class Talks extends Component {

  addTalk = (e) => {
    const id = uuidV4();
    const talk = { id, name: { en: '' }, abstract: { en: '' }, sessions: [] };
    this.props.setState([ ...this.props.state, talk ]);
  };

  updateTalk = (talk) => {
    const newTalks = [ ...this.props.state.filter(t => t.id !== talk.id), talk ];
    this.props.setState(newTalks);
  };

  render() {
    return (
      <div>
        <ul className="collection">
          {this.props.state.map(t => <Talk key={t.id} lang={this.props.lang} talk={t} setState={this.updateTalk} />)}
        </ul>
        <button className="waves-effect waves-light btn right-align btn-small" type="button" onClick={this.addTalk} style={{ marginBottom: 10 }}>
          <i className="material-icons left">add</i>Add talk
        </button>
      </div>
    );
  }
}
