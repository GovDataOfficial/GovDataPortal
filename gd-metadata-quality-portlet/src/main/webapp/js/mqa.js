// Hide all explanations in subtitles by default
$(document).ready(function() {
   $('.subtitle-ext').hide();
});

function toggleChartExtText(icon, textBlockIdSelector) {
  var suffix = 'open';
  var titleLangKey = 'od.metadata.charts.subtitle.title_tag.show.less';
  if ($(textBlockIdSelector).is(":visible")) {
    suffix = 'closed';
    titleLangKey = 'od.metadata.charts.subtitle.title_tag.show.more';
    icon.innerHTML = getInfoIcon(suffix);
  } else {
    icon.innerHTML = getInfoIcon(suffix);
  }
  icon.title = Liferay.Language.get(titleLangKey);
  $(textBlockIdSelector).toggle();
}

function getInfoIcon(suffix) {
  return '<svg aria-hidden="true" class="lexicon-icon lexicon-icon-info-panel-' + suffix + '">' +
      '<use xlink:href="' + Liferay.ThemeDisplay.getPathThemeImages() + '/lexicon/icons.svg#info-panel-' + suffix + '" />' +
    '</svg>';
}