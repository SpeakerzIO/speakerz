import React, { Component } from 'react';
import showdown from 'showdown';
import $ from 'jquery';

const converter = new showdown.Converter();

export class MarkdownPreview extends Component {

  componentDidMount() {
    $(this.modal).modal();
  }

  render() {
    return (
      <div>
        <a href={`#preview-modal-${this.props.id}`} className="waves-effect waves-light btn right-align"><i className="material-icons">visibility</i></a>
        <div ref={(ref) => this.modal = ref} id={`preview-modal-${this.props.id}`} className="modal modal-fixed-footer">
          <div className="modal-content">
            <h4>Markdown preview</h4>
            <p style={{ backgroundColor: '#ddd' }} dangerouslySetInnerHTML={{ __html: converter.makeHtml(this.props.markdown) }}></p>
          </div>
          <div className="modal-footer">
            <a href="#!" className="modal-action modal-close waves-effect waves-light btn-flat ">Close</a>
          </div>
        </div>
      </div>
    );
  }
}
