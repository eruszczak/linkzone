import {store} from '../store'
import axios from 'axios'
import jwt_decode from 'jwt-decode'
import EventEmitter from 'eventemitter3'
import router from '../router';

const LOCAL_STORAGE_ACCESS_TOKEN_KEY = 'ACCESS_TOKEN';

export default class UserService {
    authNotifier = new EventEmitter();

    user;

    isTokenValid(token) {
        const decodedToken = this.decodeToken(token);
        if (decodedToken) {
            const time = Date.now() / 1000;
            if (token.exp < time) {
                return false
            }
            this.refreshToken(token);
            return true;
        }
        return false
    }

    decodeToken(token) {
        if (!token) {
            return null
        }
        return jwt_decode(token)
    }

    refreshToken(token) {
        // if soon expires: this.extendToken()
    }

    authenticate = (username, password, cb, cbError) => {
        axios.post('/users/login/', {
            usernameOrEmail: username,
            password: password
        }).then(res => {
            store.commit('toggleLoading', false);
            store.commit('setIsAuthenticated', true);
            store.commit('setAccessToken', res.data.accessToken);
            localStorage.setItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY, res.data.accessToken);
            this.authNotifier.emit('authChange');
            cb && cb();
            if (store.getters.next) {
                console.warn(store.getters.next);
                router.push(store.getters.next);
                store.commit('setNextRoute', null);
            }
        }).catch(res => {
            console.log('catch error.response', res);
            cbError && cbError(res)
        });
    };

    register = (data, cb, cbError) => {
        axios.post('/users/', data).then(cb);
    };

    checkUsername = (username, cb, cbError) => {
        axios.get(`/users/checkUsername/${username}/`).then(cb);
    };

    checkEmail = (email, cb, cbError) => {
        axios.get(`/users/checkEmail/${email}/`).then(cb);
    };

    getUserDetails = (username, cb, cbError) => {
        if (!username) {
            return
        }
        axios.get(`/users/${username}`).then(cb);
    };

    getGroupInfo = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = `/users/groupInfo/${username}`;
        axios.get(url).then(cb);
    };

    getPosts = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = `/users/${username}/posts/`;
        axios.get(url).then(cb);
    };

    getTopPosts = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = `/users/${username}/posts/top/`;
        axios.get(url).then(cb);
    };

    getDefaultAvatar(username) {
        return `https://api.adorable.io/avatar/100/${username}`;
    }

    getComments = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = `/users/${username}/comments/`;
        axios.get(url).then(cb);
    };

    isOwner(username) {
        return username && username === this.getUsername()
    }

    getUpdateInfo = (cb, cbError) => {
        axios.get(`/users/details/`).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    };

    uploadFile(data, cb) {
        axios.post(`/files/upload`, data).then(res => {
            cb(res);
        })
    }

    updateAccount = (username, data, cb, cbError) => {
        // update this.user with new data
        axios.put(`/users/${username}`, data).then(cb).catch(cbError);
    };

    uploadAvatar = (username, form, cb, cbError) => {
        axios.post(`/users/${username}/upload-avatar/`, form).then(cb).catch(cbError);
    };

    findExact = (username, cb, cbError) => {
        axios.get(`/users/findExact?username=${username}`).then(cb).catch(cbError)
    };

    autoLogin = (cb, cbError) => {
        const isAuthenticated = this.isTokenValid(this.getToken());
        store.commit('setIsAuthenticated', isAuthenticated);
        console.log('isAuthenticated', isAuthenticated);
        store.commit('setAccessToken', this.getToken());
        if (isAuthenticated) {
            cb && cb();
            this.getUserDetails(this.getUsername(), ({data}) => {
                this.user = data;
            });
        } else {
            cbError && cbError();
        }
    };

    getUsername = () => {
        const token = this.decodeToken(this.getToken());
        return token ? token.iss : null
    };

    getUserId = () => {
        const token = this.decodeToken(this.getToken());
        return token ? parseInt(token.sub) : null
    };

    getUser = () => {
        const token = this.decodeToken(this.getToken());
        return {
            id: token ? parseInt(token.sub) : null,
            username: token.iss
        }
    };

    getToken = () => {
        return localStorage.getItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY)
    };

    logout = (cb) => {
        // store.commit('setAccessToken', '')
        localStorage.removeItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY);
        store.commit('setIsAuthenticated', false);
        console.log('logging out');
        // this.authNotifier.emit('authChange')
        if (router.currentRoute.meta.requiresAuth) {
            console.log('redirecting from protected route');
            router.push({path: '/'})
        }
        store.commit('setGroups', []);
        cb && cb()
    }
}
