module.exports = {
    devServer: {
      port: 3000,
      proxy: {
        '/api': {
            target: 'http://localhost:8080',
            changeOrigin: true
        },
        '/static': {
            target: 'http://localhost:8080',
            changeOrigin: true
        }
      }
    },
    css: {
      loaderOptions: {
        sass: {
        }
      }
    }
};

