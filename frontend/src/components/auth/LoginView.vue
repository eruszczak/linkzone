<template>
    <v-dialog v-model="dialog" max-width="500px">
        <v-card class="elevation-12">
            <v-toolbar dark color="primary">
                <v-toolbar-title>Login</v-toolbar-title>
            </v-toolbar>
            <v-card-text>
                <v-form v-model="form.valid">
                    <v-text-field prepend-icon="person" name="login" label="Username/Email" type="text"
                                  v-model="form.usernameOrEmail" :rules="[ruleIsNotEmpty]"></v-text-field>
                    <v-text-field id="password" prepend-icon="lock" name="password" label="Password" type="password"
                                  v-model="form.password" :rules="[ruleIsNotEmpty]"></v-text-field>
                </v-form>
            </v-card-text>
            <v-card-actions>
                <p>Not a memeber? <span style="color: blue" @click="signUp()">Sign up</span></p>
                <v-spacer></v-spacer>
                <v-btn color="default" @click="dialog = false">Cancel</v-btn>
                <v-btn color="success" @click="login()" :disabled="!form.valid">Login</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
    import validation from "../../mixins/validation";

    export default {
        name: 'LoginView',
        mixins: [validation],
        data() {
            return {
                dialog: false,
                error: false,
                form: {
                    usernameOrEmail: 'admin1',
                    password: 'password',
                    valid: false
                }
            }
        },
        watch: {
            loginModalActive(val) {
                console.log('watch loginModalActive', val)
                this.dialog = val
            },
            dialog(val) {
                this.setLoginModalState(val)
            }
        },
        computed: {
            ...mapGetters(['loginModalActive'])
        },
        methods: {
            ...mapMutations(['setAccessToken', 'setLoginModalState', 'toggleLoading', 'setRegisterModalState']),
            login() {
                this.error = false
                this.toggleLoading(true)
                this.$userService.authenticate(this.form.usernameOrEmail, this.form.password, () => {
                    this.dialog = false
                    this.toggleLoading(false)
                    this.$message({
                        message: "Hello " + this.form.usernameOrEmail
                    })
                }, () => {
                    this.error = true
                })
            },
            signUp() {
                console.log('signup instead')
                this.setLoginModalState(false)
                this.setRegisterModalState(true)
            }
        }
    }
</script>

<style scoped>

</style>