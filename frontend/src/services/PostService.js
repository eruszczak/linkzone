import { store } from '../store'
import axios from 'axios'
import {buildPaginationQueryString} from '../utils/utils';

const POST = 'POST'
const MEDIA = 'MEDIA'
const LINK = 'LINK'

export const POST_TYPES = {
    POST, MEDIA, LINK
}

export const POST_TYPES_TRANSLATE = {
    [POST_TYPES.POST]: 'Text post',
    [POST_TYPES.MEDIA]: 'Picture',
    [POST_TYPES.LINK]: 'Link'
}

export default class PostService {
    constructor() {
    }

    addPost = (data, groupName, type, cb) => {
        let url = `/groups/${groupName}/posts/`
        switch (type) {
            case POST_TYPES.MEDIA:
                url = `${url}media/`
                break
            case POST_TYPES.LINK:
                url = `${url}link/`
                break
        }
        axios.post(url, data).then(cb, error => {
            console.log('catch error.response', error.response)
        })
    };

    getPost = (id, cb) => {
        const url = `/posts/${id}`;
        axios.get(url). then(cb, function() {

        })
    };

    update (id, data, cb) {
        const url = `/posts/${id}`;
        axios.put(url, data). then((res) => {
            cb(res)
        })
    }

    delete (id, cb) {
        const url = `/posts/${id}`;
        axios.delete(url). then(cb)
    }
}
