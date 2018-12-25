<template>
    <section class="section">
        <div class="container">
            <h1 class="title"><router-link :to="{name: 'postView', params: {name: post.groupName, postID: post.id}}">{{post.title}}</router-link></h1>
            <h2 class="subtitle">
                A simple container to divide your page into <strong>sections</strong>, like the one you're currently reading
            </h2>
            <div class="content ml-4">
                <div v-if="post.type === POST_TYPES.POST">
                    <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}" :source="post.content"></vue-markdown>
                </div>
                <div v-else-if="post.type === POST_TYPES.MEDIA">
                    <img :src="`/static/${post.content}`">
                </div>
                <div v-else-if="post.type === POST_TYPES.LINK">
                    <img v-if="checkIfImageUrl(post.content)" :src="post.content">
                    <iframe v-else-if="getYoutubeId(post.content)" width="560" height="315" :src="`//www.youtube.com/embed/${getYoutubeId(post.content)}`" frameborder="0" allowfullscreen></iframe>
                    <p v-else>{{post.content}}</p>
                </div>
            </div>
            <button class="button" :class="[getUpvoteColor(post, true)]" @click="upvote">
                <b-icon icon="arrow-up"></b-icon>
            </button>
            <button class="button">comments: {{post.commentCount}}</button>
            <button class="button" :class="[getUpvoteColor(post, false)]" @click="downvote">
                <b-icon icon="arrow-down"></b-icon>
            </button>
            <button class="button">{{post.upvotedCount || 0}}; upvoted? {{post.isUpvoted}}</button>
            <button class="button" :color="getUpvoteColor(post, true)" @click="upvote">Upvote</button>
            <button class="button" :color="getUpvoteColor(post, false)" @click="downvote">Downvote</button>
        </div>
    </section>
</template>

<script>
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown'
    import {checkIfImageUrl, getUpvoteColor, getYoutubeId} from "../../utils/utils";

    export default {
        name: "Post",
        components: {VueMarkdown},
        props: {
            post: {
                type: Object,
                required: true
            }
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
                    this.post.upvotedCount -= 1;
                    this.clear();
                    return;
                }
                this.$postService.upvote(this.post.id, () => {
                    this.post.isUpvoted = 1;
                    this.post.upvotedCount += 1;
                });
            },
            downvote() {
                if (this.post.isUpvoted === -1) {
                    this.post.upvotedCount += 1;
                    this.clear();
                    return;
                }
                this.$postService.downvote(this.post.id, () => {
                    this.post.isUpvoted = -1;
                    this.post.upvotedCount -= 1;
                });
            },
            clear() {
                this.$postService.clearVote(this.post.id, () => {
                    this.post.isUpvoted = null;
                });
            }
        }
    }
</script>

<style scoped>

</style>