'use strict';

var Geocoder = require('leaflet-control-geocoder');
var LRM = require('leaflet-routing-machine');
var locate = require('leaflet.locatecontrol');
var options = require('./lrm_options');
var links = require('./links');
var leafletOptions = require('./leaflet_options');
var ls = require('local-storage');
var profile = require('./profile');
var state = require('./state');
var localization = require('./localization');
var yandex = require('./yandex');

var parsedOptions = links.parse(window.location.search.slice(1));

var mergedOptions = L.extend(leafletOptions.defaultState, parsedOptions);
var local = localization.get(mergedOptions.language);

L.Routing.Localization['ru'] = {
  directions: {
    N: 'сервер',
    NE: 'северо-восток',
    E: 'восток',
    SE: 'юго-восток',
    S: 'юг',
    SW: 'юго-запад',
    W: 'запад',
    NW: 'северо-запад'
  },
  instructions: {
    // instruction, postfix if the road is named
    'Head': ['Направляйтесь к {dir}', ' по {road}'],
    'Continue': ['Продолжайте движение к {dir}', ' по {road}'],
    'SlightRight': ['Правее', ' на {road}'],
    'Right': ['Направо', ' на {road}'],
    'SharpRight': ['Круто направо', ' на {road}'],
    'TurnAround': ['Развернитесь'],
    'SharpLeft': ['Круто налево', ' на {road}'],
    'Left': ['Налево', ' на {road}'],
    'SlightLeft': ['Левее', ' на {road}'],
    'WaypointReached': ['Пришли'],
    'Roundabout': ['Take the {exitStr} exit in the roundabout', ' onto {road}'],
    'DestinationReached': ['Пришли']
  },
  formatOrder: function(n) {
    if (n == 12)
      return n + 'ый';
    var i = n % 10 - 1,
        suffix = ['ый', 'ой', 'ий', 'ый', 'ый', 'ой', 'ой', 'ой', 'ый'];
    return suffix[i] ? n + suffix[i] : n + 'ый';
  },
  ui: {
    startPlaceholder: 'Начало',
    viaPlaceholder: 'Через {viaNumber}',
    endPlaceholder: 'Конец'
  }
};

var mapLayer = leafletOptions.layer;
var baseURL = window.location.href.split('?')[0];
var layers = mapLayer[0][baseURL.indexOf('sight') >= 0 ? 'openstreetmap.org' : 'Mapbox Streets'];
var map = L.map('map', {
  zoomControl: false,
  dragging: true,
  layers: layers,
  maxZoom: 18
}).setView(mergedOptions.center, mergedOptions.zoom);

L.control.zoom({
  zoomInTitle: 'Увеличить',
  zoomOutTitle: 'Уменьшить'
}).addTo(map);

// Pass basemap layers
mapLayer = mapLayer.reduce(function(title, layer) {
  title[layer.label] = L.tileLayer(layer.tileLayer, {
    id: layer.label
  });
  return title;
});

L.control.scale({imperial: false}).addTo(map);

/* OSRM setup */
var ReversablePlan = L.Routing.Plan.extend({
  createGeocoders: function() {
    var container = L.Routing.Plan.prototype.createGeocoders.call(this);
    return container;
  }
});

/* Setup markers */
function makeIcon(i, n) {
  var url = 'images/marker-via-icon-2x.png';
  var markerList = ['images/marker-start-icon-2x.png', 'images/marker-end-icon-2x.png'];
  if (i === 0) {
    return L.icon({
      iconUrl: markerList[0],
      iconSize: [20, 56],
      iconAnchor: [10, 28]
    });
  }
  if (i === n - 1) {
    return L.icon({
      iconUrl: markerList[1],
      iconSize: [20, 56],
      iconAnchor: [10, 28]
    });
  } else {
    return L.icon({
      iconUrl: url,
      iconSize: [20, 56],
      iconAnchor: [10, 28]
    });
  }
}

var plan = new ReversablePlan([], {
  // geocoder: yandex.factory(),
  geocoder: Geocoder.nominatim({
    reverseQueryParams: {'accept-language': 'ru'}
  }),
  routeWhileDragging: true,
  createMarker: function(i, wp, n) {
    var options = {
      draggable: this.draggableWaypoints,
      icon: makeIcon(i, n)
    };
    var marker = L.marker(wp.latLng, options);
    marker.on('click', function() {
      plan.spliceWaypoints(i, 1);
    });
    return marker;
  },
  routeDragInterval: options.lrm.routeDragInterval,
  addWaypoints: true,
  waypointMode: 'snap',
  position: 'topright',
  useZoomParameter: options.lrm.useZoomParameter,
  reverseWaypoints: true,
  dragStyles: options.lrm.dragStyles,
  geocodersClassName: options.lrm.geocodersClassName,
  geocoderPlaceholder: function(i, n) {
    var startend = [local['Start - press enter to drop marker'], local['End - press enter to drop marker']];
    var via = [local['Via point - press enter to drop marker']];
    if (i === 0) {
      return startend[0];
    }
    if (i === (n - 1)) {
      return startend[1];
    } else {
      return via;
    }
  }
});

// add marker labels
var lrmControl = L.Routing.control({
  plan: plan,
  routeWhileDragging: options.lrm.routeWhileDragging,
  lineOptions: options.lrm.lineOptions,
  altLineOptions: options.lrm.altLineOptions,
  summaryTemplate: options.lrm.summaryTemplate,
  containerClassName: options.lrm.containerClassName,
  alternativeClassName: options.lrm.alternativeClassName,
  stepClassName: options.lrm.stepClassName,
  language: mergedOptions.language,
  showAlternatives: options.lrm.showAlternatives,
  units: mergedOptions.units,
  serviceUrl: leafletOptions.services[mergedOptions.profile].path,
  useZoomParameter: options.lrm.useZoomParameter,
  routeDragInterval: options.lrm.routeDragInterval
}).addTo(map);

var profControl = profile.control(mergedOptions.profile, leafletOptions.services, options.profile).addTo(map);
state(map, lrmControl, profControl, mergedOptions).update();

plan.on('waypointgeocoded', function(e) {
  if (plan._waypoints.filter(function(wp) { return !!wp.latLng; }).length < 2) {
    map.panTo(e.waypoint.latLng);
  }
});

// add onClick event
map.on('click', addWaypoint);
function addWaypoint(e) {
  var length = lrmControl.getWaypoints().filter(function(pnt) {
    return pnt.latLng;
  });
  length = length.length;
  if (!length) {
    lrmControl.spliceWaypoints(0, 1, e.latlng);
  } else {
    if (length === 1) length = length + 1;
    lrmControl.spliceWaypoints(length - 1, 1, e.latlng);
  }
}

function poly(lat, lng, h, w, title) {
  w /= 100000;
  h /= 100000;
  var p1 = new L.LatLng(lat - h / 2, lng - w / 2),
    p2 = new L.LatLng(lat + h / 2, lng - w / 2),
    p3 = new L.LatLng(lat + h / 2, lng + w / 2),
    p4 = new L.LatLng(lat - h / 2, lng + w / 2),
    polygonPoints = [p1, p2, p3, p4];
  var polygon = new L.Polygon(polygonPoints);
  polygon.on('mouseover', function(e) {
    var popup = L.popup({offset: new L.Point(0, -10), autoPan: false})
      .setLatLng(e.latlng)
      .setContent(title)
      .openOn(map);
  });
  polygon.on('mouseout', function(e) {
    map.closePopup();
  });
  map.addLayer(polygon);
  return polygon;
}

function createPassport(title, access) {
  return title + '<br><table class="disabled-table">' +
    '<tr><td>Все категории инвалидов и МГН</td><td>' + access.all + '</td></tr>' +
    '<tr><td>в том числе инвалиды:</td><td>&nbsp;</td></tr>' +
    '<tr><td>передвигающиеся на креслах-колясках</td><td>' + access.wheelchair + '</td></tr>' +
    '<tr><td>с нарушениями опорно-двигательного аппарата</td><td>' + access.mobility + '</td></tr>' +
    '<tr><td>с нарушениями зрения</td><td>' + access.sight + '</td></tr>' +
    '<tr><td>с нарушениями слуха</td><td>' + access.hearing + '</td></tr>' +
    '<tr><td>с нарушениями умственного развития</td><td>' + access.mental + '</td></tr>' +
    '</table>';
}
poly(
  55.887407, 37.4525, 100, 50,
  createPassport('МБУЗ «Химкинская Центральная городская поликлиника», 1-е поликлиническое отделение', {
    all: 'А', wheelchair: 'А', mobility: 'А', sight: 'А', hearing: 'А', mental: 'А'
  })
);

// User selected routes
lrmControl.on('alternateChosen', function(e) {
  var directions = document.querySelectorAll('.leaflet-routing-alt');
  if (directions[0].style.display != 'none') {
    directions[0].style.display = 'none';
    directions[1].style.display = 'block';
  } else {
    directions[0].style.display = 'block';
    directions[1].style.display = 'none';
  }
});

L.control.locate({
  follow: false,
  setView: true,
  remainActive: false,
  keepCurrentZoomLevel: true,
  stopFollowingOnDrag: false,
  onLocationError: function(err) {
    alert(err.message)
  },
  onLocationOutsideMapBounds: function(context) {
    alert(context.options.strings.outsideMapBoundsMsg);
  },
  showPopup: false,
  locateOptions: {},
  strings: {
    title: 'Показать текущее местоположение'
  }
}).addTo(map);
