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
