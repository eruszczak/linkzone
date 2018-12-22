<template>
    <section class="hero">
        <div class="hero-body">
            <div class="container has-text-centered">
                <div class="column is-4 is-offset-4">
                    <h3 class="title has-text-grey">{{'loginView.header' | t}}</h3>
                    <p class="subtitle has-text-grey">{{'loginView.hint' | t}}</p>
                    <div class="box">
                        {{errors}}
                        <b-field type="is-success">
                            <b-input icon="account" v-model="form.usernameOrEmail" :placeholder="$t('loginView.name')"></b-input>
                        </b-field>
                        <b-field>
                            <b-input icon="lock" type="password" v-model="form.password" :placeholder="$t('loginView.password')"></b-input>
                        </b-field>
                        <button class="button is-block is-info is-fullwidth" @click="login">{{'loginView.login' | t}}</button>
                    </div>
                    <p class="has-text-grey">
                        <router-link :to="{name: 'registerView'}">{{'loginView.register' | t}}</router-link>
                    </p>
                </div>
            </div>
        </div>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import validation from "../../mixins/validation";

    export default {
        name: 'LoginView',
        mixins: [validation],
        data() {
            return {
                errors: [],
                form: {
                    usernameOrEmail: 'admin1',
                    password: 'password',
                    valid: false
                }
            }
        },
        methods: {
            ...mapMutations(['setAccessToken', 'toggleLoading']),
            login() {
                this.error = false;
                this.toggleLoading(true);
                this.$userService.authenticate(this.form.usernameOrEmail, this.form.password, () => {
                    this.toggleLoading(false);
                    this.$message({
                        message: "Hello " + this.form.usernameOrEmail
                    })
                }, ({data}) => {
                    // console.log(data.ero)
                    this.errors = data.errors
                })
            }
        }
    }
</script>

<style scoped>

</style>