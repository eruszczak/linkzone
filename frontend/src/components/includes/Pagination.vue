<template>
    <div class="text-xs-center" v-if="pagination && pagination.totalPages > 1">
        <v-pagination
                :length="pagination.totalPages"
                :total-visible="7"
                @input="handleCurrentChange"
                circle
                v-model="pagination.currentPage + 1"
        ></v-pagination>
    </div>
</template>

<script>

    export default {
        name: 'Pagination',
        props: ['pagination'],
        mounted() {
        },
        data() {
            return {}
        },
        computed: {},
        methods: {
            handleCurrentChange(val) {
                this.$emit('change', val - 1);
            }
        }
    }
</script>

<style scoped>

</style>