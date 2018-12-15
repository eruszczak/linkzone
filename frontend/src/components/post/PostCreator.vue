<template>
    <div>
        <v-tabs
                grow
                icons-and-text
                tabs
                v-model="selectedForm"
        >
            <v-tabs-slider color="yellow"></v-tabs-slider>

            <v-tab :disabled="isTabDisabled(POST_TYPES.POST)" :href="`#${POST_TYPES.POST}`">
                Post
                <v-icon>phone</v-icon>
            </v-tab>

            <v-tab :disabled="isTabDisabled(POST_TYPES.MEDIA)" :href="`#${POST_TYPES.MEDIA}`">
                Image
                <v-icon>favorite</v-icon>
            </v-tab>

            <v-tab :disabled="isTabDisabled(POST_TYPES.LINK)" :href="`#${POST_TYPES.LINK}`">
                Link
                <v-icon>account_box</v-icon>
            </v-tab>
            <v-tab-item
                    :id="`${POST_TYPES.POST}`"
            >
                <v-card flat>
                    <v-card-text>
                        <v-form :ref="`form-${POST_TYPES.POST}`" lazy-validation v-model="form.valid">
                            <v-text-field
                                    :rules="[ruleIsNotEmpty]"
                                    box
                                    label="Title"
                                    v-model="form.title"
                            ></v-text-field>
                            <v-container fluid grid-list-md>
                                <v-layout row wrap>
                                    <v-flex xs7>
                                        <v-textarea
                                                :rules="[ruleIsNotEmpty]"
                                                box
                                                label="Content"
                                                v-model="form.content"
                                        ></v-textarea>
                                    </v-flex>
                                    <v-flex xs5>
                                        <v-card>
                                            <v-card-text>
                                                <vue-markdown :anchorAttributes="{target: '_blank', rel: 'nofollow'}"
                                                              :source="form.content"></vue-markdown>
                                            </v-card-text>
                                        </v-card>
                                    </v-flex>
                                </v-layout>
                            </v-container>
                        </v-form>
                        <!--<p>This editor lets you to use markdown. Here you can learn more about markdown syntax:</p>-->
                        <!--<ul>-->
                        <!--<li><a href="https://www.markdowntutorial.com" target="_blank">https://www.markdowntutorial.com/</a></li>-->
                        <!--<li><a href="https://guides.github.com/features/mastering-markdown" target="_blank">https://guides.github.com/features/mastering-markdown</a></li>-->
                        <!--</ul>-->
                    </v-card-text>
                </v-card>
            </v-tab-item>

            <v-tab-item
                    :id="`${POST_TYPES.MEDIA}`"
            >
                <v-form :ref="`form-${POST_TYPES.MEDIA}`" lazy-validation v-model="formMedia.valid">
                    <v-text-field
                            :rules="[ruleIsNotEmpty]"
                            box
                            label="Title"
                            v-model="formMedia.title"
                    ></v-text-field>
                    <img :src="`/static/${filename}`" style="max-width:300px" v-if="filename">
                    <file-input @formData="handleImageUpload" is-image v-model="formMedia.content"></file-input>
                </v-form>
            </v-tab-item>

            <v-tab-item
                    :id="`${POST_TYPES.LINK}`"
            >
                <v-form :ref="`form-${POST_TYPES.LINK}`" lazy-validation v-model="formLink.valid">
                    <v-text-field
                            :rules="[ruleIsNotEmpty]"
                            box
                            label="Title"
                            v-model="formLink.title"
                    ></v-text-field>
                    <v-text-field
                            :rules="[ruleIsNotEmpty, ruleUrl]"
                            box
                            label="Link"
                            name="input-7-4"
                            v-model="formLink.content"
                    ></v-text-field>
                </v-form>
            </v-tab-item>
        </v-tabs>

        <v-checkbox label="Lock post"
                    v-if="post"
                    v-model="postLocked"
        ></v-checkbox>

        <v-btn @click="submit()" color="orange" flat>{{post ? 'Update' : 'Create'}}</v-btn>
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
                if (this.$refs[`form-${this.selectedForm}`].validate()) {
                    const form = this.getCurrentForm();
                    this.$emit('submit', {
                        form: {
                            title: form.title,
                            content: form.content
                        },
                        selectedForm: this.selectedForm,
                        postLocked: this.postLocked
                    });
                }
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