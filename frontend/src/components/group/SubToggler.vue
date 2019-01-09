<template>
    <a @click="toggleSub" :class="css">
        <span v-if="group.isSubbed">{{'groups.sub' | t}}</span>
        <span v-if="!group.isSubbed">{{'groups.unsub' | t}}</span>
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
              return [this.group.isSubbed ? 'is-warning' : 'is-info', this.klass];
          }  
        },
        methods: {
            toggleSub() {
                if (this.group.isSubbed) {
                    this.$dialog.confirm({
                        title: this.$t('groups.unsub-title'),
                        message: this.$t('groups.unsub-message'),
                        confirmText: this.$t('confirm'),
                        cancelText: this.$t('cancel'),
                        type: 'is-default',
                        hasIcon: true,
                        onConfirm: () => {
                            this.unsubGroup();
                        }
                    });
                } else {
                    this.subGroup();
                }
            },
            subGroup() {
                this.$groupService.subscribe(this.group, () => {
                    this.group.isSubbed = true;
                    this.$success('groups.unsub-toast');
                });
            },
            unsubGroup(group) {
                this.$groupService.unsubscribe(this.group, () => {
                    this.group.isSubbed = false;
                    this.$success('groups.sub-toast');
                });
            }
        }
    }
</script>

<style scoped>

</style>