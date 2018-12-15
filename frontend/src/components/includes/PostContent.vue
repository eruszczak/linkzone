<template>
    <div>
        <img v-if="checkIfImageUrl(content)" :src="content">
        <iframe v-else-if="getYoutubeId(content)" width="560" height="315" :src="`//www.youtube.com/embed/${getYoutubeId(content)}`" frameborder="0" allowfullscreen></iframe>
        <p v-else>{{content}}</p>
    </div>
</template>

<script>
    import {checkIfImageUrl, getYoutubeId} from "../../utils/utils";

    export default {
        name: "PostContent",
        props: {
            content: {
                type: String,
                required: true
            }
        },
        data() {
            return {};
        },
        computed: {
        },
        methods: {
            getYoutubeId: getYoutubeId,
            checkIfImageUrl: checkIfImageUrl
        }
    }
</script>

<style scoped>

</style>