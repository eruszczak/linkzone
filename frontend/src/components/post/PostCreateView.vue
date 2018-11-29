<template>
    <div>
        <v-autocomplete
                v-model="selectedGroup"
                :search-input.sync="search"
                placeholder="Start typing to Search"
                prepend-icon="mdi-database-search"
                :error="error"
                :disabled="isUpdating"
                :items="items"
                :deletable-chips="true"
                box
                :no-filter="true"
                chips
                color="blue-grey lighten-2"
                label="Select"
                item-text="name"
                item-value="name"
        >
            <template
                    slot="selection"
                    slot-scope="data"
            >
                <v-chip
                        :selected="data.selected"
                        close
                        class="chip--select-multi"
                        @input="remove(data.item)"
                >
                    <v-avatar>
                        <img :src="data.item.avatar">
                    </v-avatar>
                    {{ data.item.name }}
                </v-chip>
            </template>
            <template
                    slot="item"
                    slot-scope="data"
            >
                <template v-if="typeof data.item !== 'object'">
                    <v-list-tile-content v-text="data.item"></v-list-tile-content>
                </template>
                <template v-else>
                    <v-list-tile-avatar>
                        <img :src="data.item.avatar">
                    </v-list-tile-avatar>
                    <v-list-tile-content>
                        <v-list-tile-title v-html="data.item.name"></v-list-tile-title>
                        <v-list-tile-sub-title v-html="data.item.group"></v-list-tile-sub-title>
                    </v-list-tile-content>
                </template>
            </template>
        </v-autocomplete>

        <post-creator @submit="createPost" @upload="filename = $event"></post-creator>
    </div>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
    import PostCreator from './PostCreator'

    export default {
        name: 'PostCreateView',
        components: {PostCreator},
        props: ['groupName'],
        created () {
            this.initSubbedGroups(true)
            console.log('post create view, created()')
        },
        data () {
            return {
                selectedGroup: null,
                loading: false,
                search: null,
                isUpdating: false,
                items: [],
                filename: null
            }
        },
        watch: {
            selectedGroup (val) {
              if (val) {
                  this.$router.push({name: 'postCreateView', params: {groupName: val}})
              }
            },
            isUpdating (val) {
                if (val) {
                    setTimeout(() => (this.isUpdating = false), 3000)
                }
            },
            filteredGroups2 (val) {
                console.log('watching filterGroups2', val)
                // if (!val) {
                //     return
                // }
                this.initSubbedGroups()
            },
            search (val) {
                if (val && val.length > 2) {
                    this.selectedGroup = null
                    this.getGroupOptions(val)
                    this.filterGroups2(val)
                }
            }
        },
        computed: {
            error() {
                return !this.selectedGroup
            },
            ...mapGetters(['filteredGroups2', 'groups'])
        },
        methods: {
            ...mapMutations(['filterGroups2']),
            initSubbedGroups(created) {
                if (this.items.length === 0) {
                    this.items.push({ header: 'Subscribed' })
                }

                let index = this.items.findIndex((item => item.header === 'Other'))
                if (created) {
                    this.items.splice(1, index - 1, ...this.groups)
                } else {
                    this.items.splice(1, index - 1, ...this.filteredGroups2)
                }

                if (this.groupName) {
                    this.selectedGroup = this.groupName
                }

                this.items.map(i => i.avatar = 'https://cdn.vuetifyjs.com/images/lists/1.jpg')
                this.items.map(i => i.group = i.description)
            },
            getGroupOptions(query) {
                query = query ? query : ''
                // this.loading = true
                this.$groupService.getGroupList({}, query, res => {
                    if (res.data.content.length === 0) {
                        return
                    }
                    res.data.content = res.data.content.filter(i => this.groups.findIndex(g => g.id === i.id) === -1)
                    let index = this.items.findIndex((item => item.header === 'Other'))
                    if (index < 0) {
                        this.items.push({ header: 'Other' })
                        index = this.items.length - 1;
                    }
                    console.log('before', this.items)
                    this.items.splice(index + 1, Infinity, ...res.data.content)
                    console.log('after', this.items)
                    this.items.push(...res.data.content)


                    this.items.map(i => i.avatar = 'https://cdn.vuetifyjs.com/images/lists/1.jpg')
                    this.items.map(i => i.group = i.description)

                    if (!query && this.groupOptions.length > 1) {
                    }
                    // this.loading = false
                }, () => {
                    // this.loading = false
                })
            },
            createPost(value) {
                console.log('craete post', value, this.selectedGroup, this.filename)
                if (!this.selectedGroup || !this.filename) {
                    return;
                }
                this.$postService.addPost(value.form, this.selectedGroup, value.selectedForm, ({data}) => {
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: this.selectedGroup,
                            postID: data.id
                        }
                    })
                })
            }
        }
    }
</script>

<style scoped>

</style>
