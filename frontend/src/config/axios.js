import {store} from '../store/index'
import axios from 'axios'
import Vue from 'vue'
import router from '../router';

axios.interceptors.request.use((config) => {
    config.headers.common = {
        'Content-Type': 'application/json'
    };
    if (store.getters.accessToken) {
        config.headers.common['Authorization'] = `Bearer ${store.getters.accessToken}`;
    }
    config.url = `/api${config.url.startsWith('/') ? '' : '/'}${config.url}`;
    return config
});

axios.interceptors.response.use((response) => {
    return response;
}, ({response}) => {
    Vue.prototype.$toggleLoading(false);
    if (response.status === 401) {
        Vue.prototype.$userService.logout();
        Vue.prototype.$userService.forceLoginIfNotLoggedIn();
    } else if (response.status === 403) {
        Vue.prototype.$danger(Vue.prototype.$translate('forbidden'));
    } else if (response.status === 404) {
        if (response.data.errors && response.data.errors.indexOf("bad_credentials") === -1) {
            router.replace('/');
            Vue.prototype.$danger(Vue.prototype.$translate('404'));
        }
    }
    return Promise.reject(response);
});