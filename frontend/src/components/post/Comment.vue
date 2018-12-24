<template>
    <article class="media">
        <figure class="media-left">
            <p class="image is-64x64">
                <img :src="!item.author.avatar ? $userService.getDefaultAvatar(item.author.username) : `/static/${item.author.avatar}`" alt="avatar">
            </p>
        </figure>
        <div class="media-content">
            <div class="content">
                <p>
                    <strong><router-link :to="{name: 'userProfileView', params: {username: item.author.username}}">{{ item.author.username }}</router-link></strong>
                    <br>
                    {{item.content}}
                    <br>
                    <small><a>Like</a> · <a v-if="!noReply && !isLocked" @click="item.addReply = !item.addReply">Reply</a> {{item.createdAt | shortDate}}</small>
                </p>
            </div>
            <b-notification v-if="!item.replies && !isInner" :closable="false">
                Be 1st to reply
            </b-notification>
            <comment v-for="item in item.replies" :item="item" @removed="emitRemoveEvent(index)" no-reply :is-locked="isLocked"></comment>
            <new-comment v-if="item.addReply" v-model="item.reply.body" @add="replyToComment(item)"></new-comment>
        </div>
    </article>
    <!--<v-card :color='index % 2 === 0 ? "grey lighten-4" : "grey lighten-3"' light>-->
        <!--<v-card-title>-->
            <!--<router-link :to="{name: 'groupDetailView', params: {name: item.groupName}}">{{ item.groupName }}</router-link>; {{item.createdAt | shortDate}}-->
        <!--</v-card-title>-->
        <!--<v-card-text>-->
            <!--<v-avatar-->
                    <!--:size="40"-->
                    <!--:tile="false"-->
                    <!--color="grey lighten-4"-->
            <!--&gt;-->
                <!--<img :src="!item.author.avatar ? $userService.getDefaultAvatar(item.author.username) : `/static/${item.author.avatar}`" alt="avatar">-->
            <!--</v-avatar>-->
            <!--<span class="ml-4">{{item.content}}</span>-->
        <!--</v-card-text>-->
        <!--<v-card-actions>-->
            <!--<v-btn>{{item.upvotedCount || 0}}; upvoted? {{item.isUpvoted}}</v-btn>-->
            <!--<v-btn flat :color="getUpvoteColor(item, true)" @click="upvote">Upvote</v-btn>-->
            <!--<v-btn flat :color="getUpvoteColor(item, false)" @click="downvote">Downvote</v-btn>-->

            <!--<v-btn @click="updating = !updating" color="orange" flat v-if="!readOnly">Update</v-btn>-->
            <!--<v-btn @click="deleteComment()" color="orange" flat v-if="!readOnly">Delete</v-btn>-->
            <!--<v-btn @click="item.showReplies = !item.showReplies" color="orange" flat v-if="item.reply && canReply && !readOnly">Reply-->
                <!--/ Show replies ({{item.replies.length}})-->
            <!--</v-btn>-->
        <!--</v-card-actions>-->
        <!--<div v-if="updating">-->
            <!--<v-btn :disabled="item.body === updatedBody || !updatedBody" @click="updateComment()" color="red" flat>-->
                <!--Update-->
            <!--</v-btn>-->
            <!--<v-text-field-->
                    <!--:rules="[ruleIsNotEmpty]"-->
                    <!--box-->
                    <!--label="Update"-->
                    <!--name="input-7-4"-->
                    <!--v-model="updatedBody"-->
            <!--&gt;</v-text-field>-->
        <!--</div>-->
        <!--<div class="ml-5" v-if="item.reply && item.showReplies">-->
            <!--<v-form lazy-validation ref="commentForm" v-model="item.reply.valid">-->
                <!--<v-text-field-->
                        <!--:rules="[ruleIsNotEmpty]"-->
                        <!--box-->
                        <!--label="Reply"-->
                        <!--name="input-7-4"-->
                        <!--v-model="item.reply.body"-->
                <!--&gt;</v-text-field>-->
                <!--<v-btn :disabled="!item.reply.valid" @click="replyToComment(item)">Add reply</v-btn>-->

</template>

<script>
    import validation from '../../mixins/validation';
    import {getUpvoteColor} from "../../utils/utils";
    import NewComment from "./NewComment";
    import BNotification from "buefy/src/components/notification/Notification";

    export default {
        name: 'Comment',
        components: {BNotification, NewComment},
        props: {
            item: {
                required: true,
                type: Object
            },
            index: {
                required: false,
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
            }
        },
        mixins: [validation],
        data() {
            return {
                updating: false,
                updatedBody: this.item.content
            }
        },
        mounted() {
            console.warn('body', this.item)
        },
        computed: {},
        methods: {
            replyToComment(comment) {
                this.$commentService.reply(comment.id, {content: comment.reply.body}, ({data}) => {
                    comment.replies.push(data);
                    comment.reply.body = '';
                })
            },
            getUpvoteColor: getUpvoteColor,
            deleteComment() {
                this.$commentService.delete(this.item.id, () => {
                    this.emitRemoveEvent(null);
                })
            },
            emitRemoveEvent(innerIndex) {
                console.log('emit remove event');
                this.$emit('removed', {outerIndex: this.index, innerIndex: innerIndex})
            },
            updateComment() {
                this.$commentService.update(this.item.id, {content: this.updatedBody}, ({data}) => {
                    this.item.content = data.content;
                    this.updating = false
                })
            },
            upvote() {
                if (this.item.isUpvoted === 1) {
                    this.item.upvotedCount -= 1;
                    this.clear();
                    return;
                }
                this.$commentService.upvote(this.item.id, () => {
                    this.item.isUpvoted = 1;
                    this.item.upvotedCount += 1;
                });
            },
            downvote() {
                if (this.item.isUpvoted === -1) {
                    this.item.upvotedCount += 1;
                    this.clear();
                    return;
                }
                this.$commentService.downvote(this.item.id, () => {
                    this.item.isUpvoted = -1;
                    this.item.upvotedCount -= 1;
                });
            },
            clear() {
                this.$commentService.clearVote(this.item.id, () => {
                    this.item.isUpvoted = null;
                });
            }
        }
    }
</script>

<style scoped>


</style>
