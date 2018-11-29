<template>
    <div>
    <v-tabs
            tabs
            grow
            icons-and-text
            v-model="selectedForm"
    >
        <v-tabs-slider color="yellow"></v-tabs-slider>

        <v-tab :href="`#${POST_TYPES.POST}`" :disabled="true">
            Post
            <v-icon>phone</v-icon>
        </v-tab>

        <v-tab :href="`#${POST_TYPES.MEDIA}`">
            Image
            <v-icon>favorite</v-icon>
        </v-tab>

        <v-tab :href="`#${POST_TYPES.LINK}`">
            Link
            <v-icon>account_box</v-icon>
        </v-tab>
        <v-tab-item
                :id="`${POST_TYPES.POST}`"
        >
            <v-card flat>
                <v-card-text>
                    <v-form v-model="form.valid">
                        <v-text-field
                                label="Title"
                                :rules="[ruleIsNotEmpty]"
                                box
                                v-model="form.title"
                        ></v-text-field>
                        <v-container grid-list-md fluid>
                            <v-layout row wrap>
                                <v-flex xs7>
                                    <v-textarea
                                            box
                                            label="Content"
                                            :rules="[ruleIsNotEmpty]"
                                            v-model="form.content"
                                    ></v-textarea>
                                </v-flex>
                                <v-flex xs5>
                                    <v-card>
                                        <v-card-text>
                                            <vue-markdown :source="form.content" :anchorAttributes="{target: '_blank', rel: 'nofollow'}"></vue-markdown>
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
            <v-text-field
                    label="Title"
                    :rules="[ruleIsNotEmpty]"
                    box
                    v-model="formMedia.title"
            ></v-text-field>
            <file-input v-model="formMedia.content" @formData="handleFormData" is-image></file-input>
        </v-tab-item>

        <v-tab-item
                :id="`${POST_TYPES.LINK}`"
        >
            <v-form v-model="formLink.valid">
                <v-text-field
                        label="Title"
                        :rules="[ruleIsNotEmpty]"
                        box
                        v-model="formLink.title"
                ></v-text-field>
                <v-text-field
                        box
                        name="input-7-4"
                        label="Link"
                        :rules="[ruleIsNotEmpty, ruleUrl]"
                        v-model="formLink.content"
                ></v-text-field>
            </v-form>
        </v-tab-item>
    </v-tabs>

    <v-btn @click="submit()" :disabled="!isSubmitEnabled()" color="success">Success</v-btn>
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
        data() {
            return {
                POST_TYPES,
                selectedForm: POST_TYPES.POST,
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
        methods: {
            submit () {
                this.$postService.addPost(this.getCurrentForm(), this.selectedGroup, this.selectedForm, (res) => {
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: this.selectedGroup,
                            postID: res.data.id
                        }
                    })
                })
            },
            // getFormData () {
            //     switch (this.selectedForm) {
            //         case POST_TYPES.POST:
            //             return this.form;
            //             // const formData = new FormData();
            //             // formData.append("title", this.formUpdated.title)
            //             // formData.append("title", this.formUpdated.content)
            //             // return {
            //             //     title: this.formUpdated.title,
            //             //     content: this.formUpdated.content
            //             // }
            //         case POST_TYPES.MEDIA:
            //             return this.formMedia;
            //             // let form = this.formMediaUpdated.uploadResult.value[0]
            //             // form.append('title', this.formMediaUpdated.title)
            //             // console.log('media', form)
            //             // // form.title = this.formMediaUpdated.title
            //             // return form;
            //         case POST_TYPES.LINK:
            //             return this.formLink;
            //             // return {
            //             //     title: this.formLinkUpdated.title,
            //             //     content: this.formLinkUpdated.content
            //             // }
            //     }
            // },
            isSubmitEnabled() {
                return this.getCurrentForm().valid && this.selectedGroup
            },
            getCurrentForm() {
                switch (this.selectedForm) {
                    case POST_TYPES.POST:
                        return this.form
                    case POST_TYPES.MEDIA:
                        return this.formMedia
                    case POST_TYPES.LINK:
                        return this.formLink
                }
            },
            handleFormData(val) {
                console.log(val)
            }
        }
    }
</script>

<style scoped>

</style>