<template>
    <section style="color:black" @click="clicked" :style="{cursor: preventRouter || !link ? 'default': 'pointer'}">
        <small style="display: flex;flex-direction: row;">{{'posts.added-by' | t}} <router-link style="margin: 0 3px" :to="{name: 'userProfileView', params: {username: post.author}}">{{post.author}}</router-link> {{'posts.in' | t}} <span style="margin: 0 5px" class="image is-24x24"><img class="is-rounded" :src="$groupService.getLogoUrl({logo: post.groupLogo, name: post.groupName})"></span> <router-link :to="{name: 'groupDetailView', params: {name: post.groupName}}">{{post.groupName}}</router-link>, {{post.createdAt | since}}</small>
        <p class="title is-4" style="margin-top:15px">
            <span v-if="post.type === POST_TYPES.LINK" @click="clicked2" style="cursor: pointer; color: #3273dc;">{{post.title}}</span>
            <span v-else>{{post.title}}</span>
        </p>
        <div class="content ml-4">
            <div v-if="post.type === POST_TYPES.POST">
                <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}" :source="post.content"></vue-markdown>
            </div>
            <div v-else-if="post.type === POST_TYPES.MEDIA">
                <image-fade :src="`/static/${post.content}`"></image-fade>
            </div>
            <div v-else-if="post.type === POST_TYPES.LINK">
                <image-fade v-if="checkIfImageUrl(post.content)" :src="post.content"></image-fade>
                <iframe v-else-if="getYoutubeId(post.content)" width="100%" height="500px" :src="`//www.youtube.com/embed/${getYoutubeId(post.content)}`" frameborder="0" allowfullscreen></iframe>
                <!-- <p v-else>{{post.content}}</p> -->
            </div>
        </div>
        <a class="button is-small" @click="upvote"><b-icon :type="getUpvoteColor(post, true)" icon="arrow-up"></b-icon></a>
        <b-tag type="is-white">{{post.upvotedCount || 0}}</b-tag>
        <a class="button is-small" @click="downvote"><b-icon :type="getUpvoteColor(post, false)" icon="arrow-down"></b-icon></a>
        <span class="ml-2" v-if="link"><span v-if="post.commentCount">{{post.commentCount}} {{'posts.comments'| t}}</span><span v-else>{{'comments.no-comments'|t}}</span></span>
        <!-- <span class="ml-2">{{'posts.save' | t}}</span> -->
        <!-- <span class="ml-2">Share</span> -->
    </section>
</template>

<script>
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown'
    import {checkIfImageUrl, getUpvoteColor, getYoutubeId} from "../../utils/utils";
    import ImageFade from '../includes/ImageFade'
    import debounce from 'lodash/debounce'

    export default {
        name: "Post",
        components: {VueMarkdown, ImageFade},
        props: {
            post: {
                type: Object,
                required: true
            },
            link: {
                type: Boolean,
                default: false
            },
        },
        data() {
            return {
                POST_TYPES,
                preventRouter: false
            }
        },
        methods: {
            getYoutubeId: getYoutubeId,
            checkIfImageUrl: checkIfImageUrl,
            getUpvoteColor: getUpvoteColor,
            upvote() {
                this.preventRouter = true;
                if (this.post.isUpvoted === 1) {
                    this.clear();
                    return;
                }
                this.$postService.upvote(this.post.id, ({data}) => {
                    this.post.isUpvoted = 1;
                    this.post.upvotedCount = data.counter;
                    this.preventRouter = false;
                });
            },
            downvote() {
                console.log('downvote')
                this.preventRouter = true;
                if (this.post.isUpvoted === -1) {
                    this.clear();
                    return;
                }
                this.$postService.downvote(this.post.id, ({data}) => {
                    this.post.isUpvoted = -1;
                    this.post.upvotedCount = data.counter;
                    this.preventRouter = false;
                });
            },
            clear() {
                this.$postService.clearVote(this.post.id, ({data}) => {
                    this.post.isUpvoted = null;
                    this.post.upvotedCount = data.counter;
                    this.preventRouter = false;
                });
            },
            clicked: debounce(function (text) {
                this._clicked(text);
            }, 500),
            _clicked: function(e) {
                if (this.preventRouter || !this.link) {
                    e.preventDefault();
                    return;
                }
                this.$router.push({name: 'postView', params: {name: this.post.groupName, postID: this.post.id, slug: this.post.slug}})
            },
            clicked2: function(e) {
                if (this.preventRouter || !this.link) {
                    e.preventDefault();
                    return;
                }
                this.preventRouter = true;
                Object.assign(document.createElement('a'), { target: '_blank', href: this.post.content}).click();
                this.preventRouter = false;
            }
        }
    }
</script>

<style scoped>
</style>