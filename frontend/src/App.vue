<template>
    <div>
        <navbar></navbar>
        <main style="padding-bottom:50px">
            <transition name="fade" mode="out-in">
                <router-view></router-view>
            </transition>
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
        },
        data() {
            return {
            }
        },
        computed: {
            ...mapGetters(['isLoading']),
        },
        methods: {

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

    .link {
        cursor: pointer; color: #3273dc;
    }

    .fade-enter-active, .fade-leave-active {
        transition-duration: 0.3s;
        transition-property: opacity;
        transition-timing-function: ease;
    }
    .fade-enter, .fade-leave-active {
        opacity: 0;
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
