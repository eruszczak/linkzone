<template>
    <div class="container" v-if="post">
        <div class="column is-8 is-offset-2">
            <nav class="breadcrumb" aria-label="breadcrumbs">
                <ul>
                    <li><router-link :to="{name: 'postView', params: {postID: post.id, slug: post.slug}}">Post #{{post.id}}</router-link></li>
                    <li class="is-active"><a href="#" aria-current="page">{{'posts.update-post'|t}}</a></li>
                </ul>
            </nav>
            <post-creator :post="post" @submit="updatePost" @upload="filename = $event" v-if="post"></post-creator>
        </div>
    </div>
</template>

<script>
    import PostCreator from './PostCreator'
    import {POST_TYPES} from "../../services/PostService";

    export default {
        name: 'PostCreateView',
        components: {PostCreator},
        props: {
            id: {
                type: [String, Number],
                required: true
            }
        },
        mounted() {
            this.$postService.getPost(this.id, ({data}) => {
                this.post = data;
                this.filename = this.post.content;
                this.$toggleLoading(false);
            });
        },
        data() {
            return {
                post: null,
                filename: null
            }
        },
        methods: {
            updatePost(value) {
                console.log('updated post', value);
                if (this.post.type === POST_TYPES.MEDIA) {
                    if (this.filename == null) {
                        return;
                    }
                    value.form.content = this.filename;
                }

                value.form.locked = value.postLocked;

                this.$postService.update(this.post.id, value.form, ({data}) => {
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: data.groupName,
                            postID: data.id
                        }
                    })
                })
            }
        }
    }
</script>

<style scoped>

</style>
