AUI({
  lang : 'de'
}).use(
    'aui-datepicker',
    'datatype-date',
    function(Y) {
      Y.DateFormat = '%d.%m.%Y';

      [ '#temporalCoverageFrom', '#temporalCoverageUntil', '#datesCreated',
          '#datesModified', '#datesPublished', '.resource-modifieddate', 
          '#manualShowcaseCreatedDate', '#manualShowcaseModifiedDate', 
          '#modifyDate', '#createDate' ].forEach(function(item) {
        new Y.DatePicker({
          trigger : item,
          mask : Y.DateFormat,
          popover : {
            zIndex : 1
          }
        });
      });
      
      // confirm dialogs for remove buttons - and submit actions, if necessary
      
      function createInputField(type, name, value) {
        var input = document.createElement('input');
        input.setAttribute('type', 'hidden');
        input.name = name;
        input.value = value;
        return input;
      }
      
      var submitForm = function(name, value) {
        var editform = $('#editform');
        var input = createInputField('hidden', name, value);
        editform.append(input);
        editform.submit();
      }
      
      var submitMultiParamForm = function(name1, value1, name2, value2) {
        var editform = $('#editform');
        var input1 = createInputField('hidden', name1, value1);
        var input2 = createInputField('hidden', name2, value2);
        editform.append(input1);
        editform.append(input2);
        editform.submit();
      }
      
      var removeresourcedialog = $('#editform').data('removeresourcedialog');
      $('.buttonDeleteRow').click(function(e) {
        if(confirm(removeresourcedialog)) {
          submitForm("removeResource", e.target.value);
        }
      });
      
      var deletedatasetdialog = $('#editform').data('deletedatasetdialog');
      $('#buttonDeleteDataset').click(function(e) {
        if(confirm(deletedatasetdialog)) {
          submitForm("deleteDataset", "yes, please");
        }
      });
      
      $('#buttonAddRow').click(function(e) {
        submitForm("addResource", "yes, please");
      });
      
      $('#buttonAddAppLink').click(function(e) {
        submitForm("addLinkToShowcase", "yes, please");
      });
      
      $('#buttonAddDatasetLink').click(function(e) {
        submitForm("addLinkToDataset", "yes, please");
      });
      
      $('.buttonRemoveImage').click(function(e) {
        submitForm("removeImage", e.target.value);
      });
      
      // register confirmation when user wants to leave page without saving changes
      var formIsDirty = false;
      $('#editform input, #editform textarea, #editform select').change(function() {
        formIsDirty = true;
      });
      
      $('#editform').submit(function(event) {
        formIsDirty = false; // we do not want the confirmation message when saving the form.
      });
      
      function getCroppieConfig() {
        return {
          enableExif: true,
          viewport: {
              width: 228,
              height: 228
          },
          boundary: {
              width: 282,
              height: 282
          }
        }
      }
      
      function getResultCroppieConfig() {
        return {
          type: 'canvas',
          size: { width: 600 }
        }
      }
      
      function sendImageUploadRequest(idx, resp) {
        document.getElementById("uploadButton" + idx).classList.remove("hideElement");
        document.getElementById("uploadCroppie" + idx).classList.add("hideElement");
        document.getElementById("uploadResult" + idx).classList.add("hideElement");
        submitMultiParamForm("uploadImage", resp, "imageIndex", idx);
      }
      
      function readFile(input, idx) {
        if (input.files && input.files[0]) {
              var reader = new FileReader();
              
              reader.onload = function (ev) {
                switch (idx) {
                  case "0":
                    $uploadCrop0.croppie('bind', {
                      url: ev.target.result
                    });
                  case "1":
                    $uploadCrop1.croppie('bind', {
                      url: ev.target.result
                    });
                  case "2":
                    $uploadCrop2.croppie('bind', {
                      url: ev.target.result
                    });
                  case "3":
                    $uploadCrop3.croppie('bind', {
                      url: ev.target.result
                    });
                }
              }
              reader.readAsDataURL(input.files[0]);
              document.getElementById("uploadButton" + idx).classList.add("hideElement");
              document.getElementById("uploadCroppie" + idx).classList.remove("hideElement");
              document.getElementById("uploadResult" + idx).classList.remove("hideElement");
          }
      }
      
      var $uploadCrop0;
      var $uploadCrop1;
      var $uploadCrop2;
      var $uploadCrop3;
      
      $uploadCrop0 = $('#uploadCroppie0').croppie(getCroppieConfig());
      $uploadCrop1 = $('#uploadCroppie1').croppie(getCroppieConfig());
      $uploadCrop2 = $('#uploadCroppie2').croppie(getCroppieConfig());
      $uploadCrop3 = $('#uploadCroppie3').croppie(getCroppieConfig());
      
      $('#uploadResult0').on('click', function (e) {
        $uploadCrop0.croppie('result', getResultCroppieConfig()).then(function (resp) {
          sendImageUploadRequest(e.target.value, resp);
        });
      });
      
      $('#uploadResult1').on('click', function (e) {
        $uploadCrop1.croppie('result', getResultCroppieConfig()).then(function (resp) {
          sendImageUploadRequest(e.target.value, resp);
        });
      });
      
      $('#uploadResult2').on('click', function (e) {
        $uploadCrop2.croppie('result', getResultCroppieConfig()).then(function (resp) {
          sendImageUploadRequest(e.target.value, resp);
        });
      });
      
      $('#uploadResult3').on('click', function (e) {
        $uploadCrop3.croppie('result', getResultCroppieConfig()).then(function (resp) {
          sendImageUploadRequest(e.target.value, resp);
        });
      });
      
      $('#fileField0').change(function(e) {
        readFile(this, "0");
      });
      
      $('#fileField1').change(function(e) {
        readFile(this, "1");
      });
      
      $('#fileField2').change(function(e) {
        readFile(this, "2");
      });
      
      $('#fileField3').change(function(e) {
        readFile(this, "3");
      });
      
      window.addEventListener("beforeunload", function(event) {
        if(formIsDirty) {
          var confirmmessage = $('#editform').data('onbeforeunload');
          event.returnValue = confirmmessage;
          return confirmmessage;
        }
      });
    });