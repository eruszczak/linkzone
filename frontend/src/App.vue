<template>
    <div>
        <navbar></navbar>
        <main style="padding-bottom:50px">
            <router-view></router-view>
            <b-loading :is-full-page="true" :active.sync="isLoading" :can-cancel="false"></b-loading>
        </main>
    </div>
</template>

<script>
    import {mapGetters, mapMutations, mapState} from 'vuex'
    import Loading from './components/includes/Loading'
    import Navbar from './components/Navbar';

    export default {
        name: 'app',
        components: {
            Loading, Navbar
        },
        mounted() {
            // if (this.isAuthenticated) {
            //     this.setupForAuthenticatedUser()
            // }

            // this.$userService.authNotifier.on('authChange', () => {
            //     if (this.isAuthenticated) {
            //         this.setupForAuthenticatedUser()
            //     }
            // });
        },
        data() {
            return {
            }
        },
        computed: {
            ...mapGetters(['isLoading']),
        },
        methods: {
            // ...mapMutations(['setRegisterModalState', 'setLoginModalState', 'setGroups', 'disableAPIError', 'filterGroups']),
            // setupForAuthenticatedUser() {
            //     this.username = this.$userService.getUsername();
            //     // this.$userService.getUserDetails(this.username, (res) => {
            //     this.$groupService.getSubbedGroups(this.username, (res) => {
            //         this.setGroups(res.data)
            //     })
            // },
        }
    }
</script>

<style lang="scss">
    .mr-2 {
        margin-right: 2em!important;
    }
    .ml-2 {
        margin-left: 2em!important;
    }
    .mt-2 {
        margin-top: 2em!important;
    }
    .mb-2 {
        margin-bottom: 2em!important;
    }
    .is-horizontal-center {
        justify-content: center;
    }
    .section.is-fullwidth {
        padding: 0!important;
    }

    @import "~bulma/sass/utilities/_all";
    .navbar-item.is-active {
        color: $primary!important;
    }
    .navbar-item:hover {
        color: black!important;
    }

    @import "~bulma";
    @import "~buefy/src/scss/buefy";
</style>
