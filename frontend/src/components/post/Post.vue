<template>
    <v-card class="mb-4">
        <v-card-title primary-title>
            <div>
                <h3 class="headline mb-0"><router-link :to="{name: 'postView', params: {name: post.groupName, postID: post.id}}">{{post.title}}</router-link></h3>
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
            <v-btn flat color="orange">Share</v-btn>
            <v-btn flat color="orange">Explore</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown'
    import {checkIfImageUrl, getYoutubeId} from "../../utils/utils";

    export default {
        name: "Post",
        components: {VueMarkdown},
        props: {
            post: {
                type: Object,
                required: true
            }
        },
        data() {
            return {
                POST_TYPES
            }
        },
        methods: {
            getYoutubeId: getYoutubeId,
            checkIfImageUrl: checkIfImageUrl
        }
    }
</script>

<style scoped>

</style>