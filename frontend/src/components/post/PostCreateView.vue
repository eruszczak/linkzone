<template>
    <section class="section is-fullwidth">
        <section class="hero is-primary">
            <div class="hero-body">
                <div class="container">
                <h1 class="title">
                    {{'posts.create-post'|t}} <span v-if="selectedGroups.length">- {{ selectedGroups[0].name }}</span>
                </h1>
                </div>
            </div>
        </section>
        <div class="container">
            <div class="column is-8 is-offset-2">
                <b-field v-if="!disabled" class="mt-2 mb-2" :label="$t('posts.pick-group')" :type="{'is-danger': triedToSubmit && errors.first('group')}" :message="triedToSubmit ? errors.first('group') : null">
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
                        :disabled="disabled"
                        :closable="!disabled"
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
                <post-creator @submit="createPost" @upload="filename = $event"></post-creator>
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
            if (this.groupName) {
                this.$groupService.getGroupDetail(this.groupName, ({data}) => {
                    this.selectedGroups = [data];
                    this.$toggleLoading(false);
                    this.disabled = true;
                });
            } else {
                this.$toggleLoading(false);
            }
        },
        data() {
            return {
                filename: null,
                triedToSubmit: true,
                selectedGroups: [],
                groupOptions: [],
                disabled: false
            }
        },
        methods: {
            updateGroupOptions: debounce(function (text) {
                this.$groupService.getGroupList(null, text, ({data}) => {
                    if (data.content.length === 0) {
                        this.groupOptions = [];
                        return
                    }
                    this.groupOptions = data.content;

                }, () => {
                    this.groupOptions = [];
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
                console.log('craete post', value, this.selectedGroups[0], this.filename, value.selectedForm === POST_TYPES.MEDIA);
                if (value.selectedForm === POST_TYPES.MEDIA) {
                    if (!this.filename) {
                        return;
                    }
                    value.form.content = this.filename;
                }
                this.$postService.addPost(value.form, this.selectedGroups[0].name, value.selectedForm, ({data}) => {
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: this.selectedGroups[0].name,
                            postID: data.id,
                            slug: data.slug
                        }
                    })
                })
            }
        }
    }
</script>

<style scoped>

</style>
