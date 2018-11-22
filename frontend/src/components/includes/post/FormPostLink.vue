<template>
    <v-card-text>
        <v-form v-model="formCopy.valid">
            <v-text-field
                    label="Title"
                    :rules="[ruleIsNotEmpty]"
                    box
                    v-model="formCopy.title"
            ></v-text-field>
            <v-text-field
                    box
                    name="input-7-4"
                    label="Link"
                    :rules="[ruleIsNotEmpty, ruleUrl]"
                    v-model="formCopy.link"
            ></v-text-field>
        </v-form>
    </v-card-text>
</template>

<script>
    import validation from "../../../mixins/validation";

    export default {
        name: "FormPostLink",
        mixins: [validation],
        props: {
            form: {
                type: Object,
                required: true
            }
        },
        data () {
            return {
                formCopy: Object.assign({}, this.form)
            }
        },
        watch: {
            formCopy: {
                handler (val) {
                    this.$emit('change', val)
                },
                deep: true
            }
        },
        methods: {

        }
    }
</script>

<style scoped>

</style>