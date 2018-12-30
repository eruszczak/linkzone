<template>
    <nav class="navbar has-shadow is-spaced" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">
            <router-link class="navbar-item" to="/">
                <img src="../assets/logo.png">
            </router-link>
            <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample" @click="showBurger = !showBurger">
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
            </a>
        </div>

        <div v-if="showBurger" class="navbar-menu is-hidden-desktop is-active">
            <div class="navbar-start">
                <a href="/archive" class="nav-item is-tab">All Articles</a>

                <div class="navbar-item has-dropdown is-hoverable">
                    <a class="navbar-link " href="http://bulma.io/blog/">
                        Blog
                    </a>
                    <div  class="navbar-dropdown " data-style="width: 18rem;">

                        <a class="navbar-item" href="/2017/03/10/new-field-element/">
                            <div class="navbar-content">
                                <p>
                                    <small class="has-text-info">10 Mar 2017</small>
                                </p>
                                <p>New field element (for better controls)</p>
                            </div>
                        </a>

                        <a class="navbar-item" href="/2016/04/11/metro-ui-css-grid-with-bulma-tiles/">
                            <div class="navbar-content">
                                <p>
                                    <small class="has-text-info">11 Apr 2016</small>
                                </p>
                                <p>Metro UI CSS grid with Bulma tiles</p>
                            </div>
                        </a>

                        <hr class="navbar-divider">
                        <div class="navbar-item">
                            <div class="navbar-content">
                                <div class="level is-mobile">
                                    <div class="level-left">
                                        <div class="level-item">
                                            <strong>Stay up to date!</strong>
                                        </div>
                                    </div>
                                    <div class="level-right">
                                        <div class="level-item">
                                            <a class="button is-rss is-small" href="http://bulma.io/atom.xml">
                      <span class="icon is-small">
                        <i class="fa fa-rss"></i>
                      </span>
                                                <span>Subscribe</span>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="navbar-end">
                <a class="navbar-item" href="/register"></a>
                <a class="navbar-item" href="/login">Login</a>
               
                    <div class="navbar-item has-dropdown is-hoverable">
                        <a class="navbar-link">User</a>
                        <div class="navbar-dropdown">
                            <a href="#" class="navbar-item">My Profile</a>
                            <a href="#" class="navbar-item">Logout</a>
                            <form id="logout-form" action="" method="#" style="display: none;"></form>
                        </div>
                    </div>
            </div>
        </div>

        <div id="navbarBasicExample" class="navbar-menu">
            <div class="navbar-start">
                <router-link class="navbar-item" to="/">
                    {{'navbar.main' | t}}
                </router-link>
                <router-link class="navbar-item" :to="{name: 'postCreateView'}">
                    {{'navbar.add-post' | t}}
                </router-link>
                <div class="navbar-item has-dropdown is-hoverable">
                    <a class="navbar-link">
                        {{'navbar.more'|t}}
                    </a>
                    <div class="navbar-dropdown">
                        <router-link class="navbar-item" :to="{name: 'groupListView'}">
                            {{'navbar.groups' | t}}
                        </router-link>
                        <router-link class="navbar-item" :to="{name: 'groupCreateView'}">{{'groups.add-group'|t}}</router-link>
                    </div>
                </div>
            </div>

            <div class="navbar-end">
                <a href="https://github.com/reryk/reddit-clone" target="_blank" class="navbar-item mr-2">
                    <b-icon icon="github-circle"></b-icon>
                </a>
                <div class="navbar-item has-dropdown is-hoverable" v-if="selectedLanguage">
                    <a class="navbar-link">
                        <img :src="`/${selectedLanguage.icon}`" width="24px"/>
                    </a>
                    <div class="navbar-dropdown has-dropdown is-hoverable">
                        <a class="navbar-item" :class="{'is-active': language.locale === selectedLanguage.locale}" v-for="language in AVAILABLE_LANGUAGES" :key="language.locale" @click="setLocale(language)">
                            <img :src="`/${language.icon}`" width="24px" class="mr-2"/> <span>{{`navbar.${language.locale}` | t}}</span>
                        </a>
                    </div>
                </div>

                <div class="navbar-item has-dropdown is-hoverable" v-if="isAuthenticated && user">
                    <a class="navbar-link">
                        <figure class="image is-16x16" style="margin-right: 5px">
                            <img :src="user.avatarUrl">
                        </figure>
                        {{user.username}}
                    </a>

                    <div class="navbar-dropdown">
                        <router-link :to="{name: 'userProfileView', params: {username: user.username}}" class="navbar-item">
                            {{'navbar.my-profile' | t}}
                        </router-link>
                        <router-link class="navbar-item" :to="{name: 'userEditView'}">
                            {{'navbar.settings' | t}}
                        </router-link>
                        <hr class="navbar-divider">
                        <a class="navbar-item" @click="logout">
                            {{'navbar.logout' | t}}
                        </a>
                    </div>
                </div>
                <div class="navbar-item">
                    <div class="buttons">
                        <template v-if="!isAuthenticated">
                            <router-link :to="{name: 'registerView'}" class="button is-primary">
                                <strong>{{'navbar.register' | t}}</strong>
                            </router-link>
                            <router-link :to="{name: 'loginView'}" class="button is-light">
                                {{'navbar.login' | t}}
                            </router-link>
                        </template>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
    import {AVAILABLE_LANGUAGES} from "../locale";
    import {mapGetters} from 'vuex';
    import {LOCAL_STORAGE_LOCALE_KEY} from '../main';
    import moment from 'moment';

    export default {
        name: "Navbar",
        mounted() {
            this.selectedLanguage = AVAILABLE_LANGUAGES.find(l => l.locale === this.$i18n.locale);
            this.selectedLanguage.active = true;
        },
        data() {
            return {
                AVAILABLE_LANGUAGES,
                selectedLanguage: null,
                showBurger: false
            }
        },
        computed: {
            ...mapGetters(['isAuthenticated', 'user'])
        },
        methods: {
            setLocale(language) {
                this.selectedLanguage.active = false;
                this.$i18n.locale = language.locale;
                this.selectedLanguage = language;
                this.selectedLanguage.active = true;
                localStorage.setItem(LOCAL_STORAGE_LOCALE_KEY, this.$i18n.locale);
                moment.locale(this.$i18n.locale);
            },
            logout() {
                this.$userService.logout();
            },
        }
    }
</script>

<style scoped>

</style>