<template>
    <section class="section is-fullwidth" v-if="user">
        <section class="hero is-primary is-small">
            <div class="hero-body has-text-centered">
                <p class="title">{{user.username}}</p>
                <p class="subtitle">{{'account.edit-hint'|t}}</p>
                <nav class="level">
                    <figure class="image is-128x128 container">
                        <img class="is-rounded" :src="user.avatarUrl">
                    </figure>
                </nav>
            </div>
        </section>

        <div class="container">
            <div class="column is-8 is-offset-2">
                <nav class="breadcrumb mt-2" aria-label="breadcrumbs">
                    <ul>
                        <li><router-link :to="{name: 'userProfileView', params: {username: user.username}}">{{user.username}}</router-link></li>
                        <li class="is-active"><a href="#" aria-current="page">{{'userEditView'|t}}</a></li>
                    </ul>
                </nav>
                <b-notification v-if="errorList.length > 0" type="is-danger" :closable="false">
                    {{`errors.${errorList[0]}` | t}}
                </b-notification>

                <b-field :type="{'is-danger': triedToSubmit && errors.has('username')}" :message="triedToSubmit ? errors.first('username') : null">
                    <b-input v-validate="'required'" name="username" icon="account" v-model="form.username" :placeholder="$t('')"></b-input>
                </b-field>
                <b-field :type="{'is-danger': triedToSubmit && errors.has('email')}" :message="triedToSubmit ? errors.first('email') : null">
                    <b-input v-validate="'required|email'" name="email" icon="email" v-model="form.email" :placeholder="$t('registerView.email')"></b-input>
                </b-field>
                <b-field>
                    <b-input name="tagline" icon="text" type="text" v-model="form.tagline" :placeholder="$t('account.tagline')"></b-input>
                </b-field>
                <b-field :type="{'is-danger': triedToSubmit && errors.has('password')}" :message="triedToSubmit ? errors.first('password') : null">
                    <b-input v-validate="{ min: 6, max: 50 }" name="password" icon="lock" type="password" ref="password" v-model="form.password" :placeholder="$t('registerView.password')"></b-input>
                </b-field>
                <b-field :type="{'is-danger': triedToSubmit && errors.has('password-confirm')}" :message="triedToSubmit ? errors.first('password-confirm') : null">
                    <b-input v-validate="{ min: 6, max: 50, confirmed: 'password' }" name="password-confirm" icon="lock" type="password" v-model="form.passwordConfirm" :placeholder="$t('registerView.password-confirm')"></b-input>
                </b-field>
            </div>

            <div>
                <file-input :max-height="200" :max-width="200" :max-size="100" label="Avatar" @formData="handleFormData"></file-input>
            </div>
            <div class="has-text-centered mt-2">
                <button class="button is-primary" @click="save">{{'update' | t}}</button>
            </div>
        </div>
    </section>
</template>

<script>
    import {mapGetters} from 'vuex';
    import FileInput from '../includes/FileInput';

    export default {
        name: "UserEditView",
        components: {
            FileInput
        },
        mounted() {
            this.$userService.getUpdateInfo(({data}) => {
                this.username = data.username;
                this.form.username = data.username;
                this.form.email = data.email;
                this.form.tagline = data.tagline;
                this.$toggleLoading(false);
            })
        },
        data() {
            return {
                errorList: [],
                triedToSubmit: false,
                form: {
                    username: null,
                    email: null,
                    tagline: null,
                    password: null,
                    passwordConfirm: null
                }
            }
        },
        computed: {
            ...mapGetters(['user'])
        },
        methods: {
            save() {
                this.triedToSubmit = true;
                this.errorList = [];
                this.$validator.validate().then(result => {
                    if (result) {
                        this._save()
                    }
                });
            },
            _save() {
                this.errorList = [];
                this.$userService.updateAccount({
                    username: this.form.username,
                    email: this.form.email,
                    tagline: this.form.tagline,
                    password: this.form.password,
                    passwordConfirm: this.form.passwordConfirm
                }, ({data}) => {
                    this.updatedToast();
                    this.$userService.updateUserDetails(data);
                }, ({data}) => {
                    this.errorList = data.errors;
                })
            },
            handleFormData($event) {
                this.errorList = [];
                this.uploadAvatar($event);
            },
            uploadAvatar(form) {
                this.$userService.uploadAvatar(form, ({data}) => {
                    this.$userService.updateUserDetails(data);
                    this.updatedToast();
                }, ({data}) => {
                    this.errorList = data.errors;
                })
            },
            updatedToast() {
                this.$success('updated-success');
            }
        }
    }
</script>

<style scoped>

</style>