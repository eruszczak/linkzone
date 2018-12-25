<template>
    <section class="section">
        <p class="content"><b>Selected:</b> {{ selected }}</p>
        <b-field label="Find a movie">
            <b-autocomplete
                v-model="name"
                :data="data"
                placeholder="e.g. Fight Club"
                field="title"
                :loading="isFetching"
                @keyup.native="getAsyncData"
                @select="option => selected = option">

                <template slot-scope="props">
                    <div class="media">
                        <div class="media-left">
                            <img width="32" :src="`https://image.tmdb.org/t/p/w500/${props.option.poster_path}`">
                        </div>
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
    </section>
    <!-- <div>
        <post-creator @submit="createPost" @upload="filename = $event"></post-creator>
    </div> -->
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

                data: [],
                name: '',
                selected: null,
                isFetching: false
            }
        },
        methods: {
            createPost(value) {
                console.log('craete post', value, this.selectedGroup, this.filename, value.selectedForm === POST_TYPES.MEDIA);
                if (!this.selectedGroup) {
                    return;
                }

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
                        // You have to install and import debounce to use it,
            // it's not mandatory though.
            getAsyncData: debounce(function () {
                if (!this.name.length) {
                    this.data = [];
                    return
                }
                this.isFetching = true;

                console.log(this.name)

                this.$groupService.getGroupList({}, this.name, res => {
                    this.isFetching = false;
                    if (res.data.content.length === 0) {
                        return
                    }
                    this.data = res.data.content;
                    // res.data.content = res.data.content.filter(i => this.groups.findIndex(g => g.id === i.id) === -1);
                    // let index = this.items.findIndex((item => item.header === 'Other'));
                    // if (index < 0) {
                    //     this.items.push({header: 'Other'});
                    //     index = this.items.length - 1;
                    // }
                    // console.log('before', this.items);
                    // this.items.splice(index + 1, Infinity, ...res.data.content);
                    // console.log('after', this.items);
                    // this.items.push(...res.data.content);


                    // this.items.map(i => i.avatar = 'https://cdn.vuetifyjs.com/images/lists/1.jpg');
                    // this.items.map(i => i.group = i.description);

                    // if (!query && this.groupOptions.length > 1) {
                        
                    // }
                    // this.loading = false
                }, () => {
                    this.isFetching = false;
                    // this.loading = false
                })



                // this.$http.get(`https://api.themoviedb.org/3/search/movie?api_key=bb6f51bef07465653c3e553d6ab161a8&query=${this.name}`)
                //     .then(({ data }) => {
                //         this.data = []
                //         data.results.forEach((item) => this.data.push(item))
                //     })
                //     .catch((error) => {
                //         this.data = []
                //         throw error
                //     })
                //     .finally(() => {
                //         this.isFetching = false
                //     })
            }, 500)
        }
    }
</script>

<style scoped>

</style>
