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
    console.error('axios', error);
    if (error.response.status === 401) {
        console.log('unauthorized, logging out ...');
        Vue.prototype.$userService.logout()
        // auth.logout();
        // router.replace('/auth/login');
    } else if (error.response.status === 403) {
        console.log(error.response);
        Vue.prototype.$message({
            message: 'Forbidden',
            type: Vue.prototype.$toastColors.ERROR
        });

        store.commit('setAPIError', error.response.data);
        store.commit('toggleLoading', false);
    }
    return Promise.reject(error.response)
});