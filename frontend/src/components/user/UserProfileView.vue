<template>
    <section class="section">
        <div class="level">
            <div class="level-item has-text-centered">
                <div>
                    <p class="heading">Tweets</p>
                    <p class="title">3,456</p>
                </div>
            </div>
            <div class="level-item has-text-centered">
                <div>
                    <p class="heading">Following</p>
                    <p class="title">123</p>
                </div>
            </div>
            <div class="level-item has-text-centered">
                <div>
                    <p class="heading">Followers</p>
                    <p class="title">456K</p>
                </div>
            </div>
            <div class="level-item has-text-centered">
                <div>
                    <p class="heading">Likes</p>
                    <p class="title">789</p>
                </div>
            </div>
        </div>
        <b-tabs v-model="activeTab" size="is-small" type="is-boxed" position="is-centered" @change="tabChange">
            <b-tab-item :label="$t('profile.upvoted-posts')" icon="google-photos">
                <post-list :posts="upvotedPosts"></post-list>
            </b-tab-item>
            <b-tab-item :label="$t('profile.upvoted-comments')" icon="library-music">
                {{'profile.upvoted-comments' | t}}
            </b-tab-item>
            <b-tab-item :label="$t('profile.posts')" icon="video">
                <post-list :posts="posts"></post-list>
            </b-tab-item>
            <b-tab-item :label="$t('profile.comments')" icon="video">
                <comment v-for="item in comments" :item="item" @removed="handleRemovedComment($event)" read-only></comment>
            </b-tab-item>
            <b-tab-item :label="$t('profile.moderated-groups')" icon="video">
                <group-list v-if="groups.moderatedGroups != undefined" :groups="groups.moderatedGroups"></group-list>
            </b-tab-item>
            <b-tab-item :label="$t('profile.administrated-groups')" icon="video">
                <group-list v-if="groups.administratedGroups != undefined" :groups="groups.administratedGroups"></group-list>
            </b-tab-item>
        </b-tabs>
    </section>
</template>

<script>
    import {mapGetters} from 'vuex'
    import PostList from '../post/PostList'
    import Comment from '../post/Comment'
    import GroupList from '../group/GroupList'

    const tabNumbers = {
        UPVOTED_POSTS: 0,
        UPVOTED_COMMENTS: 1,
        POSTS: 2,
        COMMENTS: 3,
        MOD_GROUPS: 4,
        ADMIN_GROUPS: 5
    };

    const tabs = {
        'upvoted-posts': tabNumbers.UPVOTED_POSTS,
        'upvoted-comments': tabNumbers.UPVOTED_COMMENTS,
        'posts': tabNumbers.POSTS,
        'comments': tabNumbers.COMMENTS,
        'mod-groups': tabNumbers.MOD_GROUPS,
        'admin-groups': tabNumbers.ADMIN_GROUPS
    };

    const tabsRev = Object.assign({}, ...Object.entries(tabs).map(([a,b]) => ({ [b]: a })));

    export default {
        name: "UserProfileView",
        components: {PostList, Comment, GroupList},
        props: {
            username: {
                type: String,
                required: true
            }
        },
        data() {
            return {
                activeTab: 0,
                text: 'Lorem ipsum dolor',
                posts: [],
                groups: {},
                comments: [],
                upvotedPosts: [],
                visited: Object.assign({}, ...Object.entries(tabs).map(([a,b]) => ({ [b]: false })))
            }
        },
        mounted() {
            const tab = this.$route.query['tab'];
            if (!tab) {
                this.updateQuery();
            } else {
                this.activeTab = tabs[tab] || 0;
            }
            this.loadTabContent();
        },
        computed: {
            ...mapGetters([])
        },
        methods: {
            loadTabContent() {
                this.$toggleLoading(true);
                switch (this.activeTab) {
                    case tabNumbers.UPVOTED_POSTS:
                        this.$userService.getUpvotedPosts(this.username, ({data}) => {
                            this.upvotedPosts = data.content;
                            this.$toggleLoading(false);
                        });
                        break;
                    case tabNumbers.UPVOTED_COMMENTS:
                        this.$userService.getComments(this.username, ({data}) => {
                            this.comments = data.content;
                            this.$toggleLoading(false);
                        });
                        break;
                    case tabNumbers.POSTS:
                        this.$userService.getPosts(this.username, ({data}) => {
                            this.posts = data.content;
                            this.$toggleLoading(false);
                        });
                        break;
                    case tabNumbers.COMMENTS:
                        this.$userService.getComments(this.username, ({data}) => {
                            this.comments = data.content;
                            this.$toggleLoading(false);
                        });
                        break;
                    case tabNumbers.MOD_GROUPS:
                        this.getGroups();
                        break;
                    case tabNumbers.ADMIN_GROUPS:
                        this.getGroups();
                        break;
                }
                this.visited[this.activeTab] = true;
            },
            getGroups() {
                this.$userService.getGroupInfo(this.username, ({data}) => {
                    this.groups = data;
                    this.$toggleLoading(false);
                });
            },
            tabChange(index) {
                if (!this.visited[this.activeTab]) {
                    this.loadTabContent();
                    this.updateQuery();
                }
            },
            updateQuery() {
                this.$router.replace({ name: 'userProfileView', query: { tab: tabsRev[this.activeTab] }});
            }
        }
    }
</script>

<style scoped>

</style>
