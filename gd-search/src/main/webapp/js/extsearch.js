AUI({lang: 'de'}).use('node', 'model', 'model-list', 'view', 'template-micro', 'aui-datepicker', 'datatype-date', function (Y) {
  Y.DateFormat = '%d.%m.%Y';

  // Define Fielddefinitions
  var dropdownFilterSaveHandler = function(res, model) {
    var value = model.get('name') + ":" + encodeURIComponent(model.get('selected')) + ",";
    res.f = (res.hasOwnProperty('f') ? res.f : '') + value;
  };

  var fieldTypes = {
    // text (more added below programmatically)
    'query': {
      type: "textview",
      name: "query",
      value: "",
      save: function(res, model) {        // save: Function to fill selected data (from model) into res-object
        res.q = model.get('value');
      }
    },

    // date
    'temporal_coverage': {
      type: "dateview",
      name: "temporal_coverage",
      dateFrom: null,
      dateUntil: null,
      save: function(res, model) {
        if(model.get('dateFrom')) {
          res.start = model.get('dateFrom');
        }
        if(model.get('dateUntil')) {
          res.end = model.get('dateUntil');
        }
      }
    },

    // dropdown
    'type': {
      type: "dropdownview",
      name: "type",
      options: typeList,
      selected: [],
      save: dropdownFilterSaveHandler
    },

    'openness': {
      type: "dropdownview",
      name: "openness",
      options: opennessList,
      selected: [],
      save: dropdownFilterSaveHandler
    },

    'licence': {
      type: "dropdownview",
      name: "licence",
      options: licenceList,
      selected: [],
      save: dropdownFilterSaveHandler
    },
    
    'sourceportal': {
      type: "dropdownview",
      name: "sourceportal",
      options: sourceportalList,
      selected: [],
      save: dropdownFilterSaveHandler
    },

    // autocomplete (eigentlich, aber wir machen erstmal kommaseparierte liste)
    'tags': {
      type: "textview",
      name: "tags",
      value: "",
      placeholder: translationMap["tags"]["placeholder"],
      save: function(res, model) {
        var values = "";
        var value = model.get('value');
        if(value) {
          var tags = value.split(",");
          tags.forEach(function(item) {
            // remove everything that is not allowed in a tag
            item = item.trim().toLowerCase().replace(/[^a-zäöüß0-9 \-_\.]/g, '');
            values += model.get('name') + ":" + encodeURIComponent(item) + ",";
          });

          res.f = (res.hasOwnProperty('f') ? res.f : '') + values;
        }
      }
    }
  };

  // add simple Text fields with same behaviour
  ['title', 'publisher', 'maintainer', 'notes'].forEach(function(item) {
    fieldTypes[item] = {
      type: "textview",
      name: item,
      value: "",
      save: function(res, model) {
        // append to existing property. If it does not exist, create it.
        if(model.get('value')) {
          var value = model.get('value').replace(/\/|,/g, ' ').trim();
          if (value)
            res.f = (res.hasOwnProperty('f') ? res.f : '') + model.get('name') + ":" + encodeURIComponent(value) + ",";
        }
      }
    };
  });

  // add multiboxviews
  [
    {name: 'groups', data: categoryList},
    {name: 'format', data: formatList}
  ]
  .forEach(function(item) {
    fieldTypes[item.name] = {
      type: "multiboxview",
      name: item.name,
      options: item.data,
      selected: [],
      save: function(res, model) {
        var values = "";
        model.get('selected').forEach(function(item) {
          values += model.get('name') + ":" + encodeURIComponent(item) + ",";
        });

        // append to existing property. If it does not exist, create it.
        res.f = (res.hasOwnProperty('f') ? res.f : '') + values;
      }
    };
  });

  // Define List
  Y.FieldListModel = Y.Base.create('fieldListModel', Y.ModelList, [], {
    getFieldData: function() {
      var res = {};
      this.each(function (model) {
        model.get('save')(res, model);
      });
      return res;
    }
  });

  Y.FieldListView = Y.Base.create('fieldListView', Y.View, [], {
    initializer: function() {
      var list = this.get('modelList');

      list.after(['reset'], this.reset, this);
      list.after(['add'], this.add, this);
      list.after(['remove'], this.remove, this);
    },

    reset: function() {
      var self = this;

      // clear everything
      this.get('container').setHTML("");

      // add all new models
      this.get('modelList').each(function(model) {
        self.addView.call(self, model);
      });

      $("#addFieldDropdown li").show();
      this.updateButtons();
    },

    add: function(e) {
      this.addView(e.model);

      $("#addFieldDropdown").find('li.' + e.model.get('name')).hide();
      this.updateButtons();
    },

    remove: function(e) {
      // views will remove themselves, we don't need to care at this point
      $("#addFieldDropdown").find('li.' + e.model.get('name')).show();
      this.updateButtons();
    },

    addView: function(model) {
      var view = new Fields[model.get('type')]({model: model});
      this.get('container').append(view.render().get('container'));
    },

    updateButtons: function() {
      var usedFieldCount = this.get('modelList').size();
      var hasEmptyFieldlist = (usedFieldCount === 0);
      $('#resetfields').attr('disabled', hasEmptyFieldlist);

      var availableFieldCount = Object.keys(fieldTypes).length;
      $('#addfields').attr('disabled', (usedFieldCount === availableFieldCount));
    }
  }, {
    ATTRS: {
      container: {
        valueFn: function() {
          return '#fieldlist';
        }
      }
    }
  });

  // Define Fields
  var fieldFrameTemplate = Y.Template.Micro.compile(
    '<div class="row field field-<%= this.type %>">' +
      '<div class="column small-12 medium-4 large-3"><div class="fieldtypelabel"><%= this["in"] %> „<%= this.typeName %>“</div></div>' +
      '<div class="column small-12 medium-8 large-9 fieldcontentarea">' +
        '<div><%== this.field %></div>' +
        '<button type="button" class="remove-field" title="<%= this.removefield %>"></button>' +
      '</div>' +
    '</div>'
  );
  var fieldFrameWrapper = function(template, model) {
    var innerTpl = template(model.toJSON());
    var outerTpl = fieldFrameTemplate({
      typeName: translationMap.fields[model.get('name')],
      "in": translationMap["in"],
      "removefield": translationMap.removefield,
      type: model.get('name'),
      field: innerTpl
    });
    return outerTpl;
  };

  var Fields = {
    textview: Y.Base.create('fieldTextView', Y.View, [], {
      containerTemplate: '<li>',

      events: {
        "input": {
          change: 'update'
        },
        ".remove-field": {
          click: 'remove'
        }
      },

      template: Y.Template.Micro.compile(
        '<label class="offscreen" for="<%= this.name %>"><%= this.labelName %></label><input type="text" id="<%= this.name %>" name="<%= this.name %>" value="<%= this.value %>"' +
        ' placeholder="<%= this.placeholder %>" />'
      ),

      render: function() {
        var model = this.get('model');
        model.set('translation', translationMap.text);
        model.set('placeholder', model.get('placeholder') ? model.get('placeholder') : translationMap.text.placeholder);
        model.set('labelName', translationMap["in"] + " " + translationMap.fields[model.get('name')]);

        var container = this.get('container');
        container.setHTML(fieldFrameWrapper(this.template, model));

        this.set('input', container.one('input'));
        return this;
      },

      update: function(e) {
        this.get('model').set('value', this.get('input').val());
      },

      remove: function(e) {
        this.constructor.superclass.remove.call(this);
        this.get('model').destroy({'delete': true});
      }
    }),

    dropdownview: Y.Base.create('fieldDropdownView', Y.View, [], {
      containerTemplate: '<li>',

      events: {
        "select": {
          change: 'update'
        },
        ".remove-field": {
          click: 'remove'
        }
      },

      template: Y.Template.Micro.compile(
        '<label class="offscreen" for="<%= this.name %>"><%= this.labelName %></label>' +
        '<select name="<%= this.name %>" id="<%= this.name %>">' +
          '<% this.options.forEach(function(option) { %>' +
            '<option value="<%= option.key %>"<%= option.selected %>><%= option.label %></option>' +
          '<% }); %>' +
        '</select>'
      ),

      render: function() {
        var model = this.get('model');
        model.set('labelName', translationMap["in"] + " " + translationMap.fields[model.get('name')]);

        var container = this.get('container');

        // mark selected option
        var selected = model.get('selected')[0];
        var selectionFound = false;
        model.get('options').forEach(function(option) {
          if(selected == option.key) {
            option.selected = " selected";
            selectionFound = true;
          }
        });
        if(!selectionFound) {
          model.get('options')[0].selected = " selected";
        }

        // set container and html
        container.setHTML(fieldFrameWrapper(this.template, model));
        this.set('select', container.one('select'));

        // save our selection to the model
        this.update();

        return this;
      },

      update: function(e) {
        this.get('model').set('selected', this.get('select').val());
      },

      remove: function(e) {
        this.constructor.superclass.remove.call(this);
        this.get('model').destroy({'delete': true});
      }
    }),

    multiboxview: Y.Base.create('fieldMultiboxView', Y.View, [], {
      containerTemplate: '<li>',

      events: {
        "input": {
          change: 'update'
        },
        ".remove-field": {
          click: 'remove'
        }
      },

      template: Y.Template.Micro.compile(
        '<fieldset class="multiboxarea">' +
          '<legend class="offscreen"><%= this.labelName %></legend>' +
          '<% var self=this; this.options.forEach(function(option) { %>' +
            '<div class="checkboxitem">' +
              '<input class="offscreen" id="<%= self.name + "_" + option.key %>" <%= option.checked %>type="checkbox" data-key="<%= option.key %>" />' +
              '<label class="checkbox" for="<%= self.name + "_" + option.key %>"><%= option.label %></label>' +
            '</div>' +
          '<% }); %>' +
        '</fieldset>'
      ),

      render: function() {
        var model = this.get('model');
        model.set('labelName', translationMap["in"] + " " + translationMap.fields[model.get('name')]);

        var container = this.get('container');

        // mark selected checkboxes
        var selected = model.get('selected');
        model.get('options').forEach(function(option) {
          if(selected.indexOf(option.key) > -1) {
            option.checked = "checked ";
          }
        });

        container.setHTML(fieldFrameWrapper(this.template, model));
        this.set('select', $(container).find('select'));
        return this;
      },

      update: function(e) {
        var selected = [];
        this.get('container').all('input:checked').each(function(node) {
          selected.push(node.getData("key"));
        });
        this.get('model').set('selected', selected);
      },

      remove: function(e) {
        this.constructor.superclass.remove.call(this);
        this.get('model').destroy({'delete': true});
      }
    }),

    dateview: Y.Base.create('fieldDateView', Y.View, [], {
      containerTemplate: '<li>',

      events: {
        "input": {
          change: 'update'
        },
        ".remove-field": {
          click: 'remove'
        }
      },

      fields: [
        {model: 'dateFrom', input: 'filter-date-from'},
        {model: 'dateUntil', input: 'filter-date-until'}
      ],

      template: Y.Template.Micro.compile(
        '<fieldset>' +
          '<div class="flex-container">' +
            '<legend class="offscreen"><%= this.labelName %></legend>' +
            '<div class="date-from-container date-container">' +
              '<label for="filter-date-from" class="date-label"><%= this.translation.from %>:</label>' +
              '<input type="text" id="filter-date-from" class="filter-date-from" value="<%= this.dateFrom %>"' +
              ' title="<%= this.translation.format %>" placeholder="<%= this.translation.placeholder %>"/>' +
            '</div>' +
            '<div class="date-until-container date-container">' +
              '<label for="filter-date-until" class="date-label"><%= this.translation.until %>:</label>' +
              '<input type="text" id="filter-date-until" class="filter-date-until" value="<%= this.dateUntil %>"' +
              ' title="<%= this.translation.format %>" placeholder="<%= this.translation.placeholder %>"/>' +
            '</div>' +
          '</div>' +
        '</fieldset>'
      ),

      render: function() {
        var self = this;
        var model = this.get('model');
        model.set('translation', translationMap.date);
        model.set('labelName', translationMap["in"] + " " + translationMap.fields[model.get('name')]);

        var container = this.get('container');
        container.setHTML(fieldFrameWrapper(this.template, model));

        this.fields.forEach(function(inputfield) {
          var field = container.one('.'+inputfield.input);
          self.set(inputfield.input, field);

          new Y.DatePicker({
            trigger: '.'+inputfield.input,
            mask: Y.DateFormat,
            popover: {
              zIndex: 1
            },
            on: {
              selectionChange: function(event) {
                model.set(inputfield.model, Y.Date.format(event.newSelection[0], {format: Y.DateFormat}));
                $('#'+inputfield.input+'-checkbox').prop('checked', true);
              }
            }
          });
        });

        return this;
      },

      update: function(e) {
        var self = this;
        var model = this.get('model');
        this.fields.forEach(function(inputfield) {
          model.set(inputfield.model, self.get(inputfield.input).val());
        });
      },

      remove: function(e) {
        this.constructor.superclass.remove.call(this);
        this.get('model').destroy({'delete': true});
      }
    })
  };

  var fieldList = new Y.FieldListModel();
  var fieldListView = new Y.FieldListView({modelList: fieldList});

  // Parse existing parameters into Form
  var initializeForm = function(param) {
    // simple textfields
    ['title', 'publisher', 'maintainer', 'notes'].forEach(function(item) {
      if (param.activeFilters.hasOwnProperty(item)) {
        fieldList.add(Y.merge(fieldTypes[item], {
          value: param.activeFilters[item][0]
        }));
      }
    });

    // query search
    if(param.query !== null) {
      fieldList.add(Y.merge(fieldTypes.query, {
        value: param.query
      }));
    }

    // Dropdown-Fields / Multiselect (same Model-Structure)
    ['groups', 'type', 'licence', 'sourceportal', 'openness', 'format'].forEach(function(item) {
      if (param.activeFilters.hasOwnProperty(item)) {
        fieldList.add(Y.merge(fieldTypes[item], {
          selected: param.activeFilters[item]
        }));
      }
    });

    // temporal_coverage
    if (param.dateFrom !== null || param.dateUntil !== null) {
      var mergeArray = {};
      ['dateFrom', 'dateUntil'].forEach(function(item) {
        if(param[item] !== null) {
          mergeArray[item] = Y.Date.format(new Date(param[item]), {format: Y.DateFormat});
        }
      });
      fieldList.add(Y.merge(fieldTypes.temporal_coverage, mergeArray));
    }

    // tags
    if (param.activeFilters.hasOwnProperty('tags')) {
      fieldList.add(Y.merge(fieldTypes.tags, {
        value: param.activeFilters.tags.join(', ')
      }));
    }

    // special hidden f-type filter: organizationFilter
    Y.organizationFilterActive = param.activeFilters.hasOwnProperty('onlyEditorMetadata');

    // at the end: Update buttons
    fieldListView.updateButtons();
  };

  // handle form actions
  $('.searchext-container form').submit(function(e) {
    var data = fieldList.getFieldData();

    // add special hidden f-type filter: organizationFilter
    if(Y.organizationFilterActive) {
      data.f = (data.f ? data.f : '') + 'onlyEditorMetadata:onlyEditorMetadata,';
    }

    for(var hiddenField in data) {
      $('.searchext-container input[name="' + hiddenField +'"]').val(data[hiddenField]);
    }
  });


  // Add Fields Button {{{
  var dropdownContainer = $("#addFieldDropdown");
  var initializeAddFieldButton = function(itemContainer) {
    var handler = function(e) {
      var localFieldTypes = fieldTypes; // we need a local copy, because the original will be out of context later
      fieldList.add(localFieldTypes[$(e.target).data('type')]);
      $(document).foundation('dropdown', 'close', dropdownContainer);

      // set focus to the first input of the last added field
      $('#fieldlist li:last-child input, #fieldlist li:last-child select').first().focus();
    };

    for(var fieldname in fieldTypes) {
      var item = $('<button/>', {
        type: "button",
        "data-type": fieldname,
        text: translationMap.fields[fieldTypes[fieldname].name]
      }).click(handler);

      $('<li/>', {
        'class': fieldname
      }).append(item).appendTo(itemContainer);
    }

    // open the dropdown if one of its buttons is focussed
    itemContainer.find('button').focus(function(e) {
      Foundation.libs.dropdown.open($('#addFieldDropdown'), $('#addfields'));
    });

    // close the dropdown when the last button has lost focus
    itemContainer.find('button').last().blur(function(e) {
      Foundation.libs.dropdown.close($('#addFieldDropdown'), $('#addfields'));
    });
  };
  // }}} addFieldDropdown

  // Clear Fields Button {{{
  $('#resetfields').click(function() {
    if(confirm(translationMap.resetfieldsconfirm)) {
      fieldList.reset();
    }
  });
  // }}} Clear Fields Button

  initializeAddFieldButton(dropdownContainer);
  initializeForm(searchParameters);

});
