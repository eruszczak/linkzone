<template>
    <v-fade-transition appear>
        <v-app>
            <v-navigation-drawer
                    app
                    class="grey lighten-4"
                    clipped
                    fixed
                    v-model="drawer"
            >
                <v-list
                        class="grey lighten-4 mt-3"
                        dense
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
                            class="my-3"
                            dark
                    ></v-divider>
                    <v-layout
                            align-center
                            row
                    >
                        <v-flex xs6>
                            <v-subheader>
                                Subscribed groups
                            </v-subheader>
                        </v-flex>
                        <v-flex class="text-xs-right" xs6>
                            <v-btn flat small>edit</v-btn>
                        </v-flex>
                    </v-layout>
                    <v-list-tile>
                        <v-text-field
                                @input="filter"
                                label="Filter"
                                v-model="filterValue"
                        ></v-text-field>
                    </v-list-tile>

                    <v-list-tile
                            :key="group.id"
                            @click="goToGroupDetail(group)"
                            v-for="group in filteredGroups"
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
            <v-toolbar absolute app clipped-left color="amber">
                <v-toolbar-side-icon @click.native="drawer = !drawer"></v-toolbar-side-icon>
                <span class="title ml-3 mr-5"><router-link to="/">Reddit&nbsp;<span class="font-weight-light">App</span></router-link></span>
                <v-text-field
                        flat
                        hide-details
                        label="Search"
                        prepend-inner-icon="search"
                        solo-inverted
                ></v-text-field>
                <v-spacer></v-spacer>
                <v-menu attach="attach" bottom="bottom" left="left" offset-y="offset-y" v-if="selectedLanguage">
                    <v-btn flat="flat" slot="activator" style="min-width: 48px;"><img :src="`/${selectedLanguage.icon}`"
                                                                                      width="26px"/></v-btn>
                    <v-list light="light">
                        <v-list-tile :key="language.locale" @click="setLocale(language)" active-class="active"
                                     avatar="avatar" v-for="language in languages" v-model="language.active">
                            <v-list-tile-avatar size="24px" tile="tile"><img :src="`/${language.icon}`" width="24px"/>
                            </v-list-tile-avatar>
                            <v-list-tile-title>{{language.name}}</v-list-tile-title>
                        </v-list-tile>
                    </v-list>
                </v-menu>
                <v-menu attach="attach" bottom="bottom" left="left" max-height="500" offset-y="offset-y"
                        v-if="isAuthenticated">
                    <v-btn flat="flat" slot="activator" style="min-width: 48px;">
                        <span>{{username}}</span>
                        <i class="material-icons">keyboard_arrow_down</i>
                    </v-btn>
                    <v-list light="light">
                        <v-subheader light="light">Quick links</v-subheader>
                        <v-list-tile :to="{name: 'userProfileView', params: {username: username}}" v-if="username">
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
                        <v-divider class="my-3" light="light"></v-divider>
                        <!--<v-subheader light="light">xxx</v-subheader>-->
                        <v-list-tile @click="logout()" rel="noopener" target="_blank">
                            <v-list-tile-action>
                                <i class="material-icons">power_settings_new</i>
                            </v-list-tile-action>
                            <v-list-tile-content>
                                <v-list-tile-title>{{'logout' | t}}</v-list-tile-title>
                            </v-list-tile-content>
                        </v-list-tile>
                    </v-list>
                </v-menu>
                <v-btn @click="toggleLoginModal()" flat v-if="!isAuthenticated">{{'login' | t}}</v-btn>
                <v-btn @click="toggleRegisterModal()" flat v-if="!isAuthenticated">{{'sign-up' | t}}</v-btn>
                <!--TODO active class if modal is active-->
            </v-toolbar>
            <v-content>
                <v-container class="grey lighten-4" fluid>
                    <!-- <div style="padding: 30px"> -->
                    <router-view/>
                    <v-btn
                            @click="goToCreatePost()"
                            absolute
                            bottom
                            color="pink"
                            dark
                            fab
                            left
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
                    :bottom="false"
                    :color="toastOptions.color"
                    :left="false"
                    :multi-line="false"
                    :right="true"
                    :timeout="toastOptions.timeout"
                    :top="true"
                    :value="showToast"
                    :vertical="false"
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
    import {mapGetters, mapMutations, mapState} from 'vuex'
    import LoginView from './components/auth/LoginView'
    import RegisterView from './components/auth/RegisterView'
    import Loading from './components/includes/Loading'
    import {AVAILABLE_LANGUAGES} from "./locale";

    const LOCAL_STORAGE_LOCALE_KEY = 'locale';

    export default {
        name: 'app',
        components: {
            LoginView, RegisterView, Loading
        },
        mounted() {
            if (this.isAuthenticated) {
                this.setupForAuthenticatedUser()
            }

            this.$userService.authNotifier.on('authChange', () => {
                if (this.isAuthenticated) {
                    this.setupForAuthenticatedUser()
                }
            });

            const locale = localStorage.getItem(LOCAL_STORAGE_LOCALE_KEY) || this.$i18n.locale;
            this.selectedLanguage = this.languages.find(l => l.locale === locale);
            this.selectedLanguage.active = true;
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
            'apiError.isError'(isError) {
                if (isError) {
                    console.log('isError. App.vue')
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
            toggleLoginModal() {
                this.setLoginModalState(true)
            },
            toggleRegisterModal() {
                this.setRegisterModalState(true)
            },
            logout() {
                this.$userService.logout(() => {
                    this.$message({
                        message: 'logged out',
                        type: this.$toastColors.INFO
                    })
                });
                this.username = null
            },
            setupForAuthenticatedUser() {
                this.username = this.$userService.getUsername();
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
                this.selectedLanguage.active = false;
                this.$i18n.locale = language.locale;
                localStorage.setItem(LOCAL_STORAGE_LOCALE_KEY, this.$i18n.locale);
                this.selectedLanguage = language;
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
        background-color: rgba(0, 0, 0, .04);
    }
</style>
