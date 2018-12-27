<template>
    <div>
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
            this.$postService.getTopPosts(({data}) => {
                this.posts = data.content;
                this.$toggleLoading(false);
            })
        }
    }
</script>

<style scoped>

</style>