<template>
    <section class="section is-fullwidth" v-if="group">
        <div style="position: relative;">
            <div v-if="group.bannerUrl" :style="`background-image: url('/static/${group.bannerUrl}'); background-size: cover; background-position: center; height: 200px`"></div>
            <!-- <img src="https://styles.redditmedia.com/t5_2sqho/styles/bannerBackgroundImage_g0n4opey4io11.jpg?format=pjpg&s=69513e1a04e11f844755cd34902d86d7c03f4abe"/> -->
            <div v-else style="height: 100px; background-color: grey"></div>

            <div style="position: absolute;bottom: 15px">
                <div class="container">
                    <div class="column is-8 is-offset-2">
                        <div class="media" style="width: 200px;">
                            <figure class="media-left">
                                <p class="image is-48x48">
                                    <img class="is-rounded" :src="$groupService.getLogoUrl(group)">
                                </p>
                            </figure>
                            <div class="media-content" style="overflow: hidden;">
                                <p class="title is-4" style="margin-top:9px">{{group.name}}</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <section class="">
            <div class="container">
                <div class="column is-10 is-offset-1">
                    <nav class="breadcrumb" aria-label="breadcrumbs">
                        <ul>
                            <li><router-link :to="{name: 'groupListView'}">{{'groupListView' |t}}</router-link></li>
                            <li class="is-active"><a href="#" aria-current="page">{{group.name}}</a></li>
                        </ul>
                    </nav>
                    <moderator v-if="group.moderator"></moderator>

                    <div class="box is-hidden-desktop">
                        <p class="title is-4">{{group.name}} <span class="is-pulled-right"><sub-toggler :group="group"></sub-toggler></span></p>
                        <p class="subtitle is-6">{{group.description}}</p>

                        <small class="ml-2">{{group.createdAt | date}}</small>

                        <div class="field is-grouped">
                            <p class="control">
                                <router-link class="button" :to="{name: 'groupEditView', params: {name: group.name}}">{{'groups.update-group'|t}}</router-link>
                            </p>
                            <p class="control">
                                <router-link class="button" :to="{name: 'postCreateView', params: {groupName: group.name}}">{{'groups.add-post-in-group' | t}}</router-link>
                            </p>
                        </div>
                    </div>

                    <div class="columns">
                        <div class="column">
                            <!-- <p v-if="group.isAdministrator"> -->
                            <!-- </p> -->
                            <!-- <img :src="'/static/' + group.bannerUrl" v-if="group.bannerUrl"> -->

                            <post-list :is-moderator="group.isModerator" :posts="posts"></post-list>
                            <!-- <pagination :pagination="pagination" @change="handleChange"/> -->
                        </div>
                        <div class="column is-narrow is-hidden-touch mt-2">
                            <div class="card" style="width:300px">
                            <header class="card-header">
                                <p class="card-header-title">
                                {{group.name}}
                                </p>
                                <sub-toggler :group="group"></sub-toggler>
                            </header>
                            <div class="card-content">
                                <div class="content">
                                    <nav class="level">
                                        <div class="level-item has-text-centered">
                                            <div>
                                            <p class="heading">Subscribers</p>
                                            <p class="title is-5">3,456</p>
                                            </div>
                                        </div>
                                        <div class="level-item has-text-centered">
                                            <div>
                                            <p class="heading">Posts</p>
                                            <p class="title is-5">123</p>
                                            </div>
                                        </div>
                                    </nav>
                                    <nav class="level">
                                        <div class="level-item has-text-centered">
                                            <div>
                                            <p class="heading">Stworzona</p>
                                            <p class="title is-5">{{group.createdAt | date}}</p>
                                            </div>
                                        </div>
                                    </nav>
                                    <p>{{group.description}}</p>
                                </div>
                            </div>
                            <footer class="card-footer">
                                <router-link class="card-footer-item" :to="{name: 'postCreateView', params: {groupName: group.name}}">{{'groups.add-post-in-group' | t}}</router-link>
                            </footer>
                            <footer class="card-footer" v-if="group.isAdministrator">
                                <router-link class="card-footer-item" :to="{name: 'groupEditView', params: {name: group.name}}">{{'groups.update-group'|t}}</router-link>
                            </footer>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import Pagination from '../includes/Pagination'
    import PostList from '../post/PostList'
    import {getPaginationFromResponse} from '../../utils/utils';
    import SubToggler from './SubToggler';
    import Moderator from './Moderator'

    const POST_TYPES = {
        POST: 'POST', MEDIA: 'MEDIA', LINK: 'LINK'
    };

    export default {
        name: 'GroupDetailView',
        props: ['name'],
        components: {Pagination, PostList, SubToggler, Moderator},
        mounted() {
            this.init()
        },
        data() {
            return {
                group: null,
                posts: [],
                pagination: {},
                isSubbed: false,
                POST_TYPES: POST_TYPES,
                admins: [],
                mods: []
            }
        },
        watch: {
            '$route'(to, from) {
                this.init()
            },
        },
        methods: {
            init() {
                this.$groupService.getGroupDetail(this.name, res => {
                    this.$toggleLoading(false);
                    this.group = res.data;
                    this.mods = this.group.moderators.map(user => user.username);
                    this.admins = this.group.administrators.map(user => user.username);
                    console.log('mods', this.mods);
                    console.log('admins', this.admins);
                    this.getPosts({});
                    this.isSubbed = res.data.isSubbed
                })
            },
            handleChange(pageNumber) {
                this.getPosts({page: pageNumber})
            },
            getPosts(pagination = {}) {
                console.log(this.group);
                this.$groupService.getPosts(this.group, pagination, res => {
                    this.pagination = getPaginationFromResponse(res.data);
                    this.posts = res.data.content;
                    console.log('posty', this.posts)
                })
            }
        }
    }
</script>

<style scoped>

</style>