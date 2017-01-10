import React, { Component } from 'react';
import showdown from 'showdown';

const converter = new showdown.Converter();

export class Talk extends Component {

  constructor(props) {
    super(props);
    this.state = {
      showMarkdown: false
    };
  }

  render() {
    const talk = this.props.talk;
    return (
      <li className="collection-item" style={{ borderBottom: '1px solid #ddd', marginBottom: 10 }}>
        <div className="input-field col s12">
          <input id="name"
                 type="text"
                 className="validate"
                 value={talk.name.en}
                 onChange={e => this.props.setState({ ...talk, name: { en: e.target.value } })} />
          <label for="name">Name</label>
        </div>
        <div className="input-field col s12">
          <textarea
            className="materialize-textarea"
            value={talk.abstract.en}
            onChange={e => this.props.setState({ ...talk, abstract: { en: e.target.value } })}></textarea>
          <label htmlFor="textarea1">Your talks abstract</label>
        </div>
        <button type="button" className="waves-effect waves-light btn right-align" onClick={() => this.setState(ps => ({ showMarkdown: !ps.showMarkdown }))}>Preview</button>
        {this.state.showMarkdown && (
          <div style={{ position: 'fixed', backgroundColor: 'rgba(255, 255, 255, 0.5)', left: 0, top: 0, width: '100vw', height: '100vw', display: 'flex', justifyContent: 'center', alignItems: 'center', zIndex: 10000,  }}>
            <div style={{ width: '50vw', height: '50vh', backgroundColor: 'white', color: 'black', border: '1px solid black' }} dangerouslySetInnerHTML={{ __html: converter.makeHtml(state.en) }} />
          </div>
        )}
      </li>
    );
  }
}
