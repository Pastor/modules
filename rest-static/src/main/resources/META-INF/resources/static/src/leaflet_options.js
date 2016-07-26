'use strict';

var streets = L.tileLayer('https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}@2x.png?access_token=pk.eyJ1IjoibXNsZWUiLCJhIjoiclpiTWV5SSJ9.P_h8r37vD8jpIH1A6i1VRg', {
    attribution: '<a href="https://www.mapbox.com/about/maps">© Mapbox</a> <a href="http://openstreetmap.org/copyright">© OpenStreetMap</a> | <a href="http://mapbox.com/map-feedback/">Improve this map</a>'
  }),
  outdoors = L.tileLayer('https://api.tiles.mapbox.com/v4/mapbox.outdoors/{z}/{x}/{y}@2x.png?access_token=pk.eyJ1IjoibXNsZWUiLCJhIjoiclpiTWV5SSJ9.P_h8r37vD8jpIH1A6i1VRg', {
    attribution: '<a href="https://www.mapbox.com/about/maps">© Mapbox</a> <a href="http://openstreetmap.org/copyright">© OpenStreetMap</a> | <a href="http://mapbox.com/map-feedback/">Improve this map</a>'
  }),
  satellite = L.tileLayer('https://api.tiles.mapbox.com/v4/mapbox.streets-satellite/{z}/{x}/{y}@2x.png?access_token=pk.eyJ1IjoibXNsZWUiLCJhIjoiclpiTWV5SSJ9.P_h8r37vD8jpIH1A6i1VRg', {
    attribution: '<a href="https://www.mapbox.com/about/maps">© Mapbox</a> <a href="http://openstreetmap.org/copyright">© OpenStreetMap</a> | <a href="http://mapbox.com/map-feedback/">Improve this map</a>'
  }),
  osm = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '© <a href="http://www.openstreetmap.org/copyright/en">OpenStreetMap</a> contributors'
  });

module.exports = {
  defaultState: {
    center: L.latLng(55.889167, 37.445),
    zoom: 15,
    waypoints: [],
    language: 'ru',
    profile: 'bus',
    alternative: 0,
    layer: streets
  },
  services: {
    bus: {
      label: 'Для низкопольного общественного транспорта',
      path: 'http://52.201.214.44:5000/route/v1'
      // path: 'https://router.project-osrm.org/route/v1'
    },
    peop: {
      label: 'Для пешеходов',
      path: 'http://52.201.214.44:5000/route/v1'
      // path: 'https://router.project-osrm.org/route/v1'
    },
    wheelchair: {
      label: 'Для передвижения на инвалидной коляске',
      path: 'http://52.201.214.44:5000/route/v1'
      // path: 'https://router.project-osrm.org/route/v1'
    }
  },
  layer: [{
    'Mapbox Streets': streets,
    'Mapbox Outdoors': outdoors,
    'Mapbox Streets Satellite': satellite,
    'openstreetmap.org': osm
  }],
  baselayer: {
    one: streets,
    two: outdoors,
    three: satellite,
    four: osm
  }
};
