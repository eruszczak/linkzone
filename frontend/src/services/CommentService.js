import axios from 'axios'
import {buildPaginationQueryString} from '../utils/utils';

export default class CommentService {
    constructor() {
    }

    list(postId, pageable, query, cb, cbError) {
        pageable = pageable || {};
        const url = `/posts/${postId}/comments/` + buildPaginationQueryString(pageable.page, pageable.perPage);
        axios.get(url).then(res => {
            cb(res)
        }, error => {
        });
    }

    update(commentId, data, cb) {
        const url = `/comments/${commentId}`;
        axios.put(url, data).then(cb);
    }

    delete(commentId, cb) {
        const url = `/comments/${commentId}`;
        axios.delete(url).then(() => {
            cb && cb();
        });
    }
    
    create(postId, data, cb, cbError) {
        const url = `/posts/${postId}/comments/`;
        axios.post(url, data).then(cb, cbError)
    }

    reply(commentId, data, cb, cbError) {
        console.log('data', data);
        const url = `/comments/${commentId}`;
        axios.post(url, data).then(cb, cbError)
    }

    clearVote(id, cb) {
        axios.post(`/comments/${id}/clear-vote/`).then(cb);
    }

    upvote(id, cb) {
        axios.post(`/comments/${id}/upvote/`).then(cb);
    }

    downvote(id, cb) {
        axios.post(`/comments/${id}/downvote/`).then(cb);
    }
}
