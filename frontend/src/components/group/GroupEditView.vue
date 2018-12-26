<template>
    <section class="section" v-if="group && isAdmin">
        {{group}} <br>
        <h1>Update /g/{{group.name}}</h1>

        <nav class="breadcrumb" aria-label="breadcrumbs">
            <ul>
                <li><router-link :to="{name: 'groupListView'}">{{'groupListView' |t}}</router-link></li>
                <li><router-link :to="{name: 'groupDetailView', params: {name: group.name}}">{{group.name}}</router-link></li>
                <li class="is-active"><a href="#" aria-current="page">{{'groupEditView'|t}}</a></li>
            </ul>
        </nav>
        <b-notification type="is-danger" v-if="bannerErrors.length > 0">
            {{bannerErrors[0]}}
        </b-notification>

        <button class="button is-primary" @click="updateGroup">{{'update'|t}}</button>
        <button class="button is-danger" @click="deleteGroup">{{'groups.delete-group'|t}}</button>

        <b-field>
            <b-input :disabled="true" icon="account-group" v-model="group.name"></b-input>
        </b-field>
        <b-field :type="{'is-danger': triedToSubmit && errors.has('description')}" :message="triedToSubmit ? errors.first('description') : null">
            <b-input v-validate="'required'" name="description" icon="text" v-model="group.description" :placeholder="$t('groups.create-description')"></b-input>
        </b-field>

        <img v-if="group.bannerUrl" :src="'/static/' + group.bannerUrl">
        <file-input :max-height="200" :max-width="1000" @formData="handleFormData" :max-size="1000"></file-input>
        <!-- <v-btn :disabled="bannerFormData.length === 0" @click.native="uploadBanner">Upload banner</v-btn> -->
    </section>
<!-- 
        <v-autocomplete
                :cache-items="false"
                :deletable-chips="true"
                :disabled="isUpdating"
                :error="selectedAdmins.length === 0"
                :items="adminOptions"
                :no-filter="true"
                :search-input.sync="searchAdmin"
                box
                chips
                color="blue-grey lighten-2"
                label="Select administrators"
                multiple
                placeholder="Start typing to Search"
                v-model="selectedAdmins"
        >
            <template
                    slot="selection"
                    slot-scope="data"
            >
                <v-chip
                        :close="data.item.id !== group.creator.id"
                        :color="data.item.id === group.creator.id ? 'grey' : ''"
                        :selected="data.selected"
                        :text-color="data.item.id === group.creator.id ? 'white' : ''"
                        @input="removeAdmin(data.item)"
                        class="chip--select-multi"
                >

                    <span v-if="data.item.id === group.creator.id"><strong>{{data.item.username}}</strong> (creator)</span>
                    <span v-else>{{data.item.username}}</span>
                </v-chip>
            </template>
            <template
                    @click="removeAdmin(data.item)"
                    slot="item"
                    slot-scope="data"
            >
                <template>

                    <v-list-tile-content>
                        <v-list-tile-title v-html="data.item.username"></v-list-tile-title>
                        <v-list-tile-sub-title v-html="data.item.tagline"
                                               v-if="data.item.tagline"></v-list-tile-sub-title>
                    </v-list-tile-content>
                </template>
            </template>
        </v-autocomplete>

        <v-autocomplete
                :cache-items="false"
                :deletable-chips="true"
                :disabled="isUpdating"
                :error="false"
                :items="modOptions"
                :no-filter="true"
                :search-input.sync="searchMod"
                box
                chips
                color="blue-grey lighten-2"
                label="Select moderators"
                multiple
                placeholder="Start typing to Search"
                v-model="selectedMods"
        >
            <template
                    slot="selection"
                    slot-scope="data"
            >
                <v-chip
                        :close="true"
                        @input="removeMod(data.item)"
                        class="chip--select-multi"
                >

                    <span v-if="data.item.id === group.creator.id"><strong>{{data.item.username}}</strong> (creator)</span>
                    <span v-else>{{data.item.username}}</span>
                </v-chip>
            </template>
            <template
                    @click="removeMod(data.item)"
                    slot="item"
                    slot-scope="data"
            >
                <template>

                    <v-list-tile-content>
                        <v-list-tile-title v-html="data.item.username"></v-list-tile-title>
                        <v-list-tile-sub-title v-html="data.item.tagline"
                                               v-if="data.item.tagline"></v-list-tile-sub-title>
                    </v-list-tile-content>
                </template>
            </template>
        </v-autocomplete>

        <v-flex xs12>
            <v-combobox
                    :error="selectedContent.length === 0"
                    :items="contentOptions"
                    :no-filter="true"
                    box
                    chips
                    label="Allowed post content types"
                    multiple
                    v-model="selectedContent"
            >
                <template
                        slot="selection"
                        slot-scope="data"
                >
                    <v-chip
                            :disabled="data.disabled"
                            :key="JSON.stringify(data.item)"
                            :selected="data.selected"
                            @input="data.parent.selectItem(data.item)"
                            class="v-chip--select-multi"
                            close
                    >
                        <v-avatar
                                class="grey white--text"
                                v-text="POST_TYPES_TRANSLATE[data.item].slice(0, 1).toUpperCase()"
                        ></v-avatar>
                        {{ POST_TYPES_TRANSLATE[data.item] }}
                    </v-chip>
                </template>
            </v-combobox>
        </v-flex>

        <v-flex xs12>
            <v-combobox
                    :items="postTagsItems"
                    :no-filter="true"
                    box
                    chips
                    label="Available tags for posts"
                    multiple
                    v-model="group.tags"
            >
                <template
                        slot="selection"
                        slot-scope="data"
                >
                    <v-chip
                            :close="true"
                            :disabled="data.disabled"
                            :key="JSON.stringify(data.item)"
                            :selected="data.selected"
                            @input="data.parent.selectItem(data.item)"
                            class="v-chip--select-multi"
                    >
                        {{ data.item }}
                    </v-chip>
                </template>
            </v-combobox>
        </v-flex>


        <v-dialog max-width="500px" scrollable v-model="rulesDialog">
            <v-btn color="primary" dark slot="activator">Group rules</v-btn>
            <v-card>
                <v-card-title>Specify group rules</v-card-title>
                <v-divider></v-divider>
                <v-card-text style="height: 500px;">
                    <v-flex xs12>
                        <div
                                :key="$index" v-for="(rule, $index) in rules"
                        >
                            <v-subheader><strong>Rule {{$index + 1}}</strong></v-subheader>
                            <v-text-field
                                    label="Header"
                                    v-model="rule.title"
                            ></v-text-field>
                            <v-text-field
                                    label="Description"
                                    v-model="rule.description"
                            ></v-text-field>
                        </div>
                        <v-btn @click="rules.push({title: '', description: ''})">Add 1 more</v-btn>
                    </v-flex>
                </v-card-text>
                <v-divider></v-divider>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn @click.native="rulesDialog = false" color="blue darken-1" flat>Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <br
    </div> -->
</template>

<script>
    import {findIndex} from 'lodash'
    import FileInput from '../includes/FileInput.vue'
    import {POST_TYPES, POST_TYPES_TRANSLATE} from "../../services/PostService";

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
                // this.toggleLoading(false)
                this.group = res.data;
                this.isAdmin = true;
                // this.bannerFilename = this.group.bannerUrl;

                // this.isAdmin = this.$groupService.isAdmin(this.group.administrators, this.$userService.getUserId());
                // this.adminOptions = this.group.administrators.slice();
                // this.adminOptions[0].disabled = true;
                // this.selectedAdmins = this.group.administrators.slice();
                // this.selectedContent = this.group.postTypes.slice();

                // this.modOptions = this.group.moderators.slice();
                // this.selectedMods = this.group.moderators.slice();

                // this.confirmDeletion = this.group.name;
                // if (!this.isAdmin) {
                //     // TODO redirect somewhere
                // }
            })
        },
        data() {
            return {
                bannerFilename: '',
                triedToSubmit: false,
                bannerErrors: [],
                group: {},
                isAdmin: false,
                selectedAdmins: [],
                selectedMods: [],
                loading: false,
                search: null,
                isUpdating: false,
                adminOptions: [],
                modOptions: [],
                confirmDeletion: '',
                deleteDialog: false,
                searchAdmin: '',
                searchMod: '',
                selectedContent: [],
                contentOptions: [POST_TYPES.POST, POST_TYPES.MEDIA, POST_TYPES.LINK],
                // tags
                colors: ['green', 'purple', 'indigo', 'cyan', 'teal', 'orange'],
                POST_TYPES_TRANSLATE,
                postTagsItems: [],
                value: '',
                rules: [{title: '', description: ''}],
                rulesDialog: false
            }
        },
        computed: {},
        watch: {
            searchAdmin(val) {
                this.adminOptions = this.adminOptions.filter(user => {
                    return findIndex(this.selectedAdmins, {id: user.id}) > -1
                });
                if (findIndex(this.selectedAdmins, {id: this.selectedAdmins[0].id}) < 0) {
                    this.adminOptions.shift() // remove first element if 1st element is not selected admin
                }
                if (val && val.length > 2) {
                    this.$userService.findExact(val.trim(), ({data}) => {
                        if (findIndex(this.selectedAdmins, {id: data.id}) < 0 && this.adminOptions[0].id !== data.id) {
                            this.adminOptions.unshift(data)
                        }
                    }, (error) => {
                        console.log(error)
                    })
                }
            },
            searchMod(val) {
                this.modOptions = this.modOptions.filter(user => {
                    return findIndex(this.selectedMods, {id: user.id}) > -1
                });
                if (this.selectedMods.length > 0 && findIndex(this.selectedMods, {id: this.selectedMods[0].id}) < 0) {
                    this.selectedMods.shift() // remove first element if 1st element is not selected admin
                }
                if (val && val.length > 2) {
                    this.$userService.findExact(val.trim(), ({data}) => {
                        if (findIndex(this.selectedMods, {id: data.id}) < 0 && this.modOptions[0].id !== data.id) {
                            this.modOptions.unshift(data)
                        }
                    }, (error) => {
                        console.log(error)
                    })
                }
            },
            model(val, prev) {
                if (val.length === prev.length) return;

                this.model = val.map(v => {
                    if (typeof v === 'string') {
                        v = {
                            text: v,
                            color: this.colors[this.nonce - 1]
                        };

                        this.items.push(v);

                        this.nonce++
                    }

                    return v
                })
            }
        },
        methods: {
            removeAdmin(item) {
                const index = findIndex(this.selectedAdmins, {id: item.id});
                console.log('remove admin', index);
                if (this.group.creator.id === item.id) {
                    console.log('cant remove admin');
                    return
                }
                if (index >= 0) {
                    this.selectedAdmins.splice(index, 1)
                }
            },
            removeMod(item) {
                const index = this.selectedMods.indexOf(item);
                if (index >= 0) {
                    this.selectedMods.splice(index, 1)
                }
            },
            findIndex: findIndex,
            isGroupCreator() {

            },
            edit(index, item) {
                if (!this.editing) {
                    this.editing = item;
                    this.index = index
                } else {
                    this.editing = null;
                    this.index = -1
                }
            },
            filter(item, queryText, itemText) {
                if (item.header) return false;

                const hasValue = val => val != null ? val : '';

                const text = hasValue(itemText);
                const query = hasValue(queryText);

                return text.toString()
                    .toLowerCase()
                    .indexOf(query.toString().toLowerCase()) > -1
            },
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
                    tags: this.group.tags
                }, ({data}) => {
                    this.$message({
                        message: 'Updated group',
                        color: this.$toastColors.SUCCESS
                    })
                }, () => {
                    // this.$message({
                    //     message: 'Error',
                    //     color: this.$toastColors.ERROR
                    // })
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
                    this.$toast.open({
                        message: 'Removed group',
                        color: 'is-success'
                    })
                })
            },
            handleFormData($event) {
                this.uploadBanner($event);
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