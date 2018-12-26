<template>
    <section class="section" v-if="!isLoading && group">
        <nav class="breadcrumb" aria-label="breadcrumbs">
            <ul>
                <li><router-link :to="{name: 'groupListView'}">{{'groupListView' |t}}</router-link></li>
                <li class="is-active"><a href="#" aria-current="page">{{group.name}}</a></li>
            </ul>
        </nav>
        <p v-if="group.isAdministrator">
            <a class="button" :to="{name: 'groupEditView', params: {name: group.name}}">update</a>
        </p>
        <img :src="'/static/' + group.bannerUrl" v-if="group.bannerUrl">
        <p class="title is-4">{{group.name}}</p>
        <p class="subtitle is-6">description: {{group.description}}</p>

        <sub-toggler :group="group"></sub-toggler>
        <small class="ml-2">{{group.createdAt | shortDate}}</small>

        <a class="button" :to="{name: 'postCreateView', params: {groupName: group.name}}">
            {{'add-post-in-group' | t}}
        </a>

        <post-list :is-moderator="group.isModerator" :posts="posts"></post-list>

        <pagination :pagination="pagination" @change="handleChange"/>
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
        computed: {
            ...mapGetters(['isLoading'])
        },
        watch: {
            '$route'(to, from) {
                this.init()
            },
        },
        methods: {
            ...mapMutations(['toggleLoading']),
            init() {
                this.toggleLoading(true);
                this.$groupService.getGroupDetail(this.name, res => {
                    this.toggleLoading(false);
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