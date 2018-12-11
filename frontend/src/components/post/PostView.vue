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
                <v-card-text>
                    <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}"
                                  :source="post.content"></vue-markdown>
                </v-card-text>
            </v-card>
            <v-card v-if="post.type === POST_TYPES.MEDIA">
                <img :src="`/static/${post.content}`">
            </v-card>
            <p v-else>{{post.content}}</p>
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

    import validation from '../../mixins/validation';
    import Comment from './Comment'

    export default {
        name: 'PostView',
        props: ['postID', 'name'],
        mixins: [validation],
        components: {Comment, VueMarkdown},
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
                // this.postCopy = JSON.parse(JSON.stringify(this.post))
                // this.isOwner = this.$userService.isOwner(this.post.author)
                // switch (this.post.type) {
                //     case POST_TYPES.POST:
                //         this.form.title = this.post.title;
                //         this.form.content = this.post.content;
                //         break;
                //     case POST_TYPES.LINK:
                //         this.formLink.title = this.post.title;
                //         this.formLink.content = this.post.content;
                //         break;
                //     case POST_TYPES.MEDIA:
                //         this.formMedia.title = this.post.title;
                //         this.formMedia.content = this.post.content;
                //         console.log('formMedia', this.formMedia)
                //         break;
                // }
            });
            this.loadMoreComments();
        },
        // watch: {
        //     post: {
        //         handler(val) {
        //             this.postCopy = JSON.parse(JSON.stringify(val))
        //         },
        //         deep: true
        //     }
        // },
        computed: {
            // postChanged () {
            //   return this.postCopy && !_.isEqual(this.post, this.postCopy)
            // },
            // isValid () {
            //     switch (this.post.type) {
            //         case POST_TYPES.POST:
            //             return this.formUpdated.valid && !_.isEqual(this.formUpdated, this.form);
            //         case POST_TYPES.LINK:
            //             return this.formLinkUpdated.valid && !_.isEqual(this.formLinkUpdated, this.formLink);
            //         case POST_TYPES.MEDIA:
            //             return this.formMediaUpdated.valid && !_.isEqual(this.formMediaUpdated, this.formMedia);
            //     }
            // }
        },
        methods: {
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
                    // TODO
                    // data.content.forEach((el) => {
                    //   sumReplies += el.replies.length;
                    // });
                    this.commentMetadata.total = data.totalElements + sumReplies;
                    this.comments.push(...data.content.map(comment => this.prepareComment(comment)))
                })
            },
            // update() {
            //     const data = {
            //         locked: this.post.locked
            //     };
            //     switch (this.post.type) {
            //         case POST_TYPES.POST:
            //             data.title = this.formUpdated.title;
            //             data.content = this.formUpdated.content;
            //             break;
            //         case POST_TYPES.LINK:
            //             data.title = this.formLinkUpdated.title;
            //             data.content = this.formLinkUpdated.content;
            //             break;
            //         case POST_TYPES.MEDIA:
            //             data.title = this.formMediaUpdated.title
            //             break;
            //     }
            //     console.log('updating post with', data)
            //     this.$postService.update(this.post, data, (data) => {
            //         this.post = data
            //         this.postCopy = JSON.parse(JSON.stringify(data))
            //         this.updating = false
            //         this.$message({
            //             message: 'updated',
            //             type: this.$toastColors.INFO
            //         })
            //     })
            // },
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
