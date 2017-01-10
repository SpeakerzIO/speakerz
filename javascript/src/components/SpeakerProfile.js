import React from 'react';
import showdown from 'showdown';
import $ from 'jquery';

const converter = new showdown.Converter();

const BasicInfo = React.createClass({
  render() {
    const state = this.props.state;
    return (
      <div className="row">
        <div className="col s6">
          <div className="row">
            <div className="input-field col s12">
              <input id="name" type="text" className="validate"
                     value={state.name} onChange={e => this.props.setState(Object.assign({}, state, { name: e.target.value }))} />
              <label for="name">Name</label>
            </div>
          </div>
          <div className="row">
            <div className="input-field col s12">
              <input id="nickname" type="text" className="validate"
                     value={state.nickname} onChange={e => this.props.setState(Object.assign({}, state, { nickname: e.target.value }))} />
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
                     value={state.avatarUrl} onChange={e => this.props.setState(Object.assign({}, state, { avatarUrl: e.target.value }))} />
              <label for="avatar">Avatar</label>
            </div>
          </div>
        </div>
      </div>
    );
  }
});

const Resume = React.createClass({
  getInitialState() {
    return {
      showMarkdown: false
    }
  },
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
              onChange={e => this.props.setState(Object.assign({}, state, { en: e.target.value }))}></textarea>
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
});

const Links = React.createClass({
  render() {
    const state = this.props.state;
    return (
      <div className="row">
        <div className="input-field col s6">
          <input id="websiteUrl" type="text" className="validate"
                 value={state.websiteUrl} onChange={e => this.props.setState(Object.assign({}, state, { websiteUrl: e.target.value }))} />
          <label for="websiteUrl">Your Website URL</label>
        </div>
        <div className="input-field col s6">
          <input id="twitterHandle" type="text" className="validate"
                 value={state.twitterHandle} onChange={e => this.props.setState(Object.assign({}, state, { twitterHandle: e.target.value }))} />
          <label for="twitterHandle">@YourTwitterHandle</label>
        </div>
        <div className="input-field col s6">
          <input id="githubHandle" type="text" className="validate"
                 value={state.githubHandle} onChange={e => this.props.setState(Object.assign({}, state, { githubHandle: e.target.value }))} />
          <label for="githubHandle">YourGithubHandle</label>
        </div>
      </div>
    );
  }
});

const Talks = React.createClass({
  render() {
    return null;
  }
});

export const SpeakerProfile = React.createClass({
  getInitialState() {
    return {
      nickname: this.props.speaker.nickname || '',
      name: this.props.speaker.name || '',
      resume: this.props.speaker.resume || {
        en: ''
      },
      avatarUrl: this.props.speaker.avatarUrl || `https://www.gravatar.com/avatar/${encodeURIComponent(this.props.speaker.id)}?s=50&r=pg&d=retro`,
      websiteUrl: this.props.speaker.websiteUrl || '',
      twitterHandle: this.props.speaker.twitterHandle || '',
      githubHandle: this.props.speaker.githubHandle || '',
      talks: []
    };
  },
  componentDidMount() {
    setTimeout(() => {
      Materialize.updateTextFields();
      $('.collapsible').collapsible();
      $('.modal').modal();
    }, 300);
  },
  submit(e) {
    e.preventDefault();
    console.log('final state is', this.state);
    $.ajax({
      method: 'POST',
      url: '/edit',
      dataType: 'application/json',
      contentType: 'application/json',
      data: JSON.stringify(this.state),
    }).then(data => {
      window.location.reload();
    });
  },
  updateForm(state) {
    const newState = Object.assign({}, state);
    console.log('new state is', state);
    this.setState(newState);
  },
  deleteAccount(e) {
    e.preventDefault();
    $.ajax({
      method: 'POST',
      url: '/account/destroy',
      dataType: 'application/json',
      contentType: 'application/json',
      data: JSON.stringify(this.state),
      complete: () => {
        console.log('yoooooo');
        window.location = '/';
      }
    });
  },
  render() {
    return (
      <div className="row">
        <form className="col s12">
          <ul className="collapsible" data-collapsible="accordion">
            <li>
              <div className="collapsible-header active"><i className="material-icons">perm_identity</i>Your infos</div>
              <div className="collapsible-body collapsible-with-margin">
                <BasicInfo state={{
                    nickname: this.state.nickname,
                    name: this.state.name,
                    avatarUrl: this.state.avatarUrl,
                }} setState={this.updateForm} />
              </div>
            </li>
            <li>
              <div className="collapsible-header"><i className="material-icons">code</i>Your links</div>
              <div className="collapsible-body collapsible-with-margin">
                <Links state={{
                    websiteUrl: this.state.websiteUrl,
                    twitterHandle: this.state.twitterHandle,
                    githubHandle: this.state.githubHandle,
                }} setState={this.updateForm} />
              </div>
            </li>
            <li>
              <div className="collapsible-header"><i className="material-icons">subject</i>Your Resume</div>
              <div className="collapsible-body collapsible-with-margin">
                <Resume state={this.state.resume} setState={resume => this.updateForm({ resume })} />
              </div>
            </li>
            <li>
              <div className="collapsible-header"><i className="material-icons">settings_voice</i>Your Talks</div>
              <div className="collapsible-body collapsible-with-margin">
                <Talks state={this.state.talks} setState={talks => this.updateForm({ talks })} />
              </div>
            </li>
          </ul>
          <button className="waves-effect waves-light btn right-align" type="button" onClick={this.submit}>
            <i className="material-icons left">contacts</i>Save
          </button>
          <a className="waves-effect waves-light btn right-align red" data-target="deleteAccountModal" style={{ marginLeft: 5 }} href="#deleteAccountModal">
            <i className="material-icons left">delete</i>Delete account
          </a>
          <div id="deleteAccountModal" className="modal">
            <div className="modal-content">
              <h4>Destroy your account</h4>
              <p>If you continue, you are going to destroy your speaker account at www.speakez.io</p>
            </div>
            <div className="modal-footer">
              <a href="#!" onClick={this.deleteAccount} className="modal-action modal-close waves-effect waves-green btn-flat ">Destroy account</a>
              <a href="#!" className="modal-action modal-close waves-effect waves-red btn-flat ">Cancel</a>
            </div>
          </div>
        </form>
      </div>
    );
  }
});
