<template>
    <div>
        {{getCurrentForm()}}
        <b-tabs v-model="selectedTab" type="is-toggle" expanded>
            <b-tab-item :label="$t('posts.text')" icon="text">
                <b-field :type="{'is-danger': triedToSubmit && errors.has('title')}" :message="triedToSubmit ? errors.first('title') : null">
                    <b-input v-validate="'required'" name="title" icon="account" v-model="form.title" :placeholder="$t('posts.title')"></b-input>
                </b-field>
                <b-field :type="{'is-danger': triedToSubmit && errors.has('content')}" :message="triedToSubmit ? errors.first('content') : null">
                    <b-input maxlength="1000" name="content" type="textarea" v-model="form.content" :placeholder="$t('posts.content')"></b-input>
                </b-field>
                <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}" :source="form.content"></vue-markdown>
            </b-tab-item>

            <b-tab-item :label="$t('posts.media')" icon="image">
                <b-field :type="{'is-danger': triedToSubmit && errors.has('title2')}" :message="triedToSubmit ? errors.first('title2') : null">
                    <b-input v-validate="'required'" name="title2" icon="account" v-model="formMedia.title" :placeholder="$t('posts.title')"></b-input>
                </b-field>
                <img :src="`/static/${filename}`" style="max-width:300px;max-height:300px" v-if="filename">
                <file-input @formData="handleImageUpload" is-image v-model="formMedia.content" :show-error="triedToSubmit"></file-input>
            </b-tab-item>

            <b-tab-item :label="$t('posts.link')" icon="link">
                <b-field :type="{'is-danger': triedToSubmit && errors.has('title3')}" :message="triedToSubmit ? errors.first('title3') : null">
                    <b-input v-validate="'required'" name="title3" icon="account" v-model="formLink.title" :placeholder="$t('posts.title')"></b-input>
                </b-field>
                <b-field :type="{'is-danger': triedToSubmit && errors.has('link')}" :message="triedToSubmit ? errors.first('link') : null">
                    <b-input v-validate="'required'" name="link" icon="link" v-model="formLink.content" :placeholder="$t('posts.link')"></b-input>
                </b-field>
            </b-tab-item>
        </b-tabs>

        <button class="button" @click="submit()">{{ post ? $t('update') : $t('add') }}</button>
    </div>
</template>

<script>
    import validation from "../../mixins/validation";
    import {POST_TYPES} from "../../services/PostService";
    import VueMarkdown from 'vue-markdown'
    import FileInput from '../includes/FileInput'

    export default {
        name: "PostCreator",
        mixins: [validation],
        components: {
            VueMarkdown, FileInput
        },
        props: {
            post: {
                type: Object,
                required: false
            }
        },
        data() {
            return {
                POST_TYPES,
                selectedTab: 0,
                triedToSubmit: false,
                selectedForm: POST_TYPES.POST,
                filename: null,
                postLocked: false,
                form: {
                    title: '',
                    content: '',
                    valid: true
                },
                formLink: {
                    title: '',
                    content: '',
                    valid: true
                },
                formMedia: {
                    title: '',
                    content: '',
                    valid: true,
                    formData: null
                }
            }
        },
        mounted() {
            if (this.post) {
                this.selectedForm = this.post.type;
                const form = this.getCurrentForm();
                form.title = this.post.title;
                form.content = this.post.content;
                this.filename = this.post.content;
                this.postLocked = this.post.locked;
            }
        },
        methods: {
            submit() {
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        // this._submit();
                    }
                });
            },
            _submit() {
                //     const form = this.getCurrentForm();
                //     this.$emit('submit', {
                //         form: {
                //             title: form.title,
                //             content: form.content
                //         },
                //         selectedForm: this.selectedForm,
                //         postLocked: this.postLocked
                //     });
            },
            isTabDisabled(type) {
                return this.post && this.post.type !== type;
            },
            getCurrentForm() {
                switch (this.selectedForm) {
                    case POST_TYPES.POST:
                        return this.form;
                    case POST_TYPES.MEDIA:
                        return this.formMedia;
                    case POST_TYPES.LINK:
                        return this.formLink
                }
            },
            handleImageUpload(val) {
                this.$userService.uploadFile(val.value[0], ({data}) => {
                    this.filename = data.fileName;
                    this.$emit('upload', this.filename);
                });
            }
        }
    }
</script>

<style scoped>

</style>