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
       <span class="ml-4">dasads</span>
      </v-card-text>
      <v-card-actions>
        <v-btn flat color="orange">Share</v-btn>
        <v-btn flat color="orange">Explore</v-btn>
        <v-btn v-if="item.reply" flat color="orange" @click="item.showReplies = !item.showReplies">Reply / Show replies ({{item.replies.length}})</v-btn>
      </v-card-actions>
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
        <v-flex xs12 v-for="(item, outerIndex) in item.replies" :key="item.id">
          <comment :item="item" :index="outerIndex"></comment>
        </v-flex>
      </div>
    </v-card>
</template>

<script>
    import {mapMutations, mapGetters} from 'vuex'
    import FormPostText from '../includes/post/FormPostText'
    import FormPostLink from '../includes/post/FormPostLink'
    import FormPostMedia from '../includes/post/FormPostMedia'
    import VueMarkdown from 'vue-markdown'
    import {POST_TYPES} from "../../services/PostService";
    import _ from 'lodash';
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
          }
        },
        mixins: [validation],
        data () {
            return {

            }
        },
        mounted () {

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
            }
        }
    }
</script>

<style scoped>



</style>
