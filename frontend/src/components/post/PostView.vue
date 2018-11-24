<template>
    <div v-if="post">
        {{post}}
        <p>can moderate: {{canModerate}}; isOwner: {{isOwner}}</p>
        <p>author: {{post.author}}</p>
        <p>group: {{post.groupName}}</p>
        <p>updated form is valid: {{isValid}}</p>
        <p>post is changed: {{postChanged}}</p>
        <p>post copy: {{postCopy}}</p>
        <h1>{{post.title}}; {{post.type}}; <v-btn v-if="isOwner || canModerate" @click="updating = !updating">update</v-btn></h1>

        <div v-show="!updating">
            <v-card v-if="post.type === POST_TYPES.POST">
                <v-card-text>
                    <vue-markdown :source="post.content" :anchorAttributes="{target: '_blank', rel: 'nofollow'}"></vue-markdown>
                </v-card-text>
            </v-card>
            <v-card v-if="post.type === POST_TYPES.MEDIA">
              <img :src="`/static/${post.content}`">
            </v-card>
            <p v-else>{{post.content}}</p>
        </div>

        <div v-show="updating">
                <div v-if="post.type === POST_TYPES.POST">
                <form-post-text :form="form" @change="formUpdated = $event"></form-post-text>
            </div>
            <div v-if="post.type === POST_TYPES.LINK">
                <form-post-link :form="formLink" @change="formLink = $event"></form-post-link>
            </div>
            <div v-if="post.type === POST_TYPES.MEDIA" @change="formMediaUpdated = $event">
                <form-post-media></form-post-media>
            </div>
            <v-checkbox v-if="canModerate"
                    label="Locked??"
                    v-model="post.locked"
            ></v-checkbox>
            <v-btn color="success" :disabled="!isValid && !postChanged" @click="update()">Update</v-btn>
            <v-btn color="red">Remove</v-btn>
        </div>

        <v-alert
                :value="post.locked"
                type="error"
        >
            <p>This thread has been locked by the moderators of /g/{{name}} New comments cannot be posted</p>
        </v-alert>

        <v-container grid-list-sm fluid>
          <h2>{{commentMetadata.total}} comments</h2>
          <v-layout row wrap>
            <v-flex>
              <v-form v-model="comment.valid" lazy-validation ref="commentForm">
                  <v-text-field
                          box
                          name="input-7-4"
                          label="New comment"
                          :rules="[ruleIsNotEmpty]"
                          v-model="comment.body"
                  ></v-text-field>
              </v-form>
              <v-btn @click="addComment()" :disabled="!comment.valid">Add comment</v-btn>
            </v-flex>
            <v-flex xs12 v-for="(item, index) in comments" :key="item.id">
              <comment :item="item" :index="index" @removed="handleRemovedComment($event)"></comment>
            </v-flex>
          </v-layout>
          <v-btn flat :disabled="commentMetadata.lastPage" @click="loadMoreComments()">load more</v-btn>
        </v-container>
</div>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
    import FormPostText from '../includes/post/FormPostText'
    import FormPostLink from '../includes/post/FormPostLink'
    import FormPostMedia from '../includes/post/FormPostMedia'
    import VueMarkdown from 'vue-markdown'
    import {POST_TYPES} from "../../services/PostService";
    import _ from 'lodash';
    import validation from '../../mixins/validation';
    import Comment from './Comment'

    export default {
        name: 'PostView',
        props: ['postID', 'name'],
        mixins: [validation],
        components: {FormPostText, FormPostLink, FormPostMedia, VueMarkdown, Comment},
        data () {
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
                updating: false,
                user: null,
                formUpdated: {},
                formLinkUpdated: {},
                formMediaUpdated: {},
                postCopy: null,
                comment: {
                    body: '',
                    valid: true
                },
                form: {},
                formLink: {},
                formMedia: {}
            }
        },
        mounted () {
            console.log('mounted PostView', this.postID, this.name)
            this.toggleLoading(true)
            this.$groupService.getGroupDetail(this.name, res => {
                this.toggleLoading(false)
                this.group = res.data
                const isMod = this.$groupService.isMod(this.group.moderators)
                const isAdmin = this.$groupService.isAdmin(this.group.administrators);
                this.canModerate = isAdmin || isMod
            })
            this.$postService.getPost(this.postID, res => {
                this.post = res.data
                this.postCopy = JSON.parse(JSON.stringify(this.post))
                this.isOwner = this.$userService.isOwner(this.post.author)
                switch (this.post.type) {
                    case POST_TYPES.POST:
                        this.form.title = this.post.title;
                        this.form.content = this.post.content;
                        break;
                    case POST_TYPES.LINK:
                        this.formLink.title = this.post.title;
                        this.formLink.link = this.post.content;
                        break;
                    case POST_TYPES.MEDIA:
                        break;
                }

                this.form = {
                        title: this.post.title,
                        content: this.post.content,
                        titleError: false,
                        contentError: false,
                        valid: true
                }
                this.formLink = {
                        title: this.post.title,
                        link: this.post.content,
                        valid: true
                }
                this.formMedia = {
                        fileList: [],
                        valid: true
                }
            })
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
            postChanged () {
              return this.postCopy && !_.isEqual(this.post, this.postCopy)
            },
            isValid () {
                switch (this.post.type) {
                    case POST_TYPES.POST:
                        return this.formUpdated.valid && !_.isEqual(this.formUpdated, this.form);
                    case POST_TYPES.LINK:
                        return this.formLinkUpdated.valid && !_.isEqual(this.formLinkUpdated, this.formLink);
                        break;
                    case POST_TYPES.MEDIA:
                        return this.formMediaUpdated.valid && !_.isEqual(this.formMediaUpdated, this.formMedia);
                    default:
                        return false;
                }
            }
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
                  console.log('commenty', data)
                  this.commentMetadata.lastPage = data.last;
                  this.commentMetadata.pageNumber = data.number + 1;
                  console.log(data.content)
                  let sumReplies = 0;
                  // TODO
                  // data.content.forEach((el) => {
                  //   sumReplies += el.replies.length;
                  // });
                  this.commentMetadata.total = data.totalElements + sumReplies;
                  this.comments.push(...data.content.map(comment => this.prepareComment(comment)))
              })
            },
            update() {
                const data = {
                    locked: this.post.locked
                };
                switch (this.post.type) {
                    case POST_TYPES.POST:
                        data.title = this.formUpdated.title;
                        data.content = this.formUpdated.content;
                        break;
                    case POST_TYPES.LINK:
                        data.title = this.formLinkUpdated.title;
                        data.content = this.formLinkUpdated.content;
                        break;
                    case POST_TYPES.MEDIA:
                        break;
                }
                console.log('updating post with', data)
                this.$postService.update(this.post, data, (data) => {
                    this.post = data
                    this.postCopy = JSON.parse(JSON.stringify(data))
                    this.updating = false
                    this.$message({
                        message: 'updated',
                        type: this.$toastColors.INFO
                    })
                })
            },
            addComment () {
                if (this.$refs.commentForm.validate()) {
                    this.$commentService.create(this.postID, {content: this.comment.body}, ({data}) => {
                        // if sorting desc then push()
                        this.comments.unshift(this.prepareComment(data))
                        this.comment.body = ''
                        this.$refs.commentForm.reset()
                    })
                }
            },
            prepareComment(commentResponse) {
                commentResponse.reply = {
                    body: '',
                    valid: true
                }
                commentResponse.showReplies = false
                return commentResponse
            },
        }
    }
</script>

<style scoped>



</style>
