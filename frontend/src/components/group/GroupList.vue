<template>
    <div>
        <pagination v-if="pagination" :pagination="pagination" @change="handleChange"/>
        <section class="section" v-for="group in groups" :key="group.id">
            {{group}}
            <div class="box">
                <div>
                    <span class="title is-4">
                        <router-link :to="{name: 'groupDetailView', params: {name: group.name}}">{{group.name}}</router-link>
                        <button @click="toggleSub(group)" class="button is-small is-pulled-right" :class="{'is-warning': group.subbed}">
                            <span v-if="group.subbed">{{'groups.sub' | t}}</span>
                            <span v-if="!group.subbed">{{'groups.unsub' | t}}</span>
                        </button>
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
            toggleSub(group) {
                if (group.subbed) {
                    this.$dialog.confirm({
                        title: this.$t('groups.unsub-title'),
                        message: this.$t('groups.unsub-message'),
                        confirmText: this.$t('confirm'),
                        cancelText: this.$t('cancel'),
                        type: 'is-default',
                        hasIcon: true,
                        onConfirm: () => {
                            this.unsubGroup(group);
                        }
                    })
                } else {
                    this.subGroup(group);
                }
            },
            subGroup(group) {
                this.$groupService.subscribe(group, res => {
                    group.subbed = true;
                    this.$toast.open({
                        message: this.$t('groups.unsub-toast'),
                        type: 'is-success'
                    })
                });
            },
            unsubGroup(group) {
                this.$groupService.unsubscribe(group, res => {
                    group.subbed = false;
                    this.$toast.open({
                        message: this.$t('groups.sub-toast'),
                        type: 'is-success'
                    })
                });
            }
        }
    }
</script>

<style scoped>

</style>