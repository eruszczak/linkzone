<template>
    <div v-if="post">
        {{post}}
        <button class="button"><router-link :to="{name: 'postUpdateView', params: {id: post.id}}">update</router-link></button>

        <p>can moderate: {{canModerate}}; isOwner: {{isOwner}}</p>
        <p>author: {{post.author}}</p>
        <p>group: {{post.groupName}}</p>
        <h1>{{post.title}}; {{post.type}};
            <button class="button" @click="updating = !updating" v-if="isOwner || canModerate">update</button>
        </h1>

        <post :post="post"></post>

        <p class="title">{{post.commentCount}} comments</p>
        <b-notification v-if="post.locked" :closable="false">
            <p>{{'posts.locked' | t}}</p>
        </b-notification>
        <comments :comments="comments" @change="changeCommentCount" :is-locked="post.locked">
            <new-comment v-if="!post.locked" v-model="comment.body" @add="addComment"></new-comment>
        </comments>
    </div>
</template>

<script>
    import {mapMutations} from 'vuex'
    import {POST_TYPES} from "../../services/PostService";
    import Post from './Post'
    import validation from '../../mixins/validation';
    import Comment from './Comment'
    import Comments from './Comments'
    import {checkIfImageUrl, getYoutubeId, prepareComment} from "../../utils/utils";
    import BNotification from "buefy/src/components/notification/Notification";
    import NewComment from './NewComment';

    export default {
        name: 'PostView',
        props: ['postID', 'name'],
        mixins: [validation],
        components: {BNotification, Comment, Post, Comments, NewComment},
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
            this.toggleLoading(true);
            this.$groupService.getGroupDetail(this.name, res => {
                this.toggleLoading(false);
                this.group = res.data;
                // const isMod = this.$groupService.isMod(this.group.moderators);
                // const isAdmin = this.$groupService.isAdmin(this.group.administrators);
                // this.canModerate = isAdmin || isMod
            });
            this.$postService.getPost(this.postID, res => {
                this.post = res.data
            });
            this.loadMoreComments();
        },
        computed: {
        },
        methods: {
            checkIfImageUrl: checkIfImageUrl,
            getYoutubeId: getYoutubeId,
            ...mapMutations(['toggleLoading']),

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
