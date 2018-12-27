import Vue from 'vue'
import Vuex from 'vuex'
import search from './modules/search'
import auth from './modules/auth'
import groups from './modules/groups'

Vue.use(Vuex);

export const store = new Vuex.Store({
    strict: process.env.NODE_ENV !== 'production',
    state: {
        isLoading: false
    },
    getters: {
        isLoading: state => state.isLoading
    },
    mutations: {
        toggleLoading: (state, isLoading) => {
            state.isLoading = isLoading
        }
    },
    actions: {
        // FETCH_HELPERS({commit}) {
        // }
    },
    modules: {
        search,
        auth,
        groups
    }
});
