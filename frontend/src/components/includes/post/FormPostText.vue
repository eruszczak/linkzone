<template>
    <v-card flat>
        <v-card-text>
            <v-form v-model="formCopy.valid">
                <v-text-field
                        label="Title"
                        :rules="[ruleIsNotEmpty]"
                        box
                        v-model="formCopy.title"
                ></v-text-field>
                <v-container grid-list-md fluid>
                    <v-layout row wrap>
                        <v-flex xs7>
                            <v-textarea
                                    box
                                    label="Content"
                                    :rules="[ruleIsNotEmpty]"
                                    v-model="formCopy.content"
                            ></v-textarea>
                        </v-flex>
                        <v-flex xs5>
                            <v-card>
                                <v-card-text>
                                    <vue-markdown :source="formCopy.content" :anchorAttributes="{target: '_blank', rel: 'nofollow'}"></vue-markdown>
                                </v-card-text>
                            </v-card>
                        </v-flex>
                    </v-layout>
                </v-container>
            </v-form>
            <p>This editor lets you to use markdown. Here you can learn more about markdown syntax:</p>
            <ul>
                <li><a href="https://www.markdowntutorial.com" target="_blank">https://www.markdowntutorial.com/</a></li>
                <li><a href="https://guides.github.com/features/mastering-markdown" target="_blank">https://guides.github.com/features/mastering-markdown</a></li>
            </ul>
        </v-card-text>
    </v-card>
</template>

<script>
    import validation from "../../../mixins/validation";
    import VueMarkdown from 'vue-markdown'

    export default {
        name: "FormPostText",
        mixins: [validation],
        components: {VueMarkdown},
        props: {
            form: {
                type: Object,
                required: true
            }
        },
        watch: {
            formCopy: {
                handler(val) {
                    console.log('emitting', val)
                    this.$emit('change', val)
                },
                deep: true
            },
            form: {
                handler(val) {
                    this.formCopy = {...val}
                },
                deep: true
            }
        },
        data() {
            return {
                formCopy: {...this.form}
            }
        },
        beforeMount () {
            // this.formCopy = Object.assign({}, this.form)
        }
    }
</script>

<style scoped>

</style>