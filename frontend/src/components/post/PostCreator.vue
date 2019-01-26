<template>
    <section>
        <b-tabs v-model="selectedTab" type="is-toggle" expanded>
            <b-tab-item :label="$t('posts.text')" icon="text" :disabled="isTabDisabled(POST_TYPES.POST)">
                <b-field :type="{'is-danger': triedToSubmit && errors.has('post.title')}" :message="triedToSubmit ? errors.first('post.title') : null">
                    <b-input v-validate="'required'" name="title" icon="account" v-model="form.title" :placeholder="$t('posts.title')" data-vv-scope="post"></b-input>
                </b-field>
                <b-field>
                    <b-input maxlength="1000" name="content" type="textarea" v-model="form.content" :placeholder="$t('posts.content')"></b-input>
                </b-field>
                <button class="button is-small" v-if="form.content" style="margin-bottom: 1em" @click="showPreview = !showPreview">{{'posts.show-preview'|t}}</button>
                <div class="content" v-if="form.content && showPreview">
                    <div style="margin-left:1em">
                        <p><a href="https://guides.github.com/features/mastering-markdown/" target="_blank">Markdown tutorial</a></p>
                        <div class="box">
                            <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}" :source="form.content"></vue-markdown>
                        </div>
                    </div>
                </div>
            </b-tab-item>

            <b-tab-item :label="$t('posts.media')" icon="image" :disabled="isTabDisabled(POST_TYPES.MEDIA)">
                <div v-if="!post || (post && post.type === POST_TYPES.MEDIA)">
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('media.title')}" :message="triedToSubmit ? errors.first('media.title') : null">
                        <b-input v-validate="'required'" name="title" icon="account" v-model="formMedia.title" :placeholder="$t('posts.title')" data-vv-scope="media"></b-input>
                    </b-field>
                    <img :src="`/static/${filename}`" style="max-width:150px;max-height:150px" v-if="filename">
                    <file-input :max-height="4000" :max-width="4000" :max-size="2000" @formData="handleImageUpload" :show-error="triedToSubmit"></file-input>
                </div>
            </b-tab-item>

            <b-tab-item :label="$t('posts.link')" icon="link" :disabled="isTabDisabled(POST_TYPES.LINK)">
                <div>
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('link.title')}" :message="triedToSubmit ? errors.first('link.title') : null">
                        <b-input v-validate="'required'" name="title" icon="account" v-model="formLink.title" :placeholder="$t('posts.title')" data-vv-scope="link"></b-input>
                    </b-field>
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('link.link')}" :message="triedToSubmit ? errors.first('link.link') : null">
                        <b-input v-validate="'required|url'" name="link" icon="link" v-model="formLink.content" :placeholder="$t('posts.link')" data-vv-scope="link"></b-input>
                    </b-field>
                </div>
            </b-tab-item>
        </b-tabs>

        <b-notification type="is-warning" :closable="false" v-if="isPostModerator">
            <div class="field">
                <b-checkbox v-model="postLocked">{{'posts.update-locked'|t}}</b-checkbox>
                <p><small>{{'posts.update-locked-hint' |t}}</small></p>
            </div>
        </b-notification>

        <div class="has-text-centered mt-2">
            <button class="button is-primary" @click="submit()">{{ post ? $t('update') : $t('add') }}</button>
        </div>

    </section>
</template>

<script>
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown';
    import FileInput from '../includes/FileInput';

    const TABS = {
        0: POST_TYPES.POST,
        1: POST_TYPES.MEDIA,
        2: POST_TYPES.LINK
    }

    const TABS_REV = {
        [POST_TYPES.POST]: 0,
        [POST_TYPES.MEDIA]: 1,
        [POST_TYPES.LINK]: 2
    }

    export default {
        name: "PostCreator",
        components: {
            VueMarkdown, FileInput
        },
        props: {
            post: {
                type: Object,
                required: false
            },
            isPostModerator: {
                type: Boolean,
                default: false
            },
            groupPostTypes: {
                type: Array,
                required: false
            }
        },
        data() {
            return {
                POST_TYPES,
                selectedTab: 0,
                triedToSubmit: false,
                filename: null,
                postLocked: false,
                showPreview: false,
                form: {
                    title: '',
                    content: ''
                },
                formLink: {
                    title: '',
                    content: ''
                },
                formMedia: {
                    title: '',
                    content: '',
                    formData: null
                }
            }
        },
        // watch: {
        //      groupPostTypes(newVal) {
        //         this.selectedTab = TABS_REV[this.groupPostTypes[0]]
        //      }
        // },
        mounted() {
            if (this.post) {
                this.selectedTab = TABS_REV[this.post.type];
                const form = this.getCurrentForm();
                form.title = this.post.title;
                form.content = this.post.content;
                if (this.post.type === POST_TYPES.MEDIA) {
                    this.filename = this.post.content;
                }
                this.postLocked = this.post.locked;
            } else if (this.groupPostTypes) {
                this.selectedTab = TABS_REV[this.groupPostTypes[0]]
            }
        },
        methods: {
            submit() {
                this.triedToSubmit = true;
                const selectedType = this.getSelectedPostType();
                this.$validator.validateAll(this.getSelectedPostType().toLowerCase()).then((result) => {
                    if (result) {
                        if (this.isMediaSelected()) {
                            if (!this.filename) {
                                this.$danger('no-file-selected');
                                return;
                            }
                        }
                        this._submit();
                    }
                });
            },
            _submit() {
                const form = this.getCurrentForm();
                this.$emit('submit', {
                    form: {
                        title: form.title,
                        content: this.isMediaSelected() ? this.filename : form.content
                    },
                    selectedForm: this.getSelectedPostType(),
                    postLocked: this.postLocked
                });
            },
            isMediaSelected() {
                return this.getSelectedPostType() === POST_TYPES.MEDIA;
            },
            isTabDisabled(type) {
                if (this.groupPostTypes) {
                    return this.groupPostTypes.filter(t => t === type).length === 0;
                }
                return this.post && this.post.type !== type;
            },
            getSelectedPostType() {
                return TABS[this.selectedTab]
            },
            getCurrentForm() {
                switch (this.getSelectedPostType()) {
                    case POST_TYPES.POST:
                        return this.form;
                    case POST_TYPES.MEDIA:
                        return this.formMedia;
                    case POST_TYPES.LINK:
                        return this.formLink
                }
            },
            handleImageUpload(form) {
                this.$userService.uploadFile(form, ({data}) => {
                    this.filename = data.fileName;
                    this.$emit('upload', this.filename);
                });
            }
        }
    }
</script>

<style scoped>

</style>