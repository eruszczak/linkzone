<template>
    <div>
        <div class="mt-2 box" v-for="post in posts" :key="post.id">
            <post :post="post" link></post>
        </div>
        <b-notification v-if="posts.length === 0" :closable="false">
            {{'posts.empty' | t}}
        </b-notification>
    </div>
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