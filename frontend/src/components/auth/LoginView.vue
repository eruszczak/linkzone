<template>
    <section class="hero" @keyup.enter="login">
        <div class="hero-body">
            <div class="container has-text-centered">
                <div class="column is-8 is-offset-2">
                    <h3 class="title has-text-grey">{{'loginView.header' | t}}</h3>
                    <p class="subtitle has-text-grey">{{'loginView.hint' | t}}</p>
                    <div class="notification is-danger" v-if="serverErrors.length">
                        <p v-for="(error, index) in serverErrors" :key="index">
                            {{`errors.${error}` | t}}
                        </p>
                    </div>
                </div>
                <div class="column is-6 is-offset-3">
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
                    <br><br>
                    <button class="button is-small" @click="form.usernameOrEmail = 'admin'; form.password = 'password'; login()">admin</button>
                </div>
            </div>
        </div>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'

    export default {
        name: 'LoginView',
        data() {
            return {
                serverErrors: [],
                form: {
                    usernameOrEmail: '',
                    password: '',
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
                this.serverErrors = [];
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
                    this.$toggleLoading(false);
                    this.$router.replace({path: '/'});
                }, ({data}) => {
                    this.serverErrors = data.errors || [];
                })
            }
        }
    }
</script>

<style scoped>

</style>