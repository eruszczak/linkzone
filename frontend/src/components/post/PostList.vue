<template>
    <section>
        <v-flex>
            <post :post="post" v-for="post in posts"></post>
        </v-flex>
        <v-alert :value="posts.length === 0"
                 type="info"
        >
            No posts
        </v-alert>
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