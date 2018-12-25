<template>
    <nav class="navbar has-shadow is-spaced" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">
            <router-link class="navbar-item" to="/">
                <img src="https://bulma.io/images/bulma-logo.png" width="112" height="28">
            </router-link>

            <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
            </a>
        </div>

        <div id="navbarBasicExample" class="navbar-menu">
            <div class="navbar-start">
                <router-link class="navbar-item" to="/">
                    {{'navbar.main' | t}}
                </router-link>

                <router-link class="navbar-item" :to="{name: 'postCreateView'}">
                    {{'navbar.add-post' | t}}
                </router-link>

                <!-- <div class="navbar-item has-dropdown is-hoverable">
                    <a class="navbar-link">
                        More
                    </a>

                    <div class="navbar-dropdown">
                        <a class="navbar-item">
                            About
                        </a>
                        <a class="navbar-item">
                            Jobs
                        </a>
                        <a class="navbar-item">
                            Contact
                        </a>
                        <hr class="navbar-divider">
                        <a class="navbar-item">
                            Report an issue
                        </a>
                    </div>
                </div> -->
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
                        <a class="navbar-item" :class="{'is-active': language.locale === selectedLanguage.locale}" v-for="language in languages" :key="language.locale" @click="setLocale(language)">
                            <img :src="`/${language.icon}`" width="24px" class="mr-2"/> <span>{{`navbar.${language.locale}` | t}}</span>
                        </a>
                    </div>
                </div>
                <div class="navbar-item has-dropdown is-hoverable" v-if="isAuthenticated && user">
                    <a class="navbar-link">
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
    const LOCAL_STORAGE_LOCALE_KEY = 'locale';
    import {mapGetters} from 'vuex';

    export default {
        name: "Navbar",
        mounted() {
            const locale = localStorage.getItem(LOCAL_STORAGE_LOCALE_KEY) || this.$i18n.locale;
            this.selectedLanguage = this.languages.find(l => l.locale === locale);
            this.selectedLanguage.active = true;
            this.$i18n.locale = locale;
        },
        data() {
            return {
                languages: AVAILABLE_LANGUAGES,
                selectedLanguage: null
            }
        },
        computed: {
            ...mapGetters(['isAuthenticated', 'user'])
        },
        methods: {
            setLocale(language) {
                this.selectedLanguage.active = false;
                this.$i18n.locale = language.locale;
                localStorage.setItem(LOCAL_STORAGE_LOCALE_KEY, this.$i18n.locale);
                this.selectedLanguage = language;
                this.selectedLanguage.active = true
            },
            logout() {
                this.$userService.logout(() => {
                    this.$message({
                        message: 'logged out',
                        type: this.$toastColors.INFO
                    })
                });
                // this.username = null
            },
        }
    }
</script>

<style scoped>

</style>