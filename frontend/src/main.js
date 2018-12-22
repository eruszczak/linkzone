import Vue from 'vue'
import App from './App.vue'
import router from './router'
import {store} from './store'
import UserService from './services/UserService'
import GroupService from './services/GroupService'
import PostService from './services/PostService'
import CommentService from './services/CommentService'
// import Vuetify from 'vuetify'
// import 'vuetify/dist/vuetify.min.css'
import {TOAST_COLORS} from "./utils/utils";
import './config/axios'
import {toast} from "./config/toast";
import VueI18n from 'vue-i18n'
import messages from './locale'
// import '@mdi/font/css/materialdesignicons.css'
// import 'material-design-icons-iconfont/dist/material-design-icons.css' // TODO
import moment from 'moment';
import Buefy from 'buefy'
// import 'buefy/dist/buefy.css'

Vue.use(Buefy);
// Vue.use(Vuetify, {
//     iconfont: 'mdi'
// });
Vue.use(VueI18n);

const i18n = new VueI18n({
    locale: 'pl',
    messages,
    fallbackLocale: 'en',
});

// Vue.config.performance = process.env.NODE_ENV === 'development' https://github.com/vuetifyjs/vuetifyjs.com/blob/dev/src/main.js

Vue.config.productionTip = false;
Vue.prototype.$groupService = new GroupService();
Vue.prototype.$userService = new UserService();
Vue.prototype.$postService = new PostService();
Vue.prototype.$commentService = new CommentService();
Vue.prototype.$toastColors = TOAST_COLORS;
Vue.prototype.$message = toast;
Vue.filter("fullDate", str => moment(str).format('MMMM Do YYYY, HH:mm:ss'));
Vue.filter('shortDate', str => moment(str).format('dddd, HH:mm:ss'));

Vue.filter('t', function (value) {
    return i18n.t(value)
});

Vue.prototype.$userService.autoLogin();

// create the app instance.
// here we inject the router, store and ssr context to all child components,
// making them available everywhere as `this.$router` and `this.$store`.
new Vue({
    router,
    store,
    i18n,
    render: h => h(App)
}).$mount('#app');
