<template>
    <div>
        <v-form v-model="form.valid">
            <v-text-field
                    :rules="[ruleIsNotEmpty]"
                    box
                    label="Name"
                    v-model="form.name"
            ></v-text-field>
            <v-text-field
                    :rules="[]"
                    box
                    label="Description"
                    v-model="form.description"
            ></v-text-field>
        </v-form>
        <v-btn :disabled="!form.valid" @click="submit()" color="success">Create</v-btn>
    </div>
</template>

<script>
    import validation from "../../mixins/validation";

    export default {
        name: 'GroupCreateView',
        mixins: [validation],
        data() {
            return {
                form: {
                    name: '',
                    description: '',
                    region: '',
                    date1: '',
                    date2: '',
                    delivery: false,
                    type: [],
                    resource: '',
                    valid: false
                }
            }
        },
        computed: {},
        methods: {
            submit() {
                if (!this.form.valid) {
                    return;
                }
                this.$groupService.addGroup({
                    name: this.form.name,
                    description: this.form.description
                }, (res) => {
                    console.log(res);
                    this.$router.push({name: 'groupDetailView', params: {name: res.data.name}})
                }, err => {
                    console.log(err);
                    this.$message({
                        message: err.data.errors[0],
                        color: this.$toastColors.ERROR
                    })
                })
            }
        }
    }
</script>

<style scoped>

</style>