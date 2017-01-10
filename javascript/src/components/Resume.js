import React, { Component } from 'react';
import showdown from 'showdown';
import { MarkdownPreview } from './MarkdownPreview';

const converter = new showdown.Converter();

export class Resume extends Component {

  render() {
    const state = this.props.state;
    return (
      <div className="row">
        <div className="col s12">
          <div className="input-field col s12">
            <textarea
              id="textarea1"
              className="materialize-textarea"
              value={state[this.props.lang]}
              onChange={e => this.props.setState({ ...state, [this.props.lang]: e.target.value })}></textarea>
            <label htmlFor="textarea1">Your Resume</label>
          </div>
          <MarkdownPreview id="resume" markdown={state[this.props.lang]} />
        </div>
      </div>
    );
  }
}
