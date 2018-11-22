<template>
    <div v-if="group && isAdmin">

        <h1>Update /g/{{group.name}}</h1>
        {{breadcrumbs}}
        <!--<v-breadcrumbs :items="breadcrumbs">-->
        <!--</v-breadcrumbs>-->
        <!--<v-breadcrumbs :items="items" divider="-"></v-breadcrumbs>-->
        <v-breadcrumbs>
            <v-icon slot="divider">chevron_right</v-icon>
            <v-breadcrumbs-item
                    v-for="item in items"
                    :href="item.href"
                    :key="item.text"
                    :disabled="item.disabled"
            >
                {{item.text}}
           </v-breadcrumbs-item>
        </v-breadcrumbs>
        <v-divider class="my-3"></v-divider>

        <p>{{bannerErrors}}</p>
        <file-input v-model="bannerFilename" @formData="handleFormData" :is-image="true" :current-image-url="group.bannerUrl ? '/static/' + group.bannerUrl : ''"></file-input>
        <v-btn @click.native="uploadLogo" :disabled="bannerFormData.length === 0">Upload banner</v-btn>

        <v-divider class="my-3"></v-divider>
        <v-text-field
                v-model="group.description"
                label="Group description"
                box
        ></v-text-field>

        <v-autocomplete
                v-model="selectedAdmins"
                :search-input.sync="searchAdmin"
                placeholder="Start typing to Search"
                :error="selectedAdmins.length === 0"
                :disabled="isUpdating"
                :items="adminOptions"
                :deletable-chips="true"
                box
                :no-filter="true"
                :cache-items="false"
                chips
                color="blue-grey lighten-2"
                label="Select administrators"
                multiple
        >
            <template
                    slot="selection"
                    slot-scope="data"
            >
                <v-chip
                        :selected="data.selected"
                        :close="data.item.id !== group.creator.id"
                        :color="data.item.id === group.creator.id ? 'grey' : ''"
                        :text-color="data.item.id === group.creator.id ? 'white' : ''"
                        class="chip--select-multi"
                        @input="removeAdmin(data.item)"
                >
                    <!--<v-avatar>-->
                        <!--<img :src="data.item.avatar">-->
                    <!--</v-avatar>-->
                    <span v-if="data.item.id === group.creator.id"><strong>{{data.item.username}}</strong> (creator)</span>
                    <span v-else>{{data.item.username}}</span>
                </v-chip>
            </template>
            <template
                    slot="item"
                    slot-scope="data"
                    @click="removeAdmin(data.item)"
            >
                <template>
                    <!--<v-list-tile-avatar>-->
                        <!--<img :src="data.item.avatar">-->
                    <!--</v-list-tile-avatar>-->
                    <v-list-tile-content>
                        <v-list-tile-title v-html="data.item.username"></v-list-tile-title>
                        <v-list-tile-sub-title v-if="data.item.tagline" v-html="data.item.tagline"></v-list-tile-sub-title>
                    </v-list-tile-content>
                </template>
            </template>
        </v-autocomplete>

        <v-autocomplete
                v-model="selectedMods"
                :search-input.sync="searchMod"
                placeholder="Start typing to Search"
                :error="false"
                :disabled="isUpdating"
                :items="modOptions"
                :deletable-chips="true"
                box
                :no-filter="true"
                :cache-items="false"
                chips
                color="blue-grey lighten-2"
                label="Select moderators"
                multiple
        >
            <template
                    slot="selection"
                    slot-scope="data"
            >
                <v-chip
                        :close="true"
                        class="chip--select-multi"
                        @input="removeMod(data.item)"
                >
                    <!--<v-avatar>-->
                    <!--<img :src="data.item.avatar">-->
                    <!--</v-avatar>-->
                    <span v-if="data.item.id === group.creator.id"><strong>{{data.item.username}}</strong> (creator)</span>
                    <span v-else>{{data.item.username}}</span>
                </v-chip>
            </template>
            <template
                    slot="item"
                    slot-scope="data"
                    @click="removeMod(data.item)"
            >
                <template>
                    <!--<v-list-tile-avatar>-->
                    <!--<img :src="data.item.avatar">-->
                    <!--</v-list-tile-avatar>-->
                    <v-list-tile-content>
                        <v-list-tile-title v-html="data.item.username"></v-list-tile-title>
                        <v-list-tile-sub-title v-if="data.item.tagline" v-html="data.item.tagline"></v-list-tile-sub-title>
                    </v-list-tile-content>
                </template>
            </template>
        </v-autocomplete>

        <v-flex xs12>
            <v-combobox
                    v-model="selectedContent"
                    :items="contentOptions"
                    label="Allowed post content types"
                    :no-filter="true"
                    multiple
                    :error="selectedContent.length === 0"
                    chips
                    box
            >
                <template
                        slot="selection"
                        slot-scope="data"
                >
                    <v-chip
                            :selected="data.selected"
                            :disabled="data.disabled"
                            :key="JSON.stringify(data.item)"
                            class="v-chip--select-multi"
                            close
                            @input="data.parent.selectItem(data.item)"
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
                    v-model="group.tags"
                    :items="postTagsItems"
                    label="Available tags for posts"
                    :no-filter="true"
                    multiple
                    chips
                    box
            >
                <template
                        slot="selection"
                        slot-scope="data"
                >
                    <v-chip
                            :selected="data.selected"
                            :disabled="data.disabled"
                            :key="JSON.stringify(data.item)"
                            :close="true"
                            class="v-chip--select-multi"
                            @input="data.parent.selectItem(data.item)"
                    >
                        {{ data.item }}
                    </v-chip>
                </template>
            </v-combobox>
        </v-flex>


        <v-dialog v-model="rulesDialog" scrollable max-width="500px">
            <v-btn slot="activator" color="primary" dark>Group rules</v-btn>
            <v-card>
                <v-card-title>Specify group rules</v-card-title>
                <v-divider></v-divider>
                <v-card-text style="height: 500px;">
                    <v-flex xs12>
                        <div
                                v-for="(rule, $index) in rules" :key="$index"
                        >
                            <v-subheader><strong>Rule {{$index + 1}}</strong></v-subheader>
                            <v-text-field
                                    v-model="rule.title"
                                    label="Header"
                            ></v-text-field>
                            <v-text-field
                                    v-model="rule.description"
                                    label="Description"
                            ></v-text-field>
                        </div>
                        <v-btn @click="rules.push({title: '', description: ''})">Add 1 more</v-btn>
                    </v-flex>
                </v-card-text>
                <v-divider></v-divider>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="blue darken-1" flat @click.native="rulesDialog = false">Save</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <br>

        <v-btn
                color="error"
                dark
                @click="deleteDialog = true"
        >
            Delete group
        </v-btn>

        <v-dialog
                v-model="deleteDialog"
                max-width="290"
        >
            <v-card>
                <v-card-title class="headline">Remove /g/{{group.name}}?</v-card-title>

                <v-card-text>
                    You cannot restore deleted group. All posts and comments from that group will be removed too.
                    <v-text-field
                            v-model="confirmDeletion"
                            label="Enter group name to confirm"
                    ></v-text-field>
                </v-card-text>

                <v-card-actions>
                    <v-spacer></v-spacer>

                    <v-btn
                            color="green darken-1"
                            flat="flat"
                            @click="deleteDialog = false"
                    >
                        Cancel
                    </v-btn>

                    <v-btn
                            color="error darken-1"
                            flat="flat"
                            :disabled="confirmDeletion !== group.name"
                            @click="removeGroup()"
                    >
                        Remove
                    </v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-btn
                color="success"
                dark
                @click="update()"
        >
            Update
        </v-btn>
    </div>
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
            this.init()
        },
        data() {
            return {
                // logo upload
                bannerFormData: [],
                bannerFilename: '',
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
                rulesDialog: false,
                breadcrumbs: [
                    {
                        text: 'groupname'   ,
                        disabled: false,
                        href: 'breadcrumbs_dashboard'
                    },
                    {
                        text: 'Update',
                        disabled: true,
                        href: 'breadcrumbs_link_2'
                    }
                ],
                items: [
                    {
                        text: 'Dashboard',
                        disabled: false,
                        href: 'breadcrumbs_dashboard'
                    },
                    {
                        text: 'Link 1',
                        disabled: false,
                        href: 'breadcrumbs_link_1'
                    },
                    {
                        text: 'Link 2',
                        disabled: true,
                        href: 'breadcrumbs_link_2'
                    }
                ]
            }
        },
        computed: {

        },
        watch: {
            searchAdmin (val) {
                this.adminOptions = this.adminOptions.filter(user => {
                    return findIndex(this.selectedAdmins, {id: user.id}) > -1
                })
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
            searchMod (val) {
                this.modOptions = this.modOptions.filter(user => {
                    return findIndex(this.selectedMods, {id: user.id}) > -1
                })
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
            model (val, prev) {
                if (val.length === prev.length) return

                this.model = val.map(v => {
                    if (typeof v === 'string') {
                        v = {
                            text: v,
                            color: this.colors[this.nonce - 1]
                        }

                        this.items.push(v)

                        this.nonce++
                    }

                    return v
                })
            }
        },
        methods: {
            init () {
                // this.toggleLoading(true)
                this.$groupService.getGroupDetail(this.name, res => {
                    // this.toggleLoading(false)
                    this.group = res.data
                    this.bannerFilename = this.group.bannerUrl
                    
                    this.isAdmin = this.$groupService.isAdmin(this.group.administrators, this.$userService.getUserId())
                    this.adminOptions = this.group.administrators.slice()
                    this.adminOptions[0].disabled = true
                    this.selectedAdmins = this.group.administrators.slice()
                    this.selectedContent = this.group.postTypes.slice()

                    this.modOptions= this.group.moderators.slice()
                    this.selectedMods = this.group.moderators.slice()

                    this.confirmDeletion = this.group.name
                    if (!this.isAdmin) {
                        // TODO redirect somewhere
                    }
                })
            },
            removeAdmin (item) {
                const index = findIndex(this.selectedAdmins, {id: item.id})
                console.log('remove admin', index)
                if (this.group.creator.id === item.id) {
                    console.log('cant remove admin')
                    return
                }
                if (index >= 0) {
                    this.selectedAdmins.splice(index, 1)
                }
            },
            removeMod (item) {
                const index = this.selectedMods.indexOf(item)
                if (index >= 0) {
                    this.selectedMods.splice(index, 1)
                }
            },
            findIndex: findIndex,
            isGroupCreator () {

            },
            edit (index, item) {
                if (!this.editing) {
                    this.editing = item
                    this.index = index
                } else {
                    this.editing = null
                    this.index = -1
                }
            },
            filter (item, queryText, itemText) {
                if (item.header) return false

                const hasValue = val => val != null ? val : ''

                const text = hasValue(itemText)
                const query = hasValue(queryText)

                return text.toString()
                    .toLowerCase()
                    .indexOf(query.toString().toLowerCase()) > -1
            },
            update() {
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
            getRandomColor() {
                return 'green'
                // return this.colors[Math.floor(Math.random() * this.colors.length)]
            },
            removeGroup() {
                this.$groupService.delete(this.group.name, () => {
                    this.deleteDialog = false
                    this.$router.push({ path: '/' })
                    this.$message({
                        message: 'Removed group',
                        color: this.$toastColors.SUCCESS
                    })
                })
            },
            handleFormData(val) {
                this.bannerFormData = val.value
                this.bannerErrors = val.errors
            },
            uploadLogo() {
                console.log(this.bannerFormData)
                this.$groupService.uploadBanner(this.group.name, this.bannerFormData[0], ({data}) => {
                    this.group.bannerUrl = data.fileName
                    this.bannerFormData = []
                })
            }
        }
    }
</script>

<style scoped>

</style>