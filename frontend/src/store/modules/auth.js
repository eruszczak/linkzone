export default {
    state: {
        accessToken: '',
        isAuthenticated: false,
        next: null,
        user: null
    },
    getters: {
        accessToken: state => state.accessToken,
        isAuthenticated: state => state.isAuthenticated,
        next: state => state.next,
        user: state => state.user
    },
    mutations: {
        setAccessToken: (state, value) => {
            state.accessToken = value;
        },
        setIsAuthenticated: (state, value) => {
            state.isAuthenticated = value;
        },
        setNextRoute: (state, value) => {
            state.next = value;
        },
        setUser: (state, value) => {
            state.user = value;
        }
    }
}
