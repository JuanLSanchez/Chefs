module.exports = function (grunt) {

  // Load grunt tasks automatically
  require('load-grunt-tasks')(grunt);

  // Time how long tasks take. Can help when optimizing build times
  require('time-grunt')(grunt);

  // Define the configuration for all the tasks
  grunt.initConfig({
    // Run tasks whenever watched files change.
    // * https://github.com/gruntjs/grunt-contrib-watch
    watch: {
      sass: {
        files: ['src/rb-switcher.scss'],
        tasks: ['sass', 'autoprefixer']
      },
    },
    // Compile Sass to CSS
    // * https://github.com/sindresorhus/grunt-sass
    sass: {
      css: {
        options: {
          sourceComments: false,
          sourceMap: false,
          outputStyle: 'nested',
        },
        files: {
          'dist/rb-switcher.css': 'src/rb-switcher.scss',
        }
      },
      min: {
        options: {
          sourceComments: false,
          sourceMap: false,
          outputStyle: 'compressed',
        },
        files: {
          'dist/rb-switcher.min.css': 'src/rb-switcher.scss',
        }
      }
    },
    // Parse CSS and add vendor prefixes to rules by Can I Use.
    // * https://github.com/postcss/autoprefixer
    autoprefixer: {
      options: {
        browsers: ['last 2 versions', 'ie 9']
      },
      dist: {
        options: {
          // Generate a sourcemap.
          map: false
        },
        files: [{
          expand: true,
          cwd: 'dist',
          src: 'rb-switcher.css',
          dest: 'dist'
        }]
      },
      min: {
        options: {
          // Generate a sourcemap.
          map: {
            inline: false,
          }
        },
        files: [{
          expand: true,
          cwd: 'dist',
          src: 'rb-switcher.min.css',
          dest: 'dist'
        }]
      }
    }
  });

  // Main task.
  grunt.registerTask('default', [
    'sass',
    'autoprefixer',
  ]);
};
