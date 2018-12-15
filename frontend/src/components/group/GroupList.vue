<template>
    <v-flex sm12 xs12>
        <pagination v-if="pagination" :pagination="pagination" @change="handleChange"/>
        <v-card v-if="groups">
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
        <v-alert :value="groups.length === 0"
                 type="info"
        >
            No groups
        </v-alert>
    </v-flex>
</template>

<script>
    import Pagination from '../includes/Pagination'

    export default {
        name: "GroupList",
        components: {Pagination},
        props: {
            groups: {
                type: Array,
                required: true
            },
            pagination: {
                type: Object,
                required: false
            }
        },
        methods: {
            handleChange(pageNumber) {
                this.$emit('pageChange', pageNumber);
            },
        }
    }
</script>

<style scoped>

</style>