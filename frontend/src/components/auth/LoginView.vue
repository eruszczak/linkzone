<template>
    <section class="hero" @keyup.enter="login">
        <div class="hero-body">
            <div class="container has-text-centered">
                <div class="column is-6 is-offset-3">
                    <h3 class="title has-text-grey">{{'loginView.header' | t}}</h3>
                    <p class="subtitle has-text-grey">{{'loginView.hint' | t}}</p>
                    <div class="notification is-danger" v-if="serverErrors">
                        <p v-for="(error, index) in serverErrors" :key="index">
                            {{`errors.${error}` | t}}
                        </p>
                    </div>
                </div>
                <div class="column is-4 is-offset-4">
                    <div class="box">
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('name')}" :message="triedToSubmit ? errors.first('name') : null">
                            <b-input v-validate="'required'" name="name" icon="account" v-model="form.usernameOrEmail" :placeholder="$t('loginView.name')"></b-input>
                        </b-field>
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('password')}" :message="triedToSubmit ? errors.first('password') : null">
                            <b-input v-validate="'required'" name="password" icon="lock" type="password" v-model="form.password" :placeholder="$t('loginView.password')"></b-input>
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
                serverErrors: null,
                form: {
                    usernameOrEmail: 'admin1',
                    password: 'password',
                    valid: false
                },
                triedToSubmit: false
            }
        },
        created() {
            this.$toggleLoading(false);
        },
        methods: {
            login() {
                this.triedToSubmit = true;
                this.serverErrors = null;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._login();
                    }
                });
            },
            _login() {
                this.$toggleLoading(true);
                this.$userService.authenticate(this.form.usernameOrEmail, this.form.password, () => {
                    this.$info('logged-in');
                    this.$router.replace('/');
                    this.$toggleLoading(false);
                }, ({data}) => {
                    this.serverErrors = data.errors;
                })
            }
        }
    }
</script>

<style scoped>

</style>