'use strict';

var L = require('leaflet'),
    Util = require('leaflet-control-geocoder/src/util.js');

L.MyNominatim = L.Class.extend({
    options: {
        serviceUrl: 'https://nominatim.openstreetmap.org/',
        geocodingQueryParams: {},
        reverseQueryParams: {}
    },

    initialize: function (options) {
        L.Util.setOptions(this, options);
    },

    geocode: function (query, cb, context) {
        Util.jsonp(this.options.serviceUrl + 'search', L.extend({
                q: query,
                limit: 5,
                format: 'json',
                addressdetails: 1
            }, this.options.geocodingQueryParams),
            function (data) {
                var includeSuburb = false, suburbs = {};
                for (var i = data.length - 1; i >= 0; i--) {
                    var addr = data[i].address;
                    if (addr.city && addr.road && addr.house_number) {
                        var name = addr.city + '|' + addr.road + '|' + addr.house_number;
                        if (name in suburbs) {
                            var suburb = suburbs[name];
                            if (suburb !== addr.suburb) {
                                includeSuburb = true;
                                break;
                            }
                        } else {
                            suburbs[name] = addr.suburb;
                        }
                    }
                }
                var results = [];
                for (var i = data.length - 1; i >= 0; i--) {
                    var bbox = data[i].boundingbox;
                    for (var j = 0; j < 4; j++) bbox[j] = parseFloat(bbox[j]);
                    var addr = data[i].address;
                    var name;
                    if (addr.city && addr.road && addr.house_number) {
                        name = addr.road + ', ' + addr.house_number;
                        if (includeSuburb && addr.suburb) {
                            name += ', ' + addr.suburb;
                        }
                        if (addr.city !== 'Химки') {
                            name += ', ' + addr.city;
                        }
                    } else {
                        name = data[i].display_name;
                    }
                    results[i] = {
                        icon: data[i].icon,
                        name: name,
                        bbox: L.latLngBounds([bbox[0], bbox[2]], [bbox[1], bbox[3]]),
                        center: L.latLng(data[i].lat, data[i].lon),
                        properties: data[i]
                    };
                }
                cb.call(context, results);
            }, this, 'json_callback');
    },

    reverse: function (location, scale, cb, context) {
        Util.jsonp(this.options.serviceUrl + 'reverse', L.extend({
            lat: location.lat,
            lon: location.lng,
            zoom: Math.round(Math.log(scale / 256) / Math.log(2)),
            addressdetails: 1,
            format: 'json'
        }, this.options.reverseQueryParams), function (data) {
            var result = [],
                loc;

            if (data && data.lat && data.lon) {
                loc = L.latLng(data.lat, data.lon);
                var addr = data.address;
                var name;
                if (addr.city && addr.road && addr.house_number) {
                    name = addr.road + ', ' + addr.house_number;
                    if (addr.suburb) {
                        name += ', ' + addr.suburb;
                    }
                    if (addr.city !== 'Химки') {
                        name += ', ' + addr.city;
                    }
                } else {
                    name = data.display_name;
                }
                result.push({
                    name: name,
                    center: loc,
                    bounds: L.latLngBounds(loc, loc),
                    properties: data
                });
            }

            cb.call(context, result);
        }, this, 'json_callback');
    }
});

module.exports = {
    class: L.MyNominatim,

    factory: function (options) {
        return new L.MyNominatim(options);
    }
};
