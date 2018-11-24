<template>
    <div>
        <v-tabs
                v-model="active"
                color="cyan"
                dark
                slider-color="yellow"
        >
            <v-tab
                    ripple
                    href="#posts"
            >
                posts
            </v-tab>
            <v-tab
                    ripple
                    href="#comments"
            >
                comments
            </v-tab>
            <v-tab
                    ripple
                    href="#moderatedGroups"
            >
                moderated groups
            </v-tab>
            <v-tab
                    ripple
                    href="#administratedGroups"
            >
                administrated groups
            </v-tab>
            <v-tab-item
                    id="posts"
            >
                <v-card flat>
                    posts
                    <!--TODO how to handle group - two routes for post detail?? 1 with groupName-->
                    <post-list :posts="posts"></post-list>
                </v-card>
            </v-tab-item>

            <v-tab-item
                    id="comments"
            >
                <v-card flat>
                    <v-card-text>comments</v-card-text>
                </v-card>
            </v-tab-item>

            <v-tab-item
                    id="moderatedGroups"
            >
                <v-card flat>
                    <v-card-text>{{groups.moderatedGroups}}</v-card-text>
                </v-card>
            </v-tab-item>

            <v-tab-item
                    id="administratedGroups"
            >
                <v-card flat>
                    <v-card-text>{{groups.administratedGroups}}</v-card-text>
                </v-card>
            </v-tab-item>
        </v-tabs>
    </div>
</template>

<script>
    import {mapGetters} from 'vuex'
    import PostList from '../includes/post/PostList'

    export default {
        name: "UserProfileView",
        components: {PostList},
        props: {
            username: {
                type: String,
                required: true
            }
        },
        data () {
            return {
                active: null,
                text: 'Lorem ipsum dolor',
                posts: [],
                groups: {}
            }
        },
        computed: {
          ...mapGetters([])
        },
        mounted() {
            console.log('mounted')
            this.$userService.getUserPosts(this.username, ({data}) => {
                this.posts = data.content
            })

            this.$userService.getGroupInfo(this.username, ({data}) => {
              this.groups = data
            })
        },
        methods: {
            next () {
                const active = parseInt(this.active)
                this.active = (active < 2 ? active + 1 : 0)
            }
        }
    }
</script>

<style scoped>

</style>
