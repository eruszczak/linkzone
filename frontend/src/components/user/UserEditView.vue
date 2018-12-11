<template>
    <v-card
            class="hide-overflow"
            color="purple lighten-1"
            dark
    >
        <v-alert
                :value="errorList.length > 0"
                type="error"
        >
            <p :key="error" v-for="error in errorList">* {{error}}</p>
        </v-alert>
        <v-toolbar
                card
                color="purple"
        >
            <v-icon>mdi-account</v-icon>
            <v-toolbar-title class="font-weight-light">User Profile</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn
                    @click="isEditing = !isEditing"
                    color="purple darken-3"
                    fab
                    small
            >
                <v-icon v-if="isEditing">mdi-close</v-icon>
                <v-icon v-else>mdi-pencil</v-icon>
            </v-btn>
        </v-toolbar>
        <v-card-text>
            <v-text-field
                    :disabled="!isEditing"
                    :rules="[ruleIsNotEmpty]"
                    color="white"
                    label="Username"
                    v-model="form.username"
            ></v-text-field>
            <v-text-field
                    :disabled="!isEditing"
                    :rules="[ruleEmail]"
                    color="white"
                    label="Email"
                    v-model="form.email"
            ></v-text-field>
            <v-text-field
                    :disabled="!isEditing"
                    color="white"
                    label="Tagline"
                    v-model="form.tagline"
            ></v-text-field>
            <v-text-field :disabled="!isEditing" :rules="[]" label="Password" name="password" prepend-icon="lock"
                          type="password" v-model="form.password"></v-text-field>
            <v-text-field :disabled="!isEditing" :rules="[]" label="Password confirm" name="password"
                          prepend-icon="lock"
                          type="password" v-model="form.passwordConfirm"></v-text-field>

            <div>
                <p>{{avatarErrors}}</p>
                <img :src="`/static/${avatarFilename}`"/>
                <file-input :current-image-url="avatarFilename ? '/static/' + avatarFilename : ''" :disabled="!isEditing" :is-image="true" @formData="handleFormData"
                            v-model="avatarFilename"></file-input>
                <v-btn :disabled="avatarFormData.length === 0" @click.native="uploadAvatar" v-if="isEditing">Upload
                    avatar
                </v-btn>
            </div>
        </v-card-text>
        <v-divider></v-divider>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn
                    :disabled="!isEditing"
                    @click="save"
                    color="success"
            >
                Save
            </v-btn>
        </v-card-actions>
        <v-snackbar
                :timeout="2000"
                absolute
                bottom
                left
                v-model="hasSaved"
        >
            Your profile has been updated
        </v-snackbar>
    </v-card>
</template>

<script>
    import validation from "../../mixins/validation";
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
                hasSaved: false,
                errorList: [],
                isEditing: null,
                model: null,
                username: null,
                form: {
                    username: null,
                    email: null,
                    tagline: null,
                    password: null,
                    passwordConfirm: null
                }
            }
        },
        methods: {
            save() {
                this.errorList = [];
                this.$userService.updateAccount(this.username, {
                    username: this.form.username,
                    email: this.form.email,
                    tagline: this.form.tagline,
                    password: this.form.password,
                    passwordConfirm: this.form.passwordConfirm
                }, () => {
                    this.hasSaved = true;
                    this.isEditing = !this.isEditing
                }, ({data}) => {
                    console.error(data);
                    this.errorList = data.errors
                })
            },
            handleFormData(val) {
                this.avatarFormData = val.value;
                this.avatarErrors = val.errors
            },
            // todo after changing username, I won't be able to update avatar? not only avatar I think
            uploadAvatar() {
                this.$userService.uploadAvatar(this.username, this.avatarFormData[0], ({data}) => {
                    this.avatarFilename = data.fileName;
                    this.avatarFormData = []
                })
            }
        }
    }
</script>

<style scoped>

</style>