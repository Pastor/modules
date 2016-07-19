'use strict';

var L = require('leaflet'),
  Util = require('leaflet-control-geocoder/src/util.js');

L.Yandex = L.Class.extend({
  options: {
    service_url: 'https://geocode-maps.yandex.ru/1.x/'
  },

  initialize: function(accessToken, options) {
    L.setOptions(this, options);
    this._accessToken = accessToken;
  },

  geocode: function(query, cb, context) {
    var params = {
      geocode: query,
      format: 'json',
      ll: '37.445,55.889167',
      spn: '0.5,0.5',
      rspn: 1
    };

    if (this._key && this._key.length) {
      params.key = this._key;
    }

    Util.getJSON(this.options.service_url, params, function(data) {
      var results = [],
        loc,
        points,
        lowerCorner,
        upperCorner;

      var candidates = data.response.GeoObjectCollection.featureMember;
      if (candidates && candidates.length) {
        for (var i = 0; i < candidates.length; i++) {
          loc = candidates[i].GeoObject;
          points = loc.Point.pos.split(' ');
          lowerCorner = loc.boundedBy.Envelope.lowerCorner.split(' ');
          upperCorner = loc.boundedBy.Envelope.upperCorner.split(' ');
          results[i] = {
            name: loc.name,
            bbox: L.latLngBounds([lowerCorner[1], lowerCorner[0]], [upperCorner[1], upperCorner[0]]),
            center: L.latLng(points[1], points[0])
          };
        }
      }

      cb.call(context, results);
    });
  },

  suggest: function(query, cb, context) {
    return this.geocode(query, cb, context);
  },

  reverse: function(location, scale, cb, context) {
    var params = {
      geocode: encodeURIComponent(location.lng) + ',' + encodeURIComponent(location.lat),
      kind: 'house',
      spn: '0.002,0.002',
      results: 1,
      format: 'json'
    };

    if (this._key && this._key.length) {
      params.key = this._key;
    }
    console.log(this.options.service_url + L.Util.getParamString(params));

    Util.getJSON(this.options.service_url, params, function(data) {
      var result = [],
        loc,
        points,
        lowerCorner,
        upperCorner;

      var candidates = data.response.GeoObjectCollection.featureMember;
      if (candidates && candidates.length) {
        loc = candidates[0].GeoObject;
        points = loc.Point.pos.split(' ');
        lowerCorner = loc.boundedBy.Envelope.lowerCorner.split(' ');
        upperCorner = loc.boundedBy.Envelope.upperCorner.split(' ');
        result.push({
          name: loc.name,
          bounds: L.latLngBounds([lowerCorner[1], lowerCorner[0]], [upperCorner[1], upperCorner[0]]),
          center: L.latLng(points[1], points[0])
        });
      }

      cb.call(context, result);
    });
  }
});

module.exports = {
  class: L.Yandex,

  factory: function(accessToken, options) {
    return new L.Yandex(accessToken, options);
  }
};
