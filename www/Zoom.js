var exec = require('cordova/exec');
var PLUGIN_NAME = "Zoom";

function callNativeFunction(name, args, success, error) {
    args = args || [];
    success = success || function(){};
    error = error || function(){};
    exec(success, error, PLUGIN_NAME, name, args);

}

var zoom = {

    initialize: function(appKey, appSecret, success, error) {
        callNativeFunction('initialize', [appKey, appSecret], success, error);
    },
    
    login: function(username, password, success, error) {
        callNativeFunction('login', [username, password], success, error);
    },

    logout: function(success, error) {
        callNativeFunction('logout', [], success, error);
    },

    isLoggedIn: function(success, error) {
        callNativeFunction('isLoggedIn', [], success, error);
    },

    joinMeeting: function(meetingNo, meetingPassword, displayName, options, success, error) {
         callNativeFunction('joinMeeting', [meetingNo, meetingPassword, displayName, options], success, error);
    },

    startMeetingWithZAK: function(meetingNo, displayName, zoomToken, zoomAccessToken, userId, options, success, error) {
        callNativeFunction('startMeeting', [meetingNo, displayName, zoomToken, zoomAccessToken, userId, options], success, error);
    },

    startMeeting: function(meetingNo, options, success, error) {
        callNativeFunction('startMeeting', [meetingNo, "", "", "", "", options], success, error);
    },

    startInstantMeeting: function(options, success, error) {
        callNativeFunction('startInstantMeeting', [options], success, error);
    },

    setLocale: function(languageTag, success, error) {
        callNativeFunction('setLocale', [languageTag], success, error);
    }

};

module.exports = zoom;
