<template>
    <a @click="toggleSub(group)" :class="css">
        <span v-if="group.subbed">{{'groups.sub' | t}}</span>
        <span v-if="!group.subbed">{{'groups.unsub' | t}}</span>
    </a>
</template>

<script>
    export default {
        name: 'SubToggler',
        props: {
            group: {
                type: Object,
                required: true
            },
            klass: {
                type: String,
                default: 'button is-small'
            }
        },
        computed: {
          css() {
              return [this.group.subbed ? 'is-warning' : 'is-info', this.klass];
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
                    this.$success('groups.unsub-toast');
                });
            },
            unsubGroup(group) {
                this.$groupService.unsubscribe(group, res => {
                    group.subbed = false;
                    this.$success('groups.sub-toast');
                });
            }
        }
    }
</script>

<style scoped>

</style>