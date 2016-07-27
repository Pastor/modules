'use strict';

var map = L.map('map', {
    layers: L.tileLayer('https://api.tiles.mapbox.com/v4/mapbox.streets/{z}/{x}/{y}@2x.png?access_token=pk.eyJ1IjoibXNsZWUiLCJhIjoiclpiTWV5SSJ9.P_h8r37vD8jpIH1A6i1VRg', {
        attribution: '<a href="https://www.mapbox.com/about/maps">© Mapbox</a> <a href="http://openstreetmap.org/copyright">© OpenStreetMap</a> | <a href="http://mapbox.com/map-feedback/">Improve this map</a>'
    }),
}).setView(L.latLng(55.889167, 37.445), 15);

var plan = new L.Routing.Plan([], {
    geocoder: L.Control.Geocoder.nominatim({
        reverseQueryParams: {'accept-language': 'ru'},
        geocodingQueryParams: {viewbox: '37.21,56.0,37.52,55.85', bounded: 1, 'accept-language': 'ru'}
    }),
    routeWhileDragging: true,
    routeDragInterval: 100,
    addWaypoints: true,
    position: 'topright',
    reverseWaypoints: true
});
L.Routing.control({
    plan: plan,
    routeWhileDragging: plan.options.routeWhileDragging,
    showAlternatives: true,
    serviceUrl: 'http://52.201.214.44:5000/route/v1',
    useZoomParameter: plan.options.useZoomParameter,
    routeDragInterval: plan.options.routeDragInterval
}).addTo(map);
map.on('click', function (e) {
    var length = lrmControl.getWaypoints().filter(function (pnt) {
        return pnt.latLng;
    }).length;
    if (!length) {
        lrmControl.spliceWaypoints(0, 1, e.latlng);
    } else {
        if (length === 1) length = length + 1;
        lrmControl.spliceWaypoints(length - 1, 1, e.latlng);
    }
});
