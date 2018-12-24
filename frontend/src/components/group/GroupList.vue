<template>
    <div>
        <pagination v-if="pagination" :pagination="pagination" @change="handleChange"/>
        <section class="section" v-for="(group, index) in groups">
            <div class="container">
                <h1 class="title"><router-link :to="{name: 'groupDetailView', params: {name: group.name}}">{{group.name}}</router-link></h1>
                <h2 class="subtitle">
                    {{group.description}}
                </h2>
            </div>
        </section>
        <b-notification v-if="groups.length === 0" :closable="false">
            {{'groups.empty' | t}}
        </b-notification>
    </div>
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