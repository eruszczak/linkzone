<template>
    <div>
        <div class="mt-2 mb-2" v-for="group in groups" :key="group.id">
            <!-- {{group}} -->
            <div class="box">
                <div class="columns">
                    <div class="column">
                        <div class="media">
                            <figure class="media-left">
                                <p class="image is-48x48">
                                    <img class="is-rounded" :src="$groupService.getLogoUrl(group)">
                                </p>
                            </figure>
                            <div class="media-content">
                                <span class="title is-4">
                                    <h1 class="title">
                                        <router-link :to="{name: 'groupDetailView', params: {name: group.name}}">{{group.name}}</router-link>
                                    </h1>
                                </span>
                            </div>
                        </div>

                        <div class="mt-2">
                            <p class="subtitle is-6">{{group.description}}</p>
                        </div>
                    </div>
                    <div class="column has-text-centered is-narrow">
                        <p style="margin-bottom: 8px; width: 250px">
                            <router-link class="button is-small" style="margin-right: 5px" :to="{name: 'postCreateView', params: {groupName: group.name}}">{{'groups.add-post-in-group' | t}}</router-link>
                            <sub-toggler :group="group"></sub-toggler>
                        </p>
                        <b-taglist style="margin-bottom: 0" attached>
                            <b-tag><strong>{{'groups.subscribers'|t}}</strong></b-tag>
                            <b-tag>{{group.subscribers}}</b-tag>
                        </b-taglist>
                        <b-taglist attached>
                            <b-tag><strong>{{'groups.created-time'|t}}</strong></b-tag>
                            <b-tag>{{group.createdAt | since}}</b-tag>
                        </b-taglist>
                        <b-taglist attached v-if="group.groupStatus.length">
                            <b-tag v-for="status in group.groupStatus" type="is-warning" :key="status" style="margin-right:5px"><strong>{{status|t}}</strong></b-tag>
                        </b-taglist>
                    </div>
                </div>
            </div>
        </div>
        <pagination v-if="pagination" :pagination="pagination" @change="handleChange"/>
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