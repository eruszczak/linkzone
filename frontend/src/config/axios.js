import {store} from '../store/index'
import axios from 'axios'
import Vue from 'vue'

// add headers before each request
axios.interceptors.request.use((config) => {
    config.headers.common = {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${store.getters.accessToken}`
    };
    config.url = `/api${config.url.startsWith('/') ? '' : '/'}${config.url}`;
    return config
});

// handle error response
axios.interceptors.response.use((response) => {
    return response
}, function (error) {
    // console.error('axios', error);
    Vue.prototype.$toggleLoading(false);
    if (error.response.status === 401) {
        console.log('unauthorized, logging out ...');
        Vue.prototype.$userService.logout();
    } else if (error.response.status === 403) {
        Vue.prototype.$danger(Vue.prototype.$translate('forbidden'));
    }
    return Promise.reject(error.response)
});