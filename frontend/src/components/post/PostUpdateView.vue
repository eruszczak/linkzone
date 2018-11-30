<template>
    <div>
        <post-creator v-if="post" @submit="updatePost" @upload="filename = $event" :post="post"></post-creator>
    </div>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
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
        mounted () {
            this.$postService.getPost(this.id, ({data}) => {
               this.post = data;
               this.filename = this.post.content;
            });
        },
        data () {
            return {
                post: null,
                filename: null
            }
        },
        computed: {
        },
        methods: {
            updatePost(value) {
                console.log('updated post', value)
                if (this.post.type === POST_TYPES.MEDIA) {
                    if (this.filename == null) {
                        return;
                    }
                    value.form.content = this.filename;
                }

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
