<template>
    <section class="section">
        <div class="container">
            <div class="column is-8 is-offset-2">
                <div class="is-clearfix">
                    <router-link class="button is-pulled-right" :to="{name: 'groupCreateView'}">{{'groups.add-group'|t}}</router-link>
                    <b-field style="width:300px" class="is-pulled-left">
                        <b-input v-model="query" @input="search" :placeholder="$t('search')"></b-input>
                    </b-field>
                </div>
                <section class="">
                    {{pagination}}
                    <group-list :groups="groups" :pagination="pagination" @pageChange="handleChange"></group-list>
                    <!-- <pagination :pagination="pagination" @change="handleChange"/> -->
                </section>
            </div>
        </div>
    </section>
</template>

<script>
    import {getPaginationFromResponse} from "../../utils/utils"
    import Pagination from '../includes/Pagination'
    import GroupList from './GroupList'
    import debounce from 'lodash/debounce'

    export default {
        name: 'GroupListView',
        components: {
            GroupList, Pagination
        },
        mounted() {
            this.getGroups();
        },
        data() {
            return {
                groups: [],
                pagination: {},
                query: ''
            }
        },
        watch: {
            '$route' (to, from) {
                this.getGroups();
            }
        },
        methods: {
            search: debounce(function (text) {
                this.$router.push({
                    name: 'groupListView',
                    query: {
                        page: 0,
                        query: this.query
                    }
                });
            }, 500),
            handleChange(pageNumber) {
                this.$router.push({
                    name: 'groupListView',
                    query: {
                        page: pageNumber
                    }
                });
            },
            getGroups() {
                this.pagination.page = this.$route.query.page || 0;
                this.query = this.$route.query.query || '';
                this.$toggleLoading(true);
                this.$groupService.getGroupList(this.pagination, this.query, ({data}) => {
                    this.groups = data.content;
                    this.pagination = getPaginationFromResponse(data);
                    this.$toggleLoading(false);
                })
            }
        }
    }
</script>

<style scoped>

</style>