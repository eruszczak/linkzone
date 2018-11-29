import Vue from 'vue'
import Router from 'vue-router'
import LoginView from '../components/auth/LoginView'
import RegisterView from '@/components/auth/RegisterView'
import GroupListView from '@/components/group/GroupListView'
import GroupDetailView from '@/components/group/GroupDetailView'
import GroupEditView from '@/components/group/GroupEditView'
import GroupCreateView from '@/components/group/GroupCreateView'
import PostCreateView from '@/components/post/PostCreateView'
import PostView from '@/components/post/PostView'
import UserProfileView from '@/components/user/UserProfileView'
import UserEditView from '@/components/user/UserEditView'
import PostUpdateView from '@/components/post/PostUpdateView'
import { store } from '../store'

Vue.use(Router)

export const LOGIN_VIEW_ROUTE_NAME = 'loginView'

const router =  new Router({
    mode: process.env.NODE_ENV === 'production' ? 'history' : 'hash',
    // linkActiveClass: 'is-active',
    linkExactActiveClass: 'is-active',
    routes: [
        {
            name: LOGIN_VIEW_ROUTE_NAME,
            path: '/login',
            component: LoginView
        },
        {
            name: 'registerView',
            path: '/register',
            component: RegisterView
        },
        {
            name: 'groupListView',
            path: '/groups',
            component: GroupListView
        },
        {
            name: 'groupDetailView',
            path: '/groups/:name',
            component: GroupDetailView,
            props: true
        },
        {
            name: 'groupEditView',
            path: '/groups/:name/update',
            component: GroupEditView,
            props: true
        },
        {
            name: 'postCreateView',
            path: '/add/post/:groupName?',
            component: PostCreateView,
            meta: { requiresAuth: true },
            props: true
        },
        {
            name: 'postView',
            path: '/groups/:name/:postID',
            component: PostView,
            props: true
        },
        {
            name: 'postUpdateView',
            path: '/groups/:name/:id/update',
            component: PostUpdateView,
            props: true
        },
        {
            name: 'groupCreateView',
            path: '/add/group',
            meta: { requiresAuth: true },
            component: GroupCreateView
        },
        {
            name: 'userProfileView',
            path: '/user/:username',
            component: UserProfileView,
            props: true
        },
        {
            name: 'userEditView',
            path: '/settings',
            component: UserEditView
        },
    ]
})

router.beforeEach((to, from, next) => {
    console.log('to', to)
    console.log('from', from)
    if (to.matched.some(record => record.meta.requiresAuth)) {
        console.log('requires Auth', store.getters.isAuthenticated)
        if (!store.getters.isAuthenticated) {
            if (from.fullPath === '/') {
                console.log('replacing')
                router.replace({ 'path': '/'})
            }
            console.log('not logged', store.getters.loginModalActive)
            // store.commit('setLoginModalState', false)
            const timeout = from.name ? 0 : 500 // if user directly navigates to protected route, we must give some time to let login modal component to initialize
            console.log('timeout', timeout)
            setTimeout(() => {
                store.commit('setLoginModalState', true)
                store.commit('setNextRoute', { name: to.name, params: to.params })
            }, timeout)
        } else {
            next()
        }
    } else {
        next()
    }
});

// router.beforeRouteEnter((to, from, next) => {
//    console.log('before enter', to)
// });

export default router