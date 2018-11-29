<template>
    <v-card light :color='index % 2 === 0 ? "grey lighten-4" : "grey lighten-3"'>
      <v-card-title>
        {{ item.author }}; {{item.createdAt | shortDate}}
      </v-card-title>
      <v-card-text>
        <v-avatar
         :tile="false"
         :size="40"
         color="grey lighten-4"
       >
         <img :src="`https://api.adorable.io/avatar/50/${item.author}`" alt="avatar">
       </v-avatar>
       <span class="ml-4">{{item.content}}</span>
      </v-card-text>
      <v-card-actions>
        <v-btn v-if="!readOnly" @click="updating = !updating" flat color="orange">Update</v-btn>
        <v-btn @click="deleteComment()" flat color="orange">Delete</v-btn>
        <v-btn v-if="item.reply" flat color="orange" @click="item.showReplies = !item.showReplies">Reply / Show replies ({{item.replies.length}})</v-btn>
      </v-card-actions>
      <div v-if="updating">
        <v-btn @click="updateComment()" :disabled="item.body === updatedBody || !updatedBody" flat color="red">Update</v-btn>
        <v-text-field
                box
                name="input-7-4"
                label="Update"
                :rules="[ruleIsNotEmpty]"
                v-model="updatedBody"
        ></v-text-field>
      </div>
      <div v-if="item.reply && item.showReplies" class="ml-5">
          <v-form v-model="item.reply.valid" lazy-validation ref="commentForm">
              <v-text-field
                      box
                      name="input-7-4"
                      label="Reply"
                      :rules="[ruleIsNotEmpty]"
                      v-model="item.reply.body"
              ></v-text-field>
              <v-btn @click="replyToComment(item)" :disabled="!item.reply.valid">Add reply</v-btn>
          </v-form>
      </div>
      <div v-if="item.showReplies" class="ml-5">
        <v-alert
                :value="item.replies.length === 0"
                type="info"
        >
            <p>Be first to comment</p>
        </v-alert>
        <v-flex xs12 v-for="(item, index) in item.replies" :key="item.id">
          <comment :item="item" :index="index" @removed="emitRemoveEvent(index)"></comment>
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
          }
        },
        mixins: [validation],
        data () {
            return {
              updating: false,
              updatedBody: this.item.content
            }
        },
        mounted () {
          console.warn('body', this.item)
        },
        computed: {

        },
        methods: {
            replyToComment(comment) {
                if (this.$refs['commentForm'].validate()) {
                    this.$commentService.reply(comment.id, {content: comment.reply.body}, ({data}) => {
                        comment.replies.unshift(data)
                        comment.reply.body = ''
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
              console.log('emit remove event')
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
