'use strict';

var Control = L.Control.extend({
  includes: L.Mixin.Events,
  options: {
    localizationButtonClass: 'osrm-directions-icon'
  },

  initialize: function (profile, profiles, options) {
    L.setOptions(this, options);
    this._current = profile;
    this._profiles = profiles;
  },

  onAdd: function(map) {
    this._container = L.DomUtil.create('div', 'leaflet-bar leaflet-control leaflet-control-profile');
    L.DomEvent.disableClickPropagation(this._container);
    this._profileContainer = L.DomUtil.create('div', 'leaflet-osrm-tools-profile', this._container);
    this._createProfileList(this._profileContainer);
    L.DomEvent.on(this._profileContainer, 'mouseenter', this._openProfileList, this);
    L.DomEvent.on(this._profileContainer, 'mouseleave', this._closeProfileList, this);
    return this._container;
  },

  onRemove: function() {},

  _setupButton: function(button, prof) {
    button.style.backgroundImage = 'url("images/' + prof + '.png")';
    var ev = {profile: prof};
    L.DomEvent.on(button, 'click', function() { this.fire("profilechanged", ev); }, this);
  },

  _createProfileList: function(container) {
    for (var key in this._profiles)
    {
      if (key == this._current)
        continue;
      var button = L.DomUtil.create('span', this.options.localizationButtonClass + " leaflet-osrm-tools-hide", container);
      this._setupButton(button, key);
      button.title = this._profiles[key].label;
    }
    var profileButton = L.DomUtil.create('span', this.options.localizationButtonClass, container);
    this._setupButton(profileButton, this._current);
    profileButton.title = 'Выберите профиль';
  },

  _openProfileList: function() {
    var child;
    for (var i = 0; i < this._profileContainer.childNodes.length - 1; ++i)
    {
      child = this._profileContainer.childNodes[i];
      L.DomUtil.removeClass(child, 'leaflet-osrm-tools-hide');
    }
  },

  _closeProfileList: function() {
    var child;
    for (var i = 0; i < this._profileContainer.childNodes.length - 1; ++i)
    {
      child = this._profileContainer.childNodes[i];
      L.DomUtil.addClass(child, 'leaflet-osrm-tools-hide');
    }
  }
});

module.exports = {
  control: function (profile, profiles, options) {
    return new Control(profile, profiles, options)
  }
};
