<template>
    <section v-if="user" class="section">
        <nav class="breadcrumb" aria-label="breadcrumbs">
            <ul>
                <li><router-link :to="{name: 'userProfileView', params: {username: user.username}}">{{user.username}}</router-link></li>
                <li class="is-active"><a href="#" aria-current="page">{{'userEditView'|t}}</a></li>
            </ul>
        </nav>

        <b-notification v-if="errorList.length > 0" type="is-danger">
            {{`errors.${errorList[0]}` | t}}
        </b-notification>
        <!-- <div class="box"> -->
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
            
            <div class="has-text-centered mt-2">
                <button class="button is-primary" @click="save">{{'update' | t}}</button>
            </div>
        <!-- </div> -->

                        <!-- <div>
                    <img v-if="avatarFilename" :src="`/static/${avatarFilename}`" width="100"/>
                    <file-input :disabled="!isEditing" :is-image="true" @formData="handleFormData" v-model="avatarFilename"></file-input>
                    <v-btn :disabled="avatarFormData.length === 0" @click.native="uploadAvatar" v-if="isEditing">Upload
                        avatar
                    </v-btn>
                </div> -->
    </section>
</template>

<script>
    import validation from "../../mixins/validation";
    import {mapGetters} from 'vuex';
    import FileInput from '../includes/FileInput';

    export default {
        name: "UserEditView",
        components: {
            FileInput
        },
        mixins: [validation],
        mounted() {
            this.$userService.getUpdateInfo(({data}) => {
                console.log(data);
                this.username = data.username;
                this.form.username = data.username;
                this.form.email = data.email;
                this.form.tagline = data.tagline;
                this.avatarFilename = data.avatar;
            })
        },
        data() {
            return {
                avatarFilename: '',
                avatarErrors: [],
                avatarFormData: [],
                errorList: [],
                model: null,
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
                this.$userService.updateAccount({
                    username: this.form.username,
                    email: this.form.email,
                    tagline: this.form.tagline,
                    password: this.form.password,
                    passwordConfirm: this.form.passwordConfirm
                }, ({data}) => {
                    this.$toast.open({
                        message: this.$t('updated-success'),
                        type: 'is-success'
                    });
                    this.$userService.updateUserDetails(data);
                }, ({data}) => {
                    console.error(data);
                    this.errorList = data.errors;
                })
            },
            handleFormData(val) {
                console.log('UserEditView.handleFormData', val)
                if (val.value) {
                    this.avatarFormData = val.value;
                }
                this.errorList = val.errors
            },
            uploadAvatar() {
                this.errorList = [];
                this.$userService.uploadAvatar(this.avatarFormData[0], ({data}) => {
                    this.avatarFilename = data.fileName;
                    this.avatarFormData = []
                }, ({data}) => {
                    console.error('UserEditView.uploadAvatar', data);
                    this.errorList = data.errors;
                })
            }
        }
    }
</script>

<style scoped>

</style>