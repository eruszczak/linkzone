<template>
    <div>
        <v-form v-model="form.valid">
            <v-text-field
                    label="Name"
                    :rules="[ruleIsNotEmpty]"
                    box
                    v-model="form.name"
            ></v-text-field>
            <v-text-field
                    label="Description"
                    :rules="[]"
                    box
                    v-model="form.description"
            ></v-text-field>
        </v-form>
        <v-btn @click="submit()" :disabled="!form.valid" color="success">Create</v-btn>
    </div>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
    import validation from "../../mixins/validation";

    export default {
        name: 'GroupCreateView',
        mixins: [validation],
        data () {
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
        computed: {
        },
        methods: {
            submit() {
                if (!this.form.valid) {
                    return;
                }
                this.$groupService.addGroup({
                    name: this.form.name,
                    description: this.form.description
                }, (res) => {
                    console.log(res)
                    this.$router.push({name: 'groupDetailView', params: {name: res.data.name}})
                }, err => {
                    console.log(err)
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