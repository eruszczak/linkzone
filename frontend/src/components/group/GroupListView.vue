<template>
    <div>
        <h2 class="mb-4">
            <router-link :to="{name: 'groupCreateView'}">Add group</router-link>
        </h2>

        <group-list :groups="groups" :pagination="pagination" @pageChange="handleChange"></group-list>
    </div>
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
                pagination: {}
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