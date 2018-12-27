<template>
    <div class="container" v-if="post">
        <div class="column is-8 is-offset-2">
            <nav class="breadcrumb" aria-label="breadcrumbs">
                <ul>
                    <li><router-link :to="{name: 'groupDetailView', params: {name: post.groupName}}">{{post.groupName}}</router-link></li>
                    <li class="is-active"><a href="#" aria-current="page">{{post.title}}</a></li>
                </ul>
            </nav>

            <button class="button"><router-link :to="{name: 'postUpdateView', params: {id: post.id}}">update</router-link></button>

            <!-- <p>can moderate: {{canModerate}}; isOwner: {{isOwner}}</p>
            <p>author: {{post.author}}</p>
            <p>group: {{post.groupName}}</p> -->
            <!-- <button class="button" @click="updating = !updating" v-if="isOwner || canModerate">update</button> -->

            <post :post="post"></post>

            <p class="title is-4">{{post.commentCount}} comments</p>
            <b-notification v-if="post.locked" :closable="false">
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
        components: {Comment, Post, Comments, NewComment},
        data() {
            return {
                POST_TYPES,
                post: {},
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
            console.log('mounted PostView', this.postID, this.name);
            this.$groupService.getGroupDetail(this.name, res => {
                // this.$toggleLoading(false);
                this.group = res.data;
                // const isMod = this.$groupService.isMod(this.group.moderators);
                // const isAdmin = this.$groupService.isAdmin(this.group.administrators);
                // this.canModerate = isAdmin || isMod
            });
            this.$postService.getPost(this.postID, res => {
                this.post = res.data;
                this.$toggleLoading(false);
            });
            this.loadMoreComments();
        },
        computed: {
        },
        methods: {
            checkIfImageUrl: checkIfImageUrl,
            getYoutubeId: getYoutubeId,
            loadMoreComments() {
                if (this.commentMetadata.lastPage) {
                    return;
                }
                this.$commentService.list(this.postID, {page: this.commentMetadata.pageNumber}, {}, ({data}) => {
                    console.log('commenty', data);
                    this.commentMetadata.lastPage = data.last;
                    this.commentMetadata.pageNumber = data.number + 1;
                    console.log(data.content);
                    let sumReplies = 0;
                    // TODO count on the backend
                    this.commentMetadata.total = data.totalElements + sumReplies;
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
