<template>
    <section class="section is-fullwidth" v-if="group && isAdmin">
        <section class="hero is-primary">
            <div class="hero-body">
                <div class="container">
                    <h1 class="title">
                        {{'groups.update-header'|t}} /g/{{group.name}}
                    </h1>
                </div>
            </div>
        </section>
        <section class="container">
            <div class="column is-8 is-offset-2">
                <nav class="breadcrumb mt-2" aria-label="breadcrumbs">
                    <ul>
                        <li><router-link :to="{name: 'groupListView'}">{{'groupListView' |t}}</router-link></li>
                        <li><router-link :to="{name: 'groupDetailView', params: {name: group.name}}">{{group.name}}</router-link></li>
                        <li class="is-active"><a href="#" aria-current="page">{{'groupEditView'|t}}</a></li>
                    </ul>
                </nav>

                <b-notification type="is-danger" v-if="bannerErrors.length > 0" :closable="false">
                    {{'errors.' + bannerErrors[0] | t}}
                </b-notification>
                <b-field>
                    <b-input :disabled="true" icon="account-group" v-model="group.name"></b-input>
                </b-field>
                <b-field :type="{'is-danger': triedToSubmit && errors.has('description')}" :message="triedToSubmit ? errors.first('description') : null">
                    <b-input v-validate="'required'" name="description" icon="text" v-model="group.description" :placeholder="$t('groups.create-description')"></b-input>
                </b-field>

                <b-tabs class="mt-2">
                    <b-tab-item :label="$t('groups.show-logo')" icon="google-photos">
                        <div class="box has-text-centered">
                            <img class="" v-if="group.logo" :src="'/static/' + group.logo">
                            <file-input @formData="handleFormDataLogo" label="Logo" :max-height="50" :max-width="50" :max-size="50"></file-input>
                        </div>
                    </b-tab-item>
                    <b-tab-item :label="$t('groups.show-banner')" icon="library-music">
                        <div class="box">
                            <img v-if="group.bannerUrl" :src="'/static/' + group.bannerUrl">
                            <file-input @formData="handleFormData" label="Banner" :max-height="400" :max-width="2000" :max-size="1000"></file-input>
                        </div>
                    </b-tab-item>
                </b-tabs>

                <b-field :label="$t('groups.creator')" :message="$t('groups.creator-hint')">
                    <b-input :placeholder="group.creator.username" disabled></b-input>
                </b-field>

                <b-field class="mt-2" :label="$t('groups.pick-admins')" :message="$t('groups.admin-hint')">
                    <b-taginput
                        v-model="selectedAdmins"
                        :data="adminOptions"
                        autocomplete
                        field="username"
                        icon="account"
                        type="is-danger"
                        :placeholder="$t('groups.add-tag')"
                        @typing="updateAdminOptions">
                        <template slot-scope="props">
                            <figure class="image is-16x16" style="margin-right: 5px">
                                <img src="https://api.adorable.io/avatar/100/user8">
                            </figure>
                            {{props.option.username}}
                        </template>
                        <template slot="empty">
                            {{'empty'|t}}
                        </template>
                    </b-taginput>
                </b-field>

                <b-field class="mt-2" :label="$t('groups.pick-mods')" :message="$t('groups.mod-hint')">
                    <b-taginput
                        v-model="selectedMods"
                        :data="modOptions"
                        autocomplete
                        field="username"
                        icon="account"
                        type="is-warning"
                        :placeholder="$t('groups.add-tag')"
                        @typing="updateAdminOptions">
                        <template slot-scope="props">
                            <figure class="image is-16x16" style="margin-right: 5px">
                                <img src="https://api.adorable.io/avatar/100/user8">
                            </figure>
                            {{props.option.username}}
                        </template>
                        <template slot="empty">
                            {{'empty'|t}}
                        </template>
                    </b-taginput>
                </b-field>

                <b-field class="mt-2" :label="$t('groups.post-types')" :message="triedToSubmit && errors.first('content') ? errors.first('content') : $t('groups.post-types-hint')" :type="{'is-danger': triedToSubmit && errors.has('content')}">
                    <div class="block" style="margin-bottom:0">
                        <b-checkbox v-validate="{required: true}" name="content" v-model="selectedContent" :native-value="type" v-for="type in POST_TYPES" :key="type">
                            {{ type | t }}
                        </b-checkbox>
                    </div>
                </b-field>

                <div class="mt-2 has-text-centered">
                    <button class="button is-primary" @click="updateGroup">{{'update'|t}}</button>
                    <button class="button is-danger is-pulled-right is-small" @click="deleteGroup">{{'groups.delete-group'|t}}</button>
                </div>
            </div>
        </section>
    </section>
</template>

<script>
    import {findIndex} from 'lodash'
    import FileInput from '../includes/FileInput.vue'
    import {POST_TYPES} from "../../services/PostService";
    import debounce from 'lodash/debounce'

    export default {
        name: "GroupEditView",
        components: {FileInput},
        props: {
            name: {
                type: String,
                required: true
            }
        },
        mounted() {
            this.$groupService.getGroupDetail(this.name, res => {
                this.$toggleLoading(false);
                this.group = res.data;
                this.isAdmin = true;
                // this.bannerFilename = this.group.bannerUrl;

                // this.isAdmin = this.$groupService.isAdmin(this.group.administrators, this.$userService.getUserId());
                this.selectedAdmins = this.group.administrators.slice();
                this.selectedContent = this.group.postTypes.slice();

                this.selectedMods = this.group.moderators.slice();
                // if (!this.isAdmin) {
                //     // TODO redirect somewhere
                // }
            })
        },
        data() {
            return {
                bannerErrors: [],
                isAdmin: false,
                group: {},
                triedToSubmit: false,
                selectedAdmins: [],
                selectedMods: [],
                adminOptions: [],
                modOptions: [],
                selectedContent: [],
                POST_TYPES
            }
        },
        methods: {
            findIndex: findIndex,
            updateAdminOptions: debounce(function (text) {
                this.$userService.findExact(text.trim(), ({data}) => {
                    console.log(data)
                    this.adminOptions = [data];
                }, () => {
                    this.adminOptions = [];
                })
            }, 500),
            updateGroup() {
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._updateGroup();
                    }
                });
            },
            _updateGroup() {
                this.$groupService.update(this.group.name, {
                    name: this.group.name,
                    description: this.group.description,
                    administrators: this.selectedAdmins.map(user => user.id),
                    moderators: this.selectedMods.map(user => user.id),
                    postTypes: this.selectedContent,
                    // tags: this.group.tags
                }, ({data}) => {
                    this.$success('Updated group');
                }, () => {
                    this.$danger('Error');
                })
            },
            deleteGroup() {
                this.$dialog.confirm({
                    title: this.$t('groups.remove-title'),
                    message: this.$t('groups.remove-message'),
                    confirmText: this.$t('confirm'),
                    cancelText: this.$t('cancel'),
                    type: 'is-danger',
                    hasIcon: true,
                    onConfirm: () => {
                        this.removeGroup();
                    }
                })
            },
            removeGroup() {
                this.$groupService.delete(this.group.name, () => {
                    this.$router.push({path: '/'});
                    this.$danger('removed-group');
                });
            },
            handleFormDataLogo($event) {
                this.bannerErrors = [];
                this.uploadLogo($event);
            },
            handleFormData($event) {
                this.bannerErrors = [];
                this.uploadBanner($event);
            },
            uploadLogo(form) {
                this.$groupService.uploadLogo(this.group.name, form, ({data}) => {
                    this.group.logo = data.fileName;
                }, ({data}) => {
                    this.bannerErrors = data.errors;
                });
            },
            uploadBanner(form) {
                this.$groupService.uploadBanner(this.group.name, form, ({data}) => {
                    this.group.bannerUrl = data.fileName;
                }, ({data}) => {
                    this.bannerErrors = data.errors;
                });
            }
        }
    }
</script>

<style scoped>

</style>