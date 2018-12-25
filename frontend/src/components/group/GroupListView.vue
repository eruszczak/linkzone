<template>
    <section class="section">
        <router-link class="button is-pulled-right" :to="{name: 'groupCreateView'}">{{'groups.add-group'|t}}</router-link>
        <b-field style="width:300px" class="is-pulled-left">
            <b-input v-model="query" placeholder="Szukaj"></b-input>
        </b-field>
        <group-list :groups="groups" :pagination="pagination" @pageChange="handleChange"></group-list>
    </section>
</template>

<script>
    import {getPaginationFromResponse} from "../../utils/utils"
    import GroupList from './GroupList'

    export default {
        name: 'GroupListView',
        components: {
            GroupList,
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
        computed: {},
        methods: {
            handleChange(pageNumber) {
                this.$router.push({
                    name: 'groupListView',
                    query: {
                        page: pageNumber
                    }
                });
                // this.getGroups({page: pageNumber});
            },
            getGroups() {
                console.log('getting page', this.$route.query.page)
                this.$groupService.getGroupList(this.$route.query.page, '', res => {
                    this.groups = res.data.content;
                    this.pagination = getPaginationFromResponse(res.data);
                })
            }
        }
    }
</script>

<style scoped>

</style>