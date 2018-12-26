<template>
    <section class="section">
        <div class="column is-8 is-offset-2">
            <b-field :type="{'is-danger': triedToSubmit && errors.has('name')}" :message="triedToSubmit ? errors.first('name') : null">
                <b-input v-validate="'required'" name="name" icon="account-group" v-model="form.name" :placeholder="$t('groups.create-title')"></b-input>
            </b-field>
            <b-field :type="{'is-danger': triedToSubmit && errors.has('description')}" :message="triedToSubmit ? errors.first('description') : null">
                <b-input v-validate="'required'" name="description" icon="text" v-model="form.description" :placeholder="$t('groups.create-description')"></b-input>
            </b-field>
            <div class="has-text-centered mt-2">
                <button class="button is-primary" @click="submit">{{ $t('add') }}</button>
            </div>
        </div>
    </section>
</template>

<script>
    export default {
        name: 'GroupCreateView',
        created() {
            this.$toggleLoading(false);
        },
        data() {
            return {
                form: {
                    name: '',
                    description: ''
                },
                triedToSubmit: false
            }
        },
        methods: {
            submit() {
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._submit();
                    }
                });
            },
            _submit() {
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