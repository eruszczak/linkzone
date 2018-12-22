<template>
    <section class="hero">
        <div class="hero-body">
            <div class="container has-text-centered">
                <div class="column is-6 is-offset-3">
                    <h3 class="title has-text-grey">{{'registerView.header' | t}}</h3>
                    <p class="subtitle has-text-grey">{{'registerView.hint' | t}}</p>
                    <div class="notification is-danger" v-if="errors">
                        <p v-for="error in errors">
                            {{`errors.${error}` | t}}
                        </p>
                    </div>
                </div>
                <div class="column is-4 is-offset-4">
                    <div class="box">
                        <b-field  label="Error" type="is-danger"
                                  message="You can have a message too">
                            <b-input icon="account" v-model="form.username" :placeholder="$t('registerView.username')"></b-input>
                        </b-field>
                        <b-field>
                            <b-input icon="email" v-model="form.email" :placeholder="$t('registerView.email')"></b-input>
                        </b-field>
                        <b-field>
                            <b-input icon="lock" type="password" v-model="form.password" :placeholder="$t('registerView.password')"></b-input>
                        </b-field>
                        <b-field>
                            <b-input icon="lock" type="password" v-model="form.passwordConfirm" :placeholder="$t('registerView.password-confirm')"></b-input>
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
    import validation from "../../mixins/validation";

    export default {
        name: "RegisterView",
        mixins: [validation],
        data() {
            return {
                form: {
                    username: 'user3',
                    email: 'user@gmail.com',
                    password: 'password',
                    passwordConfirm: 'password',
                    loginOnSuccess: true,
                    valid: false
                },
                errors: null
            }
        },
        methods: {
            ...mapMutations(['setAccessToken', 'toggleLoading']),
            registerAccount() {
                this.error = false;
                const vm = this;
                this.toggleLoading(true);
                this.$userService.register(vm.form, res => {
                    let msg = "Account created";
                    if (vm.form.loginOnSuccess) {
                        this.$userService.authenticate(vm.form.email, vm.form.password, () => {
                            this.toggleLoading(false);
                            vm.$message({
                                message: "Hello " + vm.form.username
                            })
                        })
                    }
                }, ({data}) => {
                    this.errors = data.errors;
                })
            }
        }
    }
</script>

<style scoped>

</style>