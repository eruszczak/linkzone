<template>
    <section class="section">
        <p class="title">{{'posts.create-header' | t}}</p>
        <b-field :label="$t('posts.pick-group')" :type="{'is-danger': triedToSubmit && errors.first('group')}" :message="triedToSubmit ? errors.first('group') : null">
            <b-autocomplete
                v-validate="'required'"
                name="group"
                v-model="name"
                :data="data"
                :placeholder="$t('posts.search-group')"
                field="name"
                :loading="isFetching"
                @keyup.native="getAsyncData"
                @select="option => selected = option">

                <template slot-scope="props">
                    <div class="media">
                        <!-- <div class="media-left"> -->
                            <!-- <img width="32" :src="`https://image.tmdb.org/t/p/w500/${props.option.poster_path}`"> -->
                        <!-- </div> -->
                        <div class="media-content">
                            {{ props.option.name }}; {{ props.option.createdAt | shortDate }}
                            <br>
                            <small>
                                {{props.option.description}}
                            </small>
                        </div>
                    </div>
                </template>
            </b-autocomplete>
        </b-field>
        <br>
        <post-creator @submit="createPost" @upload="filename = $event"></post-creator>
    </section>
</template>

<script>
    import {mapGetters, mapMutations} from 'vuex'
    import PostCreator from './PostCreator'
    import {POST_TYPES} from "../../services/PostService";
    import debounce from 'lodash/debounce'

    export default {
        name: 'PostCreateView',
        components: {PostCreator},
        props: {
            groupName: {
                type: String,
                required: false
            }
        },
        mounted() {
            if (this.groupName) {
                // this.selectedGroup = this.groupName
            }
        },
        data() {
            return {
                selectedGroup: null,
                loading: false,
                search: null,
                isUpdating: false,
                items: [],
                filename: null,
                triedToSubmit: true,

                data: [],
                name: '',
                selected: null,
                isFetching: false
            }
        },
        methods: {
            createPost(value) {
                this.triedToSubmit = true;
                this.$validator.validate().then(result => {
                    if (result) {
                        this._createPost(value);
                    }
                });
            },
            _createPost(value) {
                console.log('craete post', value, this.selectedGroup, this.filename, value.selectedForm === POST_TYPES.MEDIA);
                if (value.selectedForm === POST_TYPES.MEDIA) {
                    if (!this.filename) {
                        return;
                    }
                    value.form.content = this.filename;
                }
                this.$postService.addPost(value.form, this.selectedGroup, value.selectedForm, ({data}) => {
                    console.log(data.id);
                    this.$router.push({
                        name: 'postView',
                        params: {
                            name: this.selectedGroup,
                            postID: data.id
                        }
                    })
                })
            },
            getAsyncData: debounce(function () {
                if (!this.name.length) {
                    this.data = [];
                    return
                }
                this.isFetching = true;

                console.log(this.name)

                this.$groupService.getGroupList(null, this.name, res => {
                    this.isFetching = false;
                    if (res.data.content.length === 0) {
                        this.data = [];
                        return
                    }
                    this.data = res.data.content;

                }, () => {
                    this.isFetching = false;
                    // this.loading = false
                })
            }, 500)
        }
    }
</script>

<style scoped>

</style>
