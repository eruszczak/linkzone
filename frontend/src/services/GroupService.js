import { store } from '../store'
import axios from 'axios'
import {ENDPOINTS} from '../api/endpoints';
import {buildPaginationQueryString} from '../utils/utils';

export default class GroupService {
    constructor() {
        console.log('creating groups ervice')
    }

    subscribe(group, cb, cbError) {
        const url = ENDPOINTS.groups.sub.replace('{groupName}', group.name)
        axios.post(url).then(cb, cbError).then(() => {
            store.commit('addGroup', group)
        });
    }
    unsubscribe(group, cb, cbError) {
        const url = ENDPOINTS.groups.sub.replace('{groupName}', group.name)
        axios.delete(url).then(cb, cbError).then(() => {
            store.commit('unsubGroup', group)
        });
    }
    getSubbedGroups = (username, cb, cbError) => {
        const url = ENDPOINTS.users.groups.replace('{username}', username)
        axios.get(url).then(cb, cbError)
    }

    getGroupList = (pageable, query, cb, cbError) => {
        pageable = pageable || {}
        const url = ENDPOINTS.groups.list + buildPaginationQueryString(pageable.page, pageable.perPage)
        const qs = query ? `&name=${query}` : ''
        axios.get(url + qs).then(res => {
            cb(res)
        }, error => {
            console.log('catch error.response', error.response)
        });
    }

    uploadBanner = (groupName, form, cb, cbError) => {
        const url = ENDPOINTS.groups.uploadBanner.replace('{groupName}', groupName)
        axios.post(url, form).then(cb, function (error) {
        });
    }

    update(groupName, data, cb) {
        const url = ENDPOINTS.groups.detail.replace('{groupName}', groupName)
        axios.put(url, data).then(cb, function (error) {
        });
    }

    delete(groupName, cb) {
        const url = ENDPOINTS.groups.detail.replace('{groupName}', groupName)
        axios.delete(url).then(() => {
            store.dispatch('deleteGroup', groupName)
            cb && cb();
        });
    }

    isMod = (users, userId) => {
        return users.map(user => user.id).filter(id => id === userId).length === 1
    }

    isAdmin = (users, userId) => {
        console.log('isAdmin', users, users.map(user => user.id), userId)
        return users.map(user => user.id).filter(id => id === userId).length === 1
    }

    getGroupDetail = (groupName, cb, cbError) => {
        const url = ENDPOINTS.groups.detail.replace('{groupName}', groupName)
        axios.get(url).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    }

    addGroup = (data, cb, cbError) => {
        const url = ENDPOINTS.groups.list
        axios.post(url, data).then(cb, cbError)
    }
    
    getPosts = (group, pageable, cb, cbError) => {
        const url = ENDPOINTS.posts.group.replace('{groupName}', group.name) + buildPaginationQueryString(pageable.page, pageable.perPage)
        axios.get(url).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    }
    
    checkGroupName = (name, cb, cbError) => {
        const url = ENDPOINTS.groups.checkName.replace('{name}', name);
        axios.get(url).then(cb, function (error) {
            console.log('catch error.response', error.response)
        });
    }

    getGroups = (query) => {
        // const url = ENDPOINTS.posts.group.replace('{groupName}', groupName) + buildPaginationQueryString(pageable.page, pageable.perPage)
    }
}