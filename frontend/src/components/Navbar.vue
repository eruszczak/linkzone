<template>
    <nav class="navbar has-shadow is-spaced" role="navigation" aria-label="main navigation">
        <div class="navbar-brand">
            <a class="navbar-item" href="https://bulma.io">
                <img src="https://bulma.io/images/bulma-logo.png" width="112" height="28">
            </a>

            <a role="button" class="navbar-burger burger" aria-label="menu" aria-expanded="false" data-target="navbarBasicExample">
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
                <span aria-hidden="true"></span>
            </a>
        </div>

        <div id="navbarBasicExample" class="navbar-menu">
            <div class="navbar-start">
                <a class="navbar-item">
                    Home
                </a>

                <a class="navbar-item">
                    Documentation
                </a>

                <div class="navbar-item has-dropdown is-hoverable">
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
                </div>
            </div>

            <div class="navbar-end">
                <div class="navbar-item has-dropdown is-hoverable" v-if="selectedLanguage">
                    <a class="navbar-link">
                        <img :src="`/${selectedLanguage.icon}`" width="24px"/>
                    </a>

                    <div class="navbar-dropdown">
                        <a class="navbar-item" :class="{'is-active': language.locale === selectedLanguage.locale}" v-for="language in languages" :key="language.locale" @click="setLocale(language)">
                            <img :src="`/${language.icon}`" width="24px" class="mr-2"/> <span>{{`navbar.${language.locale}` | t}}</span>
                        </a>
                    </div>
                </div>
                <div class="navbar-item">
                    <div class="buttons">
                        <router-link :to="{name: 'registerView'}" class="button is-primary">
                            <strong>Sign up</strong>
                        </router-link>
                        <router-link :to="{name: 'loginView'}" class="button is-light">
                            Log in
                        </router-link>
                    </div>
                </div>
            </div>
        </div>
    </nav>
</template>

<script>
    import {AVAILABLE_LANGUAGES} from "../locale";
    const LOCAL_STORAGE_LOCALE_KEY = 'locale';

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
        methods: {
            setLocale(language) {
                this.selectedLanguage.active = false;
                this.$i18n.locale = language.locale;
                localStorage.setItem(LOCAL_STORAGE_LOCALE_KEY, this.$i18n.locale);
                this.selectedLanguage = language;
                this.selectedLanguage.active = true
            }
        }
    }
</script>

<style scoped>

</style>