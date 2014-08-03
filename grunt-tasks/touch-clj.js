var shelljs = require('shelljs');

module.exports = function(grunt) {

    // Create a new task.
    grunt.registerTask('touch-clj', 'Touches clojure file', function() {
        shelljs.exec('touch src/k_server/core.clj');
    });

};
