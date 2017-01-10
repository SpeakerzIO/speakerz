import React, { Component } from 'react';
import showdown from 'showdown';

const converter = new showdown.Converter();

export class Resume extends Component {

  constructor(props) {
    super(props);
    this.state = {
      showMarkdown: false
    };
  }

  render() {
    const state = this.props.state;
    return (
      <div className="row">
        <div className="col s12">
          <div className="input-field col s12">
            <textarea
              id="textarea1"
              className="materialize-textarea"
              value={state.en}
              onChange={e => this.props.setState({ ...state, en: e.target.value })}></textarea>
            <label htmlFor="textarea1">Your Resume</label>
            <button type="button" className="waves-effect waves-light btn right-align" onClick={() => this.setState(ps => ({ showMarkdown: !ps.showMarkdown }))}>Preview</button>
          </div>
        </div>
        {this.state.showMarkdown && (
          <div style={{ position: 'fixed', backgroundColor: 'rgba(255, 255, 255, 0.5)', left: 0, top: 0, width: '100vw', height: '100vw', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 10000,  }}>
            <div style={{ width: '50vw', height: '50vh', backgroundColor: 'white', color: 'black', border: '1px solid black' }} dangerouslySetInnerHTML={{ __html: converter.makeHtml(state.en) }} />
          </div>
        )}
      </div>
    );
  }
}
