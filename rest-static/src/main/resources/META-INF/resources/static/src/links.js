'use strict';

var qs = require('qs');

function _formatCoord(latLng) {
    var precision = 6;
    if (!latLng) {
        return;
    }
    return latLng.lat.toFixed(precision) + "," + latLng.lng.toFixed(precision);
}

function _parseCoord(what, coordStr) {
    var lat, lon;
    if (coordStr) {
        var latLng = coordStr.split(',');
        lat = parseFloat(latLng[0]);
        lon = parseFloat(latLng[1]);
    } else {
        lat = Number.NaN;
        lon = Number.NaN;
    }
    if (isNaN(lat) || isNaN(lon)) {
        throw {
            name: 'InvalidCoords',
            message: '"' + coordStr + '" is not a valid coordinate for ' + what
        };
    }
    return L.latLng(lat, lon);
}

function _parseInteger(what, intStr) {
    var integer = parseInt(intStr, 10);
    if (isNaN(integer)) {
        throw {
            name: 'InvalidInt',
            message: '"' + intStr + '" is not a valid integer for ' + what
        };
    }
    return integer;
}

function formatLink(options) {
    return qs.stringify({
        z: options.zoom,
        center: options.center ? _formatCoord(options.center) : undefined,
        loc: options.waypoints ? options.waypoints.filter(function (wp) {
            return wp.latLng !== undefined;
        }).map(function (wp) {
            return wp.latLng;
        }).map(_formatCoord) : undefined,
        alt: options.alternative,
        prof: options.profile,
        sight: options.sight
    }, {indices: false});
}

function parseLink(link) {
    var q = qs.parse(link),
        parsedValues = {},
        options = {},
        k;
    try {
        parsedValues.zoom = _parseInteger('zoom', q.z);
        parsedValues.center = q.center && _parseCoord('center', q.center);
        var locs = q.loc ? [].concat(q.loc) : undefined;
        parsedValues.waypoints = locs && locs.filter(function (loc) {
                return loc != "";
            }).map(function (loc) {
                return _parseCoord("coord", loc);
            }).map(function (coord) {
                return L.Routing.waypoint(coord);
            });
        parsedValues.alternative = q.alt;
        parsedValues.profile = q.prof;
        parsedValues.sight = q.sight;
    } catch (e) {
        console.log("Exception " + e.name + ": " + e.message);
    }
    for (k in parsedValues) {
        if (parsedValues[k] !== undefined && parsedValues[k] !== "") {
            options[k] = parsedValues[k];
        }
    }
    return options;
}

module.exports = {
    'parse': parseLink,
    'format': formatLink
};
