import {store} from '../store'
import axios from 'axios'
import {buildPaginationQueryString} from '../utils/utils';

export default class GroupService {
    constructor() {
    }

    subscribe(group, cb, cbError) {
        axios.post(`/groups/${group.name}/membership/`).then(cb, cbError).then(() => {
            // store.commit('addGroup', group)
        });
    }

    unsubscribe(group, cb, cbError) {
        axios.delete(`/groups/${group.name}/membership/`).then(cb, cbError).then(() => {
            // store.commit('unsubGroup', group)
        });
    }

    getSubbedGroups = (username, cb, cbError) => {
        axios.get(`/users/${username}/groups/`).then(cb, cbError)
    };

    getGroupList = (pageable, query, cb, cbError) => {
        const url = '/groups/' + buildPaginationQueryString(pageable.page, pageable.perPage);
        const qs = query ? `&name=${query}` : '';
        axios.get(url + qs).then(cb).catch(cbError);
    };

    getLogoUrl(group) {
        return group.logo ? `/static/${group.logo}` : `https://api.adorable.io/avatar/50/${group.name}`;
    }

    uploadBanner = (groupName, form, cb, cbError) => {
        axios.post(`/groups/${groupName}/upload-banner/`, form).then(cb).catch(cbError);
    };

    uploadLogo = (groupName, form, cb, cbError) => {
        axios.post(`/groups/${groupName}/upload-logo/`, form).then(cb).catch(cbError);
    };

    update(groupName, data, cb) {
        axios.put(`/groups/${groupName}/`, data).then(cb);
    }

    delete(groupName, cb) {
        axios.delete(`/groups/${groupName}/`).then(() => {
            store.dispatch('deleteGroup', groupName);
            cb && cb();
        });
    }

    isMod = (users, userId) => {
        return users.map(user => user.id).filter(id => id === userId).length === 1;
    };

    isAdmin = (users, userId) => {
        console.log('isAdmin', users, users.map(user => user.id), userId);
        return users.map(user => user.id).filter(id => id === userId).length === 1
    };

    getGroupDetail = (groupName, cb, cbError) => {
        axios.get(`/groups/${groupName}/`).then(cb);
    };

    addGroup = (data, cb, cbError) => {
        axios.post(`/groups/`, data).then(cb, cbError)
    };

    getPosts = (group, pageable, cb, cbError) => {
        const url = `/groups/${group.name}/posts/` + buildPaginationQueryString(pageable.page, pageable.perPage);
        axios.get(url).then(cb).catch(cbError);
    };

    checkGroupName = (name, cb, cbError) => {
        axios.get(`/groups/checkName/${name}`).then(cb);
    };

    getGroups = (query) => {
        // const url = ENDPOINTS.posts.group.replace('{groupName}', groupName) + buildPaginationQueryString(pageable.page, pageable.perPage)
    }
}