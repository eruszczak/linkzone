<template>
    <div>
        <post :post="post" v-for="post in posts"></post>
        <b-notification v-if="posts.length === 0">
            {{'posts.empty' | t}}
        </b-notification>
    </div>
</template>

<script>
    import Post from './Post'
    import BNotification from "buefy/src/components/notification/Notification";

    export default {
        name: "PostList",
        components: {BNotification, Post},
        props: {
            posts: {
                type: Array,
                required: true
            },
            isModerator: {
                type: Boolean,
                default: false
            }
        },
        methods: {
            goToPost(post) {
                // const params = this.groupName ? {name: this.groupName, postID: post.id} : {postID: post.id};
                this.$router.push({name: 'postView', params: {name: post.groupName, postID: post.id}})
            }
        }
    }
</script>

<style scoped>

</style>