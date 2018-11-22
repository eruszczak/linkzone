export const API_PREFIX = '/api'

const URL_USER = 'users'
const URL_GROUP = 'groups'
const URL_POST = 'posts'
const URL_COMMENT = 'comments'

const USER = {
    login: `/${URL_USER}/login/`,
    register: `/${URL_USER}/`,
    checkUsername: `/${URL_USER}/checkUsername/{username}/`,
    checkEmail: `/${URL_USER}/checkEmail/{email}/`,
    getUser: `/${URL_USER}/{username}/`,
    currentUserInfo: `/${URL_USER}/details/`,
    groups: `/${URL_USER}/{username}/${URL_GROUP}/`,
    posts: `/${URL_USER}/{username}/${URL_POST}/`,
    findExact: `/${URL_USER}/findExact?username={username}`,
    uploadAvatar: `/${URL_USER}/{username}/upload-avatar/`,
}

const GROUP = {
    list: `/${URL_GROUP}/`,
    detail: `/${URL_GROUP}/{groupName}/`,
    checkName: `/${URL_GROUP}/checkName/{name}`,
    sub: `/${URL_GROUP}/{groupName}/membership/`,
    uploadBanner: `/${URL_GROUP}/{groupName}/upload-banner/`,
}

const POST = {
    detail: `/${URL_POST}/{postID}`,
    group: `${GROUP.detail}${URL_POST}/`,
    comments: `/${URL_POST}/{postID}/${URL_COMMENT}/`
}

const COMMENT = {
    detail: `/${URL_COMMENT}/{id}/`
}

export const ENDPOINTS = {
    users: USER,
    groups: GROUP,
    posts: POST,
    comments: COMMENT
}
