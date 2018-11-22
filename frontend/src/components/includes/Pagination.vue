<template>
    <div class="text-xs-center" v-if="pagination && pagination.totalPages > 1">
        <v-pagination
                v-model="pagination.currentPage + 1"
                :length="pagination.totalPages"
                :total-visible="7"
                circle
                @input="handleCurrentChange"
        ></v-pagination>
    </div>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'

    export default {
        name: 'Pagination',
        props: ['pagination'],
        mounted () {
        },
        data () {
            return {
            }
        },
        computed: {
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