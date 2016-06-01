module.exports = function(grunt) {

	// Time how long tasks take. Can help when optimizing build times
	require('time-grunt')(grunt);

	// Load grunt tasks automatically
	require('load-grunt-tasks')(grunt);

	// Define the configuration for all the tasks
	grunt.initConfig({

		// copy the sass files and rename all css file to scss
		copy: {
		  parentStyles: {
			    expand: true,
			    cwd:	'target/govdatastyle-theme-2.3.0-SNAPSHOT/css',
			    src:	['**'],
			    dest: 	'.tmp/styles/',
			    rename: function(dest, src) {
			      return dest + src.replace(/\.css$/, ".scss");
			    }
		  },
		  styles: {
		    expand: true,
		    cwd:	'src/main/webapp/css/',
		    src:	['**'],
		    dest: 	'.tmp/styles/',
		    rename: function(dest, src) {
		      return dest + src.replace(/\.css$/, ".scss");
		    }
		  },
		  scripts: {
			  expand: true,
			  cwd: 	'src/main/webapp/js/',
			  src: 	['**'],
			  dest:	'.tmp/govdatastyle-theme/js/'
		  }
		},

		// compass scss compiler
		compass: {
		  dist: {
		    options: {
		      sassDir: 	'.tmp/styles/',
		      cssDir:  	'.tmp/govdatastyle-theme/css',
		    }
		  }
		},

		// clean up configuration
	    clean: {
	      dist: {
	        files: [{
	          dot: true,
	          src: [
	            '.sass-cache',
	            '.tmp',
	          ]
	        }]
	      },
	    },

	    // server and proxy for static files
	    connect: {
	        options: {
	          port: 9000,
	          hostname: '0.0.0.0',
	          livereload: 35729
	        },
	        proxies: [{
	          context:  '/',
	          host:     'localhost',
	          port: 8080
	        }],
	        livereload: {
	          options: {
	            open: false,
	            middleware: function (connect) {
	          	  var middlewares = [];
	              // Serve static files
	              middlewares.push(connect.static('.tmp'));
	              // Setup the proxy
	              middlewares.push(require('grunt-connect-proxy/lib/utils').proxyRequest);
	              return middlewares;
	            }
	          }
	        }
	    },

	    // watch for file changes in the sass and js files
	    watch: {
	    	compass: {
	            files: ['src/main/webapp/css/{,*/}*.{scss,sass,css}'],
	            tasks: ['newer:copy:styles', 'compass'],
	            options: {
	                livereload: true,
	            }
	        },
	        scripts: {
	        	files: ['src/main/webapp/js/{,*/}*.js'],
	        	tasks: ['newer:copy:scripts'],
	        	options: {
	        		livereload: true,
	        	}
	        }
	    }

	});

	grunt.registerTask('serve', 'Compile then start a connect web server', function (target) {
	    grunt.task.run([
	      'build',
	      'configureProxies:server',
	      'connect:livereload',
	      'watch'
	    ]);
	});

	grunt.registerTask('build', [ 'newer:copy:parentStyles', 'newer:copy:styles', 'newer:copy:scripts', 'compass' ]);

	grunt.registerTask('default', [ 'serve' ]);
};
