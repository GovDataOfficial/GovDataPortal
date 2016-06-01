AUI({
  lang : 'de'
}).use(
    'aui-datepicker',
    'datatype-date',
    function(Y) {
      Y.DateFormat = '%d.%m.%Y';

      [ '#temporalCoverageFrom', '#temporalCoverageUntil', '#datesCreated',
          '#datesModified', '#datesPublished' ].forEach(function(item) {
        new Y.DatePicker({
          trigger : item,
          mask : Y.DateFormat,
          popover : {
            zIndex : 1
          }
        });
      });
      
      // confirm dialogs for remove buttons - and submit actions, if necessary
      
      
      var submitForm = function(name, value) {
        var additionalParameterField = document.getElementById('additionalParameterField');
        var editform = $('#editform');
        additionalParameterField.name = name;
        additionalParameterField.value = value;
        editform.submit();
      }
      
      var removerowdialog = $('#editform').data('removerowdialog');
      $('.buttonDeleteRow').click(function(e) {
        if(confirm(removerowdialog)) {
          submitForm("removeRow", e.target.value);
        }
      });
      
      var deletedatasetdialog = $('#editform').data('deletedatasetdialog');
      $('#buttonDeleteDataset').click(function(e) {
        if(confirm(deletedatasetdialog)) {
          submitForm("deleteDataset", "yes, please");
        }
      });
      
      $('#buttonAddRow').click(function(e) {
        submitForm("addRow", "yes, please");
      });
      
      // register confirmation when user wants to leave page without saving changes
      var formIsDirty = false;
      $('#editform input, #editform textarea, #editform select').change(function() {
        formIsDirty = true;
      })
      
      $('#editform').submit(function(event) {
        formIsDirty = false; // we do not want the confirmationmessage when saving the form.
      });
      
      window.addEventListener("beforeunload", function(event) {
        if(formIsDirty) {
          var confirmmessage = $('#editform').data('onbeforeunload');
          event.returnValue = confirmmessage;
          return confirmmessage;
        }
      });
    });