<template>
    <div class="container">
        <div class="column is-8 is-offset-2">
            <!-- <b-notification :closable="false" type="is-default" size="small">
                <span v-if="!user">{{'from-all-groups' | t}}</span>
                <span v-else>{{'from-subbed-groups' | t}}</span>
            </b-notification> -->
            <post-list v-if="loaded" :posts="posts"></post-list>
            <div v-if="loaded" class="mt-2">
                <pagination :pagination="pagination" @change="handleChange"/>
            </div>
        </div>
    </div>
</template>

<script>
    import PostList from '@/components/post/PostList';
    import {getPaginationFromResponse} from './../utils/utils';
    import Pagination from './includes/Pagination'
    import {mapGetters} from 'vuex'

    export default {
        name: "MainView",
        components: {PostList, Pagination},
        data() {
            return {
                posts: [],
                pagination: {},
                loaded: false
            }
        },
        mounted() {
            this.getPosts();
        },
        computed: {
            ...mapGetters(['user'])
        },
        methods: {
            handleChange(pageNumber) {
                this.pagination.page = pageNumber;
                this.getPosts();
            },
            getPosts() {
                this.$toggleLoading(true);
                this.loaded = false;
                this.$postService.getTopPosts(this.pagination, ({data}) => {
                    this.posts = data.content;
                    this.loaded = true;
                    this.pagination = getPaginationFromResponse(data);
                    this.$toggleLoading(false);
                })
            }
        },
    }
</script>

<style scoped>

</style>