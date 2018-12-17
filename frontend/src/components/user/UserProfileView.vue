<template>
    <div>
        <v-tabs
                color="cyan"
                dark
                slider-color="yellow"
                v-model="active"
        >
            <v-tab
                    href="#posts"
                    ripple
            >
                posts
            </v-tab>
            <v-tab
                    href="#comments"
                    ripple
            >
                comments
            </v-tab>
            <v-tab
                    href="#moderatedGroups"
                    ripple
            >
                moderated groups
            </v-tab>
            <v-tab
                    href="#administratedGroups"
                    ripple
            >
                administrated groups
            </v-tab>
            <v-tab-item
                    id="posts"
            >
                <v-card flat>
                    <post-list :posts="posts"></post-list>
                </v-card>
            </v-tab-item>

            <v-tab-item
                    id="comments"
            >
                <v-card flat>
                    <v-card-text>
                        <v-flex :key="item.id" v-for="(item, index) in comments" xs12>
                            <comment :index="index" :item="item" @removed="handleRemovedComment($event)"
                                     read-only></comment>
                        </v-flex>
                    </v-card-text>
                </v-card>
            </v-tab-item>

            <v-tab-item
                    id="moderatedGroups"
            >
                <group-list v-if="groups.moderatedGroups != undefined" :groups="groups.moderatedGroups"></group-list>
            </v-tab-item>

            <v-tab-item
                    id="administratedGroups"
            >
                <group-list v-if="groups.administratedGroups != undefined" :groups="groups.administratedGroups"></group-list>
            </v-tab-item>
        </v-tabs>
    </div>
</template>

<script>
    import {mapGetters} from 'vuex'
    import PostList from '../post/PostList'
    import Comment from '../post/Comment'
    import GroupList from '../group/GroupList'

    export default {
        name: "UserProfileView",
        components: {PostList, Comment, GroupList},
        props: {
            username: {
                type: String,
                required: true
            }
        },
        data() {
            return {
                active: null,
                text: 'Lorem ipsum dolor',
                posts: [],
                groups: {},
                comments: []
            }
        },
        computed: {
            ...mapGetters([])
        },
        mounted() {
            console.log('mounted');
            this.$userService.getPosts(this.username, ({data}) => {
                this.posts = data.content
            });

            this.$userService.getComments(this.username, ({data}) => {
                this.comments = data.content
            });

            this.$userService.getGroupInfo(this.username, ({data}) => {
                this.groups = data
            })
        },
        methods: {
            next() {
                const active = parseInt(this.active);
                this.active = (active < 2 ? active + 1 : 0)
            }
        }
    }
</script>

<style scoped>

</style>
