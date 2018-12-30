<template>
    <section class="">
        <comment v-for="(comment, index) in comments" :index="index" :item="comment" :is-locked="isLocked" :key="comment.id" @removed="handleRemovedComment" @added="handleAddedComment"></comment>
        <slot></slot>
    </section>
</template>

<script>
    import Comment from './Comment'
    import {prepareComment} from "../../utils/utils";

    export default {
        name: "Comments",
        components: {Comment},
        props: {
            comments: {
                type: Array,
                required: true
            },
            readOnly: {
                type: Boolean,
                default: false
            },
            isLocked: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {

            }
        },
        methods: {
            handleRemovedComment($event) {
                if ($event.innerIndex === null) {
                    const replies = this.comments[$event.outerIndex].replies ? this.comments[$event.outerIndex].replies.length : 0;
                    this.$emit('change', -(1 + replies));
                    this.comments.splice($event.outerIndex, 1);
                } else {
                    const before = this.comments[$event.outerIndex].replies.length;
                    this.comments[$event.outerIndex].replies.splice($event.innerIndex, 1);
                    const after = this.comments[$event.outerIndex].replies.length;
                    this.$emit('change', before - after);
                }
            },
            handleAddedComment($event) {
                this.$emit('change', 1);
            }
        }
    }
</script>

<style scoped>

</style>