import {store} from '../store'
import axios from 'axios'
import jwt_decode from 'jwt-decode'
import EventEmitter from 'eventemitter3'
import router, {setNextRoute} from '../router';
import Vue from 'vue';

const LOCAL_STORAGE_ACCESS_TOKEN_KEY = 'ACCESS_TOKEN';

export default class UserService {
    authNotifier = new EventEmitter();
    user;

    isTokenValid(token) {
        const decodedToken = this.decodeToken(token);
        if (decodedToken) {
            const time = Date.now() / 1000;
            if (token.exp < time) {
                return false;
            }
            // this.refreshToken(token);
            return true;
        }
        return false;
    }

    decodeToken(token) {
        if (!token) {
            return null;
        }
        return jwt_decode(token);
    }

    authenticate = (username, password, cb, cbError) => {
        axios.post('/users/login/', {
            usernameOrEmail: username,
            password: password
        }).then(({data}) => {
            store.commit('setIsAuthenticated', true);
            store.commit('setAccessToken', data.accessToken);
            localStorage.setItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY, data.accessToken);
            // this.authNotifier.emit('authChange');
            cb && cb();
            this.getCurrentUserDetails();
            if (store.getters.next) {
                router.push(store.getters.next);
                store.commit('setNextRoute', null);
            }
        }).catch(cbError);
    };

    register = (data, cb, cbError) => {
        axios.post('/users/', data).then(cb).catch(cbError);
    };

    checkUsername = (username, cb, cbError) => {
        axios.get(`/users/checkUsername/${username}/`).then(cb);
    };

    checkEmail = (email, cb, cbError) => {
        axios.get(`/users/checkEmail/${email}/`).then(cb);
    };

    getUserDetails = (username, cb, cbError) => {
        axios.get(`/users/${username}`).then(cb);
    };

    getUserStats = (username, cb, cbError) => {
        axios.get(`/users/${username}/stats/`).then(cb).catch(cbError);
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

    getUpvotedPosts = (username, cb, cbError) => {
        if (!username) {
            return;
        }
        const url = `/users/${username}/posts/upvoted/`;
        axios.get(url).then(cb);
    };

    getDefaultAvatar(username) {
        return `https://api.adorable.io/avatar/100/${username}`;
    }

    getComments = (username, cb, cbError) => {
        axios.get(`/users/${username}/comments/`).then(cb);
    };

    isOwner(username) {
        return username && username === this.getUsername()
    }

    getUpdateInfo = (cb, cbError) => {
        axios.get(`/users/details/`).then(cb).catch(cbError);
    };

    uploadFile(data, cb) {
        axios.post(`/files/upload`, data).then(cb);
    }

    updateAccount = (data, cb, cbError) => {
        axios.put(`/users/${this.user.username}`, data).then(cb).catch(cbError);
    };

    uploadAvatar = (form, cb, cbError) => {
        axios.post(`/users/${this.user.username}/upload-avatar/`, form).then(cb).catch(cbError);
    };

    findExact = (username, cb, cbError) => {
        axios.get(`/users/findExact?username=${username}`).then(cb).catch(cbError)
    };

    autoLogin = (cb, cbError) => {
        const isAuthenticated = this.isTokenValid(this.getToken());
        store.commit('setIsAuthenticated', isAuthenticated);
        store.commit('setAccessToken', this.getToken());
        if (isAuthenticated) {
            cb && cb();
            this.getCurrentUserDetails();
        } else {
            cbError && cbError();
        }
    };

    getCurrentUserDetails() {
        this.getUpdateInfo(({data}) => {
            this.updateUserDetails(data);
        }, () => {
            this.logout();
        });
    }

    updateUserDetails(data) {
        data.avatarUrl = this.getAvatarUrl(data);
        this.user = data;
        store.commit('setUser', data);
    }

    getAvatarUrl(user) {
        return user.avatar ? `/static/${user.avatar}` : this.getDefaultAvatar(user.username);
    }

    getUsername = () => {
        return this.user ? this.user.username : null;
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

    forceLoginIfNotLoggedIn(route) {
        if (!store.getters.isAuthenticated) {
            setNextRoute(route || router.currentRoute);
            Vue.prototype.$info('need-login');
            router.replace({'name': 'loginView'});
        }
    }

    logout = (cb) => {
        console.warn('logging out');
        if (store.getters.isAuthenticated) {
            Vue.prototype.$info('logged-out');
        }

        localStorage.removeItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY);
        store.commit('setIsAuthenticated', false);
        store.commit('setAccessToken', null);
        this.user = null;
        store.commit('setUser', null);

        if (router.currentRoute.meta.requiresAuth) {
            router.replace({path: '/'})
        }

        // this.authNotifier.emit('authChange')
        // store.commit('setGroups', []);
        cb && cb();
    }
}
