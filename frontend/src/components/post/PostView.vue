<template>
    <div v-if="post">
        {{post}}
        <v-btn :to="{name: 'postUpdateView', params: {id: post.id}}">update</v-btn>

        <p>can moderate: {{canModerate}}; isOwner: {{isOwner}}</p>
        <p>author: {{post.author}}</p>
        <p>group: {{post.groupName}}</p>
        <h1>{{post.title}}; {{post.type}};
            <v-btn @click="updating = !updating" v-if="isOwner || canModerate">update</v-btn>
        </h1>

        <div>
            <v-card v-if="post.type === POST_TYPES.POST">
                <h2>{{post.title}}</h2>
                <v-card-text>
                    <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}"
                                  :source="post.content"></vue-markdown>
                </v-card-text>
            </v-card>
            <v-card v-else-if="post.type === POST_TYPES.MEDIA">
                <h2>{{post.title}}</h2>
                <img :src="`/static/${post.content}`">
            </v-card>
            <v-card v-else-if="post.type === POST_TYPES.LINK">
                <h2>{{post.title}}</h2>
                <post-content :content="post.content"></post-content>
            </v-card>
        </div>

        <v-alert
                :value="post.locked"
                type="error"
        >
            <p>This thread has been locked. New comments cannot be posted.</p>
        </v-alert>

        <v-container fluid grid-list-sm>
            <h2>{{commentMetadata.total}} comments</h2>
            <v-layout row wrap>
                <v-flex>
                    <div v-if="!post.locked">
                        <v-form lazy-validation ref="commentForm" v-model="comment.valid">
                            <v-text-field
                                    :rules="[ruleIsNotEmpty]"
                                    box
                                    label="New comment"
                                    name="input-7-4"
                                    v-model="comment.body"
                            ></v-text-field>
                        </v-form>
                        <v-btn :disabled="!comment.valid" @click="addComment()">Add comment</v-btn>
                    </div>
                </v-flex>
                <v-flex :key="item.id" v-for="(item, index) in comments" xs12>
                    <comment :can-reply="!post.locked" :index="index" :item="item"
                             @removed="handleRemovedComment"></comment>
                </v-flex>
            </v-layout>
            <v-btn :disabled="commentMetadata.lastPage" @click="loadMoreComments()" flat>load more</v-btn>
        </v-container>
    </div>
</template>

<script>
    import {mapMutations} from 'vuex'
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown'
    import PostContent from '../includes/PostContent'

    import validation from '../../mixins/validation';
    import Comment from './Comment'
    import {checkIfImageUrl, getYoutubeId} from "../../utils/utils";

    export default {
        name: 'PostView',
        props: ['postID', 'name'],
        mixins: [validation],
        components: {Comment, VueMarkdown, PostContent},
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
                },
            }
        },
        mounted() {
            console.log('mounted PostView', this.postID, this.name);
            this.toggleLoading(true);
            this.$groupService.getGroupDetail(this.name, res => {
                this.toggleLoading(false);
                this.group = res.data;
                const isMod = this.$groupService.isMod(this.group.moderators);
                const isAdmin = this.$groupService.isAdmin(this.group.administrators);
                this.canModerate = isAdmin || isMod
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
            handleRemovedComment($event) {
                if ($event.innerIndex === null) {
                    this.comments.splice($event.outerIndex, 1);
                } else {
                    this.comments[$event.outerIndex].replies.splice($event.innerIndex, 1);
                }
                this.commentMetadata.total = Math.max(this.commentMetadata.total - 1, 0);
            },
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
                    this.comments.push(...data.content.map(comment => this.prepareComment(comment)))
                })
            },
            addComment() {
                if (this.$refs.commentForm.validate()) {
                    this.$commentService.create(this.postID, {content: this.comment.body}, ({data}) => {
                        // if sorting desc then push()
                        this.comments.unshift(this.prepareComment(data));
                        this.comment.body = '';
                        this.$refs.commentForm.reset()
                    })
                }
            },
            prepareComment(commentResponse) {
                commentResponse.reply = {
                    body: '',
                    valid: true
                };
                commentResponse.showReplies = false;
                return commentResponse
            },
        }
    }
</script>

<style scoped>


</style>
