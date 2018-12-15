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