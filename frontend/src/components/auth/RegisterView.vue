<template>
    <v-dialog v-model="dialog" max-width="500px">
        <v-card class="elevation-12">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Sign up</v-toolbar-title>
            </v-toolbar>
            <v-card-text>
                <v-form v-model="form.valid">
                    <v-text-field prepend-icon="person" label="Username" type="text"
                                  v-model="form.username" :rules="[ruleIsNotEmpty]"></v-text-field>
                    <v-text-field prepend-icon="email" label="Email" type="text"
                                  v-model="form.email" :rules="[ruleIsNotEmpty, ruleEmail]"></v-text-field>
                    <v-text-field prepend-icon="lock" label="Password" type="password"
                                  v-model="form.password" :rules="[ruleIsNotEmpty]"></v-text-field>
                    <v-text-field prepend-icon="lock" label="Password confirm" type="password"
                                  v-model="form.passwordConfirm" :rules="[ruleIsNotEmpty]"></v-text-field>
                    <v-checkbox
                            label="Log me in"
                            v-model="form.loginOnSuccess"
                    ></v-checkbox>
                </v-form>
            </v-card-text>
            <v-card-actions>
                <p>Already a memeber? <span style="color: blue" @click="signIn()">Sign in</span></p>
                <v-spacer></v-spacer>
                <v-btn color="default" @click="dialog = false">Cancel</v-btn>
                <v-btn color="success" @click="registerAccount()" :disabled="!form.valid">Sign up</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
    import debounce from 'lodash/debounce'
    import validation from "../../mixins/validation";

    export default {
        name: "RegisterView",
        mixins: [validation],
        data () {
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
        computed: {
            ...mapGetters(['registerModalActive'])
        },
        watch: {
            registerModalActive(val) {
                console.log('watch registerModalActive', val)
                this.dialog = val
            },
            dialog(val) {
                this.setRegisterModalState(val)
            }
        },
        methods: {
            ...mapMutations(['setAccessToken', 'setRegisterModalState', 'toggleLoading', 'setLoginModalState']),
            registerAccount () {
                this.error = false
                const vm = this;
                this.toggleLoading(true)
                this.$userService.register(vm.form, res => {
                    let msg = "Account created"
                    if (vm.form.loginOnSuccess) {
                        this.$userService.authenticate(vm.form.email, vm.form.password, () => {
                            this.dialog = false
                            this.toggleLoading(false)
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
            },
            signIn() {
                console.log('signIn instead')
                this.setLoginModalState(true)
                this.setRegisterModalState(false)
            }
        }
    }
</script>

<style scoped>

</style>