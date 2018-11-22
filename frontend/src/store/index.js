import Vue from 'vue'
import Vuex from 'vuex'
import search from './modules/search'
import auth from './modules/auth'
import groups from './modules/groups'

Vue.use(Vuex)

export const store = new Vuex.Store({
    strict: process.env.NODE_ENV !== 'production',
    // plugins: [
    //   createPersistedState(
    //     {
    //       paths: [
    //         'auth.accessToken'
    //       ]
    //     }
    //   )
    // ],
    state: {
        showToast: false,
        toastOptions: {},
        isLoading: false,
        apiError: {
            isError: false,
            message: null
        },
        loginModalActive: false,
        registerModalActive: false
    },
    getters: {
        isLoading: state => state.isLoading,
        apiError: state => state.apiError,
        loginModalActive: state => state.loginModalActive,
        registerModalActive: state => state.registerModalActive,
        showToast: state => state.showToast,
        toastOptions: state => state.toastOptions,
    },
    mutations: {
        toggleLoading: (state, isLoading) => {
            state.isLoading = isLoading
        },
        setAPIError: (state, message) => {
            state.apiError.isError = true
            state.apiError.message = message
        },
        disableAPIError: (state) => {
            state.apiError.isError = false
            // state.apiError.message = null
        },
        setLoginModalState: (state, value) => {
            state.loginModalActive = value
        },
        setRegisterModalState: (state, value) => {
            state.registerModalActive = value
        },
        setShowToast: (state, config) => {
            state.showToast = true
            state.toastOptions = config
            console.log(state.showToast, state.toastOptions)
        },
        hideToast: state => {
            state.showToast = false
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
})
