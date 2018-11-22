module.exports = {
    devServer: {
      proxy: {
        '/api': {
            target: 'http://localhost:8080',
            // secure: false,
            // ws: true,
            changeOrigin: true
        },
        '/static': {
          target: 'http://localhost:8080',
          pathRewrite: {
            '^/static': ''
          },
          changeOrigin: true
      }
      },
      port: 2999
    },
    css: {
  loaderOptions: {
    // pass options to sass-loader
    sass: {
      // @/ is an alias to src/
      // so this assumes you have a file named `src/variables.scss`
      // data: `@import "~bourbon"`
    }
  }
}
  // module: {
  //   loaders: [{
  //     test: /\.js$/,
  //     loader: 'babel',
  //     exclude: /node_modules/
  //   }, {
  //     test: /\.vue$/,
  //     loader: 'vue'
  //   }, {
  //     test: /\.s[a|c]ss$/,
  //     loader: 'style!css!sass'
  //   }]
  // },
  // vue: {
  //   loaders: {
  //     scss: 'style!css!sass'
  //   }
  // }
  }
