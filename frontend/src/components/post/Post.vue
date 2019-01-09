<template>
    <section>
        <small style="display: flex;flex-direction: row;">{{'posts.added-by' | t}} <router-link style="margin: 0 3px" :to="{name: 'userProfileView', params: {username: post.author}}">{{post.author}}</router-link> {{'posts.in' | t}} <span style="margin: 0 5px" class="image is-24x24"><img class="is-rounded" :src="$groupService.getLogoUrl({logo: post.groupLogo, name: post.groupName})"></span> <router-link :to="{name: 'groupDetailView', params: {name: post.groupName}}">{{post.groupName}}</router-link>, {{post.createdAt | since}}</small>
        <p class="title is-4" style="margin-top:15px">
            <router-link v-if="link" :to="{name: 'postView', params: {name: post.groupName, postID: post.id, slug: post.slug}}">{{post.title}}</router-link>
            <span v-else>{{post.title}}</span>
        </p>
        <!-- <h2 class="subtitle">
            A simple container to divide your page into <strong>sections</strong>, like the one you're currently reading
        </h2> -->
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
                <p v-else>{{post.content}}</p>
            </div>
        </div>
        <a class="button is-small" @click="upvote"><b-icon :type="getUpvoteColor(post, true)" icon="arrow-up"></b-icon></a>
        <b-tag type="is-white">{{post.upvotedCount || 0}}</b-tag>
        <a class="button is-small" @click="downvote"><b-icon :type="getUpvoteColor(post, false)" icon="arrow-down"></b-icon></a>
        <span class="ml-2" v-if="link">{{post.commentCount}} {{'posts.comments'| t}}</span>
        <!-- <span class="ml-2">{{'posts.save' | t}}</span> -->
        <!-- <span class="ml-2">Share</span> -->
    </section>
</template>

<script>
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown'
    import {checkIfImageUrl, getUpvoteColor, getYoutubeId} from "../../utils/utils";
    import ImageFade from '../includes/ImageFade'


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
        computed: {
            
        },
        data() {
            return {
                POST_TYPES
            }
        },
        methods: {
            getYoutubeId: getYoutubeId,
            checkIfImageUrl: checkIfImageUrl,
            getUpvoteColor: getUpvoteColor,
            upvote() {
                if (this.post.isUpvoted === 1) {
                    this.clear();
                    return;
                }
                this.$postService.upvote(this.post.id, ({data}) => {
                    this.post.isUpvoted = 1;
                    this.post.upvotedCount = data.counter;
                });
            },
            downvote() {
                if (this.post.isUpvoted === -1) {
                    this.clear();
                    return;
                }
                this.$postService.downvote(this.post.id, ({data}) => {
                    this.post.isUpvoted = -1;
                    this.post.upvotedCount = data.counter;
                });
            },
            clear() {
                this.$postService.clearVote(this.post.id, ({data}) => {
                    this.post.isUpvoted = null;
                    this.post.upvotedCount = data.counter;
                });
            }
        }
    }
</script>

<style scoped>
</style>