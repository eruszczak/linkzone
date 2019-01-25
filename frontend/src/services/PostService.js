import axios from 'axios'
import {buildPaginationQueryString} from '../utils/utils';

const POST = 'POST';
const MEDIA = 'MEDIA';
const LINK = 'LINK';

export const POST_TYPES = {
    POST, MEDIA, LINK
};

export default class PostService {
    constructor() {
    }

    addPost = (data, groupName, type, cb, cbError) => {
        let url = `/groups/${groupName}/posts/`;
        switch (type) {
            case POST_TYPES.MEDIA:
                url = `${url}media/`;
                break;
            case POST_TYPES.LINK:
                url = `${url}link/`;
                break
        }
        axios.post(url, data).then(cb, cbError)
    };

    getTopPosts = (pageable, cb, cbError) => {
        const url = `/posts/top/` + buildPaginationQueryString(pageable.page, pageable.perPage);
        axios.get(url).then(cb).catch(cbError);
    };

    getPost = (id, cb) => {
        const url = `/posts/${id}`;
        axios.get(url).then(cb, function () {

        })
    };

    clearVote(id, cb) {
        axios.post(`/posts/${id}/clear-vote/`).then(cb);
    }

    upvote(id, cb) {
        axios.post(`/posts/${id}/upvote/`).then(cb);
    }

    downvote(id, cb) {
        axios.post(`/posts/${id}/downvote/`).then(cb);
    }

    update(id, data, cb) {
        const url = `/posts/${id}`;
        axios.put(url, data).then((res) => {
            cb(res)
        })
    }

    delete(id, cb) {
        const url = `/posts/${id}`;
        axios.delete(url).then(cb)
    }
}
