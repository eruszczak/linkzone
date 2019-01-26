<template>
    <article class="media">
        <figure class="media-left">
            <p class="image is-32x32">
                <img :src="$userService.getAvatarUrl(item.author)" alt="avatar">
            </p>
        </figure>
        <div class="media-content">
            <div class="content">
                <p>
                    <router-link :to="{name: 'userProfileView', params: {username: item.author.username}}">
                        {{ item.author.username }}
                    </router-link>
                    <span class="ml-2">
                        <a class="button is-small is-white" @click="upvote"><b-icon :type="getUpvoteColor(item, true)" icon="chevron-up"></b-icon></a>
                        <b-tag type="is-white">{{item.upvotedCount || 0}}</b-tag>
                        <a class="button is-small is-white" @click="downvote"><b-icon :type="getUpvoteColor(item, false)" icon="chevron-down"></b-icon></a>
                        <b-tooltip v-if="!readOnly" :label="$t('comments.reply')" type="is-light" size="is-small">
                            <a v-if="!noReply && !isLocked" class="button is-small ml-2 is-white" @click="item.addReply = !item.addReply">
                                <b-icon size="is-small" type="is-info" icon="comment-multiple"></b-icon>
                            </a>
                        </b-tooltip>
                    </span>
                    <span class="is-pulled-right">
                       <a class="button is-white is-small mr-2" @click="confirmDelete" v-if="item.isCreator || item.author.username === parentUsername || canDelete">
                            <b-icon type="is-danger" icon="delete"></b-icon>
                        </a>
                        <small>{{item.createdAt | since}}</small>
                    </span>

                    <br>
                    {{item.content}}
                    <router-link :to="{name: 'postView', params: {postID: item.postId, slug: item.postSlug, name: item.groupName}}">Post #{{item.postTitle}}</router-link>
                </p>
            </div>
            <b-notification v-if="!item.replies && !isInner" :closable="false">
                {{'comments.first-to-reply'|t}}
            </b-notification>
            <comment :parent-username="item.author.username" v-for="(item, index) in item.replies" :index="index" :item="item" @removed="emitRemoveEvent(index)" :key="item.id" no-reply @added="emitAddEvent"
                     :is-locked="isLocked"></comment>
            <new-comment is-reply v-if="item.addReply" v-model="item.reply.body" @add="replyToComment(item)"></new-comment>
        </div>
    </article>
</template>

<script>
    import {getUpvoteColor} from "../../utils/utils";
    import NewComment from "./NewComment";

    export default {
        name: 'Comment',
        components: {NewComment},
        props: {
            item: {
                required: true,
                type: Object
            },
            index: {
                required: true,
                type: Number
            },
            readOnly: {
                type: Boolean,
                default: false
            },
            isLocked: {
                type: Boolean,
                default: false
            },
            noReply: {
                type: Boolean,
                default: false
            },
            parentUsername: {
                required: false,
                type: String
            },
            canDelete: {
                type: Boolean,
                default: false
            }
        },
        data() {
            return {
                updating: false,
                updatedBody: this.item.content
            }
        },
        methods: {
            replyToComment(comment) {
                this.$commentService.reply(comment.id, {content: comment.reply.body}, ({data}) => {
                    comment.replies.push(data);
                    comment.reply.body = '';
                    this.emitAddEvent();
                });
            },
            getUpvoteColor: getUpvoteColor,
            deleteComment() {
                this.$commentService.delete(this.item.id, () => {
                    this.emitRemoveEvent(null);
                    this.$info('comments.removed');
                })
            },
            emitRemoveEvent(innerIndex) {
                this.$emit('removed', {outerIndex: this.index, innerIndex: innerIndex})
            },
            emitAddEvent() {
                this.$emit('added');
            },
            updateComment() {
                this.$commentService.update(this.item.id, {content: this.updatedBody}, ({data}) => {
                    this.item.content = data.content;
                    this.updating = false
                })
            },
            upvote() {
                if (this.item.isUpvoted === 1) {
                    this.clear();
                    return;
                }
                this.$commentService.upvote(this.item.id, ({data}) => {
                    this.item.isUpvoted = 1;
                    this.item.upvotedCount = data.counter;
                });
            },
            downvote() {
                if (this.item.isUpvoted === -1) {
                    this.clear();
                    return;
                }
                this.$commentService.downvote(this.item.id, ({data}) => {
                    this.item.isUpvoted = -1;
                    this.item.upvotedCount = data.counter;
                });
            },
            clear() {
                this.$commentService.clearVote(this.item.id, ({data}) => {
                    this.item.isUpvoted = null;
                    this.item.upvotedCount = data.counter;
                });
            },
            confirmDelete() {
                this.$dialog.confirm({
                    title: this.$t('comments.remove-title'),
                    message: this.$t('comments.remove-message'),
                    confirmText: this.$t('confirm'),
                    cancelText: this.$t('cancel'),
                    type: 'is-danger',
                    hasIcon: true,
                    onConfirm: () => {
                        this.deleteComment();
                    }
                })
            }
        }
    }
</script>

<style scoped>


</style>
