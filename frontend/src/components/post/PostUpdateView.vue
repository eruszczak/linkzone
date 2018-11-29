<template>
    <div>
        <post-creator v-if="post" @submit="updatePost" @upload="filename = $event" :post="post"></post-creator>
    </div>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
    import PostCreator from './PostCreator'

    export default {
        name: 'PostCreateView',
        components: {PostCreator},
        props: {
            id: {
                type: String,
                required: true
            }
        },
        mounted () {
            this.$postService.getPost(this.id, ({data}) => {
               this.post = data;
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
                if (this.filename == null) {
                    return;
                }
                value.content = this.filename;
                this.$postService.update(this.post.id, value.form, ({data}) => {
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: this.selectedGroup,
                            postID: res.data.id
                        }
                    })
                })
            }
        }
    }
</script>

<style scoped>

</style>
