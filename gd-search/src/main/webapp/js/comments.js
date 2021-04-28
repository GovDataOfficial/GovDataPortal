function initializeComments() {
  // only initialize if we have a comment-area
  if(document.getElementById('comment-container') === null) {
    return;
  }

  var $container = $('#comment-container');
  var addurl = $container.data('addurl');
  var editurl = $container.data('editurl');
  var deleteurl = $container.data('deleteurl');
  var confirmquestion = $container.data('confirmquestion');
  var loginurl = $container.data('loginurl');
  var dialogtitle = $container.data('dialogtitle');

  var errors = {
    400: $container.data('error400'),
    403: $container.data('error403'),
    404: $container.data('error404'),
    500: $container.data('error500')
  };

  function handleError(xhr, status, error) {
    // set default error message
    var errorText = "Unknown error";
    if (errors.hasOwnProperty(xhr.status)) {
      errorText = errors[xhr.status];
    }

    // override error message if supplied
    if (xhr.hasOwnProperty('responseJSON') && xhr.responseJSON.hasOwnProperty('message')) {
      errorText = xhr.responseJSON.message;
    }

    $('#comment-message-box').removeClass('alert-info').addClass('alert-error').text(errorText).show();
  }

  // action methods
  function addComment(commentData, emailAddress, reloadAfterSubmit) {
    $.ajax({
      url: addurl,
      type: 'POST',
      dataType: 'html',
      data: 'comment=' + encodeURIComponent(commentData) + (emailAddress ? "&emailAddress=" + encodeURIComponent(emailAddress) : ""),
      success: function(data) {
        if ($.trim(data) !== '') {
          // if we reload, we can just stop here...
          if(reloadAfterSubmit) {
            window.location.reload();
            return;
          }

          // attach event handlers and add to comment list
          var elem = $(data);
          commentAddHandlers(elem);
          $('#comment-list').prepend(elem);

          // reset form and hide error messages
          $('#newCommentInput').val('');
          enableSubmit($('#newCommentInput'), $('#newCommentSubmit'));

          // Toggle empty status-class
          $('#comment-container').toggleClass('no-comments', ($('#comment-list div').length === 0));

          $('#comment-message-box').hide();
        }
      },
      error: handleError
    });
  }

  function editComment(commentId, commentData, commentEntry) {
    $.ajax({
      url: editurl,
      type: 'POST',
      dataType: 'json',
      data: 'comment=' + encodeURIComponent(commentData) + '&commentId=' + commentId,
      success: function(data) {
        if ($.trim(data) !== '') {
          // Replace comment in text input field
          commentEntry.find('textarea').val(data.comment);
          // Replace comment dislay
          commentEntry.find('.comment-text').text(data.comment);
          // hide edit form, show rendered text
          commentEntry.find('.comment-text').show();
          commentEntry.find('.comment-edit-area').hide();

          $('#comment-message-box').hide();
        }
      },
      error: handleError
    });
  }

  function deleteComment(commentId, commentEntry) {
    $.ajax({
      url: deleteurl,
      type: 'POST',
      dataType: 'json',
      data: 'commentId=' + commentId,
      success: function(data) {
        if ($.trim(data) !== '') {
          $('#comment-message-box').removeClass('alert-error').addClass('alert-info').text(data.message).show();

          // remove comment from list
          commentEntry.remove();

          // Toggle empty status-class
          $('#comment-container').toggleClass('no-comments', ($('#comment-list div').length === 0));
        }
      },
      error: handleError
    });
  }

  function enableSubmit(textarea, button) {
    if (textarea === undefined || textarea.val() === undefined) return;
    if (textarea.val().length > 0) {
      button.prop('disabled', false);
    } else {
      button.prop('disabled', true);
    }
  }

  // add comment action
  $('#newCommentSubmit.existinguser').click(function() {
    addComment($('#newCommentInput').val());
  });
  $('#newCommentSubmit.anonymoususer').click(function() {
    // set auth token for login form
    $('#pAuthToken').val(Liferay.authToken);
    // trigger modal login and set callbacks

    window.namespace = 'govdatacomments';
    window.randomNamespace = 'govdatacomments';

    // this will be called upon login via fastlogin-popup
    window.govdatacommentsafterLogin = function(emailAddress, anonymousAccount) {
      addComment($('#newCommentInput').val(), emailAddress, !anonymousAccount); // reload if existing user
    };

    Liferay.Util.openWindow({
      dialog: {
        height: 460,
        width: 770
      },
      id: 'govdatacommentssignInDialog',
      title: dialogtitle,
      uri: loginurl
    });
  });
  $('#newCommentInput').bind("keyup input", function() {
    enableSubmit($('#newCommentInput'), $('#newCommentSubmit'));
  });

  // initialize textarea
  enableSubmit($('#newCommentInput'), $('#newCommentSubmit'));

  // handlers for existing comments
  function commentAddHandlers($entry) {
    var commentId = $entry.data('commentId');

    // set event handlers
    var textarea = $entry.find('textarea');
    var submitbutton = $entry.find('button.savechange');

    textarea.bind("keyup input", function() {
      enableSubmit(textarea, submitbutton);
    });

    $entry.find('button.edit').click(function() {
      $entry.find('.comment-edit-area').show();
      $entry.find('.comment-text').hide();
      textarea.focus();
    });

    $entry.find('button.delete').click(function() {
      if (confirm(confirmquestion)) {
        deleteComment(commentId, $entry);
      }
    });

    submitbutton.click(function() {
      editComment(commentId, textarea.val(), $entry);
    });

    $entry.find('button.cancel').click(function() {
      textarea.val($entry.find('.comment-text').text()); // restore comment text to textarea
      $entry.find('.comment-edit-area').hide();
      $entry.find('.comment-text').show();
    });
  }

  // add handlers to existing comments
  $('.comment-entry').each(function() {
    commentAddHandlers($(this));
  });
}

// at the time of load jQuery is already loaded
window.addEventListener('load', initializeComments);
