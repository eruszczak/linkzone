<template>
    <section class="hero">
        <div class="hero-body">
            <div class="container has-text-centered">
                <div class="column is-4 is-offset-4">
                    <h3 class="title has-text-grey">{{'registerView.header' | t}}</h3>
                    <p class="subtitle has-text-grey">{{'registerView.hint' | t}}</p>
                    <div class="box">
                        <b-field type="is-success">
                            <b-input icon="account" v-model="form.username" :placeholder="$t('registerView.username')"></b-input>
                        </b-field>
                        <b-field type="is-success">
                            <b-input icon="email" v-model="form.email" :placeholder="$t('registerView.email')"></b-input>
                        </b-field>
                        <b-field>
                            <b-input icon="lock" type="password" v-model="form.password" :placeholder="$t('loginView.password')"></b-input>
                        </b-field>
                        <b-field>
                            <b-input icon="lock" type="password" v-model="form.passwordConfirm" :placeholder="$t('loginView.password-confirm')"></b-input>
                        </b-field>
                        <button class="button is-block is-info is-fullwidth" @click="registerAccount">{{'registerView.register' | t}}</button>
                    </div>
                    <p class="has-text-grey">
                        <router-link :to="{name: 'loginView'}">{{'registerView.login' | t}}</router-link>
                    </p>
                </div>
            </div>
        </div>
    </section>
    <!--<v-dialog max-width="500px" v-model="dialog">-->
        <!--<v-card class="elevation-12">-->
            <!--<v-toolbar color="primary" dark>-->
                <!--<v-toolbar-title>Sign up</v-toolbar-title>-->
            <!--</v-toolbar>-->
            <!--<v-card-text>-->
                <!--<v-form v-model="form.valid">-->
                    <!--<v-text-field :rules="[ruleIsNotEmpty]" label="Username" prepend-icon="person"-->
                                  <!--type="text" v-model="form.username"></v-text-field>-->
                    <!--<v-text-field :rules="[ruleIsNotEmpty, ruleEmail]" label="Email" prepend-icon="email"-->
                                  <!--type="text" v-model="form.email"></v-text-field>-->
                    <!--<v-text-field :rules="[ruleIsNotEmpty]" label="Password" prepend-icon="lock"-->
                                  <!--type="password" v-model="form.password"></v-text-field>-->
                    <!--<v-text-field :rules="[ruleIsNotEmpty]" label="Password confirm" prepend-icon="lock"-->
                                  <!--type="password" v-model="form.passwordConfirm"></v-text-field>-->
                    <!--<v-checkbox-->
                            <!--label="Log me in"-->
                            <!--v-model="form.loginOnSuccess"-->
                    <!--&gt;</v-checkbox>-->
                <!--</v-form>-->
            <!--</v-card-text>-->
            <!--<v-card-actions>-->
                <!--<p>Already a memeber? <span @click="signIn()" style="color: blue">Sign in</span></p>-->
                <!--<v-spacer></v-spacer>-->
                <!--<v-btn @click="dialog = false" color="default">Cancel</v-btn>-->
                <!--<v-btn :disabled="!form.valid" @click="registerAccount()" color="success">Sign up</v-btn>-->
            <!--</v-card-actions>-->
        <!--</v-card>-->
    <!--</v-dialog>-->
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
                dialog: false,
                error: false,
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
                            this.dialog = false;
                            this.toggleLoading(false);
                            vm.$message({
                                message: "Hello " + vm.form.username
                            })
                        })
                    } else {
                        this.dialog = false
                    }
                }, () => {
                    this.error = true
                })
            }
        }
    }
</script>

<style scoped>

</style>