<template>
    <article class="media">
        <figure class="media-left">
            <p class="image is-64x64">
                <img src="https://bulma.io/images/placeholders/128x128.png">
            </p>
        </figure>
        <div class="media-content">
            <div class="field">
                <p class="control">
                    <!--<b-field :type="{'is-danger': triedToSubmit && errors.has('email')}" :message="triedToSubmit ? errors.first('email') : null">-->
                        <!--<b-input v-validate="{ required: true, email: true, min: 3, max: 50 }" name="email" icon="email" v-model="form.email" :placeholder="$t('registerView.email')"></b-input>-->
                    <!--</b-field>-->
                    <b-field>
                        <b-input v-validate="'required'" v-model="" maxlength="200" type="textarea"></b-input>
                    </b-field>
                </p>
            </div>
            <div class="field">
                <p class="control">
                    <button class="button">Post comment</button>
                </p>
            </div>
        </div>
    </article>
</template>

<script>
    export default {
        name: "NewComment",
        props: {
            value: {
                type: String,
                required: true
            }
        },
        // methods: {
        //     addComment() {
        //             this.$commentService.create(this.postID, {content: this.comment.body}, ({data}) => {
        //                 // if sorting desc then push()
        //                 this.comments.unshift(this.prepareComment(data));
        //                 this.comment.body = '';
        //                 this.$refs.commentForm.reset()
        //             })
        //     },
        // }
    }
</script>

<style scoped>

</style>