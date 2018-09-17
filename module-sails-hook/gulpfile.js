var spawn = require('child_process').spawn;

gulp.task('npm', function (done) {
    spawn('npm', ['publish'], { stdio: 'inherit' }).on('close', done);
});