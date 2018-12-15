<template>
    <section>
        <v-flex>
            <post :post="post" v-for="post in posts"></post>
        </v-flex>
        <v-alert :value="true"
                 type="info"
                 v-if="!posts.length"
        >
            No posts
        </v-alert>
        <!--<v-list two-line>-->
            <!--<template v-for="(post, index) in posts">-->
                <!--&lt;!&ndash;<v-divider&ndash;&gt;-->
                <!--&lt;!&ndash;v-else-if="item.divider"&ndash;&gt;-->
                <!--&lt;!&ndash;:inset="item.inset"&ndash;&gt;-->
                <!--&lt;!&ndash;:key="index"&ndash;&gt;-->
                <!--&lt;!&ndash;&gt;</v-divider>&ndash;&gt;-->
                <!--<v-list-tile-->
                        <!--:key="post.id"-->
                        <!--:to="{name: 'postView', params: {name: groupName || post.groupName, postID: post.id}}"-->
                        <!--avatar-->
                <!--&gt;-->
                    <!--&lt;!&ndash;<v-list-tile-avatar>&ndash;&gt;-->
                    <!--&lt;!&ndash;<img :src="item.avatar">&ndash;&gt;-->
                    <!--&lt;!&ndash;</v-list-tile-avatar>&ndash;&gt;-->

                    <!--<v-list-tile-content>-->
                        <!--<v-list-tile-title>{{post.title}}; type: {{post.type}}; author: u/{{post.author}}-->
                        <!--</v-list-tile-title>-->
                        <!--<v-list-tile-sub-title>{{post.content}}</v-list-tile-sub-title>-->
                        <!--<v-list-tile-sub-title v-if="isAdmin || isModerator || post.author === $userService.getUsername()">-->
                            <!--edit; delete-->
                        <!--</v-list-tile-sub-title>-->
                        <!--&lt;!&ndash;<v-list-tile-sub-title v-html="post.content"></v-list-tile-sub-title>&ndash;&gt;-->
                    <!--</v-list-tile-content>-->
                <!--</v-list-tile>-->
            <!--</template>-->
        <!--</v-list>-->
    </section>

</template>

<script>
    import Post from './Post'

    export default {
        name: "PostList",
        components: {Post},
        props: {
            posts: {
                type: Array,
                required: true
            },
            groupName: {
                type: String,
                default: null
            },
            isAdmin: {
                type: Boolean,
                default: false
            },
            isModerator: {
                type: Boolean,
                default: false
            }
        },
        methods: {
            goToPost(post) {
                // const params = this.groupName ? {name: this.groupName, postID: post.id} : {postID: post.id};
                this.$router.push({name: 'postView', params: {name: this.groupName, postID: post.id}})
            }
        }
    }
</script>

<style scoped>

</style>