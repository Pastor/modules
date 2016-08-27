'use strict';

// todo: отображение объектов
// todo: определение текущего местоположения???
var links = require('./links');
require('leaflet');
var locate = require('leaflet.locatecontrol');
var myNominatim = require('./my_nominatim');
require('leaflet-routing-machine');
var Util = require('leaflet-control-geocoder/src/util.js');
require('sweetalert');

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
var allLayers = {
    'Mapbox Streets': streets,
    'Mapbox Outdoors': outdoors,
    'Mapbox Streets Satellite': satellite,
    'openstreetmap.org': osm
};
var defaultState = {
    zoom: 15,
    center: L.latLng(55.889167, 37.445),
    waypoints: [],
    alternative: 0,
    profile: 'foot',
    sight: 'eyes'
};
var services = {
    car: {
        path: 'http://176.112.215.104:5003/route/v1'
        //path: 'https://router.project-osrm.org/route/v1'
    },
    foot: {
        path: 'http://176.112.215.104:5001/route/v1'
        //path: 'http://52.201.214.44:5000/route/v1'
        // path: 'http://localhost:5000/route/v1'
        // path: 'https://router.project-osrm.org/route/v1'
    },
    bicycle: {
        path: 'http://176.112.215.104:5002/route/v1'
        //path: 'http://52.201.214.44:5000/route/v1'
        // path: 'http://localhost:5000/route/v1'
        // path: 'https://router.project-osrm.org/route/v1'
    },
    baby: {
        path: 'http://176.112.215.104:5002/route/v1'
        //path: 'http://52.201.214.44:5000/route/v1'
        // path: 'http://localhost:5000/route/v1'
        // path: 'https://router.project-osrm.org/route/v1'
    }
};
var sights = ['glasses', 'eyes'];

var queryOptions = links.parse(window.location.search.slice(1));
var hashOptions = links.parse(window.location.hash.slice(1));
var state = L.extend(defaultState, queryOptions, hashOptions);

var layers = allLayers[state.sight === 'glasses' ? 'openstreetmap.org' : 'Mapbox Streets'];
var map = L.map('map', {
    zoomControl: false,
    dragging: true,
    layers: layers,
    maxZoom: 25
}).setView(state.center, state.zoom);

L.control.zoom({
    zoomInText: '',
    zoomInTitle: 'Увеличить',
    zoomOutText: '',
    zoomOutTitle: 'Уменьшить',
    position: 'topright'
}).addTo(map);

L.control.scale({
    imperial: false,
    position: 'bottomright'
}).addTo(map);

function showError(message) {
    swal(message);
    // alert(message); // todo
}
var locate = L.control.locate({
    follow: false,
    setView: true,
    remainActive: false,
    keepCurrentZoomLevel: true,
    stopFollowingOnDrag: false,
    onLocationError: function (err) {
        console.error(err.message);
        showError('К сожалению, текущее местоположение недоступно');
    },
    onLocationOutsideMapBounds: function (context) {
        context.stop();
        showError('Вы находитесь вне границ доступной карты');
    },
    showPopup: false,
    locateOptions: {},
});
locate.addTo(map);
window.locateToCurrent = function () {
    var shouldStop = (locate._event === undefined || locate._map.getBounds().contains(locate._event.latlng) || !locate.options.setView || locate._isOutsideMapBounds());
    if (!locate.options.remainActive && (locate._active && shouldStop)) {
        locate.stop();
    } else {
        locate.start();
    }
}

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
        'SharpRight': ['Направо и назад', ' на {road}'],
        'TurnAround': ['Развернитесь'],
        'SharpLeft': ['Налево и назад', ' на {road}'],
        'Left': ['Налево', ' на {road}'],
        'SlightLeft': ['Левее', ' на {road}'],
        'WaypointReached': ['Пришли'],
        'Roundabout': ['На {exitStr} выезд в кольцевом перекрестке', ' на {road}'],
        'DestinationReached': ['Пришли']
    },
    formatOrder: function (n) {
        if (n == 12)
            return n + 'ый';
        var i = n % 10 - 1,
            suffix = ['ый', 'ой', 'ий', 'ый', 'ый', 'ой', 'ой', 'ой', 'ый'];
        return suffix[i] ? n + suffix[i] : n + 'ый';
    },
    ui: {
        startPlaceholder: 'Адрес отправления',
        viaPlaceholder: 'Через {viaNumber}',
        endPlaceholder: 'Адрес назначения'
    }
};
function makeIcon(i, n) {
    var icon;
    if (i === 0) {
        icon = 'marker-start-icon.png';
    } else if (i === n - 1) {
        icon = 'marker-end-icon.png';
    } else {
        icon = 'marker-via-icon.png';
    }
    return L.icon({
        iconUrl: 'images/' + icon,
        iconSize: [20, 56],
        iconAnchor: [10, 28]
    });
}
var ReversablePlan = L.Routing.Plan.extend({
    createGeocoders: function () {
        var container = L.DomUtil.create('div', '');

        this._geocoderContainer = container;
        this._geocoderElems = [];

        if (this.options.reverseWaypoints) {
            var reverseBtn = document.getElementById('reverseButton');
            L.DomEvent.addListener(reverseBtn, 'click', function () {
                this._waypoints.reverse();
                this.setWaypoints(this._waypoints);
            }, this);
        }

        this._updateGeocoders();
        this.on('waypointsspliced', this._updateGeocoders);

        return container;
    }
});
var plan = new ReversablePlan(state.waypoints, {
    // geocoder: yandex.factory(),
    geocoder: myNominatim.factory({
        reverseQueryParams: {'accept-language': 'ru'},
        geocodingQueryParams: {viewbox: '37.21,56.0,37.52,55.85', bounded: 1, 'accept-language': 'ru'}
    }),
    routeWhileDragging: true,
    createMarker: function (i, wp, n) {
        var options = {
            draggable: this.draggableWaypoints,
            icon: makeIcon(i, n)
        };
        var marker = L.marker(wp.latLng, options);
        marker.on('click', function () {
            plan.spliceWaypoints(i, 1);
        });
        return marker;
    },
    routeDragInterval: 100,
    addWaypoints: false,
    waypointMode: 'snap',
    position: 'topright',
    useZoomParameter: false,
    reverseWaypoints: true,
    dragStyles: [
        {color: 'black', opacity: 0.35, weight: 9},
        {color: 'white', opacity: 0.8, weight: 7}
    ],
    language: 'ru',
    autocompleteOptions: {noResultsMessage: 'Адрес не найден'},
    createGeocoder: function (i, wps, options) {
        var fakes, container, input, remove;
        fakes = L.DomUtil.create('div', 'fake');
        container = L.DomUtil.create('div', 'fake', fakes);
        remove = undefined; // todo: add clear buttons???
        if (i == 0) {
            input = document.getElementById('routeStart');
        } else {
            input = document.getElementById('routeEnd');
        }
        return {
            container: container,
            input: input,
            closeButton: remove
        };
    },
    geocoderClass: function () {
        return 'headerInput';
    }
});
var formatter = new L.Routing.Formatter({
    units: 'metric',
    language: plan.options.language,
    unitNames: {
        meters: 'м',
        kilometers: 'км',
        yards: 'yd',
        miles: 'mi',
        hours: 'ч',
        minutes: 'мин',
        seconds: 'сек'
    },
});
formatter.formatTime = function (t) {
    // More than 30 seconds precision looks ridiculous
    t = Math.round(t / 30) * 30;
    var u = this.options.unitNames;
    if (t > 86400) {
        return Math.round(t / 3600) + ' ' + u.hours;
    } else if (t >= 3600) {
        return Math.floor(t / 3600) + ' ' + u.hours + ' ' +
            Math.round((t % 3600) / 60) + ' ' + u.minutes;
    } else if (t >= 300) {
        return Math.round(t / 60) + ' ' + u.minutes;
    } else if (t >= 60) {
        return Math.floor(t / 60) + ' ' + u.minutes +
            (t % 60 !== 0 ? ' ' + (t % 60) + ' ' + u.seconds : '');
    } else {
        return t + ' ' + u.seconds;
    }
};
var lrmControl = L.Routing.control({
    plan: plan,
    routeWhileDragging: plan.options.routeWhileDragging,
    lineOptions: {
        styles: [
            {color: '#022bb1', opacity: 0.8, weight: 8},
            {color: 'white', opacity: 0.3, weight: 6}
        ]
    },
    altLineOptions: {
        styles: [
            {color: '#40007d', opacity: 0.4, weight: 8},
            {color: 'black', opacity: 0.5, weight: 2, dashArray: '2,4'},
            {color: 'white', opacity: 0.3, weight: 6}
        ]
    },
    summaryTemplate: '<div class="osrm-directions-summary"><h2>{name}</h2><h3>{distance}, {time}</h3></div>',
    // containerClassName: options.lrm.containerClassName,
    // alternativeClassName: options.lrm.alternativeClassName,
    // stepClassName: options.lrm.stepClassName,
    language: plan.options.language,
    showAlternatives: true,
    units: formatter.options.units,
    formatter: formatter,
    serviceUrl: services[state.profile].path,
    profile: state.profile,
    useZoomParameter: plan.options.useZoomParameter,
    routeDragInterval: plan.options.routeDragInterval
}).addTo(map);
lrmControl.getPlan().on('waypointgeocoded', function (e) {
    if (lrmControl.getPlan()._waypoints.filter(function (wp) {
            return !!wp.latLng;
        }).length < 2) {
        map.panTo(e.waypoint.latLng);
    }
});
lrmControl.on('alternateChosen', function (e) {
    var directions = document.querySelectorAll('.leaflet-routing-alt');
    if (directions[0].style.display != 'none') {
        directions[0].style.display = 'none';
        directions[1].style.display = 'block';
    } else {
        directions[0].style.display = 'block';
        directions[1].style.display = 'none';
    }
});
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

function updateState() {
    var newParms = links.format(state);
    try {
        var loc = window.location;
        var baseURL = loc.protocol + '//' + loc.host + loc.pathname;
        var newURL = baseURL.concat('?').concat(newParms);
        history.replaceState({}, document.title, newURL);
    } catch (e) {
        window.location.hash = newParms;
    }
}
map.on('zoomend', function () {
    state.zoom = map.getZoom();
    updateState();
});
map.on('moveend', function () {
    state.center = map.getCenter();
    updateState();
});
lrmControl.getPlan().on('waypointschanged', function () {
    state.waypoints = lrmControl.getWaypoints();
    updateState();
});
lrmControl.on('routeselected', function (e) {
    state.alternative = e.route.routesIndex;
});

function walkNodes(elem, listener) {
    var ch = elem.children;
    for (var i = 0; i < ch.length; i++) {
        var e = ch[i];
        if (e.tagName == 'A') {
            L.DomEvent.on(e, 'click', listener);
        }
        walkNodes(e, listener);
    }
}
function selectProfile(keys, idSuffix, propName) {
    keys.forEach(function (key) {
        var row = document.getElementById(key + idSuffix);
        if (key === state[propName]) {
            row.className = "selectedChoice";
        } else {
            var listener = function (e) {
                L.DomEvent.preventDefault(e);
                state[propName] = key;
                updateState();
                window.location.reload();
            };
            walkNodes(row, listener);
        }
    });
}
selectProfile(sights, 'Sight', 'sight');
selectProfile(Object.keys(services), 'Prof', 'profile');

var access = [
    "A",
    "Б",
    "ДУ",
    "ВДН"
];

function andUndefined(data, propertyName) {
    var value = data[propertyName];
    return typeof value == 'undefined' ? 'Нет' : access[parseInt(value)];
}

function createPassport(title, access) {
    return title + '<br><table class="disabled-table">' +
        '<tr><td>Все категории инвалидов и МГН</td><td>' + andUndefined(access, 'AllCats') + '</td></tr>' +
        '<tr><td>в том числе инвалиды:</td><td>&nbsp;</td></tr>' +
        '<tr><td>передвигающиеся на креслах-колясках</td><td>' + andUndefined(access, 'Trolley') + '</td></tr>' +
        '<tr><td>с нарушениями опорно-двигательного аппарата</td><td>' + andUndefined(access, 'NoWalk') + '</td></tr>' +
        '<tr><td>с нарушениями зрения</td><td>' + andUndefined(access, 'NoEyes') + '</td></tr>' +
        '<tr><td>с нарушениями слуха</td><td>' + andUndefined(access, 'NoHear') + '</td></tr>' +
        '<tr><td>с нарушениями умственного развития</td><td>' + andUndefined(access, 'Brain') + '</td></tr>' +
        '</table>';
}

function poly(object) {
    var polygonPoints = [];
    for (var i = 0; i < object.polygon.length; i++) {
        polygonPoints[i] = new L.LatLng(object.polygon[i].latitude, object.polygon[i].longitude);
    }
    var polygon = new L.Polygon(polygonPoints);
    polygon.on('mouseover', function (e) {
        Util.getJSON('http://176.112.215.104/osis/ReadOSI', {'id': object.uuid}, function (data) {
            L.popup({offset: new L.Point(0, -10)})
                .setLatLng(e.latlng)
                .setContent(createPassport(data['Name'], data['Avails']))
                .openOn(map);
            // console.log(data);
        });
    });
    // polygon.on('mouseout', function () {
    //     map.closePopup();
    // });
    map.addLayer(polygon);
    return polygon;
}

Util.getJSON('http://176.112.215.104/rest/v1/elements', {'size': 200}, function (data) {
    for (var i = 0; i < data.length; i++) {
        var obj = data[i];
        if (obj.polygon && obj.polygon.length > 0) {
            var ap = obj.accessibility_process;
            if (ap) {
                for (var j = 0; j < ap.length; j++) {
                    var a = ap[j].accessibility; // normal, baroow, eyeless, legless, brainless, other
                    var t = ap[j].type; // not_information
                    // full, //Полный
                    // parity, //Частичный
                    // condition, //Условный
                    // not_at_time, //Временно не доступен
                    // not_information, // Нет информации
                    // other
                }
            }
            poly(obj); // todo: include accessibility desc
        }
    }
});
