<template>
    <div>
        <b-tabs v-model="selectedTab" type="is-toggle" expanded>
            <b-tab-item :label="$t('posts.text')" icon="text">
                <div class="column is-8 is-offset-2">
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('post.title')}" :message="triedToSubmit ? errors.first('post.title') : null">
                        <b-input v-validate="'required'" name="title" icon="account" v-model="form.title" :placeholder="$t('posts.title')" data-vv-scope="post"></b-input>
                    </b-field>
                    <b-field>
                        <b-input maxlength="1000" name="content" type="textarea" v-model="form.content" :placeholder="$t('posts.content')"></b-input>
                    </b-field>
                    <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}" :source="form.content"></vue-markdown>
                </div>
            </b-tab-item>

            <b-tab-item :label="$t('posts.media')" icon="image">
                <div class="column is-8 is-offset-2">
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('media.title')}" :message="triedToSubmit ? errors.first('media.title') : null">
                        <b-input v-validate="'required'" name="title" icon="account" v-model="formMedia.title" :placeholder="$t('posts.title')" data-vv-scope="media"></b-input>
                    </b-field>
                    <img :src="`/static/${filename}`" style="max-width:150px;max-height:150px" v-if="filename">
                    <file-input @formData="handleImageUpload" is-image v-model="formMedia.content" :show-error="triedToSubmit"></file-input>
                </div>
            </b-tab-item>

            <b-tab-item :label="$t('posts.link')" icon="link">
                <div class="column is-8 is-offset-2">
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('link.title')}" :message="triedToSubmit ? errors.first('link.title') : null">
                        <b-input v-validate="'required'" name="title" icon="account" v-model="formLink.title" :placeholder="$t('posts.title')" data-vv-scope="link"></b-input>
                    </b-field>
                    <b-field :type="{'is-danger': triedToSubmit && errors.has('link.link')}" :message="triedToSubmit ? errors.first('link.link') : null">
                        <b-input v-validate="'required|url'" name="link" icon="link" v-model="formLink.content" :placeholder="$t('posts.link')" data-vv-scope="link"></b-input>
                    </b-field>
                </div>
            </b-tab-item>
        </b-tabs>

        <div class="field" v-if="post">
            <b-checkbox v-model="postLocked">{{'posts.update-locked'|t}}</b-checkbox>
            <small>{{'posts.update-locked-hint' |t}}</small>
        </div>

        <button class="button is-primary" @click="submit()">{{ post ? $t('update') : $t('add') }}</button>
    </div>
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
            }
        },
        data() {
            return {
                POST_TYPES,
                selectedTab: 0,
                triedToSubmit: false,
                filename: null,
                postLocked: false,
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
        mounted() {
            if (this.post) {
                this.selectedTab = TABS_REV[this.post.type];
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
                const selectedType = this.getSelectedPostType();
                this.$validator.validateAll(this.getSelectedPostType().toLowerCase()).then((result) => {
                    if (result) {
                        // TODO its validated in PostCreateView too.
                        // if (this.getSelectedPostType() === POST_TYPES.MEDIA) {
                        //     // todo additionally check if image
                        //     return;
                        // }
                        this._submit();
                    }
                });
            },
            _submit() {
                const form = this.getCurrentForm();
                this.$emit('submit', {
                    form: {
                        title: form.title,
                        content: form.content
                    },
                    selectedForm: this.getSelectedPostType(),
                    postLocked: this.postLocked
                });
            },
            isTabDisabled(type) {
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
            handleImageUpload(val) {
                this.$userService.uploadFile(val.value[0], ({data}) => {
                    this.filename = data.fileName;
                    this.$emit('upload', this.filename);
                });
            },
            // async areFieldsValid(fieldNames) {
            //     return _.every
            //     // const validations = [];
            //     // fieldNames.forEach(field => {
            //     //     console.log(field)
            //     //     validations.push(this.$validator.validate(field))
            //     // });
            //     // this.$validator.validate(fieldNames[0])
            //     // console.log(fieldNames, fieldNames.map(field => this.$validator.validate(field)))
            //     // const results = Promise.all(validations);
            //     // return (await results).every(isValid => isValid);
            // }
        }
    }
</script>

<style scoped>

</style>