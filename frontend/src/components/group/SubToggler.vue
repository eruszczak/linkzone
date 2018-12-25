<template>
    <button @click="toggleSub(group)" class="button is-small" :class="{'is-warning': group.subbed}">
        <span v-if="group.subbed">{{'groups.sub' | t}}</span>
        <span v-if="!group.subbed">{{'groups.unsub' | t}}</span>
    </button>
</template>

<script>
    export default {
        name: 'SubToggler',
        props: {
            group: {
                type: Object,
                required: true
            }
        },
        methods: {
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