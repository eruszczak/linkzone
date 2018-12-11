<template>
    <v-dialog max-width="500px" v-model="dialog">
        <v-card class="elevation-12">
            <v-toolbar color="primary" dark>
                <v-toolbar-title>Login</v-toolbar-title>
            </v-toolbar>
            <v-card-text>
                <v-alert
                        :value="errors.length > 0"
                        type="error"
                >
                    {{errors[0]}}
                </v-alert>
                <v-form v-model="form.valid">
                    <v-text-field :rules="[ruleIsNotEmpty]" label="Username/Email" name="login" prepend-icon="person"
                                  type="text" v-model="form.usernameOrEmail"></v-text-field>
                    <v-text-field :rules="[ruleIsNotEmpty]" id="password" label="Password" name="password" prepend-icon="lock"
                                  type="password" v-model="form.password"></v-text-field>
                </v-form>
            </v-card-text>
            <v-card-actions>
                <p>Not a memeber? <span @click="signUp()" style="color: blue">Sign up</span></p>
                <v-spacer></v-spacer>
                <v-btn @click="dialog = false" color="default">Cancel</v-btn>
                <v-btn :disabled="!form.valid" @click="login()" color="success">Login</v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import validation from "../../mixins/validation";

    export default {
        name: 'LoginView',
        mixins: [validation],
        data() {
            return {
                dialog: false,
                errors: [],
                form: {
                    usernameOrEmail: 'admin1',
                    password: 'password',
                    valid: false
                }
            }
        },
        watch: {
            loginModalActive(val) {
                console.log('watch loginModalActive', val);
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
                this.error = false;
                this.toggleLoading(true);
                this.$userService.authenticate(this.form.usernameOrEmail, this.form.password, () => {
                    this.dialog = false;
                    this.toggleLoading(false);
                    this.$message({
                        message: "Hello " + this.form.usernameOrEmail
                    })
                }, ({data}) => {
                    // console.log(data.ero)
                    this.errors = data.errors
                })
            },
            signUp() {
                console.log('signup instead');
                this.setLoginModalState(false);
                this.setRegisterModalState(true)
            }
        }
    }
</script>

<style scoped>

</style>