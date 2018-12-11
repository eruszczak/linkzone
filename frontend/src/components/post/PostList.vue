<template>
    <v-list two-line>
        <template v-for="(post, index) in posts">
            <!--<v-divider-->
            <!--v-else-if="item.divider"-->
            <!--:inset="item.inset"-->
            <!--:key="index"-->
            <!--&gt;</v-divider>-->
            <v-list-tile
                    :key="post.id"
                    :to="{name: 'postView', params: {name: groupName || post.groupName, postID: post.id}}"
                    avatar
            >
                <!--<v-list-tile-avatar>-->
                <!--<img :src="item.avatar">-->
                <!--</v-list-tile-avatar>-->

                <v-list-tile-content>
                    <v-list-tile-title>{{post.title}}; type: {{post.type}}; author: u/{{post.author}}
                    </v-list-tile-title>
                    <v-list-tile-sub-title>{{post.content}}</v-list-tile-sub-title>
                    <v-list-tile-sub-title v-if="isAdmin || isModerator || post.author === $userService.getUsername()">
                        edit; delete
                    </v-list-tile-sub-title>
                    <!--<v-list-tile-sub-title v-html="post.content"></v-list-tile-sub-title>-->
                </v-list-tile-content>
            </v-list-tile>
        </template>
    </v-list>
</template>

<script>
    export default {
        name: "PostList",
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