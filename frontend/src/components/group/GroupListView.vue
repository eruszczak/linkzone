<template>
    <div>
        <h2 class="mb-4">
            <router-link :to="{name: 'groupCreateView'}">Add group</router-link>
        </h2>

        <pagination :pagination="pagination" @change="handleChange"/>

        <v-flex sm12 xs12>
            <v-card>
                <v-list two-line>
                    <template v-for="(group, index) in groups">
                        <v-list-tile
                                :key="group.name"
                                :to="{name: 'groupDetailView', params: {name: group.name}}"
                                avatar
                        >
                            <v-list-tile-avatar>
                                <img src="https://cdn.vuetifyjs.com/images/lists/1.jpg">
                            </v-list-tile-avatar>

                            <v-list-tile-content>
                                <v-list-tile-title v-html="group.name"></v-list-tile-title>
                                <v-list-tile-sub-title v-html="group.description"></v-list-tile-sub-title>
                            </v-list-tile-content>
                        </v-list-tile>
                    </template>
                </v-list>
            </v-card>
        </v-flex>
    </div>
</template>

<script>
    import {getPaginationFromResponse} from "../../utils/utils"
    import Pagination from '../includes/Pagination'

    export default {
        name: 'GroupListView',
        components: {
            Pagination
        },
        mounted() {
            this.getGroups();
        },
        data() {
            return {
                groups: [],
                pagination: {},
                items: [
                    {header: 'Today'},
                    {
                        avatar: 'https://cdn.vuetifyjs.com/images/lists/1.jpg',
                        title: 'Brunch this weekend?',
                        subtitle: "<span class='text--primary'>Ali Connors</span> &mdash; I'll be in your neighborhood doing errands this weekend. Do you want to hang out?"
                    },
                    {divider: true, inset: true},
                    {
                        avatar: 'https://cdn.vuetifyjs.com/images/lists/2.jpg',
                        title: 'Summer BBQ <span class="grey--text text--lighten-1">4</span>',
                        subtitle: "<span class='text--primary'>to Alex, Scott, Jennifer</span> &mdash; Wish I could come, but I'm out of town this weekend."
                    },
                    {divider: true, inset: true},
                    {
                        avatar: 'https://cdn.vuetifyjs.com/images/lists/3.jpg',
                        title: 'Oui oui',
                        subtitle: "<span class='text--primary'>Sandra Adams</span> &mdash; Do you have Paris recommendations? Have you ever been?"
                    }
                ]
            }
        },
        computed: {},
        methods: {
            handleChange(pageNumber) {
                this.getGroups({page: pageNumber});
            },
            getGroups(pageable = {}) {
                this.$groupService.getGroupList(pageable, null, res => {
                    this.groups = res.data.content;
                    this.pagination = getPaginationFromResponse(res.data)
                })
            }
        }
    }
</script>

<style scoped>

</style>