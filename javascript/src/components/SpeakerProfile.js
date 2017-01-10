import React from 'react';
import $ from 'jquery';

import { BasicInfo } from './BasicInfo';
import { Resume } from './Resume';
import { Talks } from './Talks';
import { Links } from './Links';

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
    const newState = { ...state };
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
                <Resume state={this.state.resume} setState={resume => this.updateForm({ ...this.state, resume })} />
              </div>
            </li>
            <li>
              <div className="collapsible-header"><i className="material-icons">settings_voice</i>Your Talks</div>
              <div className="collapsible-body collapsible-with-margin">
                <Talks state={this.state.talks} setState={talks => this.updateForm({ ...this.state, talks })} />
              </div>
            </li>
          </ul>
          <button className="waves-effect waves-light btn right-align" type="button" onClick={this.submit}>
            <i className="material-icons left">contacts</i>Save
          </button>
          <a className="waves-effect waves-light btn right-align red" data-target="deleteAccountModal" style={{ marginLeft: 5 }} href="#deleteAccountModal">
            <i className="material-icons left">delete</i>Delete my account
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
