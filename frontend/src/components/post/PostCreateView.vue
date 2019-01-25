<template>
    <section class="section is-fullwidth">
        <section class="hero is-primary">
            <div class="hero-body">
                <div class="container">
                <h1 class="title">
                    <span v-if="!grpName">{{'posts.create-post'|t}}</span>
                    <div v-if="grpName">
                        <router-link :to="{name: 'groupDetailView', params: {name: grpName}}">
                            <div style="display: flex;flex-direction: row;">
                                <p style="margin-right:15px">{{'posts.create-post'|t}}:</p>
                                <p class="image is-48x48">
                                    <img class="is-rounded" :src="logo"/>
                                </p>
                                <p class="title is-4" style="margin-top:9px;margin-left:1rem; color:hsl(0, 0%, 21%)">{{grpName}}</p>
                            </div>
                        </router-link>
                    </div>
                </h1>
                </div>
            </div>
        </section>
        <div class="container">
            <div class="column is-8 is-offset-2 mt-2">
                <b-field v-if="!selectedGroupName" class="mt-2 mb-2" :label="$t('posts.pick-group')" :type="{'is-danger': triedToSubmit && errors.first('group')}" :message="triedToSubmit ? errors.first('group') : null">
                    <b-taginput
                        v-model="selectedGroups"
                        :data="groupOptions"
                        maxtags="1"
                        size="is-medium"
                        autocomplete
                        v-validate="'required'"
                        name="group"
                        field="name"
                        icon="account-group"
                        :closable="!selectedGroupName"
                        type="is-primary"
                        :placeholder="$t('posts.search-group')"
                        @typing="updateGroupOptions">
                        <template slot-scope="props">
                            <div class="media">
                                <div class="media-left">
                                    <img width="40" :src="$groupService.getLogoUrl(props.option)">
                                </div>
                                <div class="media-content">
                                    <strong>{{ props.option.name }}</strong>
                                    <br>
                                    <small>
                                        {{props.option.description}}
                                    </small>
                                </div>
                            </div>
                        </template>
                        <template slot="empty">
                            {{'empty'|t}}
                        </template>
                    </b-taginput>
                </b-field>
                <post-creator v-if="groupPostTypes" @submit="createPost" @upload="filename = $event" :group-post-types="groupPostTypes"></post-creator>
            </div>
        </div>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import PostCreator from './PostCreator'
    import {POST_TYPES} from "../../services/PostService";
    import debounce from 'lodash/debounce'

    export default {
        name: 'PostCreateView',
        components: {PostCreator},
        props: {
            groupName: {
                type: String,
                required: false
            }
        },
        mounted() {
            this.init();
        },
        watch: {
            '$route'(to, from) {
                this.init();
            },
        },
        data() {
            return {
                filename: null,
                triedToSubmit: true,
                selectedGroups: [],
                groupOptions: [],
                selectedGroupName: null
            }
        },
        computed: {
            logo() {
                return this.selectedGroups.length ? this.$groupService.getLogoUrl(this.selectedGroups[0]) : null;
            },
            grpName() {
                return this.selectedGroups.length ? this.selectedGroups[0].name : this.selectedGroupName;
            },
            groupPostTypes() {
                return this.selectedGroups.length ? this.selectedGroups[0].postTypes : null;
            }
        },
        methods: {
            init() {
                this.selectedGroups = [];
                this.selectedGroupName = this.groupName;
                if (this.groupName) {
                    this.$groupService.getGroupDetail(this.groupName, ({data}) => {
                        this.selectedGroups = [data];
                        this.$toggleLoading(false);
                    });
                } else {
                    this.$toggleLoading(false);
                }
            },
            updateGroupOptions: debounce(function (text) {
                this.$groupService.getGroupList({}, text, ({data}) => {
                    if (data.content.length === 0) {
                        this.groupOptions = [];
                        return;
                    }
                    this.groupOptions = data.content;
                }, () => {
                    this.groupOptions = [];
                })
            }, 500),
            createPost(value) {
                if (this.selectedGroups.length === 0) {
                    this.$danger('groups.no-group-selected');
                    this.selectedGroupName = null;
                    return;
                }
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._createPost(value);
                    }
                });
            },
            _createPost(value) {
                this.$postService.addPost(value.form, this.selectedGroups[0].name, value.selectedForm, ({data}) => {
                    this.$success('posts.added-post');
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: this.selectedGroups[0].name,
                            postID: data.id,
                            slug: data.slug
                        }
                    })
                }, ({data}) => {
                    if (data.message === 'post_type_not_allowed') {
                        this.$danger('errors.post_type_not_allowed')
                    }
                })
            }
        }
    }
</script>

<style scoped>

</style>
