export default {
    state: {
        accessToken: '',
        isAuthenticated: false,
        next: null
    },
    getters: {
        accessToken: state => state.accessToken,
        isAuthenticated: state => state.isAuthenticated,
        next: state => state.next
    },
    mutations: {
        setAccessToken: (state, value) => {
            state.accessToken = value
        },
        setIsAuthenticated: (state, value) => {
            state.isAuthenticated = value
        },
        setNextRoute: (state, value) => {
            state.next = value
        }
    }
}
