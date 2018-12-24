<template>
    <section class="section">
        <b-notification v-if="!readOnly && post.locked" :closable="false">
            <p>This thread has been locked. New comments cannot be posted.</p>
        </b-notification>
        <comment v-for="comment in comments" :item="comment"></comment>
        <new-comment v-if="!readOnly && !post.locked" v-model="comment.body" @add="addComment"></new-comment>
    </section>
</template>

<script>
    import Comment from './Comment'
    import NewComment from './NewComment'
    import {prepareComment} from "../../utils/utils";

    export default {
        name: "Comments",
        components: {Comment,NewComment},
        props: {
            comments: {
                type: Array,
                required: true
            },
            readOnly: {
                type: Boolean,
                default: false
            },
            post: {
                type: Object,
                required: true
            }
        },
        data() {
            return {
                comment: {
                    body: '',
                    valid: true
                }
            }
        },
        methods: {
            addComment() {
                this.$commentService.create(this.post.id, {content: this.comment.body}, ({data}) => {
                    this.comments.push(prepareComment(data));
                    this.comment.body = '';
                })
            },
            handleRemovedComment($event) {
                if ($event.innerIndex === null) {
                    this.comments.splice($event.outerIndex, 1);
                } else {
                    this.comments[$event.outerIndex].replies.splice($event.innerIndex, 1);
                }
                this.commentMetadata.total = Math.max(this.commentMetadata.total - 1, 0);
            },
        }
    }
</script>

<style scoped>

</style>