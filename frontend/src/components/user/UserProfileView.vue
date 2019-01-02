<template>
    <div class="is-fullwidth" v-if="user">
        <section class="hero is-primary is-small">
            <div class="hero-body has-text-centered">
                <p style="margin-bottom: 10px" class="has-text-warning"><strong><b-icon v-if="user.isAdmin" icon="crown" size="" type="is-warning"></b-icon><span style="margin: 0 5px">{{'account.is-admin'|t}}</span><b-icon v-if="user.isAdmin" icon="crown" size="" type="is-warning"></b-icon></strong></p>
                <p class="title">{{ user.username }}</p>

                <p class="subtitle"><span v-if="user.tagline">{{ user.tagline }}</span><span v-else>{{'account.default-tagline'|t }}</span></p>
                <nav class="level">
                    <figure class="image is-128x128 container">
                        <img class="is-rounded" :src="user.avatarUrl">
                    </figure>
                </nav>
                <p>{{'account.user-since'|t}} {{user.createdAt | since}}</p>
            </div>
        </section>
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
                        {{pagination}}

            <b-tabs v-model="activeTab" type="is-boxed" position="is-centered" @change="tabChange">
                <b-tab-item :label="$t('profile.upvoted-posts')" icon="checkbox-marked">
                    <div class="column is-8 is-offset-2">
                        <post-list :posts="upvotedPosts"></post-list>
                        <pagination :pagination="pagination[activeTab]" @change="handleChange"/>
                    </div>
                </b-tab-item>
                <b-tab-item :label="$t('profile.upvoted-comments')" icon="comment-check">
                    <div class="column is-8 is-offset-2">{{'profile.upvoted-comments' | t}}</div>
                </b-tab-item>
                <b-tab-item :label="$t('profile.posts')" icon="clipboard-text">
                    <div class="column is-8 is-offset-2">
                        <post-list :posts="posts"></post-list>
                        <pagination :pagination="pagination[activeTab]" @change="handleChange"/>
                    </div>
                </b-tab-item>
                <b-tab-item :label="$t('profile.comments')" icon="comment">
                    <div class="column is-8 is-offset-2">
                        <comment v-for="(item, index) in comments" :item="item" :index="index" :key="item.id" @removed="handleRemovedComment($event)" read-only></comment>
                    </div>
                </b-tab-item>
                <b-tab-item :label="$t('profile.groups')" icon="account-group">
                    <div class="column is-8 is-offset-2">
                        <group-list v-if="groups.moderatedGroups != undefined" :groups="groups.moderatedGroups"></group-list>
                    </div>
                </b-tab-item>
            </b-tabs>
        </section>
    </div>
</template>

<script>
    import {mapGetters} from 'vuex'
    import PostList from '../post/PostList'
    import Comment from '../post/Comment'
    import GroupList from '../group/GroupList'
    import Pagination from '../includes/Pagination'
    import {getPaginationFromResponse} from '../../utils/utils';

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
        components: {PostList, Comment, GroupList, Pagination},
        props: {
            username: {
                type: String,
                required: true
            }
        },
        data() {
            return {
                user: null,
                activeTab: 0,
                pagination: Object.assign({}, ...Object.entries(tabs).map(([a,b]) => ({ [b]: {} }))),
                posts: [],
                stats: {},
                groups: {},
                comments: [],
                upvotedPosts: [],
                visited: Object.assign({}, ...Object.entries(tabs).map(([a,b]) => ({ [b]: false }))) // mark every tab as not visited
            }
        },
        mounted() {
            this.$userService.getUserDetails(this.username, ({data}) => {
                this.user = data;
                this.user.avatarUrl = this.$userService.getAvatarUrl(this.user);
            });
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
        methods: {
            loadTabContent() {
                this.$toggleLoading(true);
                switch (this.activeTab) {
                    case tabNumbers.UPVOTED_POSTS:
                        this.$userService.getUpvotedPosts(this.username, this.pagination[this.activeTab], ({data}) => {
                            this.upvotedPosts = data.content;
                            this.pagination[this.activeTab] = getPaginationFromResponse(data);
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
                        this.$userService.getPosts(this.username, this.pagination[this.activeTab], ({data}) => {
                            this.pagination[this.activeTab] = getPaginationFromResponse(data);
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
                    // this.pagination = {};
                    this.loadTabContent();
                }
                this.updateQuery();
            },
            handleChange(pageNumber) {
                this.pagination[this.activeTab].page = pageNumber;
                this.loadTabContent();
                // this.getPosts({page: pageNumber})
            },
            updateQuery() {
                this.$router.replace({ name: 'userProfileView', query: { tab: tabsRev[this.activeTab] }});
            }
        }
    }
</script>

<style scoped>

</style>
