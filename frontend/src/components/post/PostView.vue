<template>
    <div class="container" v-if="post">
        <div class="column is-8 is-offset-2 mt-2">
            <nav class="breadcrumb" aria-label="breadcrumbs">
                <ul>
                    <li><router-link :to="{name: 'groupDetailView', params: {name: post.groupName}}">{{post.groupName}}</router-link></li>
                    <li class="is-active"><a href="#" aria-current="page">{{post.title}}</a></li>
                </ul>
            </nav>

            <moderator v-if="group && group.isModerator"></moderator>

            <div v-if="post.isCreator || (group && group.isModerator)" class="is-pulled-right">
                <router-link class="button is-warning is-small" :to="{name: 'postUpdateView', params: {id: post.id}}">{{'posts.update-post'|t}}</router-link>
                <button style="margin-left:8px;" class="button is-white is-small" @click="confirmDelete"><b-icon type="is-danger" icon="delete"></b-icon></button>
            </div>

            <post :post="post"></post>

            <p class="title is-5 mt-2">{{post.commentCount}} {{'posts.comments'| t}}</p>
            <b-notification v-if="post.locked" :closable="false" type="is-info">
                <p>{{'posts.locked' | t}}</p>
            </b-notification>
            <comments :comments="comments" @change="changeCommentCount" :is-locked="post.locked">
                <new-comment v-if="!post.locked" v-model="comment.body" @add="addComment"></new-comment>
            </comments>
        </div>
    </div>
</template>

<script>
    import {mapMutations} from 'vuex'
    import {POST_TYPES} from "../../services/PostService";
    import Post from './Post'
    import Comment from './Comment'
    import Comments from './Comments'
    import {checkIfImageUrl, getYoutubeId, prepareComment} from "../../utils/utils";
    import NewComment from './NewComment';
    import Moderator from '../group/Moderator';

    export default {
        name: 'PostView',
        props: {
            postID: {
                type: [String, Number],
                required: true
            },
            name: { // groupName
                type: String,
                required: true
            }
        },
        components: {Comment, Post, Comments, NewComment, Moderator},
        data() {
            return {
                POST_TYPES,
                post: null,
                comments: [],
                commentMetadata: {
                    lastPage: false,
                    pageNumber: 0,
                    total: 0
                },
                group: null,
                canModerate: false,
                isOwner: false,
                user: null,
                comment: {
                    body: '',
                    valid: true
                }

            }
        },
        mounted() {
            this.$groupService.getGroupDetail(this.name, ({data}) => {
                this.group = data;
            });
            this.$postService.getPost(this.postID, res => {
                this.post = res.data;
                this.$toggleLoading(false);
            });
            this.loadMoreComments();
        },
        methods: {
            checkIfImageUrl: checkIfImageUrl,
            getYoutubeId: getYoutubeId,
            confirmDelete() {
                this.$dialog.confirm({
                    title: this.$t('posts.remove-title'),
                    message: this.$t('posts.remove-message'),
                    confirmText: this.$t('confirm'),
                    cancelText: this.$t('cancel'),
                    type: 'is-danger',
                    hasIcon: true,
                    onConfirm: () => {
                        this.deletePost();
                    }
                })
            },
            deletePost() {
                this.$postService.delete(this.post.id, () => {
                    this.$info('posts.removed');
                    this.$router.push({
                        name: 'groupDetailView',
                        params: {
                            name: this.post.groupName
                        }
                    })
                })
            },
            loadMoreComments() {
                if (this.commentMetadata.lastPage) {
                    return;
                }
                this.$commentService.list(this.postID, {page: this.commentMetadata.pageNumber}, {}, ({data}) => {
                    this.commentMetadata.lastPage = data.last;
                    this.commentMetadata.pageNumber = data.number + 1;
                    this.comments.push(...data.content.map(comment => prepareComment(comment)))
                })
            },
            addComment() {
                this.$commentService.create(this.post.id, {content: this.comment.body}, ({data}) => {
                    this.comments.push(prepareComment(data));
                    this.comment.body = '';
                    this.changeCommentCount(1);
                })
            },
            changeCommentCount(val) {
                this.post.commentCount += val; 
            }
        }
    }
</script>

<style scoped>


</style>
