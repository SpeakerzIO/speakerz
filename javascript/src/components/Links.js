import React, { Component } from 'react';

export class Links extends Component {
  render() {
    const state = this.props.state;
    return (
      <div className="row">
        <div className="input-field col s6">
          <input id="websiteUrl" type="text" className="validate"
                 value={state.websiteUrl} onChange={e => this.props.setState({ ...state, websiteUrl: e.target.value })} />
          <label for="websiteUrl">Your Website URL</label>
        </div>
        <div className="input-field col s6">
          <input id="twitterHandle" type="text" className="validate"
                 value={state.twitterHandle} onChange={e => this.props.setState({ ...state, twitterHandle: e.target.value })} />
          <label for="twitterHandle">@YourTwitterHandle</label>
        </div>
        <div className="input-field col s6">
          <input id="githubHandle" type="text" className="validate"
                 value={state.githubHandle} onChange={e => this.props.setState({ ...state, githubHandle: e.target.value })} />
          <label for="githubHandle">YourGithubHandle</label>
        </div>
      </div>
    );
  }
}
