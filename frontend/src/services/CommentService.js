import { store } from '../store'
import axios from 'axios'
import {ENDPOINTS} from '../api/endpoints';
import {buildPaginationQueryString} from '../utils/utils';

export default class CommentService {
    constructor() {
    }

    list(postId, pageable, query, cb, cbError) {
        pageable = pageable || {}
        const url = `/posts/${postId}/comments/` + buildPaginationQueryString(pageable.page, pageable.perPage)
        axios.get(url).then(res => {
            cb(res)
        }, error => {
        });
    }

    update(groupName, data, cb) {
        const url = ENDPOINTS.comments.detail.replace('{id}', id)
        axios.put(url, data).then(cb, function (error) {
        });
    }

    delete(id, cb) {
        const url = ENDPOINTS.comments.detail.replace('{id}', id)
        axios.delete(url).then(() => {
            cb && cb();
        });
    }

    create(postId, data, cb, cbError) {
        const url = `/posts/${postId}/comments/`
        axios.post(url, data).then(cb, cbError)
    }

    reply(commentId, data, cb, cbError) {
        console.log('data', data)
        const url = `/comments/${commentId}`
        axios.post(url, data).then(cb, cbError)
    }
}