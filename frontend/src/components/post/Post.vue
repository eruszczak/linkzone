<template>
    <section style="color:black" @click="clicked" :style="{cursor: !link ? 'default': 'pointer'}">
        <small style="display: flex;flex-direction: row;">{{'posts.added-by' | t}}
            <a @click.stop="$router.push({name: 'userProfileView', params: {username: post.author}})" style="margin: 0 3px">{{post.author}}</a>
            {{'posts.in' | t}} <span style="margin: 0 5px" class="image is-24x24"><img class="is-rounded" :src="$groupService.getLogoUrl({logo: post.groupLogo, name: post.groupName})"></span>
            <a @click.stop="$router.push({name: 'groupDetailView', params: {name: post.groupName}})">{{post.groupName}}</a>, {{post.createdAt | since}}</small>
        <p class="title is-4" style="margin-top:15px">
            <span v-if="post.type === POST_TYPES.LINK" @click="openInTab(post.content)" class="link">
                {{post.title}}  
                <p @click.stop="openInTab(post.content)" class="link" style="font-weight:normal;padding-top:5px">{{truncate(stripUrl(post.content), {length: 30})}}</p>
            </span>
            <span v-else>{{post.title}}</span>
        </p>
        <hr/>
        <div class="content ml-4">
            <div v-if="post.type === POST_TYPES.POST">
                <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}" :source="post.content"></vue-markdown>
            </div>
            <div v-else-if="post.type === POST_TYPES.MEDIA">
                <image-fade :src="`${post.content}`"></image-fade>
            </div>
            <div v-else-if="post.type === POST_TYPES.LINK">
                <image-fade v-if="checkIfImageUrl(post.content)" :src="post.content"></image-fade>
                <iframe v-else-if="getYoutubeId(post.content)" width="100%" height="500px" :src="`//www.youtube.com/embed/${getYoutubeId(post.content)}`" frameborder="0" allowfullscreen></iframe>
            </div>
        </div>
        <a class="button is-small" @click.stop="upvote"><b-icon :type="getUpvoteColor(post, true)" icon="arrow-up"></b-icon></a>
        <b-tag type="is-white">{{post.upvotedCount || 0}}</b-tag>
        <a class="button is-small" @click.stop="downvote"><b-icon :type="getUpvoteColor(post, false)" icon="arrow-down"></b-icon></a>
        <span class="ml-2" v-if="link"><span v-if="post.commentCount">{{post.commentCount}} {{'posts.comments'| t}}</span><span v-else>{{'comments.no-comments'|t}}</span></span>
    </section>
</template>

<script>
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown'
    import {checkIfImageUrl, getUpvoteColor, getYoutubeId} from "../../utils/utils";
    import ImageFade from '../includes/ImageFade'
    import debounce from 'lodash/debounce'
    import {truncate} from 'lodash';

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
                POST_TYPES
            }
        },
        methods: {
            truncate: truncate,
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
                console.log('downvote')
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
            },
            stripUrl(url) {
                return url.replace(/(^\w+:|^)\/\//, '').replace('www.', '');
            },
            clicked() {
                this.$router.push({name: 'postView', params: {name: this.post.groupName, postID: this.post.id, slug: this.post.slug}})
            },
            openInTab(href) {
                Object.assign(document.createElement('a'), { target: '_blank', href: href}).click();
            }
        }
    }
</script>

<style scoped>
</style>