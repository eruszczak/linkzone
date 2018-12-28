<template>
    <section class="hero">
        <div class="hero-body">
            <div class="container has-text-centered">
                <div class="column is-6 is-offset-3">
                    <h3 class="title has-text-grey">{{'registerView.header' | t}}</h3>
                    <p class="subtitle has-text-grey">{{'registerView.hint' | t}}</p>
                    <div class="notification is-danger" v-if="serverErrors">
                        <p v-for="error in serverErrors" :key="error">
                            {{`errors.${error}` | t}}
                        </p>
                    </div>
                </div>
                <div class="column is-4 is-offset-4">
                    <div class="box">
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('username')}" :message="triedToSubmit ? errors.first('username') : null">
                            <b-input v-validate="{required: true, min: 3, max: 50}" icon="account" name="username" v-model="form.username" :placeholder="$t('registerView.username')"></b-input>
                        </b-field>
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('email')}" :message="triedToSubmit ? errors.first('email') : null">
                            <b-input v-validate="{ required: true, email: true, min: 3, max: 50 }" name="email" icon="email" v-model="form.email" :placeholder="$t('registerView.email')"></b-input>
                        </b-field>
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('password')}" :message="triedToSubmit ? errors.first('password') : null">
                            <b-input v-validate="{ required: true, min: 6, max: 50 }" name="password" icon="lock" type="password" ref="password" v-model="form.password" :placeholder="$t('registerView.password')"></b-input>
                        </b-field>
                        <b-field :type="{'is-danger': triedToSubmit && errors.has('password-confirm')}" :message="triedToSubmit ? errors.first('password-confirm') : null">
                            <b-input v-validate="{ required: true, min: 6, max: 50, confirmed: 'password' }" name="password-confirm" icon="lock" type="password" v-model="form.passwordConfirm" :placeholder="$t('registerView.password-confirm')"></b-input>
                        </b-field>
                        <div class="field">
                            <b-checkbox :value="form.loginOnSuccess">
                                {{'registerView.log-me-in' | t}}
                            </b-checkbox>
                        </div>
                        <button class="button is-block is-info is-fullwidth" @click="registerAccount">{{'registerView.register' | t}}</button>
                    </div>
                    <p class="has-text-grey">
                        <router-link :to="{name: 'loginView'}">{{'registerView.login' | t}}</router-link>
                    </p>
                </div>
            </div>
        </div>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'

    export default {
        name: "RegisterView",
        data() {
            return {
                form: {
                    username: 'user3',
                    email: 'user@gmail.com',
                    password: 'password',
                    passwordConfirm: 'password',
                    loginOnSuccess: true,
                    valid: false,
                },
                triedToSubmit: false,
                serverErrors: null,
                passwordValidator: { required: true, min: 6, max: 50 }
            }
        },
        created() {
            this.$toggleLoading(false);
        },
        methods: {
            registerAccount() {
                this.triedToSubmit = true;
                this.serverErrors = null;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._register();
                    }
                });
            },
            _register() {
                const vm = this;
                this.$toggleLoading(true);
                this.$userService.register(vm.form, ({data}) => {
                    if (vm.form.loginOnSuccess) {
                        this.$success('registerView.success');
                        this.$userService.authenticate(vm.form.email, vm.form.password, () => {
                            this.$toggleLoading(false);
                            this.$info('logged-in');
                            this.$router.replace('/');
                        });
                    }
                }, ({data}) => {
                    this.serverErrors = data.errors;
                })
            }
        }
    }
</script>

<style scoped>

</style>