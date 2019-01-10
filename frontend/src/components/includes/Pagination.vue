<template>
    <div class="text-xs-center mt-2" v-if="pagination && pagination.totalPages > 1">
        <b-pagination
            :total="pagination.total"
            :current.sync="pagination.currentPage"
            :order="order"
            :size="size"
            :simple="isSimple"
            :rounded="isRounded"
            :per-page="pagination.pageSize"
            @change="handleCurrentChange">
        </b-pagination>
    </div>
</template>

<script>

    export default {
        name: 'Pagination',
        props: {
            pagination: {
                required: true,
                type: Object
            }
        },
        data() {
            return {
                total: 200,
                current: 1,
                perPage: 20,
                order: '',
                size: '',
                isSimple: false,
                isRounded: false
            }
        },

        methods: {
            handleCurrentChange(val) {
                this.$emit('change', val - 1);
            }
        }
    }
</script>

<style scoped>

</style>