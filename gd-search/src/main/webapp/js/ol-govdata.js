function initMap() {
  /*var projection = ol.proj.get('EPSG:3857');
  //var projection = ol.proj.get('EPSG:25832');
  var projectionExtent = projection.getExtent();
  var size = ol.extent.getWidth(projectionExtent) / 256;
  var resolutions = new Array(14);
  var matrixIds = new Array(14);
  for (var z = 0; z < 14; ++z) {
    // generate resolutions and matrixIds arrays for this WMTS
    resolutions[z] = size / Math.pow(2, z);
    matrixIds[z] = z;
  }*/

  // fix skipped coordinates for rendering the box - I unoptimized the method.
  ol.render.canvas.Replay.prototype.appendFlatCoordinates =
    function(flatCoordinates, offset, end, stride, close) {

    var myEnd = this.coordinates.length;
    var extent = this.getBufferedMaxExtent();
    var lastCoord = [flatCoordinates[offset], flatCoordinates[offset + 1]];
    var nextCoord = [NaN, NaN];
    var skipped = true;

    var i, lastRel, nextRel;
    for (i = offset + stride; i < end; i += stride) {
      nextCoord[0] = flatCoordinates[i];
      nextCoord[1] = flatCoordinates[i + 1];
      if (skipped) {
        this.coordinates[myEnd++] = lastCoord[0];
        this.coordinates[myEnd++] = lastCoord[1];
      }
      this.coordinates[myEnd++] = nextCoord[0];
      this.coordinates[myEnd++] = nextCoord[1];
      skipped = false;
      lastCoord[0] = nextCoord[0];
      lastCoord[1] = nextCoord[1];
      lastRel = nextRel;
    }

    // handle case where there is only one point to append
    if (i === offset + stride) {
      this.coordinates[myEnd++] = lastCoord[0];
      this.coordinates[myEnd++] = lastCoord[1];
    }

    if (close) {
      this.coordinates[myEnd++] = flatCoordinates[offset];
      this.coordinates[myEnd++] = flatCoordinates[offset + 1];
    }
    return myEnd;
  };

  var styles = [
    /* We are using two different styles for the polygons:
     *  - The first style is for the polygons themselves.
     *  - The second style is to draw the vertices of the polygons.
     *    In a custom `geometry` function the vertices of a polygon are
     *    returned as `MultiPoint` geometry, which will be used to render
     *    the style.
     */
    new ol.style.Style({
      stroke: new ol.style.Stroke({
        color: 'rgba(0, 0, 0, 0.3)',
        width: 2
      }),
      fill: new ol.style.Fill({
        color: 'rgba(0, 0, 0, 0.1)'
      })
    }),
    new ol.style.Style({
      image: new ol.style.Circle({
        radius: 8,
        fill: new ol.style.Fill({
          color: '#3d3d3d'
        })
      }),
      geometry: function(feature) {
        // return the coordinates of the first ring of the polygon
        var coordinates = feature.getGeometry().getCoordinates()[0];
        return new ol.geom.MultiPoint(coordinates);
      }
    })
  ];

  var boundingboxfield = document.getElementById('boundingbox') ? document.getElementById('boundingbox').value : "";
  var bboxpredefined = false;
  var box;
  if(boundingboxfield.length > 0) {
    // there is already a boundingbox, parse it and use it
    var bboxcoords = boundingboxfield.split(',');
    if(bboxcoords.length == 4) {
      box = new ol.geom.Polygon([
        [
          [parseFloat(bboxcoords[0]), parseFloat(bboxcoords[3])], // links oben
          [parseFloat(bboxcoords[2]), parseFloat(bboxcoords[3])], // rechts oben
          [parseFloat(bboxcoords[2]), parseFloat(bboxcoords[1])], // rechts unten
          [parseFloat(bboxcoords[0]), parseFloat(bboxcoords[1])],   // links unten
        ]
      ]);
      bboxpredefined = true;
    }
  }

  if(!bboxpredefined) {
    // standard box
    box = new ol.geom.Polygon([
      [
        [7.234, 50.913], // links oben 50.913/7.234
        [11.931, 50.913], // rechts oben 50.913 11.931
        [11.931, 48.879], // rechts unten 48.879/11.931
        [7.234, 48.879],   // links unten 48.879 7.234
      ]
    ]);
  }

  box.transform('EPSG:4326', 'EPSG:3857'); // WGS 84 -> Mercator
  var feature = new ol.Feature(box);
  var features = new ol.Collection([feature]);

  var vectorSource = new ol.source.Vector({
    features: features
  });

  var vectorLayer = new ol.layer.Vector({
    source: vectorSource,
    style: styles
  });

  // --- modify box
  var modify = new ol.interaction.Modify({
    features: features,
    pixelTolerance: 30,
      style: new ol.style.Style({
        image: new ol.style.Circle({
          radius: 8,
          fill: new ol.style.Fill({
            color: '#FFFFFF'
          }),
          stroke: new ol.style.Stroke({
            color: '#3d3d3d',
            width: 2
          })
        })
      })
    });

  modify.insertVertex_ = function(a, b) {}; // don't allow inserting vertices
  modify.createOrUpdateVertexFeature_ = function(coordinates) { // override hover to only show allowed actions
    // check if the coordinates / vertexFeature point to a corner of our box
    var nodes = box.getCoordinates()[0];
    var goodcoordinate = false;
    for(var i=0; i < nodes.length; i++) {
      if (ol.coordinate.equals(nodes[i], coordinates)) {
        goodcoordinate = true;
      }
    }
    if(!goodcoordinate) {
      return;
    }

    // below: original function
    var vertexFeature = modify.vertexFeature_;
    if (!vertexFeature) {
      vertexFeature = new ol.Feature(new ol.geom.Point(coordinates));
      modify.vertexFeature_ = vertexFeature;
      modify.overlay_.getSource().addFeature(vertexFeature);
    } else {
      var geometry = /** @type {ol.geom.Point} */ (vertexFeature.getGeometry());
      geometry.setCoordinates(coordinates);
    }
    return vertexFeature;
  };
  modify.handleDragEvent_ = function(evt) { // override dragEvent to keep box shape
    modify.ignoreNextSingleClick_ = false;
    modify.willModifyFeatures_(evt);

    var vertex = evt.coordinate;
    for (var i = 0, ii = modify.dragSegments_.length; i < ii; ++i) {
      var dragSegment = modify.dragSegments_[i];
      var segmentData = dragSegment[0];
      var depth = segmentData.depth;
      var geometry = segmentData.geometry;
      var coordinates = geometry.getCoordinates();
      var segment = segmentData.segment;
      var index = dragSegment[1];

      while (vertex.length < geometry.getStride()) {
        vertex.push(0);
      }

      // modify dragged vertex
      coordinates[depth[0]][segmentData.index + index] = vertex;
      segment[index] = vertex;

      // modify connected vertices
      switch (segmentData.index + index) {
        case 0: // links oben
          coordinates[0][3][0] = vertex[0]; // x
          coordinates[0][1][1] = vertex[1]; // y
          break;

        case 1: // rechts oben
          coordinates[0][0][1] = vertex[1]; // y
          coordinates[0][2][0] = vertex[0]; // x
          break;

        case 2: // rechts unten
          coordinates[0][1][0] = vertex[0]; // x
          coordinates[0][3][1] = vertex[1]; // y
          break;

        case 3: // links unten
          coordinates[0][2][1] = vertex[1]; // y
          coordinates[0][0][0] = vertex[0]; // x
          break;
      }

      modify.setGeometryCoordinates_(geometry, coordinates);
    }
    modify.createOrUpdateVertexFeature_(vertex);
  };
  modify.handleUpEvent_ = function(evt) { // overwrite to modify function
    var segmentData;
    for (var i = modify.dragSegments_.length - 1; i >= 0; --i) {
      segmentData = modify.dragSegments_[i][0];
      modify.rBush_.update(ol.extent.boundingExtent(segmentData.segment),
          segmentData);
    }
    if (modify.modified_) {
      modify.dispatchEvent(new ol.interaction.ModifyEvent(
          ol.ModifyEventType.MODIFYEND, this.features_, evt));
      modify.modified_ = false;
    }

    // modification: tell the cache to update by emitting change-event!
    feature.changed();

    return false;
  };

  var centerMapOnBox = function() {
    // fit the view with 100px padding around the selection box
    map.getView().fit(feature.getGeometry(), map.getSize(), {'padding': [100,100,100,100]});
  };

  var centerSelection = function() { // center and resize select-box fitting the current view
    var factorX = 0.3;
    var factorY = 0.3;

    var extent = map.getView().calculateExtent(map.getSize());
    var center = ol.extent.getCenter(extent);
    var size = ol.extent.getSize(extent);
    var scaledSize = [(size[0]/2) * factorX, (size[1]/2) * factorY];

    var topleft = [center[0] - scaledSize[0], center[1] - scaledSize[1]];
    var bottomright = [center[0] + scaledSize[0], center[1] + scaledSize[1]];

    feature.getGeometry().setCoordinates([
      [
        [topleft[0], bottomright[1]], // rechts oben 50.913 11.931
        bottomright, // rechts unten 48.879/11.931
        [bottomright[0], topleft[1]],   // links unten 48.879 7.234
        topleft, // links oben 50.913/7.234
      ]
    ]);

    feature.changed();
  };

  var centerSelectionControl = function(opt_options) {
    var options = opt_options || {};

    var button = document.createElement('button');
    button.type = 'button'; // prevent form submit
    button.innerHTML = '<i class="icon-map-marker"></i>';
    button.title = 'Auswahl-Rechteck zurücksetzen und auf Karte neu positionieren';
    button.addEventListener('click', centerSelection, false);
    button.addEventListener('touchstart', centerSelection, false);

    var element = document.createElement('div');
    element.className = 'center-selection-control ol-unselectable ol-control';
    element.appendChild(button);

    ol.control.Control.call(this, {
      element: element,
      target: options.target
    });
  };
  ol.inherits(centerSelectionControl, ol.control.Control);

  // get config for tile layer
  mapconfig = document.getElementById("map").dataset;
  
  var tilelayer;
  if(mapconfig.useOsm === "true") {
    // use OSM
    tilelayer = new ol.layer.Tile({
      source: new ol.source.OSM()
    });
  } else {
    // display credits if available
    var creditArray = [];
    if(mapconfig.credits && mapconfig.credits != "") {
      creditArray =  [new ol.Attribution({html: mapconfig.credits})];
    }
    
    // use custom Tileserver
    tilelayer = new ol.layer.Tile({
      source: new ol.source.TileWMS({
        url: mapconfig.tileUrl,
        params: {
          'layers': mapconfig.layers,
        },
        serverType: 'geoserver',
        attributions: creditArray,
      })
    });
  }
  
  var map = new ol.Map({
    layers: [
      tilelayer,
      vectorLayer
    ],
    target: 'map',
    controls: ol.control.defaults({
      attributionOptions: /** @type {olx.control.AttributionOptions} */ ({
        collapsible: false
      })
    }),
    view: new ol.View({
      center: ol.proj.fromLonLat([9, 51]),
      zoom: 6,
      minZoom: 5,
      maxZoom: 20
    })
  });

  map.addInteraction(modify);
  map.addControl(new centerSelectionControl());

  // initialize boundingbox (depends on screen aspect ration, so could not be done statically)
  var mapIsInitialized = false;
  map.once('postrender', function() { // wait for map to be mapIsInitialized
    mapIsInitialized = true;

    if(bboxpredefined) {
      centerMapOnBox(); // we already have a predefined box
    } else {
      centerSelection(); // we start with default values, resize box to fit map
    }
  });

  // initialize geolocation
  if(!bboxpredefined) { // no location service when we already have a box given
    var geolocation = new ol.Geolocation({
      projection: map.getView().getProjection(),
      tracking: true
    });
    geolocation.once('change', function(evt) {
      var centerOnCurentLocation = function() {
        map.getView().setCenter(geolocation.getPosition());
        map.getView().setZoom(11);
        centerSelection();
      };

      if(mapIsInitialized) { // make sure the map is initialized before calling centerselection
        centerOnCurentLocation();
      } else {
        map.once('postrender', function() { // wait for map to be initialized
          centerOnCurentLocation();
        });
      }
    });
  }

  // initialize boundingbox watcher for form
  feature.on('change', function() {
    var extent = feature.getGeometry().getExtent();
    var boundingbox = ol.proj.transformExtent(extent, 'EPSG:3857', 'EPSG:4326');
    document.getElementById('boundingbox').value = boundingbox;
  });


  // add geolocation search
  AUI({
    lang: 'de'
  }).use('autocomplete', 'datasource-get', function (Y) {
    /*
     * Format: bbox = [lon1, lat1, lon2, lat2] 
     */
    function transformBBox(bbox) {
      var newbox = new ol.geom.Polygon([
        [
          [bbox[0], bbox[3]],
          [bbox[2], bbox[3]],
          [bbox[2], bbox[1]],
          [bbox[0], bbox[1]]
        ]
      ]);
      newbox.transform('EPSG:4326', 'EPSG:3857'); // WGS 84 -> Mercator

      // if the bbox is too tiny, enlarge it
      if(newbox.getArea() < 10000) {
        var r = 0.0003;
        newbox = new ol.geom.Polygon([
              [
                [bbox[0] -r, bbox[3] +r],
                [bbox[2] +r, bbox[3] +r],
                [bbox[2] +r, bbox[1] -r],
                [bbox[0] -r, bbox[1] -r]
              ]
            ]);
        newbox.transform('EPSG:4326', 'EPSG:3857'); // WGS 84 -> Mercator
      };
      
      return newbox;
    }
    
    var mapadapter;
    if(mapconfig.useOsm === "true") {
      mapadapter = {
          prepareBBox: function(item) {
            // parameters are in other order
            return [parseFloat(item.boundingbox[2]),
                    parseFloat(item.boundingbox[0]),
                    parseFloat(item.boundingbox[3]),
                    parseFloat(item.boundingbox[1])];
          },
          
          resultListLocator: function (response) {
            return response;
          },
          
          resultFormatter: function(query, results) {
            return results.map(function(item) {
              return "<strong>" + item.raw.display_name + "</strong> <span>" + item.raw.type + "</span>";
            });
          },
          
          resultTextLocator: "display_name"
      }
    } else {
      mapadapter = {
          prepareBBox: function(item) {
            return item.bbox;
          },
          
          resultListLocator: function (response) {
            if(response.hasOwnProperty('features')) {
              return response.features;
            }
            return [];
          },
          
          resultFormatter: function(query, results) {
            return results.map(function(item) {
              return "<strong>" + item.raw.properties.text + "</strong> <span>" + item.raw.properties.typ + "</span>";
            });
          },
          
          resultTextLocator: "properties.text"
      }
    }
    
    Y.one('#locationsearchinput').plug(Y.Plugin.AutoComplete, {
      source: mapconfig.geocodingUrl,
      // Action on selecting an item
      on: {
        select: function(e) {
          var bbox = mapadapter.prepareBBox(e.result.raw);
          var newbox = transformBBox(bbox);

          feature.getGeometry().setCoordinates(newbox.getCoordinates());
          feature.changed();

          map.getView().fit(feature.getGeometry(), map.getSize(), {'padding': [100,100,100,100]});
        }
      },

      // Only consume actual list of features
      resultListLocator: mapadapter.resultListLocator,

      // Display results nicely
      resultFormatter: mapadapter.resultFormatter,

      // Find Display Text
      resultTextLocator: mapadapter.resultTextLocator
    });
  });
}

window.addEventListener('load', initMap);
