<template>
    <div v-if="!isLoading && group">
        <!-- <p>admins: {{group.administrators}}</p>
        <p>moderators: {{group.moderators}}</p>
        <p>Am I a mod? {{isModerator}}</p>
        <p>Am I an admin? {{isAdmin}}</p> -->
        <p v-if="isAdmin">
            <v-btn :to="{name: 'groupEditView', params: {name: group.name}}">update</v-btn>
        </p>
        <img :src="'/static/' + group.bannerUrl" v-if="group.bannerUrl">
        <h2>{{group.name}}</h2>
        <v-layout row wrap>
            <v-flex sm8 xs12>
                <post-list :is-admin="isAdmin" :is-moderator="isModerator" :posts="posts"></post-list>

            </v-flex>
            <v-flex class="ml-2" sm3 xs12>
                <v-layout row>
                    <!--<v-flex xs12>-->
                    <v-card>
                        <h1>{{group.name}}</h1>
                        <p>description: {{group.description}}</p>
                        <v-btn :color="isSubbed ? 'danger' : 'success'" @click="toggleSub()" flat>{{ (isSubbed ?
                            'unsubscribe' : 'subscribe') | t }}
                        </v-btn>
                        <v-btn :to="{name: 'postCreateView', params: {groupName: group.name}}" flat>
                            {{'add-post-in-group' | t}}
                        </v-btn>
                    </v-card>
                    <!--</v-flex>-->
                </v-layout>
                <v-divider></v-divider>
                <v-layout row>
                    <v-flex xs12>
                        <v-card>
                            <v-expansion-panel>
                                <v-expansion-panel-content
                                        :key="i"
                                        v-for="(item,i) in 5"
                                >
                                    <div slot="header">Rule {{i}}</div>
                                    <v-card>
                                        <v-card-text>Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
                                            eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim
                                            veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea
                                            commodo consequat.
                                        </v-card-text>
                                    </v-card>
                                </v-expansion-panel-content>
                            </v-expansion-panel>
                        </v-card>
                    </v-flex>
                </v-layout>
            </v-flex>
        </v-layout>
        <pagination :pagination="pagination" @change="handleChange"/>
    </div>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import Pagination from '../includes/Pagination'
    import PostList from '../post/PostList'
    import {getPaginationFromResponse} from '../../utils/utils';

    const POST_TYPES = {
        POST: 'POST', MEDIA: 'MEDIA', LINK: 'LINK'
    };

    export default {
        name: 'GroupDetailView',
        props: ['name'],
        components: {Pagination, PostList},
        mounted() {
            this.init()
        },
        data() {
            return {
                group: null,
                posts: [],
                pagination: {},
                isSubbed: false,
                POST_TYPES: POST_TYPES,
                admins: [],
                mods: []
            }
        },
        computed: {
            ...mapGetters(['isLoading']),
            isModerator() {
                return this.group && this.$groupService.isMod(this.group.moderators, this.$userService.getUserId())
            },
            isAdmin() {
                return this.group && this.$groupService.isAdmin(this.group.administrators, this.$userService.getUserId())
            }
        },
        watch: {
            '$route'(to, from) {
                this.init()
            },
        },
        methods: {
            ...mapMutations(['toggleLoading']),
            init() {
                this.toggleLoading(true);
                this.$groupService.getGroupDetail(this.name, res => {
                    this.toggleLoading(false);
                    this.group = res.data;
                    this.mods = this.group.moderators.map(user => user.username);
                    this.admins = this.group.administrators.map(user => user.username);
                    console.log('mods', this.mods);
                    console.log('admins', this.admins);
                    this.getPosts({});
                    this.isSubbed = res.data.isSubbed
                })
            },
            handleChange(pageNumber) {
                this.getPosts({page: pageNumber})
            },
            getPosts(pagination = {}) {
                console.log(this.group);
                this.$groupService.getPosts(this.group, pagination, res => {
                    this.pagination = getPaginationFromResponse(res.data);
                    this.posts = res.data.content;
                    console.log('posty', this.posts)
                })
            },
            toggleSub() {
                if (this.isSubbed) {
                    this.$groupService.unsubscribe(this.group, res => {
                        this.isSubbed = false;
                        this.$message({
                            message: 'unsubbed',
                            type: 'info'
                        })
                    })
                } else {
                    this.$groupService.subscribe(this.group, res => {
                        this.isSubbed = true;
                        this.$message({
                            message: 'subbed',
                            type: 'info'
                        })
                    })
                }
            }
        }
    }
</script>

<style scoped>

</style>