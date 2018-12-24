export const TOAST_COLORS = {
    SUCCESS: 'success',
    ERROR: 'error',
    INFO: 'info'
};
export const getPaginationFromResponse = response => {
    return {
        total: response.totalElements,
        totalPages: response.totalPages,
        pageSize: response.pageable.pageSize,
        currentPage: response.number,
    }
};
export const PAGINATION = {
    page: 'page',
    perPage: 'size'
};
export const buildPaginationQueryString = (page, perPage) => {
    page = page || 0;
    perPage = perPage || 5;
    return `?${PAGINATION.page}=${page}&${PAGINATION.perPage}=${perPage}`;
};
export const getRelativeUrl = url => {
    const parts = getUrlParts(url);
    return parts.pathname + parts.search + parts.hash
};
export const getUrlParts = url => {
    let a = document.createElement('a');
    a.href = url;
    return {
        href: a.href,
        host: a.host,
        hostname: a.hostname,
        port: a.port,
        pathname: a.pathname,
        protocol: a.protocol,
        hash: a.hash,
        search: a.search
    }
};

export function checkIfImageUrl(url) {
    return url && url.match(/\.(jpeg|jpg|gif|png)$/) != null;
}

export function getYoutubeId(url) {
    const regExp = /^.*(youtu.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);

    if (match && match[2].length == 11) {
        return match[2];
    }

    return null;
}

const INACTIVE_COLOR = 'is-dark';
export function getUpvoteColor(postOrComment, forUpvote) {
    if (postOrComment.isUpvoted === null) {
        return INACTIVE_COLOR;
    }
    if (forUpvote) {
        return postOrComment.isUpvoted === 1 ? 'is-success' : INACTIVE_COLOR;
    }
    return postOrComment.isUpvoted === -1 ? 'is-danger' : INACTIVE_COLOR;
}

export function prepareComment(commentResponse) {
    commentResponse.reply = {
        body: ''
    };
    commentResponse.addReply = false;
    return commentResponse
}