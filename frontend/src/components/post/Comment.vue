<template>
    <v-card :color='index % 2 === 0 ? "grey lighten-4" : "grey lighten-3"' light>
        <v-card-title>
            <router-link :to="{name: 'groupDetailView', params: {name: item.groupName}}">{{ item.groupName }}</router-link>; {{item.createdAt | shortDate}}
        </v-card-title>
        <v-card-text>
            <v-avatar
                    :size="40"
                    :tile="false"
                    color="grey lighten-4"
            >
                <img :src="!item.author.avatar ? $userService.getDefaultAvatar(item.author.username) : `/static/${item.author.avatar}`" alt="avatar">
            </v-avatar>
            <span class="ml-4">{{item.content}}</span>
        </v-card-text>
        <v-card-actions>
            <v-btn @click="updating = !updating" color="orange" flat v-if="!readOnly">Update</v-btn>
            <v-btn @click="deleteComment()" color="orange" flat>Delete</v-btn>
            <v-btn @click="item.showReplies = !item.showReplies" color="orange" flat v-if="item.reply && canReply">Reply
                / Show replies ({{item.replies.length}})
            </v-btn>
        </v-card-actions>
        <div v-if="updating">
            <v-btn :disabled="item.body === updatedBody || !updatedBody" @click="updateComment()" color="red" flat>
                Update
            </v-btn>
            <v-text-field
                    :rules="[ruleIsNotEmpty]"
                    box
                    label="Update"
                    name="input-7-4"
                    v-model="updatedBody"
            ></v-text-field>
        </div>
        <div class="ml-5" v-if="item.reply && item.showReplies">
            <v-form lazy-validation ref="commentForm" v-model="item.reply.valid">
                <v-text-field
                        :rules="[ruleIsNotEmpty]"
                        box
                        label="Reply"
                        name="input-7-4"
                        v-model="item.reply.body"
                ></v-text-field>
                <v-btn :disabled="!item.reply.valid" @click="replyToComment(item)">Add reply</v-btn>
            </v-form>
        </div>
        <div class="ml-5" v-if="item.showReplies">
            <v-alert
                    :value="item.replies.length === 0"
                    type="info"
            >
                <p>Be first to comment</p>
            </v-alert>
            <v-flex :key="item.id" v-for="(item, index) in item.replies" xs12>
                <comment :index="index" :item="item" @removed="emitRemoveEvent(index)"></comment>
            </v-flex>
        </div>
    </v-card>
</template>

<script>
    import validation from '../../mixins/validation';

    export default {
        name: 'Comment',
        props: {
            item: {
                required: true,
                type: Object
            },
            index: {
                required: false,
                type: Number
            },
            readOnly: {
                type: Boolean,
                default: false
            },
            canReply: {
                type: Boolean,
                default: true
            },
        },
        mixins: [validation],
        data() {
            return {
                updating: false,
                updatedBody: this.item.content
            }
        },
        mounted() {
            console.warn('body', this.item)
        },
        computed: {},
        methods: {
            replyToComment(comment) {
                if (this.$refs['commentForm'].validate()) {
                    this.$commentService.reply(comment.id, {content: comment.reply.body}, ({data}) => {
                        comment.replies.unshift(data);
                        comment.reply.body = '';
                        this.$refs['commentForm'].reset()
                    })
                }
            },
            deleteComment() {
                this.$commentService.delete(this.item.id, () => {
                    this.emitRemoveEvent(null);
                })
            },
            emitRemoveEvent(innerIndex) {
                console.log('emit remove event');
                this.$emit('removed', {outerIndex: this.index, innerIndex: innerIndex})
            },
            updateComment() {
                this.$commentService.update(this.item.id, {content: this.updatedBody}, ({data}) => {
                    this.item.content = data.content;
                    this.updating = false
                })
            }
        }
    }
</script>

<style scoped>


</style>
