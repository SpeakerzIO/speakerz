import React, { Component } from 'react';

export class BasicInfo extends Component {
  render() {
    const state = this.props.state;
    return (
      <div className="row">
        <div className="col s6">
          <div className="row">
            <div className="input-field col s12">
              <input id="name" type="text" className="validate"
                     value={state.name} onChange={e => this.props.setState({ ...state, name: e.target.value })} />
              <label for="name">Name</label>
            </div>
          </div>
          <div className="row">
            <div className="input-field col s12">
              <input id="nickname" type="text" className="validate"
                     value={state.nickname} onChange={e => this.props.setState({ ...state, nickname: e.target.value })} />
              <label for="nickname">Nickname</label>
            </div>
          </div>
        </div>
        <div className="col s6">
          <div className="row">
            <div className="input-field col s12">
              <img src={state.avatarUrl || '#'}  />
            </div>
          </div>
          <div className="row">
            <div className="input-field col s12">
              <input id="avatar" type="text" className="validate"
                     value={state.avatarUrl} onChange={e => this.props.setState({ ...state, avatarUrl: e.target.value })} />
              <label for="avatar">Avatar</label>
            </div>
          </div>
        </div>
      </div>
    );
  }
}
