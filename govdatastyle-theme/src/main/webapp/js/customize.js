// ### OFF-CANVAS Enhancements
// scroll off-canvas-menu into place on open and restore scroll position on close
var currentScrollPosition = 0;
$(document).on('open.fndtn.offcanvas', '[data-offcanvas]', function() {
  currentScrollPosition = $(window).scrollTop(); // remember scroll position for later use
  $('.off-canvas-inner-wrap').css('top', currentScrollPosition);
});
$(document).on('close.fndtn.offcanvas', '[data-offcanvas]', function() {
  $(window).scrollTop(currentScrollPosition); // restore scroll position on menu-close
});

// #### Off-Canvas (general)
// switch between mainmenu and offcanvas-container menu
function sbSelectActiveMenu(activeMenuSelector) {
  var containerSelector = '.off-canvas-container';
  $(containerSelector + ':not(' + activeMenuSelector + ')').hide();
  $(containerSelector + activeMenuSelector).show();
}

// event listener for off-canvasmenu
$('#_145_navSiteNavigationNavbarBtn').click(function() {
  sbSelectActiveMenu('#off-canvas-mainmenu');
  $('.off-canvas-inner-wrap a, .off-canvas-inner-wrap button').removeAttr('tabindex');
  $('.off-canvas-wrap').addClass('move-left');
});
$('.off-canvas-close').click(function() {
  $('.off-canvas-wrap').removeClass('move-left');
  $('.off-canvas-inner-wrap a, .off-canvas-inner-wrap button').attr('tabindex','-1');
  $('.right-off-canvas-toggle').attr('aria-expanded', 'false');
});

// prevent tabbing into off-canvas if it is not open
$('.off-canvas-inner-wrap a, .off-canvas-inner-wrap button').attr('tabindex','-1');

// #### Filterarea
// off-canvas searchresults menu-transplantation
var breakpointMedium = 1024;
var breakpointSmall = 768;
var offCanvasContainerId = 'off-canvas-container';
var filterAreaContainerId = 'filterarea-container';

function sbFilterAreaResizeHandler() {
  // check threshold
  var width = $(window).width();
  var offCanvasContainer = document.getElementById(offCanvasContainerId);
  var isContainerUsed = offCanvasContainer.hasChildNodes();
  var element;

  if (width <= breakpointMedium && !isContainerUsed) {
    // move the Filter-area to the off-canvas-menu
    element = document.getElementById(filterAreaContainerId).firstElementChild;
    offCanvasContainer.appendChild(element);
  }
  if (width > breakpointMedium && isContainerUsed) {
    // move the Filter back to it's original place
    element = offCanvasContainer.firstElementChild;
    document.getElementById(filterAreaContainerId).appendChild(element);
  }
}

// apply listener only when the filterarea is element of the current page
if (document.getElementById(filterAreaContainerId) !== null) {
  // menu transplantation
  window.addEventListener('resize', sbFilterAreaResizeHandler);
  sbFilterAreaResizeHandler(); // initialize

  // menu selection -- Filterarea
  $('.open-filter-menu-medium,.open-filter-menu-small').click(function() {
    sbSelectActiveMenu('#off-canvas-container');
  });
}

// ### Main menu
// dropdown
$('.off-canvas-nav .dropdown-toggle').click(function() {
  $(this).parent().toggleClass('open');
});

// Open Off-Canvas Menu-Link in Dialog
$('.oc-userprofile').click(function(ev) {
  ev.preventDefault();

  var config = {
    'title': ev.currentTarget.title,
    'uri': ev.currentTarget.href
  };
  Liferay.Util.openWindow(config);

  // Close Off-canvas
  $('.off-canvas-wrap').removeClass('move-left');
  $('.right-off-canvas-toggle').attr('aria-expanded', 'false');
});

// ### searchresults
// show-more-button (show hidden items and hide itself)
$('.filtergroup .show-more-button').click(function(evt) {
  evt.preventDefault();
  var elem = evt.currentTarget;
  elem.parentNode.parentNode.className += " show-hidden-rows";
  elem.style.display = "none";
  $(elem.parentNode.parentNode).find('.show-more-hidden').first().focus();
});

// load next hits
$(function() {
  window.searchCallCount = 0;

  var removeMoreHitsButton = function() {
    $('#searchresults-more-block').hide();
  };

  window.scrollNextHits = function scrollNextHits(scrollResourceURL, scrollIdParam, pageSize) {
    window.searchCallCount++;
    var postData = 'scrollId=' + scrollIdParam;
    $.ajax({
      url: scrollResourceURL,
      type: 'POST',
      data: postData,
      success: function(data) {
        // Button '10 weitere anzeigen' ausblenden sobald es keine weiteren Treffer mehr zum Nachladen gibt
        var resultsCount = $(data).filter('.resultentry').length;
        if (resultsCount < pageSize) {
          removeMoreHitsButton();
        }
        // Falls keine Daten mehr zurückgeliefert wurden
        if ($.trim(data) === '') {
          return;
        }

        // insert new data and focus on first new item
        var lastResultentry = $('#searchresult-list > div:last-child');
        $('#searchresult-list').append(data);
        lastResultentry.next().find('a:first-child').focus();
      },
      error: function() {
        // scroll-ID nicht mehr gueltig? Starte neue Suche.
        location.reload();
      }
    });
  };
});

// rating (initialize all existing widgets on this page - may be 0)
$('.rateit-widget').each(function() {
  var $widget = $(this);
  var $ratingElements = $(this).find('.rating-element');
  var userscore = $widget.data('userscore');
  var ratingactionurl = $widget.data('ratingactionurl');

  function updateStars(number) {
    $ratingElements.each(function() {
      $(this).toggleClass('active', ($(this).data('score') <= number));
    });
  }

  function submitRating(rating) {
    console.log("rating!");
    $.ajax({
      url: ratingactionurl,
      method: 'POST',
      dataType: "json",
      data: "rating=" + rating
    }).success(function(data) {
      // save new rating
      userscore = data.rating;
      updateStars(userscore);

      // update average ratings
      $('.rating-area .rating-element').each(function() {
        if ($(this).data('score') <= data.avgRating) {
          $(this).removeClass('sbi-sterne-bewertung').addClass('sbi-sterne-bewertung-full');
        } else {
          $(this).addClass('sbi-sterne-bewertung').removeClass('sbi-sterne-bewertung-full');
        }
      });
      $('.rating-area .rating-mean').html(data.ratingCount);

      // show success message
      var titleElem = $('.rating-title');
      titleElem.html(titleElem.data('successmessage'));
    });
  }

  $widget.find('.rating-element')
    .click(function() {
      submitRating($(this).data('score'));
    })
    .on('mouseenter focus', function() {
      updateStars($(this).data('score'));
    })
    .on('mouseleave blur', function() {
      updateStars(userscore);
    });
});

// initialize datepicker for date-filter
if(document.querySelector('.date-filter')) {
  ['filter-date-from', 'filter-date-until'].forEach(function(inputfield) {
    AUI({
      lang: 'de'
    }).use('aui-datepicker', function(Y) {
      new Y.DatePicker({
        trigger: '.'+inputfield,
        mask: '%d.%m.%Y',
        popover: {
          zIndex: 1
        },
        on: {
          selectionChange: function(event) {
            $('#'+inputfield+'-checkbox').prop('checked', true);
          }
        }
      });
    });

    // add handler for clearing the date when checkbox is unchecked
    $('#'+inputfield+'-checkbox').change(function(evt) {
      if(!this.checked) {
        $('.'+inputfield).val('');
      }
    });
  });
}

// initialize saytCompletion for searchfield
if(document.querySelector('#searchfieldform')) {
  var saytCompletionUrl = $('#searchfieldform').data('saytcompletionurl');
  var saytHintTpl = $('#searchfieldform').data('saythinttpl');

  AUI({
    lang: 'de'
  }).use('autocomplete', 'autocomplete-highlighters', 'datasource-io', function (Y) {
    // Create a DataSource instance.
    var ds = new Y.DataSource.IO({
      source: saytCompletionUrl
    });

    Y.one('#searchfield').plug(Y.Plugin.AutoComplete, {
      maxResults: 10,
      resultHighlighter: 'phraseMatch',
      source: ds,
      requestTemplate: '&q={query}',

      resultListLocator: function (response) {
        var suggestions = JSON.parse(response[0].response).suggestions;

        var hintmsg = saytHintTpl.replace('%1', suggestions.length);
        $('#searchfield-hint').html(hintmsg);

        return suggestions;
      }
    });
  });
}
