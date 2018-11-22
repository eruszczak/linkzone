<template>
    <v-fade-transition appear>
        <v-app>
            <v-navigation-drawer
                    v-model="drawer"
                    fixed
                    clipped
                    class="grey lighten-4"
                    app
            >
                <v-list
                        dense
                        class="grey lighten-4 mt-3"
                >
                    <v-list-tile
                            @click="goToGroupList()"
                    >
                        <v-list-tile-action>
                            <v-icon>archive</v-icon>
                        </v-list-tile-action>
                        <v-list-tile-content>
                            <v-list-tile-title class="grey--text">
                                Explore groups
                            </v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                    <v-list-tile
                    >
                        <v-list-tile-action>
                            <v-icon>archive</v-icon>
                        </v-list-tile-action>
                        <v-list-tile-content>
                            <v-list-tile-title class="grey--text">
                                {{ 'saved-posts' | t }}
                            </v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                    <v-divider
                            dark
                            class="my-3"
                    ></v-divider>
                    <v-layout
                            row
                            align-center
                    >
                        <v-flex xs6>
                            <v-subheader>
                                Subscribed groups
                            </v-subheader>
                        </v-flex>
                        <v-flex xs6 class="text-xs-right">
                            <v-btn small flat>edit</v-btn>
                        </v-flex>
                    </v-layout>
                    <v-list-tile>
                        <v-text-field
                                v-model="filterValue"
                                label="Filter"
                                @input="filter"
                        ></v-text-field>
                    </v-list-tile>

                    <v-list-tile
                            v-for="group in filteredGroups"
                            :key="group.id"
                            @click="goToGroupDetail(group)"
                    >
                        <v-list-tile-action>
                            <v-icon>chat_bubble</v-icon>
                        </v-list-tile-action>
                        <v-list-tile-content>
                            <v-list-tile-title class="grey--text">
                                /g/{{group.name}}
                            </v-list-tile-title>
                        </v-list-tile-content>
                    </v-list-tile>
                </v-list>
            </v-navigation-drawer>
            <v-toolbar color="amber" app absolute clipped-left>
                <v-toolbar-side-icon @click.native="drawer = !drawer"></v-toolbar-side-icon>
                <span class="title ml-3 mr-5">Reddit&nbsp;<span class="font-weight-light">App</span></span>
                <v-text-field
                        solo-inverted
                        flat
                        hide-details
                        label="Search"
                        prepend-inner-icon="search"
                ></v-text-field>
                <v-spacer></v-spacer>
                <v-menu v-if="selectedLanguage" bottom="bottom" offset-y="offset-y" left="left" attach="attach">
                    <v-btn slot="activator" flat="flat" style="min-width: 48px;"><img :src="`/${selectedLanguage.icon}`" width="26px" /></v-btn>
                    <v-list light="light">
                        <v-list-tile v-model="language.active" avatar="avatar" v-for="language in languages" :key="language.locale" @click="setLocale(language)" active-class="active">
                            <v-list-tile-avatar tile="tile" size="24px"><img :src="`/${language.icon}`" width="24px" /></v-list-tile-avatar>
                            <v-list-tile-title>{{language.name}}</v-list-tile-title>
                        </v-list-tile>
                    </v-list>
                </v-menu>
                <v-menu v-if="isAuthenticated" attach="attach" bottom="bottom" left="left" offset-y="offset-y" max-height="500">
                    <v-btn flat="flat" slot="activator" style="min-width: 48px;">
                        <span>{{username}}</span>
                        <i class="material-icons">keyboard_arrow_down</i>
                    </v-btn>
                    <v-list light="light">
                        <v-subheader light="light">Quick links</v-subheader>
                        <v-list-tile v-if="username" :to="{name: 'userProfileView', params: {username: username}}">
                            <v-list-tile-action>
                                <v-icon>mdi-account</v-icon>
                            </v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>My profile</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>
                        <v-list-tile :to="{name: 'userEditView'}">
                            <v-list-tile-action>
                                <i class="material-icons">settings</i>
                            </v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>User settings</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>
                        <v-divider light="light" class="my-3"></v-divider>
                        <!--<v-subheader light="light">xxx</v-subheader>-->
                        <v-list-tile target="_blank" rel="noopener" @click="logout()">
                            <v-list-tile-action>
                                <i class="material-icons">power_settings_new</i>
                            </v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>{{'logout' | t}}</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>
                    </v-list>
                </v-menu>
                <v-btn v-if="!isAuthenticated" @click="toggleLoginModal()" flat>{{'login' | t}}</v-btn>
                <v-btn v-if="!isAuthenticated" @click="toggleRegisterModal()" flat>{{'sign-up' | t}}</v-btn>
                <!--TODO active class if modal is active-->
            </v-toolbar>
            <v-content>
                <v-container class="grey lighten-4" fluid>
                <!-- <div style="padding: 30px"> -->
                    <router-view/>
                    <v-btn
                            absolute
                            dark
                            fab
                            bottom
                            left
                            color="pink"
                            @click="goToCreatePost()"
                    >
                        <v-icon>add</v-icon>
                    </v-btn>
                <!-- </div> -->
                    <!--</v-layout>-->
                </v-container>
                <v-footer class="pa-3">
                    <v-spacer></v-spacer>
                    <div>&copy; {{ new Date().getFullYear() }}</div>
                </v-footer>
            </v-content>
            <v-snackbar
                    :value="showToast"
                    :bottom="false"
                    :left="false"
                    :right="true"
                    :top="true"
                    :vertical="false"
                    :multi-line="false"
                    :color="toastOptions.color"
                    :timeout="toastOptions.timeout"
            >
                {{toastOptions.message}}
            </v-snackbar>
            <login-view/>
            <register-view/>
            <loading :loading="isLoading"/>
        </v-app>
    </v-fade-transition>
</template>

<script>
    import {mapState, mapGetters, mapMutations} from 'vuex'
    import LoginView from './components/auth/LoginView'
    import RegisterView from './components/auth/RegisterView'
    import Loading from './components/includes/Loading'
    import {AVAILABLE_LANGUAGES} from "./locale";

    const LOCAL_STORAGE_LOCALE_KEY = 'locale'

    export default {
        name: 'app',
        components: {
            LoginView, RegisterView, Loading
        },
        mounted () {
            if (this.isAuthenticated) {
                this.setupForAuthenticatedUser()
            }

            this.$userService.authNotifier.on('authChange', () => {
                if (this.isAuthenticated) {
                    this.setupForAuthenticatedUser()
                }
            })

            const locale = localStorage.getItem(LOCAL_STORAGE_LOCALE_KEY) || this.$i18n.locale
            this.selectedLanguage = this.languages.find(l => l.locale === locale)
            this.selectedLanguage.active = true
            this.$i18n.locale = locale
        },
        data() {
            return {
                drawer: null,
                username: null,
                filterValue: '',
                languages: AVAILABLE_LANGUAGES,
                selectedLanguage: null
            }
        },
        computed: {
            ...mapState(['isLoading']),
            ...mapGetters(['accessToken', 'apiError', 'groups', 'isAuthenticated', 'showToast', 'toastOptions', 'filteredGroups'])
        },
        watch: {
            'apiError.isError' (isError) {
                if (isError) {
                    this.$message({
                        message: this.apiError.message.message,
                        color: this.$toastColors.ERROR
                    }, () => {
                        this.disableAPIError()
                    })
                }

            }
        },
        methods: {
            ...mapMutations(['setRegisterModalState', 'setLoginModalState', 'setGroups', 'disableAPIError', 'filterGroups']),
            toggleLoginModal () {
                this.setLoginModalState(true)
            },
            toggleRegisterModal () {
                this.setRegisterModalState(true)
            },
            logout () {
                this.$userService.logout(() => {
                    this.$message({
                        message: 'logged out',
                        type: this.$toastColors.INFO
                    })
                })
                this.username = null
            },
            setupForAuthenticatedUser() {
                this.username = this.$userService.getUsername()
                // this.$userService.getUserDetails(this.username, (res) => {
                this.$groupService.getSubbedGroups(this.username, (res) => {
                    this.setGroups(res.data)
                })
            },
            goToGroupList() {
                this.$router.push({name: 'groupListView'})
            },
            goToGroupDetail(group) {
                this.$router.push({name: 'groupDetailView', params: {name: group.name}})
            },
            goToCreatePost() {
                this.$router.push({name: 'postCreateView'})
            },
            filter() {
                this.filterGroups(this.filterValue)
            },
            setLocale(language) {
                this.selectedLanguage.active = false
                this.$i18n.locale = language.locale
                localStorage.setItem(LOCAL_STORAGE_LOCALE_KEY, this.$i18n.locale)
                this.selectedLanguage = language
                this.selectedLanguage.active = true
            }
        }
    }
</script>

<style>
    .el-header {
        background-color: #B3C0D1;
        color: #333;
        line-height: 60px;
    }

    .el-aside {
        color: #333;
    }

    .v-list__tile.active {
        background-color: rgba(0,0,0,.04);
    }
</style>
