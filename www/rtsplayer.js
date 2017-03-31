var exec = require('cordova/exec');

exports.watchVideo = function(ip, port, success, error) {
    exec(success, error, "rtsplayer", "watchVideo", [moviePath]);
};

exports.watch = function(moviePath, user, password, success, error) {
    exec(success, error, "rtsplayer", "watch", [moviePath, user, password]);
};

exports.watchRtsp = function(ip, port, path, user, password, success, error) {
    exec(success, error, "rtsplayer", "watchRtsp", [ip, port, path, user, password]);
};