<template>
    <section class="section is-fullwidth">
        <section class="hero is-primary">
            <div class="hero-body">
                <div class="container">
                <h1 class="title">
                    {{'posts.create-post'|t}}
                </h1>
                </div>
            </div>
        </section>
        <section class="container">
            <b-field class="mt-2" :label="$t('posts.pick-group')" :type="{'is-danger': triedToSubmit && errors.first('group')}" :message="triedToSubmit ? errors.first('group') : null">
                <b-taginput
                    v-model="selectedGroups"
                    :data="groupOptions"
                    maxtags="1"
                    size="is-medium"
                    autocomplete
                    v-validate="'required'"
                    name="group"
                    field="name"
                    icon="account"
                    type="is-primary"
                    :placeholder="$t('posts.search-group')"
                    @typing="updateGroupOptions">
                    <template slot-scope="props">
                        <div class="media">
                            <div class="media-left"> -->
                                <img width="32" src="https://api.adorable.io/avatar/100/user10">
                            </div>
                            <div class="media-content">
                                {{ props.option.name }}; {{ props.option.createdAt | shortDate }}
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
            <post-creator @submit="createPost" @upload="filename = $event"></post-creator>
        </section>
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
            if (this.groupName) {
                // this.selectedGroup = this.groupName
            }
            this.$toggleLoading(false);
        },
        data() {
            return {
                filename: null,
                triedToSubmit: true,
                selectedGroups: [],
                groupOptions: []
            }
        },
        methods: {
            updateGroupOptions: debounce(function (text) {
                console.log(text)
                this.$groupService.getGroupList(null, text, res => {
                    this.isFetching = false;
                    if (res.data.content.length === 0) {
                        this.groupOptions = [];
                        return
                    }
                    this.groupOptions = res.data.content;

                }, () => {
                    this.groupOptions = [];
                    // this.isFetching = false;
                    // this.loading = false
                })
            }, 500),
            createPost(value) {
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._createPost(value);
                    }
                });
            },
            _createPost(value) {
                console.log('craete post', value, this.selectedGroup, this.filename, value.selectedForm === POST_TYPES.MEDIA);
                if (value.selectedForm === POST_TYPES.MEDIA) {
                    if (!this.filename) {
                        return;
                    }
                    value.form.content = this.filename;
                }
                this.$postService.addPost(value.form, this.selectedGroup.name, value.selectedForm, ({data}) => {
                    console.log(data.id);
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: this.selectedGroup.name,
                            postID: data.id
                        }
                    })
                })
            }
        }
    }
</script>

<style scoped>

</style>
