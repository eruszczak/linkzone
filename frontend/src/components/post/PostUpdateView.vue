<template>
    <div class="container" v-if="post && group">
        <div class="column is-8 is-offset-2 mt-2">
            <nav class="breadcrumb" aria-label="breadcrumbs">
                <ul>
                    <li><router-link :to="{name: 'postView', params: {postID: post.id, slug: post.slug, name: post.groupName}}">Post #{{post.id}}</router-link></li>
                    <li class="is-active"><a href="#" aria-current="page">{{'update'|t}}</a></li>
                </ul>
            </nav>
            <post-creator :is-post-moderator="group.isModerator" :post="post" @submit="updatePost" @upload="filename = $event" v-if="post"></post-creator>
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

                this.$groupService.getGroupDetail(data.groupName, ({data}) => {
                    this.group = data;

                    if (!this.group.isModerator && !this.post.isCreator) {
                        this.$userService.forbidden();
                        return;
                    }
                });

                this.filename = this.post.content;
                this.$toggleLoading(false);
            });
        },
        data() {
            return {
                post: null,
                group: null,
                filename: null
            }
        },
        methods: {
            updatePost(value) {
                value.form.locked = value.postLocked;
                this.$postService.update(this.post.id, value.form, ({data}) => {
                    this.$success('updated-success');
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: data.groupName,
                            postID: data.id,
                            slug: data.slug
                        }
                    })
                })
            }
        }
    }
</script>

<style scoped>

</style>
