<template>
    <div>
        <navbar></navbar>
        <main class="container">
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
            // ...mapGetters(['accessToken', 'apiError', 'groups', 'isAuthenticated', 'showToast', 'toastOptions', 'filteredGroups'])
        },
        watch: {
            // 'apiError.isError'(isError) {
            //     if (isError) {
            //         console.log('isError. App.vue')
            //         this.$message({
            //             message: this.apiError.message.message,
            //             color: this.$toastColors.ERROR
            //         }, () => {
            //             this.disableAPIError()
            //         })
            //     }

            // }
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
        margin-right: 2em;
    }
    .ml-2 {
        margin-left: 2em;
    }
    .mt-2 {
        margin-top: 2em;
    }
    .mb-2 {
        margin-bottom: 2em;
    }
    .is-horizontal-center {
        justify-content: center;
    }

    @import "~bulma/sass/utilities/_all";
/*
    // Set your colors
    $primary: #63ef5c;
    $primary-invert: findColorInvert($primary);
    $twitter: #4099FF;
    $twitter-invert: findColorInvert($twitter);

    // Setup $colors to use as bulma classes (e.g. 'is-twitter')
    $colors: (
            "white": ($white, $black),
            "black": ($black, $white),
            "light": ($light, $light-invert),
            "dark": ($dark, $dark-invert),
            "primary": ($primary, $primary-invert),
            "info": ($info, $info-invert),
            "success": ($success, $success-invert),
            "warning": ($warning, $warning-invert),
            "danger": ($danger, $danger-invert),
            "twitter": ($twitter, $twitter-invert)
    );

    // Links
    $link: $primary;
    $link-invert: $primary-invert;
    $link-focus-border: $primary;
*/
    // Import Bulma and Buefy styles
    @import "~bulma";
    @import "~buefy/src/scss/buefy";
</style>
