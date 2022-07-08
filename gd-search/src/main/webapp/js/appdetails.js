$(document).ready(function() {
  
  $('dl.expandible').each(function(){
    var $dl = $(this),
        $dds = $dl.find('dd:gt(2)'),
        isExpanded = $dl.hasClass('expanded');
    $dds[isExpanded ? 'show' : 'hide']();
    
    if($dds.length > 0){
        $dl
            .append($('<span class="showmore-btn"><dd class="expand">' + (isExpanded ? 'Weniger anzeigen' : 'Mehr anzeigen') + '</dd></span>')
            .click(function(event){
                var isExpanded = $dl.hasClass('expanded');
                event.preventDefault();
                $(this).html(isExpanded ? 'Mehr anzeigen' : 'Weniger anzeigen');
                $dl.toggleClass('expanded');
                $dds.toggle();
            }));
    }
  });
});