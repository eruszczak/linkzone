import { store } from '../store'
import axios from 'axios'
import {ENDPOINTS} from "../api/endpoints";
import jwt_decode from 'jwt-decode'
import EventEmitter from 'eventemitter3'
import router from '../router';

const LOCAL_STORAGE_ACCESS_TOKEN_KEY = 'ACCESS_TOKEN'

export default class UserService {
    authNotifier = new EventEmitter();

    isTokenValid(token) {
        const decodedToken = this.decodeToken(token)
        if (decodedToken) {
            const time = Date.now() / 1000
            if (token.exp < time) {
                return false
            }
            this.refreshToken(token)
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
        axios.post(ENDPOINTS.users.login, {
            usernameOrEmail: username,
            password: password
        }).then(res => {
            store.commit('toggleLoading', false)
            store.commit('setIsAuthenticated', true)
            store.commit('setAccessToken', res.data.accessToken)
            localStorage.setItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY, res.data.accessToken)
            this.authNotifier.emit('authChange')
            cb && cb()
            if (store.getters.next) {
                console.warn(store.getters.next)
                router.push(store.getters.next)
                store.commit('setNextRoute', null)
            }
        }, error => {
            console.log('catch error.response', error.response)
            cbError && cbError()
        })
    }

    register = (data, cb, cbError) => {
        axios.post(ENDPOINTS.users.register, data).then(cb, error => {
            console.log('catch error.response', error.response)
        });
    }

    checkUsername = (username, cb, cbError) => {
        const url = ENDPOINTS.users.checkUsername.replace('{username}', username);
        axios.get(url).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    }

    checkEmail = (email, cb, cbError) => {
        const url = ENDPOINTS.users.checkEmail.replace('{email}', email)
        axios.get(url).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    }

    getUserDetails = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = ENDPOINTS.users.getUser.replace('{username}', username)
        axios.get(url).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    }

    getGroupInfo = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = `/users/groupInfo/${username}`
        axios.get(url).then(cb);
    }

    getPosts = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = `/users/${username}/posts/`
        axios.get(url).then(cb);
    }

    getComments = (username, cb, cbError) => {
        if (!username) {
            return
        }
        const url = `/users/${username}/comments/`
        axios.get(url).then(cb);
    }

    isOwner(username) {
        return username && username === this.getUsername()
    }

    getUpdateInfo = (cb, cbError) => {
        const url = ENDPOINTS.users.currentUserInfo
        axios.get(url).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    }

    updateAccount = (username, data, cb, cbError) => {
        const url = ENDPOINTS.users.getUser.replace('{username}', username)
        axios.put(url, data).then(cb).catch(cbError);
    }

    uploadAvatar = (username, form, cb, cbError) => {
        const url = ENDPOINTS.users.uploadAvatar.replace('{username}', username)
        axios.post(url, form).then(cb, function (error) {
        });
    }

    findExact = (username, cb, cbError) => {
        const url = ENDPOINTS.users.findExact.replace('{username}', username);
        axios.get(url).then(cb).catch(cbError)
    }

    autoLogin = (cb, cbError) => {
        const isAuthenticated = this.isTokenValid(this.getToken())
        store.commit('setIsAuthenticated', isAuthenticated)
        console.log('isAuthenticated', isAuthenticated)
        store.commit('setAccessToken', this.getToken())
        if (isAuthenticated) {
            cb && cb()
        } else {
            cbError && cbError()
        }
    }

    getUsername = () => {
        const token = this.decodeToken(this.getToken())
        return token ? token.iss : null
    }

    getUserId = () => {
        const token = this.decodeToken(this.getToken())
        return token ? parseInt(token.sub) : null
    }

    getUser = () => {
        const token = this.decodeToken(this.getToken())
        return {
            id: token ? parseInt(token.sub) : null,
            username: token.iss
        }
    }

    getToken = () => {
        return localStorage.getItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY)
    }

    logout = (cb) => {
        // store.commit('setAccessToken', '')
        localStorage.removeItem(LOCAL_STORAGE_ACCESS_TOKEN_KEY)
        store.commit('setIsAuthenticated', false)
        console.log('logging out')
        // this.authNotifier.emit('authChange')
        if (router.currentRoute.meta.requiresAuth) {
            console.log('redirecting from protected route')
            router.push({ path: '/' })
        }
        store.commit('setGroups', [])
        cb && cb()
    }
}
