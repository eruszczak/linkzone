<template>
    <div>
        <pagination v-if="pagination" :pagination="pagination" @change="handleChange"/>
        <section class="section" v-for="group in groups" :key="group.id">
            {{group}}
            <div class="box">
                <div>
                    <span class="title is-4">
                        <router-link :to="{name: 'groupDetailView', params: {name: group.name}}">{{group.name}}</router-link>
                        <p class="is-pulled-right">
                            <sub-toggler :group="group"></sub-toggler>
                        </p>
                    </span>
                    <small class="ml-2">{{group.createdAt | shortDate}}</small>
                </div>
                <div class="mt-2">
                    <p class="subtitle is-6">{{group.description}}</p>
                </div>
            </div>
        </section>
        <b-notification v-if="groups.length === 0" :closable="false">
            {{'groups.empty' | t}}
        </b-notification>
    </div>
</template>

<script>
    import Pagination from '../includes/Pagination'
    import SubToggler from './SubToggler';

    export default {
        name: "GroupList",
        components: {Pagination, SubToggler},
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
        data() {
            return {
            }
        },
        methods: {
            handleChange(pageNumber) {
                this.$emit('pageChange', pageNumber);
            }
        }
    }
</script>

<style scoped>

</style>