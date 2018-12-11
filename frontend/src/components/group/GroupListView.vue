<template>
    <div>
        <h2>
            <router-link :to="{name: 'groupCreateView'}">Add group</router-link>
        </h2>
        <p :key="group.name" v-for="group in groups">
            <router-link :to="{name: 'groupDetailView', params: {name: group.name}}">
                {{group.name}}
            </router-link>
            {{group.description}}
        </p>
        <pagination :pagination="pagination" @change="handleChange"/>

        <v-flex sm12 xs12>
            <v-card>
                <v-list two-line>
                    <template v-for="(item, index) in items">
                        <v-subheader
                                :key="item.header"
                                v-if="item.header"
                        >
                            {{ item.header }}
                        </v-subheader>

                        <v-divider
                                :inset="item.inset"
                                :key="index"
                                v-else-if="item.divider"
                        ></v-divider>

                        <v-list-tile
                                :key="item.title"
                                @click=""
                                avatar
                                v-else
                        >
                            <v-list-tile-avatar>
                                <img :src="item.avatar">
                            </v-list-tile-avatar>

                            <v-list-tile-content>
                                <v-list-tile-title v-html="item.title"></v-list-tile-title>
                                <v-list-tile-sub-title v-html="item.subtitle"></v-list-tile-sub-title>
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