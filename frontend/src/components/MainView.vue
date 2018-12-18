<template>
    <div>
        main view
        <post-list :posts="posts"></post-list>
    </div>
</template>

<script>
    import PostList from '@/components/post/PostList';

    export default {
        name: "MainView",
        components: {PostList},
        data() {
            return {
                posts: []
            }
        },
        mounted() {
            this.$userService.getTopPosts(this.$userService.getUsername(), ({data}) => {
                console.log(data);
                this.posts = data.content;
            })
        }
    }
</script>

<style scoped>

</style>