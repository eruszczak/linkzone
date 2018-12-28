<template>
    <section class="container">
        <div class="column is-8 is-offset-2 mt-2 mb-2">
            <div class="level">
                <div class="level-item has-text-centered">
                    <div>
                        <p class="heading">{{'profile.stat-posts'|t}}</p>
                        <p class="title">{{stats.postCount || 0 }}</p>
                    </div>
                </div>
                <div class="level-item has-text-centered">
                    <div>
                        <p class="heading">{{'profile.stat-comments'|t}}</p>
                        <p class="title">{{stats.commentCount || 0 }}</p>
                    </div>
                </div>
                <div class="level-item has-text-centered">
                    <div>
                        <p class="heading">{{'profile.stat-points'|t}}</p>
                        <p class="title">{{ (stats.commentPoints || 0) + (stats.postPoints || 0) }}</p>
                    </div>
                </div>
            </div>
        </div>
        <b-tabs v-model="activeTab" size="is-small" type="is-boxed" position="is-centered" @change="tabChange">
            <b-tab-item :label="$t('profile.upvoted-posts')" icon="google-photos">
                <div class="column is-8 is-offset-2">
                    <post-list :posts="upvotedPosts"></post-list>
                </div>
            </b-tab-item>
            <b-tab-item :label="$t('profile.upvoted-comments')" icon="library-music">
                <div class="column is-8 is-offset-2">{{'profile.upvoted-comments' | t}}</div>
            </b-tab-item>
            <b-tab-item :label="$t('profile.posts')" icon="video">
                <div class="column is-8 is-offset-2">
                    <post-list :posts="posts"></post-list>
                </div>
            </b-tab-item>
            <b-tab-item :label="$t('profile.comments')" icon="video">
                <div class="column is-8 is-offset-2">
                    <comment v-for="(item, index) in comments" :item="item" :index="index" :key="item.id" @removed="handleRemovedComment($event)" read-only></comment>
                </div>
            </b-tab-item>
            <b-tab-item :label="$t('profile.groups')" icon="video">
                <div class="column is-8 is-offset-2">
                    <group-list v-if="groups.moderatedGroups != undefined" :groups="groups.moderatedGroups"></group-list>
                </div>
            </b-tab-item>
            <!-- <b-tab-item :label="$t('profile.administrated-groups')" icon="video">
                <group-list v-if="groups.administratedGroups != undefined" :groups="groups.administratedGroups"></group-list>
            </b-tab-item> -->
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
        GROUPS: 4,
    };

    const tabs = {
        'upvoted-posts': tabNumbers.UPVOTED_POSTS,
        'upvoted-comments': tabNumbers.UPVOTED_COMMENTS,
        'posts': tabNumbers.POSTS,
        'comments': tabNumbers.COMMENTS,
        'groups': tabNumbers.GROUPS,
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
                stats: {},
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
            this.$userService.getUserStats(this.username, ({data}) => {
                this.stats = data;
            })
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
                    case tabNumbers.GROUPS:
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
