module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
    'customize-bootstrap': {
      yourTarget: {
        options: {
          components: 'bower_components',
          src: 'resources/public/css/bootstrap/',
          dest: 'resources/public/css/',
        }
      },
    },
    less: {
      bootstrap: {
        options: {
          paths: ['resources/public/css/']
        },
        files: {
          "resources/public/css/bootstrap.css": "resources/public/css/bootstrap.less",
          "resources/public/css/main.css": "resources/public/css/main.less"
        }
      },
      main: {
        options: {
          paths: ["resources/public/css"]
        },
        files: {
          "resources/public/css/main.css": "resources/public/css/main.less"
        }
      }
    },
    watch: {
      bscss: {
        files: ['resources/public/css/bootstrap.less', 'resources/public/css/bootstrap/*.less'],
        tasks: ['less:bootstrap'],
        options: {
          livereload: true,
        },
      },
      css: {
        files: 'resources/public/css/main.less',
        tasks: ['less:main'],
        options: {
          livereload: true,
        },
      },
      html: {
        files: 'src/templates/*.html',
        tasks: ['touch-clj'],
        options: {
          livereload: true,
        },
      }
    },
    connect: {
      server: {
        options: {
          port: 9001,
          base: ''
        }
      }
    },
    md2html: {
      one_file: {
        options: {},
        files: [{
          src: ['resources/markdown/CV.md'],
          dest: 'src/templates/CV.html'
        }]
      }
    }
  });

  // Load the plugin that provides the "uglify" task.
  grunt.loadNpmTasks('grunt-customize-bootstrap');
  grunt.loadNpmTasks('grunt-contrib-less');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-connect');
  grunt.loadNpmTasks('grunt-md2html');
  grunt.loadTasks('grunt-tasks');

  // Default task(s).
  grunt.registerTask('build', ['customize-bootstrap', 'less', 'md2html', 'connect', 'watch']);

};
