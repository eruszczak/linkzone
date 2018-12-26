<template>
    <section class="section is-fullwidth" v-if="group">
        <img src="https://styles.redditmedia.com/t5_2sqho/styles/bannerBackgroundImage_g0n4opey4io11.jpg?format=pjpg&s=69513e1a04e11f844755cd34902d86d7c03f4abe"/>
        <section class="container">
            <nav class="breadcrumb" aria-label="breadcrumbs">
                <ul>
                    <li><router-link :to="{name: 'groupListView'}">{{'groupListView' |t}}</router-link></li>
                    <li class="is-active"><a href="#" aria-current="page">{{group.name}}</a></li>
                </ul>
            </nav>
            <!-- <p v-if="group.isAdministrator"> -->
                <router-link class="button" :to="{name: 'groupEditView', params: {name: group.name}}">update</router-link>
            <!-- </p> -->
            <img :src="'/static/' + group.bannerUrl" v-if="group.bannerUrl">
            <p class="title is-4">{{group.name}}</p>
            <p class="subtitle is-6">description: {{group.description}}</p>

            <sub-toggler :group="group"></sub-toggler>
            <small class="ml-2">{{group.createdAt}}; {{group.createdAt | shortDate}}</small>

            <router-link class="button" :to="{name: 'postCreateView', params: {groupName: group.name}}">
                {{'add-post-in-group' | t}}
            </router-link>

            <post-list :is-moderator="group.isModerator" :posts="posts"></post-list>

            <pagination :pagination="pagination" @change="handleChange"/>
        </section>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import Pagination from '../includes/Pagination'
    import PostList from '../post/PostList'
    import {getPaginationFromResponse} from '../../utils/utils';
    import SubToggler from './SubToggler';

    const POST_TYPES = {
        POST: 'POST', MEDIA: 'MEDIA', LINK: 'LINK'
    };

    export default {
        name: 'GroupDetailView',
        props: ['name'],
        components: {Pagination, PostList, SubToggler},
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