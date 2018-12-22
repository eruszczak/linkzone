<template>
    <v-card class="mb-4">
        <v-card-title primary-title>
            <div>
                <h3 class="headline mb-0"><router-link :to="{name: 'postView', params: {name: post.groupName, postID: post.id}}">{{post.title}}</router-link></h3>
                {{post.isUpvoted}}
            </div>
        </v-card-title>

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

        <v-card-actions>
            <v-btn>{{post.upvotedCount || 0}}; upvoted? {{post.isUpvoted}}</v-btn>
            <v-btn flat :color="getUpvoteColor(post, true)" @click="upvote">Upvote</v-btn>
            <v-btn flat :color="getUpvoteColor(post, false)" @click="downvote">Downvote</v-btn>
            <v-btn flat color="orange">Share</v-btn>
            <v-btn flat color="orange">Explore</v-btn>
        </v-card-actions>
    </v-card>
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