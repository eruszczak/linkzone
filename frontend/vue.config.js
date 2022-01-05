module.exports = {
    devServer: {
      proxy: {
        '/api': {
            target: process.env.DEV_SERVER_PROXY_TARGET || 'http://localhost:8080',
            changeOrigin: true
        },
        '/static': {
            target: process.env.DEV_SERVER_PROXY_TARGET || 'http://localhost:8080',
          changeOrigin: true
      }
      },
      port: 3000
    },
    css: {
  loaderOptions: {
    sass: {
    }
  }
}
};

