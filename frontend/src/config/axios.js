import {store} from '../store/index'
import axios from 'axios'
import Vue from 'vue'

axios.interceptors.request.use((config) => {
    config.headers.common = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${store.getters.accessToken}`
    };
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
    }
    return Promise.reject(response);
});